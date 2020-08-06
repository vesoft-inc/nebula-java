/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.importer

import org.apache.spark.sql.{DataFrame, SparkSession}
import java.io.File
import java.util.concurrent.{Executors, TimeUnit}

import com.google.common.base.Optional
import com.google.common.net.HostAndPort
import com.google.common.util.concurrent.{FutureCallback, Futures, MoreExecutors, RateLimiter}
import com.vesoft.nebula.client.graph.async.AsyncGraphClientImpl
import com.vesoft.nebula.graph.ErrorCode
import com.vesoft.nebula.tools.importer.config.{
  CSVSourceConfigEntry,
  Configs,
  ConnectionConfigEntry,
  DataBaseConfigEntry,
  DataSinkConfigEntry,
  DataSourceConfigEntry,
  FileBaseSourceConfigEntry,
  HiveSourceConfigEntry,
  KafkaSourceConfigEntry,
  MySQLSourceConfigEntry,
  Neo4JSourceConfigEntry,
  PulsarSourceConfigEntry,
  SchemaConfigEntry,
  SinkCategory,
  SocketSourceConfigEntry,
  SourceCategory,
  UserConfigEntry
}
import com.vesoft.nebula.tools.importer.processor.{EdgeProcessor, VerticesProcessor}
import com.vesoft.nebula.tools.importer.reader.{
  CSVReader,
  HiveReader,
  JSONReader,
  KafkaReader,
  MySQLReader,
  Neo4JReader,
  ORCReader,
  ParquetReader,
  PulsarReader,
  SocketReader
}
import com.vesoft.nebula.tools.importer.writer.{NebulaGraphClientWriter, Writer}
import org.apache.log4j.Logger
import org.apache.spark.SparkConf

import scala.collection.JavaConverters._

final case class Argument(config: File = new File("application.conf"),
                          hive: Boolean = false,
                          directly: Boolean = false,
                          dry: Boolean = false,
                          reload: String = "")

final case class TooManyErrorsException(private val message: String) extends Exception(message)

/**
  * SparkClientGenerator is a simple spark job used to write data into Nebula Graph parallel.
  */
object Exchange {
  private[this] val LOG = Logger.getLogger(this.getClass)

  private[this] val DEFAULT_ERROR_TIMES = 16

  def main(args: Array[String]): Unit = {
    val PROGRAM_NAME = "Spark Writer"
    val options      = Configs.parser(args, PROGRAM_NAME)
    val c: Argument = options match {
      case Some(config) => config
      case _ =>
        LOG.error("Argument parse failed")
        sys.exit(-1)
    }

    val configs = Configs.parse(c.config)
    LOG.info(s"Config ${configs}")

    val session = SparkSession
      .builder()
      .appName(PROGRAM_NAME)
      .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .config(
        new SparkConf()
          .registerKryoClasses(
            Array(
              classOf[com.facebook.thrift.async.TAsyncClientManager]
            )))

    val spark = if (c.hive) {
      session.enableHiveSupport().getOrCreate()
    } else {
      session.getOrCreate()
    }

    // reload the execution sentence
    if (!c.reload.isEmpty) {
      val batchSuccess = spark.sparkContext.longAccumulator(s"batchSuccess.reload")
      val batchFailure = spark.sparkContext.longAccumulator(s"batchFailure.reload")

      spark.read
        .text(c.reload)
        .foreachPartition { records =>
          val hostAndPorts = configs.databaseConfig.addresses.map(HostAndPort.fromString).asJava
          val client = new AsyncGraphClientImpl(
            hostAndPorts,
            configs.connectionConfig.timeout,
            configs.connectionConfig.retry,
            configs.executionConfig.retry
          )
          client.setUser(configs.userConfig.user)
          client.setPassword(configs.userConfig.user)

          if (isSuccessfully(client.connect())) {
            val rateLimiter = RateLimiter.create(configs.rateConfig.limit)
            val service     = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(1))
            records.foreach { row =>
              val exec = row.getString(0)
              if (rateLimiter.tryAcquire(configs.rateConfig.timeout, TimeUnit.MILLISECONDS)) {
                val future = client.execute(exec)
                Futures.addCallback(
                  future,
                  new FutureCallback[Optional[Integer]] {
                    override def onSuccess(result: Optional[Integer]): Unit = {
                      batchSuccess.add(1)
                    }

                    override def onFailure(t: Throwable): Unit = {
                      if (batchFailure.value > DEFAULT_ERROR_TIMES) {
                        throw TooManyErrorsException("too many errors")
                      }
                      batchFailure.add(1)
                    }
                  },
                  service
                )
              } else {
                batchFailure.add(1)
              }
            }
            client.close()
          } else {
            LOG.error(
              s"Client connection failed. ${configs.userConfig.user}:${configs.userConfig.password}")
          }
        }
      sys.exit(0)
    }

    if (configs.tagsConfig.nonEmpty) {
      for (tagConfig <- configs.tagsConfig) {
        LOG.info(s"Processing Tag ${tagConfig.name}")

        val fieldKeys  = tagConfig.fields.keys.toList
        val nebulaKeys = tagConfig.fields.values.map(_.unwrapped.toString).toList

        val data = createDataSource(spark, tagConfig.dataSourceConfigEntry)
        if (data.isDefined && !c.dry) {
          val batchSuccess =
            spark.sparkContext.longAccumulator(s"batchSuccess.${tagConfig.name}")
          val batchFailure =
            spark.sparkContext.longAccumulator(s"batchFailure.${tagConfig.name}")

          val processor = new VerticesProcessor(
            repartition(data.get, tagConfig.partition, tagConfig.dataSourceConfigEntry.category),
            tagConfig,
            fieldKeys,
            nebulaKeys,
            configs,
            batchSuccess,
            batchFailure)

          processor.process()
          LOG.info(s"batchSuccess.${tagConfig.name}: ${batchSuccess.value}")
          LOG.info(s"batchFailure.${tagConfig.name}: ${batchFailure.value}")
        }
      }
    } else {
      LOG.warn("Tag is not defined")
    }

    if (configs.edgesConfig.nonEmpty) {
      for (edgeConfig <- configs.edgesConfig) {
        LOG.info(s"Processing Edge ${edgeConfig.name}")

        val fieldKeys  = edgeConfig.fields.keys.toList
        val nebulaKeys = edgeConfig.fields.values.map(_.unwrapped.toString).toList
        val data       = createDataSource(spark, edgeConfig.dataSourceConfigEntry)
        if (data.isDefined && !c.dry) {
          val batchSuccess = spark.sparkContext.longAccumulator(s"batchSuccess.${edgeConfig.name}")
          val batchFailure = spark.sparkContext.longAccumulator(s"batchFailure.${edgeConfig.name}")

          val processor = new EdgeProcessor(
            repartition(data.get, edgeConfig.partition, edgeConfig.dataSourceConfigEntry.category),
            edgeConfig,
            fieldKeys,
            nebulaKeys,
            configs,
            batchSuccess,
            batchFailure
          )

          processor.process()
        } else {
          LOG.warn("Edge is not defined")
        }
      }
    }
    spark.close()
  }

  /**
    * Create data source for different data type.
    *
    * @param session The Spark Session.
    * @param config  The config.
    * @return
    */
  private[this] def createDataSource(
      session: SparkSession,
      config: DataSourceConfigEntry
  ): Option[DataFrame] = {
    config.category match {
      case SourceCategory.PARQUET =>
        val parquetConfig = config.asInstanceOf[FileBaseSourceConfigEntry]
        LOG.info(s"""Loading Parquet files from ${parquetConfig.path}""")
        val reader = new ParquetReader(session, parquetConfig.path)
        Some(reader.read())
      case SourceCategory.ORC =>
        val orcConfig = config.asInstanceOf[FileBaseSourceConfigEntry]
        LOG.info(s"""Loading ORC files from ${orcConfig.path}""")
        val reader = new ORCReader(session, orcConfig.path)
        Some(reader.read())
      case SourceCategory.JSON =>
        val jsonConfig = config.asInstanceOf[FileBaseSourceConfigEntry]
        LOG.info(s"""Loading JSON files from ${jsonConfig.path}""")
        val reader = new JSONReader(session, jsonConfig.path)
        Some(reader.read())
      case SourceCategory.CSV =>
        val csvConfig = config.asInstanceOf[CSVSourceConfigEntry]
        LOG.info(s"""Loading CSV files from ${csvConfig.path}""")
        val reader = new CSVReader(session, csvConfig.path, csvConfig.separator, csvConfig.header)
        Some(reader.read())
      case SourceCategory.HIVE =>
        val hiveConfig = config.asInstanceOf[HiveSourceConfigEntry]
        LOG.info(s"""Loading from Hive and exec ${hiveConfig.exec}""")
        val reader = new HiveReader(session, hiveConfig.exec)
        Some(reader.read())
      // TODO: (darion.yaphet) Support Structured Streaming
      case SourceCategory.SOCKET =>
        val socketConfig = config.asInstanceOf[SocketSourceConfigEntry]
        LOG.warn("Socket streaming mode is not suitable for production environment")
        LOG.info(s"""Reading data stream from Socket ${socketConfig.host}:${socketConfig.port}""")
        val reader = new SocketReader(session, socketConfig.host, socketConfig.port)
        Some(reader.read())
      case SourceCategory.KAFKA =>
        val kafkaConfig = config.asInstanceOf[KafkaSourceConfigEntry]
        LOG.info(s"""Loading from Kafka ${kafkaConfig.server} and subscribe ${kafkaConfig.topic}""")
        val reader = new KafkaReader(session, kafkaConfig.server, kafkaConfig.topic)
        Some(reader.read())
      case SourceCategory.NEO4J =>
        val neo4jConfig = config.asInstanceOf[Neo4JSourceConfigEntry]
        LOG.info(s"Loading from neo4j config: ${neo4jConfig}")
        val reader = new Neo4JReader(session, neo4jConfig)
        Some(reader.read())
      case SourceCategory.MYSQL =>
        val mysqlConfig = config.asInstanceOf[MySQLSourceConfigEntry]
        LOG.info(s"Loading from mysql config: ${mysqlConfig}")
        val reader = new MySQLReader(session,
                                     mysqlConfig.host,
                                     mysqlConfig.port,
                                     mysqlConfig.database,
                                     mysqlConfig.table,
                                     mysqlConfig.user,
                                     mysqlConfig.password,
                                     mysqlConfig.sentence)
        Some(reader.read())
      case SourceCategory.PULSAR =>
        val pulsarConfig = config.asInstanceOf[PulsarSourceConfigEntry]
        LOG.info(s"Loading from pulsar config: ${pulsarConfig}")
        val reader = new PulsarReader(session,
                                      pulsarConfig.serviceUrl,
                                      pulsarConfig.adminUrl,
                                      pulsarConfig.options)
        Some(reader.read())
      case _ =>
        LOG.error(s"Data source ${config.category} not supported")
        None
    }
  }

  private[this] def createDataSink(entry: DataSinkConfigEntry,
                                   dataBaseConfigEntry: DataBaseConfigEntry,
                                   userConfigEntry: UserConfigEntry,
                                   connectionConfigEntry: ConnectionConfigEntry,
                                   executionRetry: Int,
                                   config: SchemaConfigEntry): Writer = {
    entry.category match {
      case SinkCategory.CLIENT =>
        LOG.info("Write to Nebula using Graph Client")
        new NebulaGraphClientWriter(dataBaseConfigEntry,
                                    userConfigEntry,
                                    connectionConfigEntry,
                                    executionRetry,
                                    config)
      case _ =>
        throw new IllegalArgumentException("Not Support")
    }
  }

  /**
    * Repartition the data frame using the specified partition number.
    *
    * @param frame
    * @param partition
    * @return
    */
  private[this] def repartition(frame: DataFrame,
                                partition: Int,
                                sourceCategory: SourceCategory.Value): DataFrame = {
    if (partition > 0 && !CheckPointHandler.checkSupportResume(sourceCategory)) {
      frame.repartition(partition).toDF
    } else {
      frame
    }
  }

  /**
    * Check the statement execution result.
    *
    * @param code The statement's execution result code.
    * @return
    */
  private[this] def isSuccessfully(code: Int) = code == ErrorCode.SUCCEEDED

}

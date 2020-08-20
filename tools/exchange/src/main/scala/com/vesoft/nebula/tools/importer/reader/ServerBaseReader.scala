/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.importer.reader

import java.util
import java.util.Date

import com.google.common.collect.Maps
import com.vesoft.nebula.tools.importer.utils.HDFSUtils
import com.vesoft.nebula.tools.importer.{JanusGraphSourceConfigEntry, Neo4JSourceConfigEntry}
import org.apache.commons.configuration.PropertiesConfiguration
import org.apache.log4j.Logger
import org.apache.spark.TaskContext
import org.apache.spark.sql.catalyst.expressions.GenericRowWithSchema
import org.apache.spark.sql.types.{DataTypes, StructField, StructType}
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal
import org.neo4j.driver.{AuthTokens, GraphDatabase}
import org.neo4j.spark.dataframe.CypherTypes
import org.neo4j.spark.utils.Neo4jSessionAwareIterator
import org.neo4j.spark.{Executor, Neo4jConfig}

import scala.collection.JavaConverters._

/**
  * @{link ServerBaseReader} is the abstract class of
  *        It include a spark session and a sentence which will sent to service.
  * @param session
  * @param sentence
  */
abstract class ServerBaseReader(override val session: SparkSession, sentence: String)
    extends Reader {

  override def close(): Unit = {
    session.close()
  }
}

/**
  * @{link HiveReader} extends the @{link ServerBaseReader}.
  *        The HiveReader reading data from Apache Hive via sentence.
  * @param session
  * @param sentence
  */
class HiveReader(override val session: SparkSession, sentence: String)
    extends ServerBaseReader(session, sentence) {
  override def read(): DataFrame = {
    session.sql(sentence)
  }
}

/**
  * The @{link MySQLReader} extends the @{link ServerBaseReader}.
  * The MySQLReader reading data from MySQL via sentence.
  *
  * @param session
  * @param host
  * @param port
  * @param database
  * @param table
  * @param user
  * @param password
  * @param sentence
  */
class MySQLReader(override val session: SparkSession,
                  host: String = "127.0.0.1",
                  port: Int = 3699,
                  database: String,
                  table: String,
                  user: String,
                  password: String,
                  sentence: String)
    extends ServerBaseReader(session, sentence) {
  override def read(): DataFrame = {
    val url = s"jdbc:mysql://${host}:${port}/${database}?useUnicode=true&characterEncoding=utf-8"
    session.read
      .format("jdbc")
      .option("url", url)
      .option("dbtable", table)
      .option("user", user)
      .option("password", password)
      .load()
  }
}

/**
  * @{link Neo4JReader} extends the @{link ServerBaseReader}
  * @param session
  * @param config
  */
class Neo4JReader(override val session: SparkSession, config: Neo4JSourceConfigEntry)
    extends ServerBaseReader(session, config.exec)
    with CheckPointSupport {

  @transient lazy private val LOG = Logger.getLogger(this.getClass)

  override def read(): DataFrame = {
    val totalCount: Long = {
      val returnIndex   = config.exec.toUpperCase.lastIndexOf("RETURN") + "RETURN".length
      val countSentence = config.exec.substring(0, returnIndex) + " count(*)"
      val driver =
        GraphDatabase.driver(s"${config.server}", AuthTokens.basic(config.user, config.password))
      val neo4JSession = driver.session()
      neo4JSession.run(countSentence).single().get(0).asLong()
    }
    val offsets = getOffsets(totalCount, config.parallel, config.checkPointPath, config.name)

    LOG.info(s"${config.name} offsets: ${offsets.mkString(",")}")

    if (offsets.forall(_.size == 0L)) {
      LOG.warn(s"${config.name} already write done from check point.")
      return session.createDataFrame(session.sparkContext.emptyRDD[Row], new StructType())
    }

    val neo4jConfig = Neo4jConfig(config.server,
                                  config.user,
                                  Some(config.password),
                                  config.database,
                                  config.encryption)

    val rdd = session.sparkContext
      .parallelize(offsets, offsets.size)
      .flatMap(offset => {
        if (config.checkPointPath.isDefined) {
          val path = s"${config.checkPointPath.get}/${config.name}.${TaskContext.getPartitionId()}"
          HDFSUtils.saveContent(path, offset.start.toString)
        }
        val query  = s"${config.exec} SKIP ${offset.start} LIMIT ${offset.size}"
        val result = new Neo4jSessionAwareIterator(neo4jConfig, query, Maps.newHashMap(), false)
        val fields = if (result.hasNext) result.peek().keys().asScala else List()
        val schema =
          if (result.hasNext)
            StructType(
              fields
                .map(k => (k, result.peek().get(k).`type`()))
                .map(keyType => CypherTypes.field(keyType)))
          else new StructType()
        result.map(record => {
          val row = new Array[Any](record.keys().size())
          for (i <- row.indices)
            row.update(i, Executor.convert(record.get(i).asObject()))
          new GenericRowWithSchema(values = row, schema).asInstanceOf[Row]
        })
      })

    if (rdd.isEmpty())
      throw new RuntimeException(
        "Please check your cypher sentence. because use it search nothing!")
    val schema = rdd.repartition(1).first().schema
    session.createDataFrame(rdd, schema)
  }
}

/**
  * @{link JanusGraphReader} extends the @{link ServerBaseReader}
  * @param session
  * @param janusGraphConfig
  */
class JanusGraphReader(override val session: SparkSession,
                       janusGraphConfig: JanusGraphSourceConfigEntry)
    extends ServerBaseReader(session, "")
    with CheckPointSupport {

  @transient lazy private val LOG = Logger.getLogger(this.getClass)

  override def read(): DataFrame = {
    def getPropertiesConfig = {
      val propertiesConfiguration = new PropertiesConfiguration(janusGraphConfig.propertiesPath)
      if (!propertiesConfiguration
            .getKeys()
            .asScala
            .toList
            .contains("gremlin.remote.remoteConnectionClass")) {
        propertiesConfiguration.addProperty(
          "gremlin.remote.remoteConnectionClass",
          "org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection")
      }
      propertiesConfiguration
    }

    val g      = traversal().withRemote(getPropertiesConfig)
    val entity = if (janusGraphConfig.isEdge) g.E() else g.V()
    val offsets = getOffsets(entity.hasLabel(janusGraphConfig.label).count().next(),
                             janusGraphConfig.parallel,
                             janusGraphConfig.checkPointPath,
                             janusGraphConfig.name)

    LOG.info(s"${janusGraphConfig.name} offsets: ${offsets.mkString(",")}")

    if (offsets.forall(_.size == 0L)) {
      LOG.warn(s"${janusGraphConfig.name} already write done from check point.")
      return session.createDataFrame(session.sparkContext.emptyRDD[Row], new StructType())
    }
    val rdd = session.sparkContext
      .parallelize(offsets, offsets.size)
      .flatMap(offset => {
        if (janusGraphConfig.checkPointPath.isDefined) {
          val path =
            s"${janusGraphConfig.checkPointPath.get}/${janusGraphConfig.name}.${TaskContext.getPartitionId()}"
          HDFSUtils.saveContent(path, offset.start.toString)
        }
        val g      = traversal().withRemote(getPropertiesConfig)
        val entity = if (janusGraphConfig.isEdge) g.E() else g.V()
        entity
          .hasLabel(janusGraphConfig.label)
          .skip(offset.start)
          .limit(offset.size)
          .valueMap()
          .asScala
          .map((record: java.util.Map[AnyRef, Nothing]) => {
            def getValue(field: Any) = record.get(field).asInstanceOf[Any] match {
              case list: util.ArrayList[Any] => list.get(0)
              case _                         => record.get(field)
            }
            val fields = record.keySet().asScala.toList.map(_.toString)
            val schema = StructType(fields.map(field => {
              getValue(field) match {
                case _: Int     => StructField(field, DataTypes.IntegerType)
                case _: Long    => StructField(field, DataTypes.LongType)
                case _: String  => StructField(field, DataTypes.StringType)
                case _: Double  => StructField(field, DataTypes.DoubleType)
                case _: Boolean => StructField(field, DataTypes.BooleanType)
                case _: Float   => StructField(field, DataTypes.FloatType)
                case _: Date    => StructField(field, DataTypes.DateType)
                case _: Byte    => StructField(field, DataTypes.BinaryType)
                case v          => throw new RuntimeException(s"Not support type ${v}")
              }
            }))
            val row = fields
              .map(getValue)
              .toArray
            new GenericRowWithSchema(row, schema).asInstanceOf[Row]
          })
      })
    if (rdd.isEmpty())
      throw new RuntimeException(
        s"It shouldn't happen. Maybe something wrong ${janusGraphConfig.propertiesPath}")
    val schema = rdd.repartition(1).first().schema
    session.createDataFrame(rdd, schema)
  }
}

/**
  *
  * @param session
  * @param sentence
  */
class NebulaReader(override val session: SparkSession, sentence: String)
    extends ServerBaseReader(session, sentence) {
  override def read(): DataFrame = ???
}

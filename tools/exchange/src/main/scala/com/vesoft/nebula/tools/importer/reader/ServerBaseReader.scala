/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.importer.reader

import com.google.common.collect.Maps
import com.vesoft.nebula.tools.importer.utils.HDFSUtils
import com.vesoft.nebula.tools.importer.{Neo4JSourceConfigEntry, Offset}
import org.apache.log4j.Logger
import org.apache.spark.TaskContext
import org.apache.spark.sql.{DataFrame, Encoders, Row, SparkSession}
import org.apache.spark.sql.catalyst.expressions.GenericRowWithSchema
import org.apache.spark.sql.types.StructType
import org.apache.tinkerpop.gremlin.process.computer.clustering.peerpressure.{
  ClusterCountMapReduce,
  PeerPressureVertexProgram
}
import org.apache.tinkerpop.gremlin.spark.process.computer.SparkGraphComputer
import org.apache.tinkerpop.gremlin.spark.structure.io.PersistedOutputRDD
import org.apache.tinkerpop.gremlin.structure.util.GraphFactory
import org.neo4j.driver.{AuthTokens, GraphDatabase}
import org.neo4j.spark.{Executor, Neo4jConfig}
import org.neo4j.spark.dataframe.CypherTypes
import org.neo4j.spark.utils.Neo4jSessionAwareIterator

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
final class HiveReader(override val session: SparkSession, sentence: String)
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
final class MySQLReader(override val session: SparkSession,
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
final class Neo4JReader(override val session: SparkSession, config: Neo4JSourceConfigEntry)
    extends ServerBaseReader(session, config.exec) {

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
    if (totalCount <= 0L) throw new RuntimeException(s"your cypher ${config.exec} return nothing!")

    val partitionCount = config.parallel

    val offsets = {
      val batchSizes = List.fill((totalCount % partitionCount).toInt)(
        totalCount / partitionCount + 1) ::: List.fill(
        (partitionCount - totalCount % partitionCount).toInt)(totalCount / partitionCount)

      val initEachPartitionOffset = {
        var offset = 0L
        0L :: (for (batchSize <- batchSizes.init) yield {
          offset += batchSize
          offset
        })
      }

      val eachPartitionOffset = config.checkPointPath match {
        case Some(path) =>
          val files = Range(0, partitionCount).map(i => s"${path}/${config.name}.${i}").toList
          if (files.forall(x => HDFSUtils.exists(x)))
            files.map(file => HDFSUtils.getContent(file).trim.toLong).sorted
          else initEachPartitionOffset
        case _ => initEachPartitionOffset
      }

      val eachPartitionLimit = {
        batchSizes
          .zip(initEachPartitionOffset.zip(eachPartitionOffset))
          .map(x => {
            x._1 - (x._2._2 - x._2._1)
          })
      }
      eachPartitionOffset.zip(eachPartitionLimit).map(x => Offset(x._1, x._2))
    }
    LOG.info(s"${config.name} offsets: ${offsets.mkString(",")}")

    if (offsets.forall(_.size == 0L)) {
      LOG.warn(s"${config.name} already write done from check point.")
      return session.createDataFrame(session.sparkContext.emptyRDD[Row], new StructType())
    }
    if (offsets.exists(_.size < 0L))
      throw new RuntimeException(
        s"Your check point file maybe broken. Please delete ${config.name}.* file")

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
  * @param sentence
  * @param isEdge
  */
final class JanusGraphReader(override val session: SparkSession,
                             sentence: String,
                             isEdge: Boolean = false)
    extends ServerBaseReader(session, sentence) {

  override def read(): DataFrame = {
    val graph = GraphFactory.open("conf/hadoop/hadoop-gryo.properties")
    graph.configuration().setProperty("gremlin.hadoop.graphWriter", classOf[PersistedOutputRDD])
    graph.configuration().setProperty("gremlin.spark.persistContext", true)

    val result = graph
      .compute(classOf[SparkGraphComputer])
      .program(PeerPressureVertexProgram.build().create(graph))
      .mapReduce(ClusterCountMapReduce.build().memoryKey("clusterCount").create())
      .submit()
      .get()

    if (isEdge) {
      result.graph().edges()
    } else {
      result.graph().variables().asMap()
    }
    null
  }
}

/**
  *
  * @param session
  * @param sentence
  */
final class NebulaReader(override val session: SparkSession, sentence: String)
    extends ServerBaseReader(session, sentence) {
  override def read(): DataFrame = ???
}

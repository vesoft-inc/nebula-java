/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.importer.reader

import com.google.common.collect.Maps
import com.vesoft.nebula.tools.importer.utils.HDFSUtils
import com.vesoft.nebula.tools.importer.{CheckPointHandler, Offset}
import com.vesoft.nebula.tools.importer.config.{
  HiveSourceConfigEntry,
  JanusGraphSourceConfigEntry,
  MySQLSourceConfigEntry,
  Neo4JSourceConfigEntry,
  ServerDataSourceConfigEntry
}
import org.apache.log4j.Logger
import org.apache.spark.TaskContext
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
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
  * ServerBaseReader is the abstract class of
  * It include a spark session and a sentence which will sent to service.
  * @param session
  * @param sentence
  */
abstract class ServerBaseReader(override val session: SparkSession, val sentence: String)
    extends Reader {

  override def close(): Unit = {
    session.close()
  }
}

/**
  * HiveReader extends the @{link ServerBaseReader}.
  * The HiveReader reading data from Apache Hive via sentence.
  * @param session
  * @param hiveConfig
  */
class HiveReader(override val session: SparkSession, hiveConfig: HiveSourceConfigEntry)
    extends ServerBaseReader(session, hiveConfig.sentence) {
  override def read(): DataFrame = {
    session.sql(sentence)
  }
}

/**
  * The MySQLReader extends the ServerBaseReader.
  * The MySQLReader reading data from MySQL via sentence.
  *
  * @param session
  * @param mysqlConfig
  */
class MySQLReader(override val session: SparkSession, mysqlConfig: MySQLSourceConfigEntry)
    extends ServerBaseReader(session, mysqlConfig.sentence) {
  override def read(): DataFrame = {
    val url =
      s"jdbc:mysql://${mysqlConfig.host}:${mysqlConfig.port}/${mysqlConfig.database}?useUnicode=true&characterEncoding=utf-8"
    session.read
      .format("jdbc")
      .option("url", url)
      .option("dbtable", mysqlConfig.table)
      .option("user", mysqlConfig.user)
      .option("password", mysqlConfig.password)
      .load()
  }
}

/**
  * Neo4JReader extends the ServerBaseReader
  * @param session
  * @param neo4jConfig
  */
class Neo4JReader(override val session: SparkSession, neo4jConfig: Neo4JSourceConfigEntry)
    extends ServerBaseReader(session, neo4jConfig.sentence) {

  @transient lazy private val LOG = Logger.getLogger(this.getClass)

  override def read(): DataFrame = {

    val offsets = getOffsets
    LOG.info(s"${neo4jConfig.name} offsets: ${offsets.mkString(",")}")
    if (offsets.forall(_.size == 0L)) {
      LOG.warn(s"${neo4jConfig.name} already write done from check point.")
      return session.createDataFrame(session.sparkContext.emptyRDD[Row], new StructType())
    }
    if (offsets.exists(_.size < 0L))
      throw new RuntimeException(
        s"Your check point file maybe broken. Please delete ${neo4jConfig.name}.* file")

    val config = Neo4jConfig(neo4jConfig.server,
                             neo4jConfig.user,
                             Some(neo4jConfig.password),
                             neo4jConfig.database,
                             neo4jConfig.encryption)

    val rdd = session.sparkContext
      .parallelize(offsets, offsets.size)
      .flatMap(offset => {
        if (neo4jConfig.checkPointPath.isDefined) {
          val path =
            s"${neo4jConfig.checkPointPath.get}/${neo4jConfig.name}.${TaskContext.getPartitionId()}"
          HDFSUtils.saveContent(path, offset.start.toString)
        }
        val query  = s"${neo4jConfig.sentence} SKIP ${offset.start} LIMIT ${offset.size}"
        val result = new Neo4jSessionAwareIterator(config, query, Maps.newHashMap(), false)
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

  def getOffsets: List[Offset] = {

    val totalCount: Long = {
      val returnIndex   = neo4jConfig.sentence.toUpperCase.lastIndexOf("RETURN") + "RETURN".length
      val countSentence = neo4jConfig.sentence.substring(0, returnIndex) + " count(*)"
      val driver =
        GraphDatabase.driver(s"${neo4jConfig.server}",
                             AuthTokens.basic(neo4jConfig.user, neo4jConfig.password))
      val neo4JSession = driver.session()
      neo4JSession.run(countSentence).single().get(0).asLong()
    }
    if (totalCount <= 0L)
      throw new RuntimeException(s"your cypher ${neo4jConfig.sentence} return nothing!")

    val partitionCount = neo4jConfig.parallel

    val batchSizes = List.fill((totalCount % partitionCount).toInt)(totalCount / partitionCount + 1) ::: List
      .fill((partitionCount - totalCount % partitionCount).toInt)(totalCount / partitionCount)

    val initEachPartitionOffset = {
      var offset = 0L
      0L :: (for (batchSize <- batchSizes.init) yield {
        offset += batchSize
        offset
      })
    }

    val eachPartitionOffset = neo4jConfig.checkPointPath match {
      case Some(path) =>
        val files = Range(0, partitionCount).map(i => s"${path}/${neo4jConfig.name}.${i}").toList
        if (files.forall(x => HDFSUtils.exists(x)))
          files.map(file => CheckPointHandler.fetchOffset(file)).sorted
        else {
          LOG.warn(
            s"Can't read ${neo4jConfig.name} offset in files, maybe this is first run or file of check point was missing.")
          initEachPartitionOffset
        }
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
}

/**
  * JanusGraphReader extends the link ServerBaseReader
  * @param session
  * @param janusGraphConfig
  */
class JanusGraphReader(override val session: SparkSession,
                       janusGraphConfig: JanusGraphSourceConfigEntry)
    extends ServerBaseReader(session, janusGraphConfig.sentence) {

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

    if (janusGraphConfig.isEdge) {
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
  * @param nebulaConfig
  */
class NebulaReader(override val session: SparkSession, nebulaConfig: ServerDataSourceConfigEntry)
    extends ServerBaseReader(session, nebulaConfig.sentence) {
  override def read(): DataFrame = ???
}

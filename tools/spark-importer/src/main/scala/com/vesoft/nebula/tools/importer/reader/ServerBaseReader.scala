/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.importer.reader

import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}
import org.apache.tinkerpop.gremlin.process.computer.clustering.peerpressure.{
  ClusterCountMapReduce,
  PeerPressureVertexProgram
}
import org.apache.tinkerpop.gremlin.spark.process.computer.SparkGraphComputer
import org.apache.tinkerpop.gremlin.spark.structure.io.PersistedOutputRDD
import org.apache.tinkerpop.gremlin.structure.util.GraphFactory
import org.neo4j.spark.Neo4j

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
  * @param sentence
  */
class Neo4JReader(override val session: SparkSession, offset: Long, sentence: String)
    extends ServerBaseReader(session, sentence) {
  override def read(): DataFrame = {
    val neo = Neo4j(session.sparkContext)
    neo.cypher(if (offset <= 0) sentence else sentence + s" SKIP ${offset}").loadDataFrame
  }
}

/**
  * @{link JanusGraphReader} extends the @{link ServerBaseReader}
  * @param session
  * @param sentence
  * @param isEdge
  */
class JanusGraphReader(override val session: SparkSession,
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
class NebulaReader(override val session: SparkSession, sentence: String)
    extends ServerBaseReader(session, sentence) {
  override def read(): DataFrame = ???
}

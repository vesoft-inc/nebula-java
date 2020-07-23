/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.importer.reader

import com.vesoft.nebula.tools.importer.{Neo4JSourceConfigEntry, Offset}
import org.apache.spark.sql.{DataFrame, Encoders, Row, SparkSession}
import org.apache.tinkerpop.gremlin.process.computer.clustering.peerpressure.{
  ClusterCountMapReduce,
  PeerPressureVertexProgram
}
import org.apache.tinkerpop.gremlin.spark.process.computer.SparkGraphComputer
import org.apache.tinkerpop.gremlin.spark.structure.io.PersistedOutputRDD
import org.apache.tinkerpop.gremlin.structure.util.GraphFactory
import org.neo4j.driver.{AuthTokens, GraphDatabase}
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
    extends ServerBaseReader(session, config.exec) {
  override def read(): DataFrame = {
    // TODO get total size of Neo4J and partitions
    val offsets = Seq(Offset(0, 100), Offset(101, 100))

    session
      .createDataset(offsets)(Encoders.kryo[Offset])
      .repartition(offsets.size)
      .flatMap { offset =>
        val driver = GraphDatabase.driver(s"bolt://${config.server}",
                                          AuthTokens.basic(config.user, config.password))
        val neo4JSession = driver.session()
        val result       = neo4JSession.run(s"${config.exec} SKIP ${offset.start} LIMIT ${offset.size}")
        val fields       = result.keys().asScala

        for {
          record <- result.list().asScala
          values = fields.map(record.get(_)) //TODO extra values
        } yield Row(values)
      }(Encoders.kryo[Row])
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

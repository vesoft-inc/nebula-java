/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.importer.reader

import java.util
import java.util.Date

import com.google.common.collect.Maps
import com.vesoft.nebula.tools.importer.config._
import com.vesoft.nebula.tools.importer.utils.HDFSUtils
import org.apache.log4j.Logger
import org.apache.spark.TaskContext
import org.apache.spark.sql.catalyst.expressions.GenericRowWithSchema
import org.apache.spark.sql.types.{DataTypes, StructField, StructType}
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.apache.tinkerpop.gremlin.driver.Cluster
import org.apache.tinkerpop.gremlin.driver.remote.DriverRemoteConnection
import org.apache.tinkerpop.gremlin.driver.ser.GryoMessageSerializerV3d0
import org.apache.tinkerpop.gremlin.process.traversal.AnonymousTraversalSource.traversal
import org.neo4j.driver.{AuthTokens, GraphDatabase}
import org.neo4j.spark.dataframe.CypherTypes
import org.neo4j.spark.utils.Neo4jSessionAwareIterator
import org.neo4j.spark.{Executor, Neo4jConfig}

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
    extends ServerBaseReader(session, neo4jConfig.sentence)
    with CheckPointSupport {

  @transient lazy private val LOG = Logger.getLogger(this.getClass)

  override def read(): DataFrame = {
    val totalCount: Long = {
      val returnIndex   = neo4jConfig.sentence.toUpperCase.lastIndexOf("RETURN") + "RETURN".length
      val countSentence = neo4jConfig.sentence.substring(0, returnIndex) + " count(*)"
      val driver =
        GraphDatabase.driver(s"${neo4jConfig.server}",
                             AuthTokens.basic(neo4jConfig.user, neo4jConfig.password))
      val neo4JSession = driver.session()
      neo4JSession.run(countSentence).single().get(0).asLong()
    }

    val offsets =
      getOffsets(totalCount, neo4jConfig.parallel, neo4jConfig.checkPointPath, neo4jConfig.name)
    LOG.info(s"${neo4jConfig.name} offsets: ${offsets.mkString(",")}")
    if (offsets.forall(_.size == 0L)) {
      LOG.warn(s"${neo4jConfig.name} already write done from check point.")
      return session.createDataFrame(session.sparkContext.emptyRDD[Row], new StructType())
    }

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
}

/**
  * JanusGraphReader extends the link ServerBaseReader
  * @param session
  * @param janusGraphConfig
  */
class JanusGraphReader(override val session: SparkSession,
                       janusGraphConfig: JanusGraphSourceConfigEntry)
    extends ServerBaseReader(session, "")
    with CheckPointSupport {

  @transient lazy private val LOG = Logger.getLogger(this.getClass)

  private def getTypeAndValue(field: Any, value: Any, prefix: String): Seq[(StructField, Any)] = {
    value match {
      case v: Int =>
        Array((StructField(prefix + field.toString, DataTypes.IntegerType), v))
      case v: Long =>
        Array((StructField(prefix + field.toString, DataTypes.LongType), v))
      case v: String =>
        Array((StructField(prefix + field.toString, DataTypes.StringType), v))
      case v: Double =>
        Array((StructField(prefix + field.toString, DataTypes.DoubleType), v))
      case v: Boolean =>
        Array((StructField(prefix + field.toString, DataTypes.BooleanType), v))
      case v: Float =>
        Array((StructField(prefix + field.toString, DataTypes.FloatType), v))
      case v: Date =>
        Array((StructField(prefix + field.toString, DataTypes.DateType), v))
      case v: Byte =>
        Array((StructField(prefix + field.toString, DataTypes.BinaryType), v))
      case m: util.Map[_, _] =>
        m.asScala
          .flatMap(pair => getTypeAndValue(pair._1, pair._2, s"${field.toString}."))
          .toSeq
      case _ =>
        throw new RuntimeException(s"Not support type!")
    }
  }

  private def getRemoteConnection = {
    val serializer = new GryoMessageSerializerV3d0()
    serializer.configure(new util.HashMap[String, Object]() {
      put("ioRegistries",
          util.Arrays.asList("org.janusgraph.graphdb.tinkerpop.JanusGraphIoRegistry"))
    }, null)
    val cluster = Cluster
      .build(janusGraphConfig.host)
      .port(janusGraphConfig.port)
      .serializer(serializer)
      .create()
    DriverRemoteConnection.using(cluster)
  }

  override def read(): DataFrame = {
    val g      = traversal().withRemote(getRemoteConnection)
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
        val g      = traversal().withRemote(getRemoteConnection)
        val entity = if (janusGraphConfig.isEdge) g.E() else g.V()
        entity
          .hasLabel(janusGraphConfig.label)
          .skip(offset.start)
          .limit(offset.size)
          .elementMap()
          .asScala
          .map((record: java.util.Map[AnyRef, Nothing]) => {
            def getValue(field: Any) = record.get(field).asInstanceOf[Any] match {
              case list: util.ArrayList[_] => list.get(0)
              case x                       => x
            }
            val fields =
              record.keySet().asScala.toList.filter(!janusGraphConfig.isEdge || _.toString != "id")
            val typeAndValue =
              fields.flatMap(field => getTypeAndValue(field, getValue(field), "")).sortBy(_._1.name)
            val schema = StructType(typeAndValue.map(_._1))
            val row    = typeAndValue.map(_._2).toArray
            new GenericRowWithSchema(row, schema).asInstanceOf[Row]
          })
      })
    if (rdd.isEmpty())
      throw new RuntimeException(s"It shouldn't happen. Maybe something wrong ${janusGraphConfig}")
    val schema = rdd.repartition(1).first().schema
    session.createDataFrame(rdd, schema)
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

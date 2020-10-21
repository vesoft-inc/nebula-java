/*
 * Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.connector.example

import com.facebook.thrift.protocol.TCompactProtocol
import com.vesoft.nebula.tools.connector.{NebulaDataFrameReader, NebulaDataFrameWriter}
import org.apache.spark.SparkConf
import org.apache.spark.graphx.Graph
import org.apache.spark.sql.{Dataset, Row, SparkSession}
import org.slf4j.LoggerFactory

object Main {
  private val LOG = LoggerFactory.getLogger(this.getClass)

  def main(args: Array[String]): Unit = {

    val sparkConf = new SparkConf
    sparkConf
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .registerKryoClasses(Array[Class[_]](classOf[TCompactProtocol]))
    val spark = SparkSession
      .builder()
//      .master("local")
      .config(sparkConf)
      .getOrCreate()

    writeNebulaVertex(spark)
  }

  def readNebulaVertex(spark: SparkSession) = {
    LOG.info("start loading nebula vertex to DataFrame ========\n")
    val vertexDataset: Dataset[Row] =
      spark.read
        .nebula("127.0.0.1:45500", "nb", "100")
        .loadVerticesToDF("player", List())
    vertexDataset.printSchema()
    vertexDataset.show()
  }

  def readNebulaEdge(spark: SparkSession): Unit = {
    LOG.info("start loading nebula edge to DataFrame ========\n")
    val edgeDataset: Dataset[Row] =
      spark.read
        .nebula("127.0.0.1:45500", "nb", "100")
        .loadEdgesToDF("serve", List())
    edgeDataset.printSchema()
    edgeDataset.show()
  }

  def readGraphX(spark: SparkSession): Unit = {
    LOG.info("start loading nebula vertex to graphx's vertex ========\n")
    val vertexRDD = spark.read
      .nebula("127.0.0.1:45500", "nb", "100")
      .loadVerticesToGraphx("player", List())

    LOG.info("start loading nebula edge to graphx's edge ========\n")
    val edgeRDD = spark.read
      .nebula("127.0.0.1:45500", "nb", "100")
      .loadEdgesToGraphx("serve", List())

    val graph = Graph(vertexRDD, edgeRDD)
    graph.degrees.foreach(println(_))
  }

  def writeNebulaVertex(spark: SparkSession): Unit = {
    val df = spark.read.json("hdfs://192.168.8.171:9000/home/darion/vertex.json")
    df.show()

    df.write
      .nebula("192.168.8.171:3699", "nb", "100")
      .writeVertices("player", "vertexId")
    spark.stop()
  }

  def writeNebulaEdge(spark: SparkSession): Unit = {
    val df = spark.read.json("tools/nebula-spark/src/main/resources/edge")
    df.show()

    df.write
      .nebula("192.168.8.171:3699", "nb", "100")
      .writeEdges("follow", "source", "target")
    spark.stop()
  }
}

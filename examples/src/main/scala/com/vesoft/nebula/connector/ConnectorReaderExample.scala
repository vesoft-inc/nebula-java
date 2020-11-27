/*
 * Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.connector

import com.facebook.thrift.protocol.TCompactProtocol
import com.vesoft.nebula.tools.connector.NebulaDataFrameReader
import org.apache.spark.SparkConf
import org.apache.spark.graphx.Graph
import org.apache.spark.sql.{Dataset, Row, SparkSession}
import org.slf4j.LoggerFactory

object ConnectorReaderExample {
  private val LOG = LoggerFactory.getLogger(this.getClass)

  def main(args: Array[String]): Unit = {

    val sparkConf = new SparkConf
    sparkConf
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .registerKryoClasses(Array[Class[_]](classOf[TCompactProtocol]))
    val spark = SparkSession
      .builder()
      .master("local")
      .config(sparkConf)
      .getOrCreate()

    readNebulaVertex(spark)
    readNebulaEdge(spark)
    readGraphX(spark)

    spark.close()
    sys.exit()
  }

  def readNebulaVertex(spark: SparkSession): Unit = {
    LOG.info("start loading nebula vertex to DataFrame ========")
    val vertexDataset: Dataset[Row] =
      spark.read
        .nebula("127.0.0.1:45500", "nb", "100")
        .withTimeout(5000)
        .withConnectionRetry(1)
        .withExecutionRetry(1)
        .loadVerticesToDF("player", "name,age")
    val count = vertexDataset.count()
    vertexDataset.printSchema()
    vertexDataset.show()
    LOG.info("**********vertex count:" + count, null);
  }

  def readNebulaEdge(spark: SparkSession): Unit = {
    LOG.info("start loading nebula edge to DataFrame ========")
    val edgeDataset: Dataset[Row] =
      spark.read
        .nebula("127.0.0.1:45500", "nb", "100")
        .loadEdgesToDF("serve", "*")
    edgeDataset.printSchema()
    edgeDataset.show()
  }

  def readGraphX(spark: SparkSession): Unit = {
    LOG.info("start loading nebula vertex to graphx's vertex ========")
    val vertexRDD = spark.read
      .nebula("127.0.0.1:45500", "nb", "100")
      .loadVerticesToGraphx("player", "*")

    LOG.info("start loading nebula edge to graphx's edge ========")
    val edgeRDD = spark.read
      .nebula("127.0.0.1:45500", "nb", "100")
      .loadEdgesToGraphx("serve", "*")

    val graph = Graph(vertexRDD, edgeRDD)
    graph.degrees.foreach(println(_))
  }

}

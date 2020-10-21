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
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}
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
      .config(sparkConf)
      .master("local")
      .getOrCreate()

//     read vertex and edge from nebula
    val vertexDataset: Dataset[Row] =
      spark.read
        .nebula("127.0.0.1:45500", "nb", "100")
        .loadVertices("player", "*")
    vertexDataset.printSchema()
    vertexDataset.show()

    val edgeDataset: Dataset[Row] =
      spark.read
        .nebula("127.0.0.1:45500", "nb", "100")
        .loadEdges("serve", "start_year,end_year")

    edgeDataset.printSchema()
    edgeDataset.show()

    LOG.info(s"vertex count=${vertexDataset.count()}, edge count=${edgeDataset.count()}")

  }

}

/*
 * Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.connector.example

import com.facebook.thrift.protocol.TCompactProtocol
import com.vesoft.nebula.bean.Parameters
import com.vesoft.nebula.tools.connector.DataTypeEnum
import org.apache.spark.SparkConf
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
      .config(sparkConf)
      .master("local")
      .getOrCreate()

    val vertexDataset: Dataset[Row] = spark.read
      .format("nebula")
      .option(Parameters.HOST_AND_PORTS, "127.0.0.1:45500")
      .option(Parameters.PARTITION_NUMBER, "100")
      .option(Parameters.SPACE_NAME, "nb")
      .option(Parameters.TYPE, DataTypeEnum.VERTEX.toString)
      .option(Parameters.LABEL, "player")
      .option(Parameters.RETURN_COLS, "")
      .load()
    vertexDataset.printSchema()
    vertexDataset.show()

    val edgeDataset: Dataset[Row] = spark.read
      .format("nebula")
      .option(Parameters.HOST_AND_PORTS, "127.0.0.1:45500")
      .option(Parameters.PARTITION_NUMBER, "100")
      .option(Parameters.SPACE_NAME, "nb")
      .option(Parameters.TYPE, DataTypeEnum.EDGE.toString)
      .option(Parameters.LABEL, "serve")
      .option(Parameters.RETURN_COLS, "")
      .load()
    edgeDataset.printSchema()
    edgeDataset.show()

    LOG.info(s"vertex count=${vertexDataset.count()}, edge count=${edgeDataset.count()}")
  }

}

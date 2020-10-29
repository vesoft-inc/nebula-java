/*
 * Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.connector

import com.facebook.thrift.protocol.TCompactProtocol
import com.vesoft.nebula.tools.connector.NebulaDataFrameWriter
import com.vesoft.nebula.tools.connector.writer.NebulaBatchWriterUtils
import org.apache.spark.SparkConf
import org.apache.spark.sql.{SparkSession}
import org.apache.spark.storage.StorageLevel
import org.slf4j.LoggerFactory

object ConnectorWriterExample {
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

    writeNebulaVertex(spark)
    writeNebulaEdge(spark)
    batchWriteNebulaVertex(spark)
    batchWriteNebulaEdge(spark)
    spark.close()
    sys.exit()
  }

  def writeNebulaVertex(spark: SparkSession): Unit = {
    val df = spark.read.json("examples/src/main/resources/vertex")
    df.show()
    df.persist(StorageLevel.MEMORY_AND_DISK_SER)

    df.write
      .nebula("127.0.0.1:3699", "nb", "100")
      .writeVertices("player", "vertexId", "hash")
  }

  def writeNebulaEdge(spark: SparkSession): Unit = {
    val df = spark.read.json("examples/src/main/resources/edge")
    df.show()
    df.persist(StorageLevel.MEMORY_AND_DISK_SER)
    df.write
      .nebula("127.0.0.1:3699", "nb", "100")
      .writeEdges("follow", "source", "target")
  }

  def batchWriteNebulaVertex(spark: SparkSession): Unit = {
    val df = spark.read.json("examples/src/main/resources/vertex")
    df.show()
    df.persist(StorageLevel.MEMORY_AND_DISK_SER)

    new NebulaBatchWriterUtils()
      .batchInsert("127.0.0.1:3699", "nb", 2000)
      .batchToNebulaVertex(df, "player", "vertexId")
  }

  def batchWriteNebulaEdge(spark: SparkSession): Unit = {
    val df = spark.read.json("examples/src/main/resources/edge")
    df.show()
    df.persist(StorageLevel.MEMORY_AND_DISK_SER)

    new NebulaBatchWriterUtils()
      .batchInsert("127.0.0.1:3699", "nb", 2000)
      .batchToNebulaEdge(df, "follow", "source", "target")
  }

}

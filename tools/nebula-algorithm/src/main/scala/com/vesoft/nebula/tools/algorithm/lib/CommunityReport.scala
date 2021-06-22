/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.algorithm.lib

import java.io.{File, PrintWriter}

import org.apache.spark.sql.types.{LongType, StringType, DoubleType, StructField, StructType}
import org.apache.spark.sql.{Encoder, Row, SparkSession}

/**
  * community analysis report
  */
object CommunityReport {

  def main(args: Array[String]): Unit = {
    if (args.length != 2) {
      System.out.println(
        "Usage: " + "spark-submit --class com.vesoft.nebula.tools.algorithm.lib.CommunityReport nebula-algorithm-xx.jar <file> <result.path>")
      return
    }

    val file                                    = args(0)
    val resultPath                              = args(1)
    val spark                                   = SparkSession.builder().appName("report").master("local").getOrCreate()
    implicit val encoder: Encoder[(Long, Long)] = org.apache.spark.sql.Encoders.kryo[(Long, Long)]

    val df          = spark.read.option("header", true).csv(file)
    val vertexCount = df.count()

    // 社区详情
    val community = df
      .map(row => (row.get(1).toString.toLong, row.get(0).toString.toLong))(encoder)
      .rdd
      .groupByKey()
      .map(comm => {
        (comm._1, comm._2.toSeq, comm._2.toSeq.size / vertexCount)
      })

    // 社区数量
    val communityNum = community.count()

    // 平均一个社区内的节点数量
    val averageCommunityDegree = vertexCount / communityNum

    val communityFile = if (resultPath.endsWith("/")) {
      resultPath + "community"
    } else {
      resultPath + "/community"
    }

    community.saveAsTextFile(communityFile)

    val writer = new PrintWriter(new File(communityFile + ".report"))
    writer.write("社区分析: ")

    writer.write("\n  社区数量: " + communityNum)
    writer.write("\n  社区内平均节点数: " + averageCommunityDegree)
    writer.write("\n")
    writer.close()
  }
}

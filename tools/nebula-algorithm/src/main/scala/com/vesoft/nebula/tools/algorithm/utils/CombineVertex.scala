/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.algorithm.utils

import org.apache.spark.sql.SparkSession

object CombineVertex {
  def main(args: Array[String]): Unit = {
    if (args.length != 2) {
      System.out.println(
        "Usage: " + "spark-submit --class com.vesoft.nebula.tools.algorithm.utils.CombineVertex nebula-algorithm-xx.jar <vertex.path> <result.path>")
      return
    }

    val vertexPath = args(0)
    val resultPath = args(1)
    println("vertexPath: " + vertexPath)
    println("resultPath: " + resultPath)

    val spark = SparkSession
      .builder()
      .appName("CombineData")
      .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .config("spark.sql.shuffle.partitions", "1")
      .getOrCreate()

    val vertexDF = spark.read.csv(vertexPath).toDF("vid", "col1", "col2", "col3", "col4")
    val resultDF = spark.read.csv(resultPath).toDF("vid", "louvain")

    println("\n*****\n start to combine vertex and louvain result ")
    vertexDF.join(resultDF, "vid").write.csv("/tmp/louvain/result")
  }
}

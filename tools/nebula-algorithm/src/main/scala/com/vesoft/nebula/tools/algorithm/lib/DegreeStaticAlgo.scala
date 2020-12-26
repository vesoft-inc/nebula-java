/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.algorithm.lib

import com.vesoft.nebula.tools.algorithm.utils.NebulaUtil
import org.apache.log4j.Logger
import org.apache.spark.graphx.{Graph, VertexRDD}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}
import org.apache.spark.sql.types.{IntegerType, LongType, StructField, StructType}

object DegreeStaticAlgo {

  private val LOGGER = Logger.getLogger(this.getClass)

  val ALGORITHM: String = "DegreeStatic"

  /**
    * run the pagerank algorithm for nebula graph
    *
    * 1. get the configuration which is configured in application.conf
    * 2. read nebula edge data
    * 3. construct initial graph
    * 4. execute pagerank algorithm
    * 5. save the pagerank result
    */
  def apply(spark: SparkSession, dataset: Dataset[Row]): DataFrame = {

    val graph: Graph[None.type, Double] = NebulaUtil.loadInitGraph(dataset, false)

    val degreeResultRDD = execute(graph)

    val schema = StructType(
      List(
        StructField("_id", LongType, nullable = false),
        StructField("_degree", IntegerType, nullable = true),
        StructField("_inDegree", IntegerType, nullable = true),
        StructField("_outDegree", IntegerType, nullable = true)
      ))
    val algoResult = spark.sqlContext
      .createDataFrame(degreeResultRDD, schema)

    algoResult
  }

  def execute(graph: Graph[None.type, Double]): RDD[Row] = {
    val degreeRdd: VertexRDD[Int]    = graph.degrees
    val inDegreeRdd: VertexRDD[Int]  = graph.inDegrees
    val outDegreeRdd: VertexRDD[Int] = graph.outDegrees

    val degreeAndInDegree: VertexRDD[(Int, Int)] =
      degreeRdd.leftJoin(inDegreeRdd)((id, d, inD) => (d, inD.getOrElse(0)))

    val result = degreeAndInDegree.leftJoin(outDegreeRdd)((id, dAndInD, opt) =>
      (dAndInD._1, dAndInD._2, opt.getOrElse(0)))
    result.map(vertex => Row(vertex._1, vertex._2._1, vertex._2._2, vertex._2._3))
  }

}

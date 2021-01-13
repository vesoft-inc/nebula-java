/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.algorithm.lib

import com.vesoft.nebula.tools.algorithm.config.KCoreConfig
import org.apache.log4j.Logger
import org.apache.spark.graphx.{Graph, VertexId, VertexRDD}
import org.apache.spark.rdd.RDD
import com.vesoft.nebula.tools.algorithm.utils.NebulaUtil
import org.apache.spark.graphx.lib.LabelPropagation
import org.apache.spark.sql.types.{
  BooleanType,
  DoubleType,
  IntegerType,
  LongType,
  StructField,
  StructType
}
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}

object KCoreAlgo {
  private val LOGGER = Logger.getLogger(this.getClass)

  val ALGORITHM: String = "LabelPropagation"
  def apply(spark: SparkSession, dataset: Dataset[Row], kCoreConfig: KCoreConfig): DataFrame = {

    val graph: Graph[None.type, Double] = NebulaUtil.loadInitGraph(dataset, false)
    val kCoreGraph                      = execute(graph, kCoreConfig.maxIter, kCoreConfig.degree)

    val schema = StructType(
      List(
        StructField("_id", LongType, nullable = false),
        StructField("_kcore", IntegerType, nullable = true)
      ))
    val resultRDD  = kCoreGraph.vertices.map(vertex => Row(vertex._1, vertex._2))
    val algoResult = spark.sqlContext.createDataFrame(resultRDD, schema)
    algoResult
  }

  /**
    * extract k-core sub-graph
    */
  def execute(graph: Graph[None.type, Double], maxIter: Int, k: Int): Graph[Int, Double] = {
    var lastVertexNum: Long    = graph.numVertices
    var currentVertexNum: Long = -1
    var isStable: Boolean      = false
    var iterNum: Int           = 1

    var degreeGraph = graph
      .outerJoinVertices(graph.degrees) { (vid, vd, degree) =>
        degree.getOrElse(0)
      }
      .cache
    var subGraph: Graph[Int, Double] = null

    while (iterNum < maxIter) {
      subGraph = degreeGraph.subgraph(vpred = (vid, degree) => degree > k)
      degreeGraph = subGraph
        .outerJoinVertices(subGraph.degrees) { (vid, vd, degree) =>
          degree.getOrElse(0)
        }
        .cache

      currentVertexNum = degreeGraph.numVertices
      if (currentVertexNum == lastVertexNum) {
        isStable = true;
      } else {
        lastVertexNum = currentVertexNum
      }

      iterNum += 1
    }
    subGraph
  }
}

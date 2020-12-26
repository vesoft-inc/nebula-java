/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.algorithm.lib

import com.vesoft.nebula.tools.algorithm.config.{
  CcConfig,
  Configs,
  LPAConfig,
  NebulaConfig,
  PRConfig,
  SparkConfig
}
import org.apache.log4j.Logger
import org.apache.spark.graphx.{Graph, VertexId, VertexRDD}
import org.apache.spark.rdd.RDD
import com.vesoft.nebula.tools.algorithm.utils.NebulaUtil
import org.apache.spark.graphx.lib.ConnectedComponents
import org.apache.spark.sql.types.{DoubleType, LongType, StructField, StructType}
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}

object ConnectedComponentsAlgo {
  private val LOGGER = Logger.getLogger(this.getClass)

  val ALGORITHM: String = "ConnectedComponents"

  /**
    * run the ConnectedComponents algorithm for nebula graph
    *
    * 1. get the configuration which is configured in application.conf
    * 2. read nebula edge data
    * 3. construct initial graph
    * 4. execute ConnectedComponents algorithm
    * 5. save the pagerank result
    */
  def apply(spark: SparkSession,
            dataset: Dataset[Row],
            ccConfig: CcConfig,
            hasWeight: Boolean): DataFrame = {

    val graph: Graph[None.type, Double] = NebulaUtil.loadInitGraph(dataset, hasWeight)

    val ccResultRDD = execute(graph, ccConfig.maxIter)

    val schema = StructType(
      List(
        StructField("_id", LongType, nullable = false),
        StructField("_cc", LongType, nullable = true)
      ))
    val algoResult = spark.sqlContext
      .createDataFrame(ccResultRDD, schema)

    algoResult
  }

  def execute(graph: Graph[None.type, Double], maxIter: Int): RDD[Row] = {
    val ccResultRDD: VertexRDD[VertexId] = ConnectedComponents.run(graph, maxIter).vertices
    ccResultRDD.map(row => Row(row._1, row._2))
  }
}

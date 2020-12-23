/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.algorithm.lib

import com.vesoft.nebula.tools.algorithm.config.{
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
import org.apache.spark.graphx.lib.LabelPropagation
import org.apache.spark.sql.types.{DoubleType, LongType, StructField, StructType}
import org.apache.spark.sql.{DataFrame, Dataset, Row}

object LabelPropagationAlgo {
  private val LOGGER = Logger.getLogger(this.getClass)

  val ALGORITHM: String = "LabelPropagation"

  /**
    * run the LabelPropagation algorithm for nebula graph
    *
    * 1. get the configuration which is configured in application.conf
    * 2. read nebula edge data
    * 3. construct initial graph
    * 4. execute LabelPropagation algorithm
    * 5. save the pagerank result
    */
  def apply(configs: Configs): DataFrame = {

    val sparkConfig  = SparkConfig.getSpark(configs, ALGORITHM)
    val nebulaConfig = NebulaConfig.getNebula(configs)
    val lpaConfig    = LPAConfig.getLPAConfig(configs)

    val dataSet: Dataset[Row] = {
      NebulaUtil.scanEdgeData(
        sparkConfig.spark,
        sparkConfig.partitionNum,
        nebulaConfig.hostPorts,
        nebulaConfig.partitionNumber,
        nebulaConfig.nameSpace,
        nebulaConfig.labels,
        nebulaConfig.hasWeight,
        nebulaConfig.weightCols
      )
    }

    val resultPath                      = NebulaUtil.getResultPath(lpaConfig.lpaPath, ALGORITHM)
    val graph: Graph[None.type, Double] = NebulaUtil.loadInitGraph(dataSet, nebulaConfig.hasWeight)

    val lpaResultRDD = execute(graph, lpaConfig.maxIter)

    val schema = StructType(
      List(
        StructField("_id", LongType, nullable = false),
        StructField("_lpa", LongType, nullable = true)
      ))
    val algoResult = sparkConfig.spark.sqlContext
      .createDataFrame(lpaResultRDD, schema)

    algoResult.write.csv(resultPath)
    algoResult
  }

  def execute(graph: Graph[None.type, Double], maxIter: Int): RDD[Row] = {
    val lpaResultRDD: VertexRDD[VertexId] = LabelPropagation.run(graph, maxIter).vertices
    lpaResultRDD.map(row => Row(row._1, row._2))
  }
}

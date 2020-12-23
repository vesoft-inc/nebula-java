/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.algorithm.lib

import com.vesoft.nebula.tools.algorithm.config.{
  Configs,
  NebulaConfig,
  PRConfig,
  ShortestPathConfig,
  SparkConfig
}
import org.apache.log4j.Logger
import org.apache.spark.graphx.{Graph, VertexId, VertexRDD}
import org.apache.spark.rdd.RDD
import com.vesoft.nebula.tools.algorithm.utils.NebulaUtil
import org.apache.spark.graphx.lib.ShortestPaths
import org.apache.spark.graphx.lib.ShortestPaths.SPMap
import org.apache.spark.sql.types.{LongType, StringType, StructField, StructType}
import org.apache.spark.sql.{DataFrame, Dataset, Row}

object ShortestPathAlgo {
  private val LOGGER = Logger.getLogger(this.getClass)

  val ALGORITHM: String = "ShortestPath"

  /**
    * run the ShortestPath algorithm for nebula graph
    *
    * 1. get the configuration which is configured in application.conf
    * 2. read nebula edge data
    * 3. construct initial graph
    * 4. execute pagerank algorithm
    * 5. save the pagerank result
    */
  def apply(configs: Configs): DataFrame = {

    val sparkConfig        = SparkConfig.getSpark(configs, ALGORITHM)
    val nebulaConfig       = NebulaConfig.getNebula(configs)
    val shortestPathConfig = ShortestPathConfig.getShortestPathConfig(configs)

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

    val resultPath                      = NebulaUtil.getResultPath(shortestPathConfig.spPath, ALGORITHM)
    val graph: Graph[None.type, Double] = NebulaUtil.loadInitGraph(dataSet, nebulaConfig.hasWeight)

    val prResultRDD = execute(graph, shortestPathConfig.landmarks)

    val schema = StructType(
      List(
        StructField("_id", LongType, nullable = false),
        StructField("_pagerank", StringType, nullable = true)
      ))
    val algoResult = sparkConfig.spark.sqlContext
      .createDataFrame(prResultRDD, schema)

    algoResult.write.csv(resultPath)
    algoResult
  }

  def execute(graph: Graph[None.type, Double], landmarks: Seq[VertexId]): RDD[Row] = {
    val spResultRDD: VertexRDD[SPMap] = ShortestPaths.run(graph, landmarks).vertices
    spResultRDD.map(row => Row(row._1, row._2.toString()))
  }
}

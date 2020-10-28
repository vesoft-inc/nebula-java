/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.algorithm.config

object ConfigSuite {

  def configMock(): Configs = {
    val address         = "127.0.0.1:45500"
    val space           = "nb"
    val partitionNumber = "100"
    val labels          = List("serve", "follow")
    val hasWeight       = true
    val weightCols      = List("start_year", "degree")

    val nebulaConfigEntry: NebulaConfigEntry =
      NebulaConfigEntry(address, space, partitionNumber, labels, hasWeight, weightCols)

    val sparkMap: Map[String, String] =
      Map("spark.app.name" -> "test", "spark.app.partitionNum" -> "12", "spark.master" -> "local")
    val sparkConfig = SparkConfigEntry(sparkMap)

    val algoMap: Map[String, String] = Map("algorithm.path" -> "/tmp",
                                           "algorithm.executeAlgo"      -> "pagerank",
                                           "algorithm.pagerank.maxIter" -> "10")
    val algorithmConfigEntry = AlgorithmConfigEntry(algoMap)

    Configs(nebulaConfigEntry, sparkConfig, algorithmConfigEntry)
  }
}

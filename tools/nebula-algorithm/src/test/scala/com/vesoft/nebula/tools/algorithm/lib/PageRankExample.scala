/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.algorithm.lib

import com.vesoft.nebula.tools.algorithm.config.{ConfigSuite, Configs, PRConfig, SparkConfig}
import org.apache.spark.sql.DataFrame

object PageRankExample {
  def main(args: Array[String]): Unit = {
    val configs: Configs = ConfigSuite.configMock()
    val spark            = SparkConfig.getSpark(configs).spark

    // construct your dataset
    val dataSet: DataFrame = null

    val pageRankAlgoConfig = PRConfig.getPRConfig(configs)
    // print the result
    PageRankAlgo.apply(spark, dataSet, pageRankAlgoConfig, false).show(10)
  }
}

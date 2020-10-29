/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.algorithm.config

import org.apache.spark.sql.SparkSession

case class SparkConfig(spark: SparkSession, partitionNum: String)

object SparkConfig {

  var spark: SparkSession = _

  var partitionNum: String = _

  def getSpark(configs: Configs, defaultAppName: String): SparkConfig = {
    val sparkConfigs = configs.sparkConfig.map
    val appName      = sparkConfigs.getOrElse("spark.app.name", defaultAppName)
    val master       = sparkConfigs.getOrElse("spark.master", "local")
    val session      = SparkSession.builder.appName(appName).master(master).getOrCreate()

    for (sparkConfig <- sparkConfigs) {
      if (sparkConfig._1.startsWith("spark.conf")) {
        sparkConfig._1 match {
          case "spark.conf.driver-memory" => session.conf.set("spark.driver.memory", sparkConfig._2)
          case "spark.conf.executor-memory" =>
            session.conf.set("spark.executor.memory", sparkConfig._2)
          case "spark.conf.executor-cores" =>
            session.conf.set("spark.executor.cores", sparkConfig._2)
          case "spark.conf.cores-max" => session.conf.set("spark.cores.max", sparkConfig._2)
          case _                      =>
          // todo add more spark configs
        }
      }
    }
    session.conf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")

    partitionNum = sparkConfigs.getOrElse("spark.app.partitionNum", "0")

    SparkConfig(session, partitionNum)
  }
}

/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.importer.test.mock

import com.vesoft.nebula.tools.importer.{
  Configs,
  ConnectionConfigEntry,
  DataBaseConfigEntry,
  ErrorConfigEntry,
  ExecutionConfigEntry,
  RateConfigEntry,
  SparkConfigEntry,
  UserConfigEntry
}

import scala.util.Random

object MockConfigs {

  val port: Int = 9000 + Random.nextInt(1000)

  val dataBaseConfig: DataBaseConfigEntry = DataBaseConfigEntry(List("127.0.0.1:" + port), "test")

  val userConfig: UserConfigEntry = UserConfigEntry("user", "password")

  val connectionConfig: ConnectionConfigEntry = ConnectionConfigEntry(1, 1)

  val executionConfig: ExecutionConfigEntry = ExecutionConfigEntry(1, 1, 1)

  val errorConfig: ErrorConfigEntry = ErrorConfigEntry("/tmp/", 100)

  val rateConfig: RateConfigEntry = RateConfigEntry(100, 100)

  val sparkConfigEntry: SparkConfigEntry = SparkConfigEntry(Map[String, String]())

  val tagsConfig = List(new MockGraphDataVertex().tagConfig)

  val edgesConfig = List(new MockGraphDataEdge().edgeConfig)

  val configs: Configs = Configs(
    dataBaseConfig,
    userConfig,
    connectionConfig,
    executionConfig,
    errorConfig,
    rateConfig,
    sparkConfigEntry,
    tagsConfig,
    edgesConfig
  )

}

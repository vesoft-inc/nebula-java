package com.vesoft.nebula.tools.importer.test.utils

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

object MockConfigs {

  val port = 9999

  val dataBaseConfig: DataBaseConfigEntry = DataBaseConfigEntry(List("127.0.0.1:" + port), "test")

  val userConfig: UserConfigEntry = UserConfigEntry("user", "password")

  val connectionConfig: ConnectionConfigEntry = ConnectionConfigEntry(1000, 3)

  val executionConfig: ExecutionConfigEntry = ExecutionConfigEntry(1000, 3, 100)

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
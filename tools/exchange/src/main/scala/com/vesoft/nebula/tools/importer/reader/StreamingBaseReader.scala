/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.importer.reader

import com.vesoft.nebula.tools.importer.config.{
  KafkaSourceConfigEntry,
  PulsarSourceConfigEntry
}
import org.apache.spark.sql.{DataFrame, SparkSession}

/**
  * Spark Streaming
  *
  * @param session
  */
abstract class StreamingBaseReader(override val session: SparkSession) extends Reader {

  override def close(): Unit = {
    session.close()
  }
}

/**
  *
  * @param session
  * @param kafkaConfig
  */
class KafkaReader(override val session: SparkSession, kafkaConfig: KafkaSourceConfigEntry)
    extends StreamingBaseReader(session) {

  require(kafkaConfig.server.trim.nonEmpty && kafkaConfig.topic.trim.nonEmpty)

  override def read(): DataFrame = {
    session.readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", kafkaConfig.server)
      .option("subscribe", kafkaConfig.topic)
      .load()
  }
}

/**
  *
  * @param session
  * @param pulsarConfig
  */
class PulsarReader(override val session: SparkSession, pulsarConfig: PulsarSourceConfigEntry)
    extends StreamingBaseReader(session) {

  override def read(): DataFrame = {
    session.readStream
      .format("pulsar")
      .option("service.url", pulsarConfig.serviceUrl)
      .option("admin.url", pulsarConfig.adminUrl)
      .options(pulsarConfig.options)
      .load()
  }
}

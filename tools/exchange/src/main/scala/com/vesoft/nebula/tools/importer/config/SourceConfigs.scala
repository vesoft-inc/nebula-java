/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.importer.config

/**
  * Category use to explain the data source which the Spark application could reading.
  */
object SourceCategory extends Enumeration {
  type Type = Value

  val PARQUET = Value("PARQUET")
  val ORC     = Value("ORC")
  val JSON    = Value("JSON")
  val CSV     = Value("CSV")
  val TEXT    = Value("TEXT")

  val HIVE        = Value("HIVE")
  val NEO4J       = Value("NEO4J")
  val JANUS_GRAPH = Value("JANUS GRAPH")
  val MYSQL       = Value("MYSQL")

  val SOCKET = Value("SOCKET")
  val KAFKA  = Value("KAFKA")
  val PULSAR = Value("PULSAR")
}

class SourceCategory

/**
  * DataSourceConfigEntry
  */
sealed trait DataSourceConfigEntry {
  def category: SourceCategory.Value
}

sealed trait StreamingDataSourceConfigEntry extends DataSourceConfigEntry {
  def intervalSeconds: Int
}

/**
  * FileBaseSourceConfigEntry
  *
  * @param path
  */
case class FileBaseSourceConfigEntry(override val category: SourceCategory.Value, path: String)
    extends DataSourceConfigEntry {
  override def toString: String = {
    s"File source path: ${path}"
  }
}

/**
  *
  * @param category
  * @param path
  * @param separator
  * @param header
  */
case class CSVSourceConfigEntry(override val category: SourceCategory.Value,
                                path: String,
                                separator: String,
                                header: Boolean)
    extends DataSourceConfigEntry {
  override def toString: String = {
    s"CSV source path: ${path}, separator: ${separator}"
  }
}

/**
  * HiveSourceConfigEntry
  *
  * @param exec
  */
case class HiveSourceConfigEntry(override val category: SourceCategory.Value, exec: String)
    extends DataSourceConfigEntry {
  require(exec.trim.nonEmpty)

  override def toString: String = {
    s"Hive source exec: ${exec}"
  }
}

/**
  *
  * @param exec
  */
case class Neo4JSourceConfigEntry(override val category: SourceCategory.Value,
                                  name: String,
                                  exec: String,
                                  server: String,
                                  user: String,
                                  password: String,
                                  database: Option[String],
                                  encryption: Boolean,
                                  parallel: Int,
                                  checkPointPath: Option[String])
    extends DataSourceConfigEntry {
  require(exec.trim.length != 0 && user.trim.length != 0 && parallel > 0)

  override def toString: String = {
    s"Neo4J source address: ${server}, user: ${user}, password: ${password}, encryption: ${encryption}," +
      s" checkPointPath: ${checkPointPath}, exec: ${exec}, parallel: ${parallel}, database: ${database}"
  }
}

case class JanusGraphSourceConfigEntry(override val category: SourceCategory.Value)
    extends DataSourceConfigEntry {
  override def toString: String = {
    s"Janus graph source"
  }
}

case class MySQLSourceConfigEntry(override val category: SourceCategory.Value,
                                  host: String,
                                  port: Int,
                                  database: String,
                                  table: String,
                                  user: String,
                                  password: String,
                                  sentence: String)
    extends DataSourceConfigEntry {
  require(
    host.trim.length != 0 && port > 0 && database.trim.length > 0 && table.trim.length > 0 && user.trim.length > 0)

  override def toString: String = {
    s"MySql source host: ${host}, port: ${port}, database: ${database}, table: ${table}, " +
      s"user: ${user}, password: ${password}, sentence: ${sentence}"
  }
}

/**
  *
  * @param host
  * @param port
  */
case class SocketSourceConfigEntry(override val category: SourceCategory.Value,
                                   host: String,
                                   port: Int)
    extends DataSourceConfigEntry {
  require(host.trim.nonEmpty && port > 0)

  override def toString: String = {
    s"Socket source address: ${host}:${port}"
  }
}

/**
  * TODO: Support more config item about Kafka Consumer
  *
  * @param server
  * @param topic
  */
case class KafkaSourceConfigEntry(override val category: SourceCategory.Value,
                                  server: String,
                                  topic: String)
    extends DataSourceConfigEntry {
  require(server.trim.nonEmpty && topic.trim.nonEmpty)

  override def toString: String = {
    s"Kafka source server: ${server} topic:${topic}"
  }
}

case class PulsarSourceConfigEntry(override val category: SourceCategory.Value,
                                   override val intervalSeconds: Int,
                                   serviceUrl: String,
                                   adminUrl: String,
                                   options: Map[String, String])
    extends StreamingDataSourceConfigEntry {
  require(serviceUrl.trim.nonEmpty && adminUrl.trim.nonEmpty && intervalSeconds >= 0)
  require(options.keys.count(key => List("topic", "topics", "topicsPattern").contains(key)) == 1)

  override def toString: String = {
    s"Pulsar source service url: ${serviceUrl} admin url: ${adminUrl} options: ${options}"
  }
}

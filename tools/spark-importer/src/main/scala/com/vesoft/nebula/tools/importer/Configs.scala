/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.importer

import java.io.File
import java.nio.file.Files

import scala.collection.JavaConverters._
import com.typesafe.config.{Config, ConfigFactory, ConfigValue}
import org.apache.log4j.Logger

import scala.collection.mutable
import util.control.Breaks._

object Type extends Enumeration {
  type Type = Value
  val VERTEX = Value("VERTEX")
  val EDGE   = Value("EDGE")
}

/**
  * DataBaseConfigEntry describe the nebula cluster's address and which space will be used.
  *
  * @param addresses
  * @param space
  */
case class DataBaseConfigEntry(addresses: List[String], space: String) {
  require(addresses != null && addresses.size != 0)
  require(space.trim.size != 0)

  override def toString: String = super.toString
}

/**
  * UserConfigEntry is used when the client login the nebula graph service.
  *
  * @param user
  * @param password
  */
case class UserConfigEntry(user: String, password: String) {
  require(user.trim.size != 0 && password.trim.size != 0)

  override def toString: String = super.toString
}

/**
  * ConnectionConfigEntry
  *
  * @param timeout
  * @param retry
  */
case class ConnectionConfigEntry(timeout: Int, retry: Int) {
  require(timeout > 0 && retry > 0)

  override def toString: String = super.toString
}

/**
  * ExecutionConfigEntry
  *
  * @param timeout
  * @param retry
  * @param interval
  */
case class ExecutionConfigEntry(timeout: Int, retry: Int, interval: Int) {
  require(timeout > 0 && retry > 0 && interval > 0)

  override def toString: String = super.toString
}

/**
  * ErrorConfigEntry
  *
  * @param errorPath
  * @param errorMaxSize
  */
case class ErrorConfigEntry(errorPath: String, errorMaxSize: Int) {
  require(errorPath.trim.size != 0 && errorMaxSize > 0)

  override def toString: String = super.toString
}

/**
  * RateConfigEntry
  *
  * @param limit
  * @param timeout
  */
case class RateConfigEntry(limit: Int, timeout: Int) {
  require(limit > 0 && timeout > 0)

  override def toString: String = super.toString
}

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

  val SOCKET = Value("SOCKET")
  val KAFKA  = Value("KAFKA")
}

class SourceCategory

/**
  *
  */
object SinkCategory extends Enumeration {
  type Type = Value

  val CLIENT = Value("CLIENT")
}

class SinkCategory

/**
  * DataSourceConfigEntry
  */
sealed trait DataSourceConfigEntry {
  def category: SourceCategory.Value
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
  require(exec.trim.size != 0)

  override def toString: String = {
    s"Hive source exec: ${exec}"
  }
}

/**
  *
  * @param exec
  * @param offset
  */
case class Neo4JSourceConfigEntry(override val category: SourceCategory.Value,
                                  exec: String,
                                  server: String,
                                  user: String,
                                  password: String,
                                  encryption: Boolean,
                                  offset: Long)
    extends DataSourceConfigEntry {
  require(
    exec.trim.length != 0 && offset >= 0 && user.trim.length != 0 && password.trim.length != 0)

  override def toString: String = {
    s"Neo4J source address: ${server}, user: ${user}, password: ${password}, encryption: ${encryption}," +
      s" offset: ${offset}, exec: ${exec}"
  }
}

case class JanusGraphSourceConfigEntry(override val category: SourceCategory.Value)
    extends DataSourceConfigEntry {
  override def toString: String = {
    s"Neo4J source"
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
  require(host.trim.size != 0 && port > 0)

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
  require(server.trim.size != 0 && topic.trim.size != 0)

  override def toString: String = {
    s"Kafka source server: ${server} topic:${topic}"
  }
}

/**
  * DataSinkConfigEntry
  */
sealed trait DataSinkConfigEntry {
  def category: SinkCategory.Value
}

/**
  * FileBaseSinkConfigEntry
  *
  * @param path
  */
case class FileBaseSinkConfigEntry(override val category: SinkCategory.Value, path: String)
    extends DataSinkConfigEntry {
  override def toString: String = {
    s"File sink path: ${path}"
  }
}

/**
  * NebulaSinkConfigEntry use to specified the nebula service's address.
  */
case class NebulaSinkConfigEntry(override val category: SinkCategory.Value, addresses: List[String])
    extends DataSinkConfigEntry {
  override def toString: String = {
    s"Nebula sink addresses: ${addresses.mkString("[", ", ", "]")}"
  }
}

/**
  * SchemaConfigEntry
  */
sealed trait SchemaConfigEntry {
  def name: String

  def dataSourceConfigEntry: DataSourceConfigEntry

  def dataSinkConfigEntry: DataSinkConfigEntry

  def fields: Map[String, ConfigValue]

  def batch: Int

  def partition: Int

  def checkPointPath: Option[String]
}

/**
  *
  * @param name
  * @param dataSourceConfigEntry
  * @param dataSinkConfigEntry
  * @param fields
  * @param vertexField
  * @param vertexPolicy
  * @param batch
  * @param partition
  * @param checkPointPath
  */
case class TagConfigEntry(override val name: String,
                          override val dataSourceConfigEntry: DataSourceConfigEntry,
                          override val dataSinkConfigEntry: DataSinkConfigEntry,
                          override val fields: Map[String, ConfigValue],
                          vertexField: String,
                          vertexPolicy: Option[KeyPolicy.Value],
                          override val batch: Int,
                          override val partition: Int,
                          override val checkPointPath: Option[String])
    extends SchemaConfigEntry {
  require(name.trim.size != 0 && vertexField.trim.size != 0 && batch > 0)

  override def toString: String = {
    s"Tag name ${name} " +
      s"source ${dataSourceConfigEntry} " +
      s"sink ${dataSinkConfigEntry} " +
      s"vertex field ${vertexField} " +
      s"vertex policy ${vertexPolicy} " +
      s"batch ${batch} " +
      s"partition ${partition}"
  }
}

/**
  *
  * @param name
  * @param dataSourceConfigEntry
  * @param dataSinkConfigEntry
  * @param fields
  * @param sourceField
  * @param sourcePolicy
  * @param rankingField
  * @param targetField
  * @param targetPolicy
  * @param isGeo
  * @param latitude
  * @param longitude
  * @param batch
  * @param partition
  * @param checkPointPath
  */
case class EdgeConfigEntry(override val name: String,
                           override val dataSourceConfigEntry: DataSourceConfigEntry,
                           override val dataSinkConfigEntry: DataSinkConfigEntry,
                           override val fields: Map[String, ConfigValue],
                           sourceField: String,
                           sourcePolicy: Option[KeyPolicy.Value],
                           rankingField: Option[String],
                           targetField: String,
                           targetPolicy: Option[KeyPolicy.Value],
                           isGeo: Boolean,
                           latitude: Option[String],
                           longitude: Option[String],
                           override val batch: Int,
                           override val partition: Int,
                           override val checkPointPath: Option[String])
    extends SchemaConfigEntry {
  require(
    name.trim.size != 0 && sourceField.trim.size != 0 &&
      targetField.trim.size != 0 && batch > 0)

  override def toString: String = {
    if (isGeo) {
      s"Edge name ${name} " +
        s"source ${dataSourceConfigEntry} " +
        s"sink ${dataSinkConfigEntry} " +
        s"latitude ${latitude} " +
        s"longitude ${longitude} " +
        s"source field ${sourceField} " +
        s"source policy ${sourcePolicy} " +
        s"ranking ${rankingField} " +
        s"target field ${targetField} " +
        s"target policy ${targetPolicy} " +
        s"batch ${batch} " +
        s"partition ${partition}"
    } else {
      s"Edge name ${name} " +
        s"source ${dataSourceConfigEntry} " +
        s"sink ${dataSinkConfigEntry} " +
        s"source field ${sourceField} " +
        s"source policy ${sourcePolicy} " +
        s"ranking ${rankingField} " +
        s"target field ${targetField} " +
        s"target policy ${targetPolicy} " +
        s"batch ${batch} " +
        s"partition ${partition}"
    }
  }
}

/**
  *
  */
object SparkConfigEntry {
  def apply(config: Config): SparkConfigEntry = {
    val map         = mutable.Map[String, String]()
    val sparkConfig = config.getObject("spark")
    for (key <- sparkConfig.unwrapped().keySet().asScala) {
      val sparkKey = s"spark.${key}"
      if (config.getAnyRef(sparkKey).isInstanceOf[String]) {
        val sparkValue = config.getString(sparkKey)
        map += sparkKey -> sparkValue
      } else {
        for (subKey <- config.getObject(sparkKey).unwrapped().keySet().asScala) {
          val key        = s"${sparkKey}.${subKey}"
          val sparkValue = config.getString(key)
          map += key -> sparkValue
        }
      }
    }
    SparkConfigEntry(map.toMap)
  }
}

/**
  * SparkConfigEntry support key-value pairs for spark session.
  *
  * @param map
  */
case class SparkConfigEntry(map: Map[String, String]) {
  override def toString: String = {
    ""
  }
}

/**
  * Configs
  *
  * @param databaseConfig
  * @param userConfig
  * @param connectionConfig
  * @param executionConfig
  * @param errorConfig
  * @param rateConfig
  * @param sparkConfigEntry
  * @param tagsConfig
  * @param edgesConfig
  */
case class Configs(databaseConfig: DataBaseConfigEntry,
                   userConfig: UserConfigEntry,
                   connectionConfig: ConnectionConfigEntry,
                   executionConfig: ExecutionConfigEntry,
                   errorConfig: ErrorConfigEntry,
                   rateConfig: RateConfigEntry,
                   sparkConfigEntry: SparkConfigEntry,
                   tagsConfig: List[TagConfigEntry],
                   edgesConfig: List[EdgeConfigEntry])

object Configs {
  private[this] val LOG = Logger.getLogger(this.getClass)

  private[this] val DEFAULT_CONNECTION_TIMEOUT   = 3000
  private[this] val DEFAULT_CONNECTION_RETRY     = 3
  private[this] val DEFAULT_EXECUTION_RETRY      = 3
  private[this] val DEFAULT_EXECUTION_TIMEOUT    = 3000
  private[this] val DEFAULT_EXECUTION_INTERVAL   = 3000
  private[this] val DEFAULT_ERROR_OUTPUT_PATH    = "/tmp/nebula.writer.errors/"
  private[this] val DEFAULT_ERROR_MAX_BATCH_SIZE = Int.MaxValue
  private[this] val DEFAULT_RATE_LIMIT           = 1024
  private[this] val DEFAULT_RATE_TIMEOUT         = 100
  private[this] val DEFAULT_EDGE_RANKING         = 0L
  private[this] val DEFAULT_BATCH                = 2
  private[this] val DEFAULT_PARTITION            = -1
  private[this] val DEFAULT_CHECK_POINT_PATH     = None

  /**
    *
    * @param configPath
    * @return
    */
  def parse(configPath: File): Configs = {
    if (!Files.exists(configPath.toPath)) {
      throw new IllegalArgumentException(s"${configPath} not exist")
    }

    val config        = ConfigFactory.parseFile(configPath)
    val nebulaConfig  = config.getConfig("nebula")
    val addresses     = nebulaConfig.getStringList("addresses").asScala.toList
    val space         = nebulaConfig.getString("space")
    val databaseEntry = DataBaseConfigEntry(addresses, space)
    LOG.info(s"DataBase Config ${databaseEntry}")

    val user      = nebulaConfig.getString("user")
    val pswd      = nebulaConfig.getString("pswd")
    val userEntry = UserConfigEntry(user, pswd)
    LOG.info(s"User Config ${userEntry}")

    val connectionConfig  = getConfigOrNone(nebulaConfig, "connection")
    val connectionTimeout = getOptOrElse(connectionConfig, "timeout", DEFAULT_CONNECTION_TIMEOUT)
    val connectionRetry   = getOptOrElse(connectionConfig, "retry", DEFAULT_CONNECTION_RETRY)
    val connectionEntry   = ConnectionConfigEntry(connectionTimeout, connectionRetry)
    LOG.info(s"Connection Config ${connectionConfig}")

    val executionConfig   = getConfigOrNone(nebulaConfig, "execution")
    val executionTimeout  = getOptOrElse(executionConfig, "timeout", DEFAULT_EXECUTION_TIMEOUT)
    val executionRetry    = getOptOrElse(executionConfig, "retry", DEFAULT_EXECUTION_RETRY)
    val executionInterval = getOptOrElse(executionConfig, "interval", DEFAULT_EXECUTION_INTERVAL)
    val executionEntry    = ExecutionConfigEntry(executionTimeout, executionRetry, executionInterval)
    LOG.info(s"Execution Config ${executionEntry}")

    val errorConfig  = getConfigOrNone(nebulaConfig, "error")
    val errorPath    = getOptOrElse(errorConfig, "output", DEFAULT_ERROR_OUTPUT_PATH)
    val errorMaxSize = getOptOrElse(errorConfig, "max", DEFAULT_ERROR_MAX_BATCH_SIZE)
    val errorEntry   = ErrorConfigEntry(errorPath, errorMaxSize)

    val rateConfig  = getConfigOrNone(nebulaConfig, "rate")
    val rateLimit   = getOptOrElse(rateConfig, "limit", DEFAULT_RATE_LIMIT)
    val rateTimeout = getOptOrElse(rateConfig, "timeout", DEFAULT_RATE_TIMEOUT)
    val rateEntry   = RateConfigEntry(rateLimit, rateTimeout)

    val sparkEntry = SparkConfigEntry(config)

    val tags       = mutable.ListBuffer[TagConfigEntry]()
    val tagConfigs = getConfigsOrNone(config, "tags")
    if (!tagConfigs.isEmpty) {
      for (tagConfig <- tagConfigs.get.asScala) {
        if (!tagConfig.hasPath("name") ||
            !tagConfig.hasPath("source.type") ||
            !tagConfig.hasPath("sink.type")) {
          LOG.error("The `name` and `type` must be specified")
          break()
        }

        val tagName = tagConfig.getString("name")
        val fields  = tagConfig.getObject("fields").asScala

        // You can specified the vertex field name via the config item `vertex`
        // If you want to qualified the key policy, you can wrap them into a block.
        val vertexField = if (tagConfig.hasPath("vertex.field")) {
          tagConfig.getString("vertex.field")
        } else {
          tagConfig.getString("vertex")
        }

        val policyOpt = if (tagConfig.hasPath("vertex.policy")) {
          val policy = tagConfig.getString("vertex.policy").toLowerCase
          Some(KeyPolicy.withName(policy))
        } else {
          None
        }

        val sourceCategory = toSourceCategory(tagConfig.getString("source.type"))
        val sourceConfig   = dataSourceConfig(sourceCategory, tagConfig)
        LOG.info(s"Source Config ${sourceConfig}")

        val sinkCategory = toSinkCategory(tagConfig.getString("sink.type"))
        val sinkConfig   = dataSinkConfig(sinkCategory, nebulaConfig)
        LOG.info(s"Sink Config ${sourceConfig}")

        val batch          = getOrElse(tagConfig, "batch", DEFAULT_BATCH)
        val partition      = getOrElse(tagConfig, "partition", DEFAULT_PARTITION)
        val checkPointPath = getOrElse(tagConfig, "check_point", DEFAULT_CHECK_POINT_PATH)
        LOG.info(s"name ${tagName}  batch ${batch}")
        val entry = TagConfigEntry(tagName,
                                   sourceConfig,
                                   sinkConfig,
                                   fields.toMap,
                                   vertexField,
                                   policyOpt,
                                   batch,
                                   partition,
                                   checkPointPath)
        LOG.info(s"Tag Config: ${entry}")
        tags += entry
      }
    }

    val edges       = mutable.ListBuffer[EdgeConfigEntry]()
    val edgeConfigs = getConfigsOrNone(config, "edges")
    if (!edgeConfigs.isEmpty) {
      for (edgeConfig <- edgeConfigs.get.asScala) {
        if (!edgeConfig.hasPath("name") ||
            !edgeConfig.hasPath("source.type") ||
            !edgeConfig.hasPath("sink.type")) {
          LOG.error("The `name` and `type`must be specified")
          break()
        }

        val edgeName = edgeConfig.getString("name")
        val fields   = edgeConfig.getObject("fields").asScala
        val isGeo = !edgeConfig.hasPath("source") &&
          edgeConfig.hasPath("latitude") &&
          edgeConfig.hasPath("longitude")

        val sourceCategory = toSourceCategory(edgeConfig.getString("source.type"))
        val sourceConfig   = dataSourceConfig(sourceCategory, edgeConfig)
        LOG.info(s"Source Config ${sourceConfig}")

        val sinkCategory = toSinkCategory(edgeConfig.getString("sink.type"))
        val sinkConfig   = dataSinkConfig(sinkCategory, nebulaConfig)
        LOG.info(s"Sink Config ${sourceConfig}")

        val sourceField = if (!isGeo) {
          if (edgeConfig.hasPath("source.field")) {
            edgeConfig.getString("source.field")
          } else {
            edgeConfig.getString("source")
          }
        } else {
          throw new IllegalArgumentException("Source must be specified")
        }

        val sourcePolicy = if (!isGeo) {
          if (edgeConfig.hasPath("source.policy")) {
            val policy = edgeConfig.getString("source.policy").toLowerCase
            Some(KeyPolicy.withName(policy))
          } else {
            None
          }
        } else {
          None
        }

        val targetField: String = if (edgeConfig.hasPath("target.field")) {
          edgeConfig.getString("target.field")
        } else {
          edgeConfig.getString("target")
        }

        val targetPolicy = if (edgeConfig.hasPath("target.policy")) {
          val policy = edgeConfig.getString("target.policy").toLowerCase
          Some(KeyPolicy.withName(policy))
        } else {
          None
        }

        val ranking = if (edgeConfig.hasPath("ranking")) {
          Some(edgeConfig.getString("ranking"))
        } else {
          None
        }

        val latitude = if (isGeo) {
          Some(edgeConfig.getString("latitude"))
        } else {
          None
        }

        val longitude = if (isGeo) {
          Some(edgeConfig.getString("longitude"))
        } else {
          None
        }

        val batch          = getOrElse(edgeConfig, "batch", DEFAULT_BATCH)
        val partition      = getOrElse(edgeConfig, "partition", DEFAULT_PARTITION)
        val saveCheckPoint = getOrElse(edgeConfig, "save.check_point", DEFAULT_CHECK_POINT_PATH)
        val entry = EdgeConfigEntry(
          edgeName,
          sourceConfig,
          sinkConfig,
          fields.toMap,
          sourceField,
          sourcePolicy,
          ranking,
          targetField,
          targetPolicy,
          isGeo,
          latitude,
          longitude,
          batch,
          partition,
          saveCheckPoint
        )
        LOG.info(s"Edge Config: ${entry}")
        edges += entry
      }
    }

    Configs(databaseEntry,
            userEntry,
            connectionEntry,
            executionEntry,
            errorEntry,
            rateEntry,
            sparkEntry,
            tags.toList,
            edges.toList)
  }

  private[this] def toSourceCategory(category: String): SourceCategory.Value = {
    category.trim.toUpperCase match {
      case "PARQUET" => SourceCategory.PARQUET
      case "ORC"     => SourceCategory.ORC
      case "JSON"    => SourceCategory.JSON
      case "CSV"     => SourceCategory.CSV
      case "HIVE"    => SourceCategory.HIVE
      case "NEO4J"   => SourceCategory.NEO4J
      case "SOCKET"  => SourceCategory.SOCKET
      case "KAFKA"   => SourceCategory.KAFKA
      case _         => throw new IllegalArgumentException(s"${category} not support")
    }
  }

  private[this] def toSinkCategory(category: String): SinkCategory.Value = {
    category.trim.toUpperCase match {
      case "CLIENT" =>
        SinkCategory.CLIENT
      case _ =>
        throw new IllegalArgumentException(s"${category} not support")
    }
  }

  private[this] def dataSourceConfig(category: SourceCategory.Value,
                                     config: Config): DataSourceConfigEntry = {
    category match {
      case SourceCategory.PARQUET =>
        FileBaseSourceConfigEntry(SourceCategory.PARQUET, config.getString("path"))
      case SourceCategory.ORC =>
        FileBaseSourceConfigEntry(SourceCategory.ORC, config.getString("path"))
      case SourceCategory.JSON =>
        FileBaseSourceConfigEntry(SourceCategory.JSON, config.getString("path"))
      case SourceCategory.CSV =>
        val separator =
          if (config.hasPath("separator"))
            config.getString("separator")
          else ","
        val header =
          if (config.hasPath("header"))
            config.getBoolean("header")
          else
            false
        CSVSourceConfigEntry(SourceCategory.CSV, config.getString("path"), separator, header)
      case SourceCategory.HIVE =>
        HiveSourceConfigEntry(SourceCategory.HIVE, config.getString("exec"))
      case SourceCategory.NEO4J =>
        val offset = if (config.hasPath("offset")) config.getLong("offset") else 0L
        val encryption =
          if (config.hasPath("encryption")) config.getBoolean("encryption") else false
        Neo4JSourceConfigEntry(SourceCategory.NEO4J,
                               config.getString("exec"),
                               config.getString("server"),
                               config.getString("user"),
                               config.getString("password"),
                               encryption,
                               offset)
      case SourceCategory.JANUS_GRAPH =>
        JanusGraphSourceConfigEntry(SourceCategory.JANUS_GRAPH)
      case SourceCategory.SOCKET =>
        SocketSourceConfigEntry(SourceCategory.SOCKET,
                                config.getString("host"),
                                config.getInt("port"))
      case SourceCategory.KAFKA =>
        KafkaSourceConfigEntry(SourceCategory.KAFKA,
                               config.getString("service"),
                               config.getString("topic"))
      case _ =>
        throw new IllegalArgumentException("")
    }
  }

  private[this] def dataSinkConfig(category: SinkCategory.Value,
                                   nebulaConfig: Config): DataSinkConfigEntry = {
    category match {
      case SinkCategory.CLIENT =>
        NebulaSinkConfigEntry(SinkCategory.CLIENT,
                              nebulaConfig.getStringList("addresses").asScala.toList)
      case _ =>
        throw new IllegalArgumentException("Not Support")
    }
  }

  /**
    * Get the config list by the path.
    *
    * @param config The config.
    * @param path   The path of the config.
    * @return
    */
  private[this] def getConfigsOrNone(config: Config,
                                     path: String): Option[java.util.List[_ <: Config]] = {
    if (config.hasPath(path)) {
      Some(config.getConfigList(path))
    } else {
      None
    }
  }

  /**
    * Get the config by the path.
    *
    * @param config
    * @param path
    * @return
    */
  def getConfigOrNone(config: Config, path: String): Option[Config] = {
    if (config.hasPath(path)) {
      Some(config.getConfig(path))
    } else {
      None
    }
  }

  /**
    * Get the value from config by the path. If the path not exist, return the default value.
    *
    * @param config       The config.
    * @param path         The path of the config.
    * @param defaultValue The default value for the path.
    * @return
    */
  private[this] def getOrElse[T](config: Config, path: String, defaultValue: T): T = {
    if (config.hasPath(path)) {
      config.getAnyRef(path).asInstanceOf[T]
    } else {
      defaultValue
    }
  }

  /**
    * Get the value from config by the path which is optional.
    * If the path not exist, return the default value.
    *
    * @param config
    * @param path
    * @param defaultValue
    * @tparam T
    * @return
    */
  private[this] def getOptOrElse[T](config: Option[Config], path: String, defaultValue: T): T = {
    if (!config.isEmpty && config.get.hasPath(path)) {
      config.get.getAnyRef(path).asInstanceOf[T]
    } else {
      defaultValue
    }
  }

  def parser(args: Array[String], programName: String): Option[Argument] = {
    val parser = new scopt.OptionParser[Argument](programName) {
      head(programName, "1.0.0")

      opt[File]('c', "config")
        .required()
        .valueName("<file>")
        .action((x, c) => c.copy(config = x))
        .text("config file")

      opt[Unit]('h', "hive")
        .action((_, c) => c.copy(hive = true))
        .text("hive supported")

      opt[Unit]('d', "directly")
        .action((_, c) => c.copy(directly = true))
        .text("directly mode")

      opt[Unit]('D', "dry")
        .action((_, c) => c.copy(dry = true))
        .text("dry run")

      opt[String]('r', "reload")
        .valueName("<path>")
        .action((x, c) => c.copy(reload = x))
        .text("reload path")
    }
    parser.parse(args, Argument())
  }
}

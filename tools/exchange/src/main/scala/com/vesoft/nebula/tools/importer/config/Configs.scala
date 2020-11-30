/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.importer.config

import java.io.File
import java.nio.file.Files

import com.vesoft.nebula.tools.importer.{Argument, KeyPolicy}

import com.typesafe.config.{Config, ConfigFactory}
import org.apache.log4j.Logger

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.util.control.Breaks._

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
case class DataBaseConfigEntry(addresses: List[String],
                               space: String,
                               metaAddresses: List[String]) {
  require(addresses != null && addresses.nonEmpty)
  require(metaAddresses != null && metaAddresses.nonEmpty)
  require(space.trim.nonEmpty)

  override def toString: String = super.toString
}

/**
  * UserConfigEntry is used when the client login the nebula graph service.
  *
  * @param user
  * @param password
  */
case class UserConfigEntry(user: String, password: String) {
  require(user.trim.nonEmpty && password.trim.nonEmpty)

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
  require(errorPath.trim.nonEmpty && errorMaxSize > 0)

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
  private[this] val DEFAULT_LOCAL_PATH           = None
  private[this] val DEFAULT_REMOTE_PATH          = None
  private[this] val DEFAULT_STREAM_INTERVAL      = 30
  private[this] val DEFAULT_PARALLEL             = 1
  private[this] val DEFAULT_IS_IMPLICIT          = true

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
    val addresses     = nebulaConfig.getStringList("address.graph").asScala.toList
    val metaAddresses = nebulaConfig.getStringList("address.meta").asScala.toList

    val space         = nebulaConfig.getString("space")
    val databaseEntry = DataBaseConfigEntry(addresses, space, metaAddresses)
    LOG.info(s"DataBase Config ${databaseEntry}")

    val user      = nebulaConfig.getString("user")
    val pswd      = nebulaConfig.getString("pswd")
    val userEntry = UserConfigEntry(user, pswd)
    LOG.info(s"User Config ${userEntry}")

    val connectionConfig  = getConfigOrNone(nebulaConfig, "connection")
    val connectionTimeout = getOrElse(connectionConfig, "timeout", DEFAULT_CONNECTION_TIMEOUT)
    val connectionRetry   = getOrElse(connectionConfig, "retry", DEFAULT_CONNECTION_RETRY)
    val connectionEntry   = ConnectionConfigEntry(connectionTimeout, connectionRetry)
    LOG.info(s"Connection Config ${connectionConfig}")

    val executionConfig   = getConfigOrNone(nebulaConfig, "execution")
    val executionTimeout  = getOrElse(executionConfig, "timeout", DEFAULT_EXECUTION_TIMEOUT)
    val executionRetry    = getOrElse(executionConfig, "retry", DEFAULT_EXECUTION_RETRY)
    val executionInterval = getOrElse(executionConfig, "interval", DEFAULT_EXECUTION_INTERVAL)
    val executionEntry    = ExecutionConfigEntry(executionTimeout, executionRetry, executionInterval)
    LOG.info(s"Execution Config ${executionEntry}")

    val errorConfig  = getConfigOrNone(nebulaConfig, "error")
    val errorPath    = getOrElse(errorConfig, "output", DEFAULT_ERROR_OUTPUT_PATH)
    val errorMaxSize = getOrElse(errorConfig, "max", DEFAULT_ERROR_MAX_BATCH_SIZE)
    val errorEntry   = ErrorConfigEntry(errorPath, errorMaxSize)

    val rateConfig  = getConfigOrNone(nebulaConfig, "rate")
    val rateLimit   = getOrElse(rateConfig, "limit", DEFAULT_RATE_LIMIT)
    val rateTimeout = getOrElse(rateConfig, "timeout", DEFAULT_RATE_TIMEOUT)
    val rateEntry   = RateConfigEntry(rateLimit, rateTimeout)

    val sparkEntry = SparkConfigEntry(config)

    val tags       = mutable.ListBuffer[TagConfigEntry]()
    val tagConfigs = getConfigsOrNone(config, "tags")
    if (tagConfigs.isDefined) {
      for (tagConfig <- tagConfigs.get.asScala) {
        if (!tagConfig.hasPath("name") ||
            !tagConfig.hasPath("type.source") ||
            !tagConfig.hasPath("type.sink")) {
          LOG.error("The `name` and `type` must be specified")
          break()
        }

        val tagName = tagConfig.getString("name")
        val fields  = tagConfig.getStringList("fields").asScala.toList
        val nebulaFields = if (tagConfig.hasPath("nebula.fields")) {
          tagConfig.getStringList("nebula.fields").asScala.toList
        } else {
          fields
        }

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

        val sourceCategory = toSourceCategory(tagConfig.getString("type.source"))
        val sourceConfig   = dataSourceConfig(sourceCategory, tagConfig, nebulaConfig)
        LOG.info(s"Source Config ${sourceConfig}")

        val sinkCategory = toSinkCategory(tagConfig.getString("type.sink"))
        val sinkConfig   = dataSinkConfig(sinkCategory, nebulaConfig)
        LOG.info(s"Sink Config ${sourceConfig}")

        val batch = getOrElse(tagConfig, "batch", DEFAULT_BATCH)
        val checkPointPath =
          if (tagConfig.hasPath("check_point_path")) Some(tagConfig.getString("check_point_path"))
          else DEFAULT_CHECK_POINT_PATH

        val localPath  = getOptOrElse(tagConfig, "local.path")
        val remotePath = getOptOrElse(tagConfig, "remote.path")

        val partition  = getOrElse(tagConfig, "partition", DEFAULT_PARTITION)
        val isImplicit = getOrElse(tagConfig, "isImplicit", DEFAULT_IS_IMPLICIT)

        LOG.info(s"name ${tagName}  batch ${batch}")
        val entry = TagConfigEntry(tagName,
                                   sourceConfig,
                                   sinkConfig,
                                   fields,
                                   nebulaFields,
                                   vertexField,
                                   policyOpt,
                                   batch,
                                   partition,
                                   checkPointPath,
                                   isImplicit)
        LOG.info(s"Tag Config: ${entry}")
        tags += entry
      }
    }

    val edges       = mutable.ListBuffer[EdgeConfigEntry]()
    val edgeConfigs = getConfigsOrNone(config, "edges")
    if (edgeConfigs.isDefined) {
      for (edgeConfig <- edgeConfigs.get.asScala) {
        if (!edgeConfig.hasPath("name") ||
            !edgeConfig.hasPath("type.source") ||
            !edgeConfig.hasPath("type.sink")) {
          LOG.error("The `name` and `type`must be specified")
          break()
        }

        val edgeName = edgeConfig.getString("name")
        val fields   = edgeConfig.getStringList("fields").asScala.toList
        val nebulaFields = if (edgeConfig.hasPath("nebula.fields")) {
          edgeConfig.getStringList("nebula.fields").asScala.toList
        } else {
          fields
        }
        val isGeo = !edgeConfig.hasPath("source") &&
          edgeConfig.hasPath("latitude") &&
          edgeConfig.hasPath("longitude")

        val sourceCategory = toSourceCategory(edgeConfig.getString("type.source"))
        val sourceConfig   = dataSourceConfig(sourceCategory, edgeConfig, nebulaConfig)
        LOG.info(s"Source Config ${sourceConfig}")

        val sinkCategory = toSinkCategory(edgeConfig.getString("type.sink"))
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

        val batch = getOrElse(edgeConfig, "batch", DEFAULT_BATCH)
        val checkPointPath =
          if (edgeConfig.hasPath("check_point_path")) Some(edgeConfig.getString("check_point_path"))
          else DEFAULT_CHECK_POINT_PATH

        val partition = getOrElse(edgeConfig, "partition", DEFAULT_PARTITION)

        val localPath  = getOptOrElse(edgeConfig, "path.local")
        val remotePath = getOptOrElse(edgeConfig, "path.remote")
        val isImplicit = getOrElse(edgeConfig, "isImplicit", DEFAULT_IS_IMPLICIT)

        val entry = EdgeConfigEntry(
          edgeName,
          sourceConfig,
          sinkConfig,
          fields,
          nebulaFields,
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
          checkPointPath,
          isImplicit
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

  /**
    * Use to category name to category value mapping.
    *
    * @param category name
    * @return
    */
  private[this] def toSourceCategory(category: String): SourceCategory.Value = {
    category.trim.toUpperCase match {
      case "PARQUET" => SourceCategory.PARQUET
      case "ORC"     => SourceCategory.ORC
      case "JSON"    => SourceCategory.JSON
      case "CSV"     => SourceCategory.CSV
      case "HIVE"    => SourceCategory.HIVE
      case "NEO4J"   => SourceCategory.NEO4J
      case "KAFKA"   => SourceCategory.KAFKA
      case "MYSQL"   => SourceCategory.MYSQL
      case "PULSAR"  => SourceCategory.PULSAR
      case "HBASE"   => SourceCategory.HBASE
      case _         => throw new IllegalArgumentException(s"${category} not support")
    }
  }

  /**
    * Use to sink name to sink value mapping.
    *
    * @param category name
    * @return
    */
  private[this] def toSinkCategory(category: String): SinkCategory.Value = {
    category.trim.toUpperCase match {
      case "CLIENT" => SinkCategory.CLIENT
      case "SST"    => SinkCategory.SST
      case _        => throw new IllegalArgumentException(s"${category} not support")
    }
  }

  /**
    * Use to generate data source config according to category of source.
    *
    * @param category
    * @param config
    * @return
    */
  private[this] def dataSourceConfig(category: SourceCategory.Value,
                                     config: Config,
                                     nebulaConfig: Config): DataSourceConfigEntry = {
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
        val csvFields = if (config.hasPath("csv.fields"))
          Some(config.getStringList("csv.fields").asScala.toList)
        else
          None
        FileBaseSourceConfigEntry(SourceCategory.CSV,
                                  config.getString("path"),
                                  Some(separator),
                                  Some(header),
                                  csvFields)
      case SourceCategory.HIVE =>
        HiveSourceConfigEntry(SourceCategory.HIVE, config.getString("exec"))
      case SourceCategory.NEO4J =>
        val name = config.getString("name")
        val checkPointPath =
          if (config.hasPath("check_point_path")) Some(config.getString("check_point_path"))
          else DEFAULT_CHECK_POINT_PATH
        val encryption =
          if (config.hasPath("encryption")) config.getBoolean("encryption") else false
        val parallel =
          if (config.hasPath("partition")) config.getInt("partition") else DEFAULT_PARALLEL
        if (parallel <= 0)
          throw new IllegalArgumentException(s"Can't set neo4j ${name} partition<=0.")
        val database = if (config.hasPath("database")) Some(config.getString("database")) else None
        Neo4JSourceConfigEntry(
          SourceCategory.NEO4J,
          config.getString("exec"),
          name,
          config.getString("server"),
          config.getString("user"),
          config.getString("password"),
          database,
          encryption,
          parallel,
          checkPointPath
        )
      case SourceCategory.JANUS_GRAPH =>
        JanusGraphSourceConfigEntry(SourceCategory.JANUS_GRAPH, "", false) // TODO
      case SourceCategory.MYSQL =>
        MySQLSourceConfigEntry(
          SourceCategory.MYSQL,
          config.getString("host"),
          config.getInt("port"),
          config.getString("database"),
          config.getString("table"),
          config.getString("user"),
          config.getString("password"),
          getOrElse(config, "sentence", "")
        )
      case SourceCategory.KAFKA =>
        val intervalSeconds =
          if (config.hasPath("interval.seconds")) config.getInt("interval.seconds")
          else DEFAULT_STREAM_INTERVAL
        KafkaSourceConfigEntry(SourceCategory.KAFKA,
                               intervalSeconds,
                               config.getString("service"),
                               config.getString("topic"))
      case SourceCategory.PULSAR =>
        val options =
          config.getObject("options").unwrapped.asScala.map(x => x._1 -> x._2.toString).toMap
        val intervalSeconds =
          if (config.hasPath("interval.seconds")) config.getInt("interval.seconds")
          else DEFAULT_STREAM_INTERVAL
        PulsarSourceConfigEntry(SourceCategory.PULSAR,
                                intervalSeconds,
                                config.getString("service"),
                                config.getString("admin"),
                                options)
      case SourceCategory.HBASE =>
        val fields: ListBuffer[String] = new ListBuffer[String]
        fields.append(config.getStringList("fields").asScala: _*)

        if (config.hasPath("vertex")) {
          fields.append(config.getString("vertex"))
        }
        if (config.hasPath("source.field")) {
          fields.append(config.getString("source.field"))
        }
        if (config.hasPath("target.field")) {
          fields.append(config.getString("target.field"))
        }

        HBaseSourceConfigEntry(SourceCategory.HBASE,
                               config.getString("host"),
                               config.getString("port"),
                               config.getString("table"),
                               config.getString("columnFamily"),
                               fields.toSet.toList)
      case _ =>
        throw new IllegalArgumentException("Unsupported data source")
    }
  }

  private[this] def dataSinkConfig(category: SinkCategory.Value,
                                   nebulaConfig: Config): DataSinkConfigEntry = {
    category match {
      case SinkCategory.CLIENT =>
        NebulaSinkConfigEntry(SinkCategory.CLIENT,
                              nebulaConfig.getStringList("address.graph").asScala.toList)
      case SinkCategory.SST =>
        FileBaseSinkConfigEntry(SinkCategory.SST,
                                nebulaConfig.getString("path.local"),
                                nebulaConfig.getString("path.remote"))
      case _ =>
        throw new IllegalArgumentException("Unsupported data sink")
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

  private[this] def getOptOrElse(config: Config, path: String): Option[String] = {
    if (config.hasPath(path)) {
      Some(config.getString(path))
    } else {
      None
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
  private[this] def getOrElse[T](config: Option[Config], path: String, defaultValue: T): T = {
    if (config.isDefined && config.get.hasPath(path)) {
      config.get.getAnyRef(path).asInstanceOf[T]
    } else {
      defaultValue
    }
  }

  /**
    * Use to parse command line arguments.
    *
    * @param args
    * @param programName
    * @return Argument
    */
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

/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.algorithm.config

import java.io.File
import java.nio.file.Files
import org.apache.log4j.Logger
import scala.collection.JavaConverters._
import com.typesafe.config.{Config, ConfigFactory}
import scala.collection.mutable

/**
  * sparkConfig is used to submit spark application, such as graph algorithm
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
  * AlgorithmConfig is used to run graph algorithm
  */
object AlgorithmConfigEntry {
  def apply(config: Config): AlgorithmConfigEntry = {
    val map        = mutable.Map[String, String]()
    val algoConfig = config.getObject("algorithm")
    for (key <- algoConfig.unwrapped().keySet().asScala) {
      val algorithmKey = s"algorithm.${key}"
      if (config.getAnyRef(algorithmKey).isInstanceOf[String]) {
        val algorithmValue = config.getString(algorithmKey)
        map += algorithmKey -> algorithmValue
      } else {
        for (subkey <- config.getObject(algorithmKey).unwrapped().keySet().asScala) {
          val key   = s"${algorithmKey}.${subkey}"
          val value = config.getString(key)
          map += key -> value
        }
      }
    }
    AlgorithmConfigEntry(map.toMap)
  }
}

/** DataSourceEntry is used to determine the data source , nebula or local */
object DataSourceSinkEntry {
  def apply(config: Config): DataSourceSinkEntry = {
    val dataSource = config.getString("data.source")
    val dataSink   = config.getString("data.sink")
    val hasWeight = if (config.hasPath("data.hasWeight")) {
      config.getBoolean("data.hasWeight")
    } else false
    DataSourceSinkEntry(dataSource, dataSink, hasWeight)
  }
}

/**
  * NebulaConfig is used to read edge data
  */
object NebulaConfigEntry {
  def apply(config: Config): NebulaConfigEntry = {
    if (!config.hasPath("nebula")) {
      return NebulaConfigEntry(NebulaReadConfigEntry(), NebulaWriteConfigEntry())
    }
    val nebulaConfig = config.getConfig("nebula")

    val metaAddress         = nebulaConfig.getString("read.metaAddress")
    val readSpace           = nebulaConfig.getString("read.space")
    val readPartitionNumber = nebulaConfig.getString("read.partitionNumber")
    val readLabels          = nebulaConfig.getStringList("read.labels").asScala.toList
    val readWeightCols      = nebulaConfig.getStringList("read.weightCols").asScala.toList
    val readConfigEntry =
      NebulaReadConfigEntry(metaAddress, readSpace, readPartitionNumber, readLabels, readWeightCols)

    val graphAddress = nebulaConfig.getString("write.graphAddress")
    val user         = nebulaConfig.getString("write.user")
    val pswd         = nebulaConfig.getString("write.pswd")
    val writeSpace   = nebulaConfig.getString("write.space")
    val writeTag     = nebulaConfig.getString("write.tag")
    val writePropCol = nebulaConfig.getString("write.propCol")
    val writeColType = nebulaConfig.getString("write.colType")
    val writeConfigEntry = NebulaWriteConfigEntry(graphAddress,
                                                  user,
                                                  pswd,
                                                  writeSpace,
                                                  writeTag,
                                                  writePropCol,
                                                  writeColType)
    NebulaConfigEntry(readConfigEntry, writeConfigEntry)
  }
}

object LocalConfigEntry {
  def apply(config: Config): LocalConfigEntry = {

    var filePath: String   = ""
    var src: String        = ""
    var dst: String        = ""
    var weight: String     = ""
    var resultPath: String = null
    var header: Boolean    = false
    var delimiter: String  = ","

    if (config.hasPath("local.read.filePath")) {
      filePath = config.getString("local.read.filePath")
      src = config.getString("local.read.srcId")
      dst = config.getString("local.read.dstId")
      if (config.hasPath("local.read.weight")) {
        weight = config.getString("local.read.weight")
      }
      if (config.hasPath("local.read.delimiter")) {
        delimiter = config.getString("local.read.delimiter")
      }
      if (config.hasPath("local.read.header")) {
        header = config.getBoolean("local.read.header")
      }
    }
    if (config.hasPath("local.write.resultPath")) {
      resultPath = config.getString("local.write.resultPath")
    }

    LocalConfigEntry(filePath, src, dst, weight, resultPath, header, delimiter)
  }
}

/**
  * SparkConfigEntry support key-value pairs for spark session.
  *
  * @param map
  */
case class SparkConfigEntry(map: Map[String, String]) {
  override def toString: String = {
    map.toString()
  }
}

/**
  * AlgorithmConfigEntry support key-value pairs for algorithms.
  *
  * @param map
  */
case class AlgorithmConfigEntry(map: Map[String, String]) {
  override def toString: String = {
    map.toString()
  }
}

/**
  * DataSourceEntry
  */
case class DataSourceSinkEntry(source: String, sink: String, hasWeight: Boolean) {
  override def toString: String = {
    s"DataSourceEntry: {source:$source, sink:$sink, hasWeight:$hasWeight}"
  }
}

case class LocalConfigEntry(filePath: String,
                            srcId: String,
                            dstId: String,
                            weight: String,
                            resultPath: String,
                            header: Boolean,
                            delimiter: String) {
  override def toString: String = {
    s"LocalConfigEntry: {filePath: $filePath, srcId: $srcId, dstId: $dstId, " +
      s"weight:$weight, resultPath:$resultPath, delimiter:$delimiter}"
  }
}

/**
  * NebulaConfigEntry support key-value pairs for nebula.
  *
  */
case class NebulaConfigEntry(readConfigEntry: NebulaReadConfigEntry,
                             writeConfigEntry: NebulaWriteConfigEntry) {
  override def toString: String = {
    s"NebulaConfigEntry:{${readConfigEntry.toString}, ${writeConfigEntry.toString}"
  }
}

case class NebulaReadConfigEntry(address: String = "",
                                 space: String = "",
                                 partitionNumber: String = "",
                                 labels: List[String] = List(),
                                 weightCols: List[String] = List()) {
  override def toString: String = {
    s"NebulaReadConfigEntry: " +
      s"{address: $address, space: $space, partitionNumber: $partitionNumber, " +
      s"labels: ${labels.mkString(",")}, weightCols: ${weightCols.mkString(",")}"
  }
}

case class NebulaWriteConfigEntry(graphAddress: String = "",
                                  user: String = "",
                                  pswd: String = "",
                                  space: String = "",
                                  tag: String = "",
                                  propCol: String = "",
                                  propType: String = "") {
  override def toString: String = {
    s"NebulaWriteConfigEntry: " +
      s"{graphAddress: $graphAddress, user: $user, password: $pswd, space: $space, tag: $tag, " +
      s"propCol: $propCol, propType: $propType}"
  }
}

/**
  * Configs
  */
case class Configs(sparkConfig: SparkConfigEntry,
                   dataSourceSinkEntry: DataSourceSinkEntry,
                   nebulaConfig: NebulaConfigEntry,
                   localConfigEntry: LocalConfigEntry,
                   algorithmConfig: AlgorithmConfigEntry)

object Configs {
  private[this] val LOG = Logger.getLogger(this.getClass)

  /**
    *
    * @param configPath
    * @return
    */
  def parse(configPath: File): Configs = {
    if (!Files.exists(configPath.toPath)) {
      throw new IllegalArgumentException(s"${configPath} not exist")
    }

    val config            = ConfigFactory.parseFile(configPath)
    val dataSourceEntry   = DataSourceSinkEntry(config)
    val localConfigEntry  = LocalConfigEntry(config)
    val nebulaConfigEntry = NebulaConfigEntry(config)
    val sparkEntry        = SparkConfigEntry(config)
    val algorithmEntry    = AlgorithmConfigEntry(config)

    Configs(sparkEntry, dataSourceEntry, nebulaConfigEntry, localConfigEntry, algorithmEntry)
  }

  /**
    * Get the config list by the path.
    *
    * @param config The config.
    * @param path   The path of the config.
    *
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
    *
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
    *
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

  final case class Argument(config: File = new File("application.conf"))

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

      opt[File]('p', "prop")
        .required()
        .valueName("<file>")
        .action((x, c) => c.copy(config = x))
        .text("config file")
    }
    parser.parse(args, Argument())
  }

}

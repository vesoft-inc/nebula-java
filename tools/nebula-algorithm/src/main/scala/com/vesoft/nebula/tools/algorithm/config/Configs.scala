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

/**
  * NebulaConfig is used to read edge data
  */
object NebulaConfigEntry {
  def apply(config: Config): NebulaConfigEntry = {
    val nebulaConfig       = config.getConfig("nebula")
    val addresses          = nebulaConfig.getString("addresses")
    val space              = nebulaConfig.getString("space")
    val partitionNumber    = nebulaConfig.getString("partitionNumber")
    val labels             = nebulaConfig.getStringList("labels").asScala.toList
    val hasWeight: Boolean = nebulaConfig.getBoolean("hasWeight")
    val weightCols = if (hasWeight) {
      nebulaConfig.getStringList("weightCols").asScala.toList
    } else { List() }
    NebulaConfigEntry(addresses, space, partitionNumber, labels, hasWeight, weightCols)
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
  * NebulaConfigEntry support key-value pairs for nebula.
  *
  * @param address
  * @param space
  * @param partitionNumber
  * @param labels
  * @param weightCols
  */
case class NebulaConfigEntry(address: String,
                             space: String,
                             partitionNumber: String,
                             labels: List[String],
                             hasWeight: Boolean,
                             weightCols: List[String]) {
  override def toString: String = {
    s"NebulaConfigEntry: " +
      s"{address: $address, space: $space, partitionNumber: $partitionNumber, " +
      s"labels: ${labels.mkString(",")}, hasWeight: $hasWeight, weightCols: ${weightCols.mkString(",")}"
  }
}

/**
  * Configs
  */
case class Configs(nebulaConfig: NebulaConfigEntry,
                   sparkConfig: SparkConfigEntry,
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
    val nebulaConfigEntry = NebulaConfigEntry(config)
    val sparkEntry        = SparkConfigEntry(config)
    val algorithmEntry    = AlgorithmConfigEntry(config)

    Configs(nebulaConfigEntry, sparkEntry, algorithmEntry)
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

  final case class Argument(config: File = new File("application.conf"),
                            hive: Boolean = false,
                            directly: Boolean = false,
                            dry: Boolean = false,
                            reload: String = "")

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

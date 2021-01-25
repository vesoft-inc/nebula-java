/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.algorithm

import com.vesoft.nebula.tools.algorithm.config.Configs.Argument
import com.vesoft.nebula.tools.algorithm.config.{
  AlgoConfig,
  CcConfig,
  Configs,
  DegreeStaticConfig,
  KCoreConfig,
  LPAConfig,
  LouvainConfig,
  NebulaConfig,
  PRConfig,
  ShortestPathConfig,
  SparkConfig
}
import com.vesoft.nebula.tools.algorithm.lib.{
  ConnectedComponentsAlgo,
  DegreeStaticAlgo,
  KCoreAlgo,
  LabelPropagationAlgo,
  LouvainAlgo,
  PageRankAlgo,
  ShortestPathAlgo
}
import com.vesoft.nebula.tools.algorithm.utils.NebulaUtil
import org.apache.commons.math3.ode.UnknownParameterException
import org.apache.log4j.Logger
import org.apache.spark.sql.{Dataset, Row}

/**
  * This object is the entry of all graph algorithms.
  *
  * How to use this tool to run algorithm:
  *    1. Configure application.conf file.
  *    2. Make sure your environment has installed spark and started spark service.
  *    3. Submit nebula algorithm application using this command:
  *        spark-submit --class com.vesoft.nebula.tools.algorithm.Main /your-jar-path/nebula-algorithm-1.1.0.jar -p /your-application.conf-path/application.conf
  */
object Main {

  private val LOGGER = Logger.getLogger(this.getClass)

  def main(args: Array[String]): Unit = {
    val PROGRAM_NAME = "Nebula graphx"
    val options      = Configs.parser(args, PROGRAM_NAME)
    val p: Argument = options match {
      case Some(config) => config
      case _ =>
        LOGGER.error("Argument parse failed")
        sys.exit(-1)
    }
    val configs = Configs.parse(p.config)
    LOGGER.info(s"configs =  ${configs}")

    val algoName: String = AlgoConfig.getAlgoName(configs)
    LOGGER.info(s"algoName= ${algoName}")

    val sparkConfig = SparkConfig.getSpark(configs)
    val dataSource  = configs.dataSourceSinkEntry.source
    val hasWeight   = configs.dataSourceSinkEntry.hasWeight

    val nebulaReadConfig = NebulaConfig.getReadNebula(configs)

    val localPath = configs.localConfigEntry.filePath
    val src       = configs.localConfigEntry.srcId
    val dst       = configs.localConfigEntry.dstId
    val weight    = configs.localConfigEntry.weight

    // reader
    val dataSet: Dataset[Row] = dataSource.toLowerCase match {
      case "nebula" => {
        NebulaUtil.scanEdgeData(
          sparkConfig.spark,
          sparkConfig.partitionNum,
          nebulaReadConfig.address,
          nebulaReadConfig.partitionNumber,
          nebulaReadConfig.space,
          nebulaReadConfig.labels,
          hasWeight,
          nebulaReadConfig.weightCols
        )
      }

      case "csv" => {
        val delimiter = configs.localConfigEntry.delimiter
        val header    = configs.localConfigEntry.header
        val data =
          sparkConfig.spark.read
            .option("header", header)
            .option("delimiter", delimiter)
            .csv(localPath)

        if (weight == null || weight.trim.isEmpty) {
          data.select(src, dst)
        } else {
          data.select(src, dst, weight)
        }
      }
      case "json" => {
        val data = sparkConfig.spark.read.json(localPath)
        if (weight == null || weight.trim.isEmpty) {
          data.select(src, dst)
        } else {
          data.select(src, dst, weight)
        }
      }
      case "parquet" => {
        val data = sparkConfig.spark.read.parquet(localPath)
        if (weight == null || weight.trim.isEmpty) {
          data.select(src, dst)
        } else {
          data.select(src, dst, weight)
        }
      }
    }

    val algoResult = {
      algoName.toLowerCase match {
        case "pagerank" => {
          val pageRankConfig = PRConfig.getPRConfig(configs)
          PageRankAlgo(sparkConfig.spark, dataSet, pageRankConfig, hasWeight)
        }
        case "louvain" => {
          val louvainConfig = LouvainConfig.getLouvainConfig(configs)
          LouvainAlgo(sparkConfig.spark, dataSet, louvainConfig, hasWeight)
        }
        case "connectedcomponent" => {
          val ccConfig = CcConfig.getCcConfig(configs)
          ConnectedComponentsAlgo(sparkConfig.spark, dataSet, ccConfig, hasWeight)
        }
        case "labelpropagation" => {
          val lpaConfig = LPAConfig.getLPAConfig(configs)
          LabelPropagationAlgo(sparkConfig.spark, dataSet, lpaConfig, hasWeight)
        }
        case "shortestpaths" => {
          val spConfig = ShortestPathConfig.getShortestPathConfig(configs)
          ShortestPathAlgo(sparkConfig.spark, dataSet, spConfig, hasWeight)
        }
        case "degreestatic" => {
          DegreeStaticAlgo(sparkConfig.spark, dataSet)
        }
        case "kcore" => {
          val kCoreConfig = KCoreConfig.getKCoreConfig(configs)
          KCoreAlgo(sparkConfig.spark, dataSet, kCoreConfig)
        }
        case _ => throw new UnknownParameterException("unknown executeAlgo name.")
      }
    }

    // writer
    val dataSink          = configs.dataSourceSinkEntry.sink
    val nebulaWriteConfig = configs.nebulaConfig.writeConfigEntry
    val resultPath        = configs.localConfigEntry.resultPath
    dataSink.toLowerCase match {
      case "nebula" => {
        NebulaUtil.writeNebula(algoResult, nebulaWriteConfig, algoName)
      }
      case "csv" => {
        algoResult.write.option("header", "true").csv(resultPath)
      }
      case "txt" => {
        algoResult.write.option("header", "true").text(resultPath)
      }
      case _ => throw new UnsupportedOperationException("unsupported data sink")
    }
  }
}

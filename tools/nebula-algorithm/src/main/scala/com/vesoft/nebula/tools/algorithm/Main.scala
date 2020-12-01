/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.algorithm

import com.vesoft.nebula.tools.algorithm.config.Configs.Argument
import com.vesoft.nebula.tools.algorithm.config.{AlgoConfig, Configs}
import com.vesoft.nebula.tools.algorithm.lib.{LouvainAlgo, PageRankAlgo}
import org.apache.commons.math3.ode.UnknownParameterException
import org.apache.log4j.Logger

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

    algoName.toLowerCase match {
      case "pagerank" => PageRankAlgo(configs)
      case "louvain"  => LouvainAlgo(configs)
      case _          => throw new UnknownParameterException("unknown executeAlgo name.")
    }
  }
}

/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.algorithm.config

import org.apache.spark.graphx.VertexId

case class PRConfig(PRPath: String, maxIter: Int, resetProb: Double)

/**
  * pagerank algorithm configuration
  */
object PRConfig {
  var prPath: String    = _
  var maxIter: Int      = _
  var resetProb: Double = _

  def getPRConfig(configs: Configs): PRConfig = {
    val prConfig = configs.algorithmConfig.map

    prPath = prConfig("algorithm.path")
    maxIter = prConfig("algorithm.pagerank.maxIter").toInt
    resetProb =
      if (prConfig.contains("algorithm.pagerank.resetProb"))
        prConfig("algorithm.pagerank.resetProb").toDouble
      else 0.15

    PRConfig(prPath, maxIter, resetProb)
  }
}

case class LPAConfig(lpaPath: String, maxIter: Int)

/**
  * labelPropagation algorithm configuration
  */
object LPAConfig {
  var lpaPath: String = _
  var maxIter: Int    = _

  def getLPAConfig(configs: Configs): LPAConfig = {
    val lpaConfig = configs.algorithmConfig.map

    lpaPath = lpaConfig("algorithm.path")
    maxIter = lpaConfig("algorithm.labelpropagation.maxIter").toInt
    LPAConfig(lpaPath, maxIter)
  }
}

case class CcConfig(ccPath: String, maxIter: Int)

/**
  * ConnectedComponect algorithm configuration
  */
object CcConfig {
  var ccPath: String = _
  var maxIter: Int   = _

  def getCcConfig(configs: Configs): CcConfig = {
    val ccConfig = configs.algorithmConfig.map

    ccPath = ccConfig("algorithm.path")
    maxIter = ccConfig("algorithm.connectedcomonent.maxIter").toInt
    CcConfig(ccPath, maxIter)
  }
}

case class ShortestPathConfig(spPath: String, landmarks: Seq[VertexId])

/**
  * ConnectedComponect algorithm configuration
  */
object ShortestPathConfig {
  var spPath: String       = _
  var landmarks: Seq[Long] = _

  def getShortestPathConfig(configs: Configs): ShortestPathConfig = {
    val spConfig = configs.algorithmConfig.map

    spPath = spConfig("algorithm.path")
    landmarks = spConfig("algorithm.shortestpath.landmarks").split(",").toSeq.map(_.toLong)
    ShortestPathConfig(spPath, landmarks)
  }
}

case class LouvainConfig(louvainPath: String, maxIter: Int, internalIter: Int, tol: Double)

/**
  * louvain algorithm configuration
  */
object LouvainConfig {
  var louvainPath: String = _
  var maxIter: Int        = _
  var internalIter: Int   = _
  var tol: Double         = _

  def getLouvainConfig(configs: Configs): LouvainConfig = {
    val louvainConfig = configs.algorithmConfig.map

    louvainPath = louvainConfig("algorithm.path")
    maxIter = louvainConfig("algorithm.louvain.maxIter").toInt
    internalIter = louvainConfig("algorithm.louvain.internalIter").toInt
    tol = louvainConfig("algorithm.louvain.tol").toDouble

    LouvainConfig(louvainPath, maxIter, internalIter, tol)
  }
}

case class AlgoConfig(configs: Configs)

object AlgoConfig {
  def getAlgoName(configs: Configs): String = {
    val algoConfig = configs.algorithmConfig.map
    algoConfig("algorithm.executeAlgo")
  }
}

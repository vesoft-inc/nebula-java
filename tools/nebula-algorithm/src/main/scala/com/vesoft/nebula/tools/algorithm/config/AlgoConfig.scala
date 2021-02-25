/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.algorithm.config

import org.apache.spark.graphx.VertexId

case class PRConfig(maxIter: Int, resetProb: Double)

/**
  * pagerank algorithm configuration
  */
object PRConfig {
  var maxIter: Int      = _
  var resetProb: Double = _

  def getPRConfig(configs: Configs): PRConfig = {
    val prConfig = configs.algorithmConfig.map

    maxIter = prConfig("algorithm.pagerank.maxIter").toInt
    resetProb =
      if (prConfig.contains("algorithm.pagerank.resetProb"))
        prConfig("algorithm.pagerank.resetProb").toDouble
      else 0.15

    PRConfig(maxIter, resetProb)
  }
}

case class LPAConfig(maxIter: Int)

/**
  * labelPropagation algorithm configuration
  */
object LPAConfig {
  var maxIter: Int = _

  def getLPAConfig(configs: Configs): LPAConfig = {
    val lpaConfig = configs.algorithmConfig.map

    maxIter = lpaConfig("algorithm.labelpropagation.maxIter").toInt
    LPAConfig(maxIter)
  }
}

case class CcConfig(maxIter: Int)

/**
  * ConnectedComponect algorithm configuration
  */
object CcConfig {
  var maxIter: Int = _

  def getCcConfig(configs: Configs): CcConfig = {
    val ccConfig = configs.algorithmConfig.map

    maxIter = ccConfig("algorithm.connectedcomponent.maxIter").toInt
    CcConfig(maxIter)
  }
}

case class ShortestPathConfig(landmarks: Seq[VertexId])

/**
  * ConnectedComponect algorithm configuration
  */
object ShortestPathConfig {
  var landmarks: Seq[Long] = _

  def getShortestPathConfig(configs: Configs): ShortestPathConfig = {
    val spConfig = configs.algorithmConfig.map

    landmarks = spConfig("algorithm.shortestpaths.landmarks").split(",").toSeq.map(_.toLong)
    ShortestPathConfig(landmarks)
  }
}

case class LouvainConfig(maxIter: Int, internalIter: Int, tol: Double)

/**
  * louvain algorithm configuration
  */
object LouvainConfig {
  var maxIter: Int      = _
  var internalIter: Int = _
  var tol: Double       = _

  def getLouvainConfig(configs: Configs): LouvainConfig = {
    val louvainConfig = configs.algorithmConfig.map

    maxIter = louvainConfig("algorithm.louvain.maxIter").toInt
    internalIter = louvainConfig("algorithm.louvain.internalIter").toInt
    tol = louvainConfig("algorithm.louvain.tol").toDouble

    LouvainConfig(maxIter, internalIter, tol)
  }
}

/**
  * degree static
  */
case class DegreeStaticConfig(degree: Boolean, inDegree: Boolean, outDegree: Boolean)

object DegreeStaticConfig {
  var degree: Boolean    = false
  var inDegree: Boolean  = false
  var outDegree: Boolean = false

  def getDegreeStaticConfig(configs: Configs): DegreeStaticConfig = {
    val degreeConfig = configs.algorithmConfig.map
    degree = ConfigUtil.getOrElseBoolean(degreeConfig, "algorithm.degreestatic.degree", false)
    inDegree = ConfigUtil.getOrElseBoolean(degreeConfig, "algorithm.degreestatic.indegree", false)
    outDegree = ConfigUtil.getOrElseBoolean(degreeConfig, "algorithm.degreestatic.outdegree", false)
    DegreeStaticConfig(degree, inDegree, outDegree)
  }
}

/**
  * k-core
  */
case class KCoreConfig(maxIter: Int, degree: Int)

object KCoreConfig {
  var maxIter: Int = _
  var degree: Int  = _

  def getKCoreConfig(configs: Configs): KCoreConfig = {
    val kCoreConfig = configs.algorithmConfig.map
    maxIter = kCoreConfig("algorithm.kcore.maxIter").toInt
    degree = kCoreConfig("algorithm.kcore.degree").toInt
    KCoreConfig(maxIter, degree)
  }
}

/**
  * Betweenness
  */
case class BetweennessConfig(maxIter: Int)

object BetweennessConfig {
  var maxIter: Int = _

  def getBetweennessConfig(configs: Configs): BetweennessConfig = {
    val betweennessConfig = configs.algorithmConfig.map
    maxIter = betweennessConfig("algorithm.betweenness.maxIter").toInt
    BetweennessConfig(maxIter)
  }
}

case class AlgoConfig(configs: Configs)

object AlgoConfig {
  def getAlgoName(configs: Configs): String = {
    val algoConfig = configs.algorithmConfig.map
    algoConfig("algorithm.executeAlgo")
  }
}

object ConfigUtil {
  def getOrElseBoolean(config: Map[String, String], key: String, defaultValue: Boolean): Boolean = {
    if (config.contains(key)) {
      config(key).toBoolean
    } else {
      defaultValue
    }
  }

}

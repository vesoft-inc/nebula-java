/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.algorithm.lib

import com.vesoft.nebula.tools.algorithm.config.{Configs, LouvainConfig, NebulaConfig, SparkConfig}
import com.vesoft.nebula.tools.algorithm.utils.NebulaUtil
import org.apache.log4j.Logger
import org.apache.spark.graphx.{Edge, Graph, VertexId, VertexRDD}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}
import org.apache.spark.sql.types.{LongType, StructField, StructType}
import scala.collection.mutable
import scala.collection.mutable.{HashMap, HashSet}

object LouvainAlgo {
  private val LOGGER = Logger.getLogger(this.getClass)

  val ALGORITHM: String = "Louvain"

  /**
    * run the louvain algorithm for nebula graph
    *
    * 1. get the configuration which is configured in application.conf
    * 2. read nebula edge data
    * 3. construct initial graph
    * 4. execute louvain algorithm
    * 5. save the louvain result
    */
  def apply(configs: Configs): DataFrame = {

    val sparkConfig   = SparkConfig.getSpark(configs, ALGORITHM)
    val nebulaConfig  = NebulaConfig.getNebula(configs)
    val louvainConfig = LouvainConfig.getLouvainConfig(configs)

    val dataSet: Dataset[Row] =
      NebulaUtil.scanEdgeData(
        sparkConfig.spark,
        sparkConfig.partitionNum,
        nebulaConfig.hostPorts,
        nebulaConfig.partitionNumber,
        nebulaConfig.nameSpace,
        nebulaConfig.labels,
        nebulaConfig.hasWeight,
        nebulaConfig.weightCols
      )

    val resultPath                      = NebulaUtil.getResultPath(louvainConfig.louvainPath, ALGORITHM)
    val graph: Graph[None.type, Double] = NebulaUtil.loadInitGraph(dataSet, nebulaConfig.hasWeight)

    val louvainResultRDD: RDD[Row] =
      execute(sparkConfig.spark,
              graph,
              louvainConfig.maxIter,
              louvainConfig.internalIter,
              louvainConfig.tol)

    val schema = StructType(
      List(
        StructField("_id", LongType, nullable = false),
        StructField("_community", LongType, nullable = false)
      ))

    val louvainResult = sparkConfig.spark.sqlContext
      .createDataFrame(louvainResultRDD, schema)
    louvainResult.write.parquet(resultPath)
    louvainResult
  }

  def execute(spark: SparkSession,
              graph: Graph[None.type, Double],
              maxIter: Int,
              internalIter: Int,
              tol: Double): RDD[Row] = {

    val sc = spark.sparkContext

    // convert origin graph to Louvain Graph, Louvain Graph records vertex's community、innerVertices and innerDegrees
    var louvainG: Graph[VertexData, Double] = LouvainGraphUtil.createLouvainGraph(graph)

    // compute and broadcast the sum of all edges
    val m       = sc.broadcast(louvainG.edges.map(e => e.attr).sum())
    var curIter = 0
    var res     = step1(internalIter, louvainG, m.value, tol)
    while (res._2 != 0 && curIter < maxIter) {
      louvainG = res._1
      louvainG = step2(louvainG)
      res = step1(internalIter, louvainG, m.value, tol)
      curIter += 1
    }
    CommUtil.getCommunities(louvainG)
  }

  /**
    * Louvain step1：Traverse the vertices and get the new community information of each node遍历节点，获取每个节点对应的所属新社区信息
    *
    *   1. Calculate the information of the community that each vertex currently belongs to.
    *   2. Calculate the community modularity deference and get the community info which has max modularity deference.
    *   3. Count the number of vertices that have changed in the community, used to determine whether the internal iteration can stop.
    *   4. Update vertices' community id and update each community's innerVertices.
    *
    * This step update vertexData's cid and commVertex.
    *
    * @param maxIter max interation
    * @param louvainG
    * @param m       The sum of the weights of all edges in the graph
    *
    * @return (Graph[VertexData,Double],Int)
    */
  def step1(
      maxIter: Int,
      louvainG: Graph[VertexData, Double],
      m: Double,
      tol: Double
  ): (Graph[VertexData, Double], Int) = {
    LOGGER.info("============================== step 1 =======================")
    var G        = louvainG
    var iterTime = 0
    var canStop  = false
    while (iterTime < maxIter && !canStop) {
      val neighborComm = getNeighCommInfo(G)
      val changeInfo   = getChangeInfo(G, neighborComm, m, tol)
      // Count the number of vertices that have changed in the community
      val changeCount =
        G.vertices.zip(changeInfo).filter(x => x._1._2.cId != x._2._2).count()
      if (changeCount == 0)
        canStop = true
      // use connectedComponents algorithm to solve the problem of community attribution delay.
      else {
        val newChangeInfo = Graph
          .fromEdgeTuples(changeInfo.map(x => (x._1, x._2)), 0)
          .connectedComponents()
          .vertices
        G = LouvainGraphUtil.updateGraph(G, newChangeInfo)
        iterTime += 1
      }
    }
    (G, iterTime)
  }

  /**
    * Louvain step 2：Combine the new graph node obtained in the first step into a super node according to
    *                 the community information to which it belongs.
    *
    * @param G graph
    * @return graph
    */
  def step2(G: Graph[VertexData, Double]): Graph[VertexData, Double] = {
    LOGGER.info("============================== step 2 =======================")
    //get edges between different communities
    val edges = G.triplets
      .filter(trip => trip.srcAttr.cId != trip.dstAttr.cId)
      .map(trip => {
        val cid1   = trip.srcAttr.cId
        val cid2   = trip.dstAttr.cId
        val weight = trip.attr
        ((math.min(cid1, cid2), math.max(cid1, cid2)), weight)
      })
      .reduceByKey(_ + _)
      .map(x => Edge(x._1._1, x._1._2, x._2)) //sum the edge weights between communities

    // sum kin of all vertices within the same community
    val vInnerKin = G.vertices
      .map(v => (v._2.cId, (v._2.innerVertices.toSet, v._2.innerDegree)))
      .reduceByKey((x, y) => {
        val vertices = (x._1 ++ y._1).toSet
        val kIn      = x._2 + y._2
        (vertices, kIn)
      })

    // get all edge weights within the same community
    val v2vKin = G.triplets
      .filter(trip => trip.srcAttr.cId == trip.dstAttr.cId)
      .map(trip => {
        val cid       = trip.srcAttr.cId
        val vertices1 = trip.srcAttr.innerVertices
        val vertices2 = trip.dstAttr.innerVertices
        val weight    = trip.attr * 2
        (cid, (vertices1.union(vertices2).toSet, weight))
      })
      .reduceByKey((x, y) => {
        val vertices = new HashSet[VertexId].toSet
        val kIn      = x._2 + y._2
        (vertices, kIn)
      })

    // new super vertex
    val superVertexInfo = vInnerKin
      .union(v2vKin)
      .reduceByKey((x, y) => {
        val vertices = x._1 ++ y._1
        val kIn      = x._2 + y._2
        (vertices, kIn)
      })

    // reconstruct graph based on new edge info
    val initG        = Graph.fromEdges(edges, None)
    var louvainGraph = LouvainGraphUtil.createLouvainGraph(initG)
    // get new louvain graph
    louvainGraph = louvainGraph.outerJoinVertices(superVertexInfo)((vid, data, opt) => {
      var innerVerteices = new HashSet[VertexId]()
      val kIn            = opt.get._2
      for (vid <- opt.get._1)
        innerVerteices += vid
      data.innerVertices = innerVerteices
      data.innerDegree = kIn
      data
    })
    louvainGraph
  }

  /**
    * get new community's basic info after the vertex joins the community
    *   1. get each vertex's community id and the community's tot.
    *   2. compute each vertex's k_in. (The sum of the edge weights between vertex and vertex i in the community)
    *
    * @param G
    */
  def getNeighCommInfo(
      G: Graph[VertexData, Double]
  ): RDD[(VertexId, Iterable[(Long, Double, Double)])] = {

    val commKIn = G.triplets
      .flatMap(trip => {
        Array(
          (
            trip.srcAttr.cId,
            (
              (trip.dstId -> trip.attr),
              (trip.srcId, trip.srcAttr.innerDegree + trip.srcAttr.degree)
            )
          ),
          (
            trip.dstAttr.cId,
            (
              (trip.srcId -> trip.attr),
              (trip.dstId, trip.dstAttr.innerDegree + trip.dstAttr.degree)
            )
          )
        )
      })
      .groupByKey()
      .map(t => {
        val cid = t._1
        // add the weight of the same vid in one community.
        val m       = new HashMap[VertexId, Double]() // store community's vertexId and vertex kin
        val degrees = new HashSet[VertexId]() // record if all vertices has computed the tot
        var tot     = 0.0
        for (x <- t._2) {
          if (m.contains(x._1._1))
            m(x._1._1) += x._1._2
          else
            m(x._1._1) = x._1._2
          // compute vertex's tot
          if (!degrees.contains(x._2._1)) {
            tot += x._2._2
            degrees += x._2._1
          }
        }
        (cid, (tot, m))
      })

    // convert commKIn
    val neighCommInfo = commKIn
      .flatMap(x => {
        val cid = x._1
        val tot = x._2._1
        x._2._2.map(t => {
          val vid = t._1
          val kIn = t._2
          (vid, (cid, kIn, tot))
        })
      })
      .groupByKey()

    neighCommInfo
  }

  /**
    * Calculate the influence of each vertex on the modularity change of neighbor communities, and find the most suitable community for the vertex
    * △Q = [Kin - Σtot * Ki / m]
    *
    * @param G graph
    * @param neighCommInfo neighbor community info
    * @param m  broadcast value
    * @param tol  threshold for modularity deference
    *
    * @return RDD
    */
  def getChangeInfo(G: Graph[VertexData, Double],
                    neighCommInfo: RDD[(VertexId, Iterable[(Long, Double, Double)])],
                    m: Double,
                    tol: Double): RDD[(VertexId, Long, Double)] = {
    val changeInfo = G.vertices
      .join(neighCommInfo)
      .map(x => {
        val vid      = x._1
        val data     = x._2._1
        val commIter = x._2._2
        val vCid     = data.cId
        val k_v      = data.degree + data.innerDegree

        val dertaQs = commIter.map(t => {
          val nCid   = t._1 // neighbor community id
          val k_v_in = t._2
          var tot    = t._3
          if (vCid == nCid)
            tot -= k_v
          val q = (k_v_in - tot * k_v / m)
          (vid, nCid, q)
        })
        val maxQ =
          dertaQs.max(Ordering.by[(VertexId, Long, Double), Double](_._3))
        if (maxQ._3 > tol)
          maxQ
        else // if entering other communities reduces its modularity, then stays in the current community
          (vid, vCid, 0.0)
      })

    changeInfo //(vid,wCid,△Q)
  }

}

object LouvainGraphUtil {

  /**
    * Construct louvain graph
    *
    * @param initG
    * @return Graph
    */
  def createLouvainGraph(
      initG: Graph[None.type, Double]
  ): Graph[VertexData, Double] = {
    // sum of the weights of the links incident to node i
    val nodeWeights: VertexRDD[Double] = initG.aggregateMessages(
      trip => {
        trip.sendToSrc(trip.attr)
        trip.sendToDst(trip.attr)
      },
      (a, b) => a + b
    )
    // update graph vertex's property
    val louvainG = initG.outerJoinVertices(nodeWeights)((vid, oldData, opt) => {
      val vData   = new VertexData(vid, vid)
      val weights = opt.getOrElse(0.0)
      vData.degree = weights
      vData.innerVertices += vid
      vData
    })
    louvainG
  }

  /**
    * update graph using new community info
    *
    * @param G          Louvain graph
    * @param changeInfo （vid，new_cid）
    *
    * @return Graph[VertexData, Double]
    */
  def updateGraph(
      G: Graph[VertexData, Double],
      changeInfo: RDD[(VertexId, Long)]
  ): Graph[VertexData, Double] = {
    // update community id
    G.joinVertices(changeInfo)((vid, data, newCid) => {
      val vData = new VertexData(vid, newCid)
      vData.innerDegree = data.innerDegree
      vData.innerVertices = data.innerVertices
      vData.degree = data.degree
      vData
    })
  }
}

object CommUtil {
  // return the collections of communities
  def getCommunities(G: Graph[VertexData, Double]): RDD[Row] = {
    val communities = G.vertices
      .map(x => {
        Row(x._1, x._2.cId)
      })
    communities
  }
}

class VertexData(val vId: Long, var cId: Long) extends Serializable {
  var innerDegree   = 0.0
  var innerVertices = new mutable.HashSet[Long]()
  var degree        = 0.0
}

/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.algorithm.lib

import com.vesoft.nebula.tools.algorithm.config.BetweennessConfig
import com.vesoft.nebula.tools.algorithm.utils.NebulaUtil
import org.apache.log4j.Logger
import org.apache.spark.SparkContext
import org.apache.spark.graphx.{Edge, EdgeDirection, EdgeTriplet, Graph, Pregel, VertexId}
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}
import org.apache.spark.sql.types.{DoubleType, LongType, StructField, StructType}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

object BetweennessCentralityAlgo {
  private val LOGGER = Logger.getLogger(this.getClass)

  val ALGORITHM: String = "BetweennessCentrality"

  /**
    * run the BetweennessCentrality algorithm for nebula graph
    */
  def apply(spark: SparkSession,
            dataset: Dataset[Row],
            betweennessConfig: BetweennessConfig,
            hasWeight: Boolean): DataFrame = {

    val graph: Graph[None.type, Double] = NebulaUtil.loadInitGraph(dataset, false)
    val betweennessGraph                = execute(graph, betweennessConfig.maxIter, hasWeight)

    val schema = StructType(
      List(
        StructField("_id", LongType, nullable = false),
        StructField("_betweenness", DoubleType, nullable = true)
      ))
    val resultRDD  = betweennessGraph.vertices.map(vertex => Row(vertex._1, vertex._2))
    val algoResult = spark.sqlContext.createDataFrame(resultRDD, schema)
    algoResult
  }

  /**
    * execute betweenness centrality 算法
    */
  def execute(graph: Graph[None.type, Double],
              k: Integer,
              isWeighted: Boolean): Graph[Double, Double] = {
    val initBCgraph = createBetweenGraph(graph, k)
    //有权图计算方法
    if (isWeighted) {
      val vertexBCGraph = initBCgraph.mapVertices((id, attr) => {
        val a = betweennessCentralityForWeightedGraph(id, attr)
        (id, a)
      })
      val BCGraph = aggregateBetweennessScores(vertexBCGraph)
      BCGraph
    }
    //无权图计算方法
    else {
      val vertexBCGraph = initBCgraph.mapVertices((id, attr) => {
        (id, betweennessCentralityForUnweightedGraph(id, attr))
      })
      val BCGraph = aggregateBetweennessScores(vertexBCGraph)
      BCGraph
    }
  }

  /**
    * betweennessCentralityForUnweightedGraph
    *
    * 对无权图计算顶点的介数中心性
    * 对每个顶点vid计算从该节点出发时其他节点的介数中心性,返回对于顶点vid而言图中节点所计算出来的介数中心性
    *
    * 对图中所有节点都会计算一次全局的介数中心性，最后进行汇总
    *
    * @param vid:顶点id
    * @param vAttr：顶点对应的属性信息
    *
    *@return List((Vid, betweennessValue))
    *
    * */
  def betweennessCentralityForUnweightedGraph(vid: VertexId,
                                              vAttr: VertexProperty): List[(VertexId, Double)] = {
    //无权图的计算方法
    println("enter betweennessCentrality for vertex: " + vid)

    //对图中每个顶点做如下操作
    val S = mutable.Stack[VertexId]() //每次访问过的节点入栈
    val P = new mutable.HashMap[VertexId, ListBuffer[VertexId]]() //存储源顶点到某个顶点中间经过哪些顶点
    //如[5,[2,3]]，表示源顶点到顶点5的最短路径会经过顶点2,3
    val Q           = mutable.Queue[VertexId]() //BFS遍历时将顶点入队列
    val dist        = new mutable.HashMap[VertexId, Double]()
    val sigma       = new mutable.HashMap[VertexId, Double]()
    val delta       = new mutable.HashMap[VertexId, Double]()
    val neighborMap = getNeighborMap(vAttr.vlist, vAttr.elist)
    val medBC       = new ListBuffer[(VertexId, Double)]()

    for (vertex <- vAttr.vlist) {
      dist.put(vertex, -1)
      sigma.put(vertex, 0.0)
      delta.put(vertex, 0.0)
      P.put(vertex, ListBuffer[VertexId]())
    }
    //对于当前节点，有特殊对待
    dist(vid) = 0.0
    sigma(vid) = 1.0
    Q.enqueue(vid)

    while (Q.nonEmpty) {
      val v = Q.dequeue()
      S.push(v)
      for (w <- neighborMap(v)) {
        if (dist(w) < 0) { //节点w未被访问过
          Q.enqueue(w)
          dist(w) = dist(v) + 1
        }
        if (dist(w) == dist(v) + 1) {
          sigma(w) += sigma(v)
          P(w).+=(v)
        }
      }
    }

    while (S.nonEmpty) {
      val w = S.pop()
      for (v <- P(w)) {
        delta(v) += sigma(v) / sigma(w) * (1 + delta(w))
      }
      if (w != vid)
        medBC.append((w, delta(w) / 2)) //一条边会被两个节点各自计算一次，所以需要对求出来的值除以2
    }
    medBC.toList
  }

  /**
    * betweennessCentralityForWeightedGraph
    *
    * 有权图求介数中心性
    *
    * 与无权图求介数中心性的区别在于“存储邻居节点信息”的数据结构不同
    * 有权图不是用队列，而是采用scala的优先级队列PriorityQueue，即最小堆
    * 利用最小堆维护顶点的邻居节点以及与邻居节点的边权重元组：（vw_dist, v, w）其中v是w的前驱节点，即w是v的往深处走的邻居节点
    *      当遍历完顶点v的所有邻居节点后，需要从中选择一个最近的邻居继续进行深度遍历，所以让最小堆根据wv_dist降序排列，
    *      每次pop出来的即是最小边对应的顶点信息，也就是每次选出来的邻居节点都是距离最近的邻居。
    *
    * @param vid:顶点id
    * @param vAttr：顶点对应的属性信息
    *
    *@return List((Vid, betweennessValue))
    * */
  def betweennessCentralityForWeightedGraph(vid: VertexId,
                                            vAttr: VertexProperty): List[(VertexId, Double)] = {
    println("enter betweennessCentralityForWeightedGraph function")
    //对图中每个顶点做如下操作
    val S = mutable.Stack[VertexId]() //每次访问过的节点入栈
    val P = new mutable.HashMap[VertexId, ListBuffer[VertexId]]() //存储源顶点到某个顶点中间经过哪些顶点
    //如[5,[2,3]]，表示源顶点到顶点5的最短路径会经过顶点2,3

    //下面定义一个优先级队列，即最小堆，根据第一个元素进行倒排
    val ord = Ordering.by[(Double, VertexId, VertexId), Double](_._1).reverse
    val Q   = new mutable.PriorityQueue[(Double, VertexId, VertexId)]()(ord) //遍历时将顶点及对应的路径长度入队列

    val dist        = new mutable.HashMap[VertexId, Double]()
    val sigma       = new mutable.HashMap[VertexId, Double]()
    val delta       = new mutable.HashMap[VertexId, Double]()
    val neighborMap = getNeighborMap(vAttr.vlist, vAttr.elist)
    val medBC       = new ListBuffer[(VertexId, Double)]()

    for (vertex <- vAttr.vlist) {
      dist.put(vertex, -1)
      sigma.put(vertex, 0.0)
      delta.put(vertex, 0.0)
      P.put(vertex, ListBuffer[VertexId]())
    }
    //对于当前节点，有特殊对待
    sigma(vid) = 1.0
    val seen = new mutable.HashMap[VertexId, Double]()
    seen(vid) = 0
    Q.enqueue((0.0, vid, vid))

    //获取两个相邻节点之间的距离
    def getDist(v: VertexId, w: VertexId) = {
      vAttr.elist
        .filter(e => (e._1 == v && e._2 == w) || (e._2 == v && e._1 == w))
        .map(x => x._3)
        .reduce(_.min(_))
    }

    while (Q.nonEmpty) {
      val (d, pred, v) = Q.dequeue()
      if (dist(v) > 0) { //节点v已经访问过了
        null
      } else {
        sigma(v) += sigma(pred)
        S.push(v)
        dist(v) = d
        for (w <- neighborMap(v)) {
          val vw_dist = d + getDist(v, w)
          if (dist(w) < 0 && (!seen.contains(w) || vw_dist < seen(w))) {
            seen(w) = vw_dist
            Q.enqueue((vw_dist, v, w))
            sigma(w) = 0.0
            P(w) = ListBuffer[VertexId](v)
          } else if (vw_dist == seen(w)) {
            sigma(w) += sigma(v)
            P(w).+=(v)
          }
        }
      }
    }

    while (S.nonEmpty) {
      val w = S.pop()
      for (v <- P(w)) {
        delta(v) += sigma(v) / sigma(w) * (1 + delta(w))
      }
      if (w != vid)
        medBC.append((w, delta(w) / 2))
    }
    medBC.toList
  }

  /**
    * 为每个顶点收集其邻居节点信息
    * 尝试过用收集邻居节点的api，但在计算介数中心性内部又需要每个节点都维护所有节点信息和边信息，所以采用对每个节点根据边来计算邻居节点的方式
    *
    * @param vlist, elist     所有顶点信息和所有边信息
    * @return [vid, [ 邻居id， 邻居id ...] ]
    * */
  def getNeighborMap(
      vlist: List[VertexId],
      elist: List[(VertexId, VertexId, Double)]): mutable.HashMap[VertexId, List[VertexId]] = {
    val neighborList = new mutable.HashMap[VertexId, List[VertexId]]()
    vlist.map(v => {
      val nlist = (elist
        .filter(e => (e._1 == v || e._2 == v)))
        .map(e => {
          if (v == e._1) e._2
          else e._1
        })
      neighborList.+=((v, nlist.distinct))
    })
    neighborList
  }

  /**
    * 根据原始数据构建初始图
    *
    * @param sc
    * @param path 原始数据所在的hdfs路径
    * @param separator 数据不同字段间的分隔符
    * @param weightCol 用于标识哪一列作为权重列，给出列号，从0开始。
    *                  对权重列的限制： 权重列为-1时，表示没有权重列,即该图是无权图，默认设权重为1.0
    *                                   为非-1的整数时，表示图数据中的第weightCol列为权重列
    *                  要求：其值小于数据中列的数目，且该权重列对应的数据必须是double数值
    *
    * @return Graph
    * */
  def loadInitGraph(sc: SparkContext,
                    path: String,
                    separator: String,
                    weightCol: Int): Graph[None.type, Double] = {
    val data = sc.textFile(path)
    val edges = data.map(line => {
      val items = line.split(separator)
      require(items.length > weightCol,
              "权重列超过了图数据的字段数，图数据字段数目为 " + items.length + ", 选择的权重列为 " + weightCol)
      var weightValue = 0.0
      if (weightCol == -1) {
        weightValue = 1.0
      } else {
        require(isNumic(items(weightCol)), "权重列必须为double数值")
        weightValue = items(weightCol).toDouble
      }
      Edge(items(0).toLong, items(1).toLong, weightValue)
    })
    Graph.fromEdges(edges, None)
  }

  /**
    * 工具方法，验证权重列中的值可以转为double
    * */
  def isNumic(str: String): Boolean = {
    var result = true
    for (s <- str.replaceAll(".", "")) {
      if (!s.isDigit)
        result = false
    }
    result
  }

  /**
    * 构建BetweennessCentrality图，图中顶点属性维护了图中所有顶点id的列表和所有边（srcId， dstId， attr）的列表
    *
    * @param initG 原始数据构造的图
    * @param k  最大迭代次数
    * @return Graph
    * */
  def createBetweenGraph(initG: Graph[None.type, Double], k: Int): Graph[VertexProperty, Double] = {
    val betweenG = initG
      .mapTriplets[Double]({ x: EdgeTriplet[None.type, Double] =>
        x.attr
      })
      .mapVertices((id, attr) => new VertexProperty)
      .cache
    //准备进入pregel前的初始化消息、vertexProgram方法、 sendMessage方法、mergeMessage方法
    val initMessage = (List[VertexId](), List[(VertexId, VertexId, Double)]())
    //将发送过来的邻居节点信息以及当前点与邻居点的边，更新到当前点的属性中
    def vertexProgram(
        id: VertexId,
        attr: VertexProperty,
        msgSum: (List[VertexId], List[(VertexId, VertexId, Double)])): VertexProperty = {
      val newAttr = new VertexProperty()
      newAttr.CB = attr.CB
      newAttr.vlist = (msgSum._1 ++ attr.vlist).distinct
      newAttr.elist = (msgSum._2 ++ attr.elist).distinct
      newAttr
    }
    //向邻居节点发送自身节点的id和自身与邻居点的边
    def sendMessage(edge: EdgeTriplet[VertexProperty, Double])
      : Iterator[(VertexId, (List[VertexId], List[(VertexId, VertexId, Double)]))] = Iterator(
      (edge.dstId,
       (edge.srcId +: edge.srcAttr.vlist,
        (edge.srcId, edge.dstId, edge.attr) +: edge.srcAttr.elist)),
      (edge.srcId,
       (edge.dstId +: edge.dstAttr.vlist,
        (edge.srcId, edge.dstId, edge.attr) +: edge.dstAttr.elist))
    )
    //合并接受到的多条消息
    def mergeMessage(a: (List[VertexId], List[(VertexId, VertexId, Double)]),
                     b: (List[VertexId], List[(VertexId, VertexId, Double)]))
      : (List[VertexId], List[(VertexId, VertexId, Double)]) = {
      ((a._1 ++ b._1).distinct, (a._2 ++ b._2).distinct)
    }

    Pregel(betweenG, initMessage, k, EdgeDirection.Either)(vertexProgram, sendMessage, mergeMessage)
  }

  /**
    * 将每个节点分别计算出来的BC值进行统计
    * */
  def aggregateBetweennessScores(
      BCgraph: Graph[(VertexId, List[(VertexId, Double)]), Double]): Graph[Double, Double] = {
    //将图中顶点属性所维护的listBC信息单独提取出来
    val BCaggregate = BCgraph.vertices.flatMap {
      case (v, (id, listBC)) => {
        listBC.map { case (w, bc) => (w, bc) }
      }
    }
    //对BCaggregate的信息 （w, bc）根据顶点id做汇总
    val vertexBC = BCaggregate.reduceByKey(_ + _)
    val resultG = BCgraph.outerJoinVertices(vertexBC)((vid, oldAttr, vBC) => {
      vBC.getOrElse(0.0)
    })
    resultG
  }
}

/**
  * 定义顶点的属性类
  * CB: 定义初始的betweennessCentrality
  * vlist： 每个顶点需维护图中所有的顶点信息
  * elist: 每个顶点需维护图中所有的边信息
  *
  * 维护所有边信息是为了在计算介数中心性的时候可以从每个顶点依次根据邻居节点走下去（空间复杂度很高，O(n2)）
  * */
class VertexProperty() extends Serializable {
  var CB    = 0.0
  var vlist = List[VertexId]()
  var elist = List[(VertexId, VertexId, Double)]()
}

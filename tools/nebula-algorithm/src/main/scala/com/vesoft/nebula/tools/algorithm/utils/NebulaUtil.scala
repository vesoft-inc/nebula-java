/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.algorithm.utils

import java.util

import com.google.common.net.HostAndPort
import com.vesoft.nebula.bean.Parameters
import com.vesoft.nebula.client.meta.MetaClientImpl
import com.vesoft.nebula.common.Type
import com.vesoft.nebula.tools.connector.{ConnectionException, NebulaUtils}
import org.apache.spark.graphx.{Edge, Graph}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Dataset, Encoder, Row, SparkSession}
import org.slf4j.LoggerFactory

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

object NebulaUtil {
  private val LOG = LoggerFactory.getLogger(this.getClass)

  /**
    * construct original graph
    *
    * @param hasWeight if the graph has no weight, then edge's weight is default 1.0
    *
    * @return Graph
    */
  def loadInitGraph(dataSet: Dataset[Row], hasWeight: Boolean): Graph[None.type, Double] = {

    implicit val encoder: Encoder[Edge[Double]] = org.apache.spark.sql.Encoders.kryo[Edge[Double]]
    val edges: RDD[Edge[Double]] = dataSet
      .map(row => {
        if (hasWeight) {
          Edge(row.get(0).toString.toLong, row.get(1).toString.toLong, row.get(2).toString.toDouble)
        } else {
          Edge(row.get(0).toString.toLong, row.get(1).toString.toLong, 1.0)
        }
      })(encoder)
      .rdd

    Graph.fromEdges(edges, None)
  }

  /**
    * scan nebula edge data
    *
    * @param spark sparkSession
    * @param sparkPartition spark partition number
    * @param hostPorts nebula address
    * @param partitionNumber nebula space partition
    * @param nameSpace nebula space
    * @param labels edge labels to construct graph
    * @param hasWeight true or false
    * @param weightCols weight col for all labels, if hasWeight is false, weightCols can be ignored
    *
    * @return union of labels' dataset
    */
  def scanEdgeData(spark: SparkSession,
                   sparkPartition: String,
                   hostPorts: String,
                   partitionNumber: String,
                   nameSpace: String,
                   labels: List[String],
                   hasWeight: Boolean,
                   weightCols: List[String]): Dataset[Row] = {
    // check the params
    val labelAndWeight = validate(hostPorts, nameSpace, labels, hasWeight, weightCols)

    val label = labels.head
    var dataset = spark.read
      .format("nebula")
      .option(Parameters.TYPE, Type.EDGE.getType)
      .option(Parameters.HOST_AND_PORTS, hostPorts)
      .option(Parameters.PARTITION_NUMBER, partitionNumber)
      .option(Parameters.SPACE_NAME, nameSpace)
      .option(Parameters.LABEL, label)
      .option(Parameters.RETURN_COLS, labelAndWeight(label))
      .load()
    if (!hasWeight) {
      dataset = dataset.select("_srcId", "_dstId")
    }

    labels.tail.foreach(edge => {
      dataset = dataset.union {
        var anotherDataset = spark.read
          .format("nebula")
          .option(Parameters.TYPE, Type.EDGE.getType)
          .option(Parameters.HOST_AND_PORTS, hostPorts)
          .option(Parameters.PARTITION_NUMBER, partitionNumber)
          .option(Parameters.SPACE_NAME, nameSpace)
          .option(Parameters.LABEL, edge)
          .option(Parameters.RETURN_COLS, labelAndWeight(edge))
          .load()
        if (!hasWeight) {
          anotherDataset = anotherDataset.select("_srcId", "_dstId")
        }
        anotherDataset
      }
    })
    if (sparkPartition.equals("0")) {
      dataset
    } else {
      dataset.repartition(sparkPartition.toInt)
    }
  }

  /**
    * convert String host and ports to List[HostAndPort]
    *
    * @param hostPorts
    *
    * @return
    */
  def hostAndPorts(hostPorts: String): util.List[HostAndPort] = {
    val hostAndPortList: util.List[HostAndPort] = new util.ArrayList[HostAndPort]
    val hostAndPortArray: Array[String]         = hostPorts.split(",")
    for (hostAndPort <- hostAndPortArray) {
      hostAndPortList.add(HostAndPort.fromString(hostAndPort))
    }
    hostAndPortList
  }

  /**
    * validate the nebula configuration
    * if labels and weightCols must be corresponding
    *
    * @param hostPorts
    * @param nameSpace
    * @param labels
    * @param hasWeight
    * @param weightCols
    *
    * @return label and weightcol map
    */
  def validate(hostPorts: String,
               nameSpace: String,
               labels: List[String],
               hasWeight: Boolean,
               weightCols: List[String]): mutable.Map[String, String] = {
    // check hostPorts
    val hostAndPortList  = new ListBuffer[HostAndPort]
    val hostAndPortArray = hostPorts.split(",")
    for (hostAndPort <- hostAndPortArray) {
      hostAndPortList.append(HostAndPort.fromString(hostAndPort))
    }
    var metaClient: MetaClientImpl = null
    metaClient = NebulaUtils.createMetaClient(hostAndPortList.toList)
    if (metaClient == null) {
      LOG.error(s" configuration for nebula.address=${hostPorts} cannot connect nebula service.")
      throw ConnectionException("failed to connect nebula service")
    }

    // check whether nameSpace exists
    var existSpace = false
    import scala.collection.JavaConverters._
    for (spaceNameID <- metaClient.listSpaces().asScala) {
      if (spaceNameID.getName.equals(nameSpace)) {
        existSpace = true
      }
    }
    if (!existSpace) {
      LOG.error(s" Space=${nameSpace} doesn't exist. ")
      throw new IllegalArgumentException(s"space $nameSpace doesn't exist in nebula database.")
    }

    // check if all edge labels exist and weight col match label
    if (hasWeight && labels.size != weightCols.size) {
      LOG.error(
        s" size of configuration nebula.labels=${labels.size} do not match nebula.weightCols=${weightCols.size}. ")
      throw new IllegalArgumentException(
        s"wrong labels and weightCols configuration, sizes mush be the same.")
    }

    // check if all weightCols' dataTypes are the same (不同label 的权重列的数据类型必须相同)
    val labelAndWeight = mutable.Map[String, String]()
    for (i <- 0 until labels.size) {
      if (hasWeight) {
        labelAndWeight += (labels(i) -> weightCols(i))
      } else {
        labelAndWeight += (labels(i) -> "")
      }
    }
    val dataTypes: ListBuffer[Class[_]] = new ListBuffer[Class[_]]()
    import scala.collection.JavaConverters._
    if (hasWeight) {
      for (label <- labels) {
        dataTypes.append(metaClient.getEdgeSchema(nameSpace, label).asScala(labelAndWeight(label)))
      }
      if (dataTypes.toSet.size > 1) {
        LOG.error(" configuration nebula.weightCols has different dataTypes.")
        throw new IllegalArgumentException(
          s"weightCols must has the same dataType in nebula database, weightCols' data types: ${dataTypes.toList}")
      }
    }

    labelAndWeight
  }

  /**
    * Assembly algorithm's result file path
    *
    * @param path algorithm configuration
    * @param algorithmName
    *
    * @return validate result path
    */
  def getResultPath(path: String, algorithmName: String): String = {
    var resultFilePath = path
    if (!resultFilePath.endsWith("/")) {
      resultFilePath = resultFilePath + "/"
    }
    resultFilePath + algorithmName
  }

}

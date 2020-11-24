/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.importer.test.mock

import com.typesafe.config.impl.ConfigImpl.fromAnyRef
import com.vesoft.nebula.tools.importer.KeyPolicy
import com.vesoft.nebula.tools.importer.config.{
  EdgeConfigEntry,
  FileBaseSourceConfigEntry,
  NebulaSinkConfigEntry,
  SinkCategory,
  SourceCategory,
  TagConfigEntry
}
import org.apache.spark.sql.{DataFrame, SparkSession}

import scala.collection.immutable

object Spark {
  private val master  = "local[1]"
  private val appName = "exchange_testing"
  val sparkSession: SparkSession =
    new SparkSession.Builder().appName(appName).master(master).getOrCreate()
  sparkSession.sparkContext.setLogLevel("warn")
}

object MockGraphData {

  import Spark.sparkSession.implicits._

  private val numberVertex     = 5
  private val numberEdgeDegree = 1

  val policyList = List(Some(KeyPolicy.HASH), Some(KeyPolicy.UUID), None)
  val dataSourceConfig: FileBaseSourceConfigEntry =
    FileBaseSourceConfigEntry(SourceCategory.TEXT, "")
  val dataSinkConfig: NebulaSinkConfigEntry = NebulaSinkConfigEntry(SinkCategory.CLIENT, List(""))

  private def genRow(id: Long) = (id, id.toString, id + 0.01, id % 2 == 0)

  val vertexTypeName = "tagA"

  val edgeTypeName = "edgeAA"

  val propertyFieldName = List("idInt", "idString", "tDouble", "tBoolean")

  val vertexFieldName: List[String] = propertyFieldName

  val edgeFieldName: List[String] = List("from.idInt", "to.idInt") ::: vertexFieldName

  val vertexIdFieldName = "idInt"

  val edgeRankFieldName = "idInt"

  val edgeFromFieldName = "from.idInt"

  val edgeToFieldName = "to.idInt"

  val vertexData: Seq[(Long, String, Double, Boolean)] =
    Seq(for (ids <- Range(0, numberVertex)) yield genRow(ids)).flatMap(_.toList)

  val vertexDataIdString: Seq[(String, String, Double, Boolean)] =
    vertexData.map(x => (x._1.toString, x._2, x._3, x._4))

  val edgeData: Seq[(Long, Long, Long, String, Double, Boolean)] = {
    val fromVertexId = Range(0, numberVertex).map(_.toLong).toList
    var toVertexId   = Range(0, numberVertex).map(_.toLong).toList
    var id           = numberVertex
    for (_ <- Range(0, numberEdgeDegree)) yield {
      toVertexId = toVertexId.last :: toVertexId.init
      for (ids <- Range(0, numberVertex)) yield {
        val property = genRow(id)
        id += 1
        (fromVertexId(ids), toVertexId(ids), property._1, property._2, property._3, property._4)
      }
    }
  }.flatMap(_.toList).toList

  private val edgeDataIdString00: Seq[(Long, Long, Long, String, Double, Boolean)] = edgeData
  private val edgeDataIdString01: Seq[(Long, String, Long, String, Double, Boolean)] =
    edgeData.map(x => (x._1, x._2.toString, x._3, x._4, x._5, x._6))
  private val edgeDataIdString10: Seq[(String, Long, Long, String, Double, Boolean)] =
    edgeData.map(x => (x._1.toString, x._2, x._3, x._4, x._5, x._6))
  private val edgeDataIdString11: Seq[(String, String, Long, String, Double, Boolean)] =
    edgeData.map(x => (x._1.toString, x._2.toString, x._3, x._4, x._5, x._6))

  def vertexDataFrame: DataFrame = vertexData.toDF(vertexFieldName: _*)

  def vertexDataFrame(vertex: Option[KeyPolicy.Value]): DataFrame =
    if (vertex.isEmpty) vertexDataFrame else vertexDataIdString.toDF(vertexFieldName: _*)

  def edgeDataFrame: DataFrame = edgeData.toDF(edgeFieldName: _*)

  def edgeDataFrame(source: Option[KeyPolicy.Value], target: Option[KeyPolicy.Value]): DataFrame = {
    {
      if (source.isEmpty) {
        if (target.isEmpty) edgeDataIdString00.toDF()
        else edgeDataIdString01.toDF()
      } else {
        if (target.isEmpty) edgeDataIdString10.toDF()
        else edgeDataIdString11.toDF()
      }
    }.toDF(edgeFieldName: _*)
  }

  private def getVertexIDTemplateFromKeyPolicy(keyPolicy: Option[KeyPolicy.Value]) =
    keyPolicy match {
      case Some(KeyPolicy.HASH) => "hash(\"%d\")"
      case Some(KeyPolicy.UUID) => "uuid(\"%d\")"
      case _                    => "%d"
    }

  def createInsertVertexSentence(vertexPolicy: Option[KeyPolicy.Value]): String = {
    val vertexIdTemplate = getVertexIDTemplateFromKeyPolicy(vertexPolicy)
    val s                = if (vertexPolicy.isEmpty) "" else "\""

    s"INSERT VERTEX ${vertexTypeName}(idInt,idString,tDouble,tBoolean) VALUES " +
      s"${vertexIdTemplate.format(0)}: (${s}0${s}, ${'"'}0${'"'}, 0.01, true), " +
      s"${vertexIdTemplate.format(1)}: (${s}1${s}, ${'"'}1${'"'}, 1.01, false), " +
      s"${vertexIdTemplate.format(2)}: (${s}2${s}, ${'"'}2${'"'}, 2.01, true), " +
      s"${vertexIdTemplate.format(3)}: (${s}3${s}, ${'"'}3${'"'}, 3.01, false), " +
      s"${vertexIdTemplate.format(4)}: (${s}4${s}, ${'"'}4${'"'}, 4.01, true)"
  }

  def createInsertEdgeSentence(fromVertexPolicy: Option[KeyPolicy.Value],
                               toVertexPolicy: Option[KeyPolicy.Value],
                               hasRank: Boolean): String = {
    val from     = getVertexIDTemplateFromKeyPolicy(fromVertexPolicy)
    val to       = getVertexIDTemplateFromKeyPolicy(toVertexPolicy)
    val rankList = for (i <- Range(5, 10)) yield if (hasRank) s"@${i}" else ""

    s"INSERT EDGE ${edgeTypeName}(idInt,idString,tDouble,tBoolean) VALUES " +
      s"${from.format(0)}->${to.format(4)}${rankList(0)}: (5, ${'"'}5${'"'}, 5.01, false), " +
      s"${from.format(1)}->${to.format(0)}${rankList(1)}: (6, ${'"'}6${'"'}, 6.01, true), " +
      s"${from.format(2)}->${to.format(1)}${rankList(2)}: (7, ${'"'}7${'"'}, 7.01, false), " +
      s"${from.format(3)}->${to.format(2)}${rankList(3)}: (8, ${'"'}8${'"'}, 8.01, true), " +
      s"${from.format(4)}->${to.format(3)}${rankList(4)}: (9, ${'"'}9${'"'}, 9.01, false)"
  }
}

class MockGraphDataVertex(vertexPolicy: Option[KeyPolicy.Value] = None) {

  val vertexDataFrame: DataFrame = MockGraphData.vertexDataFrame(vertexPolicy)
  val tagConfig: TagConfigEntry = {
    val fields = MockGraphData.propertyFieldName
      .map(property => {
        (property, fromAnyRef(property, ""))
      })
      .toMap

    // TODO fields
    TagConfigEntry(
      MockGraphData.vertexTypeName,
      MockGraphData.dataSourceConfig,
      MockGraphData.dataSinkConfig,
      Nil,
      Nil,
      MockGraphData.vertexIdFieldName,
      vertexPolicy,
      MockGraphData.vertexData.size,
      1,
      None,
      false
    )
  }

  val insertVertexSentence: String = MockGraphData.createInsertVertexSentence(vertexPolicy)

}

class MockGraphDataEdge(edgeSourcePolicy: Option[KeyPolicy.Value] = None,
                        edgeTargetPolicy: Option[KeyPolicy.Value] = None,
                        rank: Boolean = false) {

  val edgeDataFrame: DataFrame =
    MockGraphData.edgeDataFrame(edgeSourcePolicy, edgeTargetPolicy)

  val edgeConfig: EdgeConfigEntry = {
    val fields = MockGraphData.propertyFieldName
      .map(property => {
        (property, fromAnyRef(property, ""))
      })
      .toMap
    val rankingField = if (rank) Some(MockGraphData.edgeRankFieldName) else None

    // TODO fields
    EdgeConfigEntry(
      MockGraphData.edgeTypeName,
      MockGraphData.dataSourceConfig,
      MockGraphData.dataSinkConfig,
      Nil,
      Nil,
      MockGraphData.edgeFromFieldName,
      edgeSourcePolicy,
      rankingField,
      MockGraphData.edgeToFieldName,
      edgeTargetPolicy,
      isGeo = false,
      None,
      None,
      MockGraphData.edgeData.size,
      1,
      None,
      true
    )
  }

  val insertEdgeSentence: String =
    MockGraphData.createInsertEdgeSentence(edgeSourcePolicy, edgeTargetPolicy, rank)

}

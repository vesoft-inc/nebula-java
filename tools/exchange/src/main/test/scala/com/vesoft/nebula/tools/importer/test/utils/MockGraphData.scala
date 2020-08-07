package com.vesoft.nebula.tools.importer.test.utils

import com.typesafe.config.impl.ConfigImpl.fromAnyRef
import com.vesoft.nebula.tools.importer.{
  EdgeConfigEntry,
  FileBaseSourceConfigEntry,
  KeyPolicy,
  NebulaSinkConfigEntry,
  SinkCategory,
  SourceCategory,
  TagConfigEntry
}
import org.apache.spark.sql.{DataFrame, SparkSession}

import scala.collection.immutable

object SparkSessionObject {
  private val master  = "local[*]"
  private val appName = "data_load_testing"
  val sparkSession: SparkSession =
    new SparkSession.Builder().appName(appName).master(master).getOrCreate()
  sparkSession.sparkContext.setLogLevel("warn")
}

object MockGraphData {
  import SparkSessionObject.sparkSession.implicits._

  private val numberVertex     = 5
  private val numberEdgeDegree = 1
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

  val vertexDataFrame: DataFrame = vertexData.toDF(vertexFieldName: _*)

  val edgeDataFrame: DataFrame = edgeData.toDF(edgeFieldName: _*)

  private def getVertexIDTemplateFromKeyPolicy(keyPolicy: Option[KeyPolicy.Value]) =
    keyPolicy match {
      case Some(KeyPolicy.HASH) => "hash(\"%d\")"
      case Some(KeyPolicy.UUID) => "uuid(\"%d\")"
      case None                 => "%d"
    }

  private val q = "\""

  def createInsertVertexSentence(vertexPolicy: Option[KeyPolicy.Value]): String = {
    val vertexIdTemplate = getVertexIDTemplateFromKeyPolicy(vertexPolicy)

    s"INSERT VERTEX ${vertexTypeName}(idInt,idString,tDouble,tBoolean) VALUES " +
      s"${vertexIdTemplate.format(0)}: (0, ${q}0${q}, 0.01, true), " +
      s"${vertexIdTemplate.format(1)}: (1, ${q}1${q}, 1.01, false), " +
      s"${vertexIdTemplate.format(2)}: (2, ${q}2${q}, 2.01, true), " +
      s"${vertexIdTemplate.format(3)}: (3, ${q}3${q}, 3.01, false), " +
      s"${vertexIdTemplate.format(4)}: (4, ${q}4${q}, 4.01, true)"
  }

  def createInsertEdgeSentence(fromVertexPolicy: Option[KeyPolicy.Value],
                               toVertexPolicy: Option[KeyPolicy.Value],
                               hasRank: Boolean): String = {
    val from     = getVertexIDTemplateFromKeyPolicy(fromVertexPolicy)
    val to       = getVertexIDTemplateFromKeyPolicy(toVertexPolicy)
    val rankList = for (i <- Range(5, 10)) yield if (hasRank) s"@${i}" else ""

    s"INSERT EDGE ${edgeTypeName}(idInt,idString,tDouble,tBoolean) VALUES " +
      s"${from.format(0)}->${to.format(4)}${rankList(0)}: (5, ${q}5${q}, 5.01, false), " +
      s"${from.format(1)}->${to.format(0)}${rankList(1)}: (6, ${q}6${q}, 6.01, true), " +
      s"${from.format(2)}->${to.format(1)}${rankList(2)}: (7, ${q}7${q}, 7.01, false), " +
      s"${from.format(3)}->${to.format(2)}${rankList(3)}: (8, ${q}8${q}, 8.01, true), " +
      s"${from.format(4)}->${to.format(3)}${rankList(4)}: (9, ${q}9${q}, 9.01, false)"
  }
}

class MockGraphDataVertex(vertexPolicy: Option[KeyPolicy.Value] = None) {

  val tagConfig: TagConfigEntry = {
    val fields = MockGraphData.propertyFieldName
      .map(property => {
        (property, fromAnyRef(property, ""))
      })
      .toMap
    TagConfigEntry(
      MockGraphData.vertexTypeName,
      MockGraphData.dataSourceConfig,
      MockGraphData.dataSinkConfig,
      fields,
      MockGraphData.vertexIdFieldName,
      vertexPolicy,
      1,
      1,
      None
    )
  }

  val insertVertexSentence: String = MockGraphData.createInsertVertexSentence(vertexPolicy)

}

class MockGraphDataEdge(edgeSourcePolicy: Option[KeyPolicy.Value] = None,
                        edgeTargetPolicy: Option[KeyPolicy.Value] = None,
                        rank: Boolean = false) {

  val edgeConfig: EdgeConfigEntry = {
    val fields = MockGraphData.propertyFieldName
      .map(property => {
        (property, fromAnyRef(property, ""))
      })
      .toMap
    val rankingField = if (rank) Some(MockGraphData.edgeRankFieldName) else None
    EdgeConfigEntry(
      MockGraphData.edgeTypeName,
      MockGraphData.dataSourceConfig,
      MockGraphData.dataSinkConfig,
      fields,
      MockGraphData.edgeFromFieldName,
      edgeSourcePolicy,
      rankingField,
      MockGraphData.edgeToFieldName,
      edgeTargetPolicy,
      isGeo = false,
      None,
      None,
      1,
      1,
      None
    )
  }

  val insertEdgeSentence: String =
    MockGraphData.createInsertEdgeSentence(edgeSourcePolicy, edgeTargetPolicy, rank)

}
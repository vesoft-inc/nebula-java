/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.importer.test

import com.google.common.base.Optional
import com.google.common.util.concurrent.ListenableFuture
import com.vesoft.nebula.tools.importer
import com.vesoft.nebula.tools.importer.{Edge, Edges, KeyPolicy, Vertex, Vertices}
import com.vesoft.nebula.tools.importer.writer.ServerBaseWriter
import org.scalatest.funsuite.AnyFunSuite
import com.vesoft.nebula.tools.importer.processor.Processor
import mock.{MockGraphData, MockGraphDataEdge, MockGraphDataVertex}

class TestCreateSentence extends AnyFunSuite {

  private val serverBaseWriter = new ServerBaseWriter {
    override def prepare(): Unit = ???

    override def writeVertices(vertices: importer.Vertices): ListenableFuture[Optional[Integer]] =
      ???

    override def writeEdges(edges: importer.Edges): ListenableFuture[Optional[Integer]] = ???

    override def close(): Unit = ???
  }

  private val processor = new Processor {
    override def process(): Unit = ???
  }

  test("test toExecuteSentence") {

    val propertyNames = List("idInt", "idString")
    val values        = Range(0, 2).map(i => Vertex(i.toString, List(i, i.toString))).toList
    val vertices      = Vertices(propertyNames, values)

    println(serverBaseWriter.toExecuteSentence("tagA", vertices))
  }

  test("test vertex extraValue") {
    val vertexList = MockGraphData.vertexDataFrame
      .collect()
      .map(row => {
        val vertexID = row.get(row.schema.fieldIndex(MockGraphData.vertexIdFieldName)).toString
        val values = for {
          property <- MockGraphData.vertexFieldName
        } yield processor.extraValue(row, property)
        Vertex(vertexID, values)
      })
      .toList
    for (vertexPolicy <- MockGraphData.policyList) {
      val mockGraphData = new MockGraphDataVertex(vertexPolicy)
      val vertices      = Vertices(MockGraphData.propertyFieldName, vertexList, None, vertexPolicy)
      println(serverBaseWriter.toExecuteSentence("tagA", vertices))
      assertResult(mockGraphData.insertVertexSentence) {
        serverBaseWriter.toExecuteSentence(MockGraphData.vertexTypeName, vertices)
      }
    }
  }

  test("test edge extraValue") {
    val edgeList = MockGraphData.edgeDataFrame
      .collect()
      .map(row => {
        val sourceField = row.get(row.schema.fieldIndex(MockGraphData.edgeFromFieldName)).toString
        val targetField = row.get(row.schema.fieldIndex(MockGraphData.edgeToFieldName)).toString

        val values = (for {
          property <- MockGraphData.propertyFieldName
        } yield processor.extraValue(row, property))

        val rank = row.getLong(row.schema.fieldIndex(MockGraphData.edgeRankFieldName))

        (Edge(sourceField, targetField, None, values),
         Edge(sourceField, targetField, Some(rank), values))
      })
      .toList
    val edgeListWithoutRank = edgeList.map(_._1)
    val edgeListWithRank    = edgeList.map(_._2)

    for (fromPolicy <- MockGraphData.policyList)
      for (toPolicy <- MockGraphData.policyList)
        for ((edgeListRank, hasRank) <- List(edgeListWithoutRank, edgeListWithRank).zip(
               List(false, true))) {
          val edges =
            Edges(MockGraphData.propertyFieldName, edgeListRank, None, fromPolicy, toPolicy)
          val mockGraphDataEdge = new MockGraphDataEdge(fromPolicy, toPolicy, hasRank)
          println(serverBaseWriter.toExecuteSentence(MockGraphData.edgeTypeName, edges))
          assertResult(mockGraphDataEdge.insertEdgeSentence) {
            serverBaseWriter.toExecuteSentence(MockGraphData.edgeTypeName, edges)
          }
        }
  }
}

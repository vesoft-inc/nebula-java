/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.importer.test

import com.vesoft.nebula.tools.importer.UserConfigEntry
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funsuite.AnyFunSuite
import com.vesoft.nebula.tools.importer.processor.{EdgeProcessor, VerticesProcessor}
import com.vesoft.nebula.tools.importer.test.mock.{
  MockConfigs,
  MockGraphData,
  MockGraphDataEdge,
  MockGraphDataVertex,
  MockQueryProcessor,
  MockQueryServer,
  SparkSessionObject
}
import org.apache.spark.SparkException

class TestProcessor extends AnyFunSuite with BeforeAndAfterAll {

  val mockServerPort: Int = MockConfigs.port

  val mockQueryProcessor = new MockQueryProcessor

  val mockServer = new MockQueryServer(mockQueryProcessor, mockServerPort)

  override protected def beforeAll(): Unit = {
    mockServer.start()
    mockServer.waitUntilStarted()
  }

  test("test processor vertex") {
    val batchSuccess = SparkSessionObject.sparkSession.sparkContext
      .longAccumulator(s"batchSuccess.${MockGraphData.vertexTypeName}")
    val batchFailure = SparkSessionObject.sparkSession.sparkContext
      .longAccumulator(s"batchFailure.${MockGraphData.vertexTypeName}")
    for (vertexPolicy <- MockGraphData.policyList) {
      val mockGraphData = new MockGraphDataVertex(vertexPolicy)
      val verticesProcessor = new VerticesProcessor(
        mockGraphData.vertexDataFrame,
        mockGraphData.tagConfig,
        MockGraphData.vertexFieldName,
        MockGraphData.vertexFieldName,
        MockConfigs.configs,
        batchSuccess,
        batchFailure
      )
      verticesProcessor.process()
      println(mockQueryProcessor.queryStatement)
      assertResult(mockGraphData.insertVertexSentence) {
        mockQueryProcessor.queryStatement
      }
      assertResult(1) {
        batchSuccess.value
      }
      assertResult(0) {
        batchFailure.value
      }
      batchSuccess.reset()
      batchFailure.reset()
    }
  }

  test("test processor edge") {
    val batchSuccess = SparkSessionObject.sparkSession.sparkContext
      .longAccumulator(s"batchSuccess.${MockGraphData.edgeTypeName}")
    val batchFailure = SparkSessionObject.sparkSession.sparkContext
      .longAccumulator(s"batchFailure.${MockGraphData.edgeTypeName}")

    for (fromPolicy <- MockGraphData.policyList)
      for (toPolicy <- MockGraphData.policyList)
        for (hasRank <- List(true, false)) {
          val mockGraphDataEdge = new MockGraphDataEdge(fromPolicy, toPolicy, hasRank)
          val edgeProcessor = new EdgeProcessor(
            mockGraphDataEdge.edgeDataFrame,
            mockGraphDataEdge.edgeConfig,
            MockGraphData.propertyFieldName,
            MockGraphData.propertyFieldName,
            MockConfigs.configs,
            batchSuccess,
            batchFailure
          )
          edgeProcessor.process()
          println(mockQueryProcessor.queryStatement)
          assertResult(mockGraphDataEdge.insertEdgeSentence) {
            mockQueryProcessor.queryStatement
          }
          assertResult(1) {
            batchSuccess.value
          }
          assertResult(0) {
            batchFailure.value
          }
          batchSuccess.reset()
          batchFailure.reset()
        }
  }

  test("test execute error") {
    val batchSuccess = SparkSessionObject.sparkSession.sparkContext
      .longAccumulator(s"batchSuccess.${MockGraphData.vertexTypeName}")
    val batchFailure = SparkSessionObject.sparkSession.sparkContext
      .longAccumulator(s"batchFailure.${MockGraphData.vertexTypeName}")

    val successCount = 10
    val failureCount = 10

    val mockGraphDataVertex = new MockGraphDataVertex(None)
    mockQueryProcessor.countDownLatchFailOfInsert = successCount
    val dataFrameVertex = Range(0, successCount + failureCount)
      .map(_ => mockGraphDataVertex.vertexDataFrame)
      .reduce(_.union(_))
    val verticesProcessor = new VerticesProcessor(
      dataFrameVertex,
      mockGraphDataVertex.tagConfig,
      MockGraphData.vertexFieldName,
      MockGraphData.vertexFieldName,
      MockConfigs.configs,
      batchSuccess,
      batchFailure
    )
    verticesProcessor.process()
    println(s"batchSuccess: ${batchSuccess.value}, batchFailure: ${batchFailure.value}")
    assertResult(successCount.toLong) {
      batchSuccess.value
    }
    assertResult(failureCount.toLong) {
      batchFailure.value
    }
    batchSuccess.reset()
    batchFailure.reset()

    val mockGraphDataEdge = new MockGraphDataEdge()
    mockQueryProcessor.countDownLatchFailOfInsert = successCount
    val dataFrameEdge = Range(0, successCount + failureCount)
      .map(_ => mockGraphDataEdge.edgeDataFrame)
      .reduce(_.union(_))
    val edgeProcessor = new EdgeProcessor(
      dataFrameEdge,
      mockGraphDataEdge.edgeConfig,
      MockGraphData.propertyFieldName,
      MockGraphData.propertyFieldName,
      MockConfigs.configs,
      batchSuccess,
      batchFailure
    )
    edgeProcessor.process()
    println(s"batchSuccess: ${batchSuccess.value}, batchFailure: ${batchFailure.value}")
    assertResult(successCount.toLong) {
      batchSuccess.value
    }
    assertResult(failureCount.toLong) {
      batchFailure.value
    }
    batchSuccess.reset()
    batchFailure.reset()

    mockQueryProcessor.resetLatch()
  }

  test("test config error") {

    val batchSuccess = SparkSessionObject.sparkSession.sparkContext
      .longAccumulator(s"batchSuccess.${MockGraphData.vertexTypeName}")
    val batchFailure = SparkSessionObject.sparkSession.sparkContext
      .longAccumulator(s"batchFailure.${MockGraphData.vertexTypeName}")
    val configs = MockConfigs.configs.copy(userConfig = UserConfigEntry("abc", "abc"))

    val mockGraphData = new MockGraphDataVertex()

    // test user password wrong
    assertThrows[SparkException] {
      val verticesProcessor = new VerticesProcessor(
        mockGraphData.vertexDataFrame,
        mockGraphData.tagConfig,
        MockGraphData.vertexFieldName,
        MockGraphData.vertexFieldName,
        configs,
        batchSuccess,
        batchFailure
      )
      verticesProcessor.process()
    }

    // test use space sentence failure
    assertThrows[SparkException] {
      mockQueryProcessor.countDownLatchFailOfSentence = 0
      val verticesProcessor = new VerticesProcessor(
        mockGraphData.vertexDataFrame,
        mockGraphData.tagConfig,
        MockGraphData.vertexFieldName,
        MockGraphData.vertexFieldName,
        MockConfigs.configs,
        batchSuccess,
        batchFailure
      )
      verticesProcessor.process()
    }
    mockQueryProcessor.resetLatch()
  }

  override protected def afterAll(): Unit = {
    mockServer.stopServer()
    SparkSessionObject.sparkSession.close()
  }
}

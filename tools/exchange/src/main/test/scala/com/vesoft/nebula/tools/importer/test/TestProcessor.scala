package com.vesoft.nebula.tools.importer.test

import org.scalatest.BeforeAndAfterAll
import org.scalatest.funsuite.AnyFunSuite
import com.vesoft.nebula.tools.importer.processor.{EdgeProcessor, VerticesProcessor}
import com.vesoft.nebula.tools.importer.test.mock.{MockConfigs, MockGraphData, MockGraphDataEdge, MockGraphDataVertex, MockQueryProcessor, MockQueryServer, SparkSessionObject}

class TestProcessor extends AnyFunSuite with BeforeAndAfterAll {

  val mockServerPort: Int = MockConfigs.port

  val mockQueryProcessor = new MockQueryProcessor

  val mockServer = new MockQueryServer(mockQueryProcessor, mockServerPort)


  override protected def beforeAll(): Unit = {
    mockServer.start()
    mockServer.waitUntilStarted()
  }

  test("processor vertex") {
    val batchSuccess = SparkSessionObject.sparkSession.sparkContext
      .longAccumulator(s"batchSuccess.${MockGraphData.vertexTypeName}")
    val batchFailure = SparkSessionObject.sparkSession.sparkContext
      .longAccumulator(s"batchFailure.${MockGraphData.vertexTypeName}")
    for (vertexPolicy <- MockGraphData.policyList) {
      batchSuccess.reset()
      batchFailure.reset()
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
      assertResult(mockGraphData.insertVertexSentence){
        mockQueryProcessor.queryStatement
      }
    }
  }

  test("processor edge"){
    val batchSuccess = SparkSessionObject.sparkSession.sparkContext
      .longAccumulator(s"batchSuccess.${MockGraphData.vertexTypeName}")
    val batchFailure = SparkSessionObject.sparkSession.sparkContext
      .longAccumulator(s"batchFailure.${MockGraphData.vertexTypeName}")

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
          assertResult(mockGraphDataEdge.insertEdgeSentence){
            mockQueryProcessor.queryStatement
          }
        }
  }


  override protected def afterAll(): Unit = {
    mockServer.stopServer()
    SparkSessionObject.sparkSession.close()
  }
}

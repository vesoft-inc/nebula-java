/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools

import java.util.Map.Entry

import com.vesoft.nebula.tools.connector.NebulaDataFrameReader
import com.vesoft.nebula.tools.connector.reader.NebulaRelationProvider
import com.vesoft.nebula.tools.connector.writer.NebulaBatchWriter
import org.apache.spark.graphx.{Edge, VertexId}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.catalyst.util.CaseInsensitiveMap
import org.apache.spark.sql.{Encoder, Encoders}
import org.apache.spark.sql.{DataFrame, DataFrameReader, DataFrameWriter, Row}

import scala.collection.mutable.ListBuffer

package object connector {

  type Address      = (String, Int)
  type EdgeRank     = Int
  type Prop         = List[Any]
  type VertexID     = Long
  type NebulaVertex = (VertexId, Prop)
  type NebulaEdge   = Edge[(EdgeRank, Prop)]
  type NebulaType   = Int

  implicit class NebulaDataFrameReader(reader: DataFrameReader) {
    var address: String      = _
    var space: String        = _
    var partitionNum: String = _

    var timeout: Int         = 3000
    var connectionRetry: Int = 1
    var executionRetry: Int  = 1

    /**
      * @param address nebula-metad's address
      * @param partitionNum nebula space partition
      * @param space nebula space
      */
    def nebula(address: String, space: String, partitionNum: String): NebulaDataFrameReader = {
      this.address = address
      this.space = space
      this.partitionNum = partitionNum
      this
    }

    def withTimeout(timeout: Int): NebulaDataFrameReader = {
      this.timeout = timeout
      this
    }

    def withConnectionRetry(connectionRetry: Int): NebulaDataFrameReader = {
      this.connectionRetry = connectionRetry
      this
    }

    def withExecutionRetry(executionRetry: Int): NebulaDataFrameReader = {
      this.executionRetry = executionRetry
      this
    }

    def nebula(space: String, host: String, port: Int): Unit = {
      nebula(space, List(host -> port))
    }

    def nebula(space: String, address: List[(String, Int)]): Unit = ???

    /**
      * Reading com.vesoft.nebula.tools.connector.vertices from Nebula Graph
      *
      * @param tag nebula vertex type
      * @param fields tag's return columns
      * @return DataFrame
      */
    def loadVerticesToDF(tag: String, fields: String): DataFrame = {
      assert(address != null && space != null && partitionNum != null,
             "call nebula first before call loadVertices. ")
      reader
        .format(classOf[NebulaRelationProvider].getName)
        .option(NebulaOptions.HOST_AND_PORTS, address)
        .option(NebulaOptions.PARTITION_NUMBER, partitionNum)
        .option(NebulaOptions.SPACE_NAME, space)
        .option(NebulaOptions.TYPE, DataTypeEnum.VERTEX.toString)
        .option(NebulaOptions.LABEL, tag)
        .option(NebulaOptions.RETURN_COLS, fields)
        .option(NebulaOptions.TIMEOUT, timeout)
        .option(NebulaOptions.CONNECTION_RETRY, connectionRetry)
        .option(NebulaOptions.EXECUTION_RETRY, executionRetry)
        .load()
    }

    /**
      * Reading edges from Nebula Graph
      *
      * @param edge nebula edge type
      * @param fields edge's return columns
      * @return DataFrame
      */
    def loadEdgesToDF(edge: String, fields: String): DataFrame = {
      assert(address != null && space != null && partitionNum != null,
             "call nebula first before call loadEdges. ")
      reader
        .format(classOf[NebulaRelationProvider].getName)
        .option(NebulaOptions.HOST_AND_PORTS, address)
        .option(NebulaOptions.PARTITION_NUMBER, partitionNum)
        .option(NebulaOptions.SPACE_NAME, space)
        .option(NebulaOptions.TYPE, DataTypeEnum.EDGE.toString)
        .option(NebulaOptions.LABEL, edge)
        .option(NebulaOptions.RETURN_COLS, fields)
        .option(NebulaOptions.TIMEOUT, timeout)
        .option(NebulaOptions.CONNECTION_RETRY, connectionRetry)
        .option(NebulaOptions.EXECUTION_RETRY, executionRetry)
        .load()
    }

    /**
      * read nebula vertex edge to graphx's vertex
      */
    def loadVerticesToGraphx(tagName: String, fields: String): RDD[NebulaVertex] = {
      val vertexDataset = loadVerticesToDF(tagName, fields)
      implicit val encoder: Encoder[NebulaVertex] =
        Encoders.bean[NebulaVertex](classOf[NebulaVertex])
      vertexDataset
        .map(row => {
          val fields                 = row.schema.fields
          val vertexId               = row.get(0).toString.toLong
          val props: ListBuffer[Any] = ListBuffer()
          for (i <- row.schema.fields.indices) {
            if (i != 0) {
              props.append(NebulaUtils.resolveDataAndType(row, fields(i).dataType, i))
            }
          }
          (vertexId, props.toList)
        })(encoder)
        .rdd
    }

    /**
      * read nebula edge edge to graphx's edge
      */
    def loadEdgesToGraphx(edgeName: String, fields: String): RDD[NebulaEdge] = {
      val edgeDataset =
        loadEdgesToDF(edgeName, fields)
      implicit val encoder: Encoder[NebulaEdge] = Encoders.bean[NebulaEdge](classOf[NebulaEdge])
      edgeDataset
        .map(row => {
          val cols = row.schema.fields
          // TODO resolve rank
          val rank                   = 1
          val props: ListBuffer[Any] = ListBuffer()
          for (i <- row.schema.fields.indices) {
            if (i != 0 && i != 1) {
              props.append(NebulaUtils.resolveDataAndType(row, cols(i).dataType, i))
            }
          }
          Edge(row.get(0).toString.toLong, row.get(1).toString.toLong, (rank, props.toList))
        })(encoder)
        .rdd
    }

  }

  implicit class NebulaDataFrameWriter(writer: DataFrameWriter[Row]) {
    var address: String      = _
    var space: String        = _
    var partitionNum: String = _
    var timeout: Int         = 3000
    var connectionRetry: Int = 1
    var executionRetry: Int  = 1

    /**
      * @param address      nebula-metad's address
      * @param partitionNum nebula space partition
      * @param space        nebula space
      */
    def nebula(address: String, space: String, partitionNum: String): NebulaDataFrameWriter = {
      this.address = address
      this.space = space
      this.partitionNum = partitionNum
      this
    }

    def withTimeout(timeout: Int): NebulaDataFrameWriter = {
      this.timeout = timeout
      this
    }

    def withConnectionRetry(connectionRetry: Int): NebulaDataFrameWriter = {
      this.connectionRetry = connectionRetry
      this
    }

    def withExecutionRetry(executionRetry: Int): NebulaDataFrameWriter = {
      this.executionRetry = executionRetry
      this
    }

    /**
      * write dataframe into nebula vertex
      */
    def writeVertices(tag: String, vertexFiled: String, policy: String = ""): Unit = {
      writer
        .format(classOf[NebulaRelationProvider].getName)
        .option(NebulaOptions.HOST_AND_PORTS, address)
        .option(NebulaOptions.PARTITION_NUMBER, partitionNum)
        .option(NebulaOptions.SPACE_NAME, space)
        .option(NebulaOptions.LABEL, tag)
        .option(NebulaOptions.TYPE, DataTypeEnum.VERTEX.toString)
        .option(NebulaOptions.VERTEX_FIELD, vertexFiled)
        .option(NebulaOptions.POLICY, policy)
        .option(NebulaOptions.TIMEOUT, timeout)
        .option(NebulaOptions.CONNECTION_RETRY, connectionRetry)
        .option(NebulaOptions.EXECUTION_RETRY, executionRetry)
        .save()
    }

    /**
      * write dataframe into nebula edge
      */
    def writeEdges(edge: String,
                   srcVertexField: String,
                   dstVertexField: String,
                   policy: String = ""): Unit = {
      writer
        .format(classOf[NebulaRelationProvider].getName)
        .option(NebulaOptions.HOST_AND_PORTS, address)
        .option(NebulaOptions.PARTITION_NUMBER, partitionNum)
        .option(NebulaOptions.SPACE_NAME, space)
        .option(NebulaOptions.LABEL, edge)
        .option(NebulaOptions.TYPE, DataTypeEnum.EDGE.toString)
        .option(NebulaOptions.SRC_VERTEX_FIELD, srcVertexField)
        .option(NebulaOptions.DST_VERTEX_FIELD, dstVertexField)
        .option(NebulaOptions.POLICY, policy)
        .option(NebulaOptions.TIMEOUT, timeout)
        .option(NebulaOptions.CONNECTION_RETRY, connectionRetry)
        .option(NebulaOptions.EXECUTION_RETRY, executionRetry)
        .save()
    }
  }
}

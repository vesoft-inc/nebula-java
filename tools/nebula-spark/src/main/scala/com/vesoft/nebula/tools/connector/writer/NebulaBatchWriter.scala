/*
 * Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.connector.writer

import java.util.concurrent.TimeUnit

import com.google.common.base.Optional
import com.google.common.net.HostAndPort
import com.google.common.util.concurrent.{FutureCallback, Futures, ListenableFuture, RateLimiter}
import com.vesoft.nebula.client.graph.async.AsyncGraphClientImpl
import com.vesoft.nebula.tools.connector.DataTypeEnum.{EDGE, VERTEX}
import com.vesoft.nebula.tools.connector.exception.{GraphExecuteException, IllegalOptionException}
import com.vesoft.nebula.tools.connector.{
  DataTypeEnum,
  KeyPolicy,
  NebulaOptions,
  NebulaUtils,
  OperaType
}
import org.apache.spark.sql.catalyst.util.CaseInsensitiveMap
import org.apache.spark.sql.{DataFrame, Encoders, Row}
import org.apache.spark.sql.types.{DataType, StructType}
import org.slf4j.LoggerFactory

import scala.collection.mutable.ListBuffer

class NebulaBatchWriter(address: List[HostAndPort], nebulaOptions: NebulaOptions)
    extends NebulaConnection(address, nebulaOptions) {

  private val LOG = LoggerFactory.getLogger(this.getClass)

  private[this] val BATCH_INSERT_TEMPLATE               = "INSERT %s %s(%s) VALUES %s"
  private[this] val INSERT_VALUE_TEMPLATE               = "%s: (%s)"
  private[this] val INSERT_VALUE_TEMPLATE_WITH_POLICY   = "%s(\"%s\"): (%s)"
  private[this] val ENDPOINT_TEMPLATE                   = "%s(\"%s\")"
  private[this] val EDGE_VALUE_WITHOUT_RANKING_TEMPLATE = "%s->%s: (%s)"
  private[this] val EDGE_VALUE_TEMPLATE                 = "%s->%s@%d: (%s)"

  var client: AsyncGraphClientImpl = _

  def sendVertexExecution(data: DataFrame, vertexIndex: Int, schema: StructType): Unit = {

    val types     = schema.fields.map(field => field.dataType)
    val propNames = assignVertexProps(schema, vertexIndex)

    val vertices = data
      .map { row =>
        var vertexID =
          NebulaUtils.resolveDataAndType(row, types(vertexIndex), vertexIndex).toString
        if (nebulaOptions.policy != null) {
          KeyPolicy.withName(nebulaOptions.policy) match {
            case KeyPolicy.UUID =>
              vertexID = ENDPOINT_TEMPLATE.format(KeyPolicy.UUID.toString, vertexID)
            case KeyPolicy.HASH =>
              vertexID = ENDPOINT_TEMPLATE.format(KeyPolicy.HASH.toString, vertexID)
            case _ =>
              throw new IllegalArgumentException(
                s"Policy should be HASH or UUID, your configuration policy is ${nebulaOptions.policy}")
          }
        }
        Vertex(vertexID, assignVertexValues(types, row, vertexIndex))
      }(Encoders.kryo[Vertex])

    vertices.foreachPartition(rows => {
      client = connectClient()
      useSpace(client, nebulaOptions.spaceName)

      rows
        .grouped(nebulaOptions.batch)
        .foreach(datas => {
          val verticeDatas = Vertices(propNames, datas.toList)
          val exec         = toExecuteSentence(nebulaOptions.label, verticeDatas)

          @transient val rateLimiter = RateLimiter.create(nebulaOptions.rateLimit)
          if (rateLimiter.tryAcquire(nebulaOptions.rateTimeOut, TimeUnit.MILLISECONDS)) {
            val future = client.execute(exec)
            LOG.info(exec)
            Futures.addCallback(
              future,
              new FutureCallback[Optional[Integer]] {
                override def onSuccess(result: Optional[Integer]): Unit = {}

                override def onFailure(t: Throwable): Unit = {
                  LOG.error(s"failed to execute {$exec}")
                  throw new GraphExecuteException(s"failed to execute {$exec}")
                }
              }
            )
          } else {
            LOG.error(s"failed to acquire reteLimiter for statement {$exec}")
          }
        })
    })
  }

  def sendEdgeExecution(data: DataFrame,
                        srcIndex: Int,
                        dstIndex: Int,
                        rankField: String,
                        schema: StructType): Unit = {

    val types     = schema.fields.map(field => field.dataType)
    val propNames = assignEdgeProps(schema, srcIndex, dstIndex)

    val edges = data
      .map { row =>
        val rank =
          if (rankField == null || "".equals(rankField.trim)) Option.empty
          else Option(row.getAs[Long](rankField))
        var srcId = NebulaUtils.resolveDataAndType(row, types(srcIndex), srcIndex).toString
        var dstId = NebulaUtils.resolveDataAndType(row, types(dstIndex), dstIndex).toString
        if (nebulaOptions.policy != null) {
          KeyPolicy.withName(nebulaOptions.policy) match {
            case KeyPolicy.UUID =>
              srcId = ENDPOINT_TEMPLATE.format(KeyPolicy.UUID.toString, srcId)
              dstId = ENDPOINT_TEMPLATE.format(KeyPolicy.UUID.toString, dstId)
            case KeyPolicy.HASH =>
              srcId = ENDPOINT_TEMPLATE.format(KeyPolicy.HASH.toString, srcId)
              dstId = ENDPOINT_TEMPLATE.format(KeyPolicy.HASH.toString, dstId)
            case _ =>
              throw new IllegalArgumentException(
                s"Policy should be HASH or UUID, your configuration policy is ${nebulaOptions.policy}")
          }
        }
        Edge(srcId, dstId, rank, assignEdgeValues(types, row, srcIndex, dstIndex))
      }(Encoders.kryo[Edge])

    edges.foreachPartition(rows => {
      client = connectClient()
      useSpace(client, nebulaOptions.spaceName)

      rows
        .grouped(nebulaOptions.batch)
        .foreach(datas => {
          val edgeDatas = Edges(propNames, datas.toList)
          val exec      = toExecuteSentence(nebulaOptions.label, edgeDatas)

          @transient val rateLimiter = RateLimiter.create(nebulaOptions.rateLimit)
          if (rateLimiter.tryAcquire(nebulaOptions.rateTimeOut, TimeUnit.MILLISECONDS)) {
            val future = client.execute(exec)
            LOG.info(exec)
            Futures.addCallback(
              future,
              new FutureCallback[Optional[Integer]] {
                override def onSuccess(result: Optional[Integer]): Unit = {}

                override def onFailure(t: Throwable): Unit = {
                  LOG.error(s"failed to execute {$exec}")
                  throw new GraphExecuteException(s"failed to execute {$exec}")
                }
              }
            )
          } else {
            LOG.error(s"failed to acquire reteLimiter for statement {$exec}")
          }
        })
    })
  }

  def toExecuteSentence(tag: String, vertices: Vertices): String = {
    val values = vertices.values
      .map { vertex =>
        INSERT_VALUE_TEMPLATE.format(vertex.vertexID, vertex.propertyValues)
      }
      .mkString(", ")
    BATCH_INSERT_TEMPLATE.format(VERTEX.toString, tag, vertices.propertyNames, values)
  }

  def toExecuteSentence(name: String, edges: Edges): String = {
    val values = edges.values
      .map { edge =>
        (for (element <- edge.source.split(","))
          yield {
            if (edge.ranking.isEmpty)
              EDGE_VALUE_WITHOUT_RANKING_TEMPLATE
                .format(edge.source, edge.destination, edge.propertyValues)
            else
              EDGE_VALUE_TEMPLATE.format(edge.source,
                                         edge.destination,
                                         edge.ranking.get,
                                         edge.propertyValues)
          }).mkString(", ")
      }
      .mkString(", ")
    BATCH_INSERT_TEMPLATE.format(EDGE, name, edges.propertyNames, values)
  }

  def assignVertexProps(schema: StructType, vertexIndex: Int): List[String] = {
    val propNames = for {
      index <- schema.indices
      if index != vertexIndex
    } yield {
      schema.fields(index).name
    }
    propNames.toList
  }

  def assignEdgeProps(schema: StructType, srcIndex: Int, dstIndex: Int): List[String] = {
    val propNames = for {
      index <- schema.indices
      if index != srcIndex && index != dstIndex
    } yield {
      schema.fields(index).name
    }
    propNames.toList
  }

  def assignVertexValues(types: Array[DataType], record: Row, vertexIndex: Int): List[String] = {
    val values = for {
      index <- types.indices
      if index != vertexIndex
    } yield {
      NebulaUtils.resolveDataAndType(record, types(index), index).toString
    }
    values.toList
  }

  def assignEdgeValues(types: Array[DataType],
                       record: Row,
                       srcIndex: Int,
                       dstIndex: Int): List[String] = {
    val values = for {
      index <- types.indices
      if index != srcIndex && index != dstIndex
    } yield {
      NebulaUtils.resolveDataAndType(record, types(index), index).toString
    }
    values.toList
  }

  case class Vertex(vertexID: VertexIDSlice, values: PropertyValues) {

    def propertyValues = values.mkString(", ")

    override def toString: String = {
      s"Vertex ID: ${vertexID}, " +
        s"Values: ${values.mkString(", ")}"
    }
  }

  case class Vertices(names: PropertyNames,
                      values: List[Vertex],
                      policy: Option[KeyPolicy.Value] = None) {

    def propertyNames: String = names.mkString(",")

    override def toString: String = {
      s"Vertices: " +
        s"Property Names: ${names.mkString(", ")}" +
        s"Vertex Values: ${values.mkString(", ")} " +
        s"with policy ${policy}"
    }
  }

  case class Edge(source: VertexIDSlice,
                  destination: VertexIDSlice,
                  ranking: Option[EdgeRank],
                  values: PropertyValues) {

    def this(source: VertexIDSlice, destination: VertexIDSlice, values: PropertyValues) = {
      this(source, destination, None, values)
    }

    def propertyValues: String = values.mkString(", ")

    override def toString: String = {
      s"Edge: ${source}->${destination}@${ranking} values: ${propertyValues}"
    }
  }

  case class Edges(names: PropertyNames,
                   values: List[Edge],
                   sourcePolicy: Option[KeyPolicy.Value] = None,
                   targetPolicy: Option[KeyPolicy.Value] = None) {
    def propertyNames: String = names.mkString(",")

    override def toString: String = {
      "Edges:" +
        s"Property Names: ${names.mkString(", ")}" +
        s"with source policy ${sourcePolicy}" +
        s"with target policy ${targetPolicy}"
    }
  }

  type GraphSpaceID   = Int
  type PartitionID    = Int
  type TagID          = Int
  type EdgeType       = Int
  type SchemaID       = (TagID, EdgeType)
  type TagVersion     = Long
  type EdgeVersion    = Long
  type SchemaVersion  = (TagVersion, EdgeVersion)
  type VertexID       = Long
  type VertexIDSlice  = String
  type EdgeRank       = Long
  type PropertyNames  = List[String]
  type PropertyValues = List[Any]
  type ProcessResult  = ListBuffer[WriterResult]
  type WriterResult   = ListenableFuture[Optional[Integer]]
}

class NebulaBatchWriterUtils extends Serializable {
  var nebulaOptions: NebulaOptions    = _
  var parameters: Map[String, String] = Map()

  def batchInsert(address: String, space: String, batch: Int = 2000): NebulaBatchWriterUtils = {
    parameters += (NebulaOptions.HOST_AND_PORTS -> address)
    parameters += (NebulaOptions.SPACE_NAME     -> space)
    parameters += (NebulaOptions.BATCH          -> batch.toString)
    this
  }

  def batchToNebulaVertex(data: DataFrame,
                          tag: String,
                          vertexField: String,
                          policy: String = ""): Unit = {
    parameters += (NebulaOptions.TYPE   -> DataTypeEnum.VERTEX.toString)
    parameters += (NebulaOptions.LABEL  -> tag)
    parameters += (NebulaOptions.POLICY -> policy)
    val nebulaOptions: NebulaOptions = {
      new NebulaOptions(CaseInsensitiveMap(parameters))(OperaType.WRITE)
    }

    val schema = data.schema
    val vertexIndex: Int = {
      var index: Int = -1
      for (i <- schema.fields.indices) {
        if (schema.fields(i).name.equals(vertexField)) {
          index = i
        }
      }
      if (index < 0) {
        throw new IllegalOptionException(
          s" vertex field ${vertexField} does not exist in dataframe")
      }
      index
    }
    val nebulaBatchWriter = new NebulaBatchWriter(nebulaOptions.getHostAndPorts, nebulaOptions)
    nebulaBatchWriter.sendVertexExecution(data, vertexIndex, schema)
  }

  def batchToNebulaEdge(data: DataFrame,
                        edge: String,
                        srcVertexField: String,
                        dstVertexField: String,
                        rankField: String = "",
                        policy: String = ""): Unit = {
    parameters += (NebulaOptions.TYPE   -> DataTypeEnum.VERTEX.toString)
    parameters += (NebulaOptions.LABEL  -> edge)
    parameters += (NebulaOptions.POLICY -> policy)
    val nebulaOptions: NebulaOptions = {
      new NebulaOptions(CaseInsensitiveMap(parameters))(OperaType.WRITE)
    }

    val schema = data.schema
    val vertexFieldsIndex = {
      var srcIndex: Int = -1
      var dstIndex: Int = -1
      for (i <- schema.fields.indices) {
        if (schema.fields(i).name.equals(srcVertexField)) {
          srcIndex = i
        }
        if (schema.fields(i).name.equals(dstVertexField)) {
          dstIndex = i
        }
      }
      if (srcIndex < 0 || dstIndex < 0) {
        throw new IllegalOptionException(
          s" srcVertex field ${srcVertexField} or dstVertex field ${dstVertexField} do not exist in dataframe")
      }
      (srcIndex, dstIndex)
    }

    val nebulaBatchWriter = new NebulaBatchWriter(nebulaOptions.getHostAndPorts, nebulaOptions)
    nebulaBatchWriter.sendEdgeExecution(data,
                                        vertexFieldsIndex._1,
                                        vertexFieldsIndex._2,
                                        rankField,
                                        data.schema)
  }

}

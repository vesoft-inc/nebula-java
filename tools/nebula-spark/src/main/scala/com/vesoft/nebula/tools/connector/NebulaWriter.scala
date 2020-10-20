package com.vesoft.nebula.tools.connector

import java.util.concurrent.TimeUnit

import com.google.common.base.Optional
import com.google.common.net.HostAndPort
import com.google.common.util.concurrent.{FutureCallback, Futures, RateLimiter}
import com.vesoft.nebula.client.graph.async.AsyncGraphClientImpl
import com.vesoft.nebula.graph.ErrorCode
import com.vesoft.nebula.tools.connector.exception.GraphExecuteException
import org.apache.commons.httpclient.util.TimeoutController.TimeoutException
import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.sources.v2.writer.{
  DataSourceWriter,
  DataWriter,
  DataWriterFactory,
  WriterCommitMessage
}
import org.apache.spark.sql.types.{DataType, StructType}
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._

case class NebulaCommitMessage() extends WriterCommitMessage

class NebulaVWriter(address: List[HostAndPort],
                    nebulaOptions: NebulaOptions,
                    space: String,
                    tag: String,
                    vertexIndex: Int,
                    schema: StructType)
    extends DataWriter[InternalRow] {

  private val LOG = LoggerFactory.getLogger(this.getClass)

  val types: Array[DataType] = schema.fields.map(field => field.dataType)
  val propNames: String      = assignProps(schema)

  override def write(row: InternalRow): Unit = {
    val vertex = extraValue(types(vertexIndex), row, vertexIndex)
    val values = assignValues(types, row)
    println(s"INSERT TAG $tag($propNames) VALUES $vertex:($values)")

    // 连接client

    val client = new AsyncGraphClientImpl(
      address.asJava,
      nebulaOptions.connectionTimeout,
      nebulaOptions.connectionRetry,
      nebulaOptions.executionRetry
    )
    client.setUser(nebulaOptions.user)
    client.setPassword(nebulaOptions.passwd)

    if (ErrorCode.SUCCEEDED == client.connect()) {
      val rateLimiter = RateLimiter.create(nebulaOptions.rateLimit)

      val useSpace = NebulaTemplate.USE_TEMPLATE.format(space)
      if (rateLimiter.tryAcquire(nebulaOptions.rateTimeOut, TimeUnit.MILLISECONDS)) {
        val future = client.execute(useSpace)
        Futures.addCallback(
          future,
          new FutureCallback[Optional[Integer]] {
            override def onSuccess(result: Optional[Integer]): Unit = {}

            override def onFailure(t: Throwable): Unit = {
              LOG.error(s"failed to execute {$useSpace}")
            }
          }
        )
      } else {
        throw new TimeoutException()
      }

      val exec = NebulaTemplate.BATCH_INSERT_TEMPLATE.format(
        DataTypeEnum.VERTEX.toString,
        nebulaOptions.label,
        propNames,
        if (nebulaOptions.policy == null) {
          NebulaTemplate.VERTEX_VALUE_TEMPLATE.format(vertex, values)
        } else {
          KeyPolicy.withName(nebulaOptions.policy) match {
            case KeyPolicy.HASH =>
              NebulaTemplate.VERTEX_VALUE_TEMPLATE_WITH_POLICY.format(KeyPolicy.HASH.toString,
                                                                      vertex,
                                                                      values)
            case KeyPolicy.UUID =>
              NebulaTemplate.VERTEX_VALUE_TEMPLATE_WITH_POLICY.format(KeyPolicy.UUID.toString,
                                                                      vertex,
                                                                      values)
            case _ =>
              throw new IllegalArgumentException(
                s"Policy be HASH or UUID, your configuration policy is ${nebulaOptions.policy}")
          }
        }
      )
      if (rateLimiter.tryAcquire(nebulaOptions.rateTimeOut, TimeUnit.MILLISECONDS)) {
        val future = client.execute(exec)
        Futures.addCallback(
          future,
          new FutureCallback[Optional[Integer]] {
            override def onSuccess(result: Optional[Integer]): Unit = {}

            override def onFailure(t: Throwable): Unit = {
              LOG.error(s"failed to execute {$useSpace}")
              throw new GraphExecuteException(s"failed to execute {$exec}")
            }
          }
        )
      } else {
        throw new TimeoutException()
      }
    }
  }

  def assignValues(types: Array[DataType], record: InternalRow): String = {
    val values = for {
      index <- types.indices
      if index != vertexIndex
    } yield {
      NebulaUtils.getRowColData(record, types(index), index).toString
    }
    values.mkString(", ")
  }

  def extraValue(dataType: DataType, record: InternalRow, index: Int): String = {
    NebulaUtils.getRowColData(record, dataType, index).toString
  }

  def assignProps(schema: StructType): String = {
    val propNames = for {
      index <- schema.indices
      if index != vertexIndex
    } yield {
      schema.fields(index).name
    }
    propNames.mkString(",")
  }

  override def commit(): WriterCommitMessage = {
    NebulaCommitMessage()
  }

  override def abort(): Unit = {
//    client.close()
  }
}

class NebulaEWriter(address: List[HostAndPort],
                    nebulaOptions: NebulaOptions,
                    space: String,
                    edge: String,
                    srcIndex: Int,
                    dstIndex: Int,
                    schema: StructType)
    extends DataWriter[InternalRow] {

  private val LOG = LoggerFactory.getLogger(this.getClass)

  val types: Array[DataType] = schema.fields.map(field => field.dataType)
  val propNames: String      = assignProps(schema, srcIndex, dstIndex)

  override def write(row: InternalRow): Unit = {

    val edges  = extraValues(types(srcIndex), types(dstIndex), row, srcIndex, dstIndex)
    val values = assignValues(types, row)
    println(s"INSERT EDGE $edge($propNames) VALUES ${edges._1}->${edges._2}:($values)")

    // 连接client
    val client = new AsyncGraphClientImpl(
      address.asJava,
      nebulaOptions.connectionTimeout,
      nebulaOptions.connectionRetry,
      nebulaOptions.executionRetry
    )
    client.setUser(nebulaOptions.user)
    client.setPassword(nebulaOptions.passwd)

    if (ErrorCode.SUCCEEDED == client.connect()) {
      val rateLimiter = RateLimiter.create(nebulaOptions.rateLimit)

      val useSpace = NebulaTemplate.USE_TEMPLATE.format(space)
      if (rateLimiter.tryAcquire(nebulaOptions.rateTimeOut, TimeUnit.MILLISECONDS)) {

        val future = client.execute(useSpace)
        Futures.addCallback(
          future,
          new FutureCallback[Optional[Integer]] {
            override def onSuccess(result: Optional[Integer]): Unit = {}

            override def onFailure(t: Throwable): Unit = {
              LOG.error(s"failed to execute $useSpace")
            }
          }
        )

      } else {
        throw new TimeoutException()
      }

      val exec = NebulaTemplate.BATCH_INSERT_TEMPLATE.format(
        DataTypeEnum.EDGE.toString,
        nebulaOptions.label,
        propNames,
        if (nebulaOptions.policy == null) {
          NebulaTemplate.EDGE_VALUE_WITHOUT_RANKING_TEMPLATE.format(edges._1, edges._2, values)
        } else {
          KeyPolicy.withName(nebulaOptions.policy) match {
            case KeyPolicy.HASH =>
              NebulaTemplate.EDGE_VALUE_WITHOUT_RANKING_TEMPLATE_WITH_POLICY.format(
                KeyPolicy.HASH.toString,
                edges._1,
                KeyPolicy.HASH.toString,
                edges._2,
                values)
            case KeyPolicy.UUID =>
              NebulaTemplate.EDGE_VALUE_WITHOUT_RANKING_TEMPLATE_WITH_POLICY.format(
                KeyPolicy.UUID.toString,
                edges._1,
                KeyPolicy.UUID.toString,
                edges._2,
                values)
            case _ =>
              throw new IllegalArgumentException(
                s"Policy be HASH or UUID, your configuration policy is ${nebulaOptions.policy}")
          }
        }
      )
      if (rateLimiter.tryAcquire(nebulaOptions.rateTimeOut, TimeUnit.MILLISECONDS)) {
        val future = client.execute(exec)
        Futures.addCallback(
          future,
          new FutureCallback[Optional[Integer]] {
            override def onSuccess(result: Optional[Integer]): Unit = {}

            override def onFailure(t: Throwable): Unit = {
              LOG.error(s"failed to execute {$useSpace}")
              throw new GraphExecuteException(s"failed to execute {$exec}")
            }
          }
        )
      } else {
        throw new TimeoutException()
      }
    }

  }

  def assignValues(types: Array[DataType], record: InternalRow): String = {
    val values = for {
      index <- types.indices
      if index != srcIndex && index != dstIndex
    } yield {
      NebulaUtils.getRowColData(record, types(index), index).toString
    }
    values.mkString(", ")
  }

  def extraValues(srcDataType: DataType,
                  dstDataType: DataType,
                  record: InternalRow,
                  srcIndex: Int,
                  dstIndex: Int): (String, String) = {
    val srcValue = NebulaUtils.getRowColData(record, srcDataType, srcIndex)
    val dstValue = NebulaUtils.getRowColData(record, dstDataType, dstIndex)
    (srcValue.toString, dstValue.toString)
  }

  def assignProps(schema: StructType, srcIndex: Int, dstIndex: Int): String = {
    val propNames = for {
      index <- schema.indices
      if index != srcIndex && index != dstIndex
    } yield {
      schema.fields(index).name
    }
    propNames.mkString(",")
  }

  override def commit(): WriterCommitMessage = {
    NebulaCommitMessage.apply()
  }

  override def abort(): Unit = {
    LOG.error("insert edge task abort.")
  }
}

class NebulaVertexWriterFactory(address: List[HostAndPort],
                                nebulaOptions: NebulaOptions,
                                space: String,
                                tag: String,
                                vertexIndex: Int,
                                schema: StructType)
    extends DataWriterFactory[InternalRow] {
  override def createDataWriter(partitionId: EdgeRank,
                                taskId: Long,
                                epochId: Long): DataWriter[InternalRow] = {
    new NebulaVWriter(address, nebulaOptions, space, tag, vertexIndex, schema)
  }
}

class NebulaEdgeWriterFactory(address: List[HostAndPort],
                              nebulaOptions: NebulaOptions,
                              space: String,
                              tag: String,
                              srcIndex: Int,
                              dstIndex: Int,
                              schema: StructType)
    extends DataWriterFactory[InternalRow] {
  override def createDataWriter(partitionId: EdgeRank,
                                taskId: Long,
                                epochId: Long): DataWriter[InternalRow] = {
    new NebulaEWriter(address, nebulaOptions, space, tag, srcIndex, dstIndex, schema)
  }
}

class NebulaVertexWriter(addresses: List[HostAndPort],
                         nebulaOptions: NebulaOptions,
                         space: String,
                         tag: String,
                         vertexIndex: Int,
                         schema: StructType)
    extends DataSourceWriter {
  private val LOG = LoggerFactory.getLogger(this.getClass)

  override def createWriterFactory(): DataWriterFactory[InternalRow] = {
    new NebulaVertexWriterFactory(addresses, nebulaOptions, space, tag, vertexIndex, schema)
  }

  override def commit(messages: Array[WriterCommitMessage]): Unit = {
    LOG.debug(s"${messages.length}")
  }

  override def abort(messages: Array[WriterCommitMessage]): Unit = {
    LOG.error("")
  }
}

class NebulaEdgeWriter(addresses: List[HostAndPort],
                       nebulaOptions: NebulaOptions,
                       space: String,
                       edge: String,
                       srcIndex: Int,
                       dstIndex: Int,
                       schema: StructType)
    extends DataSourceWriter {
  private val LOG = LoggerFactory.getLogger(this.getClass)

  override def createWriterFactory(): DataWriterFactory[InternalRow] = {
    new NebulaEdgeWriterFactory(addresses, nebulaOptions, space, edge, srcIndex, dstIndex, schema)
  }

  override def commit(messages: Array[WriterCommitMessage]): Unit = {
    LOG.debug(s"${messages.length}")
  }

  override def abort(messages: Array[WriterCommitMessage]): Unit = {
    LOG.error("")
  }
}

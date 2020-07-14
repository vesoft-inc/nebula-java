/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.importer.writer

import java.util.concurrent.CountDownLatch

import com.google.common.base.Optional
import com.google.common.net.HostAndPort
import com.google.common.util.concurrent.{FutureCallback, ListenableFuture}
import com.vesoft.nebula.client.graph.async.AsyncGraphClientImpl
import com.vesoft.nebula.client.meta.MetaClientImpl
import com.vesoft.nebula.client.storage.StorageClientImpl
import com.vesoft.nebula.graph.ErrorCode
import com.vesoft.nebula.tools.importer.{
  ConnectionConfigEntry,
  DataBaseConfigEntry,
  Edges,
  KeyPolicy,
  SchemaConfigEntry,
  TooManyErrorsException,
  Type,
  UserConfigEntry,
  Vertices
}
import org.apache.log4j.Logger
import org.apache.spark.util.LongAccumulator

import scala.collection.JavaConverters._

abstract class ServerBaseWriter extends Writer {
  private[this] val BATCH_INSERT_TEMPLATE               = "INSERT %s %s(%s) VALUES %s"
  private[this] val INSERT_VALUE_TEMPLATE               = "%s: (%s)"
  private[this] val INSERT_VALUE_TEMPLATE_WITH_POLICY   = "%s(\"%s\"): (%s)"
  private[this] val ENDPOINT_TEMPLATE                   = "%s(\"%s\")"
  private[this] val EDGE_VALUE_WITHOUT_RANKING_TEMPLATE = "%s->%s: (%s)"
  private[this] val EDGE_VALUE_TEMPLATE                 = "%s->%s@%d: (%s)"

  def toExecuteSentence(name: String, vertices: Vertices): String = {
    BATCH_INSERT_TEMPLATE.format(
      Type.VERTEX.toString,
      name,
      vertices.propertyNames,
      vertices.values
        .map { vertex =>
          // TODO Check
          if (vertices.policy.isEmpty) {
            INSERT_VALUE_TEMPLATE.format(vertex.vertexID, vertex.propertyValues)
          } else {
            vertices.policy.get match {
              case KeyPolicy.HASH =>
                INSERT_VALUE_TEMPLATE_WITH_POLICY
                  .format(KeyPolicy.HASH.toString, vertex.vertexID, vertex.propertyValues)
              case KeyPolicy.UUID =>
                INSERT_VALUE_TEMPLATE_WITH_POLICY
                  .format(KeyPolicy.UUID.toString, vertex.vertexID, vertex.propertyValues)
              case _ =>
                throw new IllegalArgumentException("Not Support")
            }
          }
        }
        .mkString(", ")
    )
  }

  def toExecuteSentence(name: String, edges: Edges): String = {
    val values = edges.values
      .map { edge =>
        (for (element <- edge.source.split(","))
          yield {
            // TODO Check and Test
            val source = edges.sourcePolicy match {
              case Some(KeyPolicy.HASH) =>
                ENDPOINT_TEMPLATE.format(KeyPolicy.HASH.toString, element)
              case Some(KeyPolicy.UUID) =>
                ENDPOINT_TEMPLATE.format(KeyPolicy.UUID.toString, element)
              case None =>
                element
            }

            val target = edges.targetPolicy match {
              case Some(KeyPolicy.HASH) =>
                ENDPOINT_TEMPLATE.format(KeyPolicy.HASH.toString, edge.destination)
              case Some(KeyPolicy.UUID) =>
                ENDPOINT_TEMPLATE.format(KeyPolicy.UUID.toString, edge.destination)
              case None =>
                edge.destination
            }

            if (edge.ranking.isEmpty)
              EDGE_VALUE_WITHOUT_RANKING_TEMPLATE
                .format(source, target, edge.propertyValues)
            else
              EDGE_VALUE_TEMPLATE.format(source, target, edge.ranking.get, edge.propertyValues)
          }).mkString(", ")

      }
      .mkString(", ")
    BATCH_INSERT_TEMPLATE.format(Type.EDGE.toString, name, edges.propertyNames, values)
  }
}

/**
  *
  */
class NebulaGraphClientWriter(dataBaseConfigEntry: DataBaseConfigEntry,
                              userConfigEntry: UserConfigEntry,
                              connectionConfigEntry: ConnectionConfigEntry,
                              executionRetry: Int,
                              config: SchemaConfigEntry)
    extends ServerBaseWriter {

  require(dataBaseConfigEntry.addresses.size != 0 && dataBaseConfigEntry.space.trim.size != 0)
  require(userConfigEntry.user.trim.size != 0 && userConfigEntry.password.trim.size != 0)
  require(connectionConfigEntry.timeout > 0 && connectionConfigEntry.retry > 0)
  require(executionRetry > 0)

  @transient lazy val LOG = Logger.getLogger(this.getClass)

  val client = new AsyncGraphClientImpl(
    dataBaseConfigEntry.addresses
      .map(address => {
        val pair = address.split(":")
        if (pair.length != 2) {
          throw new IllegalArgumentException("address should compose by host and port")
        }
        HostAndPort.fromParts(pair(0), pair(1).toInt)
      })
      .asJava,
    connectionConfigEntry.timeout,
    connectionConfigEntry.retry,
    executionRetry
  )

  client.setUser(userConfigEntry.user)
  client.setPassword(userConfigEntry.password)

  def prepare(): Unit = {
    val code = client.connect()
    if (code != ErrorCode.SUCCEEDED) {
      throw new RuntimeException("Connection Failed")
    }

    val switchCode = client.switchSpace(dataBaseConfigEntry.space).get().get()
    if (switchCode != ErrorCode.SUCCEEDED) {
      throw new RuntimeException("Switch Failed")
    }

    LOG.info(s"Connection to ${dataBaseConfigEntry.addresses}")
  }

  override def writeVertices(vertices: Vertices): ListenableFuture[Optional[Integer]] = {
    val sentence = toExecuteSentence(config.name, vertices)
    LOG.info(sentence)
    client.execute(sentence)
  }

  override def writeEdges(edges: Edges): ListenableFuture[Optional[Integer]] = {
    val sentence = toExecuteSentence(config.name, edges)
    LOG.info(sentence)
    client.execute(sentence)
  }

  override def close(): Unit = {
    client.close()
  }
}

class NebulaWriterCallback(latch: CountDownLatch,
                           batchSuccess: LongAccumulator,
                           batchFailure: LongAccumulator,
                           checkPointOpt: Option[LongAccumulator],
                           batchSize: Int)
    extends FutureCallback[Optional[Integer]] {

  private[this] val DEFAULT_ERROR_TIMES = 16

  override def onSuccess(result: Optional[Integer]): Unit = {
    latch.countDown()
    if (result.get() == ErrorCode.SUCCEEDED) {
      batchSuccess.add(1)
      if (checkPointOpt.isDefined) {
        checkPointOpt.get.add(batchSize)
      }
    } else {
      batchFailure.add(1)
    }
  }

  override def onFailure(t: Throwable): Unit = {
    latch.countDown()
    if (batchFailure.value > DEFAULT_ERROR_TIMES) {
      throw TooManyErrorsException("too many errors")
    } else {
      batchFailure.add(1)
    }
  }
}

/**
  *
  * @param addresses
  * @param space
  */
class NebulaStorageClientWriter(addresses: List[(String, Int)], space: String)
    extends ServerBaseWriter {

  require(addresses.size != 0)

  def this(host: String, port: Int, space: String) = {
    this(List(host -> port), space)
  }

  private[this] val metaClient = new MetaClientImpl(
    addresses.map(address => HostAndPort.fromParts(address._1, address._2)).asJava)
  val client = new StorageClientImpl(metaClient)

  override def prepare(): Unit = {}

  override def writeVertices(vertices: Vertices): ListenableFuture[Optional[Integer]] = ???

  override def writeEdges(edges: Edges): ListenableFuture[Optional[Integer]] = ???

  override def close(): Unit = {
    client.close()
  }
}

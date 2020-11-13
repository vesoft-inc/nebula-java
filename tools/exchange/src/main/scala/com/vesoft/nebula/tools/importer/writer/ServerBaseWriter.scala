/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.importer.writer

import java.util.concurrent.CountDownLatch

import com.google.common.base.Optional
import com.google.common.net.HostAndPort
import com.google.common.util.concurrent.FutureCallback
import com.vesoft.nebula.client.graph.async.AsyncGraphClientImpl
import com.vesoft.nebula.client.meta.MetaClientImpl
import com.vesoft.nebula.client.storage.StorageClientImpl
import com.vesoft.nebula.graph.ErrorCode
import com.vesoft.nebula.tools.importer.config.{
  ConnectionConfigEntry,
  DataBaseConfigEntry,
  SchemaConfigEntry,
  Type,
  UserConfigEntry
}
import com.vesoft.nebula.tools.importer.utils.HDFSUtils
import com.vesoft.nebula.tools.importer.{
  Edges,
  KeyPolicy,
  TooManyErrorsException,
  Vertices,
  WriterResult
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

  def writeVertices(vertices: Vertices): WriterResult

  def writeEdges(edges: Edges): WriterResult
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
      this.close()
      throw new RuntimeException("Connection Failed")
    }

    val switchCode = client.switchSpace(dataBaseConfigEntry.space).get().get()
    if (switchCode != ErrorCode.SUCCEEDED) {
      this.close()
      throw new RuntimeException("Switch Failed")
    }

    LOG.info(s"Connection to ${dataBaseConfigEntry.addresses}")
  }

  override def writeVertices(vertices: Vertices): WriterResult = {
    val sentence = toExecuteSentence(config.name, vertices)
    LOG.info(sentence)
    client.execute(sentence)
  }

  override def writeEdges(edges: Edges): WriterResult = {
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
                           pathAndOffset: Option[(String, Long)])
    extends FutureCallback[java.util.List[Optional[Integer]]] {

  private[this] lazy val LOG = Logger.getLogger(this.getClass)

  private[this] val DEFAULT_ERROR_TIMES = 16

  override def onSuccess(results: java.util.List[Optional[Integer]]): Unit = {
    if (pathAndOffset.isDefined) {
      if (results.asScala.forall(_.get() == ErrorCode.SUCCEEDED))
        HDFSUtils.saveContent(pathAndOffset.get._1, pathAndOffset.get._2.toString)
      else
        throw new RuntimeException(
          s"Some error code: ${results.asScala.filter(_.get() != ErrorCode.SUCCEEDED).head} appear")
    }
    for (result <- results.asScala) {
      latch.countDown()
      if (result.get() == ErrorCode.SUCCEEDED) {
        batchSuccess.add(1)
      } else {
        LOG.error(s"batch insert error with code ${result.get()}, batch size is ${results.size()}")
        batchFailure.add(1)
      }
    }
  }

  override def onFailure(t: Throwable): Unit = {
    latch.countDown()
    if (batchFailure.value > DEFAULT_ERROR_TIMES) {
      throw TooManyErrorsException("too many errors")
    } else {
      batchFailure.add(1)
    }

    if (pathAndOffset.isDefined) {
      throw new RuntimeException(s"Some error appear")
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

  override def writeVertices(vertices: Vertices): WriterResult = ???

  override def writeEdges(edges: Edges): WriterResult = ???

  override def close(): Unit = {
    client.close()
  }
}

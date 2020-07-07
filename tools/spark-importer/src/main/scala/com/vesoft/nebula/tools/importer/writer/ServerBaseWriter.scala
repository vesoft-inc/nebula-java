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

abstract class ServerBaseWriter {}

/**
  *
  */
class NebulaGraphClientWriter(dataBaseConfigEntry: DataBaseConfigEntry,
                              userConfigEntry: UserConfigEntry,
                              connectionConfigEntry: ConnectionConfigEntry,
                              executionRetry: Int,
                              config: SchemaConfigEntry)
    extends ServerBaseWriter
    with Writer {

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
      LOG.error("Connection Failed")
    }

    val switchCode = client.switchSpace(dataBaseConfigEntry.space).get().get()
    if (switchCode != ErrorCode.SUCCEEDED) {
      LOG.error("Switch Failed")
    }

    LOG.info(s"Connection to ${dataBaseConfigEntry.addresses}")
  }

  override def writeVertices(vertices: Vertices): ListenableFuture[Optional[Integer]] = {
    val sentence = toExecuteSentence(config.name, vertices)
    LOG.info(sentence)
    if (client == null) {
      LOG.info("Client is null")
    } else {
      LOG.info("Client is OK")
    }
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
    extends ServerBaseWriter
    with Writer {

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

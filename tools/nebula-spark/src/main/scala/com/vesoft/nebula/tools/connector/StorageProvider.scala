/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.connector

import java.util

import com.google.common.net.HostAndPort
import com.vesoft.nebula.client.meta.MetaClientImpl
import com.vesoft.nebula.client.storage.StorageClientImpl
import com.vesoft.nebula.meta
import com.vesoft.nebula.storage
import com.vesoft.nebula.storage.{ScanEdgeResponse, ScanVertexResponse}
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._

class StorageProvider(addresses: List[Address], space: String, part: Int) extends AutoCloseable {
  private val LOG = LoggerFactory.getLogger(this.getClass)

  val metaClient = new MetaClientImpl(addresses.map { address =>
    HostAndPort.fromParts(address._1, address._2)
  }.asJava)

  if (meta.ErrorCode.SUCCEEDED != metaClient.connect()) {
    LOG.error("Meta client connect failed")
    throw ConnectionException("Meta client connect failed")
  }

  val client = new StorageClientImpl(metaClient)
  if (storage.ErrorCode.SUCCEEDED != client.connect()) {
    LOG.error("Storage client connect failed")
    throw ConnectionException("Storage client connect failed")
  }

  def fetchVertices(tag: String, fields: List[String]): util.Iterator[ScanVertexResponse] = {
    val tagFields = Map(tag -> fields.asJava).asJava
    client.scanVertex(space, tagFields) // TODO startTime and endTime
  }

  def fetchEdges(edge: String, fields: List[String]): util.Iterator[ScanEdgeResponse] = {
    val edgeFields = Map(edge -> fields.asJava).asJava
    client.scanEdge(space, edgeFields) // TODO startTime and endTime
  }

  override def close(): Unit = {
    client.close()
    metaClient.close()
  }
}

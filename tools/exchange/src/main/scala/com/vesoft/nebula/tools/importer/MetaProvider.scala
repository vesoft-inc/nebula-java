/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.importer

import com.google.common.net.HostAndPort
import com.vesoft.nebula.client.meta.MetaClientImpl
import com.vesoft.nebula.meta.ErrorCode

import scala.collection.JavaConverters._

class MetaProvider(addresses: List[String]) extends AutoCloseable {
  private lazy val metaClient = new MetaClientImpl(addresses.map { address =>
    val pair = address.split(":")
    if (pair.length != 2) {
      throw new IllegalArgumentException("address should compose by host and port")
    }
    HostAndPort.fromParts(pair(0), pair(1).toInt)
  }.asJava)

  def prepare(): Boolean = {
    metaClient.connect() == ErrorCode.SUCCEEDED
  }

  def getPartNumber(space: String): Unit = {
    metaClient.getPartsAlloc(space).size()
  }

  def getTagID(space: String, tag: String): TagID = {
    val response = metaClient.getTags(space).asScala
    response
      .filter(item => item.tag_name == tag)
      .map(_.tag_id)
      .toList(0)
  }

  def getEdgeType(space: String, edge: String): EdgeType = {
    val response = metaClient.getEdges(space).asScala
    response
      .filter(item => item.edge_name == edge)
      .map(_.edge_type)
      .toList(0)
  }

  override def close(): Unit = {
    metaClient.close()
  }
}

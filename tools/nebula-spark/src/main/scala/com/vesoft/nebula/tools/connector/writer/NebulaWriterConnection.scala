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
import com.google.common.util.concurrent.{FutureCallback, Futures, RateLimiter}
import com.vesoft.nebula.client.graph.async.AsyncGraphClientImpl
import com.vesoft.nebula.graph.ErrorCode
import com.vesoft.nebula.tools.connector.{ConnectionException, NebulaOptions, NebulaTemplate}
import org.slf4j.LoggerFactory
import scala.collection.JavaConverters._

class NebulaConnection(address: List[HostAndPort], nebulaOptions: NebulaOptions)
    extends Serializable {

  private val LOG                       = LoggerFactory.getLogger(this.getClass)
  var graphClient: AsyncGraphClientImpl = _
  var connected                         = false

  /**
    * connect the nebula client for once
    */
  def connectClient(): AsyncGraphClientImpl = {
    if (!connected) {
      graphClient = new AsyncGraphClientImpl(
        address.asJava,
        nebulaOptions.connectionTimeout,
        nebulaOptions.connectionRetry,
        nebulaOptions.executionRetry
      )
      graphClient.setUser(nebulaOptions.user)
      graphClient.setPassword(nebulaOptions.passwd)
      if (ErrorCode.SUCCEEDED == graphClient.connect()) {
        connected = true
      } else {
        throw ConnectionException("failed to connect graph client.")
      }
    }
    graphClient
  }

  /**
    * execute use space
    */
  def useSpace(client: AsyncGraphClientImpl, space: String): Unit = {
    val useSpace               = NebulaTemplate.USE_TEMPLATE.format(space)
    @transient val rateLimiter = RateLimiter.create(nebulaOptions.rateLimit)
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
      LOG.error(s"failed to acquire rateLimiter for statement {$useSpace}")
    }
  }
}

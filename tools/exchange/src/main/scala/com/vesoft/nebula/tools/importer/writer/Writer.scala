/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.importer.writer

import com.google.common.base.Optional
import com.google.common.util.concurrent.ListenableFuture
import com.vesoft.nebula.tools.importer.{Edges, Vertices}

/**
  *
  */
trait Writer extends Serializable {

  def prepare(): Unit

  def writeVertices(vertices: Vertices): ListenableFuture[Optional[Integer]]

  def writeEdges(edges: Edges): ListenableFuture[Optional[Integer]]

  def close()
}

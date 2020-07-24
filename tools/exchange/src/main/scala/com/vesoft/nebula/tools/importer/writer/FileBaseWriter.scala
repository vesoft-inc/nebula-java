/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.importer.writer

import com.google.common.base
import com.google.common.util.concurrent.ListenableFuture
import com.vesoft.nebula.tools.importer.{Edges, PartitionID, SchemaVersion, Vertices}
import com.vesoft.nebula.SchemaID
import org.rocksdb.{EnvOptions, Options, RocksDB, Slice, SstFileWriter}

/**
 *
 * @param path
 * @param partition
 * @param schema
 * @param version
 */
class NebulaSSTWriter(path: String,
                      partition: PartitionID,
                      schema: SchemaID,
                      version: SchemaVersion)
  extends Writer {
  require(path.trim.size != 0)

  RocksDB.loadLibrary

  val options = new Options().setCreateIfMissing(true)
  val env = new EnvOptions()
  val writer = new SstFileWriter(env, options)
  writer.open(path)

  override def prepare(): Unit = {

  }

  /**
   *
   * @param vertices
   */
  override def writeVertices(vertices: Vertices): ListenableFuture[base.Optional[Integer]] = ???

  /**
   *
   * @param edges
   */
  override def writeEdges(edges: Edges): ListenableFuture[base.Optional[Integer]] = ???

  override def close(): Unit = {
    writer.finish()
    env.close()
    options.close()
    writer.close()
  }

}

/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.importer.writer

import org.rocksdb.{
  EnvOptions,
  Options,
  RocksDB,
  SstFileWriter
}
import org.slf4j.LoggerFactory

/**
  *
  * @param path
  */
class NebulaSSTWriter(path: String) extends Writer {
  require(path.trim.size != 0)

  private val LOG = LoggerFactory.getLogger(getClass)

  try {
    RocksDB.loadLibrary()
    LOG.info("Loading RocksDB successfully")
  } catch {
    case _: Exception =>
      LOG.error("Can't load RocksDB library!")
  }

  // TODO More Config ...
  val options = new Options()
    .setCreateIfMissing(true)

  val env                   = new EnvOptions()
  var writer: SstFileWriter = _

  override def prepare(): Unit = {
    writer = new SstFileWriter(env, options)
    writer.open(path)
  }

  def write(key: Array[Byte], value: Array[Byte]): Unit = {
    writer.put(key, value)
  }

  override def close(): Unit = {
    writer.finish()
    writer.close()
    options.close()
    env.close()
  }
}

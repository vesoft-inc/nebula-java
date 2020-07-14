/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.importer

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}

import scala.collection.mutable.ArrayBuffer

object ErrorHandler {
  def save(buffer: ArrayBuffer[String], path: String): Unit = {
    val fileSystem = FileSystem.get(new Configuration())
    val errors     = fileSystem.create(new Path(path))

    try {
      for (error <- buffer) {
        errors.writeBytes(error)
        errors.writeBytes("\n")
      }
    } finally {
      errors.close()
      fileSystem.close()
    }
  }
}

/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.importer.utils

import java.net.URI
import java.nio.charset.Charset

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}

import scala.io.Source

object HDFSUtils {

  def getFileSystem(path: String): FileSystem =
    FileSystem.get(URI.create(path), new Configuration())

  def list(path: String): List[String] = {
    val system = getFileSystem(path)
    system.listStatus(new Path(path)).map(_.getPath.getName).toList
  }

  def getContent(path: String): String = {
    val system      = getFileSystem(path)
    val inputStream = system.open(new Path(path))
    Source.fromInputStream(inputStream).mkString
  }

  def saveContent(path: String,
                  content: String,
                  charset: Charset = Charset.defaultCharset()): Unit = {
    val system       = getFileSystem(path)
    val outputStream = system.create(new Path(path))
    outputStream.write(content.getBytes(charset))
  }
}

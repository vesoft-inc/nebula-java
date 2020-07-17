/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.importer.utils

import java.nio.charset.Charset

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}

import scala.io.Source

object HDFSUtils {

  def getFileSystem(): FileSystem =
    FileSystem.get(new Configuration())

  def list(path: String): List[String] = {
    val system = getFileSystem()
    try {
      system.listStatus(new Path(path)).map(_.getPath.getName).toList
    } finally {
      system.close()
    }
  }

  def exists(path: String): Boolean = {
    val system = getFileSystem()
    try {
      system.exists(new Path(path))
    } finally {
      system.close()
    }
  }

  def getContent(path: String): String = {
    val system      = getFileSystem()
    val inputStream = system.open(new Path(path))
    try {
      Source.fromInputStream(inputStream).mkString
    } finally {
      system.close()
    }
  }

  def saveContent(path: String,
                  content: String,
                  charset: Charset = Charset.defaultCharset()): Unit = {
    val system       = getFileSystem()
    val outputStream = system.create(new Path(path))
    try {
      outputStream.write(content.getBytes(charset))
    } finally {
      outputStream.close()
    }
  }

  def upload(localPath: String, remotePath: String): Unit = {
    val system = getFileSystem()
    try {
      system.copyFromLocalFile(new Path(localPath), new Path(remotePath))
    } finally {
      system.close()
    }
  }
}

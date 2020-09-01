/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.importer.config

/**
  * SinkCategory is use to expression the writer's type.
  * Current support client.
  */
object SinkCategory extends Enumeration {
  type Type = Value

  val CLIENT = Value("CLIENT")
  val SST    = Value("SST")
}

class SinkCategory

/**
  * DataSinkConfigEntry
  */
sealed trait DataSinkConfigEntry {
  def category: SinkCategory.Value
}

/**
  * FileBaseSinkConfigEntry
  *
  */
case class FileBaseSinkConfigEntry(override val category: SinkCategory.Value,
                                   localPath: String,
                                   remotePath: String)
    extends DataSinkConfigEntry {
  override def toString: String = {
    s"File sink: from ${localPath} to ${remotePath}"
  }
}

/**
  * NebulaSinkConfigEntry use to specified the nebula service's address.
  */
case class NebulaSinkConfigEntry(override val category: SinkCategory.Value, addresses: List[String])
    extends DataSinkConfigEntry {
  override def toString: String = {
    s"Nebula sink addresses: ${addresses.mkString("[", ", ", "]")}"
  }
}

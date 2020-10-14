/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.connector.edges

import java.util.Optional

import com.vesoft.nebula.tools.connector.{MetaProvider, NebulaVertexReader}
import org.apache.spark.sql.SaveMode
import org.apache.spark.sql.sources.DataSourceRegister
import org.apache.spark.sql.sources.v2.reader.DataSourceReader
import org.apache.spark.sql.sources.v2.writer.DataSourceWriter
import org.apache.spark.sql.sources.v2.{DataSourceOptions, DataSourceV2, ReadSupport, WriteSupport}
import org.apache.spark.sql.types.StructType

class DefaultSource()
    extends DataSourceV2
    with ReadSupport
    with WriteSupport
    with DataSourceRegister {

  override def createReader(options: DataSourceOptions): DataSourceReader = {
    val metaProvider = new MetaProvider(List("127.0.0.1" -> 8989))
    val schema       = metaProvider.getTagSchema("space", "tag name")

    new NebulaVertexReader(List("127.0.0.1" -> 8989), "space", "tag name", "")
  }

  override def createWriter(writeUUID: String,
                            schema: StructType,
                            mode: SaveMode,
                            options: DataSourceOptions): Optional[DataSourceWriter] = ???

  override def shortName(): String = "nebula edges"

}

/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.connector

import java.util

import com.vesoft.nebula.ValueType
import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.sources.v2.reader.{DataSourceReader, InputPartition}
import org.apache.spark.sql.types.{DataType, StringType, StructField, StructType}

import scala.collection.JavaConverters._

class NebulaVertexReader(addresses: List[Address], space: String, tag: String, fields: String)
    extends DataSourceReader {

  val metaProvider = new MetaProvider(addresses)

  /**
    * Reading from Nebula Meta Service
    * @return
    */
  override def readSchema(): StructType = {
    val schema = metaProvider.getTagSchema(space, tag)
    StructType(schema.map(field => StructField(field._1, field._2)))
//    val fields = for (column <- schema.columns.asScala)
//      yield StructField(column.name, extraType(column.`type`))
//    StructType(fields)
  }

  /**
    *
    * @return
    */
  override def planInputPartitions(): util.List[InputPartition[InternalRow]] = {
    val partitions = for (index <- 1 to 9)
      yield new NebulaVertexPartition(space, index, tag, fields)
    partitions.map(_.asInstanceOf[InputPartition[InternalRow]]).asJava
  }

  def extraType(valueType: ValueType): DataType = {
    StringType
  }
}

/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.connector

import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.sources.v2.reader.InputPartitionReader

import scala.collection.JavaConverters._

/**
  *
  */
abstract class NebulaPartitionReader(addresses: List[Address], space: String, part: Int)
    extends InputPartitionReader[InternalRow] {

  protected val storageProvider = new StorageProvider(addresses, space, part)

  lazy val rows: Iterator[InternalRow] = Seq(
    InternalRow.fromSeq(Array(0, 1, 2)),
    InternalRow.fromSeq(Array(10, 11, 12)),
    InternalRow.fromSeq(Array(20, 21, 22))
  ).toIterator

  override def next(): Boolean = rows.hasNext

  override def get(): InternalRow = rows.next

  override def close(): Unit = storageProvider.close()
}

final class NebulaVertexPartitionReader(addresses: List[Address],
                                        space: String,
                                        part: Int,
                                        tag: String,
                                        fields: List[String])
    extends NebulaPartitionReader(addresses, space, part) {

  val iterator = storageProvider.fetchVertices(tag, fields)

  override def next(): Boolean = iterator.hasNext

  override def get(): InternalRow = {
    val response = iterator.next()
//    response.vertex_schema.asScala.map()
    InternalRow.empty
  }
}

final class NebulaEdgePartitionReader(addresses: List[Address],
                                      space: String,
                                      part: Int,
                                      edge: String,
                                      fields: List[String])
    extends NebulaPartitionReader(addresses, space, part) {

  val iterator = storageProvider.fetchEdges(edge, fields)

  override def next(): Boolean = iterator.hasNext

  override def get(): InternalRow = {
    val response = iterator.next()
    InternalRow.empty
  }
}

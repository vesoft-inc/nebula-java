/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.connector

import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.sources.v2.reader.{InputPartition, InputPartitionReader}

import scala.collection.mutable.ListBuffer

class NebulaVertexPartition(space: String, part: Int, tag: String, fields: String)
    extends InputPartition[InternalRow] {
  override def createPartitionReader(): InputPartitionReader[InternalRow] =
    new NebulaVertexPartitionReader(Nil, space, part, tag, fields.split(",").toList)
}

class NebulaEdgePartition(space: String, part: Int, edge: String, fields: String)
    extends InputPartition[InternalRow] {
  override def createPartitionReader(): InputPartitionReader[InternalRow] =
    new NebulaEdgePartitionReader(Nil, space, part, edge, fields.split(",").toList)
}

/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

import org.apache.spark.Partition

//class NebulaPartition(var indexVar: Int) extends Partition {
//  override def index: Int = indexVar
//
//  /**
//    * allocate scanPart to partition
//    *
//    * @param totalPart nebula data part num
//    * @return scan data part list
//    */
//  def getScanParts(totalPart: Int, totalPartition: Int): List[Integer] = {
//    val scanParts: ListBuffer[Integer] = new ListBuffer[Integer]
//    var currentPart                    = index + 1
//    while ({
//      currentPart <= totalPart
//    }) {
//      scanParts.append(currentPart)
//      currentPart += totalPartition
//    }
//    scanParts.toList
//  }
//}

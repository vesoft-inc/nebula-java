/*
 * Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.connector.reader

import com.vesoft.nebula.common.Type
import com.vesoft.nebula.tools.connector.NebulaOptions
import org.apache.spark.{Partition, TaskContext}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.types.StructType

import scala.collection.mutable.ListBuffer

class NebulaRDD(val sqlContext: SQLContext, var nebulaOptions: NebulaOptions, schema: StructType)
    extends RDD[InternalRow](sqlContext.sparkContext, Nil) {

  /**
    * start to scan vertex or edge data
    *
    * @param split
    * @param context
    * @return Iterator<InternalRow>
    */
  override def compute(split: Partition, context: TaskContext): Iterator[InternalRow] = {
    val dataType = nebulaOptions.dataType
    if (Type.VERTEX.getType.equalsIgnoreCase(dataType))
      new NebulaVertexIterator(split, nebulaOptions, schema)
    else new NebulaEdgeIterator(split, nebulaOptions, schema)
  }

  override def getPartitions = {
    val partitionNumber = nebulaOptions.partitionNums.toInt
    val partitions      = new Array[Partition](partitionNumber)
    for (i <- 0 until partitionNumber) {
      partitions(i) = NebulaPartition(i)
    }
    partitions
  }
}

/**
  * An identifier for a partition in an NebulaRDD.
  */
case class NebulaPartition(indexNum: Int) extends Partition {
  override def index: Int = indexNum

  /**
    * allocate scanPart to partition
    *
    * @param totalPart nebula data part num
    * @return scan data part list
    */
  def getScanParts(totalPart: Int, totalPartition: Int): List[Integer] = {
    val scanParts   = new ListBuffer[Integer]
    var currentPart = indexNum + 1
    while (currentPart <= totalPart) {
      scanParts.append(currentPart)
      currentPart += totalPartition
    }
    scanParts.toList
  }
}

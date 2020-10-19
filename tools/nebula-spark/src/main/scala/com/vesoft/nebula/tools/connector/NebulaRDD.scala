/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.connector

import com.vesoft.nebula.tools.connector.reader.NebulaPartition
import org.apache.spark.{Partition, SparkContext, TaskContext}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.Row
import org.slf4j.LoggerFactory

class NebulaVerticesRDD(context: SparkContext, space: String, tag: String)
    extends RDD[Row](context, Nil) {
  private val logger = LoggerFactory.getLogger(this.getClass)

  override def compute(split: Partition, context: TaskContext): Iterator[Row] = ???

  override protected def getPartitions: Array[Partition] = {
    val provider = new MetaProvider(Nil)
    (1 to provider.getPartition(space).size())
      .map(NebulaPartition(_).asInstanceOf[Partition])
      .toArray
  }
}

class NebulaEdgesRDD(context: SparkContext) extends RDD[Row](context, Nil) {
  override def compute(split: Partition, context: TaskContext): Iterator[Row] = ???

  override protected def getPartitions: Array[Partition] = ???
}

/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.connector.reader

import java.util

import com.vesoft.nebula.client.meta.MetaClientImpl
import com.vesoft.nebula.client.storage.StorageClientImpl
import com.vesoft.nebula.client.storage.processor.Processor
import com.vesoft.nebula.data.Result
import com.vesoft.nebula.tools.connector.{NebulaOptions, NebulaPartition, NebulaUtils}
import org.apache.spark
import org.apache.spark.Partition
import org.apache.spark.sql.{Row, RowFactory, SparkSession}
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.{AbstractIterator, mutable}
import scala.collection.JavaConverters._

abstract class AbstractNebulaIterator extends AbstractIterator[Row] {
  private val LOGGER: Logger = LoggerFactory.getLogger(classOf[AbstractNebulaIterator])

  protected var dataIterator: Iterator[String]             = _
  protected var scanPartIterator: Iterator[Integer]        = _
  protected var resultValues: mutable.Map[String, List[_]] = mutable.Map[String, List[_]]()

  protected var storageClient: StorageClientImpl = _
  protected var metaClient: MetaClientImpl       = _
  protected var processor: Processor[_]          = _

  protected var returnCols: util.Map[String, util.List[String]] = new util.HashMap()

  def this(split: Partition, nebulaOptions: NebulaOptions) {
    this()

    nebulaOptions.getReturnColMap
      .foreach(entry => {
        this.returnCols.put(entry._1, entry._2.asJava)
      })

    this.metaClient = NebulaUtils.createMetaClient(nebulaOptions.getHostAndPorts)
    this.storageClient = NebulaUtils.createStorageClient(metaClient)

    // allocate scanPart to this partition
    val totalPart       = metaClient.getPartsAlloc(nebulaOptions.spaceName).size
    val nebulaPartition = split.asInstanceOf[NebulaPartition]
    val scanParts       = nebulaPartition.getScanParts(totalPart, nebulaOptions.partitionNums.toInt)
    LOGGER.info("partition index: {}, scanPart: {}", split.index, scanParts.toString)
    scanPartIterator = scanParts.iterator
  }

  override def hasNext: Boolean

  override def next(): Row = {
    RowFactory.create(resultValues.get(dataIterator.next()).toArray)
  }

  protected def process(result: Result[com.vesoft.nebula.data.Row]): Iterator[String]
}

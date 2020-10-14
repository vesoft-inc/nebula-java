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
import com.vesoft.nebula.data.{Property, Result}
import com.vesoft.nebula.tools.connector.NebulaUtils.NebulaValueGetter
import com.vesoft.nebula.tools.connector.{NebulaOptions, NebulaUtils}
import org.apache.avro.generic.GenericData.StringType
import org.apache.spark.Partition
import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.catalyst.encoders.RowEncoder
import org.apache.spark.sql.catalyst.expressions.SpecificInternalRow
import org.apache.spark.sql.types.{
  BooleanType,
  CharType,
  DataTypes,
  DoubleType,
  LongType,
  StructType
}
import org.apache.spark.sql.{Row, RowFactory}
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.{AbstractIterator, mutable}
import scala.collection.JavaConverters._

abstract class AbstractNebulaIterator extends Iterator[InternalRow] {
  private val LOGGER: Logger = LoggerFactory.getLogger(classOf[AbstractNebulaIterator])

  protected var dataIterator: Iterator[String]      = _
  protected var scanPartIterator: Iterator[Integer] = _

  protected var resultValues: mutable.Map[String, List[Property]] =
    mutable.Map[String, List[Property]]()
  protected var storageClient: StorageClientImpl                = _
  protected var metaClient: MetaClientImpl                      = _
  protected var processor: Processor[_]                         = _
  protected var returnCols: util.Map[String, util.List[String]] = new util.HashMap()

  private var schema: StructType = _

  def this(split: Partition, nebulaOptions: NebulaOptions, schema: StructType) {
    this()
    this.schema = schema

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

  override def next(): InternalRow = {
    val getters: Array[NebulaValueGetter] = NebulaUtils.makeGetters(schema)
    val mutableRow                        = new SpecificInternalRow(schema.fields.map(x => x.dataType))

    val resultSet: Array[Property] = resultValues(dataIterator.next()).toArray
    for (i <- getters.indices) {
      getters(i).apply(resultSet(i), mutableRow, i)
      if (resultSet(i) == null) mutableRow.setNullAt(i)
    }
    mutableRow
  }

  protected def process(result: Result[com.vesoft.nebula.data.Row]): Iterator[String]
}

/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.connector.reader

import java.util

import com.vesoft.nebula.client.storage.processor.ScanVertexProcessor
import com.vesoft.nebula.data.{Property, Result, Row}
import com.vesoft.nebula.exception.GraphOperateException
import com.vesoft.nebula.storage.ScanVertexResponse
import com.vesoft.nebula.tools.connector.{NebulaOptions, NebulaUtils}
import org.apache.spark.Partition
import org.apache.spark.sql.types.StructType
import org.slf4j.LoggerFactory

import scala.collection.mutable.ListBuffer

/**
  * iterator to read nebula's vertex data
  */
class NebulaVertexIterator(split: Partition, nebulaOptions: NebulaOptions, schema: StructType)
    extends AbstractNebulaIterator(split, nebulaOptions, schema) {
  private val LOG = LoggerFactory.getLogger(this.getClass)

  private var responseIterator: util.Iterator[ScanVertexResponse] = _

  override def hasNext: Boolean = {
    if (dataIterator == null && responseIterator == null && !scanPartIterator.hasNext)
      return false

    var continue: Boolean = false
    var break: Boolean    = false
    while ((dataIterator == null || !dataIterator.hasNext) && !break) {
      resultValues.clear()
      continue = false
      if (responseIterator == null || !responseIterator.hasNext) {
        if (scanPartIterator.hasNext) {
          try responseIterator = storageClient.scanVertex(nebulaOptions.spaceName,
                                                          scanPartIterator.next,
                                                          returnCols,
                                                          nebulaOptions.allCols,
                                                          1000,
                                                          0L,
                                                          Long.MaxValue)
          catch {
            case e: Exception =>
              LOG.error(s"Exception scanning vertex ${nebulaOptions.label}", e)
              NebulaUtils.closeMetaClient(metaClient)
              throw new GraphOperateException(e.getMessage, e)
          }
          // jump to the next loop
          continue = true
        }
        // break while loop
        break = !continue
      } else {
        val next: ScanVertexResponse = responseIterator.next
        if (next != null) {
          val processResult: Result[Row] = new ScanVertexProcessor(metaClient)
            .process(nebulaOptions.spaceName, next)
            .asInstanceOf[Result[Row]]
          dataIterator = process(processResult)
        }
      }
    }

    if (dataIterator == null) {
//      if (storageClient != null) storageClient.close()
//      if (metaClient != null) metaClient.close()
      return false
    }
    dataIterator.hasNext
  }

  override def process(result: Result[Row]): Iterator[List[Property]] = {
    val dataMap: util.Map[String, util.List[Row]] = result.getRows
    import scala.collection.JavaConversions._
    for (dataEntry <- dataMap.entrySet) {
      for (row <- dataEntry.getValue) {
        val fields: ListBuffer[Property] = new ListBuffer[Property]()
        // add default property _vertexId for tag
        fields.append(row.getDefaultProperties()(0))
        val properties: Array[Property] = row.getProperties
        for (i <- properties.indices) {
          fields.append(properties(i))
        }
        resultValues.append(fields.toList)
      }
    }
    resultValues.iterator
  }
}

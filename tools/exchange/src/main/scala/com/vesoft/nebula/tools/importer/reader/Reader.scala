/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.importer.reader

import com.vesoft.nebula.tools.importer.Offset
import com.vesoft.nebula.tools.importer.utils.HDFSUtils
import org.apache.log4j.Logger
import org.apache.spark.sql.{DataFrame, SparkSession}

/**
  * The Reader is used for create a DataFrame from the source. Such as Hive or HDFS.
  */
trait Reader extends Serializable {
  def session: SparkSession

  def read(): DataFrame

  def close(): Unit
}

trait CheckPointSupport extends Serializable {

  def getOffsets(totalCount: Long,
                 parallel: Int,
                 checkPointPath: Option[String],
                 checkPointNamePrefix: String): List[Offset] = {
    if (totalCount <= 0)
      throw new RuntimeException(s"${checkPointNamePrefix}: return data count<=0")

    val batchSizes = List.fill((totalCount % parallel).toInt)(totalCount / parallel + 1) ::: List
      .fill((parallel - totalCount % parallel).toInt)(totalCount / parallel)

    val initEachPartitionOffset = {
      var offset = 0L
      0L :: (for (batchSize <- batchSizes.init) yield {
        offset += batchSize
        offset
      })
    }

    val eachPartitionOffset = checkPointPath match {
      case Some(path) =>
        val files = Range(0, parallel).map(i => s"${path}/${checkPointNamePrefix}.${i}").toList
        if (files.forall(x => HDFSUtils.exists(x)))
          files.map(file => HDFSUtils.getContent(file).trim.toLong).sorted
        else initEachPartitionOffset
      case _ => initEachPartitionOffset
    }

    val eachPartitionLimit = {
      batchSizes
        .zip(initEachPartitionOffset.zip(eachPartitionOffset))
        .map(x => {
          x._1 - (x._2._2 - x._2._1)
        })
    }
    val offsets = eachPartitionOffset.zip(eachPartitionLimit).map(x => Offset(x._1, x._2))
    if (offsets.exists(_.size < 0L))
      throw new RuntimeException(
        s"Your check point file maybe broken. Please delete ${checkPointNamePrefix}.* file")
    offsets
  }
}

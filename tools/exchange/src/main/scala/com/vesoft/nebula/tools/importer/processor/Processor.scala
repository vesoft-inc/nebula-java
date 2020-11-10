/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.importer.processor

import java.util.concurrent.{CountDownLatch, Executor}

import com.google.common.util.concurrent.Futures
import com.vesoft.nebula.tools.importer.config.SchemaConfigEntry
import com.vesoft.nebula.tools.importer.{CheckPointHandler, ProcessResult}
import com.vesoft.nebula.tools.importer.writer.NebulaWriterCallback
import com.vesoft.nebula.tools.importer.utils.HDFSUtils
import org.apache.log4j.Logger
import org.apache.spark.sql.Row
import org.apache.spark.sql.types.{ArrayType, BooleanType, DataType, DateType, DecimalType, DoubleType, FloatType, IntegerType, LongType, MapType, ShortType, StringType, TimestampType}
import org.apache.spark.util.LongAccumulator

trait Processor extends Serializable {

  @transient lazy val LOG = Logger.getLogger(this.getClass)

  def process(): Unit

  def collectFutures(futures: ProcessResult,
                     service: Executor,
                     schemaConfig: SchemaConfigEntry,
                     breakPointCount: Long,
                     batchSuccess: LongAccumulator,
                     batchFailure: LongAccumulator): Unit = {
    val pathAndOffset = CheckPointHandler.getPathAndOffset(schemaConfig, breakPointCount)

    val latch      = new CountDownLatch(futures.size)
    val allFutures = Futures.allAsList(futures: _*)
    Futures.addCallback(allFutures,
                        new NebulaWriterCallback(latch, batchSuccess, batchFailure, pathAndOffset),
                        service)
    latch.await()
    futures.clear()
  }

  def extraValue(row: Row, field: String, fieldTypeMap: Map[String, DataType], toBytes: Boolean = false): Any = {
    // TODO
    val index = row.schema.fieldIndex(field)
    fieldTypeMap(field) match {
      case StringType =>
        val result = if (!row.isNullAt(index)) {
          row.getAs[String](index).mkString("\"", "", "\"")
        } else {
          "\"\""
        }
        if (toBytes) result.getBytes else result
      case ShortType =>
        if (!row.isNullAt(index)) {
          row.getAs[Short](index)
        } else {
          0.toShort
        }
      case IntegerType =>
        if (!row.isNullAt(index)) {
          row.getAs[Int](index)
        } else {
          0
        }
      case LongType =>
        if (!row.isNullAt(index)) {
          row.getAs[Long](index)
        } else {
          0L
        }
      case FloatType =>
        if (!row.isNullAt(index)) {
          row.getAs[Float](index)
        } else {
          0.0.toFloat
        }
      case DoubleType =>
        if (!row.isNullAt(index)) {
          row.getAs[Double](index)
        } else {
          0.0
        }
      case _: DecimalType =>
        if (!row.isNullAt(index)) {
          row.getAs[Double](index)
        } else {
          0.0
        }
      case BooleanType =>
        if (!row.isNullAt(index)) {
          row.getAs[Boolean](index)
        } else {
          false
        }
      case TimestampType =>
        if (!row.isNullAt(index)) {
          row.getAs[TimestampType](index)
        } else {
          0L
        }
      case _: DateType =>
        if (!row.isNullAt(index)) {
          row.getDate(index).toString
        } else {
          "\"\""
        }
      case _: ArrayType =>
        if (!row.isNullAt(index)) {
          row.getSeq(index).mkString("\"[", ",", "]\"")
        } else {
          "\"[]\""
        }
      case _: MapType =>
        if (!row.isNullAt(index)) {
          row.getMap(index).mkString("\"{", ",", "}\"")
        } else {
          "\"{}\""
        }
    }
  }

  def fetchOffset(path: String): Long = {
    HDFSUtils.getContent(path).toLong
  }

  def getLong(row: Row, field: String): Long = {
    val index = row.schema.fieldIndex(field)
    row.schema.fields(index).dataType match {
      case LongType    => row.getLong(index)
      case IntegerType => row.getInt(index).toLong
      case StringType  => row.getString(index).toLong
    }
  }
}

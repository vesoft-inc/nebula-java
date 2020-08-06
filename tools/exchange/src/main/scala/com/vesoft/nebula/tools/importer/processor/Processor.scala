/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.importer.processor

import java.util.concurrent.{CountDownLatch, Executor}

import com.google.common.util.concurrent.Futures
import com.vesoft.nebula.tools.importer.{CheckPointHandler, ProcessResult, SchemaConfigEntry}
import com.vesoft.nebula.tools.importer.writer.NebulaWriterCallback
import org.apache.spark.sql.Row
import org.apache.spark.sql.types.{
  ArrayType,
  BooleanType,
  DateType,
  DecimalType,
  DoubleType,
  FloatType,
  IntegerType,
  LongType,
  MapType,
  ShortType,
  StringType,
  TimestampType
}
import org.apache.spark.util.LongAccumulator

trait Processor extends Serializable {

  def process(): Unit

  def waitingFuturesFinish(futures: ProcessResult,
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

  def extraValue(row: Row, field: String): Any = {
    // TODO
    val index = row.schema.fieldIndex(field)
    row.schema.fields(index).dataType match {
      case StringType =>
        if (!row.isNullAt(index)) {
          row.getString(index).mkString("\"", "", "\"")
        } else {
          "\"\""
        }
      case ShortType =>
        if (!row.isNullAt(index)) {
          row.getShort(index).toString
        } else {
          "0"
        }
      case IntegerType =>
        if (!row.isNullAt(index)) {
          row.getInt(index).toString
        } else {
          "0"
        }
      case LongType =>
        if (!row.isNullAt(index)) {
          row.getLong(index).toString
        } else {
          "0"
        }
      case FloatType =>
        if (!row.isNullAt(index)) {
          row.getFloat(index).toString
        } else {
          "0.0"
        }
      case DoubleType =>
        if (!row.isNullAt(index)) {
          row.getDouble(index).toString
        } else {
          "0.0"
        }
      case _: DecimalType =>
        if (!row.isNullAt(index)) {
          row.getDecimal(index).toString
        } else {
          "0.0"
        }
      case BooleanType =>
        if (!row.isNullAt(index)) {
          row.getBoolean(index).toString
        } else {
          "false"
        }
      case TimestampType =>
        if (!row.isNullAt(index)) {
          row.getTimestamp(index).getTime
        } else {
          "0"
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
}

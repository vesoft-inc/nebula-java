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
import org.apache.commons.lang.StringEscapeUtils
import org.apache.log4j.Logger
import org.apache.spark.sql.Row
import org.apache.spark.sql.types.{
  ArrayType,
  BooleanType,
  DataType,
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

  def extraValue(row: Row,
                 field: String,
                 fieldTypeMap: Map[String, DataType],
                 isImplicit: Boolean,
                 toBytes: Boolean = false): Any = {
    // TODO
    val index = row.schema.fieldIndex(field)

    if (!isImplicit) {
      if (!row.isNullAt(index)) {
        throw new IllegalArgumentException(
          s"${field} has no value, Nebula does not support null value yet")
      }
      row.schema.fields(index).dataType match {
        case StringType => {
          val result = if (StringEscapeUtils.escapeJava(row.getString(index)).contains('\\')) {
            StringEscapeUtils.escapeJava(row.getString(index)).mkString("\"", "", "\"")
          } else {
            row.getString(index).mkString("\"", "", "\"")
          }
          if (toBytes) return result.getBytes else return result
        }
        case _: Any => {
          return row.get(index)
        }
      }
    }

    row.schema.fields(index).dataType match {
      case StringType => {
        fieldTypeMap(field) match {
          case StringType =>
            val result = if (!row.isNullAt(index)) {
              if (StringEscapeUtils.escapeJava(row.getString(index)).contains('\\')) {
                StringEscapeUtils.escapeJava(row.getString(index)).mkString("\"", "", "\"")
              } else {
                row.getString(index).mkString("\"", "", "\"")
              }
            } else {
              "\"\""
            }
            if (toBytes) result.getBytes else result

          case LongType =>
            if (!row.isNullAt(index)) {
              row.getString(index).toLong
            } else {
              0L
            }
          case DoubleType =>
            if (!row.isNullAt(index)) {
              row.getString(index).toDouble
            } else {
              0.0
            }
          case BooleanType =>
            if (!row.isNullAt(index)) {
              row.getString(index).toBoolean
            }
        }
      }

      case ShortType   => {
        fieldTypeMap(field) match {
          case StringType =>
            val result = if (!row.isNullAt(index)) {
              row.getShort(index).toString.mkString("\"", "", "\"")
            } else {
              "\"\""
            }
            if (toBytes) result.getBytes else result
          case LongType =>
            if (!row.isNullAt(index)) {
              row.getShort(index)
            } else {
              0L
            }
          case DoubleType =>
            if (!row.isNullAt(index)) {
              row.getShort(index).toDouble
            } else {
              0.0
            }
          case BooleanType =>
            if (!row.isNullAt(index)) {
              row.getShort(index).toString.toBoolean
            } else {
              false
            }
        }
      }

      case IntegerType => {
        fieldTypeMap(field) match {
          case StringType =>
            val result = if (!row.isNullAt(index)) {
              row.getInt(index).toString.mkString("\"", "", "\"")
            } else {
              "\"\""
            }
            if (toBytes) result.getBytes else result
          case LongType =>
            if (!row.isNullAt(index)) {
              row.getInt(index)
            } else {
              0L
            }
          case DoubleType =>
            if (!row.isNullAt(index)) {
              row.getInt(index).toDouble
            } else {
              0.0
            }
          case BooleanType =>
            if (!row.isNullAt(index)) {
              row.getInt(index).toString.toBoolean
            } else {
              false
            }
        }
      }

      case LongType => {
        fieldTypeMap(field) match {
          case StringType =>
            val result = if (!row.isNullAt(index)) {
              row.getLong(index).toString.mkString("\"", "", "\"")
            } else {
              "\"\""
            }
            if (toBytes) result.getBytes else result
          case LongType =>
            if (!row.isNullAt(index)) {
              row.getLong(index)
            } else {
              0L
            }
          case DoubleType =>
            if (!row.isNullAt(index)) {
              row.getLong(index).toDouble
            } else {
              0.0
            }
          case BooleanType =>
            if (!row.isNullAt(index)) {
              row.getLong(index).toString.toBoolean
            } else {
              false
            }
        }
      }

      case FloatType => {
        fieldTypeMap(field) match {
          case StringType =>
            val result = if (!row.isNullAt(index)) {
              row.getFloat(index).toString.mkString("\"", "", "\"")
            } else {
              "\"\""
            }
            if (toBytes) result.getBytes else result
          case LongType =>
            if (!row.isNullAt(index)) {
              row.getFloat(index).toLong
            } else {
              0L
            }
          case DoubleType =>
            if (!row.isNullAt(index)) {
              row.getFloat(index)
            } else {
              0.0
            }
          case BooleanType =>
            if (!row.isNullAt(index)) {
              row.getFloat(index).toString.toBoolean
            } else {
              false
            }
        }
      }

      case DoubleType => {
        fieldTypeMap(field) match {
          case StringType =>
            val result = if (!row.isNullAt(index)) {
              row.getDouble(index).toString.mkString("\"", "", "\"")
            } else {
              "\"\""
            }
            if (toBytes) result.getBytes else result
          case LongType =>
            if (!row.isNullAt(index)) {
              row.getDouble(index).toLong
            } else {
              0L
            }
          case DoubleType =>
            if (!row.isNullAt(index)) {
              row.getDouble(index)
            } else {
              0.0
            }
          case BooleanType =>
            if (!row.isNullAt(index)) {
              row.getDouble(index).toString.toBoolean
            } else {
              false
            }
        }
      }

      case BooleanType => {
        fieldTypeMap(field) match {
          case StringType =>
            val result = if (!row.isNullAt(index)) {
              row.getBoolean(index).toString.mkString("\"", "", "\"")
            } else {
              "\"\""
            }
            if (toBytes) result.getBytes else result
          case LongType =>
            if (!row.isNullAt(index)) {
              if (row.getBoolean(index)) 1L else 0L
            } else {
              0L
            }
          case DoubleType =>
            if (!row.isNullAt(index)) {
              if (row.getBoolean(index)) 1.0 else 0.0
            } else {
              0.0
            }
          case BooleanType =>
            if (!row.isNullAt(index)) {
              row.getBoolean(index)
            } else {
              false
            }
        }
      }

      case TimestampType => {
        fieldTypeMap(field) match {
          case StringType =>
            if (!row.isNullAt(index)) {
              row.getTimestamp(index).toString.mkString("\"", "", "\"")
            } else {
              "\"\""
            }
          case LongType =>
            if (!row.isNullAt(index)) {
              row.getTimestamp(index).toString.toLong
            } else {
              0L
            }
          case _ =>
            throw new IllegalArgumentException(
              s"field ${field} doesn't have correct datatype in nebula graph")
        }
      }

      case _: DateType => {
        fieldTypeMap(field) match {
          case StringType =>
            if (!row.isNullAt(index)) {
              row.getDate(index).toString.mkString("\"", "", "\"")
            } else {
              "\"\""
            }
          case _ =>
            throw new IllegalArgumentException(
              s"field ${field} doesn't have correct datatype in nebula graph")
        }
      }

      case _: ArrayType => {
        fieldTypeMap(field) match {
          case StringType =>
            if (!row.isNullAt(index)) {
              row.getSeq(index).toString.mkString("\"[", ",", "]\"")
            } else {
              "\"[]\""
            }
        }
      }
      case _: MapType => {
        fieldTypeMap(field) match {
          case StringType =>
            if (!row.isNullAt(index)) {
              row.getSeq(index).toString.mkString("\"{", ",", "}\"")
            } else {
              "\"{}\""
            }
        }
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

/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.importer.processor

import java.util.concurrent.{CountDownLatch, Executors, TimeUnit}

import com.google.common.geometry.{S2CellId, S2LatLng}
import com.google.common.util.concurrent.{Futures, MoreExecutors, RateLimiter}
import com.vesoft.nebula.tools.importer.{
  Configs,
  Edge,
  EdgeConfigEntry,
  Edges,
  ErrorHandler,
  ProcessResult,
  SourceCategory,
  TooManyErrorsException
}
import com.vesoft.nebula.tools.importer.writer.{NebulaGraphClientWriter, NebulaWriterCallback}
import org.apache.log4j.Logger
import org.apache.spark.TaskContext
import org.apache.spark.sql.{DataFrame, Encoders}
import org.apache.spark.util.LongAccumulator

import scala.collection.mutable.ArrayBuffer

class EdgeProcessor(data: DataFrame,
                    edgeConfig: EdgeConfigEntry,
                    fieldKeys: List[String],
                    nebulaKeys: List[String],
                    config: Configs,
                    batchSuccess: LongAccumulator,
                    batchFailure: LongAccumulator,
                    saveCheckPoint: Boolean = false)
    extends Processor {

  @transient lazy val LOG = Logger.getLogger(this.getClass)

  private[this] val DEFAULT_MIN_CELL_LEVEL = 10
  private[this] val DEFAULT_MAX_CELL_LEVEL = 18

  override def process(): Unit = {
    val checkPoint = if (!saveCheckPoint) {
      None
    } else {
      val context = data.sparkSession.sparkContext
      Some(context.longAccumulator(s"checkPoint.${edgeConfig.name}"))
    }

    data
      .map { row =>
        val sourceField = if (!edgeConfig.isGeo) {
          val sourceIndex = row.schema.fieldIndex(edgeConfig.sourceField)
          if (edgeConfig.sourcePolicy.isEmpty) {
            row.getLong(sourceIndex).toString
          } else {
            row.getString(sourceIndex)
          }
        } else {
          val lat = row.getDouble(row.schema.fieldIndex(edgeConfig.latitude.get))
          val lng = row.getDouble(row.schema.fieldIndex(edgeConfig.longitude.get))
          indexCells(lat, lng).mkString(",")
        }

        val targetIndex = row.schema.fieldIndex(edgeConfig.targetField)
        val targetField =
          if (edgeConfig.targetPolicy.isEmpty) {
            row.getLong(targetIndex).toString
          } else {
            row.getString(targetIndex)
          }

        val values = for {
          property <- fieldKeys if property.trim.length != 0
        } yield extraValue(row, property)

        if (edgeConfig.rankingField.isDefined) {
          val ranking = row.getLong(row.schema.fieldIndex(edgeConfig.rankingField.get))
          Edge(sourceField, targetField, Some(ranking), values)
        } else {
          Edge(sourceField, targetField, None, values)
        }
      }(Encoders.kryo[Edge])
      .foreachPartition { iterator: Iterator[Edge] =>
        val taskID = TaskContext.get.taskAttemptId

        // TODO Support Multi Writer
        val writer = new NebulaGraphClientWriter(config.databaseConfig,
                                                 config.userConfig,
                                                 config.connectionConfig,
                                                 config.executionConfig.retry,
                                                 edgeConfig)
        writer.prepare()

        val futures     = new ProcessResult()
        val errorBuffer = ArrayBuffer[String]()
        val rateLimiter = RateLimiter.create(config.rateConfig.limit)
        val service     = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(1))

        iterator.grouped(edgeConfig.batch).foreach { edge =>
          val edges =
            Edges(nebulaKeys, edge.toList, None, edgeConfig.sourcePolicy, edgeConfig.targetPolicy)

          if (rateLimiter.tryAcquire(config.rateConfig.timeout, TimeUnit.MILLISECONDS)) {
            val future = writer.writeEdges(edges)
            futures += future

            if (futures.size == 100) { // TODO configurable ?
              val pathAndOffset =
                if (edgeConfig.dataSourceConfigEntry.category == SourceCategory.NEO4J &&
                    edgeConfig.checkPointPath.isDefined) {
                  val offset = fetchOffset(s"${edgeConfig.checkPointPath}/${taskID}")
                  Some((edgeConfig.checkPointPath.get, offset))
                } else {
                  None
                }

              val latch      = new CountDownLatch(100)
              val allFutures = Futures.allAsList(futures: _*)
              Futures.addCallback(
                allFutures,
                new NebulaWriterCallback(latch, batchSuccess, batchFailure, pathAndOffset),
                service)
              latch.await()
              futures.clear()
            }
          } else {
            batchFailure.add(1)
            errorBuffer += writer.toExecuteSentence(edgeConfig.name, edges)
            if (errorBuffer.size == config.errorConfig.errorMaxSize) {
              throw TooManyErrorsException(s"Too Many Errors ${config.errorConfig.errorMaxSize}")
            }
          }

          if (!errorBuffer.isEmpty) {
            ErrorHandler.save(errorBuffer, s"${config.errorConfig.errorPath}/${edgeConfig.name}")
            errorBuffer.clear()
          }
        }

        if (!futures.isEmpty) {
          val pathAndOffset =
            if (edgeConfig.dataSourceConfigEntry.category == SourceCategory.NEO4J &&
              edgeConfig.checkPointPath.isDefined) {
              val offset = fetchOffset(s"${edgeConfig.checkPointPath}/${taskID}")
              Some((edgeConfig.checkPointPath.get, offset))
            } else {
              None
            }

          val latch      = new CountDownLatch(futures.size)
          val allFutures = Futures.allAsList(futures: _*)
          Futures.addCallback(
            allFutures,
            new NebulaWriterCallback(latch, batchSuccess, batchFailure, pathAndOffset),
            service)
          latch.await()
        }

        service.shutdown()
        while (!service.awaitTermination(100, TimeUnit.MILLISECONDS)) {
          Thread.sleep(10)
        }
      }

    if (checkPoint.isDefined) {
      LOG.info(s"checkPoint.${edgeConfig.name}: ${checkPoint.get.value}")
    }
  }

  private[this] def indexCells(lat: Double, lng: Double): IndexedSeq[Long] = {
    val coordinate = S2LatLng.fromDegrees(lat, lng)
    val s2CellId   = S2CellId.fromLatLng(coordinate)
    for (index <- DEFAULT_MIN_CELL_LEVEL to DEFAULT_MAX_CELL_LEVEL)
      yield s2CellId.parent(index).id()
  }
}

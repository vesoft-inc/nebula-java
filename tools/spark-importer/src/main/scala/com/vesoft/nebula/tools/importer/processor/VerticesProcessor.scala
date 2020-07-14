/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.importer.processor

import java.util.concurrent.{CountDownLatch, Executors, TimeUnit}

import com.google.common.util.concurrent.{Futures, MoreExecutors, RateLimiter}
import com.vesoft.nebula.tools.importer.{
  Configs,
  ErrorHandler,
  ProcessResult,
  TagConfigEntry,
  TooManyErrorsException,
  Vertex,
  Vertices
}
import com.vesoft.nebula.tools.importer.writer.{NebulaGraphClientWriter, NebulaWriterCallback}
import org.apache.spark.sql.types.LongType
import org.apache.spark.sql.{DataFrame, Encoders}
import org.apache.spark.util.LongAccumulator

import scala.collection.mutable.ArrayBuffer

/**
  *
  * @param data
  * @param tagConfig
  * @param fieldKeys
  * @param nebulaKeys
  * @param config
  * @param batchSuccess
  * @param batchFailure
  * @param saveCheckPoint
  */
class VerticesProcessor(data: DataFrame,
                        tagConfig: TagConfigEntry,
                        fieldKeys: List[String],
                        nebulaKeys: List[String],
                        config: Configs,
                        batchSuccess: LongAccumulator,
                        batchFailure: LongAccumulator,
                        saveCheckPoint: Boolean = false)
    extends Processor {
  override def process(): Unit = {
    val checkPoint = if (!saveCheckPoint) {
      None
    } else {
      val context = data.sparkSession.sparkContext
      Some(context.longAccumulator(s"checkPoint.${tagConfig.name}"))
    }

    data
      .map { row =>
        val vertexID =
          if (tagConfig.vertexPolicy.isEmpty) {
            val index = row.schema.fieldIndex(tagConfig.vertexField)
            if (row.schema.fields(index).dataType == LongType)
              row.getLong(index).toString
            else
              row.getString(index)
          } else {
            row.getString(row.schema.fieldIndex(tagConfig.vertexField))
          }

        val values = for {
          property <- fieldKeys if property.trim.length != 0
        } yield extraValue(row, property)
        Vertex(vertexID, values)
      }(Encoders.kryo[Vertex])
      .foreachPartition { iterator: Iterator[Vertex] =>
        // TODO Support Multi Writer
        val writer = new NebulaGraphClientWriter(config.databaseConfig,
                                                 config.userConfig,
                                                 config.connectionConfig,
                                                 config.executionConfig.retry,
                                                 tagConfig)
        writer.prepare()

        val futures     = new ProcessResult()
        val errorBuffer = ArrayBuffer[String]()
        val rateLimiter = RateLimiter.create(config.rateConfig.limit)
        val service     = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(1))
        iterator.grouped(tagConfig.batch).foreach { vertex =>
          val vertices = Vertices(nebulaKeys, vertex.toList, None, tagConfig.vertexPolicy)
          if (rateLimiter.tryAcquire(config.rateConfig.timeout, TimeUnit.MILLISECONDS)) {
            val future = writer.writeVertices(vertices)
            futures += future

            if (futures.size == 100) { // TODO configurable ?
              val latch = new CountDownLatch(100)
              for (future <- futures) {
                Futures.addCallback(future,
                                    new NebulaWriterCallback(latch,
                                                             batchSuccess,
                                                             batchFailure,
                                                             checkPoint,
                                                             tagConfig.batch),
                                    service)
              }
              latch.await()
              futures.clear()
            }
          } else {
            batchFailure.add(1)
            errorBuffer += writer.toExecuteSentence(tagConfig.name, vertices)
            if (errorBuffer.size == config.errorConfig.errorMaxSize) {
              throw TooManyErrorsException(s"Too Many Errors ${config.errorConfig.errorMaxSize}")
            }
          }

          if (!errorBuffer.isEmpty) {
            ErrorHandler.save(errorBuffer, s"${config.errorConfig.errorPath}/${tagConfig.name}")
            errorBuffer.clear()
          }
        }

        if (!futures.isEmpty) {
          val latch = new CountDownLatch(futures.size)
          for (future <- futures) {
            Futures.addCallback(future,
                                new NebulaWriterCallback(latch,
                                                         batchSuccess,
                                                         batchFailure,
                                                         checkPoint,
                                                         tagConfig.batch),
                                service)
          }
          latch.await()
        }
        service.shutdown()
        while (!service.awaitTermination(100, TimeUnit.MILLISECONDS)) {
          Thread.sleep(10)
        }
      }
  }
}

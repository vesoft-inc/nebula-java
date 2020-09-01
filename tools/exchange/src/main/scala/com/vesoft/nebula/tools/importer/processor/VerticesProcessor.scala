/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.importer.processor

import java.util.concurrent.{Executors, TimeUnit}

import com.google.common.util.concurrent.{MoreExecutors, RateLimiter}
import com.vesoft.nebula.tools.importer.config.{
  Configs,
  StreamingDataSourceConfigEntry,
  TagConfigEntry
}
import com.vesoft.nebula.tools.importer.{
  CheckPointHandler,
  ErrorHandler,
  ProcessResult,
  TooManyErrorsException,
  Vertex,
  Vertices
}
import com.vesoft.nebula.tools.importer.writer.NebulaGraphClientWriter
import org.apache.log4j.Logger
import org.apache.spark.sql.streaming.Trigger
import org.apache.spark.sql.types.{IntegerType, LongType}
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
  */
class VerticesProcessor(data: DataFrame,
                        tagConfig: TagConfigEntry,
                        fieldKeys: List[String],
                        nebulaKeys: List[String],
                        config: Configs,
                        batchSuccess: LongAccumulator,
                        batchFailure: LongAccumulator)
    extends Processor {

  @transient
  private[this] lazy val LOG = Logger.getLogger(this.getClass)

  private def processEachPartition(iterator: Iterator[Vertex]): Unit = {

    // TODO Support Multi Writer
    val writer = new NebulaGraphClientWriter(config.databaseConfig,
                                             config.userConfig,
                                             config.connectionConfig,
                                             config.executionConfig.retry,
                                             tagConfig)
    writer.prepare()

    val futures                 = new ProcessResult()
    val errorBuffer             = ArrayBuffer[String]()
    val rateLimiter             = RateLimiter.create(config.rateConfig.limit)
    val service                 = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(1))
    var breakPointVerticesCount = 0L

    iterator.grouped(tagConfig.batch).foreach { vertex =>
      val vertices = Vertices(nebulaKeys, vertex.toList, None, tagConfig.vertexPolicy)
      if (rateLimiter.tryAcquire(config.rateConfig.timeout, TimeUnit.MILLISECONDS)) {
        val future = writer.writeVertices(vertices)
        futures += future
        breakPointVerticesCount += vertices.values.length

        if (futures.size == 100) { // TODO configurable ?
          collectFutures(futures,
                         service,
                         tagConfig,
                         breakPointVerticesCount,
                         batchSuccess,
                         batchFailure)
          breakPointVerticesCount = 0L
        }
      } else {
        batchFailure.add(1)
        errorBuffer += writer.toExecuteSentence(tagConfig.name, vertices)
        if (errorBuffer.size == config.errorConfig.errorMaxSize) {
          throw TooManyErrorsException(s"Too Many Errors ${config.errorConfig.errorMaxSize}")
        }
        if (CheckPointHandler
              .checkSupportResume(tagConfig.dataSourceConfigEntry.category) && tagConfig.checkPointPath.isDefined) {
          throw new RuntimeException(s"Write tag ${tagConfig.name} errors")
        }
      }

      if (errorBuffer.nonEmpty) {
        ErrorHandler.save(errorBuffer, s"${config.errorConfig.errorPath}/${tagConfig.name}")
        errorBuffer.clear()
      }
    }

    if (futures.nonEmpty) {
      collectFutures(futures,
                     service,
                     tagConfig,
                     breakPointVerticesCount,
                     batchSuccess,
                     batchFailure)
      breakPointVerticesCount = 0L
    }
    service.shutdown()
    while (!service.awaitTermination(100, TimeUnit.MILLISECONDS)) {
      Thread.sleep(10)
    }
    writer.close()
  }

  override def process(): Unit = {
    val vertexDataFrame = data
      .map { row =>
        val vertexID =
          if (tagConfig.vertexPolicy.isEmpty) {
            val index = row.schema.fieldIndex(tagConfig.vertexField)
            row.schema.fields(index).dataType match {
              case LongType    => row.getLong(index).toString
              case IntegerType => row.getInt(index).toString
              case x           => throw new RuntimeException(s"Not support ${x} type use as vertex field")
            }
          } else {
            row.getString(row.schema.fieldIndex(tagConfig.vertexField))
          }

        val values = for {
          property <- fieldKeys if property.trim.length != 0
        } yield extraValue(row, property)
        Vertex(vertexID, values)
      }(Encoders.kryo[Vertex])

    if (data.isStreaming) {
      val streamingDataSourceConfig =
        tagConfig.dataSourceConfigEntry.asInstanceOf[StreamingDataSourceConfigEntry]
      vertexDataFrame.writeStream
        .foreachBatch((vertexSet, batchId) => {
          LOG.info(s"${tagConfig.name} tag start batch ${batchId}.")
          vertexSet.foreachPartition(processEachPartition _)
        })
        .trigger(Trigger.ProcessingTime(s"${streamingDataSourceConfig.intervalSeconds} seconds"))
        .start()
        .awaitTermination()
    } else
      vertexDataFrame.foreachPartition(processEachPartition _)
  }
}

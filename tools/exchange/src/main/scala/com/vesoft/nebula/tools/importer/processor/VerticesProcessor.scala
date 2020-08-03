/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.importer.processor

import java.util.concurrent.{CountDownLatch, Executors, TimeUnit}

import com.google.common.util.concurrent.{Futures, MoreExecutors, RateLimiter}
import com.vesoft.nebula.tools.importer.utils.HDFSUtils
import com.vesoft.nebula.tools.importer.{
  Configs,
  ErrorHandler,
  ProcessResult,
  SourceCategory,
  TagConfigEntry,
  TooManyErrorsException,
  Vertex,
  Vertices
}
import com.vesoft.nebula.tools.importer.writer.{NebulaGraphClientWriter, NebulaWriterCallback}
import org.apache.log4j.Logger
import org.apache.spark.TaskContext
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
  * @param checkPointPath
  */
class VerticesProcessor(data: DataFrame,
                        tagConfig: TagConfigEntry,
                        fieldKeys: List[String],
                        nebulaKeys: List[String],
                        config: Configs,
                        batchSuccess: LongAccumulator,
                        batchFailure: LongAccumulator,
                        checkPointPath: Option[String] = None)
    extends Processor {

  @transient lazy val LOG = Logger.getLogger(this.getClass)

  override def process(): Unit = {
    data
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
      .foreachPartition { iterator: Iterator[Vertex] =>
        val partitionId = TaskContext.getPartitionId()

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

              val pathAndOffset =
                if (tagConfig.dataSourceConfigEntry.category == SourceCategory.NEO4J &&
                    tagConfig.checkPointPath.isDefined) {
                  val path   = s"${tagConfig.checkPointPath.get}/${tagConfig.name}.${partitionId}"
                  val offset = breakPointVerticesCount + fetchOffset(path)
                  Some((path, offset))
                } else {
                  None
                }
              breakPointVerticesCount = 0L
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
            errorBuffer += writer.toExecuteSentence(tagConfig.name, vertices)
            if (errorBuffer.size == config.errorConfig.errorMaxSize) {
              throw TooManyErrorsException(s"Too Many Errors ${config.errorConfig.errorMaxSize}")
            }
            if (tagConfig.dataSourceConfigEntry.category == SourceCategory.NEO4J &&
                tagConfig.checkPointPath.isDefined) {
              throw new RuntimeException(s"Write tag ${tagConfig.name} errors")
            }
          }

          if (!errorBuffer.isEmpty) {
            ErrorHandler.save(errorBuffer, s"${config.errorConfig.errorPath}/${tagConfig.name}")
            errorBuffer.clear()
          }
        }

        if (!futures.isEmpty) {
          val pathAndOffset =
            if (tagConfig.dataSourceConfigEntry.category == SourceCategory.NEO4J &&
                tagConfig.checkPointPath.isDefined) {
              val path   = s"${tagConfig.checkPointPath.get}/${tagConfig.name}.${partitionId}"
              val offset = breakPointVerticesCount + fetchOffset(path)
              Some((path, offset))
            } else {
              None
            }

          breakPointVerticesCount = 0L
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
  }
}

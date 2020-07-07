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
  TooManyErrorsException
}
import com.vesoft.nebula.tools.importer.writer.{
  NebulaGraphClientWriter,
  NebulaWriterCallback,
  Writer
}
import org.apache.spark.sql.{DataFrame, Encoders}
import org.apache.spark.util.LongAccumulator

import scala.collection.mutable.ArrayBuffer

class EdgeProcessor(data: DataFrame,
                    edgeConfig: EdgeConfigEntry,
                    sourceProperties: List[String],
                    fieldValues: List[String],
                    config: Configs,
                    batchSuccess: LongAccumulator,
                    batchFailure: LongAccumulator,
                    saveCheckPoint: Boolean = false)
    extends Processor {

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
          if (edgeConfig.sourceField.isEmpty) {
            row.getLong(row.schema.fieldIndex(edgeConfig.sourceField)).toString
          } else {
            row.getString(row.schema.fieldIndex(edgeConfig.sourceField))
          }
        } else {
          val lat = row.getDouble(row.schema.fieldIndex(edgeConfig.latitude.get))
          val lng = row.getDouble(row.schema.fieldIndex(edgeConfig.longitude.get))
          indexCells(lat, lng).mkString(",")
        }

        val targetField =
          if (edgeConfig.targetField.isEmpty) {
            row.getLong(row.schema.fieldIndex(edgeConfig.targetField)).toString
          } else {
            row.getString(row.schema.fieldIndex(edgeConfig.targetField))
          }

        val values = for {
          property <- fieldValues if property.trim.length != 0
        } yield extraValue(row, property)

        if (edgeConfig.rankingField.isDefined) {
          val ranking = row.getLong(row.schema.fieldIndex(edgeConfig.rankingField.get))
          Edge(sourceField, targetField, Some(ranking), values)
        } else {
          Edge(sourceField, targetField, None, values)
        }
      }(Encoders.kryo[Edge])
      .foreachPartition { iterator: Iterator[Edge] =>
        // TODO Support Multi Writer
        val writer = new NebulaGraphClientWriter(config.databaseConfig,
                                                 config.userConfig,
                                                 config.connectionConfig,
                                                 config.executionConfig.retry,
                                                 edgeConfig)

        val futures     = new ProcessResult()
        val errorBuffer = ArrayBuffer[String]()
        val rateLimiter = RateLimiter.create(config.rateConfig.limit)
        val service     = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(1))

        iterator.grouped(edgeConfig.batch).foreach { edge =>
          val edges = Edges(sourceProperties,
                            edge.toList,
                            None,
                            edgeConfig.sourcePolicy,
                            edgeConfig.targetPolicy)

          if (rateLimiter.tryAcquire(config.rateConfig.timeout, TimeUnit.MILLISECONDS)) {
            val future = writer.writeEdges(edges)
            futures += future

            if (futures.size == 100) { // TODO configurable ?
              val latch = new CountDownLatch(100)
              for (future <- futures) {
                Futures.addCallback(future,
                                    new NebulaWriterCallback(latch,
                                                             batchSuccess,
                                                             batchFailure,
                                                             checkPoint,
                                                             edgeConfig.batch),
                                    service)
              }
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
          val latch = new CountDownLatch(futures.size)
          for (future <- futures) {
            Futures.addCallback(future,
                                new NebulaWriterCallback(latch,
                                                         batchSuccess,
                                                         batchFailure,
                                                         checkPoint,
                                                         edgeConfig.batch),
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

  private[this] def indexCells(lat: Double, lng: Double): IndexedSeq[Long] = {
    val coordinate = S2LatLng.fromDegrees(lat, lng)
    val s2CellId   = S2CellId.fromLatLng(coordinate)
    for (index <- DEFAULT_MIN_CELL_LEVEL to DEFAULT_MAX_CELL_LEVEL)
      yield s2CellId.parent(index).id()
  }
}

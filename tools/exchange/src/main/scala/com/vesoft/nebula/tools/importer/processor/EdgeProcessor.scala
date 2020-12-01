/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.importer.processor

import java.io.{File, IOException}
import java.nio.ByteBuffer
import java.nio.file.{Files, Paths, StandardCopyOption}
import java.util.concurrent.{Executors, TimeUnit}

import com.google.common.geometry.{S2CellId, S2LatLng}
import com.google.common.net.HostAndPort
import com.google.common.util.concurrent.{MoreExecutors, RateLimiter}
import com.vesoft.nebula.NebulaCodec
import com.vesoft.nebula.client.meta.MetaClientImpl
import com.vesoft.nebula.tools.importer.config.{
  Configs,
  EdgeConfigEntry,
  FileBaseSinkConfigEntry,
  SinkCategory,
  StreamingDataSourceConfigEntry
}
import com.vesoft.nebula.tools.importer.utils.{HDFSUtils, NebulaUtils}
import com.vesoft.nebula.tools.importer.{
  CheckPointHandler,
  Edge,
  Edges,
  ErrorHandler,
  ProcessResult,
  TooManyErrorsException
}
import org.apache.log4j.Logger
import com.vesoft.nebula.tools.importer.writer.{NebulaGraphClientWriter, NebulaSSTWriter}
import org.apache.commons.lang.StringEscapeUtils
import org.apache.spark.TaskContext
import org.apache.spark.sql.streaming.Trigger
import org.apache.spark.sql.{DataFrame, Encoders, Row}
import org.apache.spark.util.LongAccumulator

import scala.collection.JavaConverters._
import scala.collection.mutable.ArrayBuffer

class EdgeProcessor(data: DataFrame,
                    edgeConfig: EdgeConfigEntry,
                    fieldKeys: List[String],
                    nebulaKeys: List[String],
                    config: Configs,
                    batchSuccess: LongAccumulator,
                    batchFailure: LongAccumulator)
    extends Processor {

  @transient
  private[this] lazy val LOG = Logger.getLogger(this.getClass)

  private[this] val DEFAULT_MIN_CELL_LEVEL = 10
  private[this] val DEFAULT_MAX_CELL_LEVEL = 18

  private def processEachPartition(iterator: Iterator[Edge]): Unit = {

    // TODO Support Multi Writer
    val writer = new NebulaGraphClientWriter(config.databaseConfig,
                                             config.userConfig,
                                             config.connectionConfig,
                                             config.executionConfig.retry,
                                             edgeConfig)
    writer.prepare()

    val futures              = new ProcessResult()
    val errorBuffer          = ArrayBuffer[String]()
    val rateLimiter          = RateLimiter.create(config.rateConfig.limit)
    val service              = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(1))
    var breakPointEdgesCount = 0L

    iterator.grouped(edgeConfig.batch).foreach { edge =>
      val edges =
        Edges(nebulaKeys, edge.toList, edgeConfig.sourcePolicy, edgeConfig.targetPolicy)

      if (rateLimiter.tryAcquire(config.rateConfig.timeout, TimeUnit.MILLISECONDS)) {
        val future = writer.writeEdges(edges)
        futures += future
        breakPointEdgesCount += edges.values.length

        if (futures.size == 100) { // TODO configurable ?
          collectFutures(futures,
                         service,
                         edgeConfig,
                         breakPointEdgesCount,
                         batchSuccess,
                         batchFailure)
          breakPointEdgesCount = 0
        }
      } else {
        batchFailure.add(1)
        errorBuffer += writer.toExecuteSentence(edgeConfig.name, edges)
        if (errorBuffer.size == config.errorConfig.errorMaxSize) {
          throw TooManyErrorsException(s"Too Many Errors ${config.errorConfig.errorMaxSize}")
        }

        if (CheckPointHandler
              .checkSupportResume(edgeConfig.dataSourceConfigEntry.category) && edgeConfig.checkPointPath.isDefined) {
          throw new RuntimeException(s"Write edge${edgeConfig.name} errors")
        }
      }

      if (errorBuffer.nonEmpty) {
        ErrorHandler.save(errorBuffer, s"${config.errorConfig.errorPath}/${edgeConfig.name}")
        errorBuffer.clear()
      }
    }

    if (futures.nonEmpty) {
      collectFutures(futures, service, edgeConfig, breakPointEdgesCount, batchSuccess, batchFailure)
      breakPointEdgesCount = 0
    }

    service.shutdown()
    while (!service.awaitTermination(100, TimeUnit.MILLISECONDS)) {
      Thread.sleep(10)
    }
    writer.close()
  }

  override def process(): Unit = {

    val address      = config.databaseConfig.metaAddresses.mkString(",")
    val space        = config.databaseConfig.space
    val fieldTypeMap = NebulaUtils.getDataSourceFieldType(edgeConfig, address, space)

    if (edgeConfig.dataSinkConfigEntry.category == SinkCategory.SST) {
      val fileBaseConfig = edgeConfig.dataSinkConfigEntry.asInstanceOf[FileBaseSinkConfigEntry]
      val metaClient = new MetaClientImpl(config.databaseConfig.metaAddresses.map { address =>
        val pair = address.split(":")
        if (pair.length != 2) {
          throw new IllegalArgumentException("address should compose by host and port")
        }
        HostAndPort.fromParts(pair(0), pair(1).toInt)
      }.asJava)
      metaClient.connect()

      val partSize = metaClient.getPartsAlloc(config.databaseConfig.space).size()
      val response = metaClient.getEdges(config.databaseConfig.space).asScala
      val edgeType = response
        .filter(item => item.edge_name == edgeConfig.name)
        .map(_.edge_type)
        .toList
        .head
      metaClient.close() // TODO Try

      data
        .mapPartitions { iter =>
          val stream = classOf[NebulaCodec].getResourceAsStream("/libnebula_codec.so")
          val tmpDir = new File(System.getProperty("java.io.tmpdir"))
          if (!tmpDir.exists && !tmpDir.mkdir) {
            throw new IOException("Failed to create temp directory " + tmpDir)
          }
          val tmp = File.createTempFile("libnebula_codec", ".so")
          tmp.deleteOnExit()
          try {
            Files.copy(stream, tmp.toPath, StandardCopyOption.REPLACE_EXISTING)
          } finally {
            stream.close()
          }

          System.load(tmp.getAbsolutePath)
          LOG.info(s"Loading NebulaCodec successfully.")

          iter.map { row =>
            val sourceID = getLong(row, edgeConfig.sourceField)
            val targetID = getLong(row, edgeConfig.targetField)
            val ranking = if (edgeConfig.rankingField.isDefined) {
              getLong(row, edgeConfig.rankingField.get)
            } else {
              0
            }

            val part = (sourceID % partSize + 1).toInt
            val encodedKey = NebulaCodec.createEdgeKey(part,
                                                       sourceID,
                                                       edgeType,
                                                       ranking,
                                                       targetID,
                                                       0L) // TODO version
            val values = for {
              property <- fieldKeys if property.trim.length != 0
            } yield
              extraValue(row, property, fieldTypeMap, edgeConfig.isImplicit, true)
                .asInstanceOf[AnyRef]
            val encodedValue = NebulaCodec.encode(values.toArray)
            (encodedKey, encodedValue)
          }
        }(Encoders.tuple(Encoders.BINARY, Encoders.BINARY))
        .toDF("key", "value")
        .sortWithinPartitions("key")
        .foreachPartition { iterator: Iterator[Row] =>
          val taskID                  = TaskContext.get().taskAttemptId()
          var writer: NebulaSSTWriter = null
          var currentPart             = -1
          try {
            iterator.foreach { vertex =>
              val key   = vertex.getAs[Array[Byte]](0)
              val value = vertex.getAs[Array[Byte]](1)
              val part  = ByteBuffer.wrap(key, 0, 4).getInt >> 8

              if (part != currentPart) {
                if (writer != null) {
                  writer.close()
                  val localFile = s"${fileBaseConfig.localPath}/${currentPart}-${taskID}.sst"
                  HDFSUtils.upload(localFile, s"${fileBaseConfig.remotePath}/${currentPart}")
                  Files.delete(Paths.get(localFile))
                }
                currentPart = part
                val tmp = s"${fileBaseConfig.localPath}/${currentPart}-${taskID}.sst"
                writer = new NebulaSSTWriter(tmp)
                writer.prepare()
              }
              writer.write(key, value)
            }
          } finally {
            if (writer != null) {
              writer.close()
              val localFile = s"${fileBaseConfig.localPath}/${currentPart}-${taskID}.sst"
              HDFSUtils.upload(localFile, s"${fileBaseConfig.remotePath}/${currentPart}")
              Files.delete(Paths.get(localFile))
            }
          }
        }
    } else {
      val edgeFrame = data
        .map { row =>
          var sourceField = if (!edgeConfig.isGeo) {
            val sourceIndex = row.schema.fieldIndex(edgeConfig.sourceField)
            row.get(sourceIndex).toString
          } else {
            val lat = row.getDouble(row.schema.fieldIndex(edgeConfig.latitude.get))
            val lng = row.getDouble(row.schema.fieldIndex(edgeConfig.longitude.get))
            indexCells(lat, lng).mkString(",")
          }
          if (edgeConfig.sourcePolicy.isEmpty) {
            assert(NebulaUtils.isNumic(sourceField),
                   s"Not support non-Numeric type for source field")
          } else {
            if (StringEscapeUtils.escapeJava(sourceField).contains('\\')) {
              sourceField = StringEscapeUtils.escapeJava(sourceField)
            }
          }

          val targetIndex = row.schema.fieldIndex(edgeConfig.targetField)
          var targetField = row.get(targetIndex).toString
          if (edgeConfig.targetPolicy.isEmpty) {
            assert(NebulaUtils.isNumic(targetField),
                   s"Not support non-Numeric type for target field")
          } else {
            if (StringEscapeUtils.escapeJava(targetField).contains('\\')) {
              targetField = StringEscapeUtils.escapeJava(sourceField)
            }
          }

          val values = for {
            property <- fieldKeys if property.trim.length != 0
          } yield extraValue(row, property, fieldTypeMap, edgeConfig.isImplicit)

          if (edgeConfig.rankingField.isDefined) {
            val index   = row.schema.fieldIndex(edgeConfig.rankingField.get)
            val ranking = row.get(index).toString
            assert(NebulaUtils.isNumic(ranking), s"Not support non-Numeric type for ranking field")

            Edge(sourceField, targetField, Some(ranking.toLong), values)
          } else {
            Edge(sourceField, targetField, None, values)
          }
        }(Encoders.kryo[Edge])

      if (data.isStreaming) {
        val streamingDataSourceConfig =
          edgeConfig.dataSourceConfigEntry.asInstanceOf[StreamingDataSourceConfigEntry]
        edgeFrame.writeStream
          .foreachBatch((edges, batchId) => {
            LOG.info(s"${edgeConfig.name} edge start batch ${batchId}.")
            edges.foreachPartition(processEachPartition _)
          })
          .trigger(Trigger.ProcessingTime(s"${streamingDataSourceConfig.intervalSeconds} seconds"))
          .start()
          .awaitTermination()
      } else
        edgeFrame.foreachPartition(processEachPartition _)
    }
  }

  private[this] def indexCells(lat: Double, lng: Double): IndexedSeq[Long] = {
    val coordinate = S2LatLng.fromDegrees(lat, lng)
    val s2CellId   = S2CellId.fromLatLng(coordinate)
    for (index <- DEFAULT_MIN_CELL_LEVEL to DEFAULT_MAX_CELL_LEVEL)
      yield s2CellId.parent(index).id()
  }
}

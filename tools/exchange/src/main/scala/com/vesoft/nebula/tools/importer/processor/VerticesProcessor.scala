/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.importer.processor

import java.io.{File, IOException}
import java.nio.{ByteBuffer, ByteOrder}
import java.nio.file.{Files, Paths, StandardCopyOption}
import java.util.concurrent.{Executors, TimeUnit}

import com.google.common.net.HostAndPort
import com.google.common.util.concurrent.{MoreExecutors, RateLimiter}
import com.vesoft.nebula.NebulaCodec
import com.vesoft.nebula.client.meta.MetaClientImpl
import com.vesoft.nebula.tools.importer.config.{
  Configs,
  FileBaseSinkConfigEntry,
  SinkCategory,
  StreamingDataSourceConfigEntry,
  TagConfigEntry
}
import com.vesoft.nebula.tools.importer.utils.{HDFSUtils, NebulaUtils}
import com.vesoft.nebula.tools.importer.{
  CheckPointHandler,
  ErrorHandler,
  ProcessResult,
  TooManyErrorsException,
  Vertex,
  Vertices
}
import org.apache.log4j.Logger
import com.vesoft.nebula.tools.importer.writer.{NebulaGraphClientWriter, NebulaSSTWriter}
import org.apache.commons.lang.StringEscapeUtils
import org.apache.spark.TaskContext
import org.apache.spark.sql.streaming.Trigger
import org.apache.spark.sql.types.{IntegerType, LongType, StringType}
import org.apache.spark.sql.{DataFrame, Encoders, Row}
import org.apache.spark.util.LongAccumulator

import scala.collection.JavaConverters._
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
      val vertices = Vertices(nebulaKeys, vertex.toList, tagConfig.vertexPolicy)
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

    val address      = config.databaseConfig.metaAddresses.mkString(",")
    val space        = config.databaseConfig.space
    val fieldTypeMap = NebulaUtils.getDataSourceFieldType(tagConfig, address, space)

    if (tagConfig.dataSinkConfigEntry.category == SinkCategory.SST) {
      val fileBaseConfig = tagConfig.dataSinkConfigEntry.asInstanceOf[FileBaseSinkConfigEntry]
      val metaClient = new MetaClientImpl(config.databaseConfig.metaAddresses.map { address =>
        val pair = address.split(":")
        if (pair.length != 2) {
          throw new IllegalArgumentException("address should compose by host and port")
        }
        HostAndPort.fromParts(pair(0), pair(1).toInt)
      }.asJava)
      metaClient.connect()

      val partSize = metaClient.getPartsAlloc(config.databaseConfig.space).size()
      val response = metaClient.getTags(config.databaseConfig.space).asScala
      val tagID = response
        .filter(item => item.tag_name == tagConfig.name)
        .map(_.tag_id)
        .toList
        .head
      metaClient.close() // TODO Try?

      data
        .mapPartitions { iter =>
          val stream = classOf[NebulaCodec].getResourceAsStream("/libnebula_codec.so")
          val tmpDir = new File(System.getProperty("java.io.tmpdir"))
          if (!tmpDir.exists && !tmpDir.mkdir) {
            throw new IOException("Failed to create temp directory " + tmpDir)
          }
          val tmp = new File(tmpDir, "libnebula_codec.so")
          tmp.deleteOnExit()
          try {
            Files.copy(stream, tmp.toPath, StandardCopyOption.REPLACE_EXISTING)
          } finally {
            stream.close()
          }

          System.load(tmp.getAbsolutePath)
          LOG.info(s"Loading NebulaCodec successfully.")

          iter.map { row =>
            val vertexID = getLong(row, tagConfig.vertexField)
            val part     = (vertexID.toLong % partSize + 1).toInt
            // TODO version
            val encodedKey = NebulaCodec.createVertexKey(part, vertexID.toLong, tagID, 0L)

            val values = for {
              property <- fieldKeys if property.trim.length != 0
            } yield
              extraValue(row, property, fieldTypeMap, tagConfig.isImplicit, true)
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
          val localPath               = fileBaseConfig.localPath
          val remotePath              = fileBaseConfig.remotePath
          try {
            iterator.foreach { vertex =>
              val key   = vertex.getAs[Array[Byte]](0)
              val value = vertex.getAs[Array[Byte]](1)
              val part = ByteBuffer
                .wrap(key, 0, 4)
                .order(ByteOrder.LITTLE_ENDIAN)
                .getInt >> 8

              if (part != currentPart) {
                if (writer != null) {
                  writer.close()
                  val localFile = s"${localPath}/${currentPart}-${taskID}.sst"
                  HDFSUtils.upload(localFile, s"${remotePath}/${currentPart}")
                  Files.delete(Paths.get(localFile))
                }
                currentPart = part
                val tmp = s"${localPath}/${currentPart}-${taskID}.sst"
                writer = new NebulaSSTWriter(tmp)
                writer.prepare()
              }
              writer.write(key, value)
            }
          } finally {
            if (writer != null) {
              writer.close()
              val localFile = s"${localPath}/${currentPart}-${taskID}.sst"
              HDFSUtils.upload(localFile, s"${remotePath}/${currentPart}")
              Files.delete(Paths.get(localFile))
            }
          }
        }
    } else {
      val vertices = data
        .map { row =>
          val vertexID =
            if (tagConfig.vertexPolicy.isEmpty) {
              val index = row.schema.fieldIndex(tagConfig.vertexField)
              val value = row.schema.fields(index).dataType match {
                case LongType    => row.getLong(index)
                case IntegerType => row.getInt(index)
                case StringType  => row.getString(index)
              }
              assert(NebulaUtils.isNumic(value.toString),
                     s"Not support non-Numeric type for vertex id")
              value.toString
            } else {
              val vid = row.getString(row.schema.fieldIndex(tagConfig.vertexField))
              if (StringEscapeUtils.escapeJava(vid).contains('\\')) {
                StringEscapeUtils.escapeJava(vid)
              } else {
                vid
              }
            }

          val values = for {
            property <- fieldKeys if property.trim.length != 0
          } yield extraValue(row, property, fieldTypeMap, tagConfig.isImplicit)
          Vertex(vertexID, values)
        }(Encoders.kryo[Vertex])

      if (data.isStreaming) {
        val streamingDataSourceConfig =
          tagConfig.dataSourceConfigEntry.asInstanceOf[StreamingDataSourceConfigEntry]
        vertices.writeStream
          .foreachBatch((vertexSet, batchId) => {
            LOG.info(s"${tagConfig.name} tag start batch ${batchId}.")
            vertexSet.foreachPartition(processEachPartition _)
          })
          .trigger(Trigger.ProcessingTime(s"${streamingDataSourceConfig.intervalSeconds} seconds"))
          .start()
          .awaitTermination()
      } else
        vertices.foreachPartition(processEachPartition _)
    }
  }
}

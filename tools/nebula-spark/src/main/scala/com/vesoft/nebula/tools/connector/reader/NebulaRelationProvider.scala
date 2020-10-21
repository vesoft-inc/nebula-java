/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.connector.reader

import java.util.Map.Entry
import java.util.Optional
import com.vesoft.nebula.tools.connector.exception.IllegalOptionException
import com.vesoft.nebula.tools.connector.{
  DataTypeEnum,
  NebulaEdgeWriter,
  NebulaVertexWriter,
  OperaType
}
import org.apache.spark.sql.catalyst.util.CaseInsensitiveMap
import org.apache.spark.sql.sources.v2.writer.DataSourceWriter
import org.apache.spark.sql.sources.v2.{DataSourceOptions, WriteSupport}
import com.vesoft.nebula.tools.connector.NebulaOptions
import org.apache.spark.sql.sources.{
  BaseRelation,
  CreatableRelationProvider,
  DataSourceRegister,
  RelationProvider
}
import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{DataFrame, SQLContext, SaveMode}
import org.slf4j.LoggerFactory
import scala.collection.JavaConversions.iterableAsScalaIterable

class NebulaRelationProvider
    extends CreatableRelationProvider
    with RelationProvider
    with WriteSupport
    with DataSourceRegister {
  private val LOG = LoggerFactory.getLogger(this.getClass)

  /**
    * The string that represents the format that nebula edge source provider uses.
    */
  override def shortName(): String = "nebula"

  override def createRelation(sqlContext: SQLContext,
                              parameters: Map[String, String]): BaseRelation = {
    val nebulaOptions = new NebulaOptions(parameters, OperaType.READ)
    NebulaRelation(sqlContext, nebulaOptions)
  }

  override def createRelation(sqlContext: SQLContext,
                              mode: SaveMode,
                              parameters: Map[String, String],
                              data: DataFrame): BaseRelation = ???

  override def createWriter(writeUUID: String,
                            schema: StructType,
                            mode: SaveMode,
                            options: DataSourceOptions): Optional[DataSourceWriter] = {

    var parameters: Map[String, String] = Map()
    for (entry: Entry[String, String] <- options.asMap().entrySet) {
      parameters += (entry.getKey -> entry.getValue)
    }

    val nebulaOptions: NebulaOptions =
      new NebulaOptions(CaseInsensitiveMap(parameters))(OperaType.WRITE)
    val space    = nebulaOptions.spaceName
    val label    = nebulaOptions.label
    val address  = nebulaOptions.getHostAndPorts
    val dataType = nebulaOptions.dataType
    if (mode == SaveMode.Ignore || mode == SaveMode.ErrorIfExists) {
      LOG.warn("Currently do not support")
    }

    LOG.info("create writer")
    LOG.info(s"options ${options.asMap()}")

    if (DataTypeEnum.VERTEX == DataTypeEnum.withName(dataType)) {
      val vertexFiled = nebulaOptions.vertexField
      val vertexIndex: Int = {
        var index: Int = -1
        for (i <- schema.fields.indices) {
          if (schema.fields(i).name.equals(vertexFiled)) {
            index = i
          }
        }
        if (index < 0) {
          throw new IllegalOptionException(
            s" vertex field ${vertexFiled} does not exist in dataframe")
        }
        index
      }
      Optional.of(new NebulaVertexWriter(address, nebulaOptions, space, label, vertexIndex, schema))
    } else {
      val srcVertexFiled = nebulaOptions.srcVertexField
      val dstVertexField = nebulaOptions.dstVertexField
      val vertexFieldsIndex = {
        var srcIndex: Int = -1
        var dstIndex: Int = -1
        for (i <- schema.fields.indices) {
          if (schema.fields(i).name.equals(srcVertexFiled)) {
            srcIndex = i
          }
          if (schema.fields(i).name.equals(dstVertexField)) {
            dstIndex = i
          }
        }
        if (srcIndex < 0 || dstIndex < 0) {
          throw new IllegalOptionException(
            s" srcVertex field ${srcVertexFiled} or dstVertex field ${dstVertexField} do not exist in dataframe")
        }
        (srcIndex, dstIndex)
      }
      Optional.of(
        new NebulaEdgeWriter(address,
                             nebulaOptions,
                             space,
                             label,
                             vertexFieldsIndex._1,
                             vertexFieldsIndex._2,
                             schema))
    }
  }
}

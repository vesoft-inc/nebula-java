/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.importer.utils

import com.google.common.net.HostAndPort
import com.typesafe.config.Config
import com.vesoft.nebula.client.meta.MetaClientImpl
import com.vesoft.nebula.meta.{ErrorCode, TagItem}
import org.apache.log4j.Logger
import org.apache.spark.sql.types.{DataType}
import org.apache.spark.sql.types.DataTypes.{
  BooleanType,
  DoubleType,
  FloatType,
  IntegerType,
  LongType,
  StringType
}
import org.apache.thrift.TException

import scala.collection.JavaConverters._
import scala.collection.mutable

object NebulaUtils {
  private[this] val LOG = Logger.getLogger(this.getClass)

  def getDataSourceFieldType(dataSourceConf: Config,
                             nebulaConfig: Config): Map[String, DataType] = {
    val nebulaFields = dataSourceConf.getStringList("nebula.fields")
    val sourceFields = dataSourceConf.getStringList("fields")
    val address      = nebulaConfig.getStringList("metaAddress")
    val space        = nebulaConfig.getString("space")
    val label        = dataSourceConf.getString("name")

    val hostPorts = address.toArray.map(addr => HostAndPort.fromString(addr.toString)).toList

    var metaClient: MetaClientImpl = null
    try {
      metaClient = new MetaClientImpl(hostPorts.toList.asJava)
    } catch {
      case e: TException => {
        LOG.error("failed to get metaClient")
        throw e
      }
    }
    if (ErrorCode.SUCCEEDED != metaClient.connect()) {
      LOG.error("meta client connect failed.")
      throw new TException("meta client connect failed: connect operation does not return 0.")
    }

    var nebulaSchemaMap: mutable.Map[String, Class[_]] = mutable.Map()
    val isVertex: Boolean                              = isLabelVertex(metaClient, space, label)
    if (isVertex) {
      nebulaSchemaMap = metaClient.getTagSchema(space, label).asScala
    } else {
      nebulaSchemaMap = metaClient.getEdgeSchema(space, label).asScala
    }

    val sourceSchemaMap: mutable.Map[String, DataType] = mutable.HashMap[String, DataType]()
    for (i <- 0 until nebulaFields.size()) {
      sourceSchemaMap.put(sourceFields.get(i), getDataType(nebulaSchemaMap(nebulaFields.get(i))))
    }
    if (isVertex) {
      if (dataSourceConf.hasPath("policy")) {
        sourceSchemaMap.+=(dataSourceConf.getString("vertex") -> StringType)
      } else {
        sourceSchemaMap.+=(dataSourceConf.getString("vertex") -> LongType)
      }
    } else {
      if (dataSourceConf.hasPath("source.field")) {
        sourceSchemaMap.+=(dataSourceConf.getString("source.field") -> StringType)
      } else {
        sourceSchemaMap.+=(dataSourceConf.getString("source.field") -> LongType)
      }
      if (dataSourceConf.hasPath("target.field")) {
        sourceSchemaMap.+=(dataSourceConf.getString("target.field") -> StringType)
      } else {
        sourceSchemaMap.+=(dataSourceConf.getString("target.field") -> LongType)
      }
    }
    sourceSchemaMap.toMap
  }

  def getDataType(clazz: Class[_]): DataType = {
    if (classOf[java.lang.Boolean] == clazz) return BooleanType
    else if (classOf[java.lang.Long] == clazz) return LongType
    else if (classOf[java.lang.Double] == clazz) return DoubleType
    StringType
  }

  def isLabelVertex(metaClient: MetaClientImpl, space: String, label: String): Boolean = {
    val tags: List[TagItem] = metaClient.getTags(space).asScala.toList
    for (tag <- tags) {
      if (label.equals(tag.tag_name)) {
        return true
      }
    }
    false
  }

  def getDataFrameValue(value: String, dataType: DataType): Any = {
    dataType match {
      case LongType    => value.toLong
      case IntegerType => value.toInt
      case BooleanType => value.toBoolean
      case DoubleType  => value.toDouble
      case FloatType   => value.toFloat
      case _           => value
    }
  }

  def isNumic(str: String): Boolean = {
    for (char <- str.toCharArray) {
      if (!Character.isDigit(char)) return false
    }
    true
  }
}

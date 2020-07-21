/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.connector

import com.google.common.net.HostAndPort
import com.vesoft.nebula.SupportedType
import com.vesoft.nebula.client.meta.MetaClientImpl
import org.apache.spark.sql.types.{
  BooleanType,
  DataType,
  DateType,
  DoubleType,
  FloatType,
  IntegerType,
  LongType,
  StringType
}

import scala.collection.JavaConverters._

class MetaProvider(addresses: List[Address]) extends AutoCloseable {

  val metaAddress = addresses.map(address => HostAndPort.fromParts(address._1, address._2)).asJava
  val client      = new MetaClientImpl(metaAddress)

  def getTagSchema(space: String, tag: String): List[(String, DataType)] = {
    client
      .getTag(space, tag)
      .columns
      .asScala
      .map(column => (column.name, toDataType(column.`type`.`type`)))
      .toList
  }

  def getEdgeSchema(space: String, edge: String): List[(String, DataType)] = {
    client
      .getEdge(space, edge)
      .columns
      .asScala
      .map(column => (column.name, toDataType(column.`type`.`type`)))
      .toList
  }

  def getPartition(space: String) = {
    client.getPartsAlloc(space)
  }

  def getPartitionNumber(space: String): Int = {
    client.getPartsAlloc(space).size()
  }

  override def close(): Unit = {
    client.close()
  }

  private def toDataType(typz: NebulaType): DataType = {
    typz match {
      case SupportedType.BOOL                    => BooleanType
      case SupportedType.INT | SupportedType.VID => LongType
      case SupportedType.FLOAT                   => FloatType
      case SupportedType.DOUBLE                  => DoubleType
      case SupportedType.STRING                  => StringType
      case SupportedType.DATE                    => DateType
      case SupportedType.UNKNOWN                 => throw new IllegalArgumentException("Unknown Type")
    }
  }
}

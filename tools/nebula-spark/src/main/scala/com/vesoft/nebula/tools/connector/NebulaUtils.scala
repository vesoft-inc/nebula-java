/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.connector

import com.facebook.thrift.TException
import com.google.common.net.HostAndPort
import com.vesoft.nebula.client.meta.MetaClientImpl
import com.vesoft.nebula.client.storage.StorageClientImpl
import com.vesoft.nebula.data.Property
import com.vesoft.nebula.tools.connector.exception.{GraphConnectException, NebulaRPCException}
import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.types.{DataType, Metadata, StructType}
import org.apache.spark.sql.types.{
  BooleanType,
  DoubleType,
  FloatType,
  IntegerType,
  LongType,
  StringType
}
import org.apache.spark.unsafe.types.UTF8String
import org.slf4j.LoggerFactory

object NebulaUtils {
  private val LOG = LoggerFactory.getLogger(this.getClass)

  /**
    * convert nebula data type to spark sql data type
    */
  def convertDataType(clazz: Class[_]): DataType = {
    if (java.lang.Long.TYPE.getSimpleName
          .equalsIgnoreCase(clazz.getSimpleName) || java.lang.Integer.TYPE.getSimpleName
          .equalsIgnoreCase(clazz.getSimpleName)) {
      return LongType
    }
    if (java.lang.Double.TYPE.getSimpleName
          .equalsIgnoreCase(clazz.getSimpleName) || java.lang.Float.TYPE.getSimpleName
          .equalsIgnoreCase(clazz.getSimpleName)) {
      return DoubleType
    }
    if (java.lang.Boolean.TYPE.getSimpleName.equalsIgnoreCase(clazz.getSimpleName)) {
      return BooleanType
    }
    StringType
  }

  /**
    * get and connect nebula meta client
    */
  import scala.collection.JavaConverters._
  def createMetaClient(hostAndPorts: List[HostAndPort]): MetaClientImpl = {
    val metaClient = new MetaClientImpl(hostAndPorts.asJava)
    try {
      metaClient.connect()
    } catch {
      case te: TException =>
        throw new GraphConnectException(
          s"failed to connect nebula meta client with ${hostAndPorts}",
          te)
      case rpce: NebulaRPCException =>
        throw new NebulaRPCException(s"failed to connect for rpc exception with ${hostAndPorts}",
                                     rpce)
    }
    metaClient
  }

  def createStorageClient(metaClient: MetaClientImpl): StorageClientImpl = {
    new StorageClientImpl(metaClient)
  }

  def closeMetaClient(client: MetaClientImpl): Unit = {
    if (client != null) client.close()
  }

  def closeStorageClient(client: StorageClientImpl): Unit = {
    if (client != null) client.close()
  }

  type NebulaValueGetter = (Property, InternalRow, Int) => Unit

  def makeGetters(schema: StructType): Array[NebulaValueGetter] =
    schema.fields.map(field => makeGetter(field.dataType, field.metadata))

  private def makeGetter(dataType: DataType, metadata: Metadata): NebulaValueGetter = {
    dataType match {
      case BooleanType =>
        (prop: Property, row: InternalRow, pos: Int) =>
          row.setBoolean(pos, prop.getValueAsBool)
      case LongType =>
        (prop: Property, row: InternalRow, pos: Int) =>
          row.setLong(pos, prop.getValueAsLong)
      case DoubleType =>
        (prop: Property, row: InternalRow, pos: Int) =>
          row.setDouble(pos, prop.getValueAsDouble)
      case FloatType =>
        (prop: Property, row: InternalRow, pos: Int) =>
          row.setFloat(pos, prop.getValueAsFloat)
      case IntegerType =>
        (prop: Property, row: InternalRow, pos: Int) =>
          row.setInt(pos, prop.getValueAsInt)
      case _ =>
        (prop: Property, row: InternalRow, pos: Int) =>
          row.update(pos, UTF8String.fromString(prop.getValue.toString))
    }
  }
}

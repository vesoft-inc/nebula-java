package com.vesoft.nebula.tools.connector

import org.apache.spark.sql.catalyst.InternalRow
import org.apache.spark.sql.sources.v2.writer.{
  DataSourceWriter,
  DataWriter,
  DataWriterFactory,
  WriterCommitMessage
}
import org.apache.spark.sql.types.{
  BooleanType,
  DataType,
  DoubleType,
  FloatType,
  IntegerType,
  LongType,
  ShortType,
  StringType,
  StructType
}
import org.slf4j.LoggerFactory

case class NebulaCommitMessage() extends WriterCommitMessage

class NebulaWriter(address: (String, Int),
                   space: String,
                   tag: String,
                   vertexIndex: Int,
                   schema: StructType)
    extends DataWriter[InternalRow] {

  val types = schema.fields.map(field => field.dataType)

  override def write(record: InternalRow): Unit = {
    val vertex = extraValue(types(vertexIndex), record, vertexIndex)
    val values = assignValues(types, record)
    println(s"INSERT INTO ${tag} ${vertex} VALUES(${values})")
  }

  def assignValues(types: Array[DataType], record: InternalRow): String = {
    val values = for {
      index <- 0 until types.length
      if index != vertexIndex
    } yield {
      val value = types(index) match {
        case BooleanType => record.getBoolean(index)
        case ShortType   => record.getShort(index)
        case IntegerType => record.getInt(index)
        case LongType    => record.getLong(index)
        case DoubleType  => record.getDouble(index)
        case FloatType   => record.getFloat(index)
        case StringType  => record.getString(index)
      }
      value.toString
    }
    values.mkString(", ")
  }

  def extraValue(dataType: DataType, record: InternalRow, index: Int): String = {
    val value = dataType match {
      case BooleanType => record.getBoolean(index)
      case ShortType   => record.getShort(index)
      case IntegerType => record.getInt(index)
      case LongType    => record.getLong(index)
      case DoubleType  => record.getDouble(index)
      case FloatType   => record.getFloat(index)
      case StringType  => record.getString(index)
    }
    value.toString
  }

  override def commit(): WriterCommitMessage = {
    NebulaCommitMessage()
  }

  override def abort(): Unit = {
//    client.close()
  }
}

class NebulaWriterFactory(space: String, tag: String, schema: StructType)
    extends DataWriterFactory[InternalRow] {
  override def createDataWriter(partitionId: EdgeRank,
                                taskId: Long,
                                epochId: Long): DataWriter[InternalRow] = {
    schema.fields.indexOf("")
    new NebulaWriter(("127.0.0.1", 8989), space, tag,0, schema)
  }
}

class NebulaVertexWriter(addresses: List[Address], space: String, tag: String, schema: StructType)
    extends DataSourceWriter {
  private val LOG = LoggerFactory.getLogger(this.getClass)

  override def createWriterFactory(): DataWriterFactory[InternalRow] = {
    new NebulaWriterFactory(space, tag, schema)
  }

  override def commit(messages: Array[WriterCommitMessage]): Unit = {
    LOG.debug(s"${messages.length}")
  }

  override def abort(messages: Array[WriterCommitMessage]): Unit = {
    LOG.error("")
  }
}

/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools

import com.vesoft.nebula.bean.Parameters
import com.vesoft.nebula.tools.connector.reader.NebulaRelationProvider
import org.apache.spark.graphx.{Edge, Graph, VertexId}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, DataFrameReader, DataFrameWriter, Row, SparkSession}

package object connector {

  type Address    = (String, Int)
  type EdgeRank   = Int
  type Propertyy  = (String, Any)
  type VertexID   = Long
  type Vertex     = (VertexId, List[Propertyy])
  type NebulaEdge = Edge[(EdgeRank, List[Propertyy])]
  type NebulaType = Int

  def loadGraph(space: String)(implicit session: SparkSession): Graph[Vertex, NebulaEdge] = ???
//  {
//    case class Values(value: Any*)
//    val values = Seq(Values(1, 3.14, "Hello"))
//
//    val fields = List(
//      StructField("col_int", IntegerType),
//      StructField("col_double", DoubleType),
//      StructField("col_string", StringType)
//    )
//
//    val `type` = StructType(fields)
//
//    val data = session.sparkContext.parallelize(values)
//    session.createDataFrame(data)(Encoders.bean[Values])
//
//    Row.fromSeq(values)
//  }

  def loadVertices(space: String, tagName: String)(implicit session: SparkSession): RDD[Vertex] =
    ???

  def loadEdges(space: String, edgeName: String)(implicit session: SparkSession): RDD[NebulaEdge] =
    ???

  implicit class NebulaDataFrameReader(reader: DataFrameReader) {
    var address: String      = _
    var space: String        = _
    var partitionNum: String = _

    /**
      * @param address
      * @param partitionNum
      * @param space
      * */
    def nebula(address: String, space: String, partitionNum: String): NebulaDataFrameReader = {
      this.address = address
      this.space = space
      this.partitionNum = partitionNum
      this
    }

    def nebula(space: String, host: String, port: Int): Unit = {
      nebula(space, List(host -> port))
    }

    def nebula(space: String, address: List[(String, Int)]): Unit = ???

    /**
      * Reading com.vesoft.nebula.tools.connector.vertices from Nebula Graph
      *
      * @param tag
      * @param fields
      * @return DataFrame
      */
    def loadVertices(tag: String, fields: String): DataFrame = {
      assert(address != null && space != null && partitionNum != null,
             "call nebula first before call loadVertices. ")
      reader
        .format(classOf[NebulaRelationProvider].getName)
        .option(Parameters.HOST_AND_PORTS, address)
        .option(Parameters.PARTITION_NUMBER, partitionNum)
        .option(Parameters.SPACE_NAME, space)
        .option(Parameters.TYPE, DataTypeEnum.VERTEX.toString)
        .option(Parameters.LABEL, tag)
        .option(Parameters.RETURN_COLS, fields)
        .load()
    }

    /**
      * Reading edges from Nebula Graph
      *
      * @param edge
      * @param fields
      * @return DataFrame
      */
    def loadEdges(edge: String, fields: String): DataFrame = {
      assert(address != null && space != null && partitionNum != null,
             "call nebula first before call loadEdges. ")
      reader
        .format(classOf[NebulaRelationProvider].getName)
        .option(Parameters.HOST_AND_PORTS, address)
        .option(Parameters.PARTITION_NUMBER, partitionNum)
        .option(Parameters.SPACE_NAME, space)
        .option(Parameters.TYPE, DataTypeEnum.EDGE.toString)
        .option(Parameters.LABEL, edge)
        .option(Parameters.RETURN_COLS, fields)
        .load()
    }
  }

  implicit class NebulaDataFrameWriter(writer: DataFrameWriter[Row]) {

    def writeVertices(space: String, tag: String): Unit = {
      writer
        .format(classOf[vertices.DefaultSource].getName)
        .option("nebula.space", space)
        .option("nebula.tag", tag)
        .save()
    }

    def writeEdges(space: String, edge: String): Unit = {
      writer
        .format(classOf[edges.DefaultSource].getName)
        .option("nebula.space", space)
        .option("nebula.edge", edge)
        .save()
    }
  }
}

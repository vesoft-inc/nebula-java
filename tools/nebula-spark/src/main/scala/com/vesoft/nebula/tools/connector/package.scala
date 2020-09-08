/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools

import org.apache.spark.graphx
import org.apache.spark.graphx.{Edge, Graph, VertexId}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types.{DoubleType, IntegerType, StringType, StructField, StructType}
import org.apache.spark.sql.{
  DataFrame,
  DataFrameReader,
  DataFrameWriter,
  Encoders,
  Row,
  SparkSession
}

package object connector {

  type Address    = (String, Int)
  type EdgeRank   = Int
  type Property   = (String, Any)
  type VertexID   = Long
  type Vertex     = (VertexId, List[Property])
  type NebulaEdge = Edge[(EdgeRank, List[Property])]
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

    def nebula(space: String, host: String, port: Int): Unit = {
      nebula(space, List(host -> port))
    }

    def nebula(space: String, address: List[(String, Int)]): Unit = ???

    /**
      * Reading com.vesoft.nebula.tools.connector.vertices from Nebula Graph
      *
      * @param addresses
      * @return
      */
    def nebulaVertices(space: String,
                       tag: String,
                       fields: List[String],
                       address: String,
                       addresses: String*): DataFrame = {
      reader
        .format(classOf[vertices.DefaultSource].getName)
        .option("nebula.space", space)
        .option("nebula.tag", tag)
        .option("nebula.fields", fields.mkString(","))
        .load(Seq(address) ++ addresses: _*)
    }

    /**
      * Reading edges from Nebula Graph
      *
      * @param addresses
      * @return
      */
    def nebulaEdges(space: String,
                    edge: String,
                    fields: List[String],
                    address: String,
                    addresses: String*): DataFrame = {
      reader
        .format(classOf[edges.DefaultSource].getName)
        .option("nebula.space", space)
        .option("nebula.edge", edge)
        .option("nebula.fields", fields.mkString(","))
        .load(Seq(address) ++ addresses: _*)
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

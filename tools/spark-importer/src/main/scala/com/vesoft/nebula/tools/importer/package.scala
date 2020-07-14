/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools

import com.google.common.base.Optional
import com.google.common.util.concurrent.ListenableFuture

import scala.collection.mutable.ListBuffer

package object importer {

  type GraphSpaceID = Int
  type PartitionID = Int
  type TagID = Int
  type EdgeType = Int
  type SchemaID = Either[TagID, EdgeType]
  type TagVersion = Long
  type EdgeVersion = Long
  type SchemaVersion = Either[TagVersion, EdgeVersion]
  type VertexID = Long
  type VertexIDSlice = String
  type EdgeRank = Long
  type PropertyNames = List[String]
  type PropertyValues = List[Any]
  type ProcessResult = ListBuffer[ListenableFuture[Optional[Integer]]]

  case class Vertex(vertexID: VertexIDSlice, values: PropertyValues) {

    def propertyValues = values.mkString(", ")

    override def toString: String = {
      s"Vertex ID: ${vertexID}, " +
        s"Values: ${values.mkString(", ")}"
    }
  }

  case class Vertices(names: PropertyNames,
                      values: List[Vertex],
                      tagID: Option[TagID] = None,
                      policy: Option[KeyPolicy.Value] = None) {

    def propertyNames: String = names.mkString(",")

    override def toString: String = {
      if (tagID.isDefined) {
        s"Tag ID: ${tagID}" +
          s"Property Names: ${names.mkString(", ")}" +
          s"Vertex: ${values.mkString(", ")} " +
          s"with policy ${policy}"
      } else {
        s"Property Names: ${names.mkString(", ")}" +
          s"Vertex: ${values.mkString(", ")} " +
          s"with policy ${policy}"
      }
    }
  }

  case class Edge(source: VertexIDSlice,
                  destination: VertexIDSlice,
                  ranking: Option[EdgeRank],
                  values: PropertyValues) {

    def this(source: VertexIDSlice, destination: VertexIDSlice, values: PropertyValues) = {
      this(source, destination, None, values)
    }

    def propertyValues: String = values.mkString(", ")

    override def toString: String = {
      s"Edge: ${source}->${destination}@${ranking} values: ${propertyValues}"
    }
  }

  case class Edges(names: PropertyNames,
                   values: List[Edge],
                   edgeType: Option[EdgeType] = None,
                   sourcePolicy: Option[KeyPolicy.Value] = None,
                   targetPolicy: Option[KeyPolicy.Value] = None) {
    def propertyNames: String = names.mkString(",")
  }

  object KeyPolicy extends Enumeration {
    type POLICY = Value
    val HASH = Value("hash")
    val UUID = Value("uuid")
  }

}

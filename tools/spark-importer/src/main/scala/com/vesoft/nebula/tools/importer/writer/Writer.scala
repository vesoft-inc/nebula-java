/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.importer.writer

import com.google.common.base.Optional
import com.google.common.util.concurrent.ListenableFuture
import com.vesoft.nebula.tools.importer.{Edges, KeyPolicy, Type, Vertices}

/**
 *
 */
trait Writer extends Serializable {

  private[this] val BATCH_INSERT_TEMPLATE = "INSERT %s %s(%s) VALUES %s"
  private[this] val INSERT_VALUE_TEMPLATE = "%s: (%s)"
  private[this] val INSERT_VALUE_TEMPLATE_WITH_POLICY = "%s(\"%s\"): (%s)"
  private[this] val ENDPOINT_TEMPLATE = "%s(\"%s\")"
  private[this] val EDGE_VALUE_WITHOUT_RANKING_TEMPLATE = "%s->%s: (%s)"
  private[this] val EDGE_VALUE_TEMPLATE = "%s->%s@%d: (%s)"

  def prepare(): Unit

  def writeVertices(vertices: Vertices): ListenableFuture[Optional[Integer]]

  def writeEdges(edges: Edges): ListenableFuture[Optional[Integer]]

  def toExecuteSentence(name: String, vertices: Vertices): String = {
    BATCH_INSERT_TEMPLATE.format(
      Type.VERTEX.toString,
      name,
      vertices.propertyNames,
      vertices.values
        .map { vertex =>
          // TODO Check
          if (vertices.policy.isEmpty) {
            INSERT_VALUE_TEMPLATE.format(vertex.vertexID, vertex.propertyValues)
          } else {
            vertices.policy.get match {
              case KeyPolicy.HASH =>
                INSERT_VALUE_TEMPLATE_WITH_POLICY
                  .format(KeyPolicy.HASH.toString, vertex.vertexID, vertex.propertyValues)
              case KeyPolicy.UUID =>
                INSERT_VALUE_TEMPLATE_WITH_POLICY
                  .format(KeyPolicy.UUID.toString, vertex.vertexID, vertex.propertyValues)
              case _ => throw new IllegalArgumentException
            }
          }
        }
        .mkString(", ")
    )
  }

  def toExecuteSentence(name: String, edges: Edges): String = {
    val values = edges.values
      .map { edge =>
        for (source <- edge.source.split(","))
          yield
            // TODO Check
            if (edges.sourcePolicy == None) {
              EDGE_VALUE_WITHOUT_RANKING_TEMPLATE
                .format(source, edge.destination, edge.values.map(_.toString).mkString(", "))
            } else {
              val source = edges.sourcePolicy match {
                case KeyPolicy.HASH =>
                  ENDPOINT_TEMPLATE.format(KeyPolicy.HASH.toString, edge.source)
                case KeyPolicy.UUID =>
                  ENDPOINT_TEMPLATE.format(KeyPolicy.UUID.toString, edge.source)
                case _ =>
                  edge.source
              }

              val target = edges.targetPolicy match {
                case KeyPolicy.HASH =>
                  ENDPOINT_TEMPLATE.format(KeyPolicy.HASH.toString, edge.destination)
                case KeyPolicy.UUID =>
                  ENDPOINT_TEMPLATE.format(KeyPolicy.UUID.toString, edge.destination)
                case _ =>
                  edge.destination
              }

              if (edge.ranking.isEmpty)
                EDGE_VALUE_WITHOUT_RANKING_TEMPLATE
                  .format(source, target, edge.values.mkString(", "))
              else
                EDGE_VALUE_TEMPLATE.format(source,
                  target,
                  edge.ranking.get,
                  edge.values.mkString(", "))
            }
      }
      .flatMap(_.toList).mkString(", ")

    BATCH_INSERT_TEMPLATE.format(Type.EDGE.toString, name, edges.propertyNames, values)
  }

  def close()
}

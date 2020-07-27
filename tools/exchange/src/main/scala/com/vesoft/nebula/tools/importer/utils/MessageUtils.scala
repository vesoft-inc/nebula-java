/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.importer.utils

import java.util.Properties

import com.google.gson.Gson
import com.vesoft.nebula.tools.importer.{
  Edge,
  EdgeRank,
  PropertyValues,
  Vertex,
  VertexID,
  VertexIDSlice
}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig}
import org.apache.kafka.common.serialization.StringSerializer

object MessageUtils {
  private lazy val gson = new Gson()

  def producer(addresses: String): Unit = {
    val properties = new Properties()
    properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, addresses)
    properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer])
    properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, classOf[StringSerializer])
    new KafkaProducer(properties)
  }

  def writeVertex(vertexID: VertexIDSlice, values: PropertyValues): String = {
    require(!vertexID.trim.isEmpty && !values.isEmpty)
    writeVertex(Vertex(vertexID, values))
  }

  def writeVertex(vertexID: VertexID, values: PropertyValues): String = {
    require(!values.isEmpty)
    writeVertex(vertexID.toString, values)
  }

  def writeVertex(vertex: Vertex): String = {
    gson.toJson(vertex)
  }

  def writeEdge(source: VertexIDSlice,
                destination: VertexIDSlice,
                values: PropertyValues,
                ranking: Option[EdgeRank]): String = {
    require(!source.trim.isEmpty && !destination.trim.isEmpty && !values.isEmpty)
    writeEdge(Edge(source, destination, ranking, values))
  }

  def writeEdge(source: VertexID,
                destination: VertexID,
                values: PropertyValues,
                ranking: Option[EdgeRank]): String = {
    require(!values.isEmpty)
    writeEdge(source.toString, destination.toString, values, ranking)
  }

  def writeEdge(edge: Edge): String = {
    gson.toJson(edge)
  }
}

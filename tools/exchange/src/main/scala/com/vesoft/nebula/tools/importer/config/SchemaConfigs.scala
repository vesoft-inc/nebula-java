/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.importer.config

import com.typesafe.config.ConfigValue
import com.vesoft.nebula.tools.importer.KeyPolicy

/**
  * SchemaConfigEntry is tag/edge super class use to save some basic parameter for importer.
  */
sealed trait SchemaConfigEntry {
  def name: String

  def dataSourceConfigEntry: DataSourceConfigEntry

  def dataSinkConfigEntry: DataSinkConfigEntry

  def fields: List[String]

  def nebulaFields: List[String]

  def batch: Int

  def partition: Int

  def checkPointPath: Option[String]

  def isImplicit: Boolean
}

/**
  *
  * @param name
  * @param dataSourceConfigEntry
  * @param dataSinkConfigEntry
  * @param fields
  *  @param nebulaFields
  * @param vertexField
  * @param vertexPolicy
  * @param batch
  * @param partition
  * @param checkPointPath
  */
case class TagConfigEntry(override val name: String,
                          override val dataSourceConfigEntry: DataSourceConfigEntry,
                          override val dataSinkConfigEntry: DataSinkConfigEntry,
                          override val fields: List[String],
                          override val nebulaFields: List[String],
                          vertexField: String,
                          vertexPolicy: Option[KeyPolicy.Value],
                          override val batch: Int,
                          override val partition: Int,
                          override val checkPointPath: Option[String],
                          override val isImplicit: Boolean)
    extends SchemaConfigEntry {
  require(name.trim.nonEmpty && vertexField.trim.nonEmpty && batch > 0)

  override def toString: String = {
    s"Tag name ${name} " +
      s"source ${dataSourceConfigEntry} " +
      s"sink ${dataSinkConfigEntry} " +
      s"vertex field ${vertexField} " +
      s"vertex policy ${vertexPolicy} " +
      s"batch ${batch} " +
      s"partition ${partition} " +
      s"isImplicit ${isImplicit}"
  }
}

/**
  *
  * @param name
  * @param dataSourceConfigEntry
  * @param dataSinkConfigEntry
  * @param fields
  *  @param nebulaFields
  * @param sourceField
  * @param sourcePolicy
  * @param rankingField
  * @param targetField
  * @param targetPolicy
  * @param isGeo
  * @param latitude
  * @param longitude
  * @param batch
  * @param partition
  * @param checkPointPath
  */
case class EdgeConfigEntry(override val name: String,
                           override val dataSourceConfigEntry: DataSourceConfigEntry,
                           override val dataSinkConfigEntry: DataSinkConfigEntry,
                           override val fields: List[String],
                           override val nebulaFields: List[String],
                           sourceField: String,
                           sourcePolicy: Option[KeyPolicy.Value],
                           rankingField: Option[String],
                           targetField: String,
                           targetPolicy: Option[KeyPolicy.Value],
                           isGeo: Boolean,
                           latitude: Option[String],
                           longitude: Option[String],
                           override val batch: Int,
                           override val partition: Int,
                           override val checkPointPath: Option[String],
                           override val isImplicit: Boolean)
    extends SchemaConfigEntry {
  require(
    name.trim.nonEmpty && sourceField.trim.nonEmpty &&
      targetField.trim.nonEmpty && batch > 0)

  override def toString: String = {
    if (isGeo) {
      s"Edge name ${name} " +
        s"source ${dataSourceConfigEntry} " +
        s"sink ${dataSinkConfigEntry} " +
        s"latitude ${latitude} " +
        s"longitude ${longitude} " +
        s"source field ${sourceField} " +
        s"source policy ${sourcePolicy} " +
        s"ranking ${rankingField} " +
        s"target field ${targetField} " +
        s"target policy ${targetPolicy} " +
        s"batch ${batch} " +
        s"partition ${partition} " +
        s"isImplicit ${isImplicit}"
    } else {
      s"Edge name ${name} " +
        s"source ${dataSourceConfigEntry} " +
        s"sink ${dataSinkConfigEntry} " +
        s"source field ${sourceField} " +
        s"source policy ${sourcePolicy} " +
        s"ranking ${rankingField} " +
        s"target field ${targetField} " +
        s"target policy ${targetPolicy} " +
        s"batch ${batch} " +
        s"partition ${partition} " +
        s"isImplicit ${isImplicit}"
    }
  }
}

/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.connector.reader

import com.vesoft.nebula.tools.connector.NebulaOptions
import org.apache.spark.sql.sources.{
  BaseRelation,
  CreatableRelationProvider,
  DataSourceRegister,
  RelationProvider
}
import org.apache.spark.sql.{DataFrame, SQLContext, SaveMode}

class NebulaRelationProvider
    extends CreatableRelationProvider
    with RelationProvider
    with DataSourceRegister {

  /**
    * The string that represents the format that nebula data source provider uses.
    */
  override def shortName(): String = "nebula"

  override def createRelation(sqlContext: SQLContext,
                              parameters: Map[String, String]): BaseRelation = {
    val nebulaOptions = new NebulaOptions(parameters)
    NebulaRelation(sqlContext, nebulaOptions)
  }

  override def createRelation(sqlContext: SQLContext,
                              mode: SaveMode,
                              parameters: Map[String, String],
                              data: DataFrame): BaseRelation = ???
}

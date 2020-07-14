/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.importer.reader

import org.apache.spark.sql.{DataFrame, SparkSession}

/**
  * The Reader is used for create a DataFrame from the source. Such as Hive or HDFS.
  */
trait Reader extends Serializable {
  def session: SparkSession

  def read(): DataFrame

  def close(): Unit
}

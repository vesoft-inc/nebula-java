/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.connector

import org.apache.spark.sql.sources.DataSourceRegister
import org.apache.spark.sql.sources.v2.reader.DataSourceReader
import org.apache.spark.sql.sources.v2.{DataSourceOptions, ReadSupport}

class NebulaSource extends ReadSupport with DataSourceRegister {

  override def createReader(options: DataSourceOptions): DataSourceReader = ???

  override def shortName(): String = "NebulaSource"
}

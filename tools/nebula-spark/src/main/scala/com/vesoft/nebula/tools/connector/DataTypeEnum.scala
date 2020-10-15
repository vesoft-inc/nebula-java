/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.connector

object DataTypeEnum extends Enumeration {

  type DataType = Value
  val VERTEX = Value("vertex")
  val EDGE   = Value("edge")

  def validDataType(dataType: String): Boolean = {
    dataType.equalsIgnoreCase(VERTEX.toString) || dataType.equalsIgnoreCase(EDGE.toString)
  }
}

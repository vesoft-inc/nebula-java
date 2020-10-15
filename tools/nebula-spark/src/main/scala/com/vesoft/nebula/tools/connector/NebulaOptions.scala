/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.connector

import java.util.{Locale, Properties}
import java.util.concurrent.atomic.AtomicLong
import java.util.regex.Pattern

import com.google.common.net.HostAndPort
import org.apache.commons.lang.StringUtils
import org.apache.spark.internal.Logging
import org.apache.spark.sql.catalyst.util.CaseInsensitiveMap

import scala.collection.mutable.ListBuffer

class NebulaOptions(@transient val parameters: CaseInsensitiveMap[String])
    extends Serializable
    with Logging {

  import NebulaOptions._

  def this(parameters: Map[String, String]) = this(CaseInsensitiveMap(parameters))

  def this(hostAndPorts: String,
           spaceName: String,
           dataType: String,
           label: String,
           parameters: Map[String, String]) = {
    this(
      CaseInsensitiveMap(
        parameters ++ Map(
          NebulaOptions.HOST_AND_PORTS -> hostAndPorts,
          NebulaOptions.SPACE_NAME     -> spaceName,
          NebulaOptions.TYPE           -> dataType,
          NebulaOptions.LABEL          -> label
        )))
  }

  /**
    * Return property with all options
    * */
  val asProperties: Properties = {
    val properties = new Properties()
    parameters.originalMap.foreach { case (k, v) => properties.setProperty(k, v) }
    properties
  }

  /**
    * parameters invalidate
    * */
  // nebula connection info
  require(parameters.isDefinedAt(HOST_AND_PORTS), s"Option '$HOST_AND_PORTS' is required")
  val hostAndPorts: String = parameters(HOST_AND_PORTS)

  // nebula space
  require(parameters.isDefinedAt(SPACE_NAME) && StringUtils.isNotBlank(parameters(SPACE_NAME)),
          s"Option '$SPACE_NAME' is required and can not be blank")
  val spaceName: String = parameters(SPACE_NAME)

  // nebula data type: Type.vertex or Type.edge
  require(parameters.isDefinedAt(TYPE), s"Option '$TYPE' is required")
  val dataType: String = parameters(TYPE)
  require(
    DataTypeEnum.validDataType(dataType),
    s"Option '$TYPE' is illegal, it should be '${DataTypeEnum.VERTEX}' or '${DataTypeEnum.EDGE}'")

  // nebula label
  require(parameters.isDefinedAt(LABEL) && StringUtils.isNotBlank(parameters(LABEL)),
          s"Option '$LABEL' is required and can not be blank")
  val label: String = parameters(LABEL)

  // nebula return cols
  private val RETURN_COL_REGEX: String = "(\\w+)(,\\w+)*"
  require(parameters.isDefinedAt(RETURN_COLS), s"Option '$RETURN_COLS' is required")
  val returnCols: String = parameters(RETURN_COLS)
  require(StringUtils.isBlank(returnCols) || Pattern.matches(RETURN_COL_REGEX, returnCols),
          s"Option '$RETURN_COLS' should be blank or be string like a,b")
  var allCols: Boolean = false

  // spark partition numbers
  require(parameters.isDefinedAt(PARTITION_NUMBER), s"Option '$PARTITION_NUMBER' is requied")
  val partitionNums: String = parameters(PARTITION_NUMBER)

  def getReturnColMap: Map[String, List[String]] = {
    val result: Map[String, List[String]] = Map()
    if (StringUtils.isBlank(returnCols)) {
      allCols = true
      result ++ Map(label -> List())
    } else {
      val properties = returnCols.split(",").toList
      result ++ Map(label -> properties)
    }
  }

  def getHostAndPorts: List[HostAndPort] = {
    val hostPorts: ListBuffer[HostAndPort] = new ListBuffer[HostAndPort]
    hostAndPorts
      .split(",")
      .foreach(hostPort => { hostPorts.append(HostAndPort.fromString(hostPort)) })
    hostPorts.toList
  }

}

class NebulaOptionsInWrite(@transient override val parameters: CaseInsensitiveMap[String])
    extends NebulaOptions(parameters) {}

object NebulaOptions {
  private val curId             = new AtomicLong(0L)
  private val nebulaOptionNames = collection.mutable.Set[String]()

  private def newOption(name: String): String = {
    nebulaOptionNames += name.toLowerCase(Locale.ROOT)
    name
  }

  val SPACE_NAME: String       = newOption("spaceName")
  val HOST_AND_PORTS: String   = newOption("hostAndPorts")
  val TYPE: String             = newOption("type")
  val LABEL: String            = newOption("label")
  val RETURN_COLS: String      = newOption("returnCols")
  val PARTITION_NUMBER: String = newOption("partitionNumber")
}

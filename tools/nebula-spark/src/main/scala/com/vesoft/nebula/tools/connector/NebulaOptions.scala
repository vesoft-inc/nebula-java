/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.connector

import java.util.Properties
import java.util.regex.Pattern
import com.google.common.net.HostAndPort
import org.apache.commons.lang.StringUtils
import org.apache.spark.internal.Logging
import org.apache.spark.sql.catalyst.util.CaseInsensitiveMap
import scala.collection.mutable.ListBuffer

class NebulaOptions(@transient val parameters: CaseInsensitiveMap[String])(
    operaType: OperaType.Value)
    extends Serializable
    with Logging {

  import NebulaOptions._

  def this(parameters: Map[String, String], operaType: OperaType.Value) =
    this(CaseInsensitiveMap(parameters))(operaType)

  def this(hostAndPorts: String,
           spaceName: String,
           dataType: String,
           label: String,
           parameters: Map[String, String],
           operaType: OperaType.Value) = {
    this(
      CaseInsensitiveMap(
        parameters ++ Map(
          NebulaOptions.HOST_AND_PORTS -> hostAndPorts,
          NebulaOptions.SPACE_NAME     -> spaceName,
          NebulaOptions.TYPE           -> dataType,
          NebulaOptions.LABEL          -> label
        ))
    )(operaType)
  }

  /**
    * Return property with all options
    */
  val asProperties: Properties = {
    val properties = new Properties()
    parameters.originalMap.foreach { case (k, v) => properties.setProperty(k, v) }
    properties
  }

  /**
    * parameters invalidate
    */
  // nebula connection info
  require(parameters.isDefinedAt(HOST_AND_PORTS), s"Option '$HOST_AND_PORTS' is required")
  val hostAndPorts: String = parameters(HOST_AND_PORTS)

  // nebula space
  require(parameters.isDefinedAt(SPACE_NAME) && StringUtils.isNotBlank(parameters(SPACE_NAME)),
          s"Option '$SPACE_NAME' is required and can not be blank")
  val spaceName: String = parameters(SPACE_NAME)

  // nebula data type: DataTypeEnum.VERTEX or DataTypeEnum.EDGE
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
  var returnCols: String = _
  // spark partition numbers
  var partitionNums: String = _
  if (operaType == OperaType.READ) {
    val RETURN_COL_REGEX: String = "(\\w+)(,\\w+)*"
    require(parameters.isDefinedAt(RETURN_COLS), s"Option '$RETURN_COLS' is required")
    returnCols = parameters(RETURN_COLS)
    require(
      StringUtils.isNotBlank(returnCols) || Pattern.matches(RETURN_COL_REGEX, returnCols) || "*"
        .equals(returnCols.trim),
      s"Option '$RETURN_COLS' should be * or be string like a,b"
    )

    require(parameters.isDefinedAt(PARTITION_NUMBER), s"Option '$PARTITION_NUMBER' is required")
    partitionNums = parameters(PARTITION_NUMBER)
  }

  var allCols: Boolean = false

  val timeout: Int =
    parameters.getOrElse(CONNECTION_TIMEOUT, DEFAULT_CONNECTION_TIMEOUT).toString.toInt
  val connectionTimeout: Int =
    parameters.getOrElse(CONNECTION_TIMEOUT, DEFAULT_CONNECTION_TIMEOUT).toString.toInt
  val connectionRetry: Int =
    parameters.getOrElse(CONNECTION_RETRY, DEFAULT_CONNECTION_RETRY).toString.toInt
  val executionRetry: Int =
    parameters.getOrElse(EXECUTION_RETRY, DEFAULT_EXECUTION_RETRY).toString.toInt
  val user: String           = parameters.getOrElse[String](USER_NAME, DEFAULT_USER_NAME)
  val passwd: String         = parameters.getOrElse[String](PASSWD, DEFAULT_PASSWD)
  val rateLimit: Long        = parameters.getOrElse(RATE_LIMIT, DEFAULT_RATE_LIMIT).toString.toLong
  val rateTimeOut: Long      = parameters.getOrElse(RATE_TIME_OUT, DEFAULT_RATE_TIME_OUT).toString.toLong
  var policy: String         = _
  var batch: Int             = _
  var vertexField: String    = _
  var srcVertexField: String = _
  var dstVertexField: String = _
  if (operaType == OperaType.WRITE) {
    policy = parameters(POLICY)
    if (policy.trim.equals("")) {
      policy = null
    }
    batch = parameters.getOrElse(BATCH, DEFAULT_BATCH).toString.toInt

    vertexField = parameters.getOrElse(VERTEX_FIELD, EMPTY_STRING)
    srcVertexField = parameters.getOrElse(SRC_VERTEX_FIELD, EMPTY_STRING)
    dstVertexField = parameters.getOrElse(DST_VERTEX_FIELD, EMPTY_STRING)
  }

  def getReturnColMap: Map[String, List[String]] = {
    val result: Map[String, List[String]] = Map()
    if ("*".equals(returnCols)) {
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
    extends NebulaOptions(parameters)(OperaType.WRITE) {}

object NebulaOptions {

  val SPACE_NAME: String         = "spaceName"
  val HOST_AND_PORTS: String     = "hostAndPorts"
  val TYPE: String               = "type"
  val LABEL: String              = "label"
  val RETURN_COLS: String        = "returnCols"
  val PARTITION_NUMBER: String   = "partitionNumber"
  val TIMEOUT: String            = "timeout"
  val CONNECTION_TIMEOUT: String = "connectionTimeout"
  val CONNECTION_RETRY: String   = "connectionRetry"
  val EXECUTION_RETRY: String    = "executionRetry"
  val RATE_TIME_OUT: String      = "reteTimeOut"
  val USER_NAME: String          = "user"
  val PASSWD: String             = "passwd"
  val RATE_LIMIT: String         = "rate_limit"
  val POLICY: String             = "policy"
  val BATCH: String              = "batch"
  val VERTEX_FIELD               = "vertexField"
  val SRC_VERTEX_FIELD           = "srcVertexField"
  val DST_VERTEX_FIELD           = "dstVertexField"
  val ISBATCH: String            = "isbatch"

  val DEFAULT_TIMEOUT: Int            = 3000
  val DEFAULT_CONNECTION_TIMEOUT: Int = 3000
  val DEFAULT_CONNECTION_RETRY: Int   = 3
  val DEFAULT_EXECUTION_RETRY: Int    = 3
  val DEFAULT_USER_NAME: String       = "root"
  val DEFAULT_PASSWD: String          = "nebula"

  val DEFAULT_RATE_LIMIT: Long    = 1024L
  val DEFAULT_RATE_TIME_OUT: Long = 100
  val DEFAULT_POLICY: String      = null
  val DEFAULT_BATCH: Int          = 100

  val EMPTY_STRING: String = ""
}

/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.connector.exception

import com.facebook.thrift.TException

/***
  * An exception thrown if nebula client connects failed.
  */
class GraphConnectException(message: String, cause: Throwable = null)
    extends TException(message, cause)

/**
  * An exception thrown if a required option is missing form [[NebulaOptions]]
  */
class IllegalOptionException(message: String, cause: Throwable = null)
    extends IllegalArgumentException(message, cause)

/**
  * An exception thrown if nebula execution failed.
  */
class GraphExecuteException(message: String, cause: Throwable = null)
    extends TException(message, cause)

/**
  * An exception thrown if nebula execution occur rpc exception.
  */
class NebulaRPCException(message: String, cause: Throwable = null)
    extends RuntimeException(message, cause)

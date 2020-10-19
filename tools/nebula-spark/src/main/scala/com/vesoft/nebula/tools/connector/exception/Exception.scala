/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools.connector.exception

import com.facebook.thrift.TException

class Exception {}

class GraphConnectException(val message: String, val cause: Throwable)
    extends TException(message, cause)

class IllegalOptionException(val message: String, val cause: Throwable)
    extends IllegalArgumentException(message, cause)

class GraphExecuteException(val message: String, val cause: Throwable)
    extends TException(message, cause)

class NebulaRPCException(val message: String, val cause: Throwable)
    extends RuntimeException(message, cause)

/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.utils;

public class GraphConstants {
    /**
     * specify the target hosts in unmanaged model
     */
    public static final String CONN_ADDRESS = "nebula.connection.address";

    /**
     * retry count when create connection failed.
     */
    public static final String CONN_RETRY_COUNT = "nebula.connection.retry";

    /**
     * connection pool size.
     */
    public static final String CONN_POOL_SIZE = "nebula.connection.poolsize";

    /**
     * connection user name.
     */
    public static final String CONN_USERNAME = "nebula.user.name";

    /**
     * connection password.
     */
    public static final String CONN_PASSWORD = "nebula.user.password";

    /**
     * retry count when execution timeout.
     */
    public static final String EXEC_RETRY_COUNT = "nebula.execution.retry";

    /**
     * execution timeout.
     */
    public static final String EXEC_TIMEOUT = "nebula.execution.timeout";

    /**
     * failover retry count when there are exceptions in accessing graphd.
     */
    public static final String FAILOVER_RETRY_COUNT = "nebula.failover.retry";
}

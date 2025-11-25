/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.net;

public class Constants {

    static final int     DEFAULT_MAX_CLIENT_SIZE            = 10;
    static final int     DEFAULT_MIN_CLIENT_SIZE            = 1;
    static final long    DEFAULT_CONNECT_TIMEOUT_MS         = 3 * 1000; // 3 seconds
    static final long    DEFAULT_REQUEST_TIMEOUT_MS         = 60 * 1000; // 1 minute
    static final long    DEFAULT_MAX_TIMEOUT_MS             = Integer.MAX_VALUE; // about 25 day
    static final long    DEFAULT_MAX_PING_TIMEOUT_MS        = 10 * 60 * 1000;
    static final long    DEFAULT_PING_TIMEOUT_MS            = 1000L;
    static final long    DEFAULT_HEALTH_CHECK_TIME_MS       = 5 * 60 * 1000;
    static final boolean DEFAULT_TEST_ON_BORROW             = true;
    static final boolean DEFAULT_BLOCK_WHEN_EXHAUSTED       = false;
    static final long    DEFAULT_MAX_WAIT_MS                = Long.MAX_VALUE;
    static final long    DEFAULT_IDLE_EVICT_SCHEDULE_MS     = -1;
    static final long    DEFAULT_MIN_EVICTABLE_IDLE_TIME_MS = 30 * 60 * 1000;
    static final boolean DEFAULT_STRICT_SERVER_HEALTHY      = false;
    static final long    DEFAULT_MAX_LIFE_TIME_MS           = Long.MAX_VALUE;
    static final int     DEFAULT_BATCH_SIZE                 = 1000;
    static final int     DEFAULT_SCAN_PARALLEL              = 10;
    static final boolean DEFAULT_ENABLE_TLS                 = false;
    static final boolean DEFAULT_DISABLE_VERIFY_SERVER_CERT = false;
    static final boolean DEFAULT_TLS_PEER_NAME_VERIFY       = true;

}

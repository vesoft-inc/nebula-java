/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula;

import com.facebook.thrift.TException;
import com.google.common.net.HostAndPort;
import java.io.Closeable;

/**
 *
 */
public interface Client extends Closeable {

    public static final int DEFAULT_TIMEOUT_MS = 1000;
    public static final int DEFAULT_CONNECTION_RETRY_SIZE = 3;
    public static final int DEFAULT_EXECUTION_RETRY_SIZE = 3;
    public static final int DEFAULT_THREAD_COUNT = 10;

    public int doConnect(HostAndPort address) throws TException;

    public int connect() throws TException;

    public boolean isConnected();

    public void close();

}

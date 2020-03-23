/* Copyright (c) 2019 com.vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula;

import com.facebook.thrift.TException;

/**
 *
 */
public interface Client extends AutoCloseable {

    public static enum NebulaCode {
        SUCCEEDED(0);

        int code;

        int getCode() {
            return code;
        }

        private NebulaCode() {

        }

        NebulaCode(int code) {
            this.code = code;
        }
    }

    public static final int DEFAULT_TIMEOUT_MS = 1000;
    public static final int DEFAULT_CONNECTION_RETRY_SIZE = 3;
    public static final int DEFAULT_EXECUTION_RETRY_SIZE = 3;

    public int connect() throws TException;

    public boolean isConnected();

}

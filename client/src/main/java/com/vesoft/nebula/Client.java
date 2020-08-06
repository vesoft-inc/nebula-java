/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula;

import com.facebook.thrift.TException;
import java.io.Serializable;

/**
 *
 */
public interface Client extends AutoCloseable, Serializable {

    enum NebulaCode {
        SUCCEEDED(0);

        int code;

        int getCode() {
            return code;
        }

        NebulaCode(int code) {
            this.code = code;
        }
    }

    int DEFAULT_TIMEOUT_MS = 1000;
    int DEFAULT_CONNECTION_TIMEOUT_MS = 3000;
    int DEFAULT_CONNECTION_RETRY_SIZE = 3;
    int DEFAULT_EXECUTION_RETRY_SIZE = 3;

    int connect() throws TException;

    boolean isConnected();

}

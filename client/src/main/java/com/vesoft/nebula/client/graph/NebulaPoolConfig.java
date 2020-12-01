/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.graph;

public class NebulaPoolConfig {
    // The min connections in pool for all addresses
    private int minConnsSize = 0;

    // The max connections in pool for all addresses
    private int maxConnsSize = 10;

    // Socket timeout and Socket connection timeout, unit: millisecond
    private int timeout = 0;

    // The idleTime of the connection, unit: millisecond
    // The connection's idle time more than idleTime, it will be delete
    // 0 means never delete
    private int idleTime = 0;

    public int getMinConnSize() {
        return minConnsSize;
    }

    public void setMinConnSize(int minConnSize) {
        this.minConnsSize = minConnSize;
    }

    public int getMaxConnSize() {
        return maxConnsSize;
    }

    public void setMaxConnSize(int maxConnSize) {
        this.maxConnsSize = maxConnSize;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getIdleTime() {
        return idleTime;
    }

    public void setIdleTime(int idleTime) {
        this.idleTime = idleTime;
    }
}

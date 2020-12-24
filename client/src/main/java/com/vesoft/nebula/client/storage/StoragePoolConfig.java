/*
 * Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.storage;

public class StoragePoolConfig {
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

    // the max total connection in pool for all key
    private int maxTotal = 50;

    // the max total connection in pool for each key
    private int maxTotalPerKey = 10;

    public int getMinConnsSize() {
        return minConnsSize;
    }

    public void setMinConnsSize(int minConnsSize) {
        this.minConnsSize = minConnsSize;
    }

    public int getMaxConnsSize() {
        return maxConnsSize;
    }

    public void setMaxConnsSize(int maxConnsSize) {
        this.maxConnsSize = maxConnsSize;
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

    public int getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
    }

    public int getMaxTotalPerKey() {
        return maxTotalPerKey;
    }

    public void setMaxTotalPerKey(int maxTotalPerKey) {
        this.maxTotalPerKey = maxTotalPerKey;
    }
}

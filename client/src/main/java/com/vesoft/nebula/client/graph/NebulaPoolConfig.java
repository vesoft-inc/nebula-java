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

    // The interval time to check idle connection, unit ms, -1 means no check
    private int intervalIdle = -1;

    // The wait time to get idle connection, unit ms
    private int waitTime = 0;

    // The minimum rate of healthy servers to all servers. if 1 it means all servers should be available on init.
    private double minClusterHealthRate = 1;

    public int getMinConnSize() {
        return minConnsSize;
    }

    public NebulaPoolConfig setMinConnSize(int minConnSize) {
        this.minConnsSize = minConnSize;
        return this;
    }

    public int getMaxConnSize() {
        return maxConnsSize;
    }

    public NebulaPoolConfig setMaxConnSize(int maxConnSize) {
        this.maxConnsSize = maxConnSize;
        return this;
    }

    public int getTimeout() {
        return timeout;
    }

    public NebulaPoolConfig setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public int getIdleTime() {
        return idleTime;
    }

    public NebulaPoolConfig setIdleTime(int idleTime) {
        this.idleTime = idleTime;
        return this;
    }

    public int getIntervalIdle() {
        return intervalIdle;
    }

    public NebulaPoolConfig setIntervalIdle(int intervalIdle) {
        this.intervalIdle = intervalIdle;
        return this;
    }

    public int getWaitTime() {
        return waitTime;
    }

    public NebulaPoolConfig setWaitTime(int waitTime) {
        this.waitTime = waitTime;
        return this;
    }

    public double getMinClusterHealthRate() {
        return minClusterHealthRate;
    }

    public NebulaPoolConfig setMinClusterHealthRate(double minClusterHealthRate) {
        this.minClusterHealthRate = minClusterHealthRate;
        return this;
    }
}

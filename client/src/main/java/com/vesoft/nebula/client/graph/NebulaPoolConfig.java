/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.client.graph;

import com.vesoft.nebula.client.graph.data.SSLParam;
import java.io.Serializable;

public class NebulaPoolConfig implements Serializable {

    private static final long serialVersionUID = 3977910115039279651L;

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

    // the interval time to check idle connection, unit ms, -1 means no check
    private int intervalIdle = -1;

    // the wait time to get idle connection, unit ms
    private int waitTime = 0;

    // set to true to turn on ssl encrypted traffic
    private boolean enableSsl = false;

    // ssl param is required if ssl is turned on
    private SSLParam sslParam = null;

    public boolean isEnableSsl() {
        return enableSsl;
    }

    public void setEnableSsl(boolean enableSsl) {
        this.enableSsl = enableSsl;
    }

    public SSLParam getSslParam() {
        return sslParam;
    }

    public void setSslParam(SSLParam sslParam) {
        this.sslParam = sslParam;
    }

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
}

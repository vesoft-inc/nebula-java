/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.client.graph;

import com.vesoft.nebula.client.graph.data.SSLParam;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

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

    // The interval time to check idle connection, unit ms, -1 means no check
    private int intervalIdle = -1;

    // The wait time to get idle connection, unit ms
    private int waitTime = 0;

    // The minimum rate of healthy servers to all servers. if 1 it means all servers should be
    // available on init.
    private double minClusterHealthRate = 1;

    // Set to true to turn on ssl encrypted traffic
    private boolean enableSsl = false;

    // if enableSsl is true but SSL param is not config,
    // then client will not verify the server certificate. Encrypted transmission only.
    private SSLParam sslParam = null;

    // Set if use http2 protocol
    private boolean useHttp2 = false;

    // Set custom headers for http2
    private Map<String,String> customHeaders = new HashMap<>();

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

    public double getMinClusterHealthRate() {
        return minClusterHealthRate;
    }

    public NebulaPoolConfig setMinClusterHealthRate(double minClusterHealthRate) {
        this.minClusterHealthRate = minClusterHealthRate;
        return this;
    }

    public boolean isUseHttp2() {
        return useHttp2;
    }

    public NebulaPoolConfig setUseHttp2(boolean useHttp2) {
        this.useHttp2 = useHttp2;
        return this;
    }

    public Map<String, String> getCustomHeaders() {
        return customHeaders;
    }

    public NebulaPoolConfig setCustomHeaders(Map<String, String> customHeaders) {
        this.customHeaders = customHeaders;
        return this;
    }
}

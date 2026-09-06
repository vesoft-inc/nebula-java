/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.v3client.graph;

import com.vesoft.nebula.driver.v3client.graph.data.SSLParam;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Connection-pool configuration, matching the v3 client.
 *
 * <p>{@code useHttp2} and {@code customHeaders} have no equivalent in the v5 gRPC driver and are
 * kept for source compatibility only (they are ignored at runtime).
 */
public class NebulaPoolConfig implements Serializable {

    private static final long serialVersionUID = 3977910115039279651L;

    private int minConnsSize = 0;
    private int maxConnsSize = 10;
    private int timeout = 0;
    private int idleTime = 0;
    private int intervalIdle = -1;
    private int waitTime = 0;
    private double minClusterHealthRate = 1;
    private boolean enableSsl = false;
    private SSLParam sslParam = null;
    private boolean useHttp2 = false;
    private Map<String, String> customHeaders = new HashMap<>();

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

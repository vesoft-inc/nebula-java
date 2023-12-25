/* Copyright (c) 2022 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.client.graph;

import com.vesoft.nebula.client.graph.data.HostAddress;
import com.vesoft.nebula.client.graph.data.SSLParam;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SessionPoolConfig implements Serializable {

    private static final long serialVersionUID = -2266013330384849132L;

    private final List<HostAddress> graphAddressList;

    private final String username;
    private final String password;
    private final String spaceName;

    // The min connections in pool for all addresses
    private int minSessionSize = 1;

    // The max connections in pool for all addresses
    private int maxSessionSize = 10;

    // Socket timeout and Socket connection timeout, unit: millisecond
    private int timeout = 0;

    // The idleTime for clean the idle session
    // must be less than NebulaGraph's session_idle_timeout_secs, unit: second
    private int cleanTime = 3600;

    // The healthCheckTime for schedule check the health of session, unit: second
    private int healthCheckTime = 600;

    // retry times to get session
    private int retryConnectTimes = 1;

    // The wait time to get idle connection, unit ms
    private int waitTime = 0;

    // retry times for failed execute
    private int retryTimes = 3;

    // interval time for retry, unit ms
    private int intervalTime = 0;

    // whether reconnect when create session using a broken graphd server
    private boolean reconnect = false;

    // Set to true to turn on ssl encrypted traffic
    private boolean enableSsl = false;

    // SSL param is required if ssl is turned on
    private SSLParam sslParam = null;

    private boolean useHttp2 = false;

    private Map<String, String> customHeaders = new HashMap<>();

    private String version = null;


    public SessionPoolConfig(List<HostAddress> addresses,
                             String spaceName,
                             String username,
                             String password) {
        if (addresses == null || addresses.size() == 0) {
            throw new IllegalArgumentException("Graph addresses cannot be empty.");
        }
        if (spaceName == null || spaceName.trim().isEmpty()) {
            throw new IllegalArgumentException("space name cannot be blank.");
        }
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("user name cannot be blank.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("password cannot be blank.");
        }

        this.graphAddressList = addresses;
        this.spaceName = spaceName;
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public List<HostAddress> getGraphAddressList() {
        return graphAddressList;
    }

    public String getSpaceName() {
        return spaceName;
    }

    public int getMinSessionSize() {
        return minSessionSize;
    }

    public SessionPoolConfig setMinSessionSize(int minSessionSize) {
        if (minSessionSize < 1) {
            throw new IllegalArgumentException("minSessionSize cannot be less than 1.");
        }
        this.minSessionSize = minSessionSize;
        return this;
    }

    public int getMaxSessionSize() {
        return maxSessionSize;
    }

    public SessionPoolConfig setMaxSessionSize(int maxSessionSize) {
        if (maxSessionSize < 1) {
            throw new IllegalArgumentException("maxSessionSize cannot be less than 1.");
        }
        this.maxSessionSize = maxSessionSize;
        return this;
    }

    public int getTimeout() {
        return timeout;
    }

    public SessionPoolConfig setTimeout(int timeout) {
        if (timeout < 0) {
            throw new IllegalArgumentException("timeout cannot be less than 0.");
        }
        this.timeout = timeout;
        return this;
    }

    public int getCleanTime() {
        return cleanTime;
    }

    public SessionPoolConfig setCleanTime(int cleanTime) {
        if (cleanTime < 0) {
            throw new IllegalArgumentException("cleanTime cannot be less than 0.");
        }
        this.cleanTime = cleanTime;
        return this;
    }

    public int getHealthCheckTime() {
        return healthCheckTime;
    }

    public SessionPoolConfig setHealthCheckTime(int healthCheckTime) {
        if (healthCheckTime < 0) {
            throw new IllegalArgumentException("cleanTime cannot be less than 0.");
        }
        this.healthCheckTime = healthCheckTime;
        return this;
    }

    public int getRetryConnectTimes() {
        return retryConnectTimes;
    }

    public SessionPoolConfig setRetryConnectTimes(int retryConnectTimes) {
        if (retryConnectTimes < 0) {
            throw new IllegalArgumentException("retryConnectTimes cannot be less than 0.");
        }
        this.retryConnectTimes = retryConnectTimes;
        return this;
    }

    public int getWaitTime() {
        return waitTime;
    }

    public SessionPoolConfig setWaitTime(int waitTime) {
        if (waitTime < 0) {
            throw new IllegalArgumentException("waitTime cannot be less than 0.");
        }
        this.waitTime = waitTime;
        return this;
    }

    public int getRetryTimes() {
        return retryTimes;
    }

    public SessionPoolConfig setRetryTimes(int retryTimes) {
        if (retryTimes < 0) {
            throw new IllegalArgumentException("retryTimes cannot be less than 0.");
        }
        this.retryTimes = retryTimes;
        return this;
    }

    public int getIntervalTime() {
        return intervalTime;
    }

    public SessionPoolConfig setIntervalTime(int intervalTime) {
        if (intervalTime < 0) {
            throw new IllegalArgumentException("intervalTime cannot be less than 0.");
        }
        this.intervalTime = intervalTime;
        return this;
    }

    public boolean isReconnect() {
        return reconnect;
    }

    public SessionPoolConfig setReconnect(boolean reconnect) {
        this.reconnect = reconnect;
        return this;
    }

    public boolean isEnableSsl() {
        return enableSsl;
    }

    public SessionPoolConfig setEnableSsl(boolean enableSsl) {
        this.enableSsl = enableSsl;
        return this;
    }

    public SSLParam getSslParam() {
        return sslParam;
    }

    public SessionPoolConfig setSslParam(SSLParam sslParam) {
        this.sslParam = sslParam;
        return this;
    }

    public boolean isUseHttp2() {
        return useHttp2;
    }

    public SessionPoolConfig setUseHttp2(boolean useHttp2) {
        this.useHttp2 = useHttp2;
        return this;
    }

    public Map<String, String> getCustomHeaders() {
        return customHeaders;
    }

    public SessionPoolConfig setCustomHeaders(Map<String, String> customHeaders) {
        this.customHeaders = customHeaders;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "SessionPoolConfig{"
                + "username='" + username + '\''
                + ", graphAddressList=" + graphAddressList
                + ", spaceName='" + spaceName + '\''
                + ", minSessionSize=" + minSessionSize
                + ", maxSessionSize=" + maxSessionSize
                + ", timeout=" + timeout
                + ", idleTime=" + cleanTime
                + ", healthCheckTime=" + healthCheckTime
                + ", waitTime=" + waitTime
                + ", retryTimes=" + retryTimes
                + ", intervalTIme=" + intervalTime
                + ", reconnect=" + reconnect
                + ", enableSsl=" + enableSsl
                + ", sslParam=" + sslParam
                + ", useHttp2=" + useHttp2
                + ", customHeaders=" + customHeaders
                + '}';
    }
}

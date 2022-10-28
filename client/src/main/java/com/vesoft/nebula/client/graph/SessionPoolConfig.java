/* Copyright (c) 2022 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.client.graph;

import com.vesoft.nebula.client.graph.data.HostAddress;
import java.io.Serializable;
import java.util.List;

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

    // The healthCheckTime for schedule check the health of session
    private int healthCheckTime = 600;

    // The wait time to get idle connection, unit ms
    private int waitTime = 0;


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
                + '}';
    }
}

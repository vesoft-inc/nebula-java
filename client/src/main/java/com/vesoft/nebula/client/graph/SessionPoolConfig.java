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
    private int minConnsSize = 1;

    // The max connections in pool for all addresses
    private int maxConnsSize = 10;

    // The min connections in pool for all addresses
    private int minSessionSize = 0;

    // The max connections in pool for all addresses
    private int maxSessionSize = 10;

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

    public int getMinConnsSize() {
        return minConnsSize;
    }

    public SessionPoolConfig setMinConnsSize(int minConnsSize) {
        if (minConnsSize < 1) {
            throw new IllegalArgumentException("To ensure the correctness of SessionPool's init(),"
                    + " minConnsSize cannot be less than 1.");
        }
        this.minConnsSize = minConnsSize;
        return this;
    }

    public int getMaxConnsSize() {
        return maxConnsSize;
    }

    public SessionPoolConfig setMaxConnsSize(int maxConnsSize) {
        if (maxConnsSize < 1) {
            throw new IllegalArgumentException("maxConnsSize cannot be less than 1.");
        }
        this.maxConnsSize = maxConnsSize;
        return this;
    }

    public int getMinSessionSize() {
        return minSessionSize;
    }

    public SessionPoolConfig setMinSessionSize(int minSessionSize) {
        if (minSessionSize < 0) {
            throw new IllegalArgumentException("minSessionSize cannot be less than 0.");
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

    public int getIdleTime() {
        return idleTime;
    }

    public SessionPoolConfig setIdleTime(int idleTime) {
        if (idleTime < 0) {
            throw new IllegalArgumentException("idleTime cannot be less than 0.");
        }
        this.idleTime = idleTime;
        return this;
    }

    public int getIntervalIdle() {
        return intervalIdle;
    }

    public SessionPoolConfig setIntervalIdle(int intervalIdle) {
        if (intervalIdle < 0) {
            throw new IllegalArgumentException("intervalIdle cannot be less than 0.");
        }
        this.intervalIdle = intervalIdle;
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
                + ", minConnsSize=" + minConnsSize
                + ", maxConnsSize=" + maxConnsSize
                + ", minSessionSize=" + minSessionSize
                + ", maxSessionSize=" + maxSessionSize
                + ", timeout=" + timeout
                + ", idleTime=" + idleTime
                + ", intervalIdle=" + intervalIdle
                + ", waitTime=" + waitTime
                + '}';
    }
}

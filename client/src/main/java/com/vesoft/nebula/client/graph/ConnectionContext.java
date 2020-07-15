/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.graph;

/**
 * some props about connection
 */
public class ConnectionContext {
    private String hosts;
    private String user;
    private String password;
    private String space;
    private int timeout;
    private int connectionRetry;
    private int executionRetry;
    private int failoverRetry;
    private int poolSize;

    ConnectionContext withHosts(String hosts) {
        this.hosts = hosts;
        return this;
    }

    ConnectionContext withUser(String user) {
        this.user = user;
        return this;
    }

    ConnectionContext withPassword(String password) {
        this.password = password;
        return this;
    }

    ConnectionContext withTimeout(String timeout) {
        this.timeout = Integer.parseInt(timeout);
        return this;
    }

    ConnectionContext withConnectionRetry(String connectionRetry) {
        this.connectionRetry = Integer.parseInt(connectionRetry);
        return this;
    }

    ConnectionContext withExecutionRetry(String executionRetry) {
        this.executionRetry = Integer.parseInt(executionRetry);
        return this;
    }

    ConnectionContext withSpace(String space) {
        this.space = space;
        return this;
    }

    ConnectionContext withPoolSize(String poolSize) {
        this.poolSize = Integer.parseInt(poolSize);
        return this;
    }

    ConnectionContext withFailoverRetry(String failoverRetry) {
        int count = Integer.parseInt(failoverRetry);
        this.failoverRetry = count < 0 ? 0 : count;
        return this;
    }

    public String getHosts() {
        return hosts;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public int getTimeout() {
        return timeout;
    }

    public int getConnectionRetry() {
        return connectionRetry;
    }

    public int getExecutionRetry() {
        return executionRetry;
    }

    public String getSpace() {
        return this.space;
    }

    public int getPoolSize() {
        return this.poolSize;
    }

    public int getFailoverRetry() {
        return this.failoverRetry;
    }
}

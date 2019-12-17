/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.graph;

import com.facebook.thrift.TException;
import com.google.common.net.HostAndPort;
import com.vesoft.nebula.AbstractClient;
import java.util.List;

/**
 *
 */
public abstract class GraphClient extends AbstractClient {
    protected String user;
    protected String password;

    public GraphClient(List<HostAndPort> addresses, int timeout,
                       int connectionRetry, int executionRetry) {
        super(addresses, timeout, connectionRetry, executionRetry);
    }

    public GraphClient(List<HostAndPort> addresses) {
        super(addresses);
    }

    public GraphClient(String host, int port) {
        super(host, port);
    }

    public GraphClient withUser(String user) {
        this.user = user;
        return this;
    }

    public GraphClient withPassword(String password) {
        this.password = password;
        return this;
    }

    /**
     * Switch to the specified space.
     *
     * @param space space name.
     * @return The ErrorCode of status, 0 is succeeded.
     */
    public abstract int switchSpace(String space);

    /**
     * Execute the DML statement.
     *
     * @param statement execution statement.
     * @return The ErrorCode of status, 0 is succeeded.
     */
    public abstract int execute(String statement);

    /**
     * Execute the query statement and return result set.
     *
     * @param statement execution statement.
     * @return The result set of the query sentence.
     * @throws ConnectionException the connection exception
     * @throws NGQLException       the nebula exception
     * @throws TException          the thrift exception
     */
    public abstract ResultSet executeQuery(String statement)
            throws ConnectionException, NGQLException, TException;

    public void close() {
        transport.close();
    }
}

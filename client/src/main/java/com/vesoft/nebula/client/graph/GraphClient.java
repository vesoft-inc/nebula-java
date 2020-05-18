/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.graph;

import com.facebook.thrift.TException;
import com.vesoft.nebula.auth.AuthProvider;

/**
 * Currently, The GraphClient is not thread safe.
 */
public interface GraphClient extends AuthProvider, AutoCloseable {

    public int connect() throws TException;

    /**
     * Switch to the specified space.
     *
     * @param space space name.
     * @return The ErrorCode of status, 0 is succeeded.
     */
    public int switchSpace(String space);

    /**
     * Execute the DML statement.
     *
     * @param statement execution statement.
     * @return The ErrorCode of status, 0 is succeeded.
     */
    public int execute(String statement);

    /**
     * Execute the query statement and return result set.
     *
     * @param statement execution statement.
     * @return The result set of the query sentence.
     * @throws ConnectionException the connection exception
     * @throws NGQLException       the nebula exception
     * @throws TException          the thrift exception
     */
    public ResultSet executeQuery(String statement)
            throws ConnectionException, NGQLException, TException;
}

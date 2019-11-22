/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.graph.client.async;

import com.facebook.thrift.TException;
import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import com.vesoft.nebula.Client;
import com.vesoft.nebula.graph.client.ConnectionException;
import com.vesoft.nebula.graph.client.NGQLException;
import com.vesoft.nebula.graph.client.ResultSet;


public interface AsyncGraphClient extends Client {

    /**
     * Switch to the specified space.
     *
     * @param space space name.
     * @return
     */
    public ListenableFuture<Optional<Integer>> switchSpace(String space);

    /**
     * Connect to nebula query engine.
     *
     * @param username User name
     * @param password User password
     * @return
     */
    public int connect(String username, String password);

    /**
     * Execute the DML statement.
     *
     * @param statement execution statement.
     * @return
     */
    public ListenableFuture<Optional<Integer>> execute(String statement);

    /**
     * Execute the query statement and return result set.
     *
     * @param statement execution statement.
     * @return
     */
    public ListenableFuture<Optional<ResultSet>> executeQuery(String statement)
            throws ConnectionException, NGQLException, TException;
}

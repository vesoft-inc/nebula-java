/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.graph.async;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import com.vesoft.nebula.auth.AuthProvider;
import com.vesoft.nebula.client.graph.ResultSet;

public interface AsyncGraphClient extends AuthProvider {

    public int connect();

    /**
     * Switch to the specified space.
     *
     * @param space space name.
     * @return
     */
    public ListenableFuture<Optional<Integer>> switchSpace(String space);

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
    public ListenableFuture<Optional<ResultSet>> executeQuery(String statement);
}

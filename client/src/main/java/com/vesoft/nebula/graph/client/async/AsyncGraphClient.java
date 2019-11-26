/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.graph.client.async;

import com.vesoft.nebula.Client;
import com.vesoft.nebula.graph.client.async.entry.ExecuteCallback;


public interface AsyncGraphClient extends Client {

    /**
     * Switch to the specified space.
     *
     * @param space space name.
     * @return
     */
    public ExecuteCallback switchSpace(String space);

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
    public ExecuteCallback execute(String statement);
}

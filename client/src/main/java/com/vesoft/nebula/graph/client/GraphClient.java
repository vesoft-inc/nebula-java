/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.graph.client;

import com.facebook.thrift.TException;
import com.vesoft.nebula.Client;

/**
 *
 */
public interface GraphClient extends Client {

    /**
     * @param username
     * @param password
     * @return
     */
    public int connect(String username, String password);

    /**
     * @param space
     * @return
     */
    public int switchSpace(String space);

    /**
     * @param statement
     * @return
     */
    public int execute(String statement);

    /**
     * @param statement
     * @return
     * @throws ConnectionException
     * @throws NGQLException
     * @throws TException
     */
    public ResultSet executeQuery(String statement)
            throws ConnectionException, NGQLException, TException;
}

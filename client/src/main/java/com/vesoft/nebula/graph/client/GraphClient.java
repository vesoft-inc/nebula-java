/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.graph.client;

import com.facebook.thrift.TException;
import com.vesoft.nebula.Client;

public interface GraphClient extends Client {

    public int connect(String username, String password);

    public int switchSpace(String space);

    public int execute(String statement);

    public ResultSet executeQuery(String statement)
            throws ConnectionException, NGQLException, TException;
}

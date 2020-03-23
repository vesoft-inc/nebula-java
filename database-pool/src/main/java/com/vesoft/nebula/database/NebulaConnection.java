/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.database;


import com.facebook.thrift.TException;
import com.vesoft.nebula.client.graph.ConnectionException;
import com.vesoft.nebula.client.graph.NGQLException;
import com.vesoft.nebula.client.graph.ResultSet;


/**
 * @author huangzhaolai-jk
 * @version 1.0.0
 * @Description NebulaConnection
 * @Date 2020/3/17 - 14:00
 */
public interface NebulaConnection {
    /**
     * Switch the space
     * @param space
     * @return result
     */
    int switchSpace(String space);

    /**
     * execute
     * @param statement
     * @return result
     */
    int execute(String statement);

    /**
     * check connection is opened
     * @return isOpened
     */
    boolean isOpened();

    /**
     * execute query
     * @param statement
     * @return result
     * @throws ConnectionException
     * @throws NGQLException
     * @throws TException
     */
    ResultSet executeQuery(String statement)throws ConnectionException,
            NGQLException, TException;

}
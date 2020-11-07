/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package org.apache.flink.connector.nebula.connection.client;


import com.facebook.thrift.TException;
import com.vesoft.nebula.Client;
import org.apache.flink.connector.nebula.utils.NebulaConstant;

public abstract class AbstractNebulaClient {
    protected int timeout = NebulaConstant.DEFAULT_TIMEOUT_MS;
    protected int connectTimeout = NebulaConstant.DEFAULT_CONNECT_TIMEOUT_MS;
    protected int connectionRetry = NebulaConstant.DEFAULT_CONNECT_RETRY;
    protected  int executionRetry = NebulaConstant.DEFAULT_EXECUTION_RETRY;

    public AbstractNebulaClient(){}

    public AbstractNebulaClient(int timeout,int connectionRetry,int executionRetry){
        this.timeout = timeout;
        this.connectionRetry = connectionRetry;
        this.executionRetry = executionRetry;
    }

    abstract public Client connectClient(String address, String username, String password) throws TException;

}

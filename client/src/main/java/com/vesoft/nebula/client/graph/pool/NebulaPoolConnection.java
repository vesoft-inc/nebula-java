/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.graph.pool;

import com.facebook.thrift.TException;
import com.facebook.thrift.transport.TTransport;
import com.vesoft.nebula.client.graph.ConnectionException;
import com.vesoft.nebula.client.graph.ExecuteUtils;
import com.vesoft.nebula.client.graph.NGQLException;
import com.vesoft.nebula.client.graph.NebulaConnection;
import com.vesoft.nebula.client.graph.ResultSet;
import com.vesoft.nebula.graph.GraphService;
import java.util.Objects;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * @author huangzhaolai-jk
 * @version 1.0.0
 * @Description NebulaPoolConnection
 * @Date 2020/3/17 - 13:57
 */
public class NebulaPoolConnection implements NebulaConnection {

    private final Logger log = LogManager.getLogger(NebulaPoolConnection.class);

    private GraphService.Client graphClient;

    private TTransport transport;

    private long sessionId;

    private int executionRetry;

    NebulaPoolConnection(GraphService.Client graphClient,
                         TTransport transport,
                         long sessionId,
                         int executionRetry) {
        this.graphClient = graphClient;
        this.transport = transport;
        this.sessionId = sessionId;
        this.executionRetry = executionRetry;
    }

    @Override
    public int switchSpace(String space) {
        return execute(String.format("USE %s", space));
    }

    @Override
    public int execute(String statement) {
        return ExecuteUtils.execute(transport, graphClient, sessionId, statement, executionRetry);
    }

    @Override
    public ResultSet executeQuery(String statement) throws ConnectionException,
            NGQLException, TException {
        return ExecuteUtils.executeQuery(transport, graphClient, sessionId, statement);
    }

    @Override
    public boolean isOpened() {
        return transport != null && transport.isOpen();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NebulaPoolConnection that = (NebulaPoolConnection) o;
        return sessionId == that.sessionId
                && Objects.equals(graphClient, that.graphClient)
                && Objects.equals(transport, that.transport);
    }

    @Override
    public int hashCode() {
        return Objects.hash(graphClient, transport, sessionId);
    }
}

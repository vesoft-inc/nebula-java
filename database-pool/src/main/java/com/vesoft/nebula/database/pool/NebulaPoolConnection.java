package com.vesoft.nebula.database.pool;

import com.facebook.thrift.TException;
import com.facebook.thrift.transport.TTransport;
import com.vesoft.nebula.database.NebulaConnection;
import com.vesoft.nebula.graph.ErrorCode;
import com.vesoft.nebula.graph.ExecutionResponse;
import com.vesoft.nebula.graph.GraphService;
import com.vesoft.nebula.graph.client.ConnectionException;
import com.vesoft.nebula.graph.client.GraphClient;
import com.vesoft.nebula.graph.client.NGQLException;
import com.vesoft.nebula.graph.client.ResultSet;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @author huangzhaolai-jk
 * @version 1.0.0
 * @Description NebulaPoolConnection is used for
 * @Date 2020/3/17 - 13:57
 */
@Slf4j
public class NebulaPoolConnection implements NebulaConnection {

    private GraphService.Client graphClient;

    private TTransport transport;

    private long sessionId;

    private int executionRetry;

    NebulaPoolConnection(GraphService.Client graphClient, TTransport transport, long sessionId, int executionRetry) {
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
        if (checkTransportClosed(transport)) {
            return ErrorCode.E_DISCONNECTED;
        }
        int retry = executionRetry;
        while (retry-- >= 0) {
            try {
                ExecutionResponse executionResponse = graphClient.execute(sessionId, statement);
                if (executionResponse.getError_code() != ErrorCode.SUCCEEDED) {
                    log.error("execute error: " + executionResponse.getError_msg());
                }
                return executionResponse.getError_code();
            } catch (TException e) {
                log.error("Thrift rpc call failed: " + e.getMessage());
                return ErrorCode.E_RPC_FAILURE;
            }
        }
        return ErrorCode.E_RPC_FAILURE;
    }

    private boolean checkTransportClosed(TTransport transport) {
        return transport == null || !transport.isOpen();
    }

    @Override
    public ResultSet executeQuery(String statement) throws ConnectionException,
            NGQLException, TException {
        if (checkTransportClosed(transport)) {
            log.error("Thrift rpc call failed");
            throw new ConnectionException();
        }

        ExecutionResponse executionResponse = graphClient.execute(sessionId, statement);
        int code = executionResponse.getError_code();
        if (code == ErrorCode.SUCCEEDED) {
            return new ResultSet(executionResponse.getColumn_names(),
                    executionResponse.getRows());
        } else {
            log.error("Execute error: " + executionResponse.getError_msg());
            throw new NGQLException(code);
        }
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
        return sessionId == that.sessionId &&
                Objects.equals(graphClient, that.graphClient) &&
                Objects.equals(transport, that.transport);
    }

    @Override
    public int hashCode() {
        return Objects.hash(graphClient, transport, sessionId);
    }
}

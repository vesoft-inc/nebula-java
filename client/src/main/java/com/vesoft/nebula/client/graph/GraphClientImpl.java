/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.graph;

import com.facebook.thrift.TException;
import com.facebook.thrift.protocol.TCompactProtocol;
import com.facebook.thrift.transport.TSocket;
import com.google.common.net.HostAndPort;
import com.vesoft.nebula.AbstractClient;
import com.vesoft.nebula.graph.AuthResponse;
import com.vesoft.nebula.graph.ErrorCode;
import com.vesoft.nebula.graph.ExecutionResponse;
import com.vesoft.nebula.graph.GraphService;
import java.util.List;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Java thrift client wrapper.
 */
public class GraphClientImpl extends AbstractClient implements GraphClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(GraphClientImpl.class);

    protected String user;
    protected String password;
    private long sessionID;
    private GraphService.Client client;

    public GraphClientImpl(List<HostAndPort> addresses, int timeout,
                           int connectionRetry, int executionRetry) {
        super(addresses, timeout, connectionRetry, executionRetry);
    }

    public GraphClientImpl(List<HostAndPort> addresses) {
        super(addresses);
    }

    public GraphClientImpl(String host, int port) {
        super(host, port);
    }

    @Override
    public int doConnect(List<HostAndPort> addresses) throws TException {
        Random random = new Random(System.currentTimeMillis());
        int position = random.nextInt(addresses.size());
        HostAndPort address = addresses.get(position);
        transport = new TSocket(address.getHostText(), address.getPort(), timeout);
        transport.open();
        protocol = new TCompactProtocol(transport);
        client = new GraphService.Client(protocol);
        AuthResponse result = client.authenticate(user, password);
        if (result.getError_code() == ErrorCode.E_BAD_USERNAME_PASSWORD) {
            LOGGER.error("User name or password error");
            return ErrorCode.E_BAD_USERNAME_PASSWORD;
        }

        if (result.getError_code() != ErrorCode.SUCCEEDED) {
            LOGGER.error(String.format("Connect address %s failed : %s",
                    address.toString(), result.getError_msg()));
        } else {
            sessionID = result.getSession_id();
        }
        return ErrorCode.SUCCEEDED;
    }

    /**
     * Switch Graph Space
     *
     * @param space The space name
     * @return The ErrorCode of status, 0 is succeeded.
     */
    public int switchSpace(String space) {
        return execute(String.format("USE %s", space));
    }

    /**
     * Execute the query sentence.
     *
     * @param statement The query sentence.
     * @return The ErrorCode of status, 0 is succeeded.
     */
    @Override
    public int execute(String statement) {
        if (!checkTransportOpened(transport)) {
            return ErrorCode.E_DISCONNECTED;
        }

        int retry = executionRetry;
        while (retry-- > 0) {
            try {
                ExecutionResponse executionResponse = client.execute(sessionID, statement);
                if (executionResponse.getError_code() != ErrorCode.SUCCEEDED) {
                    LOGGER.error("execute error: " + executionResponse.getError_msg());
                }
                return executionResponse.getError_code();
            } catch (TException e) {
                LOGGER.error("Thrift rpc call failed: " + e.getMessage());
                return ErrorCode.E_RPC_FAILURE;
            }
        }
        return ErrorCode.E_RPC_FAILURE;
    }

    /**
     * Execute the query sentence which will return a ResultSet.
     *
     * @param statement The query sentence.
     * @return The result set of the query sentence.
     */
    @Override
    public ResultSet executeQuery(String statement) throws ConnectionException,
            NGQLException, TException {
        if (!checkTransportOpened(transport)) {
            LOGGER.error("Thrift rpc call failed");
            throw new ConnectionException();
        }

        ExecutionResponse executionResponse = client.execute(sessionID, statement);
        int code = executionResponse.getError_code();
        if (code == ErrorCode.SUCCEEDED) {
            return new ResultSet(executionResponse.getColumn_names(),
                    executionResponse.getRows());
        } else {
            LOGGER.error("Execute error: " + executionResponse.getError_msg());
            throw new NGQLException(code);
        }
    }


    /**
     * Sign out from Graph Services.
     */
    public void close() {
        super.close();
        try {
            client.signout(sessionID);
        } catch (TException e) {
            LOGGER.error("Disconnect error: " + e.getMessage());
        } finally {
            transport.close();
        }
    }

    @Override
    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }
}

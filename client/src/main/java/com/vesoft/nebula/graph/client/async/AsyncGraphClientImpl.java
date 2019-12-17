/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.graph.client.async;

import static com.google.common.base.Preconditions.checkArgument;

import com.facebook.thrift.TBase;
import com.facebook.thrift.TException;
import com.facebook.thrift.async.TAsyncClientManager;
import com.facebook.thrift.protocol.TBinaryProtocol;
import com.facebook.thrift.protocol.TProtocolFactory;
import com.facebook.thrift.transport.TNonblockingSocket;
import com.facebook.thrift.transport.TNonblockingTransport;
import com.facebook.thrift.transport.TTransportException;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.net.HostAndPort;
import com.google.common.net.InetAddresses;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.vesoft.nebula.graph.AuthResponse;
import com.vesoft.nebula.graph.ErrorCode;
import com.vesoft.nebula.graph.ExecutionResponse;
import com.vesoft.nebula.graph.GraphService;
import com.vesoft.nebula.graph.client.NGQLException;
import com.vesoft.nebula.graph.client.ResultSet;
import com.vesoft.nebula.graph.client.async.entry.AuthenticateCallback;
import com.vesoft.nebula.graph.client.async.entry.ExecuteCallback;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsyncGraphClientImpl implements AsyncGraphClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncGraphClientImpl.class);

    private ListeningExecutorService service;

    private final List<HostAndPort> addresses;
    private final int connectionRetry;
    private final int executionRetry;
    private final int timeout;
    private long sessionID;
    private GraphService.AsyncClient client;
    private TNonblockingTransport transport = null;
    private TAsyncClientManager manager;

    /**
     * The Constructor of Graph Client.
     *
     * @param addresses       The addresses of graph services.
     * @param timeout         The timeout of RPC request.
     * @param connectionRetry The number of retries when connection failure.
     * @param executionRetry  The number of retries when execution failure.
     */
    public AsyncGraphClientImpl(List<HostAndPort> addresses, int timeout, int connectionRetry,
                                int executionRetry) {
        checkArgument(timeout > 0);
        checkArgument(connectionRetry > 0);
        for (HostAndPort address : addresses) {
            String host = address.getHostText();
            int port = address.getPort();
            if (!InetAddresses.isInetAddress(host) || (port <= 0 || port >= 65535)) {
                throw new IllegalArgumentException(String.format("%s:%d is not a valid address",
                    host, port));
            }
        }

        service = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());

        this.addresses = addresses;
        this.timeout = timeout;
        this.connectionRetry = connectionRetry;
        this.executionRetry = executionRetry;
    }

    /**
     * The Constructor of Graph Client.
     *
     * @param addresses The addresses of graph services.
     */
    public AsyncGraphClientImpl(List<HostAndPort> addresses) {
        this(addresses, DEFAULT_TIMEOUT_MS, DEFAULT_CONNECTION_RETRY_SIZE,
            DEFAULT_EXECUTION_RETRY_SIZE);
    }

    /**
     * The Constructor of Graph Client.
     *
     * @param host The host of graph services.
     * @param port The port of graph services.
     */
    public AsyncGraphClientImpl(String host, int port) {
        this(Lists.newArrayList(HostAndPort.fromParts(host, port)), DEFAULT_TIMEOUT_MS,
            DEFAULT_CONNECTION_RETRY_SIZE, DEFAULT_EXECUTION_RETRY_SIZE);
    }

    /**
     * Switch Graph Space
     *
     * @param space The space name
     * @return The ErrorCode of status, 0 is succeeded.
     */
    @Override
    public ListenableFuture<Optional<Integer>> switchSpace(String space) {
        return execute(String.format("USE %s", space));
    }

    /**
     * Connect to the Graph Services.
     *
     * @param username The user's name, default is user.
     * @param password The user's password, default is password.
     * @return The ErrorCode of status, 0 is succeeded.
     */
    @Override
    public int connect(String username, String password) {
        int retry = connectionRetry;
        while (retry-- != 0) {
            Random random = new Random(System.currentTimeMillis());
            int position = random.nextInt(addresses.size());
            HostAndPort address = addresses.get(position);

            try {
                manager = new TAsyncClientManager();
                transport = new TNonblockingSocket(address.getHostText(),
                        address.getPort(), timeout);
                TProtocolFactory protocol = new TBinaryProtocol.Factory();
                client = new GraphService.AsyncClient(protocol, manager, transport);
                AuthenticateCallback callback = new AuthenticateCallback();
                client.authenticate(username, password, callback);
                Optional<TBase> respOption = Optional.absent();
                while (!callback.checkReady()) {
                    respOption = (Optional<TBase>) callback.getResult();
                }
                if (respOption.isPresent()) {
                    AuthResponse result = (AuthResponse) respOption.get();
                    if (result.getError_code() == ErrorCode.E_BAD_USERNAME_PASSWORD) {
                        LOGGER.error("User name or password error");
                        return ErrorCode.E_BAD_USERNAME_PASSWORD;
                    }

                    if (result.getError_code() != ErrorCode.SUCCEEDED) {
                        LOGGER.error(String.format("Connect address %s failed : %s",
                            address.toString(), result.getError_msg()));
                    } else {
                        sessionID = result.getSession_id();
                        return ErrorCode.SUCCEEDED;
                    }
                } else {
                    LOGGER.info(String.format("Auth not founded"));
                }
            } catch (TTransportException tte) {
                LOGGER.error("Connect failed: " + tte.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return ErrorCode.E_FAIL_TO_CONNECT;
    }

    /**
     * Execute the query sentence.
     *
     * @param statement The query sentence.
     * @return The ErrorCode of status, 0 is succeeded.
     */
    public ListenableFuture<Optional<Integer>> execute(final String statement) {
        return service.submit(new Callable<Optional<Integer>>() {
            @Override
            public Optional<Integer> call() throws Exception {
                ExecuteCallback callback = new ExecuteCallback();
                try {
                    client.execute(sessionID, statement, callback);
                } catch (TException e) {
                    e.printStackTrace();
                }
                while (!callback.checkReady()) {
                    Thread.sleep(100);
                }
                if (callback.getResult().isPresent()) {
                    ExecutionResponse resp = (ExecutionResponse) callback.getResult().get();
                    if (resp.getError_code() != ErrorCode.SUCCEEDED) {
                        LOGGER.error("execute error: " + resp.getError_msg());
                    }
                    return Optional.of(resp.getError_code());
                } else {
                    return Optional.absent();
                }
            }
        });
    }

    @Override
    public ListenableFuture<Optional<ResultSet>> executeQuery(final String statement) {
        return service.submit(new Callable<Optional<ResultSet>>() {
            @Override
            public Optional<ResultSet> call() throws Exception {
                ExecuteCallback callback = new ExecuteCallback();
                try {
                    client.execute(sessionID, statement, callback);
                } catch (TException e) {
                    e.printStackTrace();
                }
                while (!callback.checkReady()) {
                    Thread.sleep(100);
                }
                if (callback.getResult().isPresent()) {
                    ExecutionResponse resp = (ExecutionResponse) callback.getResult().get();
                    int code = resp.getError_code();
                    if (code == ErrorCode.SUCCEEDED) {
                        ResultSet rs = new ResultSet(resp.getColumn_names(), resp.getRows());
                        return Optional.of(rs);
                    } else {
                        LOGGER.error("Execute error: " + resp.getError_msg());
                        throw new NGQLException(code);
                    }
                } else {
                    return Optional.absent();
                }
            }
        });
    }

    @Override
    public void close() {
        service.shutdown();
        transport.close();
        try {
            manager.stop();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

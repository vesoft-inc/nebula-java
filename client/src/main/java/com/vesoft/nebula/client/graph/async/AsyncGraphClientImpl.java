/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.graph.async;

import com.facebook.thrift.TBase;
import com.facebook.thrift.TException;
import com.facebook.thrift.async.TAsyncClientManager;
import com.facebook.thrift.protocol.TBinaryProtocol;
import com.facebook.thrift.protocol.TProtocolFactory;
import com.facebook.thrift.transport.TNonblockingSocket;
import com.facebook.thrift.transport.TTransportException;
import com.google.common.base.Optional;
import com.google.common.net.HostAndPort;
import com.google.common.util.concurrent.ListenableFuture;
import com.vesoft.nebula.client.graph.NGQLException;
import com.vesoft.nebula.client.graph.ResultSet;
import com.vesoft.nebula.client.graph.async.entry.AuthenticateCallback;
import com.vesoft.nebula.client.graph.async.entry.ExecuteCallback;
import com.vesoft.nebula.graph.AuthResponse;
import com.vesoft.nebula.graph.ErrorCode;
import com.vesoft.nebula.graph.ExecutionResponse;
import com.vesoft.nebula.graph.GraphService;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsyncGraphClientImpl extends AsyncGraphClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncGraphClientImpl.class);

    private long sessionID;
    private GraphService.AsyncClient client;
    private String user;
    private String password;

    public AsyncGraphClientImpl(List addresses, int timeout,
                                int connectionRetry, int executionRetry) {
        super(addresses, timeout, connectionRetry, executionRetry);
    }

    public AsyncGraphClientImpl(List<HostAndPort> addresses) {
        super(addresses);
    }

    public AsyncGraphClientImpl(String host, int port) {
        super(host, port);
    }

    @Override
    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public int doConnect(List<HostAndPort> addresses) throws TException {
        Random random = new Random(System.currentTimeMillis());
        int position = random.nextInt(addresses.size());
        HostAndPort address = addresses.get(position);

        try {
            manager = new TAsyncClientManager();
            transport = new TNonblockingSocket(address.getHost(),
                    address.getPort(), timeout);
            TProtocolFactory protocol = new TBinaryProtocol.Factory();
            client = new GraphService.AsyncClient(protocol, manager, transport);
            client.setTimeout(timeout);
            AuthenticateCallback callback = new AuthenticateCallback();
            client.authenticate(user, password, callback);
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
        return ErrorCode.E_FAIL_TO_CONNECT;
    }

    @Override
    public ListenableFuture<Optional<Integer>> switchSpace(String space) {
        return execute(String.format("USE %s", space));
    }

    /**
     * Execute the query sentence.
     *
     * @param statement The query sentence.
     * @return The ErrorCode of status, 0 is succeeded.
     */
    public ListenableFuture<Optional<Integer>> execute(final String statement) {
        return service.submit(() -> {
            ExecuteCallback callback = new ExecuteCallback();
            try {
                client.execute(sessionID, statement, callback);
            } catch (TException e) {
                e.printStackTrace();
            }
            while (!callback.checkReady()) {
                Thread.sleep(1);
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
                    Thread.sleep(1);
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
        if (service != null && !service.isShutdown()) {
            service.shutdown();
        }
        if (transport != null && transport.isOpen()) {
            transport.close();
        }
        try {
            manager.stop();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

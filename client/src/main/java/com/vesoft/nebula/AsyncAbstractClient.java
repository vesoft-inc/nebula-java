/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula;

import com.facebook.thrift.TException;
import com.facebook.thrift.async.TAsyncClientManager;
import com.facebook.thrift.protocol.TProtocolFactory;
import com.facebook.thrift.transport.TNonblockingTransport;
import com.google.common.net.HostAndPort;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.vesoft.nebula.graph.ErrorCode;
import java.util.List;
import java.util.concurrent.Executors;

public abstract class AsyncAbstractClient extends AbstractClient {
    protected ListeningExecutorService service;
    protected TAsyncClientManager manager;
    protected TNonblockingTransport transport;
    protected TProtocolFactory protocolFactory;

    public AsyncAbstractClient(List<HostAndPort> addresses, int timeout,
                               int connectionRetry, int executionRetry) {
        super(addresses, timeout, DEFAULT_CONNECTION_TIMEOUT_MS, connectionRetry, executionRetry);
    }

    public AsyncAbstractClient(List<HostAndPort> addresses) {
        super(addresses);
    }

    public AsyncAbstractClient(String host, int port) {
        super(host, port);
    }

    @Override
    public int connect() throws TException {
        service = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(1));
        int retry = connectionRetry;
        int code = ErrorCode.E_DISCONNECTED;
        while (retry-- != 0) {
            code = doConnect(addresses);
            if (code == 0) {
                break;
            }
        }
        return code;
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

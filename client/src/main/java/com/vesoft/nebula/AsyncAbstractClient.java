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
import java.util.List;

public abstract class AsyncAbstractClient extends AbstractClient {
    protected ListeningExecutorService service;
    protected TAsyncClientManager manager;
    protected TNonblockingTransport transport;
    protected TProtocolFactory protocolFactory;

    public AsyncAbstractClient(List<HostAndPort> addresses, int timeout,
                               int connectionRetry, int executionRetry) {
        super(addresses, timeout, connectionRetry, executionRetry);
    }

    public AsyncAbstractClient(List<HostAndPort> addresses) {
        super(addresses);
    }

    public AsyncAbstractClient(String host, int port) {
        super(host, port);
    }

    @Override
    public int connect() throws TException {
        int retry = connectionRetry;
        while (retry-- != 0) {
            int code = doConnect(addresses);
            if (code == 0) {
                break;
            }
        }
        return -1;
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

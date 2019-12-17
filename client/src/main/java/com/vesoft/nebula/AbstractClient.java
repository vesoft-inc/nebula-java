/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula;

import static com.google.common.base.Preconditions.checkArgument;

import com.facebook.thrift.TException;
import com.facebook.thrift.protocol.TCompactProtocol;
import com.facebook.thrift.protocol.TProtocol;
import com.facebook.thrift.transport.TSocket;
import com.facebook.thrift.transport.TTransport;

import com.google.common.collect.Lists;
import com.google.common.net.HostAndPort;
import com.google.common.net.InetAddresses;
import com.vesoft.nebula.graph.ErrorCode;
import java.util.List;
import java.util.Random;

public abstract class AbstractClient implements Client {
    protected final List<HostAndPort> addresses;
    protected final int connectionRetry;
    protected final int executionRetry;
    protected final int timeout;
    protected TProtocol protocol;
    protected TTransport transport = null;

    /**
     * The Constructor of Client.
     *
     * @param addresses       The addresses of graph services.
     * @param timeout         The timeout of RPC request.
     * @param connectionRetry The number of retries when connection failure.
     * @param executionRetry  The number of retries when execution failure.
     */
    public AbstractClient(List<HostAndPort> addresses, int timeout,
                          int connectionRetry, int executionRetry) {
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
    public AbstractClient(List<HostAndPort> addresses) {
        this(addresses, DEFAULT_TIMEOUT_MS, DEFAULT_CONNECTION_RETRY_SIZE,
                DEFAULT_EXECUTION_RETRY_SIZE);
    }

    /**
     * The Constructor of Graph Client.
     *
     * @param host The host of graph services.
     * @param port The port of graph services.
     */
    public AbstractClient(String host, int port) {
        this(Lists.newArrayList(HostAndPort.fromParts(host, port)), DEFAULT_TIMEOUT_MS,
                DEFAULT_CONNECTION_RETRY_SIZE, DEFAULT_EXECUTION_RETRY_SIZE);
    }

    public int connect() throws TException {
        int retry = connectionRetry;
        while (retry-- != 0) {
            Random random = new Random(System.currentTimeMillis());
            int position = random.nextInt(addresses.size());
            HostAndPort address = addresses.get(position);
            transport = new TSocket(address.getHostText(), address.getPort(), timeout);
            transport.open();
            protocol = new TCompactProtocol(transport);
            return doConnect(address);
        }
        return ErrorCode.E_FAIL_TO_CONNECT;
    }

    /**
     * @return
     */
    public boolean isConnected() {
        return transport.isOpen();
    }

    protected boolean checkTransportOpened(TTransport transport) {
        return transport != null && transport.isOpen();
    }

}

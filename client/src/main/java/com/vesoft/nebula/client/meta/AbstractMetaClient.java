/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.meta;

import com.facebook.thrift.protocol.TProtocol;
import com.facebook.thrift.transport.TTransport;
import com.google.common.base.Preconditions;
import com.google.common.net.InetAddresses;
import com.vesoft.nebula.client.graph.data.HostAddress;
import java.util.List;

public class AbstractMetaClient {
    protected final List<HostAddress> addresses;
    protected final int connectionRetry;
    protected final int executionRetry;
    protected final int timeout;

    protected TProtocol protocol;
    protected TTransport transport;

    public AbstractMetaClient(List<HostAddress> addresses, int timeout,
                              int connectionRetry, int executionRetry) {
        Preconditions.checkArgument(timeout > 0);
        Preconditions.checkArgument(connectionRetry > 0);
        Preconditions.checkArgument(executionRetry > 0);
        for (HostAddress address : addresses) {
            String host = address.getHost();
            int port = address.getPort();
            // check if the address is a valid ip address or uri address, and chechk if the port is a valid port
            if ((!InetAddresses.isInetAddress(host) || !InetAddresses.isUriInetAddress(host))
                || (port <= 0 || port >= 65535)) {
                throw new IllegalArgumentException(String.format("%s:%d is not a valid address",
                        host, port));
            }
        }

        this.addresses = addresses;
        this.timeout = timeout;
        this.connectionRetry = connectionRetry;
        this.executionRetry = executionRetry;
    }

    public int getConnectionRetry() {
        return connectionRetry;
    }

    public int getExecutionRetry() {
        return executionRetry;
    }

    public int getTimeout() {
        return timeout;
    }
}

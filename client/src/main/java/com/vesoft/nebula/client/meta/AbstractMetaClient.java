/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.client.meta;

import com.facebook.thrift.protocol.THeaderProtocol;
import com.facebook.thrift.protocol.TProtocol;
import com.facebook.thrift.transport.THeaderTransport;
import com.facebook.thrift.transport.TTransport;
import com.google.common.base.Preconditions;
import com.google.common.net.InetAddresses;
import com.google.common.net.InternetDomainName;
import com.vesoft.nebula.client.graph.data.HostAddress;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

public class AbstractMetaClient implements Serializable {
    protected final List<HostAddress> addresses;
    protected final int connectionRetry;
    protected final int executionRetry;
    protected final int timeout;

    protected THeaderProtocol protocol;
    protected THeaderTransport transport;

    public AbstractMetaClient(List<HostAddress> addresses, int timeout,
                              int connectionRetry, int executionRetry) throws UnknownHostException {
        Preconditions.checkArgument(timeout > 0);
        Preconditions.checkArgument(connectionRetry >= 0);
        Preconditions.checkArgument(executionRetry >= 0);
        for (HostAddress address : addresses) {
            String host = InetAddress.getByName(address.getHost()).getHostAddress();
            int port = address.getPort();
            // check if the address is a valid ip, uri address or domain name and port is valid
            if (!(InetAddresses.isInetAddress(host)
                        || InetAddresses.isUriInetAddress(host)
                        || InternetDomainName.isValid(host))
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

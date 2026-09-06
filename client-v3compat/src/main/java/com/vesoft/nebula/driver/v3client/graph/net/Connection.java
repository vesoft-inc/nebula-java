/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.v3client.graph.net;

import com.vesoft.nebula.driver.v3client.graph.data.HostAddress;
import com.vesoft.nebula.driver.v3client.graph.data.SSLParam;
import com.vesoft.nebula.driver.v3client.graph.exception.ClientServerIncompatibleException;
import com.vesoft.nebula.driver.v3client.graph.exception.IOErrorException;
import java.io.Serializable;
import java.util.Map;

/**
 * Abstract connection, matching the v3 client. The v5 driver manages connections internally via
 * {@code GrpcConnection}, so this type exists for source compatibility only.
 */
public abstract class Connection implements Serializable {

    private static final long serialVersionUID = -8425216612015802331L;

    protected HostAddress serverAddr = null;

    public HostAddress getServerAddress() {
        return this.serverAddr;
    }

    public abstract void open(HostAddress address, int timeout, SSLParam sslParam)
        throws IOErrorException, ClientServerIncompatibleException;

    public abstract void open(HostAddress address, int timeout,
                              SSLParam sslParam, boolean isUseHttp2, Map<String, String> headers)
        throws IOErrorException, ClientServerIncompatibleException;

    public abstract void open(HostAddress address, int timeout)
        throws IOErrorException, ClientServerIncompatibleException;

    public abstract void open(HostAddress address, int timeout,
                              boolean isUseHttp2, Map<String, String> headers)
        throws IOErrorException, ClientServerIncompatibleException;

    public abstract void reopen() throws IOErrorException, ClientServerIncompatibleException;

    public abstract void close();

    public abstract boolean ping();

    public abstract boolean ping(long sessionID);
}

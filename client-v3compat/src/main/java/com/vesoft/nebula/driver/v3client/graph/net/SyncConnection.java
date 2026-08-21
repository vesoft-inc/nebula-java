/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.v3client.graph.net;

import com.vesoft.nebula.driver.v3client.graph.data.HostAddress;
import com.vesoft.nebula.driver.v3client.graph.data.SSLParam;
import com.vesoft.nebula.driver.v3client.graph.exception.AuthFailedException;
import com.vesoft.nebula.driver.v3client.graph.exception.ClientServerIncompatibleException;
import com.vesoft.nebula.driver.v3client.graph.exception.IOErrorException;
import java.util.Map;

/**
 * Source-compatibility shim for the v3 client's {@code SyncConnection}.
 *
 * <p>The v5 driver talks gRPC and manages connections internally; applications should obtain
 * sessions through {@link NebulaPool#getSession}. Direct use of the connection-level API is not
 * supported and the authenticate/execute methods throw {@link UnsupportedOperationException}.
 */
public class SyncConnection extends Connection {

    private SSLParam sslParam = null;
    private int timeout = 0;
    private boolean useHttp2 = false;
    private Map<String, String> headers = null;
    private boolean opened = false;

    @Override
    public void open(HostAddress address, int timeout, SSLParam sslParam)
        throws IOErrorException, ClientServerIncompatibleException {
        open(address, timeout, sslParam, false, null);
    }

    @Override
    public void open(HostAddress address, int timeout, SSLParam sslParam, boolean isUseHttp2,
                     Map<String, String> headers)
        throws IOErrorException, ClientServerIncompatibleException {
        this.serverAddr = address;
        this.timeout = timeout;
        this.sslParam = sslParam;
        this.useHttp2 = isUseHttp2;
        this.headers = headers;
        this.opened = true;
    }

    @Override
    public void open(HostAddress address, int timeout)
        throws IOErrorException, ClientServerIncompatibleException {
        open(address, timeout, null, false, null);
    }

    @Override
    public void open(HostAddress address, int timeout, boolean isUseHttp2,
                     Map<String, String> headers)
        throws IOErrorException, ClientServerIncompatibleException {
        open(address, timeout, null, isUseHttp2, headers);
    }

    @Override
    public void reopen() throws IOErrorException, ClientServerIncompatibleException {
        close();
        if (serverAddr != null) {
            open(serverAddr, timeout, sslParam, useHttp2, headers);
        }
    }

    @Override
    public void close() {
        opened = false;
    }

    @Override
    public boolean ping() {
        return false;
    }

    @Override
    public boolean ping(long sessionID) {
        return false;
    }

    public AuthResult authenticate(String user, String password)
        throws AuthFailedException, IOErrorException, ClientServerIncompatibleException {
        throw new UnsupportedOperationException(
            "Direct connection authentication is not supported by the v5 driver; "
                + "use NebulaPool.getSession(user, password, reconnect).");
    }
}

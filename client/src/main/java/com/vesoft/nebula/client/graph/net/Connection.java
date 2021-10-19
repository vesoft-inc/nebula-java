package com.vesoft.nebula.client.graph.net;

import com.vesoft.nebula.client.graph.data.HostAddress;
import com.vesoft.nebula.client.graph.data.SSLParam;
import com.vesoft.nebula.client.graph.exception.ClientServerIncompatibleException;
import com.vesoft.nebula.client.graph.exception.IOErrorException;

public abstract class Connection {
    protected HostAddress serverAddr = null;

    public HostAddress getServerAddress() {
        return this.serverAddr;
    }

    public abstract void open(HostAddress address, int timeout, SSLParam sslParam)
            throws IOErrorException, ClientServerIncompatibleException;


    public abstract void open(HostAddress address, int timeout) throws IOErrorException,
            ClientServerIncompatibleException;

    public abstract void reopen() throws IOErrorException, ClientServerIncompatibleException;

    public abstract void close();

    public abstract boolean ping();
}

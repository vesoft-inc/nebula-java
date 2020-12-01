/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.graph.net;


import com.facebook.thrift.TException;
import com.facebook.thrift.protocol.TCompactProtocol;
import com.facebook.thrift.protocol.TProtocol;
import com.facebook.thrift.transport.TSocket;
import com.facebook.thrift.transport.TTransport;
import com.facebook.thrift.transport.TTransportException;
import com.vesoft.nebula.client.graph.data.HostAddress;
import com.vesoft.nebula.client.graph.exception.AuthFailedException;
import com.vesoft.nebula.client.graph.exception.IOErrorException;
import com.vesoft.nebula.graph.AuthResponse;
import com.vesoft.nebula.graph.ErrorCode;
import com.vesoft.nebula.graph.ExecutionResponse;
import com.vesoft.nebula.graph.GraphService;

public class SyncConnection extends Connection {
    protected TTransport transport = null;
    protected TProtocol protocol = null;
    private GraphService.Client client = null;

    @Override
    public void open(HostAddress address, int timeout) throws IOErrorException {
        this.serverAddr = address;
        try {
            int newTimeout = timeout <= 0 ? Integer.MAX_VALUE : timeout;
            this.transport = new TSocket(
                    address.getHost(), address.getPort(), newTimeout, newTimeout);
            this.transport.open();
            this.protocol = new TCompactProtocol(transport);
            client = new GraphService.Client(protocol);
        } catch (TException e) {
            throw new IOErrorException(IOErrorException.E_UNKNOWN, e.getMessage());
        }
    }

    public long authenticate(String user, String password)
            throws AuthFailedException, IOErrorException {
        try {
            AuthResponse resp = client.authenticate(user.getBytes(), password.getBytes());
            if (resp.error_code != ErrorCode.SUCCEEDED) {
                throw new AuthFailedException(new String(resp.error_msg).intern());
            }
            return resp.session_id;
        } catch (TException e) {
            if (e instanceof TTransportException) {
                TTransportException te = (TTransportException)e;
                if (te.getType() == TTransportException.END_OF_FILE) {
                    throw new IOErrorException(IOErrorException.E_CONNECT_BROKEN, te.getMessage());
                }
            }
            throw new AuthFailedException(String.format("Authenticate failed: %s", e.getMessage()));
        }
    }

    public ExecutionResponse execute(long sessionID, String stmt)
            throws IOErrorException {
        try {
            return client.execute(sessionID, stmt.getBytes());
        } catch (TException e) {
            if (e instanceof TTransportException) {
                TTransportException te = (TTransportException) e;
                if (te.getType() == TTransportException.END_OF_FILE) {
                    throw new IOErrorException(IOErrorException.E_CONNECT_BROKEN, te.getMessage());
                }
            }
            throw new IOErrorException(IOErrorException.E_UNKNOWN, e.getMessage());
        }
    }

    public void signout(long sessionId) {
        try {
            client.signout(sessionId);
        } catch (TException e) {
            this.close();
        }
    }

    @Override
    public boolean ping() {
        try {
            client.execute(0, "YIELD 1;".getBytes());
            return true;
        } catch (TException e) {
            if (e instanceof TTransportException) {
                TTransportException te = (TTransportException) e;
                return te.getType() != TTransportException.END_OF_FILE
                        && te.getType() != TTransportException.NOT_OPEN;
            }
            return true;
        }
    }

    public void close() {
        if (transport != null) {
            transport.close();
        }
    }

}

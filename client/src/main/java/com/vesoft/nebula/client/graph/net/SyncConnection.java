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
    private int timeout = 0;

    @Override
    public void open(HostAddress address, int timeout) throws IOErrorException {
        this.serverAddr = address;
        try {
            this.timeout  = timeout <= 0 ? Integer.MAX_VALUE : timeout;
            this.transport = new TSocket(
                    address.getHost(), address.getPort(), this.timeout, this.timeout);
            this.transport.open();
            this.protocol = new TCompactProtocol(transport);
            client = new GraphService.Client(protocol);
        } catch (TException e) {
            throw new IOErrorException(IOErrorException.E_UNKNOWN, e.getMessage());
        }
    }

    @Override
    public void reopen() throws IOErrorException {
        close();
        open(serverAddr, timeout);
    }

    public long authenticate(String user, String password)
            throws AuthFailedException, IOErrorException {
        try {
            AuthResponse resp = client.authenticate(user.getBytes(), password.getBytes());
            if (resp.error_code != ErrorCode.SUCCEEDED) {
                if (resp.error_msg != null) {
                    throw new AuthFailedException(new String(resp.error_msg));
                } else {
                    throw new AuthFailedException(
                        "The error_msg is null, "
                            + "maybe the service not set or the response is disorder.");
                }
            }
            return resp.session_id;
        } catch (TException e) {
            if (e instanceof TTransportException) {
                TTransportException te = (TTransportException)e;
                if (te.getType() == TTransportException.END_OF_FILE) {
                    throw new IOErrorException(IOErrorException.E_CONNECT_BROKEN, te.getMessage());
                } else if (te.getType() == TTransportException.TIMED_OUT
                    || te.getMessage().contains("Read timed out")) {
                    throw new IOErrorException(IOErrorException.E_TIME_OUT, te.getMessage());
                } else if (te.getType() == TTransportException.NOT_OPEN) {
                    throw new IOErrorException(IOErrorException.E_NO_OPEN, te.getMessage());
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
                } else if (te.getType() == TTransportException.NOT_OPEN) {
                    throw new IOErrorException(IOErrorException.E_NO_OPEN, te.getMessage());
                } else if (te.getType() == TTransportException.TIMED_OUT
                    || te.getMessage().contains("Read timed out")) {
                    throw new IOErrorException(IOErrorException.E_TIME_OUT, te.getMessage());
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
            execute(0, "YIELD 1;");
            return true;
        } catch (IOErrorException e) {
            return false;
        }
    }

    public void close() {
        if (transport != null) {
            transport.close();
        }
    }

}

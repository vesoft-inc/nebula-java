/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.graph;


import com.facebook.thrift.TException;
import com.facebook.thrift.protocol.TCompactProtocol;
import com.facebook.thrift.protocol.TProtocol;
import com.facebook.thrift.transport.TSocket;
import com.facebook.thrift.transport.TTransport;
import com.facebook.thrift.transport.TTransportException;
import com.google.common.net.HostAndPort;
import com.vesoft.nebula.graph.AuthResponse;
import com.vesoft.nebula.graph.ErrorCode;
import com.vesoft.nebula.graph.ExecutionResponse;
import com.vesoft.nebula.graph.GraphService;

public class Connection {
    private TTransport transport = null;
    private GraphService.Client connection = null;
    private HostAndPort address = null;
    private Boolean isUsed = false;

    public void open(HostAndPort address, int timeout) throws TException {
        try {
            this.address = address;
            this.transport = new TSocket(
                    address.getHostText(), address.getPort(), timeout, timeout);
            this.transport.open();
            TProtocol protocol = new TCompactProtocol(transport);
            this.connection = new GraphService.Client(protocol);
        } catch (TException e) {
            throw e;
        }
    }

    public long authenticate(String user, String password)
            throws AuthFailedException, IOErrorException {
        try {
            AuthResponse resp = connection.authenticate(user, password);
            if (resp.error_code != ErrorCode.SUCCEEDED) {
                throw new AuthFailedException(resp.error_msg);
            }
            setUsed();
            return resp.session_id;
        } catch (TException e) {
            if (e instanceof TTransportException) {
                TTransportException te = (TTransportException)e;
                if (te.getType() == TTransportException.END_OF_FILE) {
                    throw new IOErrorException(IOErrorException.E_CONNECT_BROKEN, te.getMessage());
                }
            }
        }
        throw new AuthFailedException("Not authenticate");
    }

    public ExecutionResponse execute(long sessionID, String stmt)
            throws IOErrorException, TException {
        try {
            return connection.execute(sessionID, stmt);
        } catch (TException e) {
            if (e instanceof TTransportException) {
                TTransportException te = (TTransportException) e;
                if (te.getType() == TTransportException.END_OF_FILE) {
                    throw new IOErrorException(IOErrorException.E_CONNECT_BROKEN, te.getMessage());
                }
            }
            throw e;
        }
    }

    public void signout(long sessionId) {
        try {
            connection.signout(sessionId);
            isUsed = false;
        } catch (TException e) {
            this.close();
        }
    }

    public void close() {
        if (transport != null) {
            transport.close();
            isUsed = false;
        }
    }

    public Boolean ping() {
        // TODO: need server supported
        return false;
    }

    public Boolean isUsed() {
        return isUsed;
    }

    public void setUsed() {
        isUsed = true;
    }
}

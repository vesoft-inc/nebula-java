/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.storage;

import com.facebook.thrift.TException;
import com.facebook.thrift.protocol.TCompactProtocol;
import com.facebook.thrift.protocol.TProtocol;
import com.facebook.thrift.transport.TSocket;
import com.facebook.thrift.transport.TTransport;
import com.vesoft.nebula.client.graph.data.HostAddress;
import com.vesoft.nebula.storage.GraphStorageService;
import com.vesoft.nebula.storage.ScanEdgeRequest;
import com.vesoft.nebula.storage.ScanEdgeResponse;
import com.vesoft.nebula.storage.ScanVertexRequest;
import com.vesoft.nebula.storage.ScanVertexResponse;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class GraphStorageConnection {
    protected TTransport transport = null;
    protected TProtocol protocol = null;
    public HostAddress address;
    private GraphStorageService.Client client;

    protected GraphStorageConnection() {
    }

    protected GraphStorageConnection open(HostAddress address, int timeout) throws Exception {
        this.address = address;
        try {
            int newTimeout = timeout <= 0 ? Integer.MAX_VALUE : timeout;
            this.transport = new TSocket(
                    InetAddress.getByName(address.getHost()).getHostAddress(),
                    address.getPort(),
                    newTimeout,
                    newTimeout);
            this.transport.open();
            this.protocol = new TCompactProtocol(transport);
            client = new GraphStorageService.Client(protocol);
        } catch (TException | UnknownHostException e) {
            throw e;
        }
        return this;
    }


    public ScanVertexResponse scanVertex(ScanVertexRequest request) throws TException {
        return client.scanVertex(request);
    }

    public ScanEdgeResponse scanEdge(ScanEdgeRequest request) throws TException {
        return client.scanEdge(request);
    }

    public void close() {
        if (transport != null && transport.isOpen()) {
            transport.close();
        }
    }

    public HostAddress getAddress() {
        return address;
    }

}

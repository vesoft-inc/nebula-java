/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.client.storage;

import com.facebook.thrift.TException;
import com.facebook.thrift.protocol.TCompactProtocol;
import com.facebook.thrift.protocol.THeaderProtocol;
import com.facebook.thrift.protocol.TProtocol;
import com.facebook.thrift.transport.THeaderTransport;
import com.facebook.thrift.transport.TSocket;
import com.facebook.thrift.transport.TTransport;
import com.facebook.thrift.transport.TTransportException;
import com.vesoft.nebula.client.graph.data.CASignedSSLParam;
import com.vesoft.nebula.client.graph.data.HostAddress;
import com.vesoft.nebula.client.graph.data.SSLParam;
import com.vesoft.nebula.client.graph.data.SelfSignedSSLParam;
import com.vesoft.nebula.client.graph.exception.IOErrorException;
import com.vesoft.nebula.storage.GraphStorageService;
import com.vesoft.nebula.storage.ScanEdgeRequest;
import com.vesoft.nebula.storage.ScanResponse;
import com.vesoft.nebula.storage.ScanVertexRequest;
import com.vesoft.nebula.util.SslUtil;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import javax.net.ssl.SSLSocketFactory;

public class GraphStorageConnection implements Serializable {

    private static final long serialVersionUID = -3631352515689239788L;

    protected THeaderTransport transport = null;
    protected THeaderProtocol protocol = null;
    public HostAddress address;
    private GraphStorageService.Client client;

    protected GraphStorageConnection() {
    }

    protected GraphStorageConnection open(HostAddress address, int timeout, boolean enableSSL,
                                          SSLParam sslParam) throws Exception {
        this.address = address;
        int newTimeout = timeout <= 0 ? Integer.MAX_VALUE : timeout;
        if (enableSSL) {
            SSLSocketFactory sslSocketFactory;
            if (sslParam.getSignMode() == SSLParam.SignMode.CA_SIGNED) {
                sslSocketFactory = SslUtil.getSSLSocketFactoryWithCA((CASignedSSLParam) sslParam);
            } else {
                sslSocketFactory =
                        SslUtil.getSSLSocketFactoryWithoutCA((SelfSignedSSLParam) sslParam);
            }
            try {
                transport =
                        new THeaderTransport(new TSocket(
                                sslSocketFactory.createSocket(
                                        InetAddress.getByName(address.getHost()).getHostAddress(),
                                        address.getPort()),
                                newTimeout,
                                newTimeout));
            } catch (IOException e) {
                throw new TTransportException(IOErrorException.E_UNKNOWN, e);
            }
        } else {
            this.transport = new THeaderTransport(new TSocket(
                    InetAddress.getByName(address.getHost()).getHostAddress(),
                    address.getPort(),
                    newTimeout,
                    newTimeout));
            this.transport.open();
        }
        this.protocol = new THeaderProtocol(transport);
        client = new GraphStorageService.Client(protocol);
        return this;
    }


    public ScanResponse scanVertex(ScanVertexRequest request) throws TException {
        return client.scanVertex(request);
    }

    public ScanResponse scanEdge(ScanEdgeRequest request) throws TException {
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

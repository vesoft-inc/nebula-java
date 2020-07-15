/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.graph;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.collect.Lists;
import com.google.common.net.HostAndPort;
import com.vesoft.nebula.graph.ErrorCode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * The factory class used to create GraphClientImpl
 */
public class GraphClientFactory extends BasePooledObjectFactory<GraphClientImpl> {
    private List<HostAndPort> addresses;
    private final ConnectionContext connCtx;
    private AtomicInteger addressPosition = new AtomicInteger(0);

    GraphClientFactory(ConnectionContext context) {
        this.connCtx = context;
        this.addresses = parseHosts(context.getHosts());
        addressPosition.set(new Random(System.currentTimeMillis()).nextInt(addresses.size()));
    }

    private List<HostAndPort> parseHosts(String hosts) {
        List<HostAndPort> addresses = new ArrayList<>();
        checkArgument(hosts != null);
        for (String host : hosts.split(",")) {
            String[] info = host.split(":");
            checkArgument(info.length == 2);
            addresses.add(HostAndPort.fromParts(info[0], Integer.parseInt(info[1])));
        }
        return addresses;
    }

    @Override
    public GraphClientImpl create() throws Exception {
        // use different host when do connect to graphd
        int position = addressPosition.getAndIncrement() % addresses.size();
        GraphClientImpl client = new GraphClientImpl(Lists.newArrayList(addresses.get(position)),
                connCtx.getTimeout(), connCtx.getConnectionRetry(), connCtx.getExecutionRetry());
        client.setUser(connCtx.getUser());
        client.setPassword(connCtx.getPassword());
        int code = client.connect();
        if (code != ErrorCode.SUCCEEDED) {
            throw new ConnectionException("connection to " + client.getAddress() + " faild.");
        }
        code = client.switchSpace(connCtx.getSpace());
        if (ErrorCode.SUCCEEDED != code) {
            throw new ConnectionException("switch to " + connCtx.getSpace() + " faild.");
        }
        return client;
    }

    @Override
    public void destroyObject(PooledObject<GraphClientImpl> p) throws Exception {
        p.getObject().close();
    }

    @Override
    public boolean validateObject(PooledObject<GraphClientImpl> p) {
        // if addresses don't include this, destroy it
        GraphClientImpl client = p.getObject();
        return addresses.contains(client.getAddress()) && client.isConnected();
    }

    @Override
    public PooledObject<GraphClientImpl> wrap(GraphClientImpl client) {
        return new DefaultPooledObject<GraphClientImpl>(client);
    }

    void setConnectionHosts(String hosts) {
        this.addresses = parseHosts(hosts);
    }
}

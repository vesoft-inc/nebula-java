/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.meta.client.async;

import static com.google.common.base.Preconditions.checkArgument;

import com.facebook.thrift.TException;
import com.facebook.thrift.async.TAsyncClientManager;
import com.facebook.thrift.protocol.TBinaryProtocol;
import com.facebook.thrift.protocol.TProtocolFactory;
import com.facebook.thrift.transport.*;
import com.google.common.collect.Lists;
import com.google.common.net.HostAndPort;
import com.google.common.net.InetAddresses;
import com.vesoft.nebula.meta.GetPartsAllocReq;
import com.vesoft.nebula.meta.ListEdgesReq;
import com.vesoft.nebula.meta.ListSpacesReq;
import com.vesoft.nebula.meta.ListTagsReq;
import com.vesoft.nebula.meta.MetaService;
import com.vesoft.nebula.meta.client.async.entry.GetPartsAllocCallback;
import com.vesoft.nebula.meta.client.async.entry.ListEdgesCallback;
import com.vesoft.nebula.meta.client.async.entry.ListSpaceCallback;
import com.vesoft.nebula.meta.client.async.entry.ListTagsCallback;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsyncMetaClientImpl implements AsyncMetaClient {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(AsyncMetaClientImpl.class.getName());

    private MetaService.AsyncClient client;

    private TNonblockingTransport transport = null;

    private TAsyncClientManager manager;


    private final List<HostAndPort> addresses;
    private final int connectionRetry;
    private final int timeout;

    public AsyncMetaClientImpl(List<HostAndPort> addresses, int timeout, int connectionRetry) {
        checkArgument(timeout > 0);
        checkArgument(connectionRetry > 0);
        if (addresses.isEmpty()) {
            throw new IllegalArgumentException("No meta server address is specified.");
        }

        addresses.forEach(address -> {
            String host = address.getHost();
            int port = address.getPort();
            if (!InetAddresses.isInetAddress(host) || (port <= 0 || port >= 65535)) {
                throw new IllegalArgumentException(String.format("%s:%d is not a valid address",
                    host, port));
            }
        });

        this.addresses = addresses;
        this.connectionRetry = connectionRetry;
        this.timeout = timeout;
        if (!connect()) {
            LOGGER.error("Connection Failed.");
        }
    }

    public AsyncMetaClientImpl(String host, int port) {
        this(Lists.newArrayList(HostAndPort.fromParts(host, port)),
            DEFAULT_TIMEOUT_MS, DEFAULT_CONNECTION_RETRY_SIZE);
    }

    public AsyncMetaClientImpl(List<HostAndPort> addresses) {
        this(addresses, DEFAULT_TIMEOUT_MS, DEFAULT_CONNECTION_RETRY_SIZE);
    }

    private boolean connect() {
        int retry = connectionRetry;
        while (retry-- != 0) {
            Random random = new Random(System.currentTimeMillis());
            int position = random.nextInt(addresses.size());
            HostAndPort address = addresses.get(position);
            try {
                manager = new TAsyncClientManager();
                transport = new TNonblockingSocket(address.getHost(), address.getPort(), timeout);
                TProtocolFactory protocol = new TBinaryProtocol.Factory();
                client = new MetaService.AsyncClient(protocol, manager, transport);
                return true;
            } catch (TTransportException tte) {
                LOGGER.error("Connect failed: " + tte.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public ListSpaceCallback listSpaces() {
        ListSpaceCallback callback = new ListSpaceCallback();
        try {
            client.listSpaces(new ListSpacesReq(), callback);
        } catch (TException e) {
            LOGGER.error(String.format("List Space Call Error: %s", e.getMessage()));
        }
        return callback;
    }

    @Override
    public GetPartsAllocCallback getPartsAlloc(int spaceId) {
        GetPartsAllocCallback callback = new GetPartsAllocCallback();
        GetPartsAllocReq req = new GetPartsAllocReq();
        req.setSpace_id(spaceId);
        try {
            client.getPartsAlloc(req, callback);
        } catch (TException e) {
            LOGGER.error(String.format("Get Parts Alloc Call Error: %s", e.getMessage()));
        }
        return callback;
    }

    @Override
    public ListTagsCallback listTags(int spaceId) {
        ListTagsCallback callback = new ListTagsCallback();
        ListTagsReq req = new ListTagsReq();
        req.setSpace_id(spaceId);
        try {
            client.listTags(req, callback);
        } catch (TException e) {
            LOGGER.error(String.format("List Tags Call Error: %s", e.getMessage()));
        }
        return callback;
    }

    @Override
    public ListEdgesCallback listEdges(int spaceId) {
        ListEdgesCallback callback = new ListEdgesCallback();
        ListEdgesReq req = new ListEdgesReq();
        req.setSpace_id(spaceId);
        try {
            client.listEdges(req, callback);
        } catch (TException e) {
            LOGGER.error(String.format("List Edges Call Error: %s", e.getMessage()));
        }
        return callback;
    }

    @Override
    public void close() throws Exception {
        transport.close();
        manager.stop();
    }
}

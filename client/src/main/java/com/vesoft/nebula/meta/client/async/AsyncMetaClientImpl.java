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
import com.facebook.thrift.transport.TNonblockingSocket;
import com.facebook.thrift.transport.TNonblockingTransport;
import com.facebook.thrift.transport.TTransportException;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.net.HostAndPort;
import com.google.common.net.InetAddresses;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import com.vesoft.nebula.meta.GetPartsAllocReq;
import com.vesoft.nebula.meta.GetPartsAllocResp;
import com.vesoft.nebula.meta.IdName;
import com.vesoft.nebula.meta.ListEdgesReq;
import com.vesoft.nebula.meta.ListEdgesResp;
import com.vesoft.nebula.meta.ListSpacesReq;
import com.vesoft.nebula.meta.ListSpacesResp;
import com.vesoft.nebula.meta.ListTagsReq;
import com.vesoft.nebula.meta.ListTagsResp;
import com.vesoft.nebula.meta.MetaService;
import com.vesoft.nebula.meta.client.MetaClient;
import com.vesoft.nebula.meta.client.MetaClientImpl;
import com.vesoft.nebula.meta.client.async.entry.GetPartsAllocCallback;
import com.vesoft.nebula.meta.client.async.entry.ListEdgesCallback;
import com.vesoft.nebula.meta.client.async.entry.ListSpaceCallback;
import com.vesoft.nebula.meta.client.async.entry.ListTagsCallback;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsyncMetaClientImpl implements AsyncMetaClient {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(AsyncMetaClientImpl.class.getName());

    private MetaService.AsyncClient client;

    private MetaClient metaClient;

    private TNonblockingTransport transport = null;

    private TAsyncClientManager manager;

    private ListeningExecutorService service;

    private final List<HostAndPort> addresses;
    private final int connectionRetry;
    private final int timeout;
    private List<IdName> spaces;
    private Map<String, Integer> spaceNames;

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

        service = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());

        this.spaces = Lists.newArrayList();
        this.spaceNames = Maps.newHashMap();
        this.addresses = addresses;
        this.connectionRetry = connectionRetry;
        this.timeout = timeout;

        this.metaClient = new MetaClientImpl(addresses, timeout, connectionRetry);
    }

    public AsyncMetaClientImpl(String host, int port) {
        this(Lists.newArrayList(HostAndPort.fromParts(host, port)),
            DEFAULT_TIMEOUT_MS, DEFAULT_CONNECTION_RETRY_SIZE);
    }

    public AsyncMetaClientImpl(List<HostAndPort> addresses) {
        this(addresses, DEFAULT_TIMEOUT_MS, DEFAULT_CONNECTION_RETRY_SIZE);
    }

    public void init() {
        if (!metaClient.connect()) {
            LOGGER.error("Connection has not been established. Connect Failed");
            return;
        }
        this.spaces = metaClient.listSpaces();
        for (IdName space : spaces) {
            int spaceId = space.getId().getSpace_id();
            spaceNames.put(space.getName(), spaceId);
        }
    }

    public boolean connect() {
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

    /**
     * List all spaces
     *
     * @return
     */
    @Override
    public ListenableFuture<Optional<ListSpacesResp>> listSpaces() {
        return service.submit(new Callable<Optional<ListSpacesResp>>() {
            @Override
            public Optional<ListSpacesResp> call() throws Exception {
                ListSpaceCallback callback = new ListSpaceCallback();
                try {
                    client.listSpaces(new ListSpacesReq(), callback);
                } catch (TException e) {
                    LOGGER.error(String.format("List Space Call Error: %s", e.getMessage()));
                }
                while (!callback.checkReady()) {
                    Thread.sleep(100);
                }
                if (callback.getResult().isPresent()) {
                    ListSpacesResp resp = (ListSpacesResp) callback.getResult().get();
                    return Optional.of(resp);
                } else {
                    return Optional.absent();
                }
            }
        });
    }

    /**
     * Get Parts Allocations
     *
     * @param spaceName space name
     * @return
     */
    @Override
    public ListenableFuture<Optional<GetPartsAllocResp>> getPartsAlloc(String spaceName) {
        if (!spaceNames.containsKey(spaceName)) {
            LOGGER.error("Space not found");
            return null;
        } else {
            return getPartsAlloc(spaceNames.get(spaceName));
        }
    }

    /**
     * Get Parts Allocations
     *
     * @param spaceId space ID
     * @return
     */
    @Override
    public ListenableFuture<Optional<GetPartsAllocResp>> getPartsAlloc(int spaceId) {
        return service.submit(new Callable<Optional<GetPartsAllocResp>>() {
            @Override
            public Optional<GetPartsAllocResp> call() throws Exception {
                GetPartsAllocCallback callback = new GetPartsAllocCallback();
                GetPartsAllocReq req = new GetPartsAllocReq();
                req.setSpace_id(spaceId);
                try {
                    client.getPartsAlloc(req, callback);
                } catch (TException e) {
                    LOGGER.error(String.format("Get Parts Alloc Call Error: %s", e.getMessage()));
                }
                while (!callback.checkReady()) {
                    Thread.sleep(100);
                }
                if (callback.getResult().isPresent()) {
                    GetPartsAllocResp resp = (GetPartsAllocResp) callback.getResult().get();
                    return Optional.of(resp);
                } else {
                    return Optional.absent();
                }
            }
        });
    }

    /**
     * List Tags
     *
     * @param spaceName space name
     * @return
     */
    @Override
    public ListenableFuture<Optional<ListTagsResp>> listTags(String spaceName) {
        if (!spaceNames.containsKey(spaceName)) {
            LOGGER.error("Space not found");
            return null;
        } else {
            return listTags(spaceNames.get(spaceName));
        }
    }

    /**
     * List Tags
     *
     * @param spaceId space ID
     * @return
     */
    @Override
    public ListenableFuture<Optional<ListTagsResp>> listTags(int spaceId) {
        return service.submit(new Callable<Optional<ListTagsResp>>() {
            @Override
            public Optional<ListTagsResp> call() throws Exception {
                ListTagsCallback callback = new ListTagsCallback();
                ListTagsReq req = new ListTagsReq();
                req.setSpace_id(spaceId);
                try {
                    client.listTags(req, callback);
                } catch (TException e) {
                    LOGGER.error(String.format("List Tags Call Error: %s", e.getMessage()));
                }
                while (!callback.checkReady()) {
                    Thread.sleep(100);
                }
                if (callback.getResult().isPresent()) {
                    ListTagsResp resp = (ListTagsResp) callback.getResult().get();
                    return Optional.of(resp);
                } else {
                    return Optional.absent();
                }
            }
        });
    }

    /**
     * List Edges
     *
     * @param spaceName space name
     * @return
     */
    @Override
    public ListenableFuture<Optional<ListEdgesResp>> listEdges(String spaceName) {
        if (!spaceNames.containsKey(spaceName)) {
            LOGGER.error("Space not found");
            return null;
        } else {
            return listEdges(spaceNames.get(spaceName));
        }
    }

    /**
     * List Edges
     *
     * @param spaceId space ID
     * @return
     */
    @Override
    public ListenableFuture<Optional<ListEdgesResp>> listEdges(int spaceId) {
        return service.submit(new Callable<Optional<ListEdgesResp>>() {
            @Override
            public Optional<ListEdgesResp> call() throws Exception {
                ListEdgesCallback callback = new ListEdgesCallback();
                ListEdgesReq req = new ListEdgesReq();
                req.setSpace_id(spaceId);
                try {
                    client.listEdges(req, callback);
                } catch (TException e) {
                    LOGGER.error(String.format("List Edges Call Error: %s", e.getMessage()));
                }
                while (!callback.checkReady()) {
                    Thread.sleep(100);
                }
                if (callback.getResult().isPresent()) {
                    ListEdgesResp resp = (ListEdgesResp) callback.getResult().get();
                    return Optional.of(resp);
                } else {
                    return Optional.absent();
                }
            }
        });
    }

    @Override
    public void close() throws Exception {
        service.shutdown();
        transport.close();
        manager.stop();
    }
}

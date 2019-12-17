/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.meta.async;

import com.facebook.thrift.TException;
import com.facebook.thrift.protocol.TBinaryProtocol;
import com.facebook.thrift.protocol.TProtocolFactory;
import com.facebook.thrift.transport.TNonblockingSocket;
import com.google.common.base.Optional;
import com.google.common.net.HostAndPort;
import com.google.common.util.concurrent.ListenableFuture;
import com.vesoft.nebula.client.meta.async.entry.GetPartsAllocCallback;
import com.vesoft.nebula.client.meta.async.entry.ListEdgesCallback;
import com.vesoft.nebula.client.meta.async.entry.ListSpaceCallback;
import com.vesoft.nebula.client.meta.async.entry.ListTagsCallback;
import com.vesoft.nebula.meta.GetPartsAllocReq;
import com.vesoft.nebula.meta.GetPartsAllocResp;
import com.vesoft.nebula.meta.ListEdgesReq;
import com.vesoft.nebula.meta.ListEdgesResp;
import com.vesoft.nebula.meta.ListSpacesReq;
import com.vesoft.nebula.meta.ListSpacesResp;
import com.vesoft.nebula.meta.ListTagsReq;
import com.vesoft.nebula.meta.ListTagsResp;
import com.vesoft.nebula.meta.MetaService;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsyncMetaClientImpl extends AsyncMetaClient {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(AsyncMetaClientImpl.class.getName());

    private MetaService.AsyncClient client;

    public AsyncMetaClientImpl(List<HostAndPort> addresses, int timeout,
                               int connectionRetry, int executionRetry) {
        super(addresses, timeout, connectionRetry, executionRetry);
    }

    public AsyncMetaClientImpl(List<HostAndPort> addresses) {
        super(addresses);
    }

    public AsyncMetaClientImpl(String host, int port) {
        super(host, port);
    }

    private Map<String, Integer> spaceNames;

    public void init() {
    }

    @Override
    public int doConnect(HostAndPort address) throws TException {
        try {
            transport = new TNonblockingSocket(address.getHostText(),
                    address.getPort(), timeout);
            TProtocolFactory protocol = new TBinaryProtocol.Factory();
            client = new MetaService.AsyncClient(protocol, manager, transport);
            return 0;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
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
    public ListenableFuture<Optional<GetPartsAllocResp>> getPartsAlloc(final int spaceId) {
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
    public ListenableFuture<Optional<ListTagsResp>> listTags(final int spaceId) {
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
    public ListenableFuture<Optional<ListEdgesResp>> listEdges(final int spaceId) {
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
}

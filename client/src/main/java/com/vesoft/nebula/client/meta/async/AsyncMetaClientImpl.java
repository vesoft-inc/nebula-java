/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.meta.async;

import com.facebook.thrift.TException;
import com.google.common.base.Optional;
import com.google.common.net.HostAndPort;
import com.google.common.util.concurrent.ListenableFuture;
import com.vesoft.nebula.AsyncAbstractClient;
import com.vesoft.nebula.client.meta.async.entry.ListEdgesCallback;
import com.vesoft.nebula.client.meta.async.entry.ListSpaceCallback;
import com.vesoft.nebula.client.meta.async.entry.ListTagsCallback;
import com.vesoft.nebula.meta.GetPartsAllocResp;
import com.vesoft.nebula.meta.ListEdgesReq;
import com.vesoft.nebula.meta.ListEdgesResp;
import com.vesoft.nebula.meta.ListSpacesReq;
import com.vesoft.nebula.meta.ListSpacesResp;
import com.vesoft.nebula.meta.ListTagsReq;
import com.vesoft.nebula.meta.ListTagsResp;
import com.vesoft.nebula.meta.MetaService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsyncMetaClientImpl extends AsyncAbstractClient implements AsyncMetaClient {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(AsyncMetaClientImpl.class.getName());

    private Map<String, Integer> spaceNameID = new HashMap<>();
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

    @Override
    public int doConnect(List<HostAndPort> address) throws TException {
        client = new MetaService.AsyncClient(protocolFactory, manager, transport);
        return 0;
    }

    public int getSpaceIDFromCache(String name) {
        if (!spaceNameID.containsKey(name)) {
            return -1;
        } else {
            return spaceNameID.get(name);
        }
    }

    /**
     * List all spaces
     *
     * @return
     */
    @Override
    public ListenableFuture<Optional<ListSpacesResp>> listSpaces() {
        return service.submit(() -> {
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
            return getPartsAlloc(spaceName);
        }
    }

    /**
     * List Tags
     *
     * @param spaceName nebula space name
     * @return
     */
    @Override
    public ListenableFuture<Optional<ListTagsResp>> listTags(String spaceName) {
        return service.submit(() -> {
            ListTagsCallback callback = new ListTagsCallback();
            ListTagsReq req = new ListTagsReq();
            req.setSpace_id(getSpaceIDFromCache(spaceName));
            try {
                client.listTags(req, callback);
            } catch (TException e) {
                LOGGER.error(String.format("List Tags Call Error: %s", e.getMessage()));
            }
            while (!callback.checkReady()) {
                Thread.sleep(1);
            }
            if (callback.getResult().isPresent()) {
                ListTagsResp resp = (ListTagsResp) callback.getResult().get();
                return Optional.of(resp);
            } else {
                return Optional.absent();
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
        return service.submit(() -> {
            ListEdgesCallback callback = new ListEdgesCallback();
            ListEdgesReq req = new ListEdgesReq();
            req.setSpace_id(getSpaceIDFromCache(spaceName));
            try {
                client.listEdges(req, callback);
            } catch (TException e) {
                LOGGER.error(String.format("List Edges Call Error: %s", e.getMessage()));
            }
            while (!callback.checkReady()) {
                Thread.sleep(1);
            }
            if (callback.getResult().isPresent()) {
                ListEdgesResp resp = (ListEdgesResp) callback.getResult().get();
                return Optional.of(resp);
            } else {
                return Optional.absent();
            }
        });
    }
}

/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.meta.async;

import com.google.common.base.Optional;
import com.google.common.net.HostAndPort;
import com.google.common.util.concurrent.ListenableFuture;
import com.vesoft.nebula.AsyncAbstractClient;
import com.vesoft.nebula.meta.GetPartsAllocResp;
import com.vesoft.nebula.meta.ListEdgesResp;
import com.vesoft.nebula.meta.ListSpacesResp;
import com.vesoft.nebula.meta.ListTagsResp;
import java.util.List;

public abstract class AsyncMetaClient extends AsyncAbstractClient {

    public AsyncMetaClient(List<HostAndPort> addresses, int timeout,
                           int connectionRetry, int executionRetry) {
        super(addresses, timeout, connectionRetry, executionRetry);
    }

    public AsyncMetaClient(List<HostAndPort> addresses) {
        super(addresses);
    }

    public AsyncMetaClient(String host, int port) {
        super(host, port);
    }

    /**
     * List all spaces
     *
     * @return callback ListSpaceCallback
     */
    public abstract ListenableFuture<Optional<ListSpacesResp>> listSpaces();

    /**
     * Get Parts Allocations
     *
     * @param spaceName space name
     * @return
     */
    public abstract ListenableFuture<Optional<GetPartsAllocResp>> getPartsAlloc(String spaceName);

    /**
     * Get Parts Allocations
     *
     * @param spaceId space ID
     * @return
     */
    public abstract ListenableFuture<Optional<GetPartsAllocResp>> getPartsAlloc(int spaceId);

    /**
     * List Tags
     *
     * @param spaceName space name
     * @return
     */
    public abstract ListenableFuture<Optional<ListTagsResp>> listTags(String spaceName);

    /**
     * List Tags
     *
     * @param spaceId space ID
     * @return
     */
    public abstract ListenableFuture<Optional<ListTagsResp>> listTags(int spaceId);

    /**
     * List Edges
     *
     * @param spaceName space name
     * @return
     */
    public abstract ListenableFuture<Optional<ListEdgesResp>> listEdges(String spaceName);

    /**
     * List Edges
     *
     * @param spaceId space ID
     * @return
     */
    public abstract ListenableFuture<Optional<ListEdgesResp>> listEdges(int spaceId);
}

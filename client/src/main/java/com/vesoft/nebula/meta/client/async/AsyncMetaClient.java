/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.meta.client.async;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import com.vesoft.nebula.Client;
import com.vesoft.nebula.meta.GetPartsAllocResp;
import com.vesoft.nebula.meta.ListEdgesResp;
import com.vesoft.nebula.meta.ListSpacesResp;
import com.vesoft.nebula.meta.ListTagsResp;

public interface AsyncMetaClient extends Client {

    /**
     * connect
     *
     * @return boolean connection
     */
    public boolean connect();

    /**
     * Initialize Async Meta client
     */
    public void init();

    /**
     * List all spaces
     *
     * @return callback ListSpaceCallback
     */
    public ListenableFuture<Optional<ListSpacesResp>> listSpaces();

    /**
     * Get Parts Allocations
     *
     * @param spaceName space name
     * @return
     */
    public ListenableFuture<Optional<GetPartsAllocResp>> getPartsAlloc(String spaceName);

    /**
     * Get Parts Allocations
     *
     * @param spaceId space ID
     * @return
     */
    public ListenableFuture<Optional<GetPartsAllocResp>> getPartsAlloc(int spaceId);

    /**
     * List Tags
     *
     * @param spaceName space name
     * @return
     */
    public ListenableFuture<Optional<ListTagsResp>> listTags(String spaceName);

    /**
     * List Tags
     *
     * @param spaceId space ID
     * @return
     */
    public ListenableFuture<Optional<ListTagsResp>> listTags(int spaceId);

    /**
     * List Edges
     *
     * @param spaceName space name
     * @return
     */
    public ListenableFuture<Optional<ListEdgesResp>> listEdges(String spaceName);

    /**
     * List Edges
     *
     * @param spaceId space ID
     * @return
     */
    public ListenableFuture<Optional<ListEdgesResp>> listEdges(int spaceId);
}

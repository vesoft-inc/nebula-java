/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.meta.async;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import com.vesoft.nebula.meta.GetPartsAllocResp;
import com.vesoft.nebula.meta.ListEdgesResp;
import com.vesoft.nebula.meta.ListSpacesResp;
import com.vesoft.nebula.meta.ListTagsResp;

/**
 * Async Meta Client Interface
 */
public interface AsyncMetaClient {


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
     * List Tags
     *
     * @param spaceName space name
     * @return
     */
    public abstract ListenableFuture<Optional<ListTagsResp>> listTags(String spaceName);

    /**
     * List Edges
     *
     * @param spaceName space name
     * @return
     */
    public abstract ListenableFuture<Optional<ListEdgesResp>> listEdges(String spaceName);

}

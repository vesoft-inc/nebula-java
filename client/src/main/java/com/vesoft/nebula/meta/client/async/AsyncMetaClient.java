/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.meta.client.async;

import com.vesoft.nebula.Client;
import com.vesoft.nebula.meta.client.async.entry.GetPartsAllocCallback;
import com.vesoft.nebula.meta.client.async.entry.ListEdgesCallback;
import com.vesoft.nebula.meta.client.async.entry.ListSpaceCallback;
import com.vesoft.nebula.meta.client.async.entry.ListTagsCallback;

public interface AsyncMetaClient extends Client {

    /**
     * List all spaces
     *
     * @return callback ListSpaceCallback
     */
    public ListSpaceCallback listSpaces();

    /**
     * Get Parts Allocations
     *
     * @param spaceId space ID
     * @return
     */
    public GetPartsAllocCallback getPartsAlloc(int spaceId);

    /**
     * List Tags
     *
     * @param spaceId space ID
     * @return
     */
    public ListTagsCallback listTags(int spaceId);

    /**
     * List Edges
     *
     * @param spaceId space ID
     * @return
     */
    public ListEdgesCallback listEdges(int spaceId);

}

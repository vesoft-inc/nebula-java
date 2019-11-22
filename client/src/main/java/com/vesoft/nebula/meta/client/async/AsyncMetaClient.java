/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.meta.client.async;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import com.vesoft.nebula.Client;
import com.vesoft.nebula.meta.client.entry.GetPartsAllocResult;
import com.vesoft.nebula.meta.client.entry.ListEdgesResult;
import com.vesoft.nebula.meta.client.entry.ListSpaceResult;
import com.vesoft.nebula.meta.client.entry.ListTagsResult;

public interface AsyncMetaClient extends Client {

    /**
     * ListSpaceResult contains a map using spaceIds as keys and spaceNames as values
     *
     * @return
     */
    public ListenableFuture<Optional<ListSpaceResult>> listSpaces();

    /**
     * GetPartsAllocResult contains a map using partIds as keys and lists of
     * corresponding addresses as values
     *
     * @param spaceId space id
     * @return
     */
    public ListenableFuture<Optional<GetPartsAllocResult>> getPartsAlloc(int spaceId);

    /**
     * ListTagsResult contains a map using tagNames as keys and tagItems as values
     *
     * @param spaceId space id
     * @return
     */
    public ListenableFuture<Optional<ListTagsResult>> listTags(int spaceId);

    /**
     * ListEdgesResult contains a map using edgeNames as keys and edgeItems as values
     *
     * @param spaceId space id
     * @return
     */
    public ListenableFuture<Optional<ListEdgesResult>> listEdges(int spaceId);
}

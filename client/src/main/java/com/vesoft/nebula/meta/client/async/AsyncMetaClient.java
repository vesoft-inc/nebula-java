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

    public ListSpaceCallback listSpaces();

    public GetPartsAllocCallback getPartsAlloc(int spaceId);

    public ListTagsCallback listTags(int spaceId);

    public ListEdgesCallback listEdges(int spaceId);

}

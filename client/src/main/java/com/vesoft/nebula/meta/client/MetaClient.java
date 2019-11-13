/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.meta.client;

import com.vesoft.nebula.HostAddr;

import java.util.List;

public interface MetaClient extends AutoCloseable {

    public static final int DEFAULT_TIMEOUT_MS = 1000;
    public static final int DEFAULT_CONNECTION_RETRY_SIZE = 3;

    public void init();

    public boolean connect();

    public List<HostAddr> getPart(int spaceId, int partId);

    public Integer getTagId(int spaceId, String tagName);

    public Integer getEdgeType(int spaceId, String edgeName);

    public boolean getParts(int spaceId);

    public boolean getTagItems(int spaceId);

    public boolean getEdgeTypes(int spaceId);

    public boolean listSpaces();

}


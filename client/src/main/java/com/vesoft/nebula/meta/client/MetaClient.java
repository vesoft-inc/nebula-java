/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.meta.client;

import com.vesoft.nebula.Client;
import com.vesoft.nebula.HostAddr;

import java.util.List;
import java.util.Map;

public interface MetaClient extends Client {

    public boolean listSpaces();

    public List<HostAddr> getPart(int spaceId, int partId);

    public List<HostAddr> getPart(String spaceName, int partId);

    public Map<Integer, Map<Integer, List<HostAddr>>> getParts();

    public Integer getTagId(int spaceId, String tagName);

    public Integer getTagId(String spaceName, String tagName);

    public Integer getEdgeType(int spaceId, String edgeName);

    public Integer getEdgeType(String spaceName, String edgeName);
}


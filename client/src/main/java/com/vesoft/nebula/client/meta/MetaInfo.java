/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.meta;

import com.google.common.collect.Maps;
import com.google.common.net.HostAndPort;
import com.vesoft.nebula.meta.EdgeItem;
import com.vesoft.nebula.meta.TagItem;
import java.util.List;
import java.util.Map;

public class MetaInfo {

    private final Map<String, Integer> spaceNameMap = Maps.newHashMap();
    private final Map<String, Map<Integer, List<HostAndPort>>>
            spacePartLocation = Maps.newHashMap();
    private final Map<String, Map<Long, TagItem>> spaceTagItems = Maps.newHashMap();
    private final Map<String, Map<Long, EdgeItem>> spaceEdgeItems = Maps.newHashMap();
    private final Map<String, Map<String, Long>> tagNameMap = Maps.newHashMap();
    private final Map<String, Map<Long, String>> tagIdMap = Maps.newHashMap();
    private final Map<String, Map<String, Long>> edgeNameMap = Maps.newHashMap();
    private final Map<String, Map<Long, String>> edgeIdMap = Maps.newHashMap();
    private final Map<String, Map<Integer, HostAndPort>> leaders = Maps.newHashMap();

    public Map<String, Integer> getSpaceNameMap() {
        return spaceNameMap;
    }

    public Map<String, Map<Integer, List<HostAndPort>>> getSpacePartLocation() {
        return spacePartLocation;
    }

    public Map<String, Map<Long, TagItem>> getSpaceTagItems() {
        return spaceTagItems;
    }

    public Map<String, Map<Long, EdgeItem>> getSpaceEdgeItems() {
        return spaceEdgeItems;
    }

    public Map<String, Map<String, Long>> getTagNameMap() {
        return tagNameMap;
    }

    public Map<String, Map<String, Long>> getEdgeNameMap() {
        return edgeNameMap;
    }

    public Map<String, Map<Long, String>> getEdgeIdMap() {
        return edgeIdMap;
    }

    public Map<String, Map<Long, String>> getTagIdMap() {
        return tagIdMap;
    }

    public Map<String, Map<Integer, HostAndPort>> getLeaders() {
        return leaders;
    }
}

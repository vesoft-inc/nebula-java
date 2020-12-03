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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * two spaces: test1, test2, both have 2 parts
 * each space has one tag and one edge
 */
public class MockMetaInfo {

    private static final String addr = "127.0.0.1:45500";
    protected static final int tagId = 111;
    protected static final int edgeId = 222;


    public static MetaInfo createMetaInfo() {
        MetaInfo metaInfo = new MetaInfo();
        mockSpaceName(metaInfo);
        mockSpacePartLocation(metaInfo);
        mockSpaceTagItems(metaInfo);
        mockSpaceEdgeItems(metaInfo);
        mockTagName(metaInfo);
        mockEdgeName(metaInfo);
        mockTagId(metaInfo);
        mockEdgeId(metaInfo);
        mockLeader(metaInfo);
        return metaInfo;
    }


    public static void mockSpaceName(MetaInfo metaInfo) {
        Map<String, Integer> spaceNameMap = metaInfo.getSpaceNameMap();
        spaceNameMap.put("test1", 1);
        spaceNameMap.put("test2", 2);
    }

    public static void mockSpacePartLocation(MetaInfo metaInfo) {
        Map<String, Map<Integer, List<HostAndPort>>> spacePartLocation =
                metaInfo.getSpacePartLocation();
        Map<Integer, List<HostAndPort>> parts = Maps.newHashMap();
        parts.put(1, Arrays.asList(HostAndPort.fromString(addr)));
        parts.put(2, Arrays.asList(HostAndPort.fromString(addr)));
        spacePartLocation.put("test1", parts);
        spacePartLocation.put("test2", parts);
    }

    public static void mockSpaceTagItems(MetaInfo metaInfo) {
        Map<String, Map<Long, TagItem>> spaceTagItems = metaInfo.getSpaceTagItems();
        TagItem tagItem = new TagItem(tagId, "tag".getBytes(), 1, null);
        Map<Long, TagItem> tag = Maps.newHashMap();
        tag.put((long)tagId, tagItem);
        spaceTagItems.put("test1", tag);
        spaceTagItems.put("test2", tag);
    }

    public static void mockSpaceEdgeItems(MetaInfo metaInfo) {
        Map<String, Map<Long, EdgeItem>> spaceEdgeItems = metaInfo.getSpaceEdgeItems();
        EdgeItem edgeItem = new EdgeItem(edgeId, "edge".getBytes(), 1, null);
        Map<Long, EdgeItem> edge = Maps.newHashMap();
        edge.put((long)edgeId, edgeItem);
        spaceEdgeItems.put("test1", edge);
        spaceEdgeItems.put("test2", edge);
    }

    public static void mockTagName(MetaInfo metaInfo) {
        Map<String, Map<String, Long>> tagNameMap = metaInfo.getTagNameMap();
        Map<String, Long> tagName = Maps.newHashMap();
        tagName.put("tag", (long)tagId);
        tagNameMap.put("test1", tagName);
        tagNameMap.put("test2", tagName);
    }

    public static void mockEdgeName(MetaInfo metaInfo) {
        Map<String, Map<String, Long>> edgeNameMap = metaInfo.getEdgeNameMap();
        Map<String, Long> edgeName = Maps.newHashMap();
        edgeName.put("edge", (long)edgeId);
        edgeNameMap.put("test1", edgeName);
        edgeNameMap.put("test2", edgeName);
    }

    public static void mockTagId(MetaInfo metaInfo) {
        Map<String, Map<Long, String>> tagIdMap = metaInfo.getTagIdMap();
        Map<Long, String> tagIds = Maps.newHashMap();
        tagIds.put((long)tagId, "tag");
        tagIdMap.put("test1", tagIds);
        tagIdMap.put("test2", tagIds);
    }

    public static void mockEdgeId(MetaInfo metaInfo) {
        Map<String, Map<Long, String>> edgeIdMap = metaInfo.getEdgeIdMap();
        Map<Long, String> edgeIds = Maps.newHashMap();
        edgeIds.put((long)edgeId, "edge");
        edgeIdMap.put("test1", edgeIds);
        edgeIdMap.put("test2", edgeIds);
    }


    public static void mockLeader(MetaInfo metaInfo) {
        Map<String, Map<Integer, HostAndPort>> leaders = metaInfo.getLeaders();
        Map<Integer, HostAndPort> partLeader = Maps.newHashMap();
        partLeader.put(1, HostAndPort.fromString("127.0.0.1:45500"));
        partLeader.put(2, HostAndPort.fromString("127.0.0.1:45500"));
        leaders.put("test1", partLeader);
        leaders.put("test2", partLeader);
    }
}

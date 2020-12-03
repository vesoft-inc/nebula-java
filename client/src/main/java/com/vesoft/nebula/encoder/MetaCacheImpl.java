/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.encoder;

import com.vesoft.nebula.HostAddr;
import com.vesoft.nebula.meta.EdgeItem;
import com.vesoft.nebula.meta.IdName;
import com.vesoft.nebula.meta.SpaceItem;
import com.vesoft.nebula.meta.TagItem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetaCacheImpl implements MetaCache {
    private class SpaceInfo {
        private SpaceItem spaceItem;
        private Map<String, TagItem> tagItems;
        private Map<String, EdgeItem> edgeItems;

        SpaceInfo(SpaceItem spaceItem,
                  Map<String, TagItem> tagItems,
                  Map<String, EdgeItem> edgeItems) {
            this.spaceItem = spaceItem;
            this.edgeItems = edgeItems;
            this.tagItems = tagItems;
        }
    }

    private Map<String, SpaceInfo> spaceInfos = new HashMap<>();

    public MetaCacheImpl(List<HostAddr> metaAddrs) {
        // TODO: create meta client
    }

    @Override
    public SpaceItem getSpace(String spaceName) {
        if (!spaceInfos.containsKey(spaceName)) {
            return null;
        }
        return spaceInfos.get(spaceName).spaceItem;
    }

    @Override
    public List<IdName> listSpaces() {
        return null;
    }


    @Override
    public TagItem getTag(String spaceName, String tagName) {
        if (!spaceInfos.containsKey(spaceName)) {
            return null;
        }
        SpaceInfo spaceInfo = spaceInfos.get(spaceName);
        if (!spaceInfo.tagItems.containsKey(tagName)) {
            return null;
        }
        return spaceInfo.tagItems.get(tagName);
    }

    @Override
    public List<TagItem> listTags(String spaceName) {
        if (!spaceInfos.containsKey(spaceName)) {
            return new ArrayList<>();
        }
        return new ArrayList<>(spaceInfos.get(spaceName).tagItems.values());
    }

    @Override
    public EdgeItem getEdge(String spaceName, String edgeName) {
        if (!spaceInfos.containsKey(spaceName)) {
            return null;
        }
        SpaceInfo spaceInfo = spaceInfos.get(spaceName);
        if (!spaceInfo.edgeItems.containsKey(edgeName)) {
            return null;
        }
        return spaceInfo.edgeItems.get(edgeName);
    }

    @Override
    public List<EdgeItem> listEdges(String spaceName) {
        if (!spaceInfos.containsKey(spaceName)) {
            return new ArrayList<>();
        }
        return new ArrayList<>(spaceInfos.get(spaceName).edgeItems.values());
    }

    @Override
    public Map<Integer, List<HostAddr>> getPartsAlloc(String spaceName) {
        return null;
    }
}

/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.meta;

import com.google.common.net.HostAndPort;
import com.vesoft.nebula.Schema;
import com.vesoft.nebula.client.meta.entry.SpaceNameID;
import com.vesoft.nebula.meta.EdgeItem;
import com.vesoft.nebula.meta.TagItem;
import java.util.List;
import java.util.Map;

public interface MetaClient extends AutoCloseable {

    public static final int LATEST_SCHEMA_VERSION = -1;

    public int getSpaceIdFromCache(String spaceName);

    public List<SpaceNameID> listSpaces();

    public Map<Integer, List<HostAndPort>> getPartsAlloc(String spaceName);

    public Map<String, Map<Integer, List<HostAndPort>>> getPartsAllocFromCache();

    public List<HostAndPort> getPartAllocFromCache(String spaceName, int part);

    public Schema getTag(String spaceName, String tagName);

    public Schema getEdge(String spaceName, String edgeName);

    public List<TagItem> getTags(String spaceName);

    public TagItem getTagItemFromCache(String spaceName, String tagName);

    public List<EdgeItem> getEdges(String spaceName);

    public EdgeItem getEdgeItemFromCache(String spaceName, String edgeName);

    /**
     * Get tag schema with specified version.
     *
     * @param spaceName the space's name
     * @param tagName   the tag's name
     * @param version   the tag's version
     * @return
     */
    public Map<String, Class> getTagSchema(String spaceName,
                                           String tagName,
                                           long version);

    /**
     * Get edge schema with specified version.
     *
     * @param spaceName the space's name
     * @param edgeName  the edge's name
     * @param version   the edge's version
     * @return
     */
    public Map<String, Class> getEdgeSchema(String spaceName,
                                            String edgeName,
                                            long version);

}


/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.meta;

import com.facebook.thrift.TException;
import com.google.common.collect.Maps;
import com.vesoft.nebula.client.graph.data.HostAddress;
import com.vesoft.nebula.client.meta.exception.ExecuteFailedException;
import com.vesoft.nebula.meta.EdgeItem;
import com.vesoft.nebula.meta.IdName;
import com.vesoft.nebula.meta.TagItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MetaManager is a manager for meta info, such as spaces,tags and edges.
 * MetaManager is composed of a cache named {@link MetaInfo} and real metaClient.
 * How to use:
 * MetaManager manager = MetaManager.getMetaManager(Arrays.asList(HostAndPort.fromString(addr)));
 * manager.init();
 */
public class MetaManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(MetaManager.class);

    private static MetaClient metaClient;
    private static MetaManager metaManager;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();

    private MetaInfo metaInfo = new MetaInfo();

    private MetaManager() {
    }

    /**
     * only way to get a MetaManager object
     */
    public static MetaManager getMetaManager(List<HostAddress> address) throws TException {
        if (metaManager == null) {
            synchronized (MetaManager.class) {
                if (metaManager == null) {
                    metaManager = new MetaManager();
                    metaManager.getClient(address);
                }
            }
        }
        return metaManager;
    }

    private void getClient(List<HostAddress> address) throws TException {
        if (metaClient == null) {
            synchronized (this) {
                if (metaClient == null) {
                    metaClient = new MetaClient(address);
                    metaClient.connect();
                }
            }
        }
    }

    /**
     * init the meta info cache
     * make sure this method is called before use metaManager
     */
    public void init() {
        fillMetaInfo();
    }

    /**
     * close meta client
     */
    public void close() {
        metaClient.close();
    }


    /**
     * fill the meta info
     */
    private synchronized void fillMetaInfo() {
        for (IdName space : metaClient.listSpaces()) {
            // space schema
            String spaceName = new String(space.getName());
            metaInfo.getSpaceNameMap().put(spaceName, space.getId().getSpace_id());
            metaInfo.getSpacePartLocation().put(spaceName, metaClient.getPartsLocation(spaceName));

            // tag schema
            freshTags(spaceName);

            // edge schema
            freshEdges(spaceName);
        }
    }


    /**
     * get space id
     *
     * @param spaceName nebula space name
     * @return
     */
    public int getSpaceId(String spaceName) {
        if (!metaInfo.getSpaceNameMap().containsKey(spaceName)) {
            fillMetaInfo();
        }
        if (!metaInfo.getSpaceNameMap().containsKey(spaceName)) {
            throw new IllegalArgumentException("space does not exist");
        }
        return metaInfo.getSpaceNameMap().get(spaceName);
    }


    /**
     * get tag id
     *
     * @param spaceName nebula graph space name
     * @param tagName   nebula tag name
     * @return long
     */
    private long getTagId(String spaceName, String tagName) {
        if (!metaInfo.getTagNameMap().containsKey(spaceName)
                || !metaInfo.getTagNameMap().get(spaceName).containsKey(tagName)) {
            fillMetaInfo();
        }
        Map<String, Map<String, Long>> tagNameMap = metaInfo.getTagNameMap();
        if (!tagNameMap.containsKey(spaceName)) {
            throw new IllegalArgumentException("space does not exist.");
        }

        if (!tagNameMap.get(spaceName).containsKey(tagName)) {
            throw new IllegalArgumentException(String.format("tag %s does not exist in space %s",
                    tagName, spaceName));
        }
        return tagNameMap.get(spaceName).get(tagName);
    }

    /**
     * get Tag
     *
     * @param spaceName nebula space name
     * @param tagName   nebula tag name
     * @return
     */
    public TagItem getTag(String spaceName, String tagName) {
        long tagId = getTagId(spaceName, tagName);
        return metaInfo.getSpaceTagItems().get(spaceName).get(tagId);
    }


    /**
     * get edge id
     *
     * @param spaceName nebula graph space name
     * @param edgeName  nebula edge name
     * @return long
     */
    private long getEdgeId(String spaceName, String edgeName) {
        if (!metaInfo.getTagNameMap().containsKey(spaceName)
                || !metaInfo.getTagNameMap().get(spaceName).containsKey(edgeName)) {
            fillMetaInfo();
        }

        Map<String, Map<String, Long>> edgeNameMap = metaInfo.getEdgeNameMap();
        if (!edgeNameMap.containsKey(spaceName)) {
            throw new IllegalArgumentException("space does not exist.");
        }

        if (!edgeNameMap.get(spaceName).containsKey(edgeName)) {
            throw new IllegalArgumentException(String.format("edge %s does not exist in space %s",
                    edgeName, spaceName));
        }
        return edgeNameMap.get(spaceName).get(edgeName);
    }

    /**
     * get Edge
     *
     * @param spaceName nebula graph space name
     * @param edgeName  nebula edge name
     * @return
     */
    public EdgeItem getEdge(String spaceName, String edgeName) {
        long edgeId = getEdgeId(spaceName, edgeName);
        return metaInfo.getSpaceEdgeItems().get(spaceName).get(edgeId);
    }

    public String getTagName(String spaceName, long tagId) {
        if (!metaInfo.getSpaceTagItems().containsKey(spaceName)
                || !metaInfo.getSpaceTagItems().get(spaceName).containsKey(tagId)) {
            fillMetaInfo();
        }

        Map<String, Map<Long, TagItem>> tagItems = metaInfo.getSpaceTagItems();
        if (!tagItems.containsKey(spaceName)) {
            throw new IllegalArgumentException("space does not exist.");
        }
        if (!tagItems.get(spaceName).containsKey(tagId)) {
            throw new IllegalArgumentException(String.format("tag %s does not exist in space %s",
                    tagId, spaceName));
        }
        return new String(tagItems.get(spaceName).get(tagId).getTag_name());
    }

    /**
     * convert edgeID to edge name
     *
     * @param spaceName nebula graph space name
     * @param edgeId    nebula edge id
     * @return
     */
    public String getEdgeName(String spaceName, long edgeId) {
        if (!metaInfo.getSpaceEdgeItems().containsKey(spaceName)
                || !metaInfo.getSpaceEdgeItems().get(spaceName).containsKey(edgeId)) {
            fillMetaInfo();
        }
        Map<String, Map<Long, EdgeItem>> edgeItems = metaInfo.getSpaceEdgeItems();
        if (!edgeItems.containsKey(spaceName)) {
            throw new IllegalArgumentException("space does not exist.");
        }
        if (!edgeItems.get(spaceName).containsKey(edgeId)) {
            throw new IllegalArgumentException(String.format("edge %s does not exist in space %s",
                    edgeId, spaceName));
        }
        return new String(edgeItems.get(spaceName).get(edgeId).getEdge_name());
    }


    /**
     * get part leader
     *
     * @param spaceName nebula graph space name
     * @param part      nebula part id
     * @return leader
     */
    public HostAddress getLeader(String spaceName, int part) {

        if (!metaInfo.getSpacePartLocation().containsKey(spaceName)
                || !metaInfo.getSpacePartLocation().containsKey(part)) {
            fillMetaInfo();
        }

        if (!metaInfo.getSpacePartLocation().containsKey(spaceName)) {
            throw new IllegalArgumentException("space does not exist.");
        }
        if (!metaInfo.getSpacePartLocation().get(spaceName).containsKey(part)) {
            throw new IllegalArgumentException(
                    String.format("part %d does not exist in space %s.", part, spaceName));
        }

        Map<String, Map<Integer, HostAddress>> leaders = metaInfo.getLeaders();

        if (!leaders.containsKey(spaceName)) {
            writeLock.lock();
            try {
                leaders.put(spaceName, Maps.newConcurrentMap());
            } finally {
                writeLock.unlock();
            }
        }

        if (leaders.get(spaceName).containsKey(part)) {
            HostAddress leader = null;
            if (leaders.get(spaceName).containsKey(part)) {
                leader = leaders.get(spaceName).get(part);
            }
            return leader;
        }
        Map<String, Map<Integer, List<HostAddress>>> spacePartLocations =
                metaInfo.getSpacePartLocation();

        if (spacePartLocations.containsKey(spaceName)
                && spacePartLocations.get(spaceName).containsKey(part)) {
            List<HostAddress> addresses = metaInfo.getSpacePartLocation().get(spaceName).get(part);

            if (addresses != null) {
                Random random = new Random(System.currentTimeMillis());
                int position = random.nextInt(addresses.size());
                HostAddress leader = addresses.get(position);
                Map<Integer, HostAddress> partLeader = leaders.get(spaceName);

                writeLock.lock();
                try {
                    partLeader.put(part, leader);
                } finally {
                    writeLock.unlock();
                }
                return leader;
            }
        }
        return null;
    }


    /**
     * get all parts of one space
     *
     * @param spaceName nebula graph space name
     * @return List
     */
    public List<Integer> getSpaceParts(String spaceName) {
        if (!metaInfo.getSpacePartLocation().containsKey(spaceName)) {
            fillMetaInfo();
        }
        Map<String, Map<Integer, List<HostAddress>>> spacePartLocations =
                metaInfo.getSpacePartLocation();
        if (!spacePartLocations.containsKey(spaceName)) {
            throw new IllegalArgumentException("space does not exist.");
        }

        Set<Integer> spaceParts = metaInfo.getSpacePartLocation().get(spaceName).keySet();
        return new ArrayList<>(spaceParts);
    }


    /**
     * cache new leader for part
     *
     * @param spaceName nebula graph space
     * @param part      nebula part
     * @param newLeader nebula part new leader
     */
    public void freshLeader(String spaceName, int part, HostAddress newLeader) throws TException {
        if (!metaInfo.getLeaders().containsKey(spaceName)) {
            getLeader(spaceName, part);
        }
        Map<Integer, HostAddress> partLeader = metaInfo.getLeaders().get(spaceName);
        writeLock.lock();
        try {
            partLeader.put(part, newLeader);
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * get all servers
     */
    public Set<HostAddress> listHosts() {
        return metaClient.listHosts();
    }

    public int getConnectionRetry() {
        return metaClient.getConnectionRetry();
    }

    public int getTimeout() {
        return metaClient.getTimeout();
    }

    public int getExecutionRetry() {
        return metaClient.getExecutionRetry();
    }

    /**
     * fresh tags in meta info
     */
    private void freshTags(String spaceName) {
        List<TagItem> tagItems;
        try {
            tagItems = metaClient.getTags(spaceName);
        } catch (TException | ExecuteFailedException e) {
            LOGGER.error("fresh tag error, ", e);
            return;
        }

        // get the latest schema
        Map<Integer, TagItem> latestTagItems = Maps.newHashMap();
        for (TagItem tagItem: tagItems) {
            int tagId = tagItem.getTag_id();
            if (!latestTagItems.containsKey(tagId)) {
                latestTagItems.put(tagId, tagItem);
            } else {
                if (tagItem.getVersion() > latestTagItems.get(tagId).getVersion()) {
                    latestTagItems.put(tagId, tagItem);
                }
            }
        }

        Map<String, Map<Long, TagItem>> spaceTagItems = metaInfo.getSpaceTagItems();
        Map<String, Map<String, Long>> tagNameId = metaInfo.getTagNameMap();
        writeLock.lock();
        try {
            for (TagItem item : latestTagItems.values()) {
                long tagId = item.getTag_id();
                String tagName = new String(item.getTag_name());
                if (!spaceTagItems.containsKey(spaceName)) {
                    spaceTagItems.put(spaceName, Maps.newHashMap());
                    tagNameId.put(spaceName, Maps.newHashMap());
                }

                spaceTagItems.get(spaceName).put(tagId, item);
                tagNameId.get(spaceName).put(tagName, tagId);
            }
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * fresh edges in meta info
     */
    private void freshEdges(String spaceName) {
        List<EdgeItem> edgeItems = null;
        try {
            edgeItems = metaClient.getEdges(spaceName);
        } catch (TException | ExecuteFailedException e) {
            LOGGER.error("fresh edge error, ", e);
            return;
        }

        // get the latest schema
        Map<Integer, EdgeItem> latestEdgeItems = Maps.newHashMap();
        for (EdgeItem edgeItem: edgeItems) {
            int edgeId = edgeItem.getEdge_type();
            if (!latestEdgeItems.containsKey(edgeId)) {
                latestEdgeItems.put(edgeId, edgeItem);
            } else {
                if (edgeItem.getVersion() > latestEdgeItems.get(edgeId).getVersion()) {
                    latestEdgeItems.put(edgeId, edgeItem);
                }
            }
        }

        Map<String, Map<Long, EdgeItem>> spaceEdgeItems = metaInfo.getSpaceEdgeItems();
        Map<String, Map<String, Long>> edgeNameId = metaInfo.getEdgeNameMap();
        writeLock.lock();
        try {
            for (EdgeItem item : edgeItems) {
                long tagId = item.getEdge_type();
                String tagName = new String(item.getEdge_name());
                if (!spaceEdgeItems.containsKey(spaceName)) {
                    spaceEdgeItems.put(spaceName, Maps.newHashMap());
                    edgeNameId.put(spaceName, Maps.newHashMap());
                }
                spaceEdgeItems.get(spaceName).put(tagId, item);
                edgeNameId.get(spaceName).put(tagName, tagId);
            }
        } finally {
            writeLock.unlock();
        }
    }


    /**
     * inner class for meta cache
     */
    class MetaInfo {

        private final Map<String, Integer> spaceNameMap = Maps.newHashMap();
        private final Map<String, Map<Integer, List<HostAddress>>>
                spacePartLocation = Maps.newHashMap();
        private final Map<String, Map<Long, TagItem>> spaceTagItems = Maps.newHashMap();
        private final Map<String, Map<Long, EdgeItem>> spaceEdgeItems = Maps.newHashMap();
        private final Map<String, Map<String, Long>> tagNameMap = Maps.newHashMap();
        private final Map<String, Map<String, Long>> edgeNameMap = Maps.newHashMap();
        private final Map<String, Map<Integer, HostAddress>> leaders = Maps.newHashMap();

        public Map<String, Integer> getSpaceNameMap() {
            return spaceNameMap;
        }

        public Map<String, Map<Integer, List<HostAddress>>> getSpacePartLocation() {
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

        public Map<String, Map<Integer, HostAddress>> getLeaders() {
            return leaders;
        }
    }

}

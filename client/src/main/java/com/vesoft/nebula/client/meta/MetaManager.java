/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.meta;

import com.facebook.thrift.TException;
import com.google.common.collect.Maps;
import com.google.common.net.HostAndPort;
import com.vesoft.nebula.client.meta.exception.ExecuteFailedException;
import com.vesoft.nebula.meta.EdgeItem;
import com.vesoft.nebula.meta.IdName;
import com.vesoft.nebula.meta.SpaceItem;
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

    // todo set decodeType
    private String decodeType = "utf-8";

    MetaInfo metaInfo = new MetaInfo();
    private static MetaClient metaClient;
    private static MetaManager metaManager;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
    private final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();

    private MetaManager() {
    }

    /**
     * only way to get a MetaManager object
     */
    public static MetaManager getMetaManager(List<HostAndPort> address) throws TException {
        if (metaManager == null) {
            synchronized (MetaManager.class) {
                if (metaManager == null) {
                    metaManager = new MetaManager();
                }
            }
        }
        metaManager = new MetaManager();
        metaManager.getClient(address);
        return metaManager;
    }

    private void getClient(List<HostAndPort> address) throws TException {
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
    private void fillMetaInfo() {
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
    public int getSpaceId(String spaceName) throws TException {
        if (!metaInfo.getSpaceNameMap().containsKey(spaceName)) {
            freshSpace(spaceName);
        }
        int spaceId;
        readLock.lock();
        try {
            spaceId = metaInfo.getSpaceNameMap().get(spaceName);
        } finally {
            readLock.unlock();
        }
        return spaceId;
    }

    /**
     * get all tag id of space
     *
     * @param spaceName nebula graph space name
     */
    public Set<Long> getTagIds(String spaceName) throws TException {
        if (!metaInfo.getTagIdMap().containsKey(spaceName)) {
            freshSpace(spaceName);
            freshTags(spaceName);
        }

        return metaInfo.getTagIdMap().get(spaceName).keySet();
    }


    /**
     * get tag id
     *
     * @param spaceName nebula graph space name
     * @param tagName   nebula tag name
     * @return long
     */
    public long getTagId(String spaceName, String tagName) throws TException {
        if (!metaInfo.getSpaceNameMap().containsKey(spaceName)) {
            freshSpace(spaceName);
        }
        Map<String, Map<String, Long>> tagNameMap = metaInfo.getTagNameMap();
        if (!tagNameMap.containsKey(spaceName)) {
            tagNameMap.put(spaceName, Maps.newHashMap());
        }
        if (!tagNameMap.get(spaceName).containsKey(tagName)) {
            freshTags(spaceName);
            // reget tagNameMap after freshTag
            tagNameMap = metaInfo.getTagNameMap();
        }

        if (!tagNameMap.get(spaceName).containsKey(tagName)) {
            throw new IllegalArgumentException(String.format("tag %s does not exist in space %s",
                    tagName, spaceName));
        }

        long tagId;
        readLock.lock();
        try {
            tagId = tagNameMap.get(spaceName).get(tagName);
        } finally {
            readLock.unlock();
        }
        return tagId;
    }

    /**
     * get Tag
     *
     * @param spaceName nebula space name
     * @param tagName   nebula tag name
     * @return
     */
    public TagItem getTag(String spaceName, String tagName) throws TException {
        long tagId = getTagId(spaceName, tagName);
        return metaInfo.getSpaceTagItems().get(spaceName).get(tagId);
    }


    /**
     * get all edge id of space
     *
     * @param spaceName nebula graph space name
     */
    public Set<Long> getEdgeIds(String spaceName) throws TException {
        if (!metaInfo.getEdgeIdMap().containsKey(spaceName)) {
            freshSpace(spaceName);
            freshEdges(spaceName);
        }

        return metaInfo.getEdgeIdMap().get(spaceName).keySet();
    }


    /**
     * get edge id
     *
     * @param spaceName nebula graph space name
     * @param edgeName  nebula edge name
     * @return long
     */
    public long getEdgeId(String spaceName, String edgeName) throws TException {
        if (!metaInfo.getSpaceNameMap().containsKey(spaceName)) {
            freshSpace(spaceName);
        }
        Map<String, Map<String, Long>> edgeNameMap = metaInfo.getEdgeNameMap();
        if (!edgeNameMap.containsKey(spaceName)) {
            edgeNameMap.put(spaceName, Maps.newHashMap());
        }
        if (!edgeNameMap.get(spaceName).containsKey(edgeName)) {
            freshEdges(spaceName);
            // reget edgeNameMap after freshTag
            edgeNameMap = metaInfo.getEdgeNameMap();
        }
        if (!edgeNameMap.get(spaceName).containsKey(edgeName)) {
            throw new IllegalArgumentException(String.format("tag %s does not exist in space %s",
                    edgeName, spaceName));
        }

        long edgeId;
        readLock.lock();
        try {
            edgeId = edgeNameMap.get(spaceName).get(edgeName);
        } finally {
            readLock.unlock();
        }
        return edgeId;
    }

    /**
     * get Edge
     *
     * @param spaceName nebula graph space name
     * @param edgeName  nebula edge name
     * @return
     */
    public EdgeItem getEdge(String spaceName, String edgeName) throws TException {
        long edgeId = getEdgeId(spaceName, edgeName);
        return metaInfo.getSpaceEdgeItems().get(spaceName).get(edgeId);
    }

    public String getTagName(String spaceName, long tagId) throws TException {
        if (!metaInfo.getTagIdMap().containsKey(spaceName)) {
            freshSpace(spaceName);
            freshTags(spaceName);
        }
        Map<Long, String> tagIdName = metaInfo.getTagIdMap().get(spaceName);
        if (tagIdName == null) {
            return null;
        } else {
            return tagIdName.get(tagId);
        }
    }

    /**
     * convert edgeID to edge name
     *
     * @param spaceName nebula graph space name
     * @param edgeId    nebula edge id
     * @return
     */
    public String getEdgeName(String spaceName, long edgeId) throws TException {
        if (!metaInfo.getEdgeIdMap().containsKey(spaceName)) {
            freshSpace(spaceName);
            freshEdges(spaceName);
        }
        Map<Long, String> edgeIdName = metaInfo.getEdgeIdMap().get(spaceName);
        if (edgeIdName == null) {
            return null;
        } else {
            return edgeIdName.get(edgeId);
        }
    }


    /**
     * get part leader
     *
     * @param spaceName nebula graph space name
     * @param part      nebula part id
     * @return leader
     */
    public HostAndPort getLeader(String spaceName, int part) throws TException {

        if (!metaInfo.getSpacePartLocation().containsKey(spaceName)) {
            freshSpace(spaceName);
        }

        Map<String, Map<Integer, HostAndPort>> leaders = metaInfo.getLeaders();

        if (!leaders.containsKey(spaceName)) {
            writeLock.lock();
            leaders.put(spaceName, Maps.newConcurrentMap());
            writeLock.unlock();
        }

        if (leaders.get(spaceName).containsKey(part)) {
            HostAndPort leader = null;
            if (leaders.get(spaceName).containsKey(part)) {
                leader = leaders.get(spaceName).get(part);
            }
            return leader;
        }
        Map<String, Map<Integer, List<HostAndPort>>> spacePartLocations =
                metaInfo.getSpacePartLocation();

        readLock.lock();
        if (spacePartLocations.containsKey(spaceName)
                && spacePartLocations.get(spaceName).containsKey(part)) {
            List<HostAndPort> addresses;
            try {
                addresses = metaInfo.getSpacePartLocation().get(spaceName).get(part);
            } finally {
                readLock.unlock();
            }
            if (addresses != null) {
                Random random = new Random(System.currentTimeMillis());
                int position = random.nextInt(addresses.size());
                HostAndPort leader = addresses.get(position);
                Map<Integer, HostAndPort> partLeader = leaders.get(spaceName);
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
    public List<Integer> getSpaceParts(String spaceName) throws TException {
        if (!metaInfo.getSpacePartLocation().containsKey(spaceName)) {
            freshSpace(spaceName);
        }
        Set<Integer> spaceParts;
        readLock.lock();
        try {
            spaceParts = metaInfo.getSpacePartLocation().get(spaceName).keySet();
        } finally {
            readLock.unlock();
        }
        return new ArrayList<>(spaceParts);
    }

    /**
     * get the size of parts for space
     *
     * @param spaceName nebula graph space name
     */
    public int getPartSize(String spaceName) throws TException {
        if (!metaInfo.getSpacePartLocation().containsKey(spaceName)) {
            freshSpace(spaceName);
        }
        Map<String, Map<Integer, List<HostAndPort>>> spacePartLocations =
                metaInfo.getSpacePartLocation();
        if (spacePartLocations.containsKey(spaceName)) {
            return spacePartLocations.get(spaceName).keySet().size();
        }
        return -1;
    }


    /**
     * cache new leader for part
     *
     * @param spaceName nebula graph space
     * @param part      nebula part
     * @param newLeader nebula part new leader
     */
    public void freshLeader(String spaceName, int part, HostAndPort newLeader) throws TException {
        if (!metaInfo.getLeaders().containsKey(spaceName)) {
            getLeader(spaceName, part);
        }
        Map<Integer, HostAndPort> partLeader = metaInfo.getLeaders().get(spaceName);
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
    public Set<HostAndPort> listHosts() {
        return metaClient.listHosts();
    }

    public MetaInfo getMetaInfo() {
        return metaInfo;
    }

    public String getDecodeType() {
        return decodeType;
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
     * fresh space in metaInfo
     */
    private void freshSpace(String spaceName) throws TException {
        SpaceItem spaceItem = metaClient.getSpace(spaceName);
        if (spaceItem == null) {
            throw new IllegalArgumentException(
                    String.format("space %s does not exist.", spaceName));
        }

        Map<String, Map<Integer, List<HostAndPort>>> spaceParts = metaInfo.getSpacePartLocation();
        Map<String, Integer> spaceNames = metaInfo.getSpaceNameMap();
        writeLock.lock();
        try {
            spaceNames.put(spaceName, spaceItem.getSpace_id());
            spaceParts.put(spaceName, metaClient.getPartsLocation(spaceName));
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * fresh tags in meta info
     */
    private void freshTags(String spaceName) {
        List<TagItem> tagItems = null;
        try {
            tagItems = metaClient.getTags(spaceName);
        } catch (TException | ExecuteFailedException e) {
            LOGGER.error("fresh tag error, ", e);
        }

        Map<String, Map<Long, TagItem>> spaceTagItems = metaInfo.getSpaceTagItems();
        Map<String, Map<Long, String>> tagIdName = metaInfo.getTagIdMap();
        Map<String, Map<String, Long>> tagNameId = metaInfo.getTagNameMap();
        writeLock.lock();
        try {
            for (TagItem item : tagItems) {
                long tagId = item.getTag_id();
                String tagName = new String(item.getTag_name());
                if (!spaceTagItems.containsKey(spaceName)) {
                    spaceTagItems.put(spaceName, Maps.newHashMap());
                    tagIdName.put(spaceName, Maps.newHashMap());
                    tagNameId.put(spaceName, Maps.newHashMap());
                }
                spaceTagItems.get(spaceName).put(tagId, item);
                tagIdName.get(spaceName).put(tagId, tagName);
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
        }

        Map<String, Map<Long, EdgeItem>> spaceEdgeItems = metaInfo.getSpaceEdgeItems();
        Map<String, Map<Long, String>> edgeIdName = metaInfo.getEdgeIdMap();
        Map<String, Map<String, Long>> edgeNameId = metaInfo.getEdgeNameMap();
        writeLock.lock();
        try {
            for (EdgeItem item : edgeItems) {
                long tagId = item.getEdge_type();
                String tagName = new String(item.getEdge_name());
                if (!spaceEdgeItems.containsKey(spaceName)) {
                    spaceEdgeItems.put(spaceName, Maps.newHashMap());
                    edgeIdName.put(spaceName, Maps.newHashMap());
                    edgeNameId.put(spaceName, Maps.newHashMap());
                }
                spaceEdgeItems.get(spaceName).put(tagId, item);
                edgeIdName.get(spaceName).put(tagId, tagName);
                edgeNameId.get(spaceName).put(tagName, tagId);
            }
        } finally {
            writeLock.unlock();
        }
    }
}

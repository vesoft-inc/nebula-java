/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.meta;

import com.facebook.thrift.TException;
import com.google.common.collect.Maps;
import com.vesoft.nebula.HostAddr;
import com.vesoft.nebula.client.graph.data.HostAddress;
import com.vesoft.nebula.client.meta.exception.ExecuteFailedException;
import com.vesoft.nebula.meta.EdgeItem;
import com.vesoft.nebula.meta.IdName;
import com.vesoft.nebula.meta.SpaceItem;
import com.vesoft.nebula.meta.TagItem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MetaManager is a manager for meta info, such as spaces,tags and edges.
 * How to use:
 * MetaManager manager = MetaManager.getMetaManager(Arrays.asList(HostAddress(host, port)));
 */
public class MetaManager implements MetaCache {
    private class SpaceInfo {
        private SpaceItem spaceItem = null;
        private Map<String, TagItem> tagItems = new HashMap<>();
        private Map<Integer, String> tagIdNames = new HashMap<>();
        private Map<String, EdgeItem> edgeItems = new HashMap<>();
        private Map<Integer, String> edgeTypeNames = new HashMap<>();
        private Map<Integer, List<HostAddr>> partsAlloc = new HashMap<>();
    }

    private Map<String, MetaManager.SpaceInfo> spacesInfo = new HashMap<>();
    private Map<String, Map<Integer, HostAddr>> partLeaders = null;

    private static final Logger LOGGER = LoggerFactory.getLogger(MetaManager.class);

    private static MetaClient metaClient;
    private static MetaManager metaManager;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private MetaManager() {
    }

    /**
     * init the meta info cache
     * make sure this method is called before use metaManager
     */
    private void init(List<HostAddress> address) throws TException {
        metaClient = new MetaClient(address);
        metaClient.connect();
        fillMetaInfo();
    }

    /**
     * only way to get a MetaManager object
     */
    public static MetaManager getMetaManager(List<HostAddress> address) throws TException {
        if (metaManager == null) {
            synchronized (MetaManager.class) {
                if (metaManager == null) {
                    metaManager = new MetaManager();
                    metaManager.init(address);
                }
            }
        }
        return metaManager;
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
        try {
            Map<String, MetaManager.SpaceInfo> tempSpacesInfo = new HashMap<>();
            List<IdName> spaces = metaClient.getSpaces();
            for (IdName space : spaces) {
                SpaceInfo spaceInfo = new SpaceInfo();
                String spaceName = new String(space.name);
                SpaceItem spaceItem = metaClient.getSpace(spaceName);
                spaceInfo.spaceItem = spaceItem;
                List<TagItem> tags = metaClient.getTags(spaceName);
                for (TagItem tag : tags) {
                    spaceInfo.tagItems.put(new String(tag.tag_name), tag);
                    spaceInfo.tagIdNames.put(tag.tag_id, new String(tag.tag_name));
                }
                List<EdgeItem> edges = metaClient.getEdges(spaceName);
                for (EdgeItem edge : edges) {
                    spaceInfo.edgeItems.put(new String(edge.edge_name), edge);
                    spaceInfo.edgeTypeNames.put(edge.edge_type, new String(edge.edge_name));
                }
                Map<Integer, List<HostAddr>> partsAlloc = metaClient.getPartsAlloc(spaceName);
                spaceInfo.partsAlloc = partsAlloc;
                tempSpacesInfo.put(spaceName, spaceInfo);
            }
            try {
                lock.writeLock().lock();
                spacesInfo = tempSpacesInfo;
                if (partLeaders == null) {
                    partLeaders = new HashMap<>();
                }
                for (String spaceName : spacesInfo.keySet()) {
                    if (!partLeaders.containsKey(spaceName)) {
                        partLeaders.put(spaceName, Maps.newConcurrentMap());
                        for (int partId : spacesInfo.get(spaceName).partsAlloc.keySet()) {
                            if (spacesInfo.get(spaceName).partsAlloc.get(partId).size() < 1) {
                                LOGGER.error("space {} part {} has not allocation host.",
                                        spaceName, partId);
                            } else {
                                partLeaders.get(spaceName).put(partId,
                                        spacesInfo.get(spaceName).partsAlloc.get(partId).get(0));
                            }

                        }
                    }
                }
            } finally {
                lock.writeLock().unlock();
            }
        } catch (TException | ExecuteFailedException e) {
            LOGGER.error(e.getMessage());
        }
    }


    /**
     * get space id
     *
     * @param spaceName nebula space name
     * @return
     */
    public int getSpaceId(String spaceName) throws IllegalArgumentException {
        return getSpace(spaceName).space_id;
    }

    /**
     * get space item
     *
     * @param spaceName nebula graph space name
     * @return SpaceItem
     */
    @Override
    public SpaceItem getSpace(String spaceName) throws IllegalArgumentException {
        if (!spacesInfo.containsKey(spaceName)) {
            fillMetaInfo();
        }
        try {
            lock.readLock().lock();
            if (!spacesInfo.containsKey(spaceName)) {
                throw new IllegalArgumentException("space:" + spaceName + " does not exist.");
            }
            return spacesInfo.get(spaceName).spaceItem;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * get tag id
     *
     * @param spaceName nebula graph space name
     * @param tagName   nebula tag name
     * @return int
     */
    public int getTagId(String spaceName, String tagName) throws IllegalArgumentException {
        return getTag(spaceName, tagName).tag_id;
    }

    /**
     * get tag
     *
     * @param spaceName nebula space name
     * @param tagName   nebula tag name
     * @return
     */
    @Override
    public TagItem getTag(String spaceName, String tagName) throws IllegalArgumentException {
        if (!spacesInfo.containsKey(spaceName)
                || !spacesInfo.get(spaceName).tagItems.containsKey(tagName)) {
            fillMetaInfo();
        }
        try {
            lock.readLock().lock();
            if (!spacesInfo.containsKey(spaceName)) {
                throw new IllegalArgumentException("Space:" + spaceName + " does not exist.");
            }
            if (!spacesInfo.get(spaceName).tagItems.containsKey(tagName)) {
                throw new IllegalArgumentException("Tag:" + tagName + " does not exist.");
            }
            return spacesInfo.get(spaceName).tagItems.get(tagName);
        } finally {
            lock.readLock().unlock();
        }
    }


    /**
     * get edge type
     *
     * @param spaceName nebula graph space name
     * @param edgeName  nebula edge name
     * @return long
     */
    public int getEdgeType(String spaceName, String edgeName) throws IllegalArgumentException {
        return getEdge(spaceName, edgeName).edge_type;
    }

    /**
     * get Edge
     *
     * @param spaceName nebula graph space name
     * @param edgeName  nebula edge name
     * @return
     */
    @Override
    public EdgeItem getEdge(String spaceName, String edgeName) throws IllegalArgumentException {
        if (!spacesInfo.containsKey(spaceName)
                || !spacesInfo.get(spaceName).tagItems.containsKey(edgeName)) {
            fillMetaInfo();
        }
        try {
            lock.readLock().lock();
            if (!spacesInfo.containsKey(spaceName)) {
                throw new IllegalArgumentException("Space:" + spaceName + " does not exist.");
            }
            if (!spacesInfo.get(spaceName).edgeItems.containsKey(edgeName)) {
                throw new IllegalArgumentException("Edge:" + edgeName + " does not exist.");
            }
            return spacesInfo.get(spaceName).edgeItems.get(edgeName);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * get part leader
     *
     * @param spaceName nebula graph space name
     * @param part      nebula part id
     * @return leader
     */
    public HostAddr getLeader(String spaceName, int part) throws IllegalArgumentException {
        if (!spacesInfo.containsKey(spaceName)) {
            fillMetaInfo();
        }
        try {
            lock.readLock().lock();
            if (partLeaders == null) {
                throw new IllegalArgumentException("Space:" + spaceName + " does not exist.");
            }

            if (!partLeaders.containsKey(spaceName)) {
                throw new IllegalArgumentException("Space:" + spaceName + " does not exist.");
            }

            if (!partLeaders.get(spaceName).containsKey(part)) {
                throw new IllegalArgumentException("PartId:" + part + " does not exist.");
            }
            return partLeaders.get(spaceName).get(part);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * get all parts of one space
     *
     * @param spaceName nebula graph space name
     * @return Lsit
     */
    public List<Integer> getSpaceParts(String spaceName) throws IllegalArgumentException {
        return new ArrayList<>(getPartsAlloc(spaceName).keySet());
    }

    /**
     * get all parts alloc of one space
     *
     * @param spaceName nebula graph space name
     * @return Map
     */
    @Override
    public Map<Integer, List<HostAddr>> getPartsAlloc(String spaceName)
            throws IllegalArgumentException {
        if (!spacesInfo.containsKey(spaceName)) {
            fillMetaInfo();
        }
        try {
            lock.readLock().lock();
            if (!spacesInfo.containsKey(spaceName)) {
                throw new IllegalArgumentException("Space:" + spaceName + " does not exist.");
            }
            return spacesInfo.get(spaceName).partsAlloc;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * cache new leader for part
     *
     * @param spaceName nebula graph space
     * @param part      nebula part
     * @param newLeader nebula part new leader
     */
    public void updateLeader(String spaceName, int part, HostAddr newLeader)
            throws IllegalArgumentException {
        try {
            lock.writeLock().lock();
            if (partLeaders == null) {
                throw new IllegalArgumentException("Space:" + spaceName + " does not exist.");
            }

            if (!partLeaders.containsKey(spaceName)) {
                throw new IllegalArgumentException("Space:" + spaceName + " does not exist.");
            }

            if (!partLeaders.get(spaceName).containsKey(part)) {
                throw new IllegalArgumentException("PartId:" + part + " does not exist.");
            }
            partLeaders.get(spaceName).put(part, newLeader);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * get all storage addresses
     */
    public Set<HostAddr> listHosts() {
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
}

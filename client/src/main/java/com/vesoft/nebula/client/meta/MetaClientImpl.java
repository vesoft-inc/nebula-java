/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.meta;

import com.facebook.thrift.TException;
import com.facebook.thrift.protocol.TCompactProtocol;
import com.facebook.thrift.transport.TSocket;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.net.HostAndPort;
import com.vesoft.nebula.AbstractClient;
import com.vesoft.nebula.ColumnDef;
import com.vesoft.nebula.HostAddr;
import com.vesoft.nebula.Schema;
import com.vesoft.nebula.client.meta.entry.SpaceNameID;
import com.vesoft.nebula.meta.EdgeItem;
import com.vesoft.nebula.meta.ErrorCode;
import com.vesoft.nebula.meta.GetEdgeReq;
import com.vesoft.nebula.meta.GetEdgeResp;
import com.vesoft.nebula.meta.GetPartsAllocReq;
import com.vesoft.nebula.meta.GetPartsAllocResp;
import com.vesoft.nebula.meta.GetTagReq;
import com.vesoft.nebula.meta.GetTagResp;
import com.vesoft.nebula.meta.ListEdgesReq;
import com.vesoft.nebula.meta.ListEdgesResp;
import com.vesoft.nebula.meta.ListSpacesReq;
import com.vesoft.nebula.meta.ListSpacesResp;
import com.vesoft.nebula.meta.ListTagsReq;
import com.vesoft.nebula.meta.ListTagsResp;
import com.vesoft.nebula.meta.MetaService;
import com.vesoft.nebula.meta.TagItem;
import com.vesoft.nebula.utils.AddressUtil;
import com.vesoft.nebula.utils.NebulaTypeUtil;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Nebula Meta Client
 */
public class MetaClientImpl extends AbstractClient implements MetaClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetaClientImpl.class);

    // Use a lock to protect the cache
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private Map<String, Integer> spaceNameMap = Maps.newHashMap();
    private Map<String, Map<Integer, List<HostAndPort>>> spacePartLocation = Maps.newHashMap();
    private Map<String, Map<String, TagItem>> spaceTagItems = Maps.newHashMap();
    private Map<String, Map<String, EdgeItem>> spaceEdgeItems = Maps.newHashMap();
    private Map<String, Map<Integer, String>> tagNameMap = Maps.newHashMap();
    private Map<String, Map<Integer, String>> edgeNameMap = Maps.newHashMap();

    private MetaService.Client client;

    public MetaClientImpl(List<HostAndPort> addresses, int timeout,
                          int connectionRetry, int executionRetry) {
        this(addresses, timeout, DEFAULT_CONNECTION_TIMEOUT_MS, connectionRetry, executionRetry);
    }

    public MetaClientImpl(List<HostAndPort> addresses, int timeout, int connTimeout,
                          int connectionRetry, int executionRetry) {
        super(addresses, timeout, connTimeout, connectionRetry, executionRetry);
    }

    public MetaClientImpl(List<HostAndPort> addresses) {
        super(addresses);
    }

    public MetaClientImpl(String host, int port) {
        super(host, port);
    }

    @Override
    public int doConnect(List<HostAndPort> addresses) throws TException {
        Random random = new Random(System.currentTimeMillis());
        int position = random.nextInt(addresses.size());
        HostAndPort address = addresses.get(position);
        getClient(address.getHost(), address.getPort());

        for (SpaceNameID space : listSpaces()) {
            String spaceName = space.getName();
            spaceNameMap.put(spaceName, space.getId());
            spacePartLocation.put(spaceName, getPartsAlloc(spaceName));

            // Loading tag schema's cache
            Map<String, TagItem> tags = Maps.newHashMap();
            Map<Integer, String> tagsName = Maps.newHashMap();
            for (TagItem item : getTags(spaceName)) {
                tags.put(item.getTag_name(), item);
                tagsName.put(item.getTag_id(), item.getTag_name());
            }
            spaceTagItems.put(spaceName, tags);
            tagNameMap.put(spaceName, tagsName);

            // Loading edge schema's cache
            Map<String, EdgeItem> edges = Maps.newHashMap();
            Map<Integer, String> edgesName = Maps.newHashMap();
            for (EdgeItem item : getEdges(spaceName)) {
                edges.put(item.getEdge_name(), item);
                edgesName.put(item.getEdge_type(), item.getEdge_name());
            }
            spaceEdgeItems.put(spaceName, edges);
            edgeNameMap.put(spaceName, edgesName);
        }
        return 0;
    }

    private void getClient(String host, int port) throws TException {
        transport = new TSocket(host, port, timeout, connectionTimeout);
        transport.open();
        protocol = new TCompactProtocol(transport);
        client = new MetaService.Client(protocol);
    }

    private void freshClient(HostAddr leader) throws TException {
        close();
        getClient(AddressUtil.intToIPv4(leader.getIp()), leader.getPort());
    }

    public Map<String, Integer> getSpaces() {
        return spaceNameMap;
    }

    public int getSpaceIdFromCache(String name) {
        if (!spaceNameMap.containsKey(name)) {
            return -1;
        } else {
            return spaceNameMap.get(name);
        }
    }

    /**
     * Get all spaces and store in this.spaces
     *
     * @return
     */
    public List<SpaceNameID> listSpaces() {
        int retry = 1;
        ListSpacesReq request = new ListSpacesReq();
        ListSpacesResp response = null;
        try {
            while (retry-- >= 0) {
                response = client.listSpaces(request);
                if (response.getCode() == ErrorCode.E_LEADER_CHANGED) {
                    freshClient(response.getLeader());
                } else {
                    break;
                }
            }
        } catch (TException e) {
            LOGGER.error(String.format("List Spaces Error: %s", e.getMessage()));
            return Lists.newLinkedList();
        }
        if (response == null) {
            LOGGER.error("response of listSpaces is null");
            return Lists.newArrayList();
        }
        if (response.getCode() == ErrorCode.SUCCEEDED) {
            return response.getSpaces().stream().map(SpaceNameID::new).collect(Collectors.toList());
        } else {
            LOGGER.error(String.format("List Spaces Error Code: %d", response.getCode()));
            return Lists.newLinkedList();
        }
    }

    /**
     * Get a list of host addresses by a particular space Id
     *
     * @param spaceName Nebula space name
     * @param part      Nebula partition ID
     * @return
     */
    public List<HostAndPort> getPartFromCache(String spaceName, int part) {
        if (!this.spacePartLocation.containsKey(spaceName)) {
            if (lock.writeLock().tryLock()) {
                spacePartLocation.put(spaceName, getPartsAlloc(spaceName));
            }
            lock.writeLock().unlock();
        }

        Map<Integer, List<HostAndPort>> map = spacePartLocation.get(spaceName);
        if (Objects.isNull(map) || !map.containsKey(part)) {
            return null;
        }
        return map.get(part);
    }

    /**
     * Get all parts and the addrs in a space
     * Store in this.parts
     *
     * @param spaceName Nebula space name
     * @return
     */
    @Override
    public Map<Integer, List<HostAndPort>> getPartsAlloc(String spaceName) {
        int retry = 1;
        GetPartsAllocReq request = new GetPartsAllocReq();
        int spaceID = getSpaceIdFromCache(spaceName);
        request.setSpace_id(spaceID);

        GetPartsAllocResp response = null;
        try {
            while (retry-- >= 0) {
                response = client.getPartsAlloc(request);
                if (response.getCode() == ErrorCode.E_LEADER_CHANGED) {
                    freshClient(response.getLeader());
                } else {
                    break;
                }
            }
        } catch (TException e) {
            LOGGER.error(String.format("Get Parts failed: %s", e.getMessage()));
            return Maps.newHashMap();
        }

        if (response == null) {
            LOGGER.error("response of getPartsAlloc is null.");
            return Maps.newHashMap();
        }
        if (response.getCode() == ErrorCode.SUCCEEDED) {
            Map<Integer, List<HostAndPort>> addressMap = Maps.newHashMap();
            for (Map.Entry<Integer, List<HostAddr>> entry : response.getParts().entrySet()) {
                List<HostAndPort> addresses = Lists.newLinkedList();
                for (HostAddr address : entry.getValue()) {
                    String host = AddressUtil.intToIPv4(address.ip);
                    HostAndPort pair = HostAndPort.fromParts(host, address.port);
                    addresses.add(pair);
                }
                addressMap.put(entry.getKey(), addresses);
            }
            return addressMap;
        } else {
            LOGGER.error(String.format("Get Parts Error: %s", response.getCode()));
            return Maps.newHashMap();
        }
    }

    @Override
    public Map<String, Map<Integer, List<HostAndPort>>> getPartsAllocFromCache() {
        return this.spacePartLocation;
    }

    @Override
    public List<HostAndPort> getPartAllocFromCache(String spaceName, int part) {
        if (spacePartLocation.containsKey(spaceName)) {
            Map<Integer, List<HostAndPort>> partsAlloc = spacePartLocation.get(spaceName);
            if (partsAlloc.containsKey(part)) {
                return partsAlloc.get(part);
            }
        }
        return null;
    }

    public TagItem getTagItemFromCache(String spaceName, String tagName) {
        if (!spaceTagItems.containsKey(spaceName)) {
            if (lock.writeLock().tryLock()) {
                Map<String, TagItem> tags = Maps.newHashMap();
                for (TagItem item : getTags(spaceName)) {
                    tags.put(item.getTag_name(), item);
                }
                spaceTagItems.put(spaceName, tags);
            }
            lock.writeLock().unlock();
        }

        Map<String, TagItem> map = spaceTagItems.get(spaceName);
        if (Objects.isNull(map) || !map.containsKey(tagName)) {
            return null;
        }
        return map.get(tagName);
    }

    /**
     * Get a tag Id by a particular space Id and tag name
     *
     * @param spaceName Nebula space name
     * @param tagId     Nebula tag id
     * @return
     */
    public String getTagNameFromCache(String spaceName, Integer tagId) {
        if (tagNameMap.containsKey(spaceName)) {
            Map<Integer, String> map = tagNameMap.get(spaceName);
            if (map.containsKey(tagId)) {
                return map.get(tagId);
            }
        }
        return null;
    }

    /**
     * Get all tags, store as tagName : tagItem in this.tagItems
     *
     * @param spaceName Nebula space name
     * @return
     */
    @Override
    public List<TagItem> getTags(String spaceName) {
        int retry = 1;
        ListTagsReq request = new ListTagsReq();
        int spaceID = getSpaceIdFromCache(spaceName);
        request.setSpace_id(spaceID);
        ListTagsResp response = null;
        try {
            while (retry-- >= 0) {
                response = client.listTags(request);
                if (response.getCode() == ErrorCode.E_LEADER_CHANGED) {
                    freshClient(response.getLeader());
                } else {
                    break;
                }
            }
        } catch (TException e) {
            LOGGER.error(String.format("Get Tag Error: %s", e.getMessage()));
            return Lists.newLinkedList();
        }

        if (response == null) {
            LOGGER.error("response of listTags is null.");
            return Lists.newLinkedList();
        }
        if (response.getCode() == ErrorCode.SUCCEEDED) {
            return response.getTags();
        } else {
            LOGGER.error(String.format("Get tags Error: %s", response.getCode()));
            return Lists.newLinkedList();
        }
    }

    @Override
    public Schema getTag(String spaceName, String tagName) {
        int retry = 1;
        GetTagReq request = new GetTagReq();
        int spaceID = getSpaceIdFromCache(spaceName);
        request.setSpace_id(spaceID);
        request.setTag_name(tagName);
        request.setVersion(LATEST_SCHEMA_VERSION);
        GetTagResp response = null;
        try {
            while (retry-- >= 0) {
                response = client.getTag(request);
                if (response.getCode() == ErrorCode.E_LEADER_CHANGED) {
                    freshClient(response.getLeader());
                } else {
                    break;
                }
            }
        } catch (TException e) {
            LOGGER.error(String.format("Get Tag Error: %s", e.getMessage()));
            return null;
        }

        if (response == null) {
            LOGGER.error("response of getTag is null.");
            return null;
        }
        if (response.getCode() == ErrorCode.SUCCEEDED) {
            return response.getSchema();
        } else {
            LOGGER.error(String.format("Get Tag Error: %s", response.getCode()));
            return null;
        }
    }

    @Override
    public Map<String, Class> getTagSchema(String spaceName, String tagName, long version) {
        int retry = 1;
        Map<String, Class> result = Maps.newHashMap();
        if (!spaceNameMap.containsKey(spaceName)) {
            return result;
        }

        GetTagReq request = new GetTagReq();
        request.setSpace_id(spaceNameMap.get(spaceName));
        request.setTag_name(tagName);
        request.setVersion(version);
        GetTagResp response = null;
        try {
            while (retry-- >= 0) {
                response = client.getTag(request);
                if (response.getCode() == ErrorCode.E_LEADER_CHANGED) {
                    freshClient(response.getLeader());
                } else {
                    break;
                }
            }
        } catch (TException e) {
            e.printStackTrace();
            return result;
        }
        if (response == null) {
            LOGGER.error("response of getTag is null.");
            return result;
        }

        for (ColumnDef column : response.getSchema().columns) {
            result.put(column.name, NebulaTypeUtil.supportedTypeToClass(column.type.type));
        }
        return result;
    }

    public Map<String, Class> getTagSchema(String spaceName, String tagName) {
        return getTagSchema(spaceName, tagName, LATEST_SCHEMA_VERSION);
    }

    public EdgeItem getEdgeItemFromCache(String spaceName, String edgeName) {
        if (!spaceEdgeItems.containsKey(spaceName)) {
            if (lock.writeLock().tryLock()) {
                Map<String, EdgeItem> edges = Maps.newHashMap();
                for (EdgeItem item : getEdges(spaceName)) {
                    edges.put(item.getEdge_name(), item);
                }
                spaceEdgeItems.put(spaceName, edges);
            }
            lock.writeLock().unlock();
        }

        Map<String, EdgeItem> map = spaceEdgeItems.get(spaceName);
        if (Objects.isNull(map) || !map.containsKey(edgeName)) {
            return new EdgeItem();
        }

        return map.get(edgeName);
    }

    /**
     * Get a edge type by a particular space name and edge name
     *
     * @param spaceName Nebula space name
     * @param edgeType  Nebula edge type
     * @return
     */
    public String getEdgeNameFromCache(String spaceName, Integer edgeType) {
        if (edgeNameMap.containsKey(spaceName)) {
            Map<Integer, String> map = edgeNameMap.get(spaceName);
            if (map.containsKey(edgeType)) {
                return map.get(edgeType);
            }
        }
        return null;
    }

    /**
     * Get all edges, store as edgeName : edgeItem in this.edgeItems
     *
     * @param spaceName Nebula space name
     * @return
     */
    @Override
    public List<EdgeItem> getEdges(String spaceName) {
        int retry = 1;
        ListEdgesReq request = new ListEdgesReq();
        int spaceID = getSpaceIdFromCache(spaceName);
        request.setSpace_id(spaceID);

        ListEdgesResp response = null;
        try {
            while (retry-- >= 0) {
                response = client.listEdges(request);
                if (response.getCode() == ErrorCode.E_LEADER_CHANGED) {
                    freshClient(response.getLeader());
                } else {
                    break;
                }
            }
        } catch (TException e) {
            LOGGER.error(String.format("Get Edge Error: %s", e.getMessage()));
            return Lists.newLinkedList();
        }

        if (response == null) {
            LOGGER.error("response of getEdges is null.");
            return Lists.newLinkedList();
        }
        if (response.getCode() == ErrorCode.SUCCEEDED) {
            return response.getEdges();
        } else {
            LOGGER.error(String.format("Get tags Error: %s", response.getCode()));
            return Lists.newLinkedList();
        }
    }

    @Override
    public Schema getEdge(String spaceName, String edgeName) {
        int retry = 1;
        GetEdgeReq request = new GetEdgeReq();
        int spaceID = getSpaceIdFromCache(spaceName);
        request.setSpace_id(spaceID);
        request.setEdge_name(edgeName);
        request.setVersion(LATEST_SCHEMA_VERSION);
        GetEdgeResp response = null;
        try {
            while (retry-- >= 0) {
                response = client.getEdge(request);
                if (response.getCode() == ErrorCode.E_LEADER_CHANGED) {
                    freshClient(response.getLeader());
                } else {
                    break;
                }
            }
        } catch (TException e) {
            LOGGER.error(String.format("Get Edge Error: %s", e.getMessage()));
            return null;
        }

        if (response == null) {
            LOGGER.error("response of getEdge is null");
            return null;
        }
        if (response.getCode() == ErrorCode.SUCCEEDED) {
            return response.getSchema();
        } else {
            return null;
        }
    }

    @Override
    public Map<String, Class> getEdgeSchema(String spaceName, String edgeName, long version) {
        int retry = 1;
        Map<String, Class> result = Maps.newHashMap();
        if (!spaceNameMap.containsKey(spaceName)) {
            return result;
        }

        GetEdgeReq request = new GetEdgeReq();
        request.setSpace_id(spaceNameMap.get(spaceName));
        request.setEdge_name(edgeName);
        request.setVersion(version);

        GetEdgeResp response = null;
        try {
            while (retry-- >= 0) {
                response = client.getEdge(request);
                if (response.getCode() == ErrorCode.E_LEADER_CHANGED) {
                    freshClient(response.getLeader());
                } else {
                    break;
                }
            }
        } catch (TException e) {
            e.printStackTrace();
            return result;
        }

        for (ColumnDef column : response.getSchema().columns) {
            result.put(column.name, NebulaTypeUtil.supportedTypeToClass(column.type.type));
        }
        return result;
    }

    public Map<String, Class> getEdgeSchema(String spaceName, String edgeName) {
        return getEdgeSchema(spaceName, edgeName, LATEST_SCHEMA_VERSION);
    }
}


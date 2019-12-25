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

import java.util.HashMap;
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

    private HostAndPort leader;

    // Use a lock to protect the cache
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private Map<String, Integer> spaceNameID = new HashMap<>();
    private Map<String, Map<Integer, List<HostAndPort>>> spacePartLocation = new HashMap<>();
    private Map<String, Map<String, TagItem>> spaceTagItems = new HashMap<>();
    private Map<String, Map<String, EdgeItem>> spaceEdgeItems = new HashMap<>();


    private MetaService.Client client;

    public MetaClientImpl(List<HostAndPort> addresses, int timeout,
                          int connectionRetry, int executionRetry) {
        super(addresses, timeout, connectionRetry, executionRetry);
    }

    public MetaClientImpl(List<HostAndPort> addresses) {
        super(addresses);
    }

    public MetaClientImpl(String host, int port) throws Exception {
        super(host, port);
        this.connect();
    }

    @Override
    public int doConnect(List<HostAndPort> addresses) throws TException {
        Random random = new Random(System.currentTimeMillis());
        int position = random.nextInt(addresses.size());
        HostAndPort address = addresses.get(position);
        transport = new TSocket(address.getHostText(), address.getPort(), timeout);
        transport.open();
        protocol = new TCompactProtocol(transport);
        client = new MetaService.Client(protocol);

        for (SpaceNameID space : listSpaces()) {
            String spaceName = space.getName();
            spaceNameID.put(spaceName, space.getId());
            spacePartLocation.put(spaceName, getPartsAlloc(spaceName));

            // Loading tag schema's cache
            Map<String, TagItem> tags = Maps.newHashMap();
            for (TagItem item : getTags(spaceName)) {
                tags.put(item.getTag_name(), item);
            }
            spaceTagItems.put(spaceName, tags);

            // Loading edge schema's cache
            Map<String, EdgeItem> edges = Maps.newHashMap();
            for (EdgeItem item : getEdges(spaceName)) {
                edges.put(item.getEdge_name(), item);
            }
            spaceEdgeItems.put(spaceName, edges);
        }
        return 0;
    }

    public Map<String, Integer> getSpaces() {
        return spaceNameID;
    }

    public int getSpaceIDFromCache(String name) {
        if (!spaceNameID.containsKey(name)) {
            return -1;
        } else {
            return spaceNameID.get(name);
        }
    }

    /**
     * Get all spaces and store in this.spaces
     *
     * @return
     */
    public List<SpaceNameID> listSpaces() {
        ListSpacesReq request = new ListSpacesReq();
        ListSpacesResp response;
        try {
            response = client.listSpaces(request);
        } catch (TException e) {
            LOGGER.error(String.format("List Spaces Error: %s", e.getMessage()));
            return Lists.newLinkedList();
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
        GetPartsAllocReq request = new GetPartsAllocReq();
        int spaceID = getSpaceIDFromCache(spaceName);
        request.setSpace_id(spaceID);

        GetPartsAllocResp response;
        try {
            response = client.getPartsAlloc(request);
        } catch (TException e) {
            LOGGER.error(String.format("Get Parts failed: %s", e.getMessage()));
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
     * @param tagName   Nebula tag name
     * @return
     */
    public Integer getTagIdFromCache(String spaceName, String tagName) {
        TagItem item = getTagItemFromCache(spaceName, tagName);
        if (Objects.isNull(item)) {
            return -1;
        } else {
            return item.getTag_id();
        }
    }

    /**
     * Get all tags, store as tagName : tagItem in this.tagItems
     *
     * @param spaceName Nebula space name
     * @return
     */
    @Override
    public List<TagItem> getTags(String spaceName) {
        ListTagsReq request = new ListTagsReq();
        int spaceID = getSpaceIDFromCache(spaceName);
        request.setSpace_id(spaceID);
        ListTagsResp response;
        try {
            response = client.listTags(request);
        } catch (TException e) {
            LOGGER.error(String.format("Get Tag Error: %s", e.getMessage()));
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
        GetTagReq request = new GetTagReq();
        int spaceID = getSpaceIDFromCache(spaceName);
        request.setSpace_id(spaceID);
        GetTagResp response;

        try {
            response = client.getTag(request);
        } catch (TException e) {
            LOGGER.error(String.format("Get Tag Error: %s", e.getMessage()));
            return new Schema();
        }

        if (response.getCode() == ErrorCode.SUCCEEDED) {
            return response.getSchema();
        } else {
            return new Schema();
        }
    }

    @Override
    public Map<String, Class> getTagSchema(String spaceName, String tagName, long version) {
        Map<String, Class> result = Maps.newHashMap();
        if (!spaceNameID.containsKey(spaceName)) {
            return result;
        }

        GetTagReq request = new GetTagReq();
        request.setSpace_id(spaceNameID.get(spaceName));
        request.setTag_name(tagName);
        request.setVersion(version);
        GetTagResp response;
        try {
            response = client.getTag(request);
        } catch (TException e) {
            e.printStackTrace();
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
     * @param edgeName  Nebula edge name
     * @return
     */
    public Integer getEdgeTypeFromCache(String spaceName, String edgeName) {
        EdgeItem item = getEdgeItemFromCache(spaceName, edgeName);
        if (Objects.isNull(item)) {
            return -1;
        } else {
            return item.getEdge_type();
        }
    }

    /**
     * Get all edges, store as edgeName : edgeItem in this.edgeItems
     *
     * @param spaceName Nebula space name
     * @return
     */
    @Override
    public List<EdgeItem> getEdges(String spaceName) {
        ListEdgesReq request = new ListEdgesReq();
        int spaceID = getSpaceIDFromCache(spaceName);
        request.setSpace_id(spaceID);

        ListEdgesResp response;
        try {
            response = client.listEdges(request);
        } catch (TException e) {
            LOGGER.error(String.format("Get Edge Error: %s", e.getMessage()));
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
        GetEdgeReq request = new GetEdgeReq();
        int spaceID = getSpaceIDFromCache(spaceName);
        request.setSpace_id(spaceID);
        GetEdgeResp response;

        try {
            response = client.getEdge(request);
        } catch (TException e) {
            LOGGER.error(String.format("Get Tag Error: %s", e.getMessage()));
            return new Schema();
        }

        if (response.getCode() == ErrorCode.SUCCEEDED) {
            return response.getSchema();
        } else {
            return new Schema();
        }
    }

    @Override
    public Map<String, Class> getEdgeSchema(String spaceName, String edgeName, long version) {
        Map<String, Class> result = Maps.newHashMap();
        if (!spaceNameID.containsKey(spaceName)) {
            return result;
        }

        GetEdgeReq request = new GetEdgeReq();
        request.setSpace_id(spaceNameID.get(spaceName));
        request.setEdge_name(edgeName);
        request.setVersion(version);

        GetEdgeResp response;
        try {
            response = client.getEdge(request);
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


    public HostAndPort getLeader() {
        return leader;
    }

}


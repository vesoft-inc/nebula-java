/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.meta.client;

import com.facebook.thrift.TException;
import com.facebook.thrift.protocol.TBinaryProtocol;
import com.facebook.thrift.protocol.TProtocol;
import com.facebook.thrift.transport.TSocket;
import com.facebook.thrift.transport.TTransport;
import com.facebook.thrift.transport.TTransportException;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.net.HostAndPort;
import com.google.common.net.InetAddresses;
import com.vesoft.nebula.HostAddr;
import com.vesoft.nebula.meta.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Nebula Meta Client
 */
public class MetaClientImpl implements MetaClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetaClientImpl.class.getName());

    private MetaService.Client client;

    private TTransport transport = null;

    private final List<HostAndPort> addresses;
    private final int connectionRetry;
    private final int timeout;
    private HostAndPort leader;
    private List<IdName> spaces;
    private Map<String, Integer> spaceNames;
    private Map<Integer, Map<Integer, List<HostAddr>>> parts;
    private Map<Integer, Map<String, TagItem>> tagItems;
    private Map<Integer, Map<String, EdgeItem>> edgeItems;

    public MetaClientImpl(List<HostAndPort> addresses, int timeout, int connectionRetry) {
        com.google.common.base.Preconditions.checkArgument(timeout > 0);
        com.google.common.base.Preconditions.checkArgument(connectionRetry > 0);

        if (addresses.isEmpty()) {
            throw new IllegalArgumentException("No meta server address is specified. Meta server is required");
        }
        addresses.forEach(address -> {
            String host = address.getHost();
            int port = address.getPort();
            if (!InetAddresses.isInetAddress(host) || (port <= 0 || port >= 65535)) {
                throw new IllegalArgumentException(String.format("%s:%d is not a valid address",
                        host, port));
            }
        });
        this.spaces = Lists.newArrayList();
        this.spaceNames = Maps.newHashMap();
        this.parts = Maps.newHashMap();
        this.tagItems = Maps.newHashMap();
        this.edgeItems = Maps.newHashMap();
        this.addresses = addresses;
        this.timeout = timeout;
        this.connectionRetry = connectionRetry;

        this.init();
    }

    public MetaClientImpl(String host, int port) {
        this(Lists.newArrayList(HostAndPort.fromParts(host, port)), DEFAULT_TIMEOUT_MS, DEFAULT_CONNECTION_RETRY_SIZE);
    }

    public MetaClientImpl(List<HostAndPort> addresses) {
        this(addresses, DEFAULT_TIMEOUT_MS, DEFAULT_CONNECTION_RETRY_SIZE);
    }

    /**
     * Get a list of host addresses by a particular space Id
     *
     * @param spaceId
     * @param partId
     * @return
     */
    @Override
    public List<HostAddr> getPart(int spaceId, int partId) {
        if (!this.parts.containsKey(spaceId)) {
            getParts(spaceId);
        }
        Map<Integer, List<HostAddr>> map = parts.get(spaceId);
        if (map == null){
            return null;
        }
        List<HostAddr> addrs = map.get(partId);
        return addrs;
    }

    @Override
    public List<HostAddr> getPart(String spaceName, int partId) {
        if (!spaceNames.containsKey(spaceName)) {
            LOGGER.error("There is no space named: %s", spaceName);
            return null;
        }
        return getPart(spaceNames.get(spaceName), partId);
    }

    /**
     * Get a tag Id by a particular space Id and tag name
     *
     * @param spaceId
     * @param tagName
     * @return
     */
    @Override
    public Integer getTagId(int spaceId, String tagName) {
        if (!this.tagItems.containsKey(spaceId)) {
            getTagItems(spaceId);
        }
        Map<String, TagItem> map = tagItems.get(spaceId);
        if (map.isEmpty()) return null;
        TagItem tag = map.get(tagName);
        return tag == null ? null : tag.getTag_id();
    }

    @Override
    public Integer getTagId(String spaceName, String tagName) {
        if (!spaceNames.containsKey(spaceName)) {
            LOGGER.error("There is no space named: %s", spaceName);
            return null;
        }
        return getTagId(spaceNames.get(spaceName), tagName);
    }

    /**
     * Get a edge type by a particular space Id and edge name
     *
     * @param spaceId
     * @param edgeName
     * @return
     */
    @Override
    public Integer getEdgeType(int spaceId, String edgeName) {
        if (!this.edgeItems.containsKey(spaceId)) {
            getEdgeTypes(spaceId);
        }
        Map<String, EdgeItem> map = edgeItems.get(spaceId);
        if (map.isEmpty()) return null;
        EdgeItem edge = map.get(edgeName);
        return edge == null ? null : edge.getEdge_type();
    }

    @Override
    public Integer getEdgeType(String spaceName, String edgeName) {
        if (!spaceNames.containsKey(spaceName)) {
            LOGGER.error("There is no space named: %s", spaceName);
            return null;
        }
        return getEdgeType(spaceNames.get(spaceName), edgeName);
    }

    private void init() {
        boolean isConnected = connect();
        if (!isConnected) {
            LOGGER.error("Connection has not been established. Connect Failed");
        }
        listSpaces();
        for (IdName space : spaces) {
            int spaceId = space.getId().getSpace_id();
            spaceNames.put(space.getName(), spaceId);
            getParts(spaceId);
            getTagItems(spaceId);
            getEdgeTypes(spaceId);
        }
    }

    private boolean connect() {
        int retry = connectionRetry;
        while (retry-- != 0) {
            Random random = new Random(System.currentTimeMillis());
            int position = random.nextInt(addresses.size());
            HostAndPort address = addresses.get(position);
            transport = new TSocket(address.getHost(), address.getPort(), timeout);
            TProtocol protocol = new TBinaryProtocol(transport);
            try {
                transport.open();
                client = new MetaService.Client(protocol);
                return true;
            } catch (TTransportException tte) {
                LOGGER.error("Connect failed: " + tte.getMessage());
            } catch (TException te) {
                LOGGER.error("Connect failed: " + te.getMessage());
            }
        }
        return false;
    }

    /**
     * Get all spaces and store in this.spaces
     *
     * @return
     */
    private boolean listSpaces() {
        ListSpacesReq request = new ListSpacesReq();
        ListSpacesResp response;
        try {
            response = client.listSpaces(request);
        } catch (TException e) {
            LOGGER.error("List Spaces Error: " + e.getMessage());
            return false;
        }
        if (response.getCode() == ErrorCode.SUCCEEDED) {
            this.spaces = response.getSpaces();
        } else {
            LOGGER.error("Init Error: " + response.getCode());
            return false;
        }
        return true;
    }

    /**
     * Get all parts and the addrs in a space
     * Store in this.parts
     *
     * @param spaceId
     * @return
     */
    private boolean getParts(int spaceId) {
        GetPartsAllocReq request = new GetPartsAllocReq();
        request.setSpace_id(spaceId);

        GetPartsAllocResp response;
        try {
            response = client.getPartsAlloc(request);
        } catch (Exception e) {
            LOGGER.error("Get Parts failed: " + e.getMessage());
            return false;
        }
        if (response.getCode() == ErrorCode.SUCCEEDED) {
            Map<Integer, List<HostAddr>> part = response.getParts();
            this.parts.put(spaceId, part);
        } else {
            LOGGER.error("Get Parts Error: " + response.getCode());
            return false;
        }
        return true;
    }

    /**
     * Get all tags, store as tagName : tagItem in this.tagItems
     *
     * @param spaceId
     * @return
     */
    private boolean getTagItems(int spaceId) {
        ListTagsReq request = new ListTagsReq();
        request.setSpace_id(spaceId);

        ListTagsResp response;
        try {
            response = client.listTags(request);
        } catch (TException e) {
            LOGGER.error("Get Tag Error: " + e.getMessage());
            return false;
        }
        if (response.getCode() == ErrorCode.SUCCEEDED) {
            List<TagItem> tagItem = response.getTags();
            Map<String, TagItem> tmp = new HashMap<>();
            if (tagItem != null) {
                for (TagItem ti : tagItem) {
                    tmp.put(ti.getTag_name(), ti);
                }
                this.tagItems.put(spaceId, tmp);
            }
        } else {
            LOGGER.error("Get tags Error: " + response.getCode());
            return false;
        }
        return true;
    }

    /**
     * Get all edges, store as edgeName : edgeItem in this.edgeItems
     *
     * @param spaceId
     * @return
     */
    private boolean getEdgeTypes(int spaceId) {
        ListEdgesReq request = new ListEdgesReq();
        request.setSpace_id(spaceId);

        ListEdgesResp response;
        try {
            response = client.listEdges(request);
        } catch (TException e) {
            LOGGER.error("Get Edge Error: " + e.getMessage());
            return false;
        }
        if (response.getCode() == ErrorCode.SUCCEEDED) {
            List<EdgeItem> edgeItem = response.getEdges();
            Map<String, EdgeItem> tmp = new HashMap<>();
            if (edgeItem != null) {
                for (EdgeItem ei : edgeItem) {
                    tmp.put(ei.getEdge_name(), ei);
                }
                this.edgeItems.put(spaceId, tmp);
            }
        } else {
            LOGGER.error("Get tags Error: " + response.getCode());
            return false;
        }
        return true;
    }

    public HostAndPort getLeader() {
        return leader;
    }

    public List<IdName> getSpaces() {
        return spaces;
    }

    @Override
    public Map<Integer, Map<Integer, List<HostAddr>>> getParts() {
        return this.parts;
    }

    public void close() throws Exception {

    }
}


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
import com.vesoft.nebula.meta.EdgeItem;
import com.vesoft.nebula.meta.ErrorCode;
import com.vesoft.nebula.meta.GetPartsAllocReq;
import com.vesoft.nebula.meta.GetPartsAllocResp;
import com.vesoft.nebula.meta.IdName;
import com.vesoft.nebula.meta.ListEdgesReq;
import com.vesoft.nebula.meta.ListEdgesResp;
import com.vesoft.nebula.meta.ListSpacesReq;
import com.vesoft.nebula.meta.ListSpacesResp;
import com.vesoft.nebula.meta.ListTagsReq;
import com.vesoft.nebula.meta.ListTagsResp;
import com.vesoft.nebula.meta.MetaService;
import com.vesoft.nebula.meta.TagItem;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Nebula Meta Client
 */
public class MetaClientImpl implements MetaClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetaClientImpl.class);

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
            throw new IllegalArgumentException("No meta server address is specified.");
        }

        for (HostAndPort address : addresses) {
            String host = address.getHost();
            int port = address.getPort();
            if (!InetAddresses.isInetAddress(host) || (port <= 0 || port >= 65535)) {
                throw new IllegalArgumentException(String.format("%s:%d is not a valid address",
                        host, port));
            }
        }

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
        this(Lists.newArrayList(HostAndPort.fromParts(host, port)),
                DEFAULT_TIMEOUT_MS, DEFAULT_CONNECTION_RETRY_SIZE);
    }

    public MetaClientImpl(List<HostAndPort> addresses) {
        this(addresses, DEFAULT_TIMEOUT_MS, DEFAULT_CONNECTION_RETRY_SIZE);
    }

    /**
     * Get a list of host addresses by a particular space Id
     *
     * @param spaceId Nebula space ID
     * @param partId  Nebula partition ID
     * @return
     */
    @Override
    public List<HostAddr> getPart(int spaceId, int partId) {
        if (!this.parts.containsKey(spaceId)) {
            getParts(spaceId);
        }
        Map<Integer, List<HostAddr>> map = parts.get(spaceId);
        if (map == null || map.isEmpty()) {
            return null;
        }
        return map.get(partId);
    }

    @Override
    public List<HostAddr> getPart(String spaceName, int partId) {
        if (!spaceNames.containsKey(spaceName)) {
            LOGGER.error(String.format("There is no space named: %s", spaceName));
            return null;
        }
        return getPart(spaceNames.get(spaceName), partId);
    }

    /**
     * Get a tag Id by a particular space Id and tag name
     *
     * @param spaceId Nebula space ID
     * @param tagName Nebula tag name
     * @return
     */
    @Override
    public Integer getTagId(int spaceId, String tagName) {
        if (!this.tagItems.containsKey(spaceId)) {
            getTagItems(spaceId);
        }

        Map<String, TagItem> map = tagItems.get(spaceId);
        if (map == null || map.isEmpty()) {
            return null;
        }

        TagItem tag = map.get(tagName);
        return tag == null ? -1 : tag.getTag_id();
    }

    @Override
    public Integer getTagId(String spaceName, String tagName) {
        if (!spaceNames.containsKey(spaceName)) {
            LOGGER.error(String.format("There is no space named: %s", spaceName));
            return null;
        }
        return getTagId(spaceNames.get(spaceName), tagName);
    }

    /**
     * Get a edge type by a particular space Id and edge name
     *
     * @param space    Nebula space ID
     * @param edgeName Nebula edge name
     * @return
     */
    @Override
    public Integer getEdgeType(int space, String edgeName) {
        if (!this.edgeItems.containsKey(space)) {
            getEdgeTypes(space);
        }

        Map<String, EdgeItem> map = edgeItems.get(space);
        if (map == null || map.isEmpty()) {
            return null;
        }

        EdgeItem edge = map.get(edgeName);
        return edge == null ? -1 : edge.getEdge_type();
    }

    @Override
    public Integer getEdgeType(String spaceName, String edgeName) {
        if (!spaceNames.containsKey(spaceName)) {
            LOGGER.error(String.format("There is no space named: %s", spaceName));
            return null;
        }
        return getEdgeType(spaceNames.get(spaceName), edgeName);
    }

    public void init() {
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
            LOGGER.error(String.format("List Spaces Error: %s", e.getMessage()));
            return false;
        }
        if (response.getCode() == ErrorCode.SUCCEEDED) {
            this.spaces = response.getSpaces();
        } else {
            LOGGER.error(String.format("Init Error: %s", response.getCode()));
            return false;
        }
        return true;
    }

    @Override
    public Map<Integer, Map<Integer, List<HostAddr>>> getParts() {
        return this.parts;
    }

    /**
     * Get all parts and the addrs in a space
     * Store in this.parts
     *
     * @param spaceId Nebula space ID
     * @return
     */
    private boolean getParts(int spaceId) {
        GetPartsAllocReq request = new GetPartsAllocReq();
        request.setSpace_id(spaceId);

        GetPartsAllocResp response;
        try {
            response = client.getPartsAlloc(request);
        } catch (TException e) {
            LOGGER.error(String.format("Get Parts failed: %s", e.getMessage()));
            return false;
        }

        if (response.getCode() == ErrorCode.SUCCEEDED) {
            Map<Integer, List<HostAddr>> part = response.getParts();
            this.parts.put(spaceId, part);
        } else {
            LOGGER.error(String.format("Get Parts Error: %s", response.getCode()));
            return false;
        }
        return true;
    }

    /**
     * Get all tags, store as tagName : tagItem in this.tagItems
     *
     * @param spaceId Nebula space ID
     * @return
     */
    private boolean getTagItems(int spaceId) {
        ListTagsReq request = new ListTagsReq();
        request.setSpace_id(spaceId);

        ListTagsResp response;
        try {
            response = client.listTags(request);
        } catch (TException e) {
            LOGGER.error(String.format("Get Tag Error: %s", e.getMessage()));
            return false;
        }
        if (response.getCode() == ErrorCode.SUCCEEDED) {
            List<TagItem> tagItem = response.getTags();
            Map<String, TagItem> tmp = Maps.newHashMap();
            if (tagItem != null) {
                for (TagItem ti : tagItem) {
                    tmp.put(ti.getTag_name(), ti);
                }
                this.tagItems.put(spaceId, tmp);
            }
        } else {
            LOGGER.error(String.format("Get tags Error: %s", response.getCode()));
            return false;
        }
        return true;
    }

    /**
     * Get all edges, store as edgeName : edgeItem in this.edgeItems
     *
     * @param spaceId Nebula space ID
     * @return
     */
    private boolean getEdgeTypes(int spaceId) {
        ListEdgesReq request = new ListEdgesReq();
        request.setSpace_id(spaceId);

        ListEdgesResp response;
        try {
            response = client.listEdges(request);
        } catch (TException e) {
            LOGGER.error(String.format("Get Edge Error: %s", e.getMessage()));
            return false;
        }

        if (response.getCode() == ErrorCode.SUCCEEDED) {
            List<EdgeItem> edgeItem = response.getEdges();
            Map<String, EdgeItem> tmp = Maps.newHashMap();
            if (edgeItem != null) {
                for (EdgeItem ei : edgeItem) {
                    tmp.put(ei.getEdge_name(), ei);
                }
                this.edgeItems.put(spaceId, tmp);
            }
        } else {
            LOGGER.error(String.format("Get tags Error: %s", response.getCode()));
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

    public void close() {
        transport.close();
    }
}


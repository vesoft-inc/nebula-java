/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.meta;

import com.facebook.thrift.TException;
import com.facebook.thrift.transport.TTransport;
import com.google.common.collect.Maps;
import com.google.common.net.HostAndPort;
import com.vesoft.nebula.ColumnDef;
import com.vesoft.nebula.HostAddr;
import com.vesoft.nebula.meta.EdgeItem;
import com.vesoft.nebula.meta.ErrorCode;
import com.vesoft.nebula.meta.GetEdgeReq;
import com.vesoft.nebula.meta.GetEdgeResp;
import com.vesoft.nebula.meta.GetPartsAllocReq;
import com.vesoft.nebula.meta.GetPartsAllocResp;
import com.vesoft.nebula.meta.GetTagReq;
import com.vesoft.nebula.meta.GetTagResp;
import com.vesoft.nebula.meta.IdName;
import com.vesoft.nebula.meta.ListEdgesReq;
import com.vesoft.nebula.meta.ListEdgesResp;
import com.vesoft.nebula.meta.ListSpacesReq;
import com.vesoft.nebula.meta.ListSpacesResp;
import com.vesoft.nebula.meta.ListTagsReq;
import com.vesoft.nebula.meta.ListTagsResp;
import com.vesoft.nebula.meta.MetaService;
import com.vesoft.nebula.meta.TagItem;
import com.vesoft.nebula.utils.NebulaTypeUtil;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Nebula Meta Client
 */
public class MetaClientImpl extends MetaClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetaClientImpl.class);

    private MetaService.Client client;

    private TTransport transport = null;
    private HostAndPort leader;
    private List<IdName> spaces;
    private Map<String, Integer> spaceNames;
    private Map<String, Map<Integer, List<HostAddr>>> parts;
    private Map<String, Map<String, TagItem>> tagItems;
    private Map<String, Map<String, EdgeItem>> edgeItems;

    private static final int LATEST_TAG_VERSION = -1;
    private static final int LATEST_EDGE_VERSION = -1;

    public MetaClientImpl(List<HostAndPort> addresses, int timeout,
                           int connectionRetry, int executionRetry) {
        super(addresses, timeout, connectionRetry, executionRetry);
    }

    public MetaClientImpl(List<HostAndPort> addresses) {
        super(addresses);
    }

    public MetaClientImpl(String host, int port) {
        super(host, port);
    }

    /**
     * Get a list of host addresses by a particular space Id
     *
     * @param spaceName Nebula space name
     * @param partId    Nebula partition ID
     * @return
     */
    @Override
    public List<HostAddr> getPart(String spaceName, int partId) {
        if (!this.parts.containsKey(spaceName)) {
            getParts(spaceName);
        }

        Map<Integer, List<HostAddr>> map = parts.get(spaceName);
        if (map == null || map.isEmpty()) {
            return null;
        }
        return map.get(partId);
    }


    /**
     * Get a tag Id by a particular space Id and tag name
     *
     * @param spaceName Nebula space name
     * @param tagName   Nebula tag name
     * @return
     */
    @Override
    public Integer getTagId(String spaceName, String tagName) {
        if (!this.tagItems.containsKey(spaceName)) {
            getTagItems(spaceName);
        }

        Map<String, TagItem> map = tagItems.get(spaceName);
        if (map == null || map.isEmpty()) {
            return null;
        }

        TagItem tag = map.get(tagName);
        return tag == null ? -1 : tag.getTag_id();
    }

    /**
     * Get a edge type by a particular space Id and edge name
     *
     * @param space    Nebula space ID
     * @param edgeName Nebula edge name
     * @return
     */
    @Override
    public Integer getEdgeType(String space, String edgeName) {
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
    public Map<String, Class> getTagSchema(String spaceName, String tagName, long version) {
        Map<String, Class> result = Maps.newHashMap();
        if (!spaceNames.containsKey(spaceName)) {
            return result;
        }

        GetTagReq request = new GetTagReq();
        request.setSpace_id(spaceNames.get(spaceName));
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
        return getTagSchema(spaceName, tagName, LATEST_TAG_VERSION);
    }

    @Override
    public Map<String, Class> getEdgeSchema(String spaceName, String edgeName, long version) {
        Map<String, Class> result = Maps.newHashMap();
        if (!spaceNames.containsKey(spaceName)) {
            return result;
        }

        GetEdgeReq request = new GetEdgeReq();
        request.setSpace_id(spaceNames.get(spaceName));
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
        return getEdgeSchema(spaceName, edgeName, LATEST_EDGE_VERSION);
    }

    public void init() {
        int isConnected = -1;
        try {
            isConnected = connect();
        } catch (TException e) {
            e.printStackTrace();
        }

        if (isConnected != 0) {
            LOGGER.error("Connection has not been established. Connect Failed");
        }

        this.spaces = listSpaces();
        for (IdName space : spaces) {
            int spaceId = space.getId().getSpace_id();
            spaceNames.put(space.getName(), spaceId);
            getParts(space.getName());
            getTagItems(space.getName());
            getEdgeTypes(space.getName());
        }
    }


    @Override
    public int doConnect(HostAndPort address) throws TException {
        return 0;
    }

    @Override
    public boolean isConnected() {
        return transport.isOpen();
    }

    /**
     * Get all spaces and store in this.spaces
     *
     * @return
     */
    public List<IdName> listSpaces() {
        ListSpacesReq request = new ListSpacesReq();
        ListSpacesResp response;
        try {
            response = client.listSpaces(request);
        } catch (TException e) {
            LOGGER.error(String.format("List Spaces Error: %s", e.getMessage()));
            return null;
        }
        if (response.getCode() == ErrorCode.SUCCEEDED) {
            return response.getSpaces();
        } else {
            LOGGER.error(String.format("List Spaces Error Code: %d", response.getCode()));
            return null;
        }
    }

    @Override
    public int getSpaceIDFromCache(String name) {
        return 0;
    }

    @Override
    public Map<String, Map<Integer, List<HostAddr>>> getParts() {
        return this.parts;
    }

    /**
     * Get all parts and the addrs in a space
     * Store in this.parts
     *
     * @param spaceName Nebula space name
     * @return
     */
    private boolean getParts(String spaceName) {
        GetPartsAllocReq request = new GetPartsAllocReq();
        int spaceID = getSpaceIDFromCache(spaceName);
        request.setSpace_id(spaceID);

        GetPartsAllocResp response;
        try {
            response = client.getPartsAlloc(request);
        } catch (TException e) {
            LOGGER.error(String.format("Get Parts failed: %s", e.getMessage()));
            return false;
        }

        if (response.getCode() == ErrorCode.SUCCEEDED) {
            Map<Integer, List<HostAddr>> part = response.getParts();
            this.parts.put(spaceName, part);
        } else {
            LOGGER.error(String.format("Get Parts Error: %s", response.getCode()));
            return false;
        }
        return true;
    }

    /**
     * Get all tags, store as tagName : tagItem in this.tagItems
     *
     * @param spaceName Nebula space name
     * @return
     */
    private boolean getTagItems(String spaceName) {
        ListTagsReq request = new ListTagsReq();
        int spaceID = getSpaceIDFromCache(spaceName);
        request.setSpace_id(spaceID);

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
                this.tagItems.put(spaceName, tmp);
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
     * @param spaceName Nebula space name
     * @return
     */
    private boolean getEdgeTypes(String spaceName) {
        ListEdgesReq request = new ListEdgesReq();
        int spaceID = getSpaceIDFromCache(spaceName);
        request.setSpace_id(spaceID);

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
                this.edgeItems.put(spaceName, tmp);
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


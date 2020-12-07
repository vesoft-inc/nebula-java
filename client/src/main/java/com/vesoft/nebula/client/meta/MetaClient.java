/* Copyright (c) 2020 vesoft inc. All rights reserved.
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
import com.vesoft.nebula.HostAddr;
import com.vesoft.nebula.client.meta.exception.ExecuteFailedException;
import com.vesoft.nebula.meta.EdgeItem;
import com.vesoft.nebula.meta.ErrorCode;
import com.vesoft.nebula.meta.GetEdgeReq;
import com.vesoft.nebula.meta.GetEdgeResp;
import com.vesoft.nebula.meta.GetPartsAllocReq;
import com.vesoft.nebula.meta.GetPartsAllocResp;
import com.vesoft.nebula.meta.GetSpaceReq;
import com.vesoft.nebula.meta.GetSpaceResp;
import com.vesoft.nebula.meta.GetTagReq;
import com.vesoft.nebula.meta.GetTagResp;
import com.vesoft.nebula.meta.HostItem;
import com.vesoft.nebula.meta.IdName;
import com.vesoft.nebula.meta.ListEdgesReq;
import com.vesoft.nebula.meta.ListEdgesResp;
import com.vesoft.nebula.meta.ListHostsReq;
import com.vesoft.nebula.meta.ListHostsResp;
import com.vesoft.nebula.meta.ListSpacesReq;
import com.vesoft.nebula.meta.ListSpacesResp;
import com.vesoft.nebula.meta.ListTagsReq;
import com.vesoft.nebula.meta.ListTagsResp;
import com.vesoft.nebula.meta.MetaService;
import com.vesoft.nebula.meta.Schema;
import com.vesoft.nebula.meta.SpaceItem;
import com.vesoft.nebula.meta.TagItem;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetaClient extends AbstractMetaClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetaClient.class);

    public static final int LATEST_SCHEMA_VERSION = -1;

    private static final int DEFAULT_TIMEOUT_MS = 1000;
    private static final int DEFAULT_CONNECTION_RETRY_SIZE = 3;
    private static final int DEFAULT_EXECUTION_RETRY_SIZE = 3;

    // todo change client to Map<HostAndPost, MetaService.Client> when server changes
    private MetaService.Client client;
    private final List<HostAndPort> addresses;

    public MetaClient(String host, int port) {
        this(HostAndPort.fromParts(host, port));
    }

    public MetaClient(String address) {
        this(HostAndPort.fromString(address));
    }

    public MetaClient(HostAndPort address) {
        this(Arrays.asList(address), DEFAULT_CONNECTION_RETRY_SIZE, DEFAULT_EXECUTION_RETRY_SIZE);
    }

    public MetaClient(List<HostAndPort> addresses) {
        this(addresses, DEFAULT_CONNECTION_RETRY_SIZE, DEFAULT_EXECUTION_RETRY_SIZE);
    }

    public MetaClient(List<HostAndPort> addresses, int connectionRetry, int executionRetry) {
        this(addresses, DEFAULT_TIMEOUT_MS, connectionRetry, executionRetry);
    }

    public MetaClient(List<HostAndPort> addresses, int timeout, int connectionRetry,
                      int executionRetry) {
        super(addresses, timeout, connectionRetry, executionRetry);
        this.addresses = addresses;
    }

    public void connect() throws TException {
        doConnect();
    }

    /**
     * connect nebula meta server
     */
    private void doConnect() throws TException {
        Random random = new Random(System.currentTimeMillis());
        int position = random.nextInt(addresses.size());
        HostAndPort address = addresses.get(position);
        transport = new TSocket(address.getHostText(), address.getPort(), timeout, timeout);
        transport.open();
        protocol = new TCompactProtocol(transport);
        client = new MetaService.Client(protocol);
    }

    /**
     * close transport
     */
    public void close() {
        if (transport != null && transport.isOpen()) {
            transport.close();
        }
    }

    /**
     * get all spaces
     *
     * @return
     */
    public List<IdName> getSpaces() throws TException {
        ListSpacesReq request = new ListSpacesReq();
        ListSpacesResp response = client.listSpaces(request);
        return response.getSpaces();
    }

    /**
     * get one space
     *
     * @param spaceName nebula graph space
     * @return SpaceItem
     */
    public SpaceItem getSpace(String spaceName) throws TException {
        GetSpaceReq request = new GetSpaceReq();
        request.setSpace_name(spaceName.getBytes());
        GetSpaceResp response = client.getSpace(request);
        return response.getItem();
    }

    /**
     * get all tags of spaceName
     *
     * @param spaceName nebula graph space
     * @return TagItem list
     */
    public List<TagItem> getTags(String spaceName) throws TException, ExecuteFailedException {

        int spaceID = getSpace(spaceName).space_id;
        ListTagsReq request = new ListTagsReq(spaceID);
        ListTagsResp response;
        try {
            response = client.listTags(request);
        } catch (TException e) {
            LOGGER.error(String.format("Get Tag Error: %s", e.getMessage()));
            throw e;
        }

        if (response.getCode() == ErrorCode.SUCCEEDED) {
            return response.getTags();
        } else {
            LOGGER.error(String.format("Get tags Error: %s", response.getCode()));
            throw new ExecuteFailedException("Get Tags Error:"
                    + ErrorCode.VALUES_TO_NAMES.get(response.getCode()));
        }
    }


    /**
     * get schema of specific tag
     *
     * @param spaceName nebula graph space
     * @param tagName   nebula tag name
     * @return Schema
     */
    public Schema getTag(String spaceName, String tagName)
            throws TException, ExecuteFailedException {
        GetTagReq request = new GetTagReq();
        int spaceID = getSpace(spaceName).getSpace_id();
        request.setSpace_id(spaceID);
        request.setTag_name(tagName.getBytes());
        request.setVersion(LATEST_SCHEMA_VERSION);
        GetTagResp response;

        try {
            response = client.getTag(request);
        } catch (TException e) {
            LOGGER.error(String.format("Get Tag Error: %s", e.getMessage()));
            throw e;
        }

        if (response.getCode() == ErrorCode.SUCCEEDED) {
            return response.getSchema();
        } else {
            LOGGER.error(String.format(
                    "Get tag execute Error: %s",
                    ErrorCode.VALUES_TO_NAMES.get(response.getCode())));
            throw new ExecuteFailedException("Get tag execute Error: "
                    + ErrorCode.VALUES_TO_NAMES.get(response.getCode()));
        }
    }


    /**
     * get all edges of specific space
     *
     * @param spaceName nebula graph space
     * @return EdgeItem list
     */
    public List<EdgeItem> getEdges(String spaceName) throws TException, ExecuteFailedException {
        int spaceID = getSpace(spaceName).getSpace_id();
        ListEdgesReq request = new ListEdgesReq(spaceID);
        ListEdgesResp response;
        try {
            response = client.listEdges(request);
        } catch (TException e) {
            LOGGER.error(String.format("Get Tag Error: %s", e.getMessage()));
            throw e;
        }

        if (response.getCode() == ErrorCode.SUCCEEDED) {
            return response.getEdges();
        } else {
            LOGGER.error(String.format("Get tags Error: %s", response.getCode()));
            throw new ExecuteFailedException("Get Edges Error:"
                    + ErrorCode.VALUES_TO_NAMES.get(response.getCode()));
        }
    }

    /**
     * get schema of specific edgeRow
     *
     * @param spaceName nebula graph space
     * @param edgeName  nebula edgeRow name
     * @return Schema
     */
    public Schema getEdge(String spaceName, String edgeName)
            throws TException, ExecuteFailedException {
        GetEdgeReq request = new GetEdgeReq();
        int spaceID = getSpace(spaceName).getSpace_id();
        request.setSpace_id(spaceID);
        request.setEdge_name(edgeName.getBytes());
        request.setVersion(LATEST_SCHEMA_VERSION);
        GetEdgeResp response;

        try {
            response = client.getEdge(request);
        } catch (TException e) {
            LOGGER.error(String.format("Get Tag Error: %s", e.getMessage()));
            throw e;
        }

        if (response.getCode() == ErrorCode.SUCCEEDED) {
            return response.getSchema();
        } else {
            LOGGER.error(String.format(
                    "Get Edge execute Error: %s",
                    ErrorCode.VALUES_TO_NAMES.get(response.getCode())));
            throw new ExecuteFailedException(
                    "Get Edge execute Error: "
                            + ErrorCode.VALUES_TO_NAMES.get(response.getCode()));
        }
    }


    /**
     * Get all parts and the address in a space
     * Store in this.parts
     *
     * @param spaceName Nebula space name
     * @return
     */
    public Map<Integer, List<HostAndPort>> getPartsAlloc(String spaceName)
            throws ExecuteFailedException, TException {
        GetPartsAllocReq request = new GetPartsAllocReq();
        int spaceID = getSpace(spaceName).getSpace_id();
        request.setSpace_id(spaceID);

        GetPartsAllocResp response;
        try {
            response = client.getPartsAlloc(request);
        } catch (TException e) {
            LOGGER.error(String.format("Get Parts failed: %s", e.getMessage()));
            throw e;
        }

        if (response.getCode() == ErrorCode.SUCCEEDED) {
            Map<Integer, List<HostAndPort>> addressMap = Maps.newHashMap();
            for (Map.Entry<Integer, List<HostAddr>> entry : response.getParts().entrySet()) {
                List<HostAndPort> addresses = Lists.newLinkedList();
                for (HostAddr address : entry.getValue()) {
                    HostAndPort pair = HostAndPort.fromParts(address.getHost(), address.getPort());
                    addresses.add(pair);
                }
                addressMap.put(entry.getKey(), addresses);
            }
            return addressMap;
        } else {
            LOGGER.error(String.format("Get Parts Error: %s", response.getCode()));
            throw new ExecuteFailedException("Get Parts allocation failed: "
                    + ErrorCode.VALUES_TO_NAMES.get(response.getCode()));
        }
    }

    /**
     * get all spaces info, used by {@link MetaManager}
     *
     * @return empty list if exception happen
     */
    protected List<IdName> listSpaces() {
        List<IdName> response;
        try {
            response = getSpaces();
        } catch (TException e) {
            LOGGER.error(String.format("List Spaces Error: %s", e.getMessage()), e);
            return Lists.newLinkedList();
        }
        if (response == null) {
            return Lists.newLinkedList();
        }
        return response;
    }


    /**
     * method for fill meta cache
     *
     * @param spaceName nebula graph space
     * @return empty map if exception happen
     */
    protected Map<Integer, List<HostAndPort>> getPartsLocation(String spaceName) {
        Map<Integer, List<HostAndPort>> result;
        try {
            result = getPartsAlloc(spaceName);
        } catch (ExecuteFailedException | TException e) {
            LOGGER.error("getPartsAlloc error, ", e);
            return Maps.newHashMap();
        }
        return result;
    }

    /**
     * method for fill meta cache
     *
     * @param spaceName nebula graph space
     * @return empty list if exception happen
     */
    protected List<TagItem> listTags(String spaceName) {
        List<TagItem> tagItems;
        try {
            tagItems = getTags(spaceName);
        } catch (TException | ExecuteFailedException e) {
            LOGGER.error("getTags error, ", e);
            return Lists.newLinkedList();
        }
        return tagItems;
    }

    /**
     * method for fill meta cache
     *
     * @param spaceName nebula graph space
     * @return empty list if exception happen
     */
    protected List<EdgeItem> listEdges(String spaceName) {
        List<EdgeItem> edgeItems;
        try {
            edgeItems = getEdges(spaceName);
        } catch (TException | ExecuteFailedException e) {
            LOGGER.error("getEdges error, ", e);
            return Lists.newLinkedList();
        }
        return edgeItems;
    }

    /**
     * check if space exist
     */
    protected boolean existSpace(String spaceName) {
        List<IdName> spaces = listSpaces();
        for (IdName space : spaces) {
            if (new String(space.getName()).equals(spaceName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * check if tag exist
     */
    protected boolean existTag(String spaceName, String tag) {
        List<TagItem> tags;
        try {
            tags = getTags(spaceName);
        } catch (Exception e) {
            LOGGER.error("failed to get tags", e);
            return false;
        }
        for (TagItem item : tags) {
            if (new String(item.getTag_name()).equals(tag)) {
                return true;
            }
        }
        return false;
    }

    /**
     * check if edgeRow exist
     */
    protected boolean existEdge(String spaceName, String edgeRow) {
        List<EdgeItem> edges;
        try {
            edges = getEdges(spaceName);
        } catch (Exception e) {
            LOGGER.error("failed to get edges", e);
            return false;
        }
        for (EdgeItem item : edges) {
            if (new String(item.getEdge_name()).equals(edgeRow)) {
                return true;
            }
        }
        return false;
    }


    /**
     * get all servers
     */
    public Set<HostAndPort> listHosts() {
        ListHostsReq request = new ListHostsReq();
        // todo request.setType();
        ListHostsResp resp;
        try {
            resp = client.listHosts(request);
        } catch (TException e) {
            LOGGER.error("listHosts error", e);
            return null;
        }
        Set<HostAndPort> hostAndPorts = new HashSet<>();
        for (HostItem hostItem : resp.hosts) {
            HostAddr addr = hostItem.getHostAddr();
            hostAndPorts.add(HostAndPort.fromParts(addr.getHost(), addr.getPort()));
        }
        return hostAndPorts;
    }
}

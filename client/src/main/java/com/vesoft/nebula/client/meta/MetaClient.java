/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.client.meta;

import com.facebook.thrift.TException;
import com.facebook.thrift.protocol.TCompactProtocol;
import com.facebook.thrift.protocol.THeaderProtocol;
import com.facebook.thrift.transport.THeaderTransport;
import com.facebook.thrift.transport.TSocket;
import com.facebook.thrift.transport.TTransportException;
import com.google.common.base.Charsets;
import com.vesoft.nebula.ErrorCode;
import com.vesoft.nebula.HostAddr;
import com.vesoft.nebula.client.graph.data.CASignedSSLParam;
import com.vesoft.nebula.client.graph.data.HostAddress;
import com.vesoft.nebula.client.graph.data.SSLParam;
import com.vesoft.nebula.client.graph.data.SelfSignedSSLParam;
import com.vesoft.nebula.client.graph.exception.ClientServerIncompatibleException;
import com.vesoft.nebula.client.graph.exception.IOErrorException;
import com.vesoft.nebula.client.meta.exception.ExecuteFailedException;
import com.vesoft.nebula.meta.EdgeItem;
import com.vesoft.nebula.meta.GetEdgeReq;
import com.vesoft.nebula.meta.GetEdgeResp;
import com.vesoft.nebula.meta.GetPartsAllocReq;
import com.vesoft.nebula.meta.GetPartsAllocResp;
import com.vesoft.nebula.meta.GetSpaceReq;
import com.vesoft.nebula.meta.GetSpaceResp;
import com.vesoft.nebula.meta.GetTagReq;
import com.vesoft.nebula.meta.GetTagResp;
import com.vesoft.nebula.meta.HostItem;
import com.vesoft.nebula.meta.HostStatus;
import com.vesoft.nebula.meta.IdName;
import com.vesoft.nebula.meta.ListEdgesReq;
import com.vesoft.nebula.meta.ListEdgesResp;
import com.vesoft.nebula.meta.ListHostType;
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
import com.vesoft.nebula.meta.VerifyClientVersionReq;
import com.vesoft.nebula.meta.VerifyClientVersionResp;
import com.vesoft.nebula.util.SslUtil;
import java.io.IOException;
import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.net.ssl.SSLSocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetaClient extends AbstractMetaClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetaClient.class);

    public static final int LATEST_SCHEMA_VERSION = -1;

    private static final int DEFAULT_TIMEOUT_MS = 1000;
    private static final int DEFAULT_CONNECTION_RETRY_SIZE = 3;
    private static final int DEFAULT_EXECUTION_RETRY_SIZE = 3;
    private static final int RETRY_TIMES = 1;

    private boolean enableSSL = false;
    private SSLParam sslParam = null;

    private MetaService.Client client;
    private final List<HostAddress> addresses;

    private String version = null;

    public MetaClient(String host, int port) throws UnknownHostException {
        this(new HostAddress(host, port));
    }

    public MetaClient(HostAddress address) throws UnknownHostException {
        this(Arrays.asList(address), DEFAULT_CONNECTION_RETRY_SIZE, DEFAULT_EXECUTION_RETRY_SIZE);
    }

    public MetaClient(List<HostAddress> addresses) throws UnknownHostException {
        this(addresses, DEFAULT_CONNECTION_RETRY_SIZE, DEFAULT_EXECUTION_RETRY_SIZE);
    }

    public MetaClient(List<HostAddress> addresses, int connectionRetry, int executionRetry)
            throws UnknownHostException {
        this(addresses, DEFAULT_TIMEOUT_MS, connectionRetry, executionRetry);
    }

    public MetaClient(List<HostAddress> addresses, int timeout, int connectionRetry,
                      int executionRetry) throws UnknownHostException {
        super(addresses, timeout, connectionRetry, executionRetry);
        this.addresses = addresses;
    }

    public MetaClient(List<HostAddress> addresses, int timeout, int connectionRetry,
                      int executionRetry, boolean enableSSL, SSLParam sslParam)
            throws UnknownHostException {
        super(addresses, timeout, connectionRetry, executionRetry);
        this.addresses = addresses;
        this.enableSSL = enableSSL;
        this.sslParam = sslParam;
        if (enableSSL && sslParam == null) {
            throw new IllegalArgumentException("SSL is enabled, but SSLParam is null.");
        }
    }

    /**
     * set the version info for MetaClient
     *
     * @param version version info
     * @return MetaClient
     */
    public MetaClient setVersion(String version) {
        this.version = version;
        return this;
    }

    public void connect()
            throws TException, ClientServerIncompatibleException {
        doConnect();
    }

    /**
     * connect nebula meta server
     */
    private void doConnect()
            throws TTransportException, ClientServerIncompatibleException {
        Random random = new Random(System.currentTimeMillis());
        int position = random.nextInt(addresses.size());
        HostAddress address = addresses.get(position);
        getClient(address.getHost(), address.getPort());
    }

    private void getClient(String host, int port)
            throws TTransportException, ClientServerIncompatibleException {
        if (enableSSL) {
            SSLSocketFactory sslSocketFactory;
            if (sslParam.getSignMode() == SSLParam.SignMode.CA_SIGNED) {
                sslSocketFactory = SslUtil.getSSLSocketFactoryWithCA((CASignedSSLParam) sslParam);
            } else {
                sslSocketFactory =
                        SslUtil.getSSLSocketFactoryWithoutCA((SelfSignedSSLParam) sslParam);
            }
            try {
                transport = new THeaderTransport(
                        new TSocket(sslSocketFactory.createSocket(host, port), timeout, timeout));
            } catch (IOException e) {
                throw new TTransportException(IOErrorException.E_UNKNOWN, e);
            }
        } else {
            transport = new THeaderTransport(new TSocket(host, port, timeout, timeout));
            transport.open();
        }

        protocol = new THeaderProtocol(transport);
        client = new MetaService.Client(protocol);

        // check if client version matches server version
        VerifyClientVersionReq verifyClientVersionReq = new VerifyClientVersionReq();
        if (version != null) {
            verifyClientVersionReq.setClient_version(version.getBytes(Charsets.UTF_8));
        }
        VerifyClientVersionResp resp = client.verifyClientVersion(verifyClientVersionReq);
        if (resp.getCode() != ErrorCode.SUCCEEDED) {
            client.getInputProtocol().getTransport().close();
            if (resp.getError_msg() == null) {
                throw new ClientServerIncompatibleException(
                        new String("Error code: ")
                                + String.valueOf(resp.getCode().getValue()));
            }
            throw new ClientServerIncompatibleException(new String(resp.getError_msg()));
        }
    }

    private void freshClient(HostAddr leader) throws TTransportException {
        close();
        try {
            if (leader.getHost() == null || "".equals(leader.getHost())) {
                doConnect();
            } else {
                getClient(leader.getHost(), leader.getPort());
            }
        } catch (ClientServerIncompatibleException e) {
            LOGGER.error(e.getMessage());
        }
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
    public synchronized List<IdName> getSpaces() throws TException, ExecuteFailedException {
        int retry = RETRY_TIMES;
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
            throw e;
        }
        if (response.getCode() == ErrorCode.SUCCEEDED) {
            return response.getSpaces();
        } else {
            LOGGER.error("Get Spaces execute failed, errorCode: " + response.getCode());
            throw new ExecuteFailedException(
                    "Get Spaces execute failed, errorCode: " + response.getCode());
        }
    }

    /**
     * get one space
     *
     * @param spaceName nebula graph space
     * @return SpaceItem
     */
    public synchronized SpaceItem getSpace(String spaceName) throws TException,
            ExecuteFailedException {
        int retry = RETRY_TIMES;
        GetSpaceReq request = new GetSpaceReq();
        request.setSpace_name(spaceName.getBytes());
        GetSpaceResp response = null;
        try {
            while (retry-- >= 0) {
                response = client.getSpace(request);
                if (response.getCode() == ErrorCode.E_LEADER_CHANGED) {
                    freshClient(response.getLeader());
                } else {
                    break;
                }
            }
        } catch (TException e) {
            LOGGER.error(String.format("Get Space Error: %s", e.getMessage()));
            throw e;
        }
        if (response.getCode() == ErrorCode.SUCCEEDED) {
            return response.getItem();
        } else {
            LOGGER.error("Get Space execute failed, errorCode: " + response.getCode());
            throw new ExecuteFailedException(
                    "Get Space execute failed, errorCode: " + response.getCode());
        }
    }

    /**
     * get all tags of spaceName
     *
     * @param spaceName nebula graph space
     * @return TagItem list
     */
    public synchronized List<TagItem> getTags(String spaceName)
            throws TException, ExecuteFailedException {
        int retry = RETRY_TIMES;

        int spaceID = getSpace(spaceName).space_id;
        ListTagsReq request = new ListTagsReq(spaceID);
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
            throw e;
        }
        if (response.getCode() == ErrorCode.SUCCEEDED) {
            return response.getTags();
        } else {
            LOGGER.error("Get tags execute failed, errorCode: " + response.getCode());
            throw new ExecuteFailedException(
                    "Get Tags execute failed, errorCode: " + response.getCode());
        }
    }


    /**
     * get schema of specific tag
     *
     * @param spaceName nebula graph space
     * @param tagName   nebula tag name
     * @return Schema
     */
    public synchronized Schema getTag(String spaceName, String tagName)
            throws TException, ExecuteFailedException {
        int retry = RETRY_TIMES;
        GetTagReq request = new GetTagReq();
        int spaceID = getSpace(spaceName).getSpace_id();
        request.setSpace_id(spaceID);
        request.setTag_name(tagName.getBytes());
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
            throw e;
        }
        if (response.getCode() == ErrorCode.SUCCEEDED) {
            return response.getSchema();
        } else {
            LOGGER.error("Get tag execute failed, errorCode: " + response.getCode());
            throw new ExecuteFailedException(
                    "Get tag execute failed, errorCode: " + response.getCode());
        }
    }


    /**
     * get all edges of specific space
     *
     * @param spaceName nebula graph space
     * @return EdgeItem list
     */
    public synchronized List<EdgeItem> getEdges(String spaceName)
            throws TException, ExecuteFailedException {
        int retry = RETRY_TIMES;
        int spaceID = getSpace(spaceName).getSpace_id();
        ListEdgesReq request = new ListEdgesReq(spaceID);
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
            throw e;
        }
        if (response.getCode() == ErrorCode.SUCCEEDED) {
            return response.getEdges();
        } else {
            LOGGER.error("Get edges execute failed: errorCode: " + response.getCode());
            throw new ExecuteFailedException(
                    "Get execute edges failed, errorCode: " + response.getCode());
        }
    }

    /**
     * get schema of specific edgeRow
     *
     * @param spaceName nebula graph space
     * @param edgeName  nebula edgeRow name
     * @return Schema
     */
    public synchronized Schema getEdge(String spaceName, String edgeName)
            throws TException, ExecuteFailedException {
        int retry = RETRY_TIMES;
        GetEdgeReq request = new GetEdgeReq();
        int spaceID = getSpace(spaceName).getSpace_id();
        request.setSpace_id(spaceID);
        request.setEdge_name(edgeName.getBytes());
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
            throw e;
        }
        if (response.getCode() == ErrorCode.SUCCEEDED) {
            return response.getSchema();
        } else {
            LOGGER.error("Get Edge execute failed, errorCode: " + response.getCode());
            throw new ExecuteFailedException(
                    "Get Edge execute failed, errorCode: " + response.getCode());
        }
    }


    /**
     * Get all parts and the address in a space
     * Store in this.parts
     *
     * @param spaceName Nebula space name
     * @return
     */
    public synchronized Map<Integer, List<HostAddr>> getPartsAlloc(String spaceName)
            throws ExecuteFailedException, TException {
        int retry = RETRY_TIMES;
        GetPartsAllocReq request = new GetPartsAllocReq();
        int spaceID = getSpace(spaceName).getSpace_id();
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
            LOGGER.error(String.format("Get Parts Error: %s", e.getMessage()));
            throw e;
        }
        if (response.getCode() == ErrorCode.SUCCEEDED) {
            return response.getParts();
        } else {
            LOGGER.error("Get Parts execute failed, errorCode" + response.getCode());
            throw new ExecuteFailedException(
                    "Get Parts execute failed, errorCode" + response.getCode());
        }
    }

    /**
     * get all Storaged servers
     */
    public synchronized Set<HostAddr> listHosts() {
        int retry = RETRY_TIMES;
        ListHostsReq request = new ListHostsReq();
        request.setType(ListHostType.STORAGE);
        ListHostsResp resp = null;
        try {
            while (retry-- >= 0) {
                resp = client.listHosts(request);
                if (resp.getCode() == ErrorCode.E_LEADER_CHANGED) {
                    freshClient(resp.getLeader());
                } else {
                    break;
                }
            }
        } catch (TException e) {
            LOGGER.error("listHosts error", e);
            return null;
        }
        if (resp.getCode() != ErrorCode.SUCCEEDED) {
            LOGGER.error("listHosts execute failed, errorCode: " + resp.getCode());
            return null;
        }
        Set<HostAddr> hostAddrs = new HashSet<>();
        for (HostItem hostItem : resp.hosts) {
            if (hostItem.getStatus().getValue() == HostStatus.ONLINE.getValue()) {
                hostAddrs.add(hostItem.getHostAddr());
            }
        }
        return hostAddrs;
    }
}

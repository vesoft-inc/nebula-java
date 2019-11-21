/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.meta.client;

import static com.google.common.base.Preconditions.checkArgument;

import com.facebook.thrift.TException;
import com.facebook.thrift.protocol.TBinaryProtocol;
import com.facebook.thrift.protocol.TProtocol;
import com.facebook.thrift.transport.TSocket;
import com.facebook.thrift.transport.TTransport;
import com.facebook.thrift.transport.TTransportException;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.net.HostAndPort;
import com.google.common.net.InetAddresses;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
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
import com.vesoft.nebula.meta.client.entry.GetPartsAllocResult;
import com.vesoft.nebula.meta.client.entry.ListEdgesResult;
import com.vesoft.nebula.meta.client.entry.ListSpaceResult;
import com.vesoft.nebula.meta.client.entry.ListTagsResult;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsyncMetaClientImpl implements AsyncMetaClient {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(AsyncMetaClientImpl.class.getName());

    private MetaService.Client client;

    private TTransport transport = null;

    private ListeningExecutorService threadPool =
            MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());

    private final List<HostAndPort> addresses;
    private final int connectionRetry;
    private final int timeout;

    public AsyncMetaClientImpl(List<HostAndPort> addresses, int timeout, int connectionRetry) {
        checkArgument(timeout > 0);
        checkArgument(connectionRetry > 0);
        if (addresses.isEmpty()) {
            throw new IllegalArgumentException("No meta server address is specified.");
        }

        addresses.forEach(address -> {
            String host = address.getHost();
            int port = address.getPort();
            if (!InetAddresses.isInetAddress(host) || (port <= 0 || port >= 65535)) {
                throw new IllegalArgumentException(String.format("%s:%d is not a valid address",
                        host, port));
            }
        });

        this.addresses = addresses;
        this.connectionRetry = connectionRetry;
        this.timeout = timeout;
        connect();
    }

    public AsyncMetaClientImpl(String host, int port) {
        this(Lists.newArrayList(HostAndPort.fromParts(host, port)),
                DEFAULT_TIMEOUT_MS, DEFAULT_CONNECTION_RETRY_SIZE);
    }

    public AsyncMetaClientImpl(List<HostAndPort> addresses) {
        this(addresses, DEFAULT_TIMEOUT_MS, DEFAULT_CONNECTION_RETRY_SIZE);
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
            }
        }
        return false;
    }

    @Override
    public ListenableFuture<Optional<ListSpaceResult>> listSpaces() {
        return threadPool.submit(new Callable<Optional<ListSpaceResult>>() {
            @Override
            public Optional<ListSpaceResult> call() {
                ListSpacesResp response;
                try {
                    response = client.listSpaces(new ListSpacesReq());
                } catch (TException e) {
                    LOGGER.error(String.format("List Spaces Error: %s", e.getMessage()));
                    return Optional.absent();
                }
                if (response.getCode() != ErrorCode.SUCCEEDED) {
                    LOGGER.error(String.format("Init Error: %s", response.getCode()));
                    return Optional.absent();
                }
                ListSpaceResult result = new ListSpaceResult();
                for (IdName space : response.getSpaces()) {
                    result.add(space.id, space.name);
                }
                return Optional.of(result);
            }
        });
    }

    @Override
    public ListenableFuture<Optional<GetPartsAllocResult>> getPartsAlloc(int spaceId) {
        return threadPool.submit(new Callable<Optional<GetPartsAllocResult>>() {
            @Override
            public Optional<GetPartsAllocResult> call() throws Exception {
                GetPartsAllocReq request = new GetPartsAllocReq();
                request.setSpace_id(spaceId);

                GetPartsAllocResp response;
                try {
                    response = client.getPartsAlloc(request);
                } catch (TException e) {
                    LOGGER.error(String.format("Get Parts Error: %s", e.getMessage()));
                    return Optional.absent();
                }

                if (response.getCode() == ErrorCode.SUCCEEDED) {
                    Map<Integer, List<HostAddr>> part = response.getParts();
                    GetPartsAllocResult result = new GetPartsAllocResult();
                    for (Map.Entry<Integer, List<HostAddr>> entry : part.entrySet()) {
                        result.add(entry.getKey(), entry.getValue());
                    }
                    return Optional.of(result);
                } else {
                    LOGGER.error(String.format("Get Parts Error: %s", response.getCode()));
                    return Optional.absent();
                }
            }
        });
    }

    @Override
    public ListenableFuture<Optional<ListTagsResult>> listTags(int spaceId) {
        return threadPool.submit(new Callable<Optional<ListTagsResult>>() {
            @Override
            public Optional<ListTagsResult> call() throws Exception {
                ListTagsReq request = new ListTagsReq();
                request.setSpace_id(spaceId);

                ListTagsResp response;
                try {
                    response = client.listTags(request);
                } catch (TException e) {
                    LOGGER.error(String.format("Get Tag Error: %s", e.getMessage()));
                    return Optional.absent();
                }
                if (response.getCode() == ErrorCode.SUCCEEDED) {
                    List<TagItem> tags = response.getTags();
                    ListTagsResult result = new ListTagsResult();
                    if (tags != null) {
                        for (TagItem tagItem : tags) {
                            result.add(tagItem.getTag_name(), tagItem);
                        }
                        return Optional.of(result);
                    }
                } else {
                    LOGGER.error(String.format("Get tags Error: %s", response.getCode()));
                }
                return Optional.absent();
            }
        });
    }

    @Override
    public ListenableFuture<Optional<ListEdgesResult>> listEdges(int spaceId) {
        return threadPool.submit(new Callable<Optional<ListEdgesResult>>() {
            @Override
            public Optional<ListEdgesResult> call() throws Exception {
                ListEdgesReq request = new ListEdgesReq();
                request.setSpace_id(spaceId);

                ListEdgesResp response;
                try {
                    response = client.listEdges(request);
                } catch (TException e) {
                    LOGGER.error(String.format("Get Edge Error: %s", e.getMessage()));
                    return Optional.absent();
                }

                if (response.getCode() == ErrorCode.SUCCEEDED) {
                    List<EdgeItem> edges = response.getEdges();
                    ListEdgesResult result = new ListEdgesResult();
                    if (edges != null) {
                        for (EdgeItem edgeItem : edges) {
                            result.add(edgeItem.getEdge_name(), edgeItem);
                        }
                        return Optional.of(result);
                    }
                } else {
                    LOGGER.error(String.format("Get tags Error: %s", response.getCode()));
                }
                return Optional.absent();
            }
        });
    }

    @Override
    public void close() throws Exception {

    }
}

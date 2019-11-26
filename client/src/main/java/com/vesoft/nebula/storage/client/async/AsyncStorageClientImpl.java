/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.storage.client.async;

import com.facebook.thrift.TException;
import com.facebook.thrift.async.TAsyncClientManager;
import com.facebook.thrift.protocol.TBinaryProtocol;
import com.facebook.thrift.protocol.TProtocolFactory;
import com.facebook.thrift.transport.TNonblockingSocket;
import com.facebook.thrift.transport.TNonblockingTransport;
import com.facebook.thrift.transport.TTransportException;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.net.HostAndPort;
import com.google.common.util.concurrent.ListenableFuture;
import com.vesoft.nebula.HostAddr;
import com.vesoft.nebula.meta.client.MetaClientImpl;
import com.vesoft.nebula.storage.StorageService;
import com.vesoft.nebula.utils.IPv4IntTransformer;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Nebula Async Storage Client
 */
public class AsyncStorageClientImpl implements AsyncStorageClient {
    // TODO (freddie) Implement this AsyncStorageClient
    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncStorageClientImpl.class);

    private Map<HostAddr, StorageService.AsyncClient> clientMap;

    private final int connectionRetry;
    private final int timeout;
    private MetaClientImpl metaClient;
    private TNonblockingTransport transport = null;
    private TAsyncClientManager manager;
    private Map<Integer, Map<Integer, HostAddr>> leaders;
    private Map<Integer, Map<Integer, List<HostAddr>>> partsAlloc;

    /**
     * Constructor
     *
     * @param addresses       The addresses of storage services.
     * @param timeout         The timeout of RPC request.
     * @param connectionRetry The number of retries when connection failure.
     */
    public AsyncStorageClientImpl(List<HostAndPort> addresses, int timeout, int connectionRetry) {
        com.google.common.base.Preconditions.checkArgument(timeout > 0);
        com.google.common.base.Preconditions.checkArgument(connectionRetry > 0);

        this.timeout = timeout;
        this.connectionRetry = connectionRetry;
        this.leaders = new ConcurrentHashMap<>();
        this.clientMap = new ConcurrentHashMap<>();
    }

    /**
     * Constructor with a MetaClient object
     *
     * @param metaClient The Nebula MetaClient
     */
    public AsyncStorageClientImpl(MetaClientImpl metaClient) {
        this(Lists.newArrayList(), DEFAULT_TIMEOUT_MS, DEFAULT_CONNECTION_RETRY_SIZE);
        this.metaClient = metaClient;
        this.partsAlloc = this.metaClient.getParts();
    }

    private StorageService.AsyncClient connect(HostAddr addr) {
        if (clientMap.containsKey(addr)) {
            return clientMap.get(addr);
        }

        int retry = connectionRetry;
        while (retry-- != 0) {
            String ip = IPv4IntTransformer.intToIPv4(addr.getIp());
            int port = addr.getPort();

            try {
                manager = new TAsyncClientManager();
                transport = new TNonblockingSocket(ip, port, timeout);
                TProtocolFactory protocol = new TBinaryProtocol.Factory();
                StorageService.AsyncClient client = new StorageService.AsyncClient(protocol,
                    manager, transport);
                clientMap.put(addr, client);
                return client;
            } catch (TTransportException tte) {
                LOGGER.error("Connect failed: " + tte.getMessage());
            } catch (TException te) {
                LOGGER.error("Connect failed: " + te.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public ListenableFuture<Boolean> put(int space, String key, String value) {
        return null;
    }

    @Override
    public ListenableFuture<Optional<String>> get(int space, String key) {
        return null;
    }

    @Override
    public ListenableFuture<Boolean> remove(int space, String key) {
        return null;
    }

    @Override
    public void close() throws Exception {

    }
}
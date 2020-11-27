/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.storage.async;

import com.facebook.thrift.TException;
import com.facebook.thrift.async.TAsyncClientManager;
import com.facebook.thrift.protocol.TBinaryProtocol;
import com.facebook.thrift.protocol.TProtocolFactory;
import com.facebook.thrift.transport.TNonblockingSocket;
import com.facebook.thrift.transport.TNonblockingTransport;
import com.facebook.thrift.transport.TTransportException;
import com.google.common.base.Optional;
import com.google.common.net.HostAndPort;
import com.google.common.util.concurrent.ListenableFuture;
import com.vesoft.nebula.HostAddr;
import com.vesoft.nebula.client.meta.MetaClientImpl;
import com.vesoft.nebula.storage.StorageService;
import com.vesoft.nebula.utils.AddressUtil;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Nebula Async Storage Client
 */
public class AsyncStorageClientImpl extends AsyncStorageClient {
    // TODO (freddie) Implement this AsyncStorageClient
    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncStorageClientImpl.class);

    private Map<HostAddr, StorageService.AsyncClient> clientMap = new HashMap<>();

    private MetaClientImpl metaClient;
    private TNonblockingTransport transport = null;
    private TAsyncClientManager manager;
    private Map<Integer, Map<Integer, HostAddr>> leaders = new HashMap<>();
    private Map<String, Map<Integer, List<HostAddr>>> partsAlloc = new HashMap<>();

    public AsyncStorageClientImpl(List<HostAndPort> addresses, int timeout,
                                  int connectionRetry, int executionRetry) {
        super(addresses, timeout, connectionRetry, executionRetry);
    }

    private StorageService.AsyncClient connect(HostAddr addr) {
        if (clientMap.containsKey(addr)) {
            return clientMap.get(addr);
        }

        int retry = connectionRetry;
        while (retry-- != 0) {
            String ip = AddressUtil.intToIPv4(addr.getIp());
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
    public ListenableFuture<Boolean> put(String space, String key, String value) {
        return null;
    }

    @Override
    public ListenableFuture<Optional<String>> get(String space, String key) {
        return null;
    }

    @Override
    public ListenableFuture<Boolean> remove(String space, String key) {
        return null;
    }

    @Override
    public int doConnect(List<HostAndPort> address) throws TException {
        return 0;
    }

    @Override
    public void close() {
        if (transport != null && transport.isOpen()) {
            transport.close();
        }
        try {
            manager.stop();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
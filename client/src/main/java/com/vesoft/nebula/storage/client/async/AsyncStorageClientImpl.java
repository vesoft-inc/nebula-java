/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.storage.client.async;

import static com.google.common.base.Preconditions.checkArgument;

import com.facebook.thrift.transport.TTransport;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.net.HostAndPort;
import com.google.common.net.InetAddresses;
import com.vesoft.nebula.HostAddr;
import com.vesoft.nebula.meta.client.MetaClientImpl;
import com.vesoft.nebula.storage.StorageService;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Nebula Async Storage Client
 */
public class AsyncStorageClientImpl implements AsyncStorageClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncStorageClientImpl.class);

    private TTransport transport = null;
    private StorageService.Client client;

    private final List<HostAndPort> addresses;
    private final int connectionRetry;
    private final int timeout;
    private int space;
    private HostAddr currentLeaderAddress; // Used to record the address of the recent connection
    private MetaClientImpl metaClient;
    private Map<Integer, Map<Integer, HostAddr>> leaders;

    /**
     * Constructor
     *
     * @param addresses       The addresses of storage services.
     * @param timeout         The timeout of RPC request.
     * @param connectionRetry The number of retries when connection failure.
     */
    public AsyncStorageClientImpl(List<HostAndPort> addresses, int timeout, int connectionRetry) {
        checkArgument(timeout > 0);
        checkArgument(connectionRetry > 0);

        addresses.forEach(address -> {
            String host = address.getHost();
            int port = address.getPort();
            if (!InetAddresses.isInetAddress(host) || (port <= 0 || port >= 65535)) {
                throw new IllegalArgumentException(String.format("%s:%d is not a valid address",
                        host, port));
            }
        });

        this.addresses = addresses;
        this.timeout = timeout;
        this.connectionRetry = connectionRetry;
        this.leaders = Maps.newHashMap();
    }

    /**
     * Constructor with Storage Host String and Port Integer
     *
     * @param host The host of storage services.
     * @param port The port of storage services.
     */
    public AsyncStorageClientImpl(String host, int port) {
        this(Lists.newArrayList(HostAndPort.fromParts(host, port)), DEFAULT_TIMEOUT_MS,
                DEFAULT_CONNECTION_RETRY_SIZE);
    }

    /**
     * Constructor with a List of Storage addresses
     *
     * @param addresses The addresses of storage services.
     */
    public AsyncStorageClientImpl(List<HostAndPort> addresses) {
        this(addresses, DEFAULT_TIMEOUT_MS, DEFAULT_CONNECTION_RETRY_SIZE);
    }



    @Override
    public void close() throws Exception {

    }
}

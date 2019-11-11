/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.storage.client;

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
import com.vesoft.nebula.Pair;
import com.vesoft.nebula.storage.ExecResponse;
import com.vesoft.nebula.storage.GeneralResponse;
import com.vesoft.nebula.storage.GetRequest;
import com.vesoft.nebula.storage.PutRequest;
import com.vesoft.nebula.storage.RemoveRangeRequest;
import com.vesoft.nebula.storage.RemoveRequest;
import com.vesoft.nebula.storage.StorageService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Nebula Storage Client
 */
public class StorageClientImpl implements StorageClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(StorageClientImpl.class.getName());

    private TTransport transport = null;
    private StorageService.Client client;

    private final List<HostAndPort> addresses;
    private final int connectionRetry;
    private final int timeout;
    private int space;

    /**
     * The Constructor of Storage Client.
     *
     * @param addresses       The addresses of storage services.
     * @param timeout         The timeout of RPC request.
     * @param connectionRetry The number of retries when connection failure.
     */
    public StorageClientImpl(List<HostAndPort> addresses, int timeout, int connectionRetry) {
        com.google.common.base.Preconditions.checkArgument(timeout > 0);
        com.google.common.base.Preconditions.checkArgument(connectionRetry > 0);

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
    }

    /**
     * The Constructor of Storage Client.
     *
     * @param addresses The addresses of storage services.
     */
    public StorageClientImpl(List<HostAndPort> addresses) {
        this(addresses, DEFAULT_TIMEOUT_MS, DEFAULT_CONNECTION_RETRY_SIZE);
    }

    /**
     * The Constructor of Storage Client.
     *
     * @param host The host of graph services.
     * @param port The port of graph services.
     */
    public StorageClientImpl(String host, int port) {
        this(Lists.newArrayList(HostAndPort.fromParts(host, port)), DEFAULT_TIMEOUT_MS,
                DEFAULT_CONNECTION_RETRY_SIZE);
    }

    /**
     * Use Space
     *
     * @param space nebula space ID
     */
    @Override
    public void switchSpace(int space) {
        this.space = space;
    }

    public boolean connect() {
        int retry = connectionRetry;
        while (retry-- != 0) {
            Random random = new Random(System.currentTimeMillis());
            int position = random.nextInt(addresses.size());
            HostAndPort address = addresses.get(position);
            transport = new TSocket(address.getHost(), address.getPort(), timeout);
            TProtocol protocol = new TBinaryProtocol(transport);

            try {
                transport.open();
                client = new StorageService.Client(protocol);
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
     * Put key-value pair into partition
     *
     * @param part  partitionID
     * @param key   nebula key
     * @param value nebula value
     * @return
     */
    @Override
    public boolean put(int part, String key, String value) {
        PutRequest request = new PutRequest();
        request.setSpace_id(space);
        Map<Integer, List<Pair>> parts = Maps.newHashMap();
        List<Pair> pairs = Lists.newArrayList(new Pair(key, value));
        parts.put(part, pairs);
        request.setParts(parts);
        LOGGER.debug("Put Request: %s", request.toString());

        ExecResponse response;
        try {
            response = client.put(request);
        } catch (TException e) {
            LOGGER.error("Put Failed: %s", e.getMessage());
            return false;
        }

        return isSuccess(response);
    }

    /**
     * Put multi key-value pairs into partition
     *
     * @param part   partitionID
     * @param values key-value pairs
     * @return
     */
    public boolean put(int part, Map<String, String> values) {
        PutRequest request = new PutRequest();
        request.setSpace_id(space);
        Map<Integer, List<Pair>> parts = Maps.newHashMap();
        List<Pair> pairs = Lists.newLinkedList();
        for (Map.Entry<String, String> entry : values.entrySet()) {
            pairs.add(new Pair(entry.getKey(), entry.getValue()));
        }
        parts.put(part, pairs);
        request.setParts(parts);
        LOGGER.debug("Put Request: %s", request.toString());

        ExecResponse response;
        try {
            response = client.put(request);
        } catch (TException e) {
            LOGGER.error("Put Failed: %s", e.getMessage());
            return false;
        }
        return isSuccess(response);
    }

    /**
     * Get key from part
     *
     * @param part partitionID
     * @param key  nebula key
     * @return
     */
    @Override
    public Optional<String> get(int part, String key) {
        GetRequest request = new GetRequest();
        request.setSpace_id(space);
        Map<Integer, List<String>> parts = Maps.newHashMap();
        parts.put(part, Arrays.asList(key));
        request.setParts(parts);
        LOGGER.debug("Get Request: %s", request.toString());

        GeneralResponse response;
        try {
            response = client.get(request);
        } catch (TException e) {
            LOGGER.error("Get Failed: %s", e.getMessage());
            return Optional.empty();
        }

        if (response.values.containsKey(key)) {
            return Optional.of(response.values.get(key));
        } else {
            return null;
        }
    }

    /**
     * Get multi keys from part
     *
     * @param part partitionID
     * @param keys nebula keys
     * @return
     */
    public Optional<Map<String, String>> get(int part, List<String> keys) {
        GetRequest request = new GetRequest();
        Map<Integer, List<String>> parts = Maps.newHashMap();
        parts.put(part, keys);
        request.setSpace_id(space);
        request.setParts(parts);
        LOGGER.debug("Get Request: %s", request.toString());

        GeneralResponse response;
        try {
            response = client.get(request);
        } catch (TException e) {
            LOGGER.error("Get Failed: %s", e.getMessage());
            return Optional.empty();
        }
        return Optional.of(response.values);
    }

    /**
     * Remove key from part
     *
     * @param part partitionID
     * @param key  nebula key
     * @return
     */
    @Override
    public boolean remove(int part, String key) {
        RemoveRequest request = new RemoveRequest();
        request.setSpace_id(space);
        Map<Integer, List<String>> parts = Maps.newHashMap();
        parts.put(part, Arrays.asList(key));
        request.setParts(parts);
        LOGGER.debug("Remove Request: %s", request.toString());

        ExecResponse response;
        try {
            response = client.remove(request);
        } catch (TException e) {
            LOGGER.error("Remove Failed: %s", e.getMessage());
            return false;
        }
        return isSuccess(response);
    }

    /**
     * Remove keys from start to end at part
     *
     * @param part  partitionID
     * @param start nebula start key
     * @param end   nebula end key
     * @return
     */
    @Override
    public boolean removeRange(int part, String start, String end) {
        RemoveRangeRequest request = new RemoveRangeRequest();
        request.setSpace_id(space);
        Map<Integer, List<Pair>> parts = Maps.newHashMap();
        parts.put(part, Arrays.asList(new Pair(start, end)));
        request.setParts(parts);
        LOGGER.debug("Remove Range Request: %s", request.toString());

        ExecResponse response;
        try {
            response = client.removeRange(request);
        } catch (TException e) {
            LOGGER.error("Remove Range Failed: %s", e.getMessage());
            return false;
        }
        return isSuccess(response);
    }

    /**
     * Check the response is successfully
     *
     * @param response execution response
     * @return
     */
    private boolean isSuccess(ExecResponse response) {
        return response.result.failed_codes.size() == 0 ? true : false;
    }

    /**
     * Close the client
     *
     * @throws Exception close exception
     */
    @Override
    public void close() throws Exception {

    }
}


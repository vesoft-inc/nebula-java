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
import com.vesoft.nebula.HostAddr;
import com.vesoft.nebula.IPv4IntTransformer;
import com.vesoft.nebula.Pair;
import com.vesoft.nebula.meta.ErrorCode;
import com.vesoft.nebula.meta.client.MetaClientImpl;
import com.vesoft.nebula.storage.*;

import java.util.*;

import org.apache.commons.codec.digest.MurmurHash2;
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
    private HostAddr tmpLeader;
    private MetaClientImpl metaClient;
    private Map<Integer, Map<Integer, HostAddr>> leaders;

    /**
     * Constructor
     *
     * @param addresses
     * @param timeout
     * @param connectionRetry
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
        this.leaders = Maps.newHashMap();
    }

    /**
     * Constructor with Storage Host String and Port Integer
     *
     * @param host
     * @param port
     */
    public StorageClientImpl(String host, int port) {
        this(Lists.newArrayList(HostAndPort.fromParts(host, port)), DEFAULT_TIMEOUT_MS,
                DEFAULT_CONNECTION_RETRY_SIZE);
    }

    /**
     * Constructor with a List of Storage addresses
     *
     * @param addresses
     */
    public StorageClientImpl(List<HostAndPort> addresses) {
        this(addresses, DEFAULT_TIMEOUT_MS, DEFAULT_CONNECTION_RETRY_SIZE);
    }

    /**
     * Constructor with a MetaClient object
     *
     * @param metaClient
     */
    public StorageClientImpl(MetaClientImpl metaClient) {
        this(Lists.newArrayList(), DEFAULT_TIMEOUT_MS, DEFAULT_CONNECTION_RETRY_SIZE);
        this.metaClient = metaClient;
    }

    private boolean connect() {
        int retry = connectionRetry;
        while (retry-- != 0) {
            Random random = new Random(System.currentTimeMillis());
            int position = random.nextInt(addresses.size());
            HostAndPort address = addresses.get(position);
            tmpLeader = new HostAddr(IPv4IntTransformer.ip2Integer(address.getHost()), address.getPort());
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

    private boolean connect(HostAddr addr) {
        int retry = connectionRetry;
        while (retry-- != 0) {
            String leaderHost = IPv4IntTransformer.intToIPv4(addr.getIp());
            int leaderPort = addr.getPort();
            tmpLeader = addr;
            transport = new TSocket(leaderHost, leaderPort, timeout);
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
     * Use Space
     *
     * @param space nebula space ID
     */
    @Override
    public void switchSpace(int space) {
        this.space = space;
        if (!leaders.containsKey(space))
            leaders.put(space, Maps.newHashMap());
    }

    @Override
    public boolean put(int part, int key, int value) {
        return put(part, String.valueOf(key), String.valueOf(value));
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
        int retry = connectionRetry;
        checkLeader(part);

        PutRequest request = new PutRequest();
        request.setSpace_id(space);
        Map<Integer, List<Pair>> parts = Maps.newHashMap();
        List<Pair> pairs = Lists.newArrayList(new Pair(key, value));
        parts.put(part, pairs);
        request.setParts(parts);
        LOGGER.debug(String.format("Put Request: %s", request.toString()));

        ExecResponse response;
        while (retry-- != 0) {
            try {
                response = client.put(request);
                if (!isSuccess(response)) {
                    for (ResultCode code : response.result.getFailed_codes()) {
                        //LOGGER.error("Put Failure! Failed part " + code.getPart_id() +
                        //", failed code " + code.getCode());
                        if (code.getCode() == ErrorCode.E_LEADER_CHANGED) {
                            HostAddr addr = code.getLeader();
                            if (addr != null && addr.getIp() != 0 && addr.getPort() != 0) {
                                updateLeader(space, code.getPart_id(), new HostAddr(addr.getIp(), addr.getPort()));
                                connect(addr);
                            }
                        }
                    }
                } else {
                    //LOGGER.info("Put Succeed! Put " + key + " : " + value + " to part " + part);
                    if (!leaders.get(space).containsKey(part) || leaders.get(space).get(part) != tmpLeader) {
                        updateLeader(space, part, tmpLeader);
                    }
                    return true;
                }
            } catch (TException e) {
                LOGGER.error(String.format("Put Failed: %s", e.getMessage()));
            }
        }
        return false;
    }

    /**
     * Put multi key-value pairs into partition
     *
     * @param part   partitionID
     * @param values key-value pairs
     * @return
     */
    @Override
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
        LOGGER.debug(String.format("Put Request: %s", request.toString()));

        ExecResponse response;
        try {
            response = client.put(request);
        } catch (TException e) {
            LOGGER.error(String.format("Put Failed: %s", e.getMessage()));
            return false;
        }
        return isSuccess(response);
    }

    @Override
    public int get(int part, int key) {
        return get(part, String.valueOf(key)).isPresent() ? Integer.parseInt(get(part, String.valueOf(key)).get()) : null;
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
        int retry = connectionRetry;
        checkLeader(part);
        GetRequest request = new GetRequest();
        request.setSpace_id(space);
        Map<Integer, List<String>> parts = Maps.newHashMap();
        parts.put(part, Arrays.asList(key));
        request.setParts(parts);
        LOGGER.debug(String.format("Get Request: ", request.toString()));

        GeneralResponse response;
        while (retry-- != 0) {
            try {
                response = client.get(request);
                if (!isSuccess(response)) {
                    for (ResultCode code : response.result.getFailed_codes()) {
                        //LOGGER.error("Get Failure! Failed part " + code.getPart_id() +
                        //", failed code " + code.getCode());
                        if (code.getCode() == ErrorCode.E_LEADER_CHANGED) {
                            HostAddr addr = code.getLeader();
                            if (addr != null && addr.getIp() != 0 && addr.getPort() != 0) {
                                updateLeader(space, code.getPart_id(), new HostAddr(addr.getIp(), addr.getPort()));
                                connect(addr);
                            }
                        }
                    }
                } else {
                    //LOGGER.info("Get Succeed!");
                    if (!leaders.get(space).containsKey(part) || leaders.get(space).get(part) != tmpLeader) {
                        updateLeader(space, part, tmpLeader);
                    }
                    if (response.values.containsKey(key)) {
                        return Optional.of(response.values.get(key));
                    } else {
                        return Optional.empty();
                    }
                }
            } catch (TException e) {
                LOGGER.error(String.format("Get Failed: %s", e.getMessage()));
            }
        }
        return Optional.empty();
    }

    /**
     * Get multi keys from part
     *
     * @param part partitionID
     * @param keys nebula keys
     * @return
     */
    @Override
    public Optional<Map<String, String>> get(int part, List<String> keys) {
        GetRequest request = new GetRequest();
        Map<Integer, List<String>> parts = Maps.newHashMap();
        parts.put(part, keys);
        request.setSpace_id(space);
        request.setParts(parts);
        LOGGER.debug(String.format("Get Request: %s", request.toString()));

        GeneralResponse response;
        try {
            response = client.get(request);
        } catch (TException e) {
            LOGGER.error(String.format("Get Failed: %s", e.getMessage()));
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
        LOGGER.debug(String.format("Remove Request: %s", request.toString()));

        ExecResponse response;
        try {
            response = client.remove(request);
        } catch (TException e) {
            LOGGER.error(String.format("Remove Failed: %s", e.getMessage()));
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
        LOGGER.debug(String.format("Remove Range Request: %s", request.toString()));

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
        return response.result.failed_codes.size() == 0;
    }

    private boolean isSuccess(GeneralResponse response) {
        return response.result.failed_codes.size() == 0;
    }

    private void updateLeader(int spaceId, int partId, HostAddr addr) {
        LOGGER.debug("Update leader for space " + spaceId + ", " + partId + " to " + addr);
        leaders.get(spaceId).put(partId, addr);
    }

    private void checkLeader(int part) {
        HostAddr leader;
        if (leaders.get(space).containsKey(part)) {
            leader = leaders.get(space).get(part);
            connect(leader);
        } else {
            List<HostAddr> addrs = metaClient.getPart(space, part);
            if (addrs != null) {
                this.addresses.clear();
                for (HostAddr addr : addrs) {
                    addresses.add(HostAndPort.fromParts(IPv4IntTransformer.intToIPv4(addr.getIp()), addr.getPort()));
                }
            }
            connect();
        }
    }

    /**
     * Compute the partID using key
     *
     * @param key
     * @return
     */
    public long hash(int key) {
        return hash(String.valueOf(key));
    }

    public long hash(String key) {
        return MurmurHash2.hash64(key);
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


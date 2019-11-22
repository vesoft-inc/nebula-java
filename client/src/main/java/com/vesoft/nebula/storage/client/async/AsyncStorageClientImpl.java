/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.storage.client.async;

import com.facebook.thrift.TException;
import com.facebook.thrift.protocol.TBinaryProtocol;
import com.facebook.thrift.protocol.TProtocol;
import com.facebook.thrift.transport.TSocket;
import com.facebook.thrift.transport.TTransport;
import com.facebook.thrift.transport.TTransportException;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.net.HostAndPort;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.vesoft.nebula.HostAddr;
import com.vesoft.nebula.Pair;
import com.vesoft.nebula.meta.ErrorCode;
import com.vesoft.nebula.meta.client.MetaClientImpl;
import com.vesoft.nebula.storage.ExecResponse;
import com.vesoft.nebula.storage.GeneralResponse;
import com.vesoft.nebula.storage.GetRequest;
import com.vesoft.nebula.storage.PutRequest;
import com.vesoft.nebula.storage.RemoveRequest;
import com.vesoft.nebula.storage.ResultCode;
import com.vesoft.nebula.storage.StorageService;
import com.vesoft.nebula.utils.IPv4IntTransformer;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

import org.apache.commons.codec.digest.MurmurHash2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Nebula Async Storage Client
 */
public class AsyncStorageClientImpl implements AsyncStorageClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncStorageClientImpl.class);

    private TTransport transport = null;
    private Map<HostAddr, StorageService.Client> clientMap;

    private final int connectionRetry;
    private final int timeout;
    private MetaClientImpl metaClient;
    private Map<Integer, Map<Integer, HostAddr>> leaders;
    private Map<Integer, Map<Integer, List<HostAddr>>> partsAlloc;

    private ListeningExecutorService threadPool;

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
        this.threadPool = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());
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

    private StorageService.Client connect(HostAddr addr) {
        if (clientMap.containsKey(addr)) {
            return clientMap.get(addr);
        }

        int retry = connectionRetry;
        while (retry-- != 0) {
            String ip = IPv4IntTransformer.intToIPv4(addr.getIp());
            int port = addr.getPort();
            transport = new TSocket(ip, port, timeout);
            TProtocol protocol = new TBinaryProtocol(transport);
            try {
                transport.open();
                StorageService.Client client = new StorageService.Client(protocol);
                clientMap.put(addr, client);
                return client;
            } catch (TTransportException tte) {
                LOGGER.error("Connect failed: " + tte.getMessage());
            } catch (TException te) {
                LOGGER.error("Connect failed: " + te.getMessage());
            }
        }
        return null;
    }

    /**
     * Put key-value pair into partition
     *
     * @param space nebula space id
     * @param key   nebula key
     * @param value nebula value
     * @return
     */
    @Override
    public ListenableFuture<Boolean> put(int space, String key, String value) {
        return threadPool.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                int part = keyToPartId(space, key);
                HostAddr leader = getLeader(space, part);
                if (leader == null) {
                    LOGGER.error(String.format("Get Leader Failed When Putting %s : %s to Space "
                        + "%d", key, value, space));
                    return false;
                }

                PutRequest request = new PutRequest();
                request.setSpace_id(space);
                Map<Integer, List<Pair>> parts = Maps.newHashMap();
                List<Pair> pairs = Lists.newArrayList(new Pair(key, value));
                parts.put(part, pairs);
                request.setParts(parts);
                //LOGGER.debug(String.format("Put Request: %s", request.toString()));

                return doPut(space, leader, request);
            }
        });


    }

    private boolean doPut(int space, HostAddr leader, PutRequest request) {
        StorageService.Client client = connect(leader);
        if (client == null) {
            return false;
        }

        ExecResponse response;
        int retry = connectionRetry;
        while (retry-- != 0) {
            try {
                response = client.put(request);
                if (!isSuccess(response)) {
                    for (ResultCode code : response.result.getFailed_codes()) {
                        if (code.getCode() == ErrorCode.E_LEADER_CHANGED) {
                            HostAddr addr = code.getLeader();
                            if (addr != null && addr.getIp() != 0 && addr.getPort() != 0) {
                                HostAddr newLeader = new HostAddr(addr.getIp(), addr.getPort());
                                updateLeader(space, code.getPart_id(), newLeader);
                                StorageService.Client newClient = connect(newLeader);
                                if (newClient != null) {
                                    client = newClient;
                                }
                            }
                        }
                    }
                } else {
                    return true;
                }
            } catch (TException e) {
                for (Integer part : request.parts.keySet()) {
                    invalidLeader(space, part);
                }
                LOGGER.error(String.format("Put Failed: %s", e.getMessage()));
            }
        }
        return false;
    }

    /**
     * Get key from part
     *
     * @param space nebula space id
     * @param key   nebula key
     * @return
     */
    @Override
    public ListenableFuture<Optional<String>> get(int space, String key) {
        return threadPool.submit(new Callable<Optional<String>>() {
            @Override
            public Optional<String> call() throws Exception {
                int part = keyToPartId(space, key);
                HostAddr leader = getLeader(space, part);
                if (leader == null) {
                    LOGGER.error(String.format("Get Leader Failed When Getting Key %s from Space "
                        + "%d"), key, space);
                    return Optional.absent();
                }
                GetRequest request = new GetRequest();
                request.setSpace_id(space);
                Map<Integer, List<String>> parts = Maps.newHashMap();
                parts.put(part, Arrays.asList(key));
                request.setParts(parts);
                //LOGGER.debug(String.format("Get Request: %s", request.toString()));

                Optional<Map<String, String>> result = doGet(space, leader, request);
                if (!result.isPresent() || !result.get().containsKey(key)) {
                    return Optional.absent();
                } else {
                    return Optional.of(result.get().get(key));
                }
            }
        });
    }

    private Optional<Map<String, String>> doGet(int space, HostAddr leader,
                                                GetRequest request) {
        StorageService.Client client = connect(leader);
        if (client == null) {
            return Optional.absent();
        }

        GeneralResponse response;
        int retry = connectionRetry;
        while (retry-- != 0) {
            try {
                response = client.get(request);
                if (!isSuccess(response)) {
                    for (ResultCode code : response.result.getFailed_codes()) {
                        if (code.getCode() == ErrorCode.E_LEADER_CHANGED) {
                            HostAddr addr = code.getLeader();
                            if (addr != null && addr.getIp() != 0 && addr.getPort() != 0) {
                                HostAddr newLeader = new HostAddr(addr.getIp(), addr.getPort());
                                updateLeader(space, code.getPart_id(), newLeader);
                                StorageService.Client newClient = connect(newLeader);
                                if (newClient != null) {
                                    client = newClient;
                                }
                            }
                        }
                    }
                } else {
                    return Optional.of(response.values);
                }
            } catch (TException e) {
                for (Integer part : request.parts.keySet()) {
                    invalidLeader(space, part);
                }
                LOGGER.error(String.format("Get Failed: %s", e.getMessage()));
                return Optional.absent();
            }
        }
        return Optional.absent();
    }

    /**
     * Remove key from part
     *
     * @param space nebula space id
     * @param key   nebula key
     * @return
     */
    @Override
    public ListenableFuture<Boolean> remove(int space, String key) {
        return threadPool.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                int part = keyToPartId(space, key);
                HostAddr leader = getLeader(space, part);
                if (leader == null) {
                    LOGGER.error(String.format("Get Leader Faild When Removing Key %s from Space "
                        + "%d", key, space));
                    return false;
                }
                RemoveRequest request = new RemoveRequest();
                request.setSpace_id(space);
                Map<Integer, List<String>> parts = Maps.newHashMap();
                parts.put(part, Arrays.asList(key));
                request.setParts(parts);
                LOGGER.debug(String.format("Remove Request: %s", request.toString()));

                return doRemove(space, leader, request);
            }
        });
    }

    private boolean doRemove(int space, HostAddr leader, RemoveRequest request) {
        StorageService.Client client = connect(leader);
        if (client == null) {
            return false;
        }

        ExecResponse response;
        int retry = connectionRetry;
        while (retry-- != 0) {
            try {
                response = client.remove(request);
                if (!isSuccess(response)) {
                    for (ResultCode code : response.result.getFailed_codes()) {
                        if (code.getCode() == ErrorCode.E_LEADER_CHANGED) {
                            HostAddr addr = code.getLeader();
                            if (addr != null && addr.getIp() != 0 && addr.getPort() != 0) {
                                HostAddr newLeader = new HostAddr(addr.getIp(), addr.getPort());
                                updateLeader(space, code.getPart_id(), newLeader);
                                StorageService.Client newClient = connect(newLeader);
                                if (newClient != null) {
                                    client = newClient;
                                }
                            }
                        }
                    }
                } else {
                    return true;
                }
            } catch (TException e) {
                for (Integer part : request.parts.keySet()) {
                    invalidLeader(space, part);
                }
                LOGGER.error(String.format("Remove Failed: %s", e.getMessage()));
                return false;
            }
        }
        return false;
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
        if (!leaders.containsKey(spaceId)) {
            leaders.put(spaceId, new ConcurrentHashMap<>());
        }
        leaders.get(spaceId).put(partId, addr);
    }

    private void invalidLeader(int spaceId, int partId) {
        LOGGER.debug("Invalid leader for space " + spaceId + ", " + partId);
        if (!leaders.containsKey(spaceId)) {
            leaders.put(spaceId, new ConcurrentHashMap<>());
        }
        leaders.get(spaceId).remove(partId);
    }

    private HostAddr getLeader(int space, int part) {
        if (!leaders.containsKey(space)) {
            leaders.put(space, new ConcurrentHashMap<>());
        }
        if (leaders.get(space).containsKey(part)) {
            return leaders.get(space).get(part);
        } else {
            List<HostAddr> addrs = metaClient.getPart(space, part);
            if (addrs != null) {
                Random random = new Random(System.currentTimeMillis());
                int position = random.nextInt(addrs.size());
                HostAddr leader = addrs.get(position);
                leaders.get(space).put(part, leader);
                return leader;
            }
            return null;
        }
    }

    private long hash(String key) {
        return MurmurHash2.hash64(key);
    }

    private int keyToPartId(int space, String key) {
        if (!partsAlloc.containsKey(space)) {
            LOGGER.error("Invalid part of " + key);
            return -1;
        }
        int partNum = partsAlloc.get(space).size();
        return (int) (Math.abs(hash(key)) % partNum + 1);
    }

    /**
     * Close the client
     *
     * @throws Exception close exception
     */
    @Override
    public void close() throws Exception {
        threadPool.shutdownNow();
    }
}
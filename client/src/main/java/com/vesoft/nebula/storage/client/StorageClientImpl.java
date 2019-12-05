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
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.net.HostAndPort;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.commons.codec.digest.MurmurHash2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Nebula Storage Client
 */
public class StorageClientImpl implements StorageClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(StorageClientImpl.class);

    private TTransport transport = null;
    private Map<HostAddr, StorageService.Client> clientMap;

    private final int connectionRetry;
    private final int timeout;
    private MetaClientImpl metaClient;
    private Map<Integer, Map<Integer, HostAddr>> leaders;
    private Map<Integer, Map<Integer, List<HostAddr>>> partsAlloc;

    private ExecutorService threadPool;

    /**
     * Constructor
     *
     * @param addresses       The addresses of storage services.
     * @param timeout         The timeout of RPC request.
     * @param connectionRetry The number of retries when connection failure.
     */
    public StorageClientImpl(List<HostAndPort> addresses, int timeout, int connectionRetry) {
        com.google.common.base.Preconditions.checkArgument(timeout > 0);
        com.google.common.base.Preconditions.checkArgument(connectionRetry > 0);

        this.timeout = timeout;
        this.connectionRetry = connectionRetry;
        this.leaders = Maps.newConcurrentMap();
        this.clientMap = Maps.newConcurrentMap();
        this.threadPool = Executors.newFixedThreadPool(DEFAULT_THREAD_COUNT);
    }

    /**
     * Constructor with a MetaClient object
     *
     * @param metaClient The Nebula MetaClient
     */
    public StorageClientImpl(MetaClientImpl metaClient) {
        this(Lists.<HostAndPort>newArrayList(), DEFAULT_TIMEOUT_MS, DEFAULT_CONNECTION_RETRY_SIZE);
        this.metaClient = metaClient;
        this.metaClient.init();
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
    public boolean put(int space, String key, String value) {
        int part = keyToPartId(space, key);
        HostAddr leader = getLeader(space, part);
        if (leader == null) {
            return false;
        }

        PutRequest request = new PutRequest();
        request.setSpace_id(space);
        Map<Integer, List<Pair>> parts = Maps.newHashMap();
        List<Pair> pairs = Lists.newArrayList(new Pair(key, value));
        parts.put(part, pairs);
        request.setParts(parts);
        LOGGER.debug(String.format("Put Request: %s", request.toString()));

        return doPut(space, leader, request);
    }

    /**
     * Put multi key-value pairs into partition
     *
     * @param space nebula space id
     * @param kvs   key-value pairs
     * @return
     */
    @Override
    public boolean put(final int space, Map<String, String> kvs) {
        Map<Integer, List<Pair>> groups = Maps.newHashMap();
        for (Map.Entry<String, String> kv : kvs.entrySet()) {
            int part = keyToPartId(space, kv.getKey());
            if (!groups.containsKey(part)) {
                groups.put(part, new ArrayList<Pair>());
            }
            groups.get(part).add(new Pair(kv.getKey(), kv.getValue()));
        }

        Map<HostAddr, PutRequest> requests = Maps.newHashMap();
        for (Map.Entry<Integer, List<Pair>> entry : groups.entrySet()) {
            int part = entry.getKey();
            HostAddr leader = getLeader(space, part);
            if (!requests.containsKey(leader)) {
                PutRequest request = new PutRequest();
                request.setSpace_id(space);
                Map<Integer, List<Pair>> parts = Maps.newHashMap();
                parts.put(part, entry.getValue());
                request.setParts(parts);
                LOGGER.debug(String.format("Put Request: %s", request.toString()));
                requests.put(leader, request);
            } else {
                PutRequest request = requests.get(leader);
                if (!request.parts.containsKey(part)) {
                    request.parts.put(part, entry.getValue());
                } else {
                    request.parts.get(part).addAll(entry.getValue());
                }
            }
        }

        final CountDownLatch countDownLatch = new CountDownLatch(groups.size());
        final List<Boolean> responses = Collections.synchronizedList(
                new ArrayList<Boolean>(groups.size()));
        for (final Map.Entry<HostAddr, PutRequest> entry : requests.entrySet()) {
            threadPool.submit(new Runnable() {
                @Override
                public void run() {
                    if (doPut(space, entry.getKey(), entry.getValue())) {
                        responses.add(true);
                    } else {
                        responses.add(false);
                    }
                    countDownLatch.countDown();
                }
            });
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            LOGGER.error("Put interrupted");
            return false;
        }

        for (Boolean ret : responses) {
            if (!ret) {
                return false;
            }
        }
        return true;
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
                if (!isSuccessfully(response)) {
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
    public Optional<String> get(int space, String key) {
        int part = keyToPartId(space, key);
        HostAddr leader = getLeader(space, part);
        if (leader == null) {
            return Optional.absent();
        }

        GetRequest request = new GetRequest();
        request.setSpace_id(space);
        Map<Integer, List<String>> parts = Maps.newHashMap();
        parts.put(part, Arrays.asList(key));
        request.setParts(parts);
        LOGGER.debug(String.format("Get Request: %s", request.toString()));

        Optional<Map<String, String>> result = doGet(space, leader, request);
        if (!result.isPresent() || !result.get().containsKey(key)) {
            return Optional.absent();
        } else {
            return Optional.of(result.get().get(key));
        }
    }

    /**
     * Get multi keys from part
     *
     * @param space nebula space id
     * @param keys  nebula keys
     * @return
     */
    @Override
    public Optional<Map<String, String>> get(final int space, List<String> keys) {
        Map<Integer, List<String>> groups = Maps.newHashMap();
        for (String key : keys) {
            int part = keyToPartId(space, key);
            if (!groups.containsKey(part)) {
                groups.put(part, new ArrayList<String>());
            }
            groups.get(part).add(key);
        }

        Map<HostAddr, GetRequest> requests = Maps.newHashMap();
        for (Map.Entry<Integer, List<String>> entry : groups.entrySet()) {
            int part = entry.getKey();
            HostAddr leader = getLeader(space, part);
            if (!requests.containsKey(leader)) {
                GetRequest request = new GetRequest();
                request.setSpace_id(space);
                Map<Integer, List<String>> parts = Maps.newHashMap();
                parts.put(part, entry.getValue());
                request.setParts(parts);
                LOGGER.debug(String.format("Get Request: %s", request.toString()));
                requests.put(leader, request);
            } else {
                GetRequest request = requests.get(leader);
                if (!request.parts.containsKey(part)) {
                    request.parts.put(part, entry.getValue());
                } else {
                    request.parts.get(part).addAll(entry.getValue());
                }
            }
        }

        final CountDownLatch countDownLatch = new CountDownLatch(groups.size());
        final List<Optional<Map<String, String>>> responses = Collections.synchronizedList(
                new ArrayList<Optional<Map<String, String>>>(groups.size()));
        for (final Map.Entry<HostAddr, GetRequest> entry : requests.entrySet()) {
            threadPool.submit(new Runnable() {
                @Override
                public void run() {
                    responses.add(doGet(space, entry.getKey(), entry.getValue()));
                    countDownLatch.countDown();
                }
            });
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            LOGGER.error("Put interrupted");
            return Optional.absent();
        }

        Map<String, String> result = Maps.newHashMap();
        for (Optional<Map<String, String>> response : responses) {
            if (response.isPresent()) {
                result.putAll(response.get());
            }
        }
        return Optional.of(result);
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
                if (!isSuccessfully(response)) {
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
    public boolean remove(int space, String key) {
        int part = keyToPartId(space, key);
        HostAddr leader = getLeader(space, part);
        if (leader == null) {
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

    /**
     * Remove multi keys from part
     *
     * @param space nebula space id
     * @param keys  nebula keys
     * @return
     */
    @Override
    public boolean remove(final int space, List<String> keys) {
        Map<Integer, List<String>> groups = Maps.newHashMap();
        for (String key : keys) {
            int part = keyToPartId(space, key);
            if (!groups.containsKey(part)) {
                groups.put(part, new ArrayList<String>());
            }
            groups.get(part).add(key);
        }

        Map<HostAddr, RemoveRequest> requests = Maps.newHashMap();
        for (Map.Entry<Integer, List<String>> entry : groups.entrySet()) {
            int part = entry.getKey();
            HostAddr leader = getLeader(space, part);
            if (!requests.containsKey(leader)) {
                RemoveRequest request = new RemoveRequest();
                request.setSpace_id(space);
                Map<Integer, List<String>> parts = Maps.newHashMap();
                parts.put(part, entry.getValue());
                request.setParts(parts);
                LOGGER.debug(String.format("Put Request: %s", request.toString()));
                requests.put(leader, request);
            } else {
                RemoveRequest request = requests.get(leader);
                if (!request.parts.containsKey(part)) {
                    request.parts.put(part, entry.getValue());
                } else {
                    request.parts.get(part).addAll(entry.getValue());
                }
            }
        }

        final CountDownLatch countDownLatch = new CountDownLatch(groups.size());
        final List<Boolean> responses = Collections.synchronizedList(
                new ArrayList<Boolean>(groups.size()));
        for (final Map.Entry<HostAddr, RemoveRequest> entry : requests.entrySet()) {
            threadPool.submit(new Runnable() {
                @Override
                public void run() {
                    if (doRemove(space, entry.getKey(), entry.getValue())) {
                        responses.add(true);
                    } else {
                        responses.add(false);
                    }
                    countDownLatch.countDown();
                }
            });
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            LOGGER.error("Put interrupted");
            return false;
        }

        for (Boolean ret : responses) {
            if (!ret) {
                return false;
            }
        }
        return true;
    }

    /*
    @Override
    public boolean removeRange(int part, String start, String end) {
        checkLeader(part);
        RemoveRangeRequest request = new RemoveRangeRequest();
        request.setSpace_id(space);
        Map<Integer, List<Pair>> parts = Maps.newHashMap();
        parts.put(part, Arrays.asList(new Pair(start, end)));
        request.setParts(parts);
        LOGGER.debug(String.format("Remove Range Request: %s", request.toString()));

        ExecResponse response;
        int retry = connectionRetry;
        while (retry-- != 0) {
            try {
                response = client.removeRange(request);
                if (!isSuccess(response)) {
                    for (ResultCode code : response.result.getFailed_codes()) {
                        if (code.getCode() == ErrorCode.E_LEADER_CHANGED) {
                            HostAddr addr = code.getLeader();
                            if (addr != null && addr.getIp() != 0 && addr.getPort() != 0) {
                                HostAddr address = new HostAddr(addr.getIp(), addr.getPort());
                                updateLeader(space, code.getPart_id(), address);
                                connect(addr);
                            }
                        }
                    }
                } else {
                    if (!leaders.get(space).containsKey(part)
                            || leaders.get(space).get(part) != currentLeaderAddress) {
                        updateLeader(space, part, currentLeaderAddress);
                    }
                    return true;
                }
            } catch (TException e) {
                LOGGER.error(String.format("Remove Range Failed: %s", e.getMessage()));
                return false;
            }
        }
        return false;
    }
     */
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
                if (!isSuccessfully(response)) {
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
     * Check the exec response is successfully
     *
     * @param response execution response
     * @return
     */
    private boolean isSuccessfully(ExecResponse response) {
        return response.result.failed_codes.size() == 0;
    }

    /**
     * Check the general response is successfully
     *
     * @param response general response
     * @return
     */
    private boolean isSuccessfully(GeneralResponse response) {
        return response.result.failed_codes.size() == 0;
    }

    private void updateLeader(int spaceId, int partId, HostAddr addr) {
        LOGGER.debug("Update leader for space " + spaceId + ", " + partId + " to " + addr);
        if (!leaders.containsKey(spaceId)) {
            leaders.put(spaceId, Maps.<Integer, HostAddr>newConcurrentMap());
        }
        leaders.get(spaceId).put(partId, addr);
    }

    private void invalidLeader(int spaceId, int partId) {
        LOGGER.debug("Invalid leader for space " + spaceId + ", " + partId);
        if (!leaders.containsKey(spaceId)) {
            leaders.put(spaceId, Maps.<Integer, HostAddr>newConcurrentMap());
        }
        leaders.get(spaceId).remove(partId);
    }

    private HostAddr getLeader(int space, int part) {
        if (!leaders.containsKey(space)) {
            leaders.put(space, Maps.<Integer, HostAddr>newConcurrentMap());
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
        // TODO: need to handle this
        if (!partsAlloc.containsKey(space)) {
            LOGGER.error("Invalid part of " + key);
            return -1;
        }
        // TODO: this is different to implement in c++, which converts to unsigned long at first
        int partNum = partsAlloc.get(space).size();
        return (int) (Math.abs(hash(key)) % partNum + 1);
    }

    /**
     * Close the client
     *
     * @throws Exception close exception
     */
    public void close() {
        threadPool.shutdownNow();
        transport.close();
    }
}


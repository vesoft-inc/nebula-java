/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.storage;

import com.facebook.thrift.TException;
import com.facebook.thrift.protocol.TCompactProtocol;
import com.facebook.thrift.protocol.TProtocol;
import com.facebook.thrift.transport.TSocket;
import com.facebook.thrift.transport.TTransport;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.net.HostAndPort;
import com.vesoft.nebula.AbstractClient;
import com.vesoft.nebula.HostAddr;
import com.vesoft.nebula.Pair;
import com.vesoft.nebula.client.meta.MetaClientImpl;
import com.vesoft.nebula.meta.ErrorCode;
import com.vesoft.nebula.storage.ExecResponse;
import com.vesoft.nebula.storage.GeneralResponse;
import com.vesoft.nebula.storage.GetRequest;
import com.vesoft.nebula.storage.PutRequest;
import com.vesoft.nebula.storage.RemoveRequest;
import com.vesoft.nebula.storage.ResultCode;
import com.vesoft.nebula.storage.ScanEdgeRequest;
import com.vesoft.nebula.storage.ScanEdgeResponse;
import com.vesoft.nebula.storage.ScanVertexRequest;
import com.vesoft.nebula.storage.ScanVertexResponse;
import com.vesoft.nebula.storage.StorageService;
import com.vesoft.nebula.utils.AddressUtil;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import org.apache.commons.codec.digest.MurmurHash2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Nebula Storage Client
 */
public class StorageClientImpl extends AbstractClient implements StorageClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(StorageClientImpl.class);

    private Map<HostAndPort, StorageService.Client> clients = new ConcurrentHashMap<>();

    private MetaClientImpl metaClient;
    private Map<String, Map<Integer, HostAndPort>> leaders = new HashMap<>();
    private Map<String, Map<Integer, List<HostAndPort>>> partsAlloc = new HashMap<>();

    private ExecutorService threadPool;

    /**
     * Constructor with a MetaClient object
     *
     * @param client The Nebula MetaClient
     */
    public StorageClientImpl(MetaClientImpl client) {
        this.metaClient = client;
    }

    @Override
    public int doConnect(List<HostAndPort> addresses) throws TException {
        for (HostAndPort address : addresses) {
            StorageService.Client client = doConnect(address);
            clients.put(address, client);
        }
        return 0;
    }

    private StorageService.Client doConnect(HostAndPort address) throws TException {
        TTransport transport = new TSocket(address.getHostText(), address.getPort(), timeout);
        transport.open();

        TProtocol protocol = new TCompactProtocol(transport);
        return new StorageService.Client(protocol);
    }

    /**
     * Put key-value pair into partition
     *
     * @param spaceName nebula space id
     * @param key       nebula key
     * @param value     nebula value
     * @return
     */
    @Override
    public boolean put(String spaceName, String key, String value) {
        int spaceID = metaClient.getSpaceIDFromCache(spaceName);
        int part = keyToPartId(spaceID, key);
        HostAndPort leader = getLeader(spaceName, part);
        if (leader == null) {
            return false;
        }

        PutRequest request = new PutRequest();
        request.setSpace_id(spaceID);
        Map<Integer, List<Pair>> parts = Maps.newHashMap();
        List<Pair> pairs = Lists.newArrayList(new Pair(key, value));
        parts.put(part, pairs);
        request.setParts(parts);
        LOGGER.debug(String.format("Put Request: %s", request.toString()));
        return doPut(spaceName, leader, request);
    }

    /**
     * Put multi key-value pairs into partition
     *
     * @param spaceName nebula space id
     * @param kvs       key-value pairs
     * @return
     */
    @Override
    public boolean put(final String spaceName, Map<String, String> kvs) {
        int spaceID = metaClient.getSpaceIDFromCache(spaceName);
        Map<Integer, List<Pair>> groups = Maps.newHashMap();
        for (Map.Entry<String, String> kv : kvs.entrySet()) {
            int part = keyToPartId(spaceID, kv.getKey());
            if (!groups.containsKey(part)) {
                groups.put(part, new ArrayList<Pair>());
            }
            groups.get(part).add(new Pair(kv.getKey(), kv.getValue()));
        }

        Map<HostAndPort, PutRequest> requests = Maps.newHashMap();
        for (Map.Entry<Integer, List<Pair>> entry : groups.entrySet()) {
            int part = entry.getKey();
            HostAndPort leader = getLeader(spaceName, part);
            if (!requests.containsKey(leader)) {
                PutRequest request = new PutRequest();
                request.setSpace_id(spaceID);
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
                new ArrayList<>(groups.size()));
        for (final Map.Entry<HostAndPort, PutRequest> entry : requests.entrySet()) {
            threadPool.submit(() -> {
                if (doPut(spaceName, entry.getKey(), entry.getValue())) {
                    responses.add(true);
                } else {
                    responses.add(false);
                }
                countDownLatch.countDown();
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

    private boolean doPut(String space, HostAndPort leader, PutRequest request) {
        StorageService.Client client = connect(leader);
        if (Objects.isNull(client)) {
            disconnect(leader);
            return false;
        }

        ExecResponse response;
        int retry = connectionRetry;
        while (retry-- != 0) {
            try {
                response = client.put(request);
                if (!isSuccessfully(response)) {
                    handleResultCodes(response.result.failed_codes, space, client, leader);
                } else {
                    return true;
                }
            } catch (TException e) {
                for (Integer part : request.parts.keySet()) {
                    invalidLeader(space, part);
                }
                disconnect(leader);
                LOGGER.error(String.format("Put Failed: %s", e.getMessage()));
                return false;
            }
        }
        return false;
    }

    /**
     * Get key from part
     *
     * @param spaceName nebula space id
     * @param key       nebula key
     * @return
     */
    @Override
    public Optional<String> get(String spaceName, String key) {
        int spaceID = metaClient.getSpaceIDFromCache(spaceName);
        int part = keyToPartId(spaceID, key);
        HostAndPort leader = getLeader(spaceName, part);
        if (leader == null) {
            return Optional.absent();
        }

        GetRequest request = new GetRequest();
        request.setSpace_id(spaceID);
        Map<Integer, List<String>> parts = Maps.newHashMap();
        parts.put(part, Arrays.asList(key));
        request.setParts(parts);
        LOGGER.debug(String.format("Get Request: %s", request.toString()));

        Optional<Map<String, String>> result = doGet(spaceName, leader, request);
        if (!result.isPresent() || !result.get().containsKey(key)) {
            return Optional.absent();
        } else {
            return Optional.of(result.get().get(key));
        }
    }

    /**
     * Get multi keys from part
     *
     * @param spaceName nebula space id
     * @param keys      nebula keys
     * @return
     */
    @Override
    public Optional<Map<String, String>> get(final String spaceName, List<String> keys) {
        int spaceID = metaClient.getSpaceIDFromCache(spaceName);
        Map<Integer, List<String>> groups = Maps.newHashMap();
        for (String key : keys) {
            int part = keyToPartId(spaceID, key);
            if (!groups.containsKey(part)) {
                groups.put(part, new ArrayList<>());
            }
            groups.get(part).add(key);
        }

        Map<HostAndPort, GetRequest> requests = Maps.newHashMap();
        for (Map.Entry<Integer, List<String>> entry : groups.entrySet()) {
            int part = entry.getKey();
            HostAndPort leader = getLeader(spaceName, part);
            if (!requests.containsKey(leader)) {
                GetRequest request = new GetRequest();
                request.setSpace_id(spaceID);
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
                new ArrayList<>(groups.size()));
        for (final Map.Entry<HostAndPort, GetRequest> entry : requests.entrySet()) {
            threadPool.submit(() -> {
                responses.add(doGet(spaceName, entry.getKey(), entry.getValue()));
                countDownLatch.countDown();
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

    private Optional<Map<String, String>> doGet(String space, HostAndPort leader,
                                                GetRequest request) {
        StorageService.Client client = connect(leader);
        if (Objects.isNull(client)) {
            disconnect(leader);
            return Optional.absent();
        }

        GeneralResponse response;
        int retry = connectionRetry;
        while (retry-- != 0) {
            try {
                response = client.get(request);
                if (!isSuccessfully(response)) {
                    handleResultCodes(response.result.failed_codes, space, client, leader);
                } else {
                    return Optional.of(response.values);
                }
            } catch (TException e) {
                for (Integer part : request.parts.keySet()) {
                    invalidLeader(space, part);
                }
                disconnect(leader);
                LOGGER.error(String.format("Get Failed: %s", e.getMessage()));
                return Optional.absent();
            }
        }
        return Optional.absent();
    }

    /**
     * Remove key from part
     *
     * @param spaceName nebula space id
     * @param key       nebula key
     * @return
     */
    @Override
    public boolean remove(String spaceName, String key) {
        int spaceID = metaClient.getSpaceIDFromCache(spaceName);
        int part = keyToPartId(spaceID, key);
        HostAndPort leader = getLeader(spaceName, part);
        if (leader == null) {
            return false;
        }

        RemoveRequest request = new RemoveRequest();
        request.setSpace_id(spaceID);
        Map<Integer, List<String>> parts = Maps.newHashMap();
        parts.put(part, Arrays.asList(key));
        request.setParts(parts);
        LOGGER.debug(String.format("Remove Request: %s", request.toString()));
        return doRemove(spaceName, leader, request);
    }

    /**
     * Remove multi keys from part
     *
     * @param space nebula space id
     * @param keys  nebula keys
     * @return
     */
    @Override
    public boolean remove(final String space, List<String> keys) {
        Map<Integer, List<String>> groups = Maps.newHashMap();
        int spaceID = metaClient.getSpaceIDFromCache(space);
        for (String key : keys) {
            int part = keyToPartId(spaceID, key);
            if (!groups.containsKey(part)) {
                groups.put(part, new ArrayList<>());
            }
            groups.get(part).add(key);
        }

        Map<HostAndPort, RemoveRequest> requests = Maps.newHashMap();
        for (Map.Entry<Integer, List<String>> entry : groups.entrySet()) {
            int part = entry.getKey();
            HostAndPort leader = getLeader(space, part);
            if (!requests.containsKey(leader)) {
                RemoveRequest request = new RemoveRequest();
                request.setSpace_id(spaceID);
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
                new ArrayList<>(groups.size()));
        for (final Map.Entry<HostAndPort, RemoveRequest> entry : requests.entrySet()) {
            threadPool.submit(() -> {
                if (doRemove(space, entry.getKey(), entry.getValue())) {
                    responses.add(true);
                } else {
                    responses.add(false);
                }
                countDownLatch.countDown();
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

    private boolean doRemove(String spaceName, HostAndPort leader, RemoveRequest request) {
        StorageService.Client client = connect(leader);
        if (Objects.isNull(client)) {
            disconnect(leader);
            return false;
        }

        ExecResponse response;
        int retry = connectionRetry;
        while (retry-- != 0) {
            try {
                response = client.remove(request);
                if (!isSuccessfully(response)) {
                    handleResultCodes(response.result.failed_codes, spaceName, client, leader);
                } else {
                    return true;
                }
            } catch (TException e) {
                for (Integer part : request.parts.keySet()) {
                    invalidLeader(spaceName, part);
                }
                disconnect(leader);
                LOGGER.error(String.format("Remove Failed: %s", e.getMessage()));
                return false;
            }
        }
        return false;
    }

    public Iterator<ScanEdgeResponse> scanEdge(String space) {
        Set<Integer> partIds = metaClient.getPartsAllocFromCache().get(space).keySet();
        Iterator<Integer> iterator = partIds.iterator();
        return scanEdge(space, iterator, DEFAULT_SCAN_ROW_LIMIT,
                DEFAULT_SCAN_START_TIME, DEFAULT_SCAN_END_TIME);
    }

    public Iterator<ScanEdgeResponse> scanEdge(String space, int rowLimit,
                                               long startTime, long endTime) {
        Set<Integer> partIds = metaClient.getPartsAllocFromCache().get(space).keySet();
        Iterator<Integer> iterator = partIds.iterator();
        return scanEdge(space, iterator, rowLimit, startTime, endTime);
    }

    public Iterator<ScanEdgeResponse> scanEdge(String space, int part) throws IOException {
        return scanEdge(space, part, DEFAULT_SCAN_ROW_LIMIT,
                DEFAULT_SCAN_START_TIME, DEFAULT_SCAN_END_TIME);
    }

    public Iterator<ScanEdgeResponse> scanEdge(String space, int part, int rowLimit,
                                               long startTime, long endTime) throws IOException {
        HostAndPort leader = getLeader(space, part);
        if (Objects.isNull(leader)) {
            throw new IllegalArgumentException("Part " + part + " not found in space " + space);
        }

        return doScanEdge(space, leader, part, rowLimit, startTime, endTime);
    }

    private Iterator<ScanEdgeResponse> scanEdge(String space, Iterator<Integer> parts,
                                                int rowLimit, long startTime, long endTime) {
        return new Iterator<ScanEdgeResponse>() {
            Iterator<ScanEdgeResponse> iterator;

            @Override
            public boolean hasNext() {
                return parts.hasNext() || iterator.hasNext();
            }

            @Override
            public ScanEdgeResponse next() {
                if (Objects.isNull(iterator) || !iterator.hasNext()) {
                    int part = parts.next();
                    HostAndPort leader = getLeader(space, part);
                    if (Objects.isNull(leader)) {
                        throw new IllegalArgumentException("Part " + part
                                + " not found in space " + space);
                    }

                    try {
                        iterator = doScanEdge(space, leader, part, rowLimit, startTime, endTime);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                return iterator.next();
            }
        };
    }

    /**
     * Scan all edges of a partition
     *
     * @param space  nebula space name
     * @param leader host address
     * @return response which contains next start cursor, done if next cursor is empty
     */
    private Iterator<ScanEdgeResponse> doScanEdge(String space, HostAndPort leader, int part,
                                                  int rowLimit, long startTime, long endTime)
            throws IOException {
        StorageService.Client client = connect(leader);
        if (Objects.isNull(client)) {
            disconnect(leader);
            throw new IOException("Failed to connect " + leader);
        }

        int spaceID = metaClient.getSpaceIDFromCache(space);
        int retry = executionRetry;
        while (retry-- != 0) {
            new Iterator<ScanEdgeResponse>() {
                private byte[] cursor = null;
                private boolean haveNext = true;

                @Override
                public boolean hasNext() {
                    return haveNext;
                }

                @Override
                public ScanEdgeResponse next() {
                    ScanEdgeRequest request = new ScanEdgeRequest();
                    request.setSpace_id(spaceID)
                            .setPart_id(part)
                            .setRow_limit(rowLimit)
                            .setStart_time(startTime)
                            .setCursor(cursor)
                            .setEnd_time(endTime);

                    ScanEdgeResponse response;
                    try {
                        response = client.scanEdge(request);
                        cursor = response.next_cursor;
                        haveNext = response.has_next;
                    } catch (TException e) {
                        e.printStackTrace();
                        haveNext = false;
                        return null;
                    }

                    if (!response.result.failed_codes.isEmpty()) {
                        handleResultCodes(response.result.failed_codes, space, client, leader);
                        haveNext = false;
                        return null;
                    } else {
                        return response;
                    }
                }
            };
        }
        throw new IOException("Failed to scan edge within " + executionRetry + " retry");
    }

    public Iterator<ScanVertexResponse> scanVertex(String space) {
        Set<Integer> partIds = metaClient.getPartsAllocFromCache().get(space).keySet();
        Iterator<Integer> iterator = partIds.iterator();
        if (!iterator.hasNext()) {
            return null;
        }
        return scanVertex(space, iterator, DEFAULT_SCAN_ROW_LIMIT,
                DEFAULT_SCAN_START_TIME, DEFAULT_SCAN_END_TIME);
    }

    public Iterator<ScanVertexResponse> scanVertex(
            String space, int rowLimit, long startTime, long endTime) {
        Set<Integer> partIds = metaClient.getPartsAllocFromCache().get(space).keySet();
        Iterator<Integer> iterator = partIds.iterator();
        if (!iterator.hasNext()) {
            return null;
        }
        return scanVertex(space, iterator, rowLimit, startTime, endTime);
    }

    @Override
    public Iterator<ScanVertexResponse> scanVertex(String space, int part) throws IOException {
        return scanVertex(space, part, DEFAULT_SCAN_ROW_LIMIT,
                DEFAULT_SCAN_START_TIME, DEFAULT_SCAN_END_TIME);
    }

    @Override
    public Iterator<ScanVertexResponse> scanVertex(String space, int part, int rowLimit,
                                                   long startTime, long endTime)
            throws IOException {
        HostAndPort leader = getLeader(space, part);
        if (Objects.isNull(leader)) {
            throw new IllegalArgumentException("Part " + part + " not found in space " + space);
        }

        return doScanVertex(space, leader, part, rowLimit, startTime, endTime);
    }

    private Iterator<ScanVertexResponse> scanVertex(String space,
                                                    Iterator<Integer> parts,
                                                    int rowLimit, long startTime, long endTime) {
        return new Iterator<ScanVertexResponse>() {
            Iterator<ScanVertexResponse> iterator;

            @Override
            public boolean hasNext() {
                return parts.hasNext() || iterator.hasNext();
            }

            @Override
            public ScanVertexResponse next() {
                if (Objects.isNull(iterator) || !iterator.hasNext()) {
                    int part = parts.next();
                    HostAndPort leader = getLeader(space, part);
                    if (Objects.isNull(leader)) {
                        throw new IllegalArgumentException("Part " + part
                                + " not found in space " + space);
                    }

                    try {
                        iterator = doScanVertex(space, leader, part, rowLimit, startTime, endTime);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                return iterator.next();
            }
        };
    }

    /**
     * Scan all vertex of a partition
     *
     * @param spaceName nebula space id
     * @param leader    host address
     * @return response which contains next start cursor, done if next cursor is empty
     */
    private Iterator<ScanVertexResponse> doScanVertex(String spaceName, HostAndPort leader,
                                                      int part, int rowLimit, long startTime,
                                                      long endTime) throws IOException {
        StorageService.Client client = connect(leader);
        if (Objects.isNull(client)) {
            disconnect(leader);
            throw new IOException("Failed to connect " + leader);
        }

        int spaceID = metaClient.getSpaceIDFromCache(spaceName);
        int retry = executionRetry;
        while (retry-- != 0) {
            new Iterator<ScanVertexResponse>() {
                private byte[] cursor = null;
                private boolean haveNext = true;

                @Override
                public boolean hasNext() {
                    return haveNext;
                }

                @Override
                public ScanVertexResponse next() {
                    ScanVertexRequest request = new ScanVertexRequest();
                    request.setSpace_id(spaceID)
                            .setPart_id(part)
                            .setRow_limit(rowLimit)
                            .setStart_time(startTime)
                            .setCursor(cursor)
                            .setEnd_time(endTime);

                    ScanVertexResponse response;
                    try {
                        response = client.scanVertex(request);
                        cursor = response.next_cursor;
                        haveNext = response.has_next;
                    } catch (TException e) {
                        e.printStackTrace();
                        haveNext = false;
                        return null;
                    }

                    if (!response.result.failed_codes.isEmpty()) {
                        handleResultCodes(response.result.failed_codes, spaceName, client, leader);
                        haveNext = false;
                        return null;
                    } else {
                        return response;
                    }
                }
            };
        }
        throw new IOException("Failed to scan edge within " + executionRetry + " retry");
    }

    /**
     * Check the exec response is successfully
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

    private void updateLeader(String spaceName, int partId, HostAndPort address) {
        LOGGER.debug("Update leader for space " + spaceName + ", " + partId + " to " + address);
        if (!leaders.containsKey(spaceName)) {
            leaders.put(spaceName, Maps.newConcurrentMap());
        }
        leaders.get(spaceName).put(partId, address);
    }

    private void invalidLeader(String spaceName, int partId) {
        LOGGER.debug("Invalid leader for space " + spaceName + ", " + partId);
        if (!leaders.containsKey(spaceName)) {
            leaders.put(spaceName, Maps.newConcurrentMap());
        }
        leaders.get(spaceName).remove(partId);
    }

    private HostAndPort getLeader(String spaceName, int part) {
        if (!leaders.containsKey(spaceName)) {
            leaders.put(spaceName, Maps.newConcurrentMap());
        }

        if (leaders.get(spaceName).containsKey(part)) {
            return leaders.get(spaceName).get(part);
        } else {
            List<HostAndPort> address = metaClient.getPartFromCache(spaceName, part);
            if (address != null) {
                Random random = new Random(System.currentTimeMillis());
                int position = random.nextInt(address.size());
                HostAndPort leader = address.get(position);
                leaders.get(spaceName).put(part, leader);
                return leader;
            }
            return null;
        }
    }

    private void handleResultCodes(List<ResultCode> failedCodes, String space,
                                   StorageService.Client client, HostAndPort leader) {
        for (ResultCode code : failedCodes) {
            if (code.getCode() == ErrorCode.E_LEADER_CHANGED) {
                HostAddr addr = code.getLeader();
                if (addr != null && addr.getIp() != 0 && addr.getPort() != 0) {
                    int ip = addr.getIp();
                    HostAndPort newLeader = HostAndPort.fromParts(
                            AddressUtil.intToIPv4(ip), addr.getPort());
                    updateLeader(space, code.getPart_id(), newLeader);
                    StorageService.Client newClient = clients.get(newLeader);
                    if (newClient != null) {
                        client = newClient;
                        leader = newLeader;
                    }
                }
            }
        }
    }

    private StorageService.Client connect(HostAndPort address) {
        if (!clients.containsKey(address)) {
            try {
                StorageService.Client client = doConnect(address);
                clients.put(address, client);
                return client;
            } catch (TException e) {
                LOGGER.error(e.getMessage());
                return null;
            }
        } else {
            return clients.get(address);
        }
    }

    private void disconnect(HostAndPort address) {
        clients.remove(address);
    }

    private long hash(String key) {
        return MurmurHash2.hash64(key);
    }

    private long hash(long key) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(key);
        return MurmurHash2.hash64(buffer.array(), Long.BYTES);
    }

    private int keyToPartId(int space, long vertexId) {
        // TODO: need to handle this
        if (!partsAlloc.containsKey(space)) {
            LOGGER.error("Invalid part of " + space);
            return -1;
        }
        int partNum = partsAlloc.get(space).size();
        if (partNum <= 0) {
            return -1;
        }
        long hashValue = Long.parseUnsignedLong(Long.toUnsignedString(hash(vertexId)));
        return (int) (Math.floorMod(hashValue, partNum) + 1);
    }

    private int keyToPartId(int space, String key) {
        // TODO: need to handle this
        if (!partsAlloc.containsKey(space)) {
            LOGGER.error("Invalid part of " + space);
            return -1;
        }
        int partNum = partsAlloc.get(space).size();
        if (partNum <= 0) {
            return -1;
        }
        long hashValue = Long.parseUnsignedLong(Long.toUnsignedString(hash(key)));
        return (int) (Math.floorMod(hashValue, partNum) + 1);
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


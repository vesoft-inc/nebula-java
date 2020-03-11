/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.storage.client;

import static com.google.common.base.Preconditions.checkArgument;

import com.facebook.thrift.TException;
import com.facebook.thrift.protocol.TBinaryProtocol;
import com.facebook.thrift.protocol.TProtocol;
import com.facebook.thrift.transport.TSocket;
import com.facebook.thrift.transport.TTransport;
import com.facebook.thrift.transport.TTransportException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.net.HostAndPort;
import com.vesoft.nebula.HostAddr;
import com.vesoft.nebula.Pair;
import com.vesoft.nebula.meta.client.MetaClientImpl;
import com.vesoft.nebula.storage.*;
import com.vesoft.nebula.utils.IPv4IntTransformer;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
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

    private static final int SUCCESS = 0;
    private static final int FAIL = -1;
    private static final int TIMEOUT = -2;

    private TTransport transport = null;
    private Map<HostAddr, StorageService.Client> clientMap;

    private final int connectionRetry;
    private final int timeout;
    private MetaClientImpl metaClient;
    private Map<Integer, Map<Integer, HostAddr>> leaders;
    private Map<Integer, Map<Integer, List<HostAddr>>> partsAlloc;


    /**
     * Constructor
     *
     * @param addresses       The addresses of storage services.
     * @param timeout         The timeout of RPC request.
     * @param connectionRetry The number of retries when connection failure.
     */
    public StorageClientImpl(List<HostAndPort> addresses, int timeout, int connectionRetry) {
        checkArgument(timeout > 0);
        checkArgument(connectionRetry > 0);

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
    public StorageClientImpl(MetaClientImpl metaClient) {
        this(Lists.newArrayList(), DEFAULT_TIMEOUT_MS, DEFAULT_CONNECTION_RETRY_SIZE);
        this.metaClient = metaClient;
        this.metaClient.init();
        this.partsAlloc = this.metaClient.getParts();
    }

    private StorageService.Client connect(HostAddr addr) {
        if (clientMap.containsKey(addr)) {
            return clientMap.get(addr);
        }

        int retry = connectionRetry;
        while (retry-- > 0) {
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

    public int put(int space, int key, int value) {
        return put(space, String.valueOf(key), String.valueOf(value));
    }

    public int put(int space, String key, int value) {
        return put(space, key, String.valueOf(value));
    }
    /**
     * Put key-value pair into partition
     *
     * @param space nebula space id
     * @param key   nebula key
     * @param value nebula value
     * @return
     */
    public int put(int space, String key, String value) {
        int part = keyToPartId(space, key);
        int retry = connectionRetry + 1;
        while (retry-- > 0) {
            HostAddr leader = getLeader(space, part);
            if (leader == null) {
                continue;
            }

            StorageService.Client client = connect(leader);
            if (client == null) {
                switchLeader(space, part);
                clientMap.remove(leader);
                continue;
            }

            //LOGGER.info(String.format("Putting key %s value %s to part %d", key, value, part));
            PutRequest request = new PutRequest();
            request.setSpace_id(space);
            Map<Integer, List<Pair>> parts = Maps.newHashMap();
            List<Pair> pairs = Lists.newArrayList(new Pair(key, value));
            parts.put(part, pairs);
            request.setParts(parts);
            LOGGER.debug(String.format("Put Request: %s", request.toString()));
            LOGGER.info("Putting: " + key + " : " + value + ", to " +
                IPv4IntTransformer.intToIPv4(leader.getIp()) + ":" + leader.getPort());

            ExecResponse response;
            try {
                response = client.put(request);
                if (!isSuccess(response)) {
                    for (ResultCode code : response.result.getFailed_codes()) {
                        if (code.getCode() == ErrorCode.E_LEADER_CHANGED) {
                            LOGGER.info("Code: Leader Changed");
                            HostAddr addr = code.getLeader();
                            if (addr != null && addr.getIp() != 0 && addr.getPort() != 0) {
                                HostAddr newLeader = new HostAddr(addr.getIp(), addr.getPort());
                                LOGGER.info("NEW LEADER:" + IPv4IntTransformer.intToIPv4(newLeader.getIp()) + ":" + newLeader.port);
                                if (newLeader.ip == leader.ip && newLeader.port == leader.port) {
                                    switchLeader(space, part);
                                } else {
                                    updateLeader(space, code.getPart_id(), newLeader);
                                }
                            } else {
                                LOGGER.info("LEADER UNKNOWN");
                                switchLeader(space, part);
                            }
                        } else {
                            LOGGER.info("Code: " + code.getCode());
                        }
                    }
                } else {
                    LOGGER.info("PUT SUCCESS!!!");
                    return SUCCESS;
                }
            } catch (TException e) {
                switchLeader(space, part);
                clientMap.remove(leader);
                LOGGER.error(String.format("Put Failed: %s", e.getMessage()));
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return FAIL;
    }

    public int get(int space, int key) {
        return get(space, String.valueOf(key));
    }

    /**
     * Get key from part
     *
     * @param space nebula space id
     * @param key   nebula key
     * @return
     */
    @Override
    public int get(int space, String key) {
        int part = keyToPartId(space, key);
        int retry = connectionRetry + 1;
        while (retry-- > 0) {
            HostAddr leader = getLeader(space, part);
            if (leader == null) {
                continue;
            }

            StorageService.Client client = connect(leader);
            if (client == null) {
                switchLeader(space, part);
                clientMap.remove(leader);
                continue;
            }

            GetRequest request = new GetRequest();
            request.setSpace_id(space);
            Map<Integer, List<String>> parts = Maps.newHashMap();
            parts.put(part, Arrays.asList(key));
            request.setParts(parts);
            LOGGER.debug(String.format("Get Request: %s", request.toString()));
            LOGGER.info("Getting: " + key +  ", from " +
                IPv4IntTransformer.intToIPv4(leader.getIp()) + ":" + leader.getPort());

            GeneralResponse response;
            try {
                response = client.get(request);
                if (!isSuccess(response)) {
                    for (ResultCode code : response.result.getFailed_codes()) {
                        if (code.getCode() == ErrorCode.E_LEADER_CHANGED) {
                            LOGGER.info("Code: Leader Changed");
                            HostAddr addr = code.getLeader();
                            if (addr != null && addr.getIp() != 0 && addr.getPort() != 0) {
                                HostAddr newLeader = new HostAddr(addr.getIp(), addr.getPort());
                                LOGGER.info("NEW LEADER: " + IPv4IntTransformer.intToIPv4(newLeader.getIp()) + ":" + newLeader.port);
                                if (newLeader.ip == leader.ip && newLeader.port == leader.port) {
                                    switchLeader(space, part);
                                } else {
                                    updateLeader(space, code.getPart_id(), newLeader);
                                }
                            } else {
                                LOGGER.info("LEADER UNKNOWN");
                                switchLeader(space, part);
                            }
                        } else {
                            LOGGER.info("Code: " + code.getCode());
                        }
                    }
                } else {
                    LOGGER.info("GET SUCCESS!!!");
                    return Integer.parseInt(response.values.get(key));
                }
            } catch (TException e) {
                switchLeader(space, part);
                clientMap.remove(leader);
                LOGGER.error(String.format("Get Failed: %s", e.getMessage()));
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return FAIL;
    }

    /*
    public int cas(int space, int key, int expected, int value) {
        return cas(space, String.valueOf(key), String.valueOf(expected), String.valueOf(value));
    }

    public int cas(int space, String key, int expected, int value) {
        return cas(space, key, String.valueOf(expected), String.valueOf(value));
    }
     */

    /**
     * CAS
     *
     * @param space nebula space id
     * @param key   nebula key
     * @return
     */
    /*
    public int cas(int space, String key, String expected, String value) {
        int part = keyToPartId(space, key);
        int retry = connectionRetry + 1;
        while (retry-- > 0) {
            HostAddr leader = getLeader(space, part);
            if (leader == null) {
                continue;
            }

            StorageService.Client client = connect(leader);
            if (client == null) {
                switchLeader(space, part);
                clientMap.remove(leader);
                continue;
            }

            CasRequest request = new CasRequest();
            request.setSpace_id(space);
            Map<Integer, List<Pair>> parts = Maps.newHashMap();
            List<Pair> pairs = Lists.newArrayList(new Pair(key, value, expected));
            parts.put(part, pairs);
            request.setParts(parts);
            LOGGER.debug(String.format("CAS Request: %s", request.toString()));
            LOGGER.info("CAS: " + key +  ":" + value + ", expected: " + expected + ", from: " +
                IPv4IntTransformer.intToIPv4(leader.getIp()) + ":" + leader.getPort());

            ExecResponse response;
            try {
                response = client.cas(request);
                if (!isSuccess(response)) {
                    for (ResultCode code : response.result.getFailed_codes()) {
                        if (code.getCode() == ErrorCode.E_LEADER_CHANGED) {
                            LOGGER.info("Code: Leader Changed");
                            HostAddr addr = code.getLeader();
                            if (addr != null && addr.getIp() != 0 && addr.getPort() != 0) {
                                HostAddr newLeader = new HostAddr(addr.getIp(), addr.getPort());
                                LOGGER.info("NEW LEADER: " + IPv4IntTransformer.intToIPv4(newLeader.getIp()) + ":" + newLeader.port);
                                if (newLeader.ip == leader.ip && newLeader.port == leader.port) {
                                    switchLeader(space, part);
                                } else {
                                    updateLeader(space, code.getPart_id(), newLeader);
                                }
                            } else {
                                LOGGER.info("LEADER UNKNOWN");
                                switchLeader(space, part);
                            }
                        } else if (code.getCode() == ErrorCode.E_TIMEOUT) {
                            LOGGER.info("TIME OUT");
                            return TIMEOUT;
                        } else if (code.getCode() == ErrorCode.E_CONSENSUS_ERROR) {
                            LOGGER.info("Expected value doesn't match. Cas Failed.");
                            return FAIL;
                        } else {
                            LOGGER.info("Code: " + code.getCode());
                        }
                    }
                } else {
                    LOGGER.info("CAS SUCCESS!!!");
                    return SUCCESS;
                }
            } catch (TException e) {
                switchLeader(space, part);
                clientMap.remove(leader);
                LOGGER.error(String.format("Cas Failed: %s", e.getMessage()));
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return FAIL;
    }
     */

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
        LOGGER.info("Update leader to " + IPv4IntTransformer.intToIPv4(addr.getIp()) + ":" + addr.port);
        if (!leaders.containsKey(spaceId)) {
            leaders.put(spaceId, new ConcurrentHashMap<>());
        }
        leaders.get(spaceId).put(partId, addr);
    }

    private void switchLeader(int spaceId, int partId) {
        LOGGER.info("Switch leader for part: " + partId);
        if (!leaders.containsKey(spaceId)) {
            leaders.put(spaceId, new ConcurrentHashMap<>());
        } else {
            if (!leaders.get(spaceId).containsKey(partId)) {
                return;
            }
            HostAddr addr = leaders.get(spaceId).get(partId);
            List<HostAddr> addrs = metaClient.getPart(spaceId, partId);
            int idx = -1;
            for (int i = 0; i < addrs.size(); i++) {
                if (addrs.get(i).equals(addr)) {
                    idx = i;
                    break;
                }
            }
            if (idx != -1) {
                int newIdx = idx == addrs.size() - 1 ? 0 : idx + 1;
                leaders.get(spaceId).put(partId, addrs.get(newIdx));
            } else {
                leaders.get(spaceId).remove(partId);
            }
        }
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
    @Override
    public void close() throws Exception {
    }
}


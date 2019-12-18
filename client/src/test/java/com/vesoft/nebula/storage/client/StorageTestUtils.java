/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.storage.client;

import com.facebook.thrift.protocol.TBinaryProtocol;
import com.facebook.thrift.protocol.TProtocol;
import com.facebook.thrift.transport.TSocket;
import com.facebook.thrift.transport.TTransport;
import com.vesoft.nebula.client.storage.StorageClient;
import com.vesoft.nebula.client.storage.StorageClientImpl;
import com.vesoft.nebula.storage.StorageService;
import org.apache.commons.lang.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StorageTestUtils {
    private NebulaStorageServer server;
    private StorageService.Client client;
    private StorageClient storageClient;
    private int port;

    private static final Logger LOGGER = LoggerFactory.getLogger(StorageTestUtils.class);

    public StorageTestUtils() throws Exception {
        port = randomPort(5000, 6000);
        buildServer();
        buildClient();
    }

    private void buildServer() throws Exception {
        server = new NebulaStorageServer(port);
        server.start();
        server.waitUntilStarted();
    }

    private void buildClient() throws Exception {
        TTransport transport = new TSocket("localhost", port);
        TProtocol protocol = new TBinaryProtocol(transport);
        client = new StorageService.Client(protocol);
        transport.open();

        storageClient = new StorageClientImpl("localhost", port);
    }

    public void stop() {
        server.stopServer();
    }

    public StorageService.Client getClient() {
        return client;
    }

    public StorageClient getStorageClient() {
        return storageClient;
    }

    private int randomPort(int min, int max) {
        return min + RandomUtils.nextInt(max - min);
    }
}

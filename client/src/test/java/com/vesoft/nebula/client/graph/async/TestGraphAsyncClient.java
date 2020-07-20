/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.graph.async;

import com.facebook.thrift.TException;
import com.google.common.collect.Lists;
import com.google.common.net.HostAndPort;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Random;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestGraphAsyncClient {
    private static Logger LOGGER = LoggerFactory.getLogger(TestGraphAsyncClient.class);

    @Test(timeout = 6000)
    public void testGraphAsyncClientTime() throws TException, InterruptedException {
        MockNoResponseServer server = new MockNoResponseServer();
        server.start();
        synchronized (server) {
            server.wait();
        }
        int port = server.port;
        int timeout = 1000;

        AsyncGraphClient asyncGraphClient = null;
        try {
            HostAndPort address = HostAndPort.fromParts("127.0.0.1", port);
            asyncGraphClient = new AsyncGraphClientImpl(Lists.newArrayList(address),
                timeout, 1, 3);
            asyncGraphClient.connect();
        } finally {
            if (asyncGraphClient != null) {
                asyncGraphClient.close();
            }
        }
    }

    private static class MockNoResponseServer extends Thread {
        private volatile int port = nextPort();

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try (ServerSocket socket = new ServerSocket(port)) {
                    LOGGER.info("Mock socket bind to " + port);
                    synchronized (MockNoResponseServer.this) {
                        notifyAll();
                    }
                    while (!Thread.currentThread().isInterrupted()) {
                        socket.accept();
                    }
                } catch (IOException e) {
                    port = nextPort();
                }
            }
        }

        private static int nextPort() {
            return new Random().nextInt(55535) + 10000;
        }
    }
}

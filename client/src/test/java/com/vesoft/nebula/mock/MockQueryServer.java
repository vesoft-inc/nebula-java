/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.mock;

import com.facebook.thrift.TProcessor;
import com.facebook.thrift.TProcessorFactory;
import com.facebook.thrift.protocol.TProtocol;
import com.facebook.thrift.server.TRpcConnectionContext;
import com.facebook.thrift.server.TServer;
import com.facebook.thrift.transport.TServerSocket;
import com.facebook.thrift.transport.TTransport;
import com.facebook.thrift.transport.TTransportException;
import com.vesoft.nebula.graph.GraphService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MockQueryServer extends Thread {
    private static final Logger LOGGER = LoggerFactory.getLogger(MockQueryServer.class);
    private TServer server;

    public MockQueryServer(int port) {
        TProcessor processor = new GraphService.Processor(new MockQueryProcessor());
        TProcessorFactory factory = new TProcessorFactory(processor);
        TServerSocket serverSocket = null;

        try {
            serverSocket = new TServerSocket(port);
        } catch (TTransportException e) {
            e.printStackTrace();
        }

        server = new TServer(factory, serverSocket) {
            @Override
            public void serve() {
                LOGGER.info("TServer start " + port);
                try {
                    serverTransport_.listen();
                } catch (TTransportException ttx) {
                    LOGGER.error("Error occurred during listening.", ttx);
                    return;
                }

                while (true) {
                    TTransport client;
                    TProcessor processor;
                    TTransport inputTransport = null;
                    TTransport outputTransport = null;
                    TProtocol inputProtocol;
                    TProtocol outputProtocol;
                    try {
                        client = serverTransport_.accept();
                        if (client != null) {
                            processor = processorFactory_.getProcessor(client);

                            inputTransport = inputTransportFactory_.getTransport(client);
                            inputProtocol = inputProtocolFactory_.getProtocol(inputTransport);
                            outputTransport = outputTransportFactory_.getTransport(client);
                            outputProtocol = outputProtocolFactory_.getProtocol(outputTransport);

                            TRpcConnectionContext serverCtx = new TRpcConnectionContext(client,
                                    inputProtocol, outputProtocol);
                            while (processor.process(inputProtocol, outputProtocol, serverCtx)) {

                            }
                        }
                    } catch (Exception x) {
                        LOGGER.error("Error occurred during processing of message.", x);
                    }

                    if (inputTransport != null) {
                        inputTransport.close();
                    }

                    if (outputTransport != null) {
                        outputTransport.close();
                    }
                }
            }
        };
    }

    @Override
    public void run() {
        startServer();
    }

    public void startServer() {
        server.serve();
        synchronized (server) {
            server.notify();
        }
    }

    public void stopServer() {
        server.stop();
    }
}


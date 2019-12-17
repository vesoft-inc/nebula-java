package com.vesoft.nebula;

import com.facebook.thrift.TException;
import com.facebook.thrift.async.TAsyncClientManager;
import com.facebook.thrift.protocol.TBinaryProtocol;
import com.facebook.thrift.protocol.TProtocolFactory;
import com.facebook.thrift.transport.TNonblockingSocket;
import com.facebook.thrift.transport.TNonblockingTransport;
import com.facebook.thrift.transport.TTransportException;
import com.google.common.net.HostAndPort;
import com.google.common.util.concurrent.ListeningExecutorService;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public abstract class AsyncAbstractClient extends AbstractClient {
    protected ListeningExecutorService service;
    protected TAsyncClientManager manager;
    protected TNonblockingTransport transport;
    protected TProtocolFactory protocolFactory;

    public AsyncAbstractClient(List<HostAndPort> addresses, int timeout,
                               int connectionRetry, int executionRetry) {
        super(addresses, timeout, connectionRetry, executionRetry);
    }

    public AsyncAbstractClient(List<HostAndPort> addresses) {
        super(addresses);
    }

    public AsyncAbstractClient(String host, int port) {
        super(host, port);
    }

    @Override
    public int connect() {
        int retry = connectionRetry;
        while (retry-- != 0) {
            Random random = new Random(System.currentTimeMillis());
            int position = random.nextInt(addresses.size());
            HostAndPort address = addresses.get(position);
            try {
                manager = new TAsyncClientManager();
                transport = new TNonblockingSocket(address.getHostText(),
                        address.getPort(), timeout);
                protocolFactory = new TBinaryProtocol.Factory();
                return doConnect(address);
            } catch (TTransportException transportException) {
                transportException.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    @Override
    public void close() {
        service.shutdown();
        transport.close();
        try {
            manager.stop();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

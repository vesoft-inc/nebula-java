/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.meta.client.async;

import com.facebook.thrift.TBase;
import com.facebook.thrift.async.TAsyncClientManager;
import com.facebook.thrift.protocol.TBinaryProtocol;
import com.facebook.thrift.protocol.TProtocolFactory;
import com.facebook.thrift.transport.TNonblockingSocket;
import com.facebook.thrift.transport.TNonblockingTransport;
import com.facebook.thrift.transport.TTransportException;
import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.vesoft.nebula.Client;
import com.vesoft.nebula.meta.client.async.entry.ListSpaceCallback;
import com.vesoft.nebula.meta.ListSpacesReq;
import com.vesoft.nebula.meta.ListSpacesResp;
import com.vesoft.nebula.meta.MetaService.AsyncClient;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AsyncMetaClient implements Client {

    private ListeningExecutorService service;

    private AsyncClient client;
    private TAsyncClientManager manager;
    private TNonblockingTransport transport;

    public AsyncMetaClient(String host, int port) {
        // TODO (darion) make it configurable
        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(16);
        service = MoreExecutors.listeningDecorator(new ThreadPoolExecutor(1, 1,
                3000, TimeUnit.MILLISECONDS, queue));

        try {
            manager = new TAsyncClientManager();
            transport = new TNonblockingSocket(host, port);
            transport.open();

            TProtocolFactory protocol = new TBinaryProtocol.Factory();
            client = new AsyncClient(protocol, manager, transport);
            client.setTimeout(DEFAULT_TIMEOUT_MS);
        } catch (TTransportException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ListenableFuture<Optional<ListSpacesResp>> listSpaces() {
        ListenableFuture<Optional<ListSpacesResp>> future = service.submit(new Callable<Optional<ListSpacesResp>>() {
            @Override
            public Optional<ListSpacesResp> call() throws Exception {
                ListSpaceCallback callback = new ListSpaceCallback();
                client.listSpaces(new ListSpacesReq(), callback);
                Optional<TBase> result = callback.getResult();
                if (!result.isPresent()) {
                    return Optional.absent();
                } else {
                    return Optional.of((ListSpacesResp) result.get());
                }
            }
        });
        return future;
    }

    @Override
    public void close() throws Exception {
        transport.close();
        manager.stop();
    }
}

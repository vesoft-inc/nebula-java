/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.storage.client.async;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import com.vesoft.nebula.meta.client.MetaClientImpl;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

import org.checkerframework.checker.nullness.qual.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsyncStorageClientExample {
    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncStorageClientExample.class);
    private static ListeningExecutorService EXECUTOR_SERVICE =
        MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());
    private static final int SPACE = 1;

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: "
                + "com.vesoft.nebula.examples.AsyncStorageClientExample <host> <port>");
            return;
        }
        try {
            MetaClientImpl metaClient = new MetaClientImpl(args[0], Integer.parseInt(args[1]));
            AsyncStorageClient asyncStorageClient = new AsyncStorageClientImpl(metaClient);
            final int count = 10;
            final CountDownLatch putCountDownLatch = new CountDownLatch(count);
            for (int i = 0; i < count; i++) {
                ListenableFuture<Boolean> putFuture = asyncStorageClient.put(SPACE,
                    String.valueOf(i), String.valueOf(i));
                Futures.addCallback(putFuture, new FutureCallback<Boolean>() {
                    @Override
                    public void onSuccess(@Nullable Boolean flag) {
                        if (!flag) {
                            LOGGER.error("Put Failed");
                        }
                        putCountDownLatch.countDown();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        LOGGER.error(String.format("Put Error: %s", throwable.getMessage()));
                        System.exit(-1);
                    }
                }, EXECUTOR_SERVICE);
            }

            try {
                putCountDownLatch.await();
            } catch (InterruptedException e) {
                LOGGER.error("Put interrupted");
                System.exit(-1);
            }


            final CountDownLatch getCountDownLatch = new CountDownLatch(count);
            for (int i = 0; i < count; i++) {
                ListenableFuture<Optional<String>> getFuture = asyncStorageClient.get(SPACE,
                    String.valueOf(i));
                int finalI = i;
                Futures.addCallback(getFuture, new FutureCallback<Optional<String>>() {
                    @Override
                    public void onSuccess(@Nullable Optional<String> s) {
                        if (s.isPresent() && s.get().equals(String.valueOf(finalI))) {
                            LOGGER.info(String.format("Get Success. Key %s Value %s",
                                String.valueOf(finalI), s.get()));
                        } else {
                            LOGGER.error("Get Failed");
                        }
                        getCountDownLatch.countDown();
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        LOGGER.error(String.format("Get Error: %s", throwable.getMessage()));
                        System.exit(-1);
                    }
                }, EXECUTOR_SERVICE);
            }

            try {
                getCountDownLatch.await();
            } catch (InterruptedException e) {
                LOGGER.error("Get interrupted");
                System.exit(-1);
            }


            LOGGER.info("Done");
            asyncStorageClient.close();
            EXECUTOR_SERVICE.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

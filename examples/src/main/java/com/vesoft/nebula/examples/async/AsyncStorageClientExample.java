/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.examples.async;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import java.util.concurrent.Executors;
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
                + "com.vesoft.nebula.examples.async.AsyncStorageClientExample <host> <port>");
            return;
        }

        // TODO (freddie) Write a great example
    }
}

/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.storage.async;

import com.google.common.base.Optional;
import com.google.common.net.HostAndPort;
import com.google.common.util.concurrent.ListenableFuture;
import com.vesoft.nebula.AsyncAbstractClient;
import java.util.List;


public abstract class AsyncStorageClient extends AsyncAbstractClient {

    public AsyncStorageClient(List<HostAndPort> addresses, int timeout,
                              int connectionRetry, int executionRetry) {
        super(addresses, timeout, connectionRetry, executionRetry);
    }

    public abstract ListenableFuture<Boolean> put(String space, String key, String value);

    public abstract ListenableFuture<Optional<String>> get(String space, String key);

    public abstract ListenableFuture<Boolean> remove(String space, String key);
}

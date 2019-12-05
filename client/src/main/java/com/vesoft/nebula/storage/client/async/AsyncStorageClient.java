/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.storage.client.async;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import com.vesoft.nebula.Client;


public interface AsyncStorageClient extends Client {

    public ListenableFuture<Boolean> put(int space, String key, String value);

    public ListenableFuture<Optional<String>> get(int space, String key);

    public ListenableFuture<Boolean> remove(int space, String key);

    public void close();
}

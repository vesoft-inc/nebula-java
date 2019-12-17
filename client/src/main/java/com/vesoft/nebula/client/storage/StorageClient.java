/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.storage;

import com.google.common.base.Optional;
import com.google.common.net.HostAndPort;
import com.vesoft.nebula.AbstractClient;
import com.vesoft.nebula.HostAddr;
import java.util.List;
import java.util.Map;

public abstract class StorageClient extends AbstractClient {
    protected HostAddr addr;

    public StorageClient(List<HostAndPort> addresses, int timeout,
                         int connectionRetry, int executionRetry) {
        super(addresses, timeout, connectionRetry, executionRetry);
    }

    public StorageClient(List<HostAndPort> addresses) {
        super(addresses);
    }

    public StorageClient(String host, int port) {
        super(host, port);
    }

    public abstract boolean put(String space, String key, String value);

    public abstract boolean put(String space, Map<String, String> kvs);

    public abstract Optional<String> get(String space, String key);

    public abstract Optional<Map<String, String>> get(String space, List<String> keys);

    public abstract boolean remove(String space, String key);

    public abstract boolean remove(String space, List<String> keys);
}

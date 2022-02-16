/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.client.storage;

import com.vesoft.nebula.client.graph.data.HostAddress;
import java.io.Serializable;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StorageConnPool implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(StorageConnPool.class);
    private static final long serialVersionUID = -6459633350295900558L;

    private final GenericKeyedObjectPool<HostAddress, GraphStorageConnection> keyedPool;
    private final StorageConnPoolFactory poolFactory;

    public StorageConnPool(StoragePoolConfig config) {
        poolFactory = new StorageConnPoolFactory(config);

        GenericKeyedObjectPoolConfig poolConfig = new GenericKeyedObjectPoolConfig();
        poolConfig.setMaxIdlePerKey(config.getMaxConnsSize());
        poolConfig.setMinIdlePerKey(config.getMinConnsSize());
        poolConfig.setMinEvictableIdleTimeMillis(
                config.getIdleTime() <= 0 ? Long.MAX_VALUE : config.getIdleTime());
        poolConfig.setMaxTotal(config.getMaxTotal());
        poolConfig.setMaxTotalPerKey(config.getMaxTotalPerKey());

        keyedPool = new GenericKeyedObjectPool<>(poolFactory);
        keyedPool.setConfig(poolConfig);
    }

    public void close() {
        keyedPool.close();
    }

    public GraphStorageConnection getStorageConnection(HostAddress address) throws Exception {
        return keyedPool.borrowObject(address);
    }

    public void release(HostAddress address, GraphStorageConnection connection) {
        keyedPool.returnObject(address, connection);
    }

    public int getNumActive(HostAddress address) {
        return keyedPool.getNumActive(address);
    }

    public int get(HostAddress address) {
        return keyedPool.getNumIdle(address);
    }
}

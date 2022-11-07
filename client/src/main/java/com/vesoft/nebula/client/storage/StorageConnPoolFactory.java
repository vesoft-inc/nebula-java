/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.client.storage;

import com.vesoft.nebula.client.graph.data.HostAddress;
import java.io.Serializable;
import org.apache.commons.pool2.KeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("checkstyle:Indentation")
public class StorageConnPoolFactory
        implements KeyedPooledObjectFactory<HostAddress, GraphStorageConnection>, Serializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(StorageConnPoolFactory.class);

    private final StoragePoolConfig config;

    public StorageConnPoolFactory(StoragePoolConfig config) {
        this.config = config;
    }

    @Override
    public PooledObject<GraphStorageConnection> makeObject(HostAddress address) throws Exception {
        GraphStorageConnection connection = new GraphStorageConnection();
        return new DefaultPooledObject<>(connection);
    }

    @Override
    public void destroyObject(
            HostAddress hostAndPort, PooledObject<GraphStorageConnection> pooledObject) {
        pooledObject.getObject().close();
    }

    @Override
    public boolean validateObject(
            HostAddress hostAndPort, PooledObject<GraphStorageConnection> pooledObject) {
        GraphStorageConnection connection = pooledObject.getObject();
        if (connection == null) {
            return false;
        }
        try {
            return connection.transport.isOpen();
        } catch (Exception e) {
            LOGGER.warn(
                    String.format(
                            "storage connection with %s:%d is not open",
                            hostAndPort.getHost(), hostAndPort.getPort()),
                    e);
            return false;
        }
    }

    @Override
    public void activateObject(
            HostAddress address, PooledObject<GraphStorageConnection> pooledObject)
            throws Exception {
        pooledObject
                .getObject()
                .open(address, config.getTimeout(), config.isEnableSSL(), config.getSslParam());
    }

    @Override
    public void passivateObject(
            HostAddress hostAndPort, PooledObject<GraphStorageConnection> pooledObject) {
        pooledObject.markReturning();
    }
}

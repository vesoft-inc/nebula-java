/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.graph.net;

import com.vesoft.nebula.client.graph.NebulaPoolConfig;
import com.vesoft.nebula.client.graph.data.HostAddress;
import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

public class ConnObjectPool extends BaseKeyedPooledObjectFactory<HostAddress, SyncConnection> {
    private final NebulaPoolConfig config;

    public ConnObjectPool(NebulaPoolConfig config) {
        this.config = config;
    }

    @Override
    public SyncConnection create(HostAddress key) throws Exception {
        SyncConnection conn = new SyncConnection();
        conn.open(key, config.getTimeout());
        return conn;
    }

    @Override
    public PooledObject<SyncConnection> wrap(SyncConnection connection) {
        return new DefaultPooledObject<>(connection);
    }

    @Override
    public void destroyObject(HostAddress key, PooledObject<SyncConnection> p) throws Exception {
        p.getObject().close();
        super.destroyObject(key, p);
    }

    @Override
    public boolean validateObject(HostAddress key, PooledObject<SyncConnection> p) {
        if (p.getObject() == null) {
            return false;
        }
        return p.getObject().ping();
    }
}

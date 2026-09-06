/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.v3client.graph.net;

import com.vesoft.nebula.driver.v3client.graph.NebulaPoolConfig;
import java.io.Serializable;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * Source-compatibility shim for the v3 client's connection object pool.
 *
 * <p>The v5 driver manages its own client pool; this factory is not used by
 * {@link NebulaPool#getSession} and {@link #create()} is unsupported.
 */
public class ConnObjectPool extends BasePooledObjectFactory<SyncConnection>
    implements Serializable {

    private static final long serialVersionUID = 6101157301791971560L;

    private final NebulaPoolConfig config;
    private final LoadBalancer loadBalancer;

    public ConnObjectPool(LoadBalancer loadBalancer, NebulaPoolConfig config) {
        this.loadBalancer = loadBalancer;
        this.config = config;
    }

    @Override
    public SyncConnection create() throws Exception {
        throw new UnsupportedOperationException(
            "The v3 connection object pool is not used by the v5 driver.");
    }

    @Override
    public PooledObject<SyncConnection> wrap(SyncConnection connection) {
        return new DefaultPooledObject<>(connection);
    }

    @Override
    public void destroyObject(PooledObject<SyncConnection> p) throws Exception {
        p.getObject().close();
        super.destroyObject(p);
    }

    @Override
    public boolean validateObject(PooledObject<SyncConnection> p) {
        return p.getObject() != null;
    }

    @SuppressWarnings("unused")
    public boolean init() {
        return loadBalancer.isServersOK();
    }

    @SuppressWarnings("unused")
    public void updateServerStatus() {
        loadBalancer.updateServersStatus();
    }

    @SuppressWarnings("unused")
    public NebulaPoolConfig getConfig() {
        return config;
    }
}

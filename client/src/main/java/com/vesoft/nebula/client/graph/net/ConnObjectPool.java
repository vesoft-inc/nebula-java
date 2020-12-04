package com.vesoft.nebula.client.graph.net;

import com.vesoft.nebula.client.graph.NebulaPoolConfig;
import com.vesoft.nebula.client.graph.data.HostAddress;
import com.vesoft.nebula.client.graph.exception.IOErrorException;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

public class ConnObjectPool extends BasePooledObjectFactory<SyncConnection> {
    private final NebulaPoolConfig config;
    private LoadBalancer loadBalancer;
    private static final int retryTime = 3;

    public ConnObjectPool(LoadBalancer loadBalancer, NebulaPoolConfig config) {
        this.loadBalancer = loadBalancer;
        this.config = config;
    }

    @Override
    public SyncConnection create() throws IOErrorException {
        int retry = loadBalancer.getHostNum();
        SyncConnection conn = new SyncConnection();
        while (retry-- > 0) {
            try {
                HostAddress address = loadBalancer.getAddress();
                if (address == null) {
                    throw new IOErrorException(IOErrorException.E_ALL_BROKEN,
                        "All servers are broken.");
                }
                conn.open(address, config.getTimeout());
                return conn;
            } catch (IOErrorException e) {
                this.loadBalancer.updateServersStatus();
            }
        }
        if (retry == 0) {
            throw new IOErrorException(IOErrorException.E_ALL_BROKEN, "All servers are broken.");
        }
        return null;
    }

    @Override
    public PooledObject<SyncConnection> wrap(SyncConnection connection) {
        return new DefaultPooledObject<>(connection);
    }

    @Override
    public void destroyObject(PooledObject<SyncConnection> p) throws Exception {
        p.getObject().close();
        // TODO: update the server connection num into load balancer
        super.destroyObject(p);
    }

    @Override
    public boolean validateObject(PooledObject<SyncConnection> p) {
        if (p.getObject() == null) {
            return false;
        }
        return true;
    }

    public boolean init() {
        return loadBalancer.isServersOK();
    }

    public void updateServerStatus() {
        loadBalancer.updateServersStatus();
    }
}

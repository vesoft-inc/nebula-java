package com.vesoft.nebula.client.graph.net;

import com.vesoft.nebula.client.graph.NebulaPoolConfig;
import com.vesoft.nebula.client.graph.data.HostAddress;
import com.vesoft.nebula.client.graph.exception.IOErrorException;
import java.util.List;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

public class ConnObjectPool extends BasePooledObjectFactory<SyncConnection> {
    private final RoundRobinLoadBalancer loadBalancer;
    private NebulaPoolConfig config;

    public ConnObjectPool(List<HostAddress> addresses, NebulaPoolConfig config) {
        this.loadBalancer = new RoundRobinLoadBalancer(addresses, config.getTimeout());
        this.config = config;
    }

    @Override
    public SyncConnection create() throws IOErrorException {
        HostAddress address = this.loadBalancer.getAddress();
        if (address == null) {
            throw new IOErrorException(IOErrorException.E_ALL_BROKEN,
                    "All servers are broken.");
        }
        int retry = this.config.getRetryConnectTimes();
        while (retry-- > 0) {
            try {
                SyncConnection conn = new SyncConnection();
                conn.open(address, this.config.getTimeout());
                return conn;
            } catch (IOErrorException e) {
                if (retry == 0) {
                    throw e;
                }
                this.loadBalancer.updateServersStatus();
            }
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
        super.destroyObject(p);
    }

    @Override
    public boolean validateObject(PooledObject<SyncConnection> p) {
        if (!this.loadBalancer.ping(p.getObject().getServerAddress())) {
            p.getObject().close();
            return false;
        }
        return true;
    }

    public boolean init() {
        return this.loadBalancer.isServersOK();
    }

    public void updateServerStatus() {
        this.loadBalancer.updateServersStatus();
    }
}

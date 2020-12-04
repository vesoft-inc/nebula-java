/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.graph.net;

import com.vesoft.nebula.client.graph.NebulaPoolConfig;
import com.vesoft.nebula.client.graph.data.HostAddress;
import com.vesoft.nebula.client.graph.exception.AuthFailedException;
import com.vesoft.nebula.client.graph.exception.IOErrorException;
import com.vesoft.nebula.client.graph.exception.InvalidConfigException;
import com.vesoft.nebula.client.graph.exception.NotValidConnectionException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.pool2.impl.AbandonedConfig;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NebulaPool {
    private GenericObjectPool<SyncConnection> objectPool = null;
    private LoadBalancer loadBalancer;
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    // the wait time to get idle connection, unit ms
    private final int waitTime = 60 * 1000;

    private List<HostAddress> hostToIp(List<HostAddress> addresses)
        throws UnknownHostException {
        List<HostAddress> newAddrs = new ArrayList<>();
        for (HostAddress addr : addresses) {
            String ip = InetAddress.getByName(addr.getHost()).getHostAddress();
            newAddrs.add(new HostAddress(ip, addr.getPort()));
        }
        return newAddrs;
    }

    private void checkConfig(NebulaPoolConfig config) {
        if (config.getIdleTime() < 0) {
            throw new InvalidConfigException(
                "Config idleTime:" + config.getIdleTime() + " is illegal");
        }

        if (config.getMaxConnSize() <= 0) {
            throw new InvalidConfigException(
                "Config maxConnSize:" + config.getMaxConnSize() + " is illegal");
        }

        if (config.getMinConnSize() < 0 || config.getMinConnSize() > config.getMaxConnSize()) {
            throw new InvalidConfigException(
                "Config minConnSize:" + config.getMinConnSize() + " is illegal");
        }

        if (config.getTimeout() < 0) {
            throw new InvalidConfigException(
                "Config timeout:" + config.getTimeout() + " is illegal");
        }
    }

    public boolean init(List<HostAddress> addresses, NebulaPoolConfig config)
            throws UnknownHostException, InvalidConfigException {
        checkConfig(config);
        List<HostAddress> newAddrs = hostToIp(addresses);
        this.loadBalancer = new RoundRobinLoadBalancer(newAddrs, config.getTimeout());
        ConnObjectPool objectPool = new ConnObjectPool(this.loadBalancer, config);
        this.objectPool = new GenericObjectPool<>(objectPool);
        GenericObjectPoolConfig objConfig = new GenericObjectPoolConfig();
        objConfig.setMinIdle(config.getMinConnSize());
        objConfig.setMaxTotal(config.getMaxConnSize());
        objConfig.setMinEvictableIdleTimeMillis(
                config.getIdleTime() <= 0 ? Long.MAX_VALUE : config.getIdleTime());
        this.objectPool.setConfig(objConfig);

        AbandonedConfig abandonedConfig = new AbandonedConfig();
        abandonedConfig.setRemoveAbandonedOnBorrow(true);
        this.objectPool.setAbandonedConfig(abandonedConfig);
        return objectPool.init();
    }

    public void close() {
        this.loadBalancer.close();
        this.objectPool.close();
    }

    public Session getSession(String userName, String password, boolean reconnect)
            throws NotValidConnectionException, IOErrorException, AuthFailedException {
        SyncConnection connection = getConnection();
        log.info(String.format("Get connection to %s:%d",
            connection.getServerAddress().getHost(),
            connection.getServerAddress().getPort()));
        long sessionID = connection.authenticate(userName, password);
        returnConnection(connection);
        return new Session(sessionID,this, reconnect);
    }

    public SyncConnection getConnection() throws NotValidConnectionException {
        SyncConnection connection;
        try {
            connection = objectPool.borrowObject(waitTime);
        } catch (Exception e) {
            throw new NotValidConnectionException(e.getMessage());
        }
        if (connection == null) {
            throw new NotValidConnectionException("Get connection object failed.");
        }
        return connection;
    }

    public void setInvalidateConn(SyncConnection connection) {
        try {
            objectPool.invalidateObject(connection);
        } catch (Exception e) {
            log.warn("Set connection invalidate failed");
        }
    }

    public void returnConnection(SyncConnection connection) {
        try {
            objectPool.returnObject(connection);
        } catch (Exception e) {
            log.warn("Return object to pool failed.");
        }
    }

    public int getActiveConnNum() {
        return objectPool.getNumActive();
    }

    public int getIdleConnNum() {
        return objectPool.getNumIdle();
    }

    public int getCanUseNum() {
        if (objectPool.getMaxTotal() - objectPool.getCreatedCount() > 0) {
            return (int) (objectPool.getMaxTotal()
                - objectPool.getCreatedCount() + objectPool.getNumIdle());
        }
        return objectPool.getNumIdle();
    }

    public int getWaitersNum() {
        return objectPool.getNumWaiters();
    }

    public void updateServerStatus() {
        if (objectPool.getFactory() instanceof ConnObjectPool) {
            ((ConnObjectPool)objectPool.getFactory()).updateServerStatus();
        }
    }
}

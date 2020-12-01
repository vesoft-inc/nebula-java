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

    public boolean init(List<HostAddress> addresses, NebulaPoolConfig config)
            throws UnknownHostException {
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
        try {
            // If no idle connection, try once
            int retry = getIdleConnNum() == 0 ? 1 : getIdleConnNum();
            SyncConnection connection = null;
            while (retry-- > 0) {
                connection = objectPool.borrowObject(waitTime);
                if (connection == null || !connection.ping()) {
                    continue;
                }
                break;
            }
            if (connection == null) {
                throw new NotValidConnectionException("Get connection object failed.");
            }
            log.info(String.format("Get connection to %s:%d",
                     connection.getServerAddress().getHost(),
                     connection.getServerAddress().getPort()));
            long sessionID = connection.authenticate(userName, password);
            return new Session(connection, sessionID, this.objectPool, reconnect);
        } catch (NotValidConnectionException | AuthFailedException | IOErrorException e) {
            throw e;
        } catch (IllegalStateException e) {
            throw new NotValidConnectionException(e.getMessage());
        } catch (Exception e) {
            throw new IOErrorException(IOErrorException.E_UNKNOWN, e.getMessage());
        }
    }

    public int getActiveConnNum() {
        return objectPool.getNumActive();
    }

    public int getIdleConnNum() {
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

    private List<HostAddress> hostToIp(List<HostAddress> addresses)
            throws UnknownHostException {
        List<HostAddress> newAddrs = new ArrayList<>();
        for (HostAddress addr : addresses) {
            String ip = InetAddress.getByName(addr.getHost()).getHostAddress();
            newAddrs.add(new HostAddress(ip, addr.getPort()));
        }
        return newAddrs;
    }
}

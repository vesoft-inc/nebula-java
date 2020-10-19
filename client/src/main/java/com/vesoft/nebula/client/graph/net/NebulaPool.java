package com.vesoft.nebula.client.graph.net;

import com.vesoft.nebula.client.graph.NebulaPoolConfig;
import com.vesoft.nebula.client.graph.data.HostAddress;
import com.vesoft.nebula.client.graph.exception.AuthFailedException;
import com.vesoft.nebula.client.graph.exception.IOErrorException;
import com.vesoft.nebula.client.graph.exception.NotValidConnectionException;
import java.util.List;
import org.apache.commons.pool2.impl.AbandonedConfig;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NebulaPool {
    private GenericObjectPool<SyncConnection> objectPool = null;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public boolean init(List<HostAddress> addresses, NebulaPoolConfig config) {
        ConnObjectPool objectPool = new ConnObjectPool(addresses, config);
        this.objectPool = new GenericObjectPool<>(objectPool);
        GenericObjectPoolConfig objConfig = new GenericObjectPoolConfig();
        objConfig.setMaxIdle(config.getIdleTime());
        objConfig.setMaxTotal(config.getMaxConnSize());
        this.objectPool.setConfig(objConfig);

        AbandonedConfig abandonedConfig = new AbandonedConfig();
        abandonedConfig.setRemoveAbandonedOnBorrow(true);
        this.objectPool.setAbandonedConfig(abandonedConfig);
        return objectPool.init();
    }

    public void close() {
        this.objectPool.close();
    }

    public Session getSession(String userName, String password, boolean reconnect)
            throws NotValidConnectionException, IOErrorException, AuthFailedException {
        try {
            SyncConnection connection = objectPool.borrowObject(1000);
            if (connection == null) {
                throw new NotValidConnectionException("Get connection object failed.");
            }
            log.info(String.format("Get connection to %s:%d",
                    connection.getServerAddress().getHost(),
                    connection.getServerAddress().getPort()));
            Session session = new Session(connection, this.objectPool, reconnect);
            session.auth(userName, password);
            return session;
        } catch (NotValidConnectionException | AuthFailedException | IOErrorException e) {
            throw e;
        } catch (IllegalStateException e) {
            throw new NotValidConnectionException(e.getMessage());
        } catch (Exception e) {
            throw new IOErrorException(IOErrorException.E_UNKNOWN, e.getMessage());
        }
    }

    public int getActiveConnNum() {
        return this.objectPool.getNumActive();
    }

    public int getIdleConnNum() {
        return this.objectPool.getNumIdle();
    }

    public int getWaitersNum() {
        return this.objectPool.getNumWaiters();
    }

    public void updateServerStatus() {
        if (this.objectPool.getFactory() instanceof ConnObjectPool) {
            ((ConnObjectPool)this.objectPool.getFactory()).updateServerStatus();
        }
    }
}
package com.vesoft.nebula.database.pool;

import com.vesoft.nebula.database.ConnectionManager;
import com.vesoft.nebula.database.NebulaConnection;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

/**
 * @author huangzhaolai-jk
 * @version 1.0.0
 * @Description PoolConnectionManager is used for
 * @Date 2020/3/17 - 15:24
 */
@Slf4j
public class PoolConnectionManager implements ConnectionManager {

    /**
     * maxConnectionCount
     */
    private int maxConnectionCount;

    /**
     * activeConnectionCount
     */
    private int activeConnectionCount;

    /**
     * freeConnectionCount
     */
    private int freeConnectionCount;

    /**
     * Free connection pool
     */
    private final List<NebulaConnection> FREE_LIST = new Vector<>();
    /**
     * Active connection pool
     */
    private final List<NebulaConnection> ACTIVE_LIST = new Vector<>();

    PoolConnectionManager(int maxConnectionCount) {
        this.maxConnectionCount = maxConnectionCount;
    }

    @Override
    public int maxConnectionCount() {
        return this.maxConnectionCount;
    }

    @Override
    public int currentConnectionCount() {
        return this.activeConnectionCount + this.freeConnectionCount;
    }

    @Override
    public int activeConnectionCount() {
        return this.activeConnectionCount;
    }

    @Override
    public int freeConnectionCount() {
        return this.freeConnectionCount;
    }

    @Override
    public NebulaConnection getConnection() {
        NebulaConnection nebulaConnection = null;
        if (FREE_LIST.size() > 0) {
            synchronized (FREE_LIST) {
                if (FREE_LIST.size() > 0) {
                    nebulaConnection = FREE_LIST.get(0);
                    //空闲队列减少
                    boolean remove = FREE_LIST.remove(nebulaConnection);
                    if (remove) {
                        freeConnectionCount--;
                    }
                    log.trace("Free connection pool remove nebulaConnection:" + nebulaConnection + "-" + remove);
                }
            }
        }
        if (nebulaConnection != null && nebulaConnection.isOpened() && canAddConnection()) {
            synchronized (ACTIVE_LIST) {
                if (nebulaConnection.isOpened() && canAddConnection()) {
                    //判断可用，并加入活跃数;
                    ACTIVE_LIST.add(nebulaConnection);
                    activeConnectionCount++;
                    return nebulaConnection;
                }
            }
        }
        return null;
    }

    @Override
    public boolean canAddConnection() {
        return currentConnectionCount() < maxConnectionCount;
    }

    @Override
    public boolean addConnection(NebulaConnection connection) {
        if (connection != null && canAddConnection()) {
            if (connection.isOpened()) {
                synchronized (ACTIVE_LIST) {
                    if (canAddConnection() && connection.isOpened()) {
                        ACTIVE_LIST.add(connection);
                        activeConnectionCount++;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void releaseConnection(NebulaConnection connection) {
        synchronized (ACTIVE_LIST) {
            boolean remove = ACTIVE_LIST.remove(connection);
            if (remove) {
                activeConnectionCount--;
            }
            log.trace("Active connection pool remove nebulaConnection:" + connection + "-" + remove);
        }
        if (connection != null && connection.isOpened() && canAddConnection()) {
            synchronized (FREE_LIST) {
                if (connection.isOpened() && canAddConnection()) {
                    FREE_LIST.add(connection);
                    freeConnectionCount++;
                }
            }
        }

    }


}

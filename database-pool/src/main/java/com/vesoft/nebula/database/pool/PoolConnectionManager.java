/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.database.pool;

import com.vesoft.nebula.database.ConnectionManager;
import com.vesoft.nebula.database.NebulaConnection;
import java.util.List;
import java.util.Vector;
import lombok.extern.slf4j.Slf4j;

/**
 * @author huangzhaolai-jk
 * @version 1.0.0
 * @Description PoolConnectionManager is used for manage  connections
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
    private final List<NebulaConnection> freeList = new Vector<>();
    /**
     * Active connection pool
     */
    private final List<NebulaConnection> activeList = new Vector<>();

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
        if (freeList.size() > 0) {
            synchronized (freeList) {
                if (freeList.size() > 0) {
                    nebulaConnection = freeList.get(0);
                    //空闲队列减少
                    boolean remove = freeList.remove(nebulaConnection);
                    if (remove) {
                        freeConnectionCount--;
                    }
                    log.trace("Free connection pool remove nebulaConnection:"
                            + nebulaConnection + "-" + remove);
                }
            }
        }
        if (nebulaConnection != null && nebulaConnection.isOpened() && canAddConnection()) {
            synchronized (activeList) {
                if (nebulaConnection.isOpened() && canAddConnection()) {
                    //判断可用，并加入活跃数;
                    activeList.add(nebulaConnection);
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
                synchronized (activeList) {
                    if (canAddConnection() && connection.isOpened()) {
                        activeList.add(connection);
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
        synchronized (activeList) {
            boolean remove = activeList.remove(connection);
            if (remove) {
                activeConnectionCount--;
            }
            log.trace("Active connection pool remove nebulaConnection:"
                    + connection + "-" + remove);
        }
        if (connection != null && connection.isOpened() && canAddConnection()) {
            synchronized (freeList) {
                if (connection.isOpened() && canAddConnection()) {
                    freeList.add(connection);
                    freeConnectionCount++;
                }
            }
        }

    }


}

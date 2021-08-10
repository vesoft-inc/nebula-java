/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.graph.net;

import com.vesoft.nebula.client.graph.data.HostAddress;
import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.exception.IOErrorException;
import com.vesoft.nebula.graph.ExecutionResponse;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Session {
    private final long sessionID;
    private SyncConnection connection;
    private final NebulaPool pool;
    private final Boolean retryConnect;
    private final AtomicBoolean connectionIsBroken = new AtomicBoolean(false);
    private final Logger log = LoggerFactory.getLogger(getClass());

    public Session(SyncConnection connection,
                   long sessionID,
                   NebulaPool connPool,
                   Boolean retryConnect) {
        this.connection = connection;
        this.sessionID = sessionID;
        this.pool = connPool;
        this.retryConnect = retryConnect;
    }

    /**
     * Execute the query sentence.
     *
     * @param stmt The query sentence.
     * @return The ResultSet.
     */
    public synchronized ResultSet execute(String stmt) throws IOErrorException {
        if (connection == null) {
            throw new IOErrorException(IOErrorException.E_CONNECT_BROKEN,
                "The session was released, couldn't use again.");
        }

        if (connectionIsBroken.get() && retryConnect) {
            if (retryConnect()) {
                ExecutionResponse resp = connection.execute(sessionID, stmt);
                return new ResultSet(resp);
            } else {
                throw new IOErrorException(IOErrorException.E_ALL_BROKEN,
                    "All servers are broken.");
            }
        }

        try {
            if (connection == null) {
                throw new IOErrorException(IOErrorException.E_CONNECT_BROKEN,
                        "Connection is null");
            }
            ExecutionResponse resp = connection.execute(sessionID, stmt);
            return new ResultSet(resp);
        } catch (IOErrorException ie) {
            if (ie.getType() == IOErrorException.E_CONNECT_BROKEN) {
                connectionIsBroken.set(true);
                pool.updateServerStatus();

                if (retryConnect) {
                    if (retryConnect()) {
                        connectionIsBroken.set(false);
                        ExecutionResponse resp = connection.execute(sessionID, stmt);
                        return new ResultSet(resp);
                    } else {
                        connectionIsBroken.set(true);
                        throw new IOErrorException(IOErrorException.E_ALL_BROKEN,
                                "All servers are broken.");
                    }
                }
            }
            throw ie;
        }
    }

    private boolean retryConnect() {
        try {
            pool.setInvalidateConnection(connection);
            SyncConnection newConn = pool.getConnection();
            if (newConn == null) {
                log.error("Get connection object failed.");
                return false;
            }
            connection = newConn;
            return true;
        } catch (Exception e) {
            log.error("Reconnected failed: " + e);
            return false;
        }
    }

    public synchronized boolean ping() {
        if (connection == null) {
            return false;
        }
        return connection.ping();
    }

    public synchronized void release() {
        if (connection == null) {
            return;
        }
        try {
            connection.signout(sessionID);
            pool.returnConnection(connection);
        } catch (Exception e) {
            log.warn("Signout out failed or return object to pool failed:" + e.getMessage());
        }
        connection = null;
    }

    public synchronized HostAddress getGraphHost() {
        if (connection == null) {
            return null;
        }
        return connection.getServerAddress();
    }
}

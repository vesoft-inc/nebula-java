/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.graph.net;

import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.exception.IOErrorException;
import com.vesoft.nebula.graph.ExecutionResponse;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Session {
    private final long sessionID;
    private SyncConnection connection;
    private final GenericObjectPool<SyncConnection> pool;
    private final Boolean retryConnect;
    private final Logger log = LoggerFactory.getLogger(getClass());

    public Session(SyncConnection connection,
                   long sessionID,
                   GenericObjectPool<SyncConnection> connPool,
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
    public ResultSet execute(String stmt) throws IOErrorException {
        try {
            if (connection == null) {
                throw new IOErrorException(IOErrorException.E_CONNECT_BROKEN,
                        "Connection is null");
            }
            ExecutionResponse resp = connection.execute(sessionID, stmt);
            return new ResultSet(resp);
        } catch (IOErrorException ie) {
            if (ie.getType() == IOErrorException.E_CONNECT_BROKEN) {
                if (pool.getFactory() instanceof ConnObjectPool) {
                    ((ConnObjectPool) pool.getFactory()).updateServerStatus();
                }

                if (retryConnect) {
                    if (retryConnect()) {
                        ExecutionResponse resp = connection.execute(sessionID, stmt);
                        return new ResultSet(resp);
                    } else {
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
            try {
                pool.invalidateObject(connection);
            } catch (Exception e) {
                log.error("Return object failed");
            }
            SyncConnection newConn = pool.borrowObject();
            if (newConn == null) {
                log.error("Get connection object failed.");
            }
            connection = newConn;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Need server supported, v1.0 nebula-graph doesn't supported
    public boolean ping() {
        if (connection == null) {
            return false;
        }
        return connection.ping();
    }

    public void release() {
        if (connection == null) {
            return;
        }
        connection.signout(sessionID);
        try {
            pool.returnObject(connection);
        } catch (Exception e) {
            log.warn("Return object to pool failed.");
        }
    }
}

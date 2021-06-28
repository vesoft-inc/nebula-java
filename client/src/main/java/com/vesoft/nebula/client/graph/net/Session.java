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
    private final int timezoneOffset;
    private SyncConnection connection;
    private final NebulaPool pool;
    private final Boolean retryConnect;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    private final Logger log = LoggerFactory.getLogger(getClass());

    public Session(SyncConnection connection,
                   AuthResult authResult,
                   NebulaPool connPool,
                   Boolean retryConnect) {
        this.connection = connection;
        this.sessionID = authResult.getSessionId();
        this.timezoneOffset = authResult.getTimezoneOffset();
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
        if (isRunning.get()) {
            throw new IOErrorException(
                IOErrorException.E_MULTI_THREADS_USE_CONNECTION,
                "Multi threads use the same session, "
                    + "the previous execution was not completed, current thread is: "
                    + Thread.currentThread().getName());
        }
        isRunning.set(true);
        try {
            if (connection == null) {
                throw new IOErrorException(IOErrorException.E_CONNECT_BROKEN,
                        "Connection is null");
            }
            ExecutionResponse resp = connection.execute(sessionID, stmt);
            return new ResultSet(resp, timezoneOffset);
        } catch (IOErrorException ie) {
            if (ie.getType() == IOErrorException.E_CONNECT_BROKEN) {
                pool.updateServerStatus();

                if (retryConnect) {
                    if (retryConnect()) {
                        ExecutionResponse resp = connection.execute(sessionID, stmt);
                        return new ResultSet(resp, timezoneOffset);
                    } else {
                        throw new IOErrorException(IOErrorException.E_ALL_BROKEN,
                                "All servers are broken.");
                    }
                }
            }
            throw ie;
        } finally {
            isRunning.set(false);
        }
    }

    private boolean retryConnect() {
        try {
            pool.setInvalidateConnection(connection);
            SyncConnection newConn = pool.getConnection();
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
            pool.returnConnection(connection);
        } catch (Exception e) {
            log.warn("Return object to pool failed.");
        }
    }

    public HostAddress getGraphHost() {
        if (connection == null) {
            return null;
        }
        return connection.getServerAddress();
    }
}

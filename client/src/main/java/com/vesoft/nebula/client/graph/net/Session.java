/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.graph.net;

import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.exception.IOErrorException;
import com.vesoft.nebula.graph.ExecutionResponse;

public class Session {
    private final long sessionID;

    private Connection conn;

    private final ConnectionPool pool;

    private final Boolean retryConnect;

    public Session(Connection connection,
                   long sessionID,
                   ConnectionPool connPool,
                   Boolean retryConnect) {
        this.conn = connection;
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
            ExecutionResponse resp = conn.execute(sessionID, stmt);
            return new ResultSet(resp);
        } catch (IOErrorException ie) {
            if (ie.getType() == IOErrorException.E_CONNECT_BROKEN) {
                pool.updateServersStatus();
                if (this.retryConnect) {
                    if (retryConnect()) {
                        ExecutionResponse resp = conn.execute(sessionID, stmt);
                        return new ResultSet(resp);
                    } else {
                        throw new IOErrorException(IOErrorException.E_ALL_BROKEN,
                                "All servers are broken.");
                    }
                }
                throw ie;
            }
        }
        return new ResultSet();
    }

    private Boolean retryConnect() {
        Connection newConn = pool.getConnection();
        if (newConn == null) {
            return false;
        }
        this.conn.close();
        this.conn = newConn;
        return true;
    }

    // Need server supported, v1.0 nebula-graph doesn't supported
    public Boolean ping() {
        if (this.conn == null) {
            return false;
        }
        return this.conn.ping();
    }

    public void release() {
        this.conn.setUsed(false);
        this.conn.signout(sessionID);
    }
}

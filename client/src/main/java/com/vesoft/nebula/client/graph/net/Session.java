/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.graph.net;

import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.exception.AuthFailedException;
import com.vesoft.nebula.client.graph.exception.IOErrorException;
import com.vesoft.nebula.graph.ExecutionResponse;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Session extends Object {
    private long sessionID;
    private SyncConnection connection;
    private final GenericObjectPool<SyncConnection> pool;
    private final Boolean retryConnect;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public Session(SyncConnection connection,
                   GenericObjectPool<SyncConnection> connPool,
                   Boolean retryConnect) {
        this.connection = connection;
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
            if (this.connection == null) {
                throw new IOErrorException(IOErrorException.E_CONNECT_BROKEN,
                        "Connection is null");
            }
            ExecutionResponse resp = connection.execute(sessionID, stmt);
            return new ResultSet(resp);
        } catch (IOErrorException ie) {
            if (ie.getType() == IOErrorException.E_CONNECT_BROKEN) {
                if (this.pool.getFactory() instanceof ConnObjectPool) {
                    ((ConnObjectPool) this.pool.getFactory()).updateServerStatus();
                }
                try {
                    this.pool.invalidateObject(this.connection);
                } catch (Exception e) {
                    log.error("Return object failed");
                }

                if (this.retryConnect) {
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

    public void auth(String userName, String password)
            throws AuthFailedException, IOErrorException {
        try {
            this.sessionID = connection.authenticate(userName, password);
        } catch (AuthFailedException e) {
            throw e;
        } catch (IOErrorException ie) {
            if (ie.getType() == IOErrorException.E_CONNECT_BROKEN) {
                if (this.pool.getFactory() instanceof ConnObjectPool) {
                    ((ConnObjectPool)this.pool.getFactory()).updateServerStatus();
                }
                try {
                    this.pool.invalidateObject(this.connection);
                } catch (Exception e) {
                    log.error("Return object failed");
                }

                if (this.retryConnect) {
                    if (retryConnect()) {
                        this.sessionID = connection.authenticate(userName, password);
                    } else {
                        throw new IOErrorException(IOErrorException.E_ALL_BROKEN,
                                "All servers are broken.");
                    }
                }
                throw ie;
            } else {
                throw ie;
            }
        }
    }

    private boolean retryConnect() {
        try {
            SyncConnection newConn = pool.borrowObject();
            this.connection.close();
            this.connection = newConn;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Need server supported, v1.0 nebula-graph doesn't supported
    public boolean ping() {
        if (this.connection == null) {
            return false;
        }
        return this.connection.ping();
    }

    public void release() {
        this.connection.signout(sessionID);
        try {
            this.pool.returnObject(this.connection);
        } catch (Exception e) {
            return;
        }
    }
}

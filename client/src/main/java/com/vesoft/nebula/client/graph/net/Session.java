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

/**
 * The Session is an object that operates with nebula-graph.
 * It provides an interface  `execute` to send any NGQL.
 * The returned data result `ResultSet` include wrapped string encoding
 * and time zone calculations and Node and Relationship and PathWrapper
 * and DateWrapper and TimeWrapper and DateTimeWrapper.
 * The data type obtained by the user is `ValueWrapper`,
 * which is the wrapper of the original data structure Value returned by the server.
 * The user can directly read the data using the interface of ValueWrapper.
 *
 */
public class Session {
    private final long sessionID;
    private final int timezoneOffset;
    private SyncConnection connection;
    private final NebulaPool pool;
    private final Boolean retryConnect;
    private final AtomicBoolean connectionIsBroken = new AtomicBoolean(false);
    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Constructor
     *
     * @param connection the connection from the pool
     * @param authResult the auth result from graph service
     * @param connPool the connection pool
     * @param retryConnect whether to retry after the connection is disconnected
     */
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
     * Execute the nGql sentence.
     *
     * @param stmt The nGql sentence.
     *             such as insert ngql `INSERT VERTEX person(name) VALUES "Tom":("Tom");`
     * @return The ResultSet
     */
    public synchronized ResultSet execute(String stmt) throws IOErrorException {
        if (connection == null) {
            throw new IOErrorException(IOErrorException.E_CONNECT_BROKEN,
                "The session was released, couldn't use again.");
        }

        if (connectionIsBroken.get() && retryConnect) {
            if (retryConnect()) {
                ExecutionResponse resp = connection.execute(sessionID, stmt);
                return new ResultSet(resp, timezoneOffset);
            } else {
                throw new IOErrorException(IOErrorException.E_ALL_BROKEN,
                    "All servers are broken.");
            }
        }

        try {
            ExecutionResponse resp = connection.execute(sessionID, stmt);
            return new ResultSet(resp, timezoneOffset);
        } catch (IOErrorException ie) {
            if (ie.getType() == IOErrorException.E_CONNECT_BROKEN) {
                connectionIsBroken.set(true);
                pool.updateServerStatus();

                if (retryConnect) {
                    if (retryConnect()) {
                        connectionIsBroken.set(false);
                        ExecutionResponse resp = connection.execute(sessionID, stmt);
                        return new ResultSet(resp, timezoneOffset);
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

    /**
     * Check current connection is ok
     *
     * @return boolean
     */
    public synchronized boolean ping() {
        if (connection == null) {
            return false;
        }
        return connection.ping();
    }

    /**
     * Notifies the server that the session is no longer needed
     * and returns the connection to the pool,
     * and the connection will be reuse.
     * This function is called if the user is no longer using the session.
     */
    public synchronized void release() {
        if (connection == null) {
            return;
        }
        try {
            connection.signout(sessionID);
            pool.returnConnection(connection);
        } catch (Exception e) {
            log.warn("Release session or return object to pool failed:" + e.getMessage());
        }
        connection = null;
    }

    /**
     * Gets the service address of the current connection
     *
     * @return HostAddress the graph service address
     */
    public synchronized HostAddress getGraphHost() {
        if (connection == null) {
            return null;
        }
        return connection.getServerAddress();
    }

    /**
     * set current connection is invalid, and get a new connection from the pool,
     * if get connection failed, return false, else return true
     *
     * @return true or false
     */
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
}

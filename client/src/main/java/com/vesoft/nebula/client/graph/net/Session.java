/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.graph.net;

import com.vesoft.nebula.client.graph.data.HostAddress;
import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.exception.IOErrorException;
import com.vesoft.nebula.client.graph.exception.NotValidConnectionException;
import com.vesoft.nebula.graph.ExecutionResponse;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The execute of Session can't use by multi thread.
 * The safeExecute of Session can use by multi thread,
 * the safeExecute will get a connection from the pool
 */
public class Session {
    private final long sessionID;
    private SyncConnection connection = null;
    private final NebulaPool pool;
    private final Boolean retryConnect;
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Charset charset = StandardCharsets.UTF_8;
    private AtomicReference<HostAddress> safeAddress = new AtomicReference<>();
    private final AtomicBoolean isSafeExecute = new AtomicBoolean(false);
    private final AtomicBoolean isExecute = new AtomicBoolean(false);

    public Session(long sessionID,
                   HostAddress safeAddress,
                   NebulaPool connPool,
                   Boolean retryConnect) {
        this.sessionID = sessionID;
        this.pool = connPool;
        this.safeAddress.set(safeAddress);
        this.retryConnect = retryConnect;
    }

    /**
     * Execute the ngql. the interface is not thread-safe
     *
     * @param stmt The ngl.
     * @return The ResultSet.
     */
    public ResultSet execute(String stmt)
        throws IOErrorException, NotValidConnectionException {
        if (isSafeExecute.get()) {
            throw new IOErrorException(IOErrorException.E_UNKNOWN,
                "The session is already called safeExecute, "
                    + "You can only use execute or safeExecute in the session");
        }
        isExecute.set(true);
        byte[] ngql = stmt.getBytes(charset);
        try {
            if (connection == null) {
                connection = pool.getConnectionByKey(safeAddress.get());
            }
            ExecutionResponse resp = connection.execute(sessionID, ngql);
            return new ResultSet(resp);
        } catch (IOErrorException ie) {
            if (ie.getType() == IOErrorException.E_CONNECT_BROKEN) {
                if (retryConnect) {
                    if (retryConnect()) {
                        ExecutionResponse resp = connection.execute(sessionID, ngql);
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

    /**
     * Execute the ngql. the interface is thread-safe,
     * the interface use the connection from the pool. the pool should init one graph server
     *
     * @param stmt The ngl.
     * @return The ResultSet.
     */
    public ResultSet safeExecute(String stmt)
        throws IOErrorException, NotValidConnectionException {
        if (isExecute.get()) {
            throw new IOErrorException(IOErrorException.E_UNKNOWN,
                "The session is already called execute, "
                    + "You can only use execute or safeExecute in the session");
        }
        isSafeExecute.set(true);
        if (safeAddress == null) {
            throw new IOErrorException(IOErrorException.E_UNKNOWN, "Wrong graphd server address");
        }
        byte[] ngql = stmt.getBytes(charset);
        SyncConnection connection = null;
        try {
            connection = pool.getConnectionByKey(safeAddress.get());
            return new ResultSet(connection.execute(sessionID, ngql));
        } catch (IOErrorException ie) {
            pool.setInvalidateConn(connection);
            if (ie.getType() == IOErrorException.E_CONNECT_BROKEN) {
                if (retryConnect) {
                    connection = pool.getConnection();
                    int retry = pool.getCanUseNum();
                    while (retry-- > 0) {
                        connection = pool.getConnection();
                        if (!connection.ping()) {
                            pool.setInvalidateConn(connection);
                            continue;
                        }
                        safeAddress.set(connection.getServerAddress());
                        return new ResultSet(connection.execute(sessionID, ngql));
                    }
                }
                throw new IOErrorException(IOErrorException.E_CONNECT_BROKEN,
                    "Connection is broken");
            }
            throw ie;
        } finally {
            if (connection != null) {
                pool.returnConnection(connection);
            }
        }
    }

    private boolean retryConnect() {
        try {
            pool.setInvalidateConn(connection);
            int retry = pool.getCanUseNum();
            while (retry-- > 0) {
                SyncConnection newConn = pool.getConnection();
                if (newConn.ping()) {
                    connection = newConn;
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            return false;
        }
    }

    public void release() {
        if (connection == null) {
            return;
        }
        connection.signout(sessionID);
        pool.returnConnection(connection);
    }
}

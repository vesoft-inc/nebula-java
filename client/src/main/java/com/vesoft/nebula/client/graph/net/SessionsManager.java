/* Copyright (c) 2021 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.client.graph.net;

import com.vesoft.nebula.client.graph.SessionsManagerConfig;
import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.exception.AuthFailedException;
import com.vesoft.nebula.client.graph.exception.ClientServerIncompatibleException;
import com.vesoft.nebula.client.graph.exception.IOErrorException;
import com.vesoft.nebula.client.graph.exception.NotValidConnectionException;
import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.BitSet;
import java.util.concurrent.CopyOnWriteArrayList;

public class SessionsManager implements Serializable {

    private static final long serialVersionUID = 7519424097351713021L;

    private final SessionsManagerConfig config;
    private NebulaPool pool = null;
    private final CopyOnWriteArrayList<SessionWrapper> sessionList;
    private BitSet canUseBitSet;
    private Boolean isClose = false;

    public SessionsManager(SessionsManagerConfig config) {
        this.config = config;
        this.sessionList = new CopyOnWriteArrayList<>();
        checkConfig();
    }

    private void checkConfig() {
        if (config.getAddresses().isEmpty()) {
            throw new RuntimeException("Empty graph addresses");
        }

        if (config.getSpaceName().isEmpty()) {
            throw new RuntimeException("Empty space name");
        }
    }

    /**
     * getSessionWrapper: return a SessionWrapper from sessionManager,
     * the SessionWrapper couldn't use by multi-thread
     * @return SessionWrapper
     * @throws RuntimeException the exception when get SessionWrapper
     */
    public synchronized SessionWrapper getSessionWrapper() throws RuntimeException,
            ClientServerIncompatibleException {
        checkClose();
        if (pool == null) {
            init();
        }
        if (canUseBitSet.isEmpty()
            && sessionList.size() >= config.getPoolConfig().getMaxConnSize()) {
            throw new RuntimeException("The SessionsManager does not have available sessions.");
        }
        if (!canUseBitSet.isEmpty()) {
            int index = canUseBitSet.nextSetBit(0);
            if (index >= 0) {
                if (canUseBitSet.get(index)) {
                    canUseBitSet.set(index, false);
                    return sessionList.get(index);
                }
            }
        }
        // create new session
        try {
            Session session = pool.getSession(
                config.getUserName(), config.getPassword(), config.getReconnect());
            ResultSet resultSet = session.execute("USE " + config.getSpaceName());
            if (!resultSet.isSucceeded()) {
                throw new RuntimeException(
                    "Switch space `"
                        + config.getSpaceName()
                        + "' failed: "
                        + resultSet.getErrorMessage());
            }
            SessionWrapper sessionWrapper = new SessionWrapper(session);
            sessionList.add(sessionWrapper);
            return sessionWrapper;
        } catch (AuthFailedException | NotValidConnectionException | IOErrorException e) {
            throw new RuntimeException("Get session failed: " + e.getMessage());
        }
    }

    /**
     * returnSessionWrapper: return the SessionWrapper to the sessionManger,
     * the old SessionWrapper couldn't use again.
     * @param session The SessionWrapper
     */
    public synchronized void returnSessionWrapper(SessionWrapper session) {
        checkClose();
        if (session == null) {
            return;
        }
        int index = sessionList.indexOf(session);
        if (index >= 0) {
            Session ses = session.getSession();
            sessionList.set(index, new SessionWrapper(ses));
            session.setNoAvailable();
            canUseBitSet.set(index, true);
        }
    }

    /**
     * close: release all sessions and close the connection pool
     */
    public synchronized void close() {
        for (SessionWrapper session : sessionList) {
            session.release();
        }
        pool.close();
        sessionList.clear();
        isClose = true;
    }

    private void init() throws RuntimeException {
        try {
            pool = new NebulaPool();
            if (!pool.init(config.getAddresses(), config.getPoolConfig())) {
                throw new RuntimeException("Init pool failed: services are broken.");
            }
            canUseBitSet = new BitSet(config.getPoolConfig().getMaxConnSize());
            canUseBitSet.set(0, config.getPoolConfig().getMaxConnSize(), false);
        } catch (UnknownHostException e) {
            throw new RuntimeException("Init the pool failed: " + e.getMessage());
        }
    }

    private void checkClose() {
        if (isClose) {
            throw new RuntimeException("The SessionsManager was closed.");
        }
    }
}

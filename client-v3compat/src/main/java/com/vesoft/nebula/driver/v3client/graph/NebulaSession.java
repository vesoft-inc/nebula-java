/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.v3client.graph;

import com.vesoft.nebula.driver.v3client.graph.data.ResultSet;
import com.vesoft.nebula.driver.v3client.graph.exception.IOErrorException;
import com.vesoft.nebula.driver.v3client.graph.net.Session;
import com.vesoft.nebula.driver.v3client.graph.net.SessionState;
import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A pooled session, matching the v3 client's {@code NebulaSession}.
 *
 * <p>Each instance wraps one v5 driver {@link com.vesoft.nebula.driver.graph.net.NebulaClient}
 * (one server-side session).
 */
public class NebulaSession implements Serializable {

    private static final long serialVersionUID = -88438249377120255L;

    private final long sessionID;
    private final int timezoneOffset;

    private volatile com.vesoft.nebula.driver.graph.net.NebulaClient client;
    private final AtomicReference<SessionState> state = new AtomicReference<>();
    private final AtomicBoolean isReleased = new AtomicBoolean(false);

    public NebulaSession(com.vesoft.nebula.driver.graph.net.NebulaClient client,
                         SessionState state) {
        this.client = client;
        this.sessionID = client.getSessionId();
        this.timezoneOffset = 0;
        this.state.set(state);
    }

    public long getSessionID() {
        return sessionID;
    }

    public Boolean isIdle() {
        return state.get() == SessionState.IDLE;
    }

    public Boolean isUsed() {
        return state.get() == SessionState.USED;
    }

    public boolean isUsedAndSetIdle() {
        return state.compareAndSet(SessionState.USED, SessionState.IDLE);
    }

    public boolean isIdleAndSetUsed() {
        return state.compareAndSet(SessionState.IDLE, SessionState.USED);
    }

    public ResultSet execute(String stmt) throws IOErrorException {
        return executeWithParameter(stmt, Collections.emptyMap());
    }

    public ResultSet executeWithParameter(String stmt, Map<String, Object> parameterMap)
        throws IOErrorException {
        checkReleased();
        String gql = Session.inlineParameters(stmt, parameterMap);
        try {
            return new ResultSet(client.execute(gql), timezoneOffset);
        } catch (com.vesoft.nebula.driver.graph.exception.IOErrorException e) {
            throw Session.toCompat(e);
        }
    }

    public ResultSet executeWithTimeout(String stmt, long timeoutMs) throws IOErrorException {
        return executeWithParameterTimeout(stmt, Collections.emptyMap(), timeoutMs);
    }

    public ResultSet executeWithParameterTimeout(String stmt,
                                                 Map<String, Object> parameterMap,
                                                 long timeoutMs) throws IOErrorException {
        checkReleased();
        if (timeoutMs <= 0) {
            throw new IllegalArgumentException("timeout should be a positive number");
        }
        String gql = Session.inlineParameters(stmt, parameterMap);
        try {
            return new ResultSet(client.execute(gql, timeoutMs), timezoneOffset);
        } catch (com.vesoft.nebula.driver.graph.exception.IOErrorException e) {
            throw Session.toCompat(e);
        }
    }

    public String executeJsonWithParameter(String stmt, Map<String, Object> parameterMap)
        throws IOErrorException {
        return Session.toJson(executeWithParameter(stmt, parameterMap));
    }

    public void release() {
        if (isReleased.compareAndSet(false, true)) {
            try {
                client.close();
            } catch (Exception e) {
                // ignore; the connection is being released anyway.
            }
            client = null;
        }
    }

    private void checkReleased() throws IOErrorException {
        if (client == null) {
            throw new IOErrorException(IOErrorException.E_CONNECT_BROKEN,
                                       "The session was released, couldn't use again.");
        }
    }
}

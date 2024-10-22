/* Copyright (c) 2022 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.client.graph;

import com.vesoft.nebula.Value;
import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.exception.IOErrorException;
import com.vesoft.nebula.client.graph.net.Session;
import com.vesoft.nebula.client.graph.net.SessionState;
import com.vesoft.nebula.client.graph.net.SyncConnection;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NebulaSession implements Serializable {

    private static final long serialVersionUID = -88438249377120255L;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final long                          sessionID;
    private final int                           timezoneOffset;
    private       SyncConnection                connection;
    private final AtomicReference<SessionState> state = new AtomicReference<>();

    private final AtomicBoolean isReleased = new AtomicBoolean(false);

    public NebulaSession(SyncConnection connection, long sessionID, int timezoneOffset,
                         SessionState state) {
        this.connection = connection;
        this.sessionID = sessionID;
        this.timezoneOffset = timezoneOffset;
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
        return new ResultSet(connection.execute(sessionID, stmt), timezoneOffset);
    }

    public ResultSet executeWithParameter(String stmt, Map<String, Object> parameterMap)
            throws IOErrorException {
        Map<byte[], Value> map = new HashMap<>();
        parameterMap.forEach((key, value) -> map.put(key.getBytes(), Session.value2Nvalue(value)));
        return new ResultSet(connection.executeWithParameter(sessionID, stmt, map), timezoneOffset);
    }

    public ResultSet executeWithTimeout(String stmt, long timeoutMs) throws IOErrorException {
        return executeWithParameterTimeout(stmt,
                                           (Map<String, Object>) Collections.EMPTY_MAP,
                                           timeoutMs);
    }

    public ResultSet executeWithParameterTimeout(String stmt,
                                                 Map<String, Object> parameterMap,
                                                 long timeoutMs) throws IOErrorException {
        Map<byte[], Value> map = new HashMap<>();
        parameterMap.forEach((key, value) -> map.put(key.getBytes(), Session.value2Nvalue(value)));
        return new ResultSet(connection.executeWithParameterTimeout(sessionID,
                                                                    stmt,
                                                                    map,
                                                                    timeoutMs),
                             timezoneOffset);

    }

    public String executeJsonWithParameter(String stmt, Map<String, Object> parameterMap)
            throws IOErrorException {
        Map<byte[], Value> map = new HashMap<>();
        parameterMap.forEach((key, value) -> map.put(key.getBytes(), Session.value2Nvalue(value)));
        return connection.executeJsonWithParameter(sessionID, stmt, map);
    }

    public void release() {
        if (isReleased.get()) {
            return;
        }
        if (isReleased.compareAndSet(false, true)) {
            try {
                connection.signout(sessionID);
                connection.close();
            } catch (Exception e) {
                // not print the warn to avoid confuse for session and connect,
                // when connection is broken, release will failed, just make connection as null.
                // log.warn("release session failed, " + e.getMessage());
            }
            connection = null;
        }
    }
}

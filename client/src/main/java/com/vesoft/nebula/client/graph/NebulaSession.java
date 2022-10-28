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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class NebulaSession implements Serializable {

    private static final long serialVersionUID = -88438249377120255L;

    private long sessionID;
    private int timezoneOffset;
    private SyncConnection connection;
    private AtomicReference<SessionState> state = new AtomicReference<>();

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

    public void release() {
        connection.signout(sessionID);
        connection.close();
        connection = null;
    }
}

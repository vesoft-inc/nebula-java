/* Copyright (c) 2022 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.client.graph;

import com.vesoft.nebula.client.graph.net.Session;
import com.vesoft.nebula.client.graph.net.SessionState;
import java.io.Serializable;

public class NebulaSession implements Serializable {

    private static final long serialVersionUID = -88438249377120255L;

    private Session session;
    private SessionState state;

    public NebulaSession(Session session, SessionState state) {
        this.session = session;
        this.state = state;
    }

    public Session getSession() {
        return session;
    }

    public Boolean isIdle() {
        return state == SessionState.IDLE;
    }

    public Boolean isUsed() {
        return state == SessionState.USED;
    }

    public void setIdle() {
        if (isUsed()) {
            state = SessionState.IDLE;
        }
    }

    public void setUsed() {
        if (isIdle()) {
            state = SessionState.USED;
        }
    }
}

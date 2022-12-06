/* Copyright (c) 2021 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.client.graph.net;

import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.exception.IOErrorException;
import com.vesoft.nebula.client.graph.exception.InvalidSessionException;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicBoolean;

public class SessionWrapper implements Serializable {

    private static final long serialVersionUID = -8128331485649098264L;

    private final Session session;
    private final long sessionID;
    private final AtomicBoolean available = new AtomicBoolean(true);

    public SessionWrapper(Session session) {
        this.session = session;
        this.sessionID = session.getSessionID();
    }

    /**
     * Execute the query sentence.
     *
     * @param stmt The query sentence.
     * @return The ResultSet.
     */
    public ResultSet execute(String stmt)
            throws IOErrorException {
        if (!available()) {
            throw new InvalidSessionException();
        }
        return session.execute(stmt);
    }

    public boolean ping() {
        return session.pingSession();
    }

    void setNoAvailable() {
        this.available.set(false);
    }

    boolean available() {
        return available.get();
    }

    void release() {
        session.release();
        setNoAvailable();
    }

    Session getSession() {
        return session;
    }
}

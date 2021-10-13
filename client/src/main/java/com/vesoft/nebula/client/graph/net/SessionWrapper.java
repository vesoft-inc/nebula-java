/* Copyright (c) 2021 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.graph.net;

import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.exception.ClientServerIncompatibleException;
import com.vesoft.nebula.client.graph.exception.IOErrorException;
import com.vesoft.nebula.client.graph.exception.InvalidSessionException;
import java.util.concurrent.atomic.AtomicBoolean;

public class SessionWrapper {
    private final Session session;
    private final AtomicBoolean available = new AtomicBoolean(true);

    public SessionWrapper(Session session) {
        this.session = session;
    }

    /**
     * Execute the query sentence.
     *
     * @param stmt The query sentence.
     * @return The ResultSet.
     */
    public ResultSet execute(String stmt)
            throws IOErrorException, ClientServerIncompatibleException {
        if (!available()) {
            throw new InvalidSessionException();
        }
        return session.execute(stmt);
    }

    void setNoAvailable() {
        this.available.set(false);
    }

    boolean available() {
        return available.get();
    }

    void release() {
        session.release();
    }

    Session getSession() {
        return session;
    }
}

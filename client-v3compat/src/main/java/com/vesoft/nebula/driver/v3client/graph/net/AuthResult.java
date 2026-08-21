/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.v3client.graph.net;

import java.io.Serializable;

/**
 * Authentication result, matching the v3 client.
 *
 * <p>The v5 driver has no per-session timezone offset, so {@link #getTimezoneOffset()} always
 * returns {@code 0}.
 */
public class AuthResult implements Serializable {

    private static final long serialVersionUID = 8795815613377375650L;

    private final long sessionId;
    private final int timezoneOffset;

    public AuthResult(long sessionId, int timezoneOffset) {
        this.sessionId = sessionId;
        this.timezoneOffset = timezoneOffset;
    }

    public long getSessionId() {
        return sessionId;
    }

    public int getTimezoneOffset() {
        return timezoneOffset;
    }
}

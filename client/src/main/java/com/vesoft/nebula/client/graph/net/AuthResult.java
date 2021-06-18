/* Copyright (c) 2021 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.graph.net;

public class AuthResult {
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

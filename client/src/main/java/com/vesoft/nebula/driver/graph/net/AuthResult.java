/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.net;

import java.io.Serializable;

public class AuthResult implements Serializable {

    private static final long serialVersionUID = 8795815613377375650L;

    private final long sessionId;

    private final String version;

    public AuthResult(long sessionId, String version) {
        this.sessionId = sessionId;
        this.version = version;
    }

    public long getSessionId() {
        return sessionId;
    }

    public String getVersion() {
        return version;
    }

}

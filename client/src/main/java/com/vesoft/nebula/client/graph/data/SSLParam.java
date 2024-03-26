/* Copyright (c) 2021 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.client.graph.data;

import java.io.Serializable;

public abstract class SSLParam implements Serializable {

    private static final long serialVersionUID = 7410233298826490747L;

    private boolean skipVerifyServer = false;

    public enum SignMode {
        NONE,
        SELF_SIGNED,
        CA_SIGNED
    }

    private SignMode signMode;

    public boolean isSkipVerifyServer() {
        return skipVerifyServer;
    }

    public void setSkipVerifyServer(boolean skipVerifyServer) {
        this.skipVerifyServer = skipVerifyServer;
    }

    public SSLParam(SignMode signMode) {
        this.signMode = signMode;
    }

    public SignMode getSignMode() {
        return signMode;
    }
}

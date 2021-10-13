/* Copyright (c) 2021 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.graph.data;

public abstract class SSLParam {
    public enum SignMode {
        NONE,
        SELF_SIGNED,
        CA_SIGNED
    }

    private SignMode signMode;

    public SSLParam(SignMode signMode) {
        this.signMode = signMode;
    }

    public SignMode getSignMode() {
        return signMode;
    }
}

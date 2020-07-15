/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.graph;

/**
 *
 */
public class NGQLException extends Exception {
    private static final long serialVersionUID = -90563191494716743L;
    private int code;

    public NGQLException(int code) {
        super();
        this.code = code;
    }

    public NGQLException(String msg, Throwable t, int code) {
        super(msg, t);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}

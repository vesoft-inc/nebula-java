/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.client.graph.exception;

/** */
public class IOErrorException extends java.lang.Exception {
    public static final int E_UNKNOWN = 0;

    public static final int E_ALL_BROKEN = 1;

    public static final int E_CONNECT_BROKEN = 2;

    public static final int E_TIME_OUT = 4;

    public static final int E_NO_OPEN = 5;

    private int type = E_UNKNOWN;

    public IOErrorException(int errorType, String message) {
        super(message);
        this.type = errorType;
    }

    public int getType() {
        return type;
    }
}

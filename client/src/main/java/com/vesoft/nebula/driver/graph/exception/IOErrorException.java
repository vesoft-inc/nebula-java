/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.exception;

/**
 *
 */
public class IOErrorException extends Exception {
    public static final int E_UNKNOWN = 0;

    public static final int E_ALL_BROKEN = 1;

    public static final int E_CONNECT_BROKEN = 2;

    public static final int E_SERVER_BAD = 3;

    public static final int E_TIME_OUT = 4;

    public static final int E_NO_OPEN = 5;

    public static final int E_SSL_ERROR = 6;

    private int type = E_UNKNOWN;

    public IOErrorException(int errorType, String message) {
        super(message);
        this.type = errorType;
    }

    public int getType() {
        return type;
    }
}


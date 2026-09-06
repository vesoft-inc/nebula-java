/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.v3client.graph.exception;

/**
 * Thrown when an IO error occurs while talking to the NebulaGraph server.
 *
 * <p>The {@code type} field mirrors the v3 client's error-type constants.
 */
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

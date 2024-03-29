/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.client.graph.exception;

/**
 *
 */
public class NotValidConnectionException extends Exception {
    public NotValidConnectionException(String message) {
        super(String.format("No extra connection: %s", message));
    }
}


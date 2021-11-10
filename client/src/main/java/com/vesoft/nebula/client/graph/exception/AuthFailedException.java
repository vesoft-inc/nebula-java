/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.client.graph.exception;

/**
 *
 */
public class AuthFailedException extends Exception {
    public AuthFailedException(String message) {
        super(String.format("Auth failed: %s", message));
    }
}


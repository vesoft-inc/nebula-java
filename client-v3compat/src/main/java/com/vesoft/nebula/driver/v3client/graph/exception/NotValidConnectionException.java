/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.v3client.graph.exception;

/**
 * Thrown when a connection cannot be obtained from the pool.
 */
public class NotValidConnectionException extends Exception {
    public NotValidConnectionException(String message) {
        super(String.format("No extra connection: %s", message));
    }
}

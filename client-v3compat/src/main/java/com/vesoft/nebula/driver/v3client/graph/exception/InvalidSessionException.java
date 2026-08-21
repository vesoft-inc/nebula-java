/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.v3client.graph.exception;

/**
 * Thrown when a released or invalidated session is used.
 */
public class InvalidSessionException extends RuntimeException {
    public InvalidSessionException() {
        super("The session was released, could not use again.");
    }
}

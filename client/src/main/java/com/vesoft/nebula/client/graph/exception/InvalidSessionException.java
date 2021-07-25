/* Copyright (c) 2021 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.graph.exception;

public class InvalidSessionException extends RuntimeException {
    public InvalidSessionException() {
            super("The session was released, could not use again.");
        }
}

/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.meta.exception;

public class ExecuteFailedException extends Exception {
    public ExecuteFailedException(String message) {
        super(String.format("Execute failed: %s", message));
    }
}

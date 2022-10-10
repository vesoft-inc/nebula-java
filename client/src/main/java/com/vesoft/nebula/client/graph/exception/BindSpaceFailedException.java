/* Copyright (c) 2022 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.client.graph.exception;

public class BindSpaceFailedException extends Exception {
    private static final long serialVersionUID = -8678623814979666625L;

    public BindSpaceFailedException(String message) {
        super(String.format("use space failed: %s", message));
    }
}

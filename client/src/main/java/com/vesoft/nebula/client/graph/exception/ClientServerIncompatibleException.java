/* Copyright (c) 2021 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.client.graph.exception;

/**
 *
 */
public class ClientServerIncompatibleException extends Exception {
    public ClientServerIncompatibleException(String message) {
        super("Current client is not compatible with the remote server. " + message);
    }
}


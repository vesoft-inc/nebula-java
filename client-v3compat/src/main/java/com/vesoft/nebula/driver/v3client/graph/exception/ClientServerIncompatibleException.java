/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.v3client.graph.exception;

/**
 * Thrown when the client and the remote server versions are incompatible.
 */
public class ClientServerIncompatibleException extends Exception {
    public ClientServerIncompatibleException(String message) {
        super("Current client is not compatible with the remote server, please check the "
              + "version: " + message);
    }
}

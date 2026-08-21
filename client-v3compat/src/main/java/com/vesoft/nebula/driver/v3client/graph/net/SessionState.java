/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.v3client.graph.net;

/**
 * The lifecycle state of a pooled session, matching the v3 client.
 */
public enum SessionState {
    IDLE, USED
}

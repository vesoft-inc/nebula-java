/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.graph;

public class Config {
    public int maxConnectionPoolSize = 10;   // The max connections in pool for all addresses
    public int timeout = 1000; // Socket timeout and Socket connection timeout
}

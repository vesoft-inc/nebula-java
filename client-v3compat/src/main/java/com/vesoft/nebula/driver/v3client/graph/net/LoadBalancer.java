/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.v3client.graph.net;

import com.vesoft.nebula.driver.v3client.graph.data.HostAddress;

/**
 * Server load-balancer interface, matching the v3 client.
 */
public interface LoadBalancer {
    HostAddress getAddress();

    void close();

    void updateServersStatus();

    boolean isServersOK();
}

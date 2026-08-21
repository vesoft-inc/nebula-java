/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.v3client.graph.net;

import com.vesoft.nebula.driver.v3client.graph.data.HostAddress;
import com.vesoft.nebula.driver.v3client.graph.data.SSLParam;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Source-compatibility shim for the v3 client's round-robin load balancer.
 *
 * <p>The v5 driver performs its own server health management, so this shim only provides a
 * round-robin {@link #getAddress()} over the configured addresses.
 */
public class RoundRobinLoadBalancer implements LoadBalancer {

    private final List<HostAddress> addresses = new ArrayList<>();
    private final AtomicInteger pos = new AtomicInteger(0);

    public RoundRobinLoadBalancer(List<HostAddress> addresses, int timeout,
                                  double minClusterHealthRate) {
        this(addresses);
    }

    public RoundRobinLoadBalancer(List<HostAddress> addresses, int timeout,
                                  double minClusterHealthRate, boolean useHttp2,
                                  Map<String, String> headers) {
        this(addresses);
    }

    public RoundRobinLoadBalancer(List<HostAddress> addresses, int timeout, SSLParam sslParam,
                                  double minClusterHealthRate) {
        this(addresses);
    }

    public RoundRobinLoadBalancer(List<HostAddress> addresses, int timeout, SSLParam sslParam,
                                  double minClusterHealthRate, boolean useHttp2,
                                  Map<String, String> headers) {
        this(addresses);
    }

    private RoundRobinLoadBalancer(List<HostAddress> addresses) {
        if (addresses != null) {
            this.addresses.addAll(addresses);
        }
    }

    @Override
    public HostAddress getAddress() {
        if (addresses.isEmpty()) {
            return null;
        }
        return addresses.get(Math.abs(pos.getAndIncrement()) % addresses.size());
    }

    @Override
    public void close() {
        // no-op
    }

    @Override
    public void updateServersStatus() {
        // no-op; health is managed by the v5 driver.
    }

    @Override
    public boolean isServersOK() {
        return !addresses.isEmpty();
    }

    @SuppressWarnings("unused")
    private static final Map<String, String> EMPTY = new HashMap<>();
}

/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.v3client.graph.data;

import java.io.Serializable;

/**
 * A graphd host address (host + port), matching the v3 client.
 */
public class HostAddress implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String host;
    private final int    port;

    public HostAddress(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    @Override
    public int hashCode() {
        return host.hashCode() + port;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof HostAddress) {
            HostAddress that = (HostAddress) obj;
            return this.host.equals(that.host) && this.port == that.port;
        }
        return false;
    }

    @Override
    public String toString() {
        if (host.contains(":")) {
            return "[" + host + "]:" + port;
        }
        return host + ":" + port;
    }

    /**
     * Convert to the v5 driver's host address type.
     */
    public com.vesoft.nebula.driver.graph.data.HostAddress toV5() {
        return new com.vesoft.nebula.driver.graph.data.HostAddress(host, port);
    }

    /**
     * Convert from the v5 driver's host address type.
     */
    public static HostAddress fromV5(com.vesoft.nebula.driver.graph.data.HostAddress address) {
        return new HostAddress(address.getHost(), address.getPort());
    }
}

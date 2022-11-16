/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.client.graph.data;

import java.io.Serializable;

public class HostAddress implements Serializable {
    private final String host;
    private final int port;

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
        return host + ":" + port;
    }
}

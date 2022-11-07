package com.vesoft.nebula.client.graph.net;

import com.vesoft.nebula.client.graph.data.HostAddress;

public interface LoadBalancer {
    HostAddress getAddress();

    void close();

    void updateServersStatus();

    boolean isServersOK();
}

/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.client.graph;

import com.vesoft.nebula.client.graph.data.SSLParam;
import java.io.Serializable;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class NebulaPoolConfig implements Serializable {

    public static NebulaPoolConfig defaultConfig() {
        return NebulaPoolConfig.builder().build();
    }

    // The min connections in pool for all addresses
    int minConnSize;

    // The max connections in pool for all addresses
    @Default int maxConnSize = 10;

    // Socket timeout and Socket connection timeout, unit: millisecond
    int timeout;

    // The idleTime of the connection, unit: millisecond
    // The connection's idle time more than idleTime, it will be delete
    // 0 means never delete
    int idleTime;

    // The interval time to check idle connection, unit ms, -1 means no check
    @Default int intervalIdle = -1;

    // The wait time to get idle connection, unit ms
    int waitTime;

    // The minimum rate of healthy servers to all servers. if 1 it means all servers should be
    // available on init.
    @Default double minClusterHealthRate = 1;

    // Set to true to turn on ssl encrypted traffic
    boolean enableSsl;

    // SSL param is required if ssl is turned on
    SSLParam sslParam;
}

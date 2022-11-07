/* Copyright (c) 2022 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.client.graph;

import com.vesoft.nebula.client.graph.data.HostAddress;
import java.io.Serializable;
import java.util.List;
import lombok.Builder;
import lombok.Singular;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class SessionPoolConfig implements Serializable {

    @Singular List<HostAddress> graphAddresses;

    String username;
    String password;
    String spaceName;

    // The min connections in pool for all addresses
    int minSessionSize;

    // The max connections in pool for all addresses
    int maxSessionSize;

    // Socket timeout and Socket connection timeout, unit: millisecond
    int timeout;

    // The idleTime for clean the idle session
    // must be less than NebulaGraph's session_idle_timeout_secs, unit: second
    int cleanTime;

    // The healthCheckTime for schedule check the health of session
    int healthCheckTime;

    // The wait time to get idle connection, unit ms
    int waitTime;

    public static SessionPoolConfig of(
            List<HostAddress> graphAddresses, String spaceName, String username, String password) {
        return SessionPoolConfig.builder()
                .graphAddresses(graphAddresses)
                .spaceName(spaceName)
                .username(username)
                .password(password)
                .build();
    }

    SessionPoolConfig(
            List<HostAddress> graphAddresses,
            String username,
            String password,
            String spaceName,
            int minSessionSize,
            int maxSessionSize,
            int timeout,
            int cleanTime,
            int healthCheckTime,
            int waitTime) {
        this.graphAddresses = graphAddresses;
        this.username = username;
        this.password = password;
        this.spaceName = spaceName;
        this.timeout = timeout;
        this.cleanTime = cleanTime == 0 ? 3600 : cleanTime;
        this.waitTime = waitTime;

        // defaults
        this.healthCheckTime = healthCheckTime == 0 ? 600 : healthCheckTime;
        this.minSessionSize = minSessionSize == 0 ? 1 : minSessionSize;
        this.maxSessionSize = maxSessionSize == 0 ? 10 : maxSessionSize;

        if (maxSessionSize < 1) {
            throw new IllegalArgumentException("maxSessionSize cannot be less than 1.");
        }
        if (timeout < 0) {
            throw new IllegalArgumentException("timeout cannot be less than 0.");
        }
        if (cleanTime < 0) {
            throw new IllegalArgumentException("cleanTime cannot be less than 0.");
        }
        if (healthCheckTime < 0) {
            throw new IllegalArgumentException("cleanTime cannot be less than 0.");
        }
        if (waitTime < 0) {
            throw new IllegalArgumentException("waitTime cannot be less than 0.");
        }
    }
}

/* Copyright (c) 2021 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.client.graph;

import com.vesoft.nebula.client.graph.data.HostAddress;
import java.io.Serializable;
import java.util.List;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Singular;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class SessionsManagerConfig implements Serializable {
    // graphd addresses
    @Singular List<HostAddress> addresses;
    // the userName to authenticate graph
    @Default String userName = "root";
    // the password to authenticate graph
    @Default String password = "nebula";
    // the space name
    @Default String spaceName = "";
    // the session needs do reconnect
    @Default boolean reconnect = true;
    // The config of NebulaConfig
    @Default NebulaPoolConfig poolConfig = NebulaPoolConfig.builder().build();
}

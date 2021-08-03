/* Copyright (c) 2021 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.graph;

import com.vesoft.nebula.client.graph.data.HostAddress;
import com.vesoft.nebula.client.graph.net.NebulaPool;
import java.util.ArrayList;
import java.util.List;

public class SessionsManagerConfig {
    // graphd addresses
    private List<HostAddress> addresses = new ArrayList<>();

    // the userName to authenticate graph
    private String userName = "root";

    // the password to authenticate graph
    private String password = "nebula";

    // the space name
    private String spaceName = "";

    // the session needs do reconnect
    private Boolean reconnect = true;

    // The config of NebulaConfig
    private NebulaPoolConfig poolConfig = new NebulaPoolConfig();

    public List<HostAddress> getAddresses() {
        return addresses;
    }

    public SessionsManagerConfig setAddresses(List<HostAddress> addresses) {
        this.addresses = addresses;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public SessionsManagerConfig setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public SessionsManagerConfig setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getSpaceName() {
        return spaceName;
    }

    public SessionsManagerConfig setSpaceName(String spaceName) {
        this.spaceName = spaceName;
        return this;
    }

    public Boolean getReconnect() {
        return reconnect;
    }

    public void setReconnect(Boolean reconnect) {
        this.reconnect = reconnect;
    }

    public NebulaPoolConfig getPoolConfig() {
        return poolConfig;
    }

    public SessionsManagerConfig setPoolConfig(NebulaPoolConfig poolConfig) {
        this.poolConfig = poolConfig;
        return this;
    }
}

/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.bean;

import com.google.common.net.HostAndPort;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ConnectInfo implements Serializable {

    private final String spaceName;

    private final String hostAndPorts;

    public ConnectInfo(String spaceName, String hostAndPorts) {
        this.spaceName = spaceName;
        this.hostAndPorts = hostAndPorts;
    }

    public String getSpaceName() {
        return this.spaceName;
    }

    public List<HostAndPort> getHostAndPorts() {
        List<HostAndPort> hostAndPortList = new ArrayList<>();
        String[] hostAndPortArray = hostAndPorts.split(",");
        for (String hostAndPort : hostAndPortArray) {
            hostAndPortList.add(HostAndPort.fromString(hostAndPort));
        }
        return hostAndPortList;
    }
}

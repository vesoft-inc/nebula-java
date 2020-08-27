/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.bean;

import com.google.common.base.Preconditions;
import com.google.common.net.HostAndPort;
import com.vesoft.nebula.common.Checkable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

@Data
public class ConnectInfo implements Checkable, Serializable {

    private String spaceName;

    private String hostAndPorts;

    public ConnectInfo(String spaceName, String hostAndPorts) {
        this.spaceName = spaceName;
        this.hostAndPorts = hostAndPorts;
        check();
    }

    public List<HostAndPort> getHostAndPorts() {
        List<HostAndPort> hostAndPortList = new ArrayList<>();
        String[] hostAndPortArray = hostAndPorts.split(",");
        for (String hostAndPort : hostAndPortArray) {
            hostAndPortList.add(HostAndPort.fromString(hostAndPort));
        }
        return hostAndPortList;
    }

    @Override
    public void check() throws IllegalArgumentException {
        Preconditions.checkArgument(StringUtils.isNotEmpty(spaceName),
                "The spaceName can't be null or empty");

        Preconditions.checkArgument(StringUtils.isNotEmpty(hostAndPorts),
                "The hostAndPorts can't be null or empty");
    }
}

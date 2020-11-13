/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package org.apache.flink.connector.nebula.utils;

import com.google.common.net.HostAndPort;

import java.util.ArrayList;
import java.util.List;

public class NebulaUtils {

    public static List<HostAndPort> getHostAndPorts(String address){
        if(address == null || "".equalsIgnoreCase(address)){
            throw new IllegalArgumentException("empty address");
        }
        List<HostAndPort> hostAndPortList = new ArrayList<>();
        for(String addr: address.split(NebulaConstant.COMMA)){
            hostAndPortList.add(HostAndPort.fromString(addr));
        }
        return hostAndPortList;
    }
}

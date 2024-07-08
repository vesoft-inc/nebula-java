/* Copyright (c) 2024 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.util;

import com.vesoft.nebula.HostAddr;

/**
 * The util of network
 *
 * @Author jiangyiwang-jk
 * @Date 2024/2/1 15:36
 */
public class NetUtil {

    public static HostAddr parseHostAddr(String hostAddress) {
        assert hostAddress != null : "Host address should not be null";
        String[] hostPort = hostAddress.split(":");
        assert hostPort.length == 2 : String.format("Invalid host address %s", hostAddress);
        return new HostAddr(hostPort[0].trim(), Integer.parseInt(hostPort[1].trim()));
    }

}

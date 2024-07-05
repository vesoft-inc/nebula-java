package com.vesoft.nebula.util;

import com.vesoft.nebula.HostAddr;
import com.vesoft.nebula.client.graph.data.HostAddress;

/**
 * The util of network
 *
 * @Author jiangyiwang-jk
 * @Date 2024/2/1 15:36
 */
public class NetUtil {

    private NetUtil() {
        ;
    }

    public static HostAddr parseHostAddr(String hostAddress) {
        assert hostAddress != null : "Host address should not be null";
        String[] hostPort = hostAddress.split(":");
        assert hostPort.length == 2 : String.format("Invalid host address %s", hostAddress);
        return new HostAddr(hostPort[0].trim(), Integer.parseInt(hostPort[1].trim()));
    }

}

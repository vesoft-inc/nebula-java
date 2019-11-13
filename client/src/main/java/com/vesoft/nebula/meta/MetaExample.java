/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.meta;

import com.google.common.collect.Lists;
import com.google.common.net.HostAndPort;
import com.vesoft.nebula.meta.client.MetaClientImpl;

import java.util.List;

public class MetaExample {
    public static void main(String[] args) {
        List<HostAndPort> addresses = Lists.newArrayList(HostAndPort.fromParts("127.0.0.1", 28910));
        addresses.add(HostAndPort.fromParts("127.0.0.1", 28912));
        addresses.add(HostAndPort.fromParts("127.0.0.1", 28914));
        MetaClientImpl metaClient = new MetaClientImpl(addresses);
        System.out.println(metaClient.getPart(2, 1));
        System.out.println(metaClient.getTagId(1, "test"));
        System.out.println(metaClient.getEdgeType(1, "test"));
    }
}


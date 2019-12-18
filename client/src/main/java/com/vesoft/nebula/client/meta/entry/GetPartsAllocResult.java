/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.meta.entry;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.net.HostAndPort;
import com.vesoft.nebula.HostAddr;
import com.vesoft.nebula.utils.AddressUtil;
import java.util.List;
import java.util.Map;

public class GetPartsAllocResult {

    private Map<Integer, List<HostAndPort>> result;

    public GetPartsAllocResult() {
        this.result = Maps.newHashMap();
    }

    public void add(Integer partId, List<HostAddr> addrs) {
        List<HostAndPort> hostAndPortList = Lists.newArrayList();
        for (HostAddr addr : addrs) {
            hostAndPortList.add(HostAndPort.fromParts(AddressUtil.intToIPv4(addr.ip),
                    addr.port));
        }
        result.put(partId, hostAndPortList);
    }

    public Map<Integer, List<HostAndPort>> getResult() {
        return result;
    }
}

/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.util;

import com.vesoft.nebula.driver.graph.data.HostAddress;
import com.vesoft.nebula.driver.graph.utils.AddressUtil;
import java.util.List;
import org.junit.Test;

public class AddressUtilTest {


    @Test
    public void testIpv4Adderss() {
        try {
            // test ipv4
            List<HostAddress> addresses = AddressUtil.validateAddress("localhost:9669");
            assert (addresses.size() == 1);
            assert (addresses.get(0).toString().equals("localhost:9669"));

            addresses = AddressUtil.validateAddress(",127.0.0.1:9669 ");
            assert (addresses.size() == 1);
            assert (addresses.get(0).toString().equals("127.0.0.1:9669"));
        } catch (Exception e) {
            assert (false);
        }
    }

    @Test
    public void testIpv6Address() {
        List<HostAddress> addresses;
        try {
            // test ipv6
            addresses = AddressUtil.validateAddress("fe80::64eb:eff:fe32:f7fe:9669");
        } catch (Exception e) {
            assert (e.getMessage().contains("Possible bracketless IPv6 literal"));
        }
        try {
            AddressUtil.validateAddress("127.0.0.1");
        } catch (Exception e) {
            assert (e.getMessage().contains("No port"));
        }

        try {
            addresses = AddressUtil.validateAddress("[fe80::64eb:eff:fe32:f7fe]:9669");
            assert (addresses.get(0).getHost().equals("fe80::64eb:eff:fe32:f7fe"));
            assert (addresses.get(0).getPort() == 9669);
        } catch (Exception e) {
            assert (false);
        }
    }
}

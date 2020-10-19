/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.graph.sync;

import com.vesoft.nebula.client.graph.NebulaPoolConfig;
import com.vesoft.nebula.client.graph.data.HostAddress;
import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.exception.NotValidConnectionException;
import com.vesoft.nebula.client.graph.net.NebulaPool;
import com.vesoft.nebula.client.graph.net.Session;
import com.vesoft.nebula.graph.ErrorCode;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import sun.net.util.IPAddressUtil;

public class TestConnectionPool {
    @Test()
    public void testInitFailed() {
        // hostname is not existed
        try {
            List<HostAddress> addresses = Arrays.asList(new HostAddress("hostname", 3888));
            NebulaPool pool = new NebulaPool();
            assert (false == pool.init(addresses, new NebulaPoolConfig()));
        } catch (UnknownHostException e) {
            System.out.println("We expect must reach here: init pool failed.");
            assert (true);
        }

        // connect failed
        try {
            NebulaPoolConfig nebulaPoolConfig = new NebulaPoolConfig();
            nebulaPoolConfig.setMinConnSize(0);
            nebulaPoolConfig.setMaxConnSize(1);
            List<HostAddress> addresses = Arrays.asList(new HostAddress("127.0.0.1", 3888));
            NebulaPool pool = new NebulaPool();
            assert (false == pool.init(addresses, nebulaPoolConfig));
        } catch (Exception e) {
            e.printStackTrace();
            assert (false);
        }
    }

    @Test()
    public void testGetSession() {
        try {
            NebulaPoolConfig nebulaPoolConfig = new NebulaPoolConfig();
            nebulaPoolConfig.setMinConnSize(2);
            nebulaPoolConfig.setMaxConnSize(4);
            List<HostAddress> addresses = Arrays.asList(new HostAddress("127.0.0.1", 3699),
                    new HostAddress("127.0.0.1", 3700));
            NebulaPool pool = new NebulaPool();
            assert pool.init(addresses, nebulaPoolConfig);
            int i = 0;
            List<Session> sessions = new ArrayList<>();
            while (i < nebulaPoolConfig.getMaxConnSize()) {
                Session session = pool.getSession("root", "nebula", false);
                assert (session != null);
                ResultSet resp = session.execute("SHOW SPACES");
                assert (resp.getErrorCode() == ErrorCode.SUCCEEDED);
                sessions.add(session);
                i++;
            }

            assert (pool.getActiveConnNum() == 4);
            assert (pool.getIdleConnNum() == 0);
            // All sessions are in used, so getSession failed
            try {
                Session session = pool.getSession("root", "nebula", false);
                assert (session == null);
            } catch (Exception e) {
                System.out.println("We expect must reach here: get session failed.");
                assert (true);
            }

            // Test reuse session after the sessions have been release
            for (Session s : sessions) {
                s.release();
            }
            assert (pool.getActiveConnNum() == 0);
            assert (pool.getIdleConnNum() == 4);
            Session session = pool.getSession("root", "nebula", false);
            assert (session != null);
            assert (pool.getActiveConnNum() == 1);
            assert (pool.getIdleConnNum() == 3);
        } catch (Exception e) {
            e.printStackTrace();
            assert (false);
        }
    }

    @Test()
    public void testClose() {
        try {
            NebulaPoolConfig nebulaPoolConfig = new NebulaPoolConfig();
            nebulaPoolConfig.setMaxConnSize(1);
            List<HostAddress> addresses = Arrays.asList(new HostAddress("127.0.0.1", 3699));
            NebulaPool pool = new NebulaPool();
            assert pool.init(addresses, nebulaPoolConfig);
            pool.close();
            Session s = pool.getSession("root", "nebula", false);
            assert (false);
        } catch (NotValidConnectionException e) {
            System.out.println("We expect must reach here: get session failed.");
            assert (true);
        } catch (Exception e) {
            e.printStackTrace();
            assert (false);
        }
    }
}

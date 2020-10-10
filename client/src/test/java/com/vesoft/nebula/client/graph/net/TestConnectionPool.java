/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.graph.sync;

import com.google.common.net.HostAndPort;
import com.vesoft.nebula.client.graph.Config;
import com.vesoft.nebula.client.graph.ConnectionPool;
import com.vesoft.nebula.client.graph.NotValidConnectionException;
import com.vesoft.nebula.client.graph.ResultSet;
import com.vesoft.nebula.client.graph.Session;
import com.vesoft.nebula.graph.ErrorCode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;

public class TestConnectionPool {
    private ConnectionPool pool = new ConnectionPool();

    @Test(timeout = 30000)
    public void testInitFailed() {
        try {
            Config config = new Config();
            config.maxConnectionPoolSize = 1;
            List<HostAndPort> addresses = Arrays.asList(HostAndPort.fromParts("127.0.0.1", 3888));
            assert (false == pool.init(addresses, "root", "nebula", config));
        } catch (Exception e) {
            e.printStackTrace();
            assert (false);
        }
    }

    @Test(timeout = 30000)
    public void testGetSession() {
        try {
            Config config = new Config();
            config.maxConnectionPoolSize = 4;
            List<HostAndPort> addresses = Arrays.asList(HostAndPort.fromParts("127.0.0.1", 3699),
                    HostAndPort.fromParts("127.0.0.1", 3700));
            assert (pool.init(addresses, "root", "nebula", config));
            int i = 0;
            List<Session> sessions = new ArrayList<Session>();
            while (i < config.maxConnectionPoolSize) {
                Session session = pool.getSession(false);
                assert (session != null);
                ResultSet resp = session.execute("SHOW SPACES");
                assert (resp.getErrorCode() == ErrorCode.SUCCEEDED);
                sessions.add(session);
                assert (session != null);
                i++;
            }

            assert (pool.getInUsedNum() == 4);
            assert (pool.getOkServersNum() == 2);
            // All sessions are in used, so getSession failed
            try {
                Session session = pool.getSession(false);
                assert (session == null);
            } catch (Exception e) {
                System.out.println("We expect must reach here: get session failed.");
                assert (true);
            }

            // Test reuse session after the sessions have been release
            for (Session s : sessions) {
                s.release();
            }
            assert (pool.getInUsedNum() == 0);
            assert (pool.getOkServersNum() == 2);
            Session session = pool.getSession(false);
            assert (session != null);
            assert (pool.getInUsedNum() == 1);
            assert (pool.getOkServersNum() == 2);
        } catch (Exception e) {
            e.printStackTrace();
            assert (false);
        }
    }

    @Test(timeout = 3000)
    public void testClose() {
        pool.close();
        assert (pool.getConnection() == null);
        try {
            Session s = pool.getSession(false);
            assert (s == null);
        } catch (NotValidConnectionException e) {
            System.out.println("We expect must reach here: get session failed.");
            assert (true);
        } catch (Exception e) {
            e.printStackTrace();
            assert (false);
        }
    }
}

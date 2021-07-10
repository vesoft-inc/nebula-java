/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.graph.net;

import com.vesoft.nebula.ErrorCode;
import com.vesoft.nebula.client.graph.NebulaPoolConfig;
import com.vesoft.nebula.client.graph.data.HostAddress;
import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.exception.IOErrorException;
import com.vesoft.nebula.client.graph.exception.InvalidConfigException;
import com.vesoft.nebula.client.graph.exception.NotValidConnectionException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;

public class TestConnectionPool {
    @Test()
    public void testInitFailed() {
        // config illegal, illegal idle time
        try {
            NebulaPool pool = new NebulaPool();
            NebulaPoolConfig config = new NebulaPoolConfig();
            config.setIdleTime(-10);
            pool.init(
                Collections.singletonList(new HostAddress("127.0.0.1", 3777)),
                config);
            assert false;
        } catch (UnknownHostException e) {
            System.out.println("We expect must reach here: init pool failed.");
            assert false;
        } catch (InvalidConfigException e) {
            System.out.println("We expect must reach here: init pool failed.");
            Assert.assertTrue(true);
        }

        // config illegal, illegal max conn size
        try {
            NebulaPool pool = new NebulaPool();
            NebulaPoolConfig config = new NebulaPoolConfig();
            config.setMaxConnSize(-10);
            pool.init(
                Collections.singletonList(new HostAddress("127.0.0.1", 3777)),
                config);
            assert false;
        } catch (UnknownHostException e) {
            System.out.println("We expect must reach here: init pool failed.");
            assert false;
        } catch (InvalidConfigException e) {
            System.out.println("We expect must reach here: init pool failed.");
            Assert.assertTrue(true);
        }

        // config illegal, illegal min conn size
        try {
            NebulaPool pool = new NebulaPool();
            NebulaPoolConfig config = new NebulaPoolConfig();
            config.setMaxConnSize(10);
            config.setMinConnSize(20);
            pool.init(
                Collections.singletonList(new HostAddress("127.0.0.1", 3777)),
                config);
            assert false;
        } catch (UnknownHostException e) {
            System.out.println("We expect must reach here: init pool failed.");
            assert false;
        } catch (InvalidConfigException e) {
            System.out.println("We expect must reach here: init pool failed.");
            Assert.assertTrue(true);
        }

        // config illegal, illegal timeout
        try {
            NebulaPool pool = new NebulaPool();
            NebulaPoolConfig config = new NebulaPoolConfig();
            config.setTimeout(-10);
            pool.init(
                Collections.singletonList(new HostAddress("127.0.0.1", 3777)),
                config);
            assert false;
        } catch (UnknownHostException e) {
            System.out.println("We expect must reach here: init pool failed.");
            assert false;
        } catch (InvalidConfigException e) {
            System.out.println("We expect must reach here: init pool failed.");
            Assert.assertTrue(true);
        }

        // hostname is not existed
        try {
            List<HostAddress> addresses = Collections.singletonList(
                    new HostAddress("hostname", 3888));
            NebulaPool pool = new NebulaPool();
            Assert.assertFalse(pool.init(addresses, new NebulaPoolConfig()));
        } catch (UnknownHostException e) {
            System.out.println("We expect must reach here: init pool failed.");
            Assert.assertTrue(true);
        }

        // connect failed
        try {
            NebulaPoolConfig nebulaPoolConfig = new NebulaPoolConfig();
            nebulaPoolConfig.setMinConnSize(0);
            nebulaPoolConfig.setMaxConnSize(1);
            List<HostAddress> addresses = Collections.singletonList(
                    new HostAddress("127.0.0.1", 3888));
            NebulaPool pool = new NebulaPool();
            Assert.assertFalse(pool.init(addresses, nebulaPoolConfig));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test()
    public void testGetSession() {
        try {
            NebulaPoolConfig nebulaPoolConfig = new NebulaPoolConfig();
            nebulaPoolConfig.setMinConnSize(2);
            nebulaPoolConfig.setMaxConnSize(4);
            // set idle time
            nebulaPoolConfig.setIdleTime(2000);
            nebulaPoolConfig.setIntervalIdle(1000);
            // set wait time
            nebulaPoolConfig.setWaitTime(1000);
            List<HostAddress> addresses = Collections.singletonList(
                    new HostAddress("127.0.0.1", 9670));
            NebulaPool pool = new NebulaPool();
            assert pool.init(addresses, nebulaPoolConfig);
            int i = 0;
            List<Session> sessions = new ArrayList<>();
            while (i < nebulaPoolConfig.getMaxConnSize()) {
                Session session = pool.getSession("root", "nebula", false);
                assert (session != null);
                ResultSet resp = session.execute("SHOW SPACES");
                assert (resp.getErrorCode() == ErrorCode.SUCCEEDED.getValue());
                sessions.add(session);
                i++;
            }

            assert (pool.getActiveConnNum() == 4);
            assert (pool.getIdleConnNum() == 0);
            // All sessions are in used, so getSession failed, and the wait time is 1000ms
            long beginTime = System.currentTimeMillis();
            try {
                Session session = pool.getSession("root", "nebula", false);
                Assert.assertTrue(session == null);
            } catch (Exception e) {
                System.out.println("We expect must reach here: get session failed.");
                long  timeInterval = System.currentTimeMillis() - beginTime;
                System.out.println("timeInterval is " + timeInterval);
                Assert.assertTrue(System.currentTimeMillis() - beginTime >= 1000);
                assert (true);
            }

            // Test reuse session after the sessions have been release
            for (Session s : sessions) {
                s.release();
            }
            Assert.assertEquals(0, pool.getActiveConnNum());
            Assert.assertEquals(4, pool.getIdleConnNum());
            Session session = pool.getSession("root", "nebula", false);
            Assert.assertNotNull(session);
            Assert.assertEquals(1, pool.getActiveConnNum());
            Assert.assertEquals(3, pool.getIdleConnNum());

            // test idletime connection is closed
            session.release();
            TimeUnit.SECONDS.sleep(3);
            Assert.assertEquals(2, pool.getIdleConnNum());
            Assert.assertEquals(0, pool.getActiveConnNum());

            // Test multi release
            session.release();
            session.release();
        } catch (Exception e) {
            e.printStackTrace();
            assert (false);
        }
    }

    @Test()
    public void testExecuteTimeout() {
        Session session = null;
        try {
            NebulaPoolConfig nebulaPoolConfig = new NebulaPoolConfig();
            nebulaPoolConfig.setMinConnSize(1);
            nebulaPoolConfig.setMaxConnSize(1);
            // set timeout
            nebulaPoolConfig.setTimeout(100);
            List<HostAddress> addresses = Collections.singletonList(
                new HostAddress("127.0.0.1", 9670));
            NebulaPool pool = new NebulaPool();
            assert pool.init(addresses, nebulaPoolConfig);
            session = pool.getSession("root", "nebula", false);
            // test timeout
            try {
                StringBuilder ngql = new StringBuilder();
                for (int n = 0; n < 500; n++) {
                    ngql.append("show hosts;");
                }
                ResultSet resultSet = session.execute(ngql.toString());
                System.out.println(resultSet);
                assert false;
            } catch (IOErrorException e) {
                System.out.println(e.getMessage());
                Assert.assertTrue(e.getMessage().contains("timed out"));
                assert true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            assert (false);
        } finally {
            if (session != null) {
                session.release();
            }
        }
    }

    @Test()
    public void testClose() {
        try {
            NebulaPoolConfig nebulaPoolConfig = new NebulaPoolConfig();
            nebulaPoolConfig.setMaxConnSize(1);
            List<HostAddress> addresses = Collections.singletonList(
                    new HostAddress("127.0.0.1", 9670));
            NebulaPool pool = new NebulaPool();
            Assert.assertTrue(pool.init(addresses, nebulaPoolConfig));
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

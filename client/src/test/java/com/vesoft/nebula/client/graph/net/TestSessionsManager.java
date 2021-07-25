/* Copyright (c) 2021 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.graph.net;

import com.vesoft.nebula.client.graph.NebulaPoolConfig;
import com.vesoft.nebula.client.graph.SessionsManagerConfig;
import com.vesoft.nebula.client.graph.data.HostAddress;
import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.exception.AuthFailedException;
import com.vesoft.nebula.client.graph.exception.IOErrorException;
import com.vesoft.nebula.client.graph.exception.InvalidConfigException;
import com.vesoft.nebula.client.graph.exception.InvalidSessionException;
import com.vesoft.nebula.client.graph.exception.NotValidConnectionException;
import org.junit.Assert;
import org.junit.Test;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class TestSessionsManager {
    @Test()
    public void testBase() {
        try {
            try {
                NebulaPool pool = new NebulaPool();
                NebulaPoolConfig nebulaPoolConfig = new NebulaPoolConfig();
                nebulaPoolConfig.setMaxConnSize(1);
                Assert.assertTrue(pool.init(Collections.singletonList(new HostAddress("127.0.0.1", 9670)),
                    nebulaPoolConfig));
                Session session = pool.getSession("root", "nebula", true);
                ResultSet resp = session.execute("CREATE SPACE IF NOT EXISTS test_session_manager(vid_type=INT);");
                Assert.assertTrue(resp.getErrorMessage(), resp.isSucceeded());
                session.release();
                pool.close();
                TimeUnit.SECONDS.sleep(3);
            } catch (UnknownHostException
                | NotValidConnectionException
                | AuthFailedException
                | InterruptedException e) {
                Assert.assertFalse(e.getMessage(), false);
            }

            SessionsManagerConfig config = new SessionsManagerConfig();
            NebulaPoolConfig poolConfig = new NebulaPoolConfig();
            poolConfig.setMaxConnSize(4);
            config.setAddresses(Collections.singletonList(
                new HostAddress("127.0.0.1", 9670)))
                .setUserName("root")
                .setPassword("nebula")
                .setSpaceName("test_session_manager")
                .setPoolConfig(poolConfig);
            SessionsManager sessionsManager = new SessionsManager(config);
            // Gets the session of the specified space
            SessionWrapper session = sessionsManager.getSession();
            ResultSet resultSet = session.execute("SHOW TAGS");
            Assert.assertEquals("test_session_manager", resultSet.getSpaceName());

            // Test get SessionWrapper failed
            List<SessionWrapper> sessionList = new ArrayList<>();
            for (int i = 0 ; i < 3; i++) {
                sessionList.add(sessionsManager.getSession());
            }
            try {
                sessionsManager.getSession();
                Assert.fail();
            } catch (RuntimeException e) {
                Assert.assertTrue(e.getMessage().contains("The driverManager does not have available sessions."));
                Assert.assertTrue(e.getMessage(), true);
            }

            // Test return Session
            try {
                sessionsManager.returnSession(session);
                session.execute("SHOW TAGS");
                Assert.fail();
            } catch (InvalidSessionException e) {
                Assert.assertTrue(e.getMessage(), true);
            } catch (Exception e) {
                Assert.assertFalse(e.getMessage(), false);
            }

            // Test get the session success after return session
            try {
                SessionWrapper session2 = sessionsManager.getSession();
                resultSet = session2.execute("SHOW TAGS");
                Assert.assertEquals("test_session_manager", resultSet.getSpaceName());
            } catch (RuntimeException e) {
                Assert.assertFalse(e.getMessage(), true);
            }
            for (int i = 0 ; i < 3; i++) {
                sessionsManager.returnSession(sessionList.get(i));
            }
            try {
                for (int i = 0 ; i < 3; i++) {
                    sessionsManager.getSession();
                }
            } catch (RuntimeException e) {
                Assert.assertFalse(e.getMessage(), true);
            }

            // Test close
            try {
                session = sessionsManager.getSession();
                sessionsManager.close();
                session.execute("SHOW SPACES");
            } catch (RuntimeException e) {
                System.out.println(e.getMessage());
                assert true;
            } catch (Exception e) {
                assert false;
            }

        } catch (InvalidConfigException | IOErrorException e) {
            Assert.fail();
        }
    }
}

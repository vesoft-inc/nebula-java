/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.graph.net;

import com.vesoft.nebula.client.graph.NebulaPoolConfig;
import com.vesoft.nebula.client.graph.data.HostAddress;
import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.exception.IOErrorException;
import com.vesoft.nebula.graph.ErrorCode;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;

public class TestSession {
    @Test()
    public void testReconnectWithOneService() {
        System.out.println("testReconnectWithOneService");
        NebulaPool pool = new NebulaPool();
        try {
            NebulaPoolConfig nebulaPoolConfig = new NebulaPoolConfig();
            nebulaPoolConfig.setMaxConnSize(1);
            List<HostAddress> addresses = Arrays.asList(
                new HostAddress("127.0.0.1", 9669));
            Assert.assertTrue(pool.init(addresses, nebulaPoolConfig));
            Session session = pool.getSession("root", "nebula", true);
            session.release();

            Runtime runtime = Runtime.getRuntime();
            runtime.exec("docker restart nebula-docker-compose_graphd0_1")
                .waitFor(5, TimeUnit.SECONDS);
            TimeUnit.SECONDS.sleep(5);
            // the connections in pool are broken, test getSession can get right connection
            session = pool.getSession("root", "nebula", true);

            // the connections in pool are broken, test execute can get right connection
            runtime.exec("docker restart nebula-docker-compose_graphd0_1")
                .waitFor(5, TimeUnit.SECONDS);
            TimeUnit.SECONDS.sleep(5);
            session.execute("SHOW SPACES");
            session.release();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertFalse(e.getMessage(), true);
        } finally {
            pool.close();
        }
    }

    @Test()
    public void testReconnectWithMultiServices() {
        Runtime runtime = Runtime.getRuntime();
        NebulaPool pool = new NebulaPool();
        try {
            NebulaPoolConfig nebulaPoolConfig = new NebulaPoolConfig();
            nebulaPoolConfig.setMaxConnSize(6);
            List<HostAddress> addresses = Arrays.asList(
                    new HostAddress("127.0.0.1", 9669),
                    new HostAddress("127.0.0.1", 9670),
                    new HostAddress("127.0.0.1", 9671));
            Assert.assertTrue(pool.init(addresses, nebulaPoolConfig));
            Session session = pool.getSession("root", "nebula", true);

            // test ping
            Assert.assertTrue(session.ping());

            for (int i = 0; i < 10; i++) {
                if (i == 3) {
                    runtime.exec("docker stop nebula-docker-compose_graphd0_1")
                            .waitFor(5, TimeUnit.SECONDS);
                    runtime.exec("docker stop nebula-docker-compose_graphd1_1")
                            .waitFor(5, TimeUnit.SECONDS);
                }
                try {
                    ResultSet resp = session.execute("SHOW SPACES");
                    if (i >= 3) {
                        Assert.assertEquals(ErrorCode.E_SESSION_INVALID, resp.getErrorCode());
                    } else {
                        Assert.assertEquals(ErrorCode.SUCCEEDED, resp.getErrorCode());
                    }
                } catch (IOErrorException ie) {
                    Assert.fail();
                }
                TimeUnit.SECONDS.sleep(2);
            }
            session.release();
            // test release then execute ngql
            ResultSet result = session.execute("SHOW SPACES;");
            Assert.assertFalse(result.isSucceeded());

            // get new session from the pool
            Session session1 = pool.getSession("root", "nebula", false);
            Assert.assertNotNull(session1);
            Session session2 = pool.getSession("root", "nebula", false);
            Assert.assertNotNull(session2);

        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertFalse(e.getMessage(),false);
        } finally {
            try {
                runtime.exec("docker start nebula-docker-compose_graphd0_1")
                        .waitFor(5, TimeUnit.SECONDS);
                runtime.exec("docker start nebula-docker-compose_graphd1_1")
                        .waitFor(5, TimeUnit.SECONDS);
            } catch (Exception e) {
                e.printStackTrace();
            }
            pool.close();
        }
    }
}

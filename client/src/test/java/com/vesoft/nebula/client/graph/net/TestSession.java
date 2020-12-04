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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestSession {
    private static NebulaPool pool = new NebulaPool();

    @BeforeClass
    public static void beforeClass() throws Exception {
        try {
            NebulaPoolConfig nebulaPoolConfig = new NebulaPoolConfig();
            nebulaPoolConfig.setMaxConnSize(6);
            List<HostAddress> addresses = Arrays.asList(
                new HostAddress("127.0.0.1", 9669),
                new HostAddress("127.0.0.1", 9670),
                new HostAddress("127.0.0.1", 9671));
            Assert.assertTrue(pool.init(addresses, nebulaPoolConfig));
        } catch (Exception e) {
            e.printStackTrace();
            assert false;
        }
    }

    @AfterClass
    public static void afterClass() throws Exception {
        pool.close();
    }

    @Test()
    public void testAll() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Session session = pool.getSession("root", "nebula", true);
            ExecutorService executorService = Executors.newFixedThreadPool(4);

            // Test safe interface
            ResultSet resultSet = session.execute("CREATE SPACE IF NOT EXISTS test;"
                + "USE test;CREATE TAG IF NOT EXISTS a()");
            assert resultSet.isSucceeded();
            AtomicInteger succeedCount = new AtomicInteger(0);
            AtomicInteger invalidSessionCount = new AtomicInteger(0);
            for (int i = 0; i < 4; i++) {
                executorService.submit(() -> {
                    try {
                        ResultSet resp = session.executeThreadSafe("SHOW TAGS;");
                        succeedCount.incrementAndGet();
                        if (resp.getErrorCode() == ErrorCode.E_SESSION_INVALID) {
                            invalidSessionCount.incrementAndGet();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        assert false;
                    }
                });
            }
            executorService.awaitTermination(2, TimeUnit.SECONDS);
            executorService.shutdown();
            assert succeedCount.get() >= 1;
            assert invalidSessionCount.get() >= 1;

            // Test reconnect
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
                    ie.printStackTrace();
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

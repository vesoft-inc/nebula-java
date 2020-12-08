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
import java.util.Objects;
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
            Session safeSession = pool.getSession("root", "nebula", true);
            ExecutorService executorService = Executors.newFixedThreadPool(4);

            // Test safe interface
            ResultSet resultSet = safeSession.safeExecute("CREATE SPACE IF NOT EXISTS test;"
                + "USE test;CREATE TAG IF NOT EXISTS a()");
            assert resultSet.isSucceeded();
            AtomicInteger succeedCount = new AtomicInteger(0);
            AtomicInteger invalidSessionCount = new AtomicInteger(0);
            for (int i = 0; i < 4; i++) {
                executorService.submit(() -> {
                    try {
                        ResultSet resp = safeSession.safeExecute("SHOW TAGS;");
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
            Assert.assertEquals(4, succeedCount.get());
            Assert.assertEquals(0, invalidSessionCount.get());

            // test use execute and safeExecute in the same session
            try {
                safeSession.execute("SHOW HOSTS;");
                assert false;
            } catch (IOErrorException e) {
                assert Objects.equals(new String(e.getMessage()),
                    "The session is already called safeExecute, "
                        + "You can only use execute or safeExecute in the session");
                assert true;
            }

            // Test reconnect
            Session session = pool.getSession("root", "nebula", true);
            for (int i = 0; i < 10; i++) {
                if (i == 3) {
                    runtime.exec("docker stop nebula-docker-compose_graphd0_1")
                        .waitFor(5, TimeUnit.SECONDS);
                    runtime.exec("docker stop nebula-docker-compose_graphd1_1")
                        .waitFor(5, TimeUnit.SECONDS);
                }
                try {
                    ResultSet resp = session.execute("SHOW SPACES");
                    ResultSet resp1 = safeSession.safeExecute("SHOW SPACES");
                    if (i >= 3) {
                        Assert.assertEquals(ErrorCode.E_SESSION_INVALID, resp.getErrorCode());
                        Assert.assertEquals(ErrorCode.E_SESSION_INVALID, resp1.getErrorCode());
                    } else {
                        Assert.assertEquals(ErrorCode.SUCCEEDED, resp.getErrorCode());
                        Assert.assertEquals(ErrorCode.SUCCEEDED, resp1.getErrorCode());
                    }
                } catch (IOErrorException ie) {
                    ie.printStackTrace();
                    Assert.fail();
                }
                TimeUnit.SECONDS.sleep(2);
            }

            // test use execute and safeExecute in the same session
            try {
                session.safeExecute("SHOW HOSTS;");
                assert false;
            } catch (IOErrorException e) {
                assert Objects.equals(new String(e.getMessage()),
                    "The session is already called execute, "
                        + "You can only use execute or safeExecute in the session");
                assert true;
            }
            safeSession.release();
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
            Assert.assertFalse(e.getMessage(),true);
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

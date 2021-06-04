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
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;

public class TestSession {
    private static void printProcessStatus(String cmd, Process p) {
        try {
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(p.getInputStream()));

            String line;
            System.out.print(cmd + " output: ");
            while ((line = reader.readLine()) != null) {
                System.out.print(line);
            }
            System.out.print("\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testMultiThreadUseTheSameSession() {
        NebulaPool pool = new NebulaPool();
        try {
            NebulaPoolConfig nebulaPoolConfig = new NebulaPoolConfig();
            nebulaPoolConfig.setMaxConnSize(1);
            List<HostAddress> addresses = Arrays.asList(new HostAddress("127.0.0.1", 9670));
            Assert.assertTrue(pool.init(addresses, nebulaPoolConfig));
            Session session = pool.getSession("root", "nebula", true);
            ExecutorService executorService = Executors.newFixedThreadPool(10);
            AtomicInteger failedCount = new AtomicInteger(0);
            AtomicReference<String> exceptionStr = new AtomicReference<>("");
            for (int i = 0; i < 10; i++) {
                executorService.submit(() -> {
                    try {
                        session.execute("SHOW SPACES;");
                    } catch (Exception e) {
                        exceptionStr.set(e.getMessage());
                        failedCount.incrementAndGet();
                    }
                });
            }
            executorService.awaitTermination(10, TimeUnit.SECONDS);
            executorService.shutdown();
            assert failedCount.get() > 0;
            Assert.assertTrue(exceptionStr.get().contains(
                "Multi threads use the same session, "
                    + "the previous execution was not completed, current thread is:"));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertFalse(e.getMessage(),false);
        } finally {
            pool.close();
        }
    }

    @Test
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

    @Test
    public void testReconnectWithMultiServices() {
        Runtime runtime = Runtime.getRuntime();
        NebulaPool pool = new NebulaPool();
        try {
            // make sure the graphd2_1 without any sessions
            String cmd = "docker restart nebula-docker-compose_graphd2_1";
            Process p = runtime.exec(cmd);
            p.waitFor(10, TimeUnit.SECONDS);
            printProcessStatus(cmd, p);

            NebulaPoolConfig nebulaPoolConfig = new NebulaPoolConfig();
            nebulaPoolConfig.setMaxConnSize(6);
            List<HostAddress> addresses = Arrays.asList(
                new HostAddress("127.0.0.1", 9669),
                new HostAddress("127.0.0.1", 9670),
                new HostAddress("127.0.0.1", 9671));
            Assert.assertTrue(pool.init(addresses, nebulaPoolConfig));
            Session session = pool.getSession("root", "nebula", true);
            System.out.println("The address of session is " + session.getGraphHost());

            // test ping
            Assert.assertTrue(session.ping());

            for (int i = 0; i < 10; i++) {
                if (i == 3) {
                    cmd = "docker stop nebula-docker-compose_graphd0_1";
                    p = runtime.exec(cmd);
                    p.waitFor(5, TimeUnit.SECONDS);
                    printProcessStatus(cmd, p);
                    cmd = "docker stop nebula-docker-compose_graphd1_1";
                    p = runtime.exec(cmd);
                    p.waitFor(5, TimeUnit.SECONDS);
                    printProcessStatus(cmd, p);
                }
                try {
                    ResultSet resp = session.execute("SHOW SPACES");
                    System.out.println("The address of session is " + session.getGraphHost());
                    if (i >= 3) {
                        Assert.assertEquals(
                            ErrorCode.E_SESSION_INVALID.getValue(), resp.getErrorCode());
                    } else {
                        Assert.assertEquals(ErrorCode.SUCCEEDED.getValue(), resp.getErrorCode());
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

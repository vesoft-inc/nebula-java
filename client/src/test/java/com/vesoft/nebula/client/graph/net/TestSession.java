/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.client.graph.net;

import com.vesoft.nebula.Date;
import com.vesoft.nebula.Row;
import com.vesoft.nebula.Value;
import com.vesoft.nebula.client.graph.NebulaPoolConfig;
import com.vesoft.nebula.client.graph.data.HostAddress;
import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.exception.IOErrorException;
import com.vesoft.nebula.client.util.ProcessUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;

public class TestSession {

    @Test
    public void testMultiThreadUseTheSameSession() {
        System.out.println("<==== testMultiThreadUseTheSameSession ====>");
        NebulaPool pool = new NebulaPool();
        try {
            NebulaPoolConfig nebulaPoolConfig = new NebulaPoolConfig();
            nebulaPoolConfig.setMaxConnSize(1);
            List<HostAddress> addresses = Arrays.asList(new HostAddress("127.0.0.1", 9670));
            Assert.assertTrue(pool.init(addresses, nebulaPoolConfig));
            Session session = pool.getSession("root", "nebula", true);
            ExecutorService executorService = Executors.newFixedThreadPool(10);
            AtomicInteger failedCount = new AtomicInteger(0);
            for (int i = 0; i < 10; i++) {
                executorService.submit(
                        () -> {
                            try {
                                session.execute("SHOW SPACES;");
                            } catch (Exception e) {
                                failedCount.incrementAndGet();
                            }
                        });
            }
            executorService.awaitTermination(10, TimeUnit.SECONDS);
            executorService.shutdown();
            assert failedCount.get() == 0;
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertFalse(e.getMessage(), false);
        } finally {
            pool.close();
        }
    }

    @Test
    public void testReconnectWithOneService() {
        System.out.println("<==== testReconnectWithOneService ====>");
        NebulaPool pool = new NebulaPool();
        try {
            NebulaPoolConfig nebulaPoolConfig = new NebulaPoolConfig();
            nebulaPoolConfig.setMaxConnSize(1);
            List<HostAddress> addresses = Arrays.asList(new HostAddress("127.0.0.1", 9669));
            Assert.assertTrue(pool.init(addresses, nebulaPoolConfig));
            Session session = pool.getSession("root", "nebula", true);
            session.release();

            Runtime runtime = Runtime.getRuntime();
            runtime.exec("docker restart nebula-docker-compose_graphd0_1")
                    .waitFor(5, TimeUnit.SECONDS);
            TimeUnit.SECONDS.sleep(10);
            // the connections in pool are broken, test getSession can get right connection
            session = pool.getSession("root", "nebula", true);

            // the connections in pool are broken, test execute can get right connection
            runtime.exec("docker restart nebula-docker-compose_graphd0_1")
                    .waitFor(5, TimeUnit.SECONDS);
            TimeUnit.SECONDS.sleep(10);
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
        System.out.println("<==== testReconnectWithMultiServices ====>");
        Runtime runtime = Runtime.getRuntime();
        NebulaPool pool = new NebulaPool();
        try {
            // make sure the graphd2_1 without any sessions
            String cmd = "docker restart nebula-docker-compose_graphd2_1";
            Process p = runtime.exec(cmd);
            p.waitFor(10, TimeUnit.SECONDS);
            ProcessUtil.printProcessStatus(cmd, p);

            NebulaPoolConfig nebulaPoolConfig = new NebulaPoolConfig();
            nebulaPoolConfig.setMaxConnSize(6);
            List<HostAddress> addresses =
                    Arrays.asList(
                            new HostAddress("127.0.0.1", 9669),
                            new HostAddress("127.0.0.1", 9670),
                            new HostAddress("127.0.0.1", 9671));
            TimeUnit.SECONDS.sleep(15);
            Assert.assertTrue(pool.init(addresses, nebulaPoolConfig));
            TimeUnit.SECONDS.sleep(15);
            Session session = pool.getSession("root", "nebula", true);
            System.out.println("The address of session is " + session.getGraphHost());

            // test ping
            Assert.assertTrue(session.ping());

            ResultSet resp =
                    session.execute(
                            "CREATE SPACE IF NOT EXISTS test_session(vid_type=fixed_string(8)); "
                                    + "USE test_session;");
            Assert.assertTrue(resp.isSucceeded());
            for (int i = 0; i < 10; i++) {
                if (i == 3) {
                    cmd = "docker stop nebula-docker-compose_graphd0_1";
                    p = runtime.exec(cmd);
                    p.waitFor(5, TimeUnit.SECONDS);
                    ProcessUtil.printProcessStatus(cmd, p);
                    cmd = "docker stop nebula-docker-compose_graphd1_1";
                    p = runtime.exec(cmd);
                    p.waitFor(5, TimeUnit.SECONDS);
                    ProcessUtil.printProcessStatus(cmd, p);
                }
                try {
                    // the session update later
                    // resp = session.execute("SHOW TAGS");
                    resp = session.execute("SHOW HOSTS");
                    System.out.println("The address of session is " + session.getGraphHost());
                    Assert.assertTrue(resp.getErrorMessage(), resp.isSucceeded());
                    // Assert.assertEquals(resp.getSpaceName(), "test_session");
                } catch (IOErrorException ie) {
                    ie.printStackTrace();
                    Assert.assertFalse(ie.getMessage(), true);
                }
                TimeUnit.SECONDS.sleep(2);
            }
            // test release then execute ngql
            session.release();
            try {
                session.execute("SHOW SPACES;");
                assert false;
            } catch (IOErrorException e) {
                assert true;
            }

            // get new session from the pool
            Session session1 = pool.getSession("root", "nebula", false);
            Assert.assertNotNull(session1);
            Session session2 = pool.getSession("root", "nebula", false);
            Assert.assertNotNull(session2);

        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertFalse(e.getMessage(), false);
        } finally {
            try {
                runtime.exec("docker start nebula-docker-compose_graphd0_1")
                        .waitFor(5, TimeUnit.SECONDS);
                runtime.exec("docker start nebula-docker-compose_graphd1_1")
                        .waitFor(5, TimeUnit.SECONDS);
                TimeUnit.SECONDS.sleep(5);
            } catch (Exception e) {
                e.printStackTrace();
            }
            pool.close();
        }
    }

    @Test
    public void testExecuteWithParameter() {
        System.out.println("<==== testExecuteWithParameter ====>");
        Runtime runtime = Runtime.getRuntime();
        NebulaPool pool = new NebulaPool();
        try {
            // make sure the graphd2_1 without any sessions
            String cmd = "docker restart nebula-docker-compose_graphd2_1";
            Process p = runtime.exec(cmd);
            p.waitFor(10, TimeUnit.SECONDS);
            ProcessUtil.printProcessStatus(cmd, p);

            NebulaPoolConfig nebulaPoolConfig = new NebulaPoolConfig();
            nebulaPoolConfig.setMaxConnSize(6);
            List<HostAddress> addresses =
                    Arrays.asList(
                            new HostAddress("127.0.0.1", 9669),
                            new HostAddress("127.0.0.1", 9670),
                            new HostAddress("127.0.0.1", 9671));
            TimeUnit.SECONDS.sleep(15);
            Assert.assertTrue(pool.init(addresses, nebulaPoolConfig));
            TimeUnit.SECONDS.sleep(15);
            Session session = pool.getSession("root", "nebula", true);
            System.out.println("The address of session is " + session.getGraphHost());

            // test ping
            Assert.assertTrue(session.ping());
            // prepare parameters
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("p1", 3);
            paramMap.put("p2", true);
            paramMap.put("p3", 3.3);
            Value nvalue = new Value();
            Date date = new Date();
            date.setYear((short) 2021);
            nvalue.setDVal(date);
            List<Object> list = new ArrayList<>();
            list.add(1);
            list.add(true);
            list.add(nvalue);
            list.add(date);
            paramMap.put("p4", list);
            Map<String, Object> map = new HashMap<>();
            map.put("a", 1);
            map.put("b", true);
            map.put("c", nvalue);
            map.put("d", list);
            paramMap.put("p5", map);
            // test `executeWithParameter` interface
            ResultSet resp =
                    session.executeWithParameter("RETURN $p1+1,$p2,$p3,$p4[2],$p5.d[3]", paramMap);
            Assert.assertTrue(resp.isSucceeded());
            Row row = resp.getRows().get(0);
            Assert.assertTrue(row.values.get(0).equals(Value.iVal(4)));
            Assert.assertTrue(row.values.get(1).equals(Value.bVal(true)));
            Assert.assertTrue(row.values.get(2).equals(Value.fVal(3.3)));
            Assert.assertTrue(row.values.get(3).equals(list.get(2)));
            Assert.assertTrue(row.values.get(4).getDVal().equals(list.get(3)));
            // release session
            session.release();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertFalse(true);
        } finally {
            try {
                runtime.exec("docker start nebula-docker-compose_graphd0_1")
                        .waitFor(5, TimeUnit.SECONDS);
                runtime.exec("docker start nebula-docker-compose_graphd1_1")
                        .waitFor(5, TimeUnit.SECONDS);
                TimeUnit.SECONDS.sleep(5);
            } catch (Exception e) {
                e.printStackTrace();
            }
            pool.close();
        }
    }
}

/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.net;

import com.vesoft.nebula.driver.graph.ErrorCode;
import com.vesoft.nebula.driver.graph.ServerConstant;
import com.vesoft.nebula.driver.graph.data.ResultSet;
import com.vesoft.nebula.driver.graph.exception.AuthFailedException;
import com.vesoft.nebula.driver.graph.util.MockGraph;
import com.vesoft.nebula.driver.graph.util.ProcessUtil;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class NebulaPoolTest {

    String addresses = ServerConstant.address;
    String user      = ServerConstant.user;
    String passwd    = ServerConstant.passwd;

    @Before
    public void setup() {
        MockGraph.mockGraphData();
    }

    @Test
    public void testNullUser() {
        System.out.println("<==== testNullUser =====>");
        NebulaPool pool = null;
        try {
            pool = NebulaPool.builder(addresses, null, null)
                .withConnectTimeoutMills(1111)
                .withRequestTimeoutMills(2222)
                .withScanParallel(15)
                .build();
            NebulaClient client = pool.getClient();
            pool.returnClient(client);
        } catch (AuthFailedException e) {
            Assert.assertTrue(true);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        } finally {
            if (pool != null) {
                pool.close();
            }
        }
    }

    @Test
    public void testBuilder() {
        System.out.println("<==== testBuilder =====>");
        NebulaPool pool = null;
        try {
            pool = NebulaPool.builder(addresses, user, passwd)
                .withConnectTimeoutMills(1111)
                .withRequestTimeoutMills(2222)
                .withScanParallel(15)
                .withHealthCheckTimeMills(3333)
                .build();
            NebulaClient client = pool.getClient();
            Assert.assertEquals(1111L, client.getConnectTimeoutMills());
            Assert.assertEquals(2222L, client.getRequestTimeoutMills());
            Assert.assertEquals(15, client.getScanParallel());
            pool.returnClient(client);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        } finally {
            if (pool != null) {
                pool.close();
            }
        }

        try {
            pool = NebulaPool.builder(addresses, user, passwd)
                .withConnectTimeoutMills(0)
                .withRequestTimeoutMills(-1)
                .build();
            NebulaClient client = pool.getClient();
            Assert.assertEquals(Integer.MAX_VALUE, client.getConnectTimeoutMills());
            Assert.assertEquals(Integer.MAX_VALUE, client.getRequestTimeoutMills());
            pool.returnClient(client);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        } finally {
            if (pool != null) {
                pool.close();
            }
        }
    }

    @Test
    public void testWrongServerWithStrictlyServerHealthy() {
        System.out.println("<==== testWrongServerWithStrictlyServerHealthy ====>");
        NebulaPool pool = null;
        try {
            pool = NebulaPool.builder(addresses, user, "123")
                .withMaxClientSize(10)
                .withMinClientSize(1)
                .build();
        } catch (Exception e) {
            Assert.assertEquals(
                "Auth failed: Authenticate error: invalid username or password",
                e.getMessage());
        } finally {
            if (pool != null) {
                pool.close();
            }
        }

        try {
            pool = NebulaPool.builder("127.0.0.1:1000", user, "123")
                .withMaxClientSize(10)
                .withMinClientSize(1)
                .build();
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("Connection refused (Connection refused)")
                                  || e.getMessage().contains("UNAVAILABLE: io exception"));
        } finally {
            if (pool != null) {
                pool.close();
            }
        }
    }

    @Test
    public void testNebulaPool() {
        System.out.println("<==== testNebulaPool ====>");
        NebulaPool pool = null;
        try {
            pool = NebulaPool.builder(addresses, user, passwd)
                .withMaxClientSize(10)
                .withMinClientSize(1)
                .build();
            NebulaClient client = pool.getClient();
            client.execute("RETURN 1");
            pool.returnClient(client);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        } finally {
            if (pool != null) {
                pool.close();
            }
        }
    }

    @Test
    public void testSessionSet() {
        System.out.println("<==== testSessionSet ====>");
        NebulaPool pool = null;
        // test all session set configs
        try {
            pool = NebulaPool.builder(addresses, user, passwd)
                .withGraph("nba")
                .withSchema("/default_schema")
                .withTimeZone("Asia/Shanghai")
                .build();
            NebulaClient client = pool.getClient();
            Assert.assertEquals("/default_schema/nba",
                                client
                                    .execute("show current_session")
                                    .next()
                                    .get("graph")
                                    .asString());
            Assert.assertEquals("/default_schema",
                                client
                                    .execute("show current_session")
                                    .next()
                                    .get("schema")
                                    .asString());
            Assert.assertEquals("Asia/Shanghai",
                                client
                                    .execute("show current_session")
                                    .next()
                                    .get("timezone")
                                    .asString());
            pool.returnClient(client);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        } finally {
            if (pool != null) {
                pool.close();
            }
        }

        // test one session set config

        try {
            pool = NebulaPool.builder(addresses, user, passwd)
                .withGraph("nba")
                .build();
            NebulaClient client = pool.getClient();
            Assert.assertEquals("/default_schema/nba",
                                client
                                    .execute("show current_session")
                                    .next()
                                    .get("graph")
                                    .asString());
            pool.returnClient(client);
        } catch (Exception e) {
            assert e.getMessage().contains("SESSION SET GRAPH \"nba_not_exist\" failed");
        } finally {
            if (pool != null) {
                pool.close();
            }
        }

        try {
            pool = NebulaPool.builder(addresses, user, passwd)
                .withSchema("/default_schema")
                .build();
            NebulaClient client = pool.getClient();
            Assert.assertEquals("/default_schema",
                                client
                                    .execute("show current_session")
                                    .next()
                                    .get("schema")
                                    .asString());
            pool.returnClient(client);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        } finally {
            if (pool != null) {
                pool.close();
            }
        }

        try {
            pool = NebulaPool.builder(addresses, user, passwd)
                .withTimeZone("UTC")
                .build();
            NebulaClient client = pool.getClient();
            Assert.assertEquals("UTC",
                                client
                                    .execute("show current_session")
                                    .next()
                                    .get("timezone")
                                    .asString());
            pool.returnClient(client);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        } finally {
            if (pool != null) {
                pool.close();
            }
        }

        // test session set format
        try {
            pool = NebulaPool.builder(addresses, user, passwd)
                .withDateFormat("%Y/%m/%d")
                .withLocalDatetimeFormat("%Y-%m-%d %H:%M:%S")
                .withZonedDatetimeFormat("%Y-%m-%d %H:%M:%S %z")
                .withLocalTimeFormat("%H-%M-%S")
                .withZonedTimeFormat("%H-%M-%S %z")
                .build();
            NebulaClient client = pool.getClient();
            ResultSet    res    = client.execute("show session configs");
            Assert.assertEquals(5, res.rowSize());
            while (res.hasNext()) {
                ResultSet.Record record = res.next();
                if (record.get("name").asString().equals("date_format")) {
                    Assert.assertEquals("%Y/%m/%d", record.get("value").asString());
                }
                if (record.get("name").asString().equals("local_datetime_format")) {
                    Assert.assertEquals("%Y-%m-%d %H:%M:%S", record.get("value").asString());
                }
                if (record.get("name").asString().equals("zoned_datetime_format")) {
                    Assert.assertEquals("%Y-%m-%d %H:%M:%S %z", record.get("value").asString());
                }
                if (record.get("name").asString().equals("local_time_format")) {
                    Assert.assertEquals("%H-%M-%S", record.get("value").asString());
                }
                if (record.get("name").asString().equals("zoned_time_format")) {
                    Assert.assertEquals("%H-%M-%S %z", record.get("value").asString());
                }
            }

            pool.returnClient(client);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        } finally {
            if (pool != null) {
                pool.close();
            }
        }
        // test sessionConfigs
        Map<String, String> configs = new HashMap<>();
        try {
            configs.put("date_format", "\"%Y/%m/%d\"");
            pool = NebulaPool.builder(addresses, user, passwd)
                .withSessionConfigs(configs)
                .build();
            NebulaClient client = pool.getClient();
            ResultSet    res    = client.execute("show session configs");
            Assert.assertEquals(1, res.rowSize());
            while (res.hasNext()) {
                ResultSet.Record record = res.next();
                if (record.get("name").asString().equals("date_format")) {
                    Assert.assertEquals("%Y/%m/%d", record.get("value").asString());
                }
            }
            pool.returnClient(client);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        } finally {
            if (pool != null) {
                pool.close();
            }
        }

        // test the sequence of configs
        try {
            pool = NebulaPool.builder(addresses, user, passwd)
                .withDateFormat("%Y--%m--%d")
                .withSessionConfigs(configs)
                .build();
            NebulaClient client = pool.getClient();
            ResultSet    res    = client.execute("show session configs");
            Assert.assertEquals(1, res.rowSize());
            while (res.hasNext()) {
                ResultSet.Record record = res.next();
                if (record.get("name").asString().equals("date_format")) {
                    Assert.assertEquals("%Y/%m/%d", record.get("value").asString());
                }
            }
            pool.returnClient(client);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        } finally {
            if (pool != null) {
                pool.close();
            }
        }

        try {
            pool = NebulaPool.builder(addresses, user, passwd)
                .withSessionConfigs(configs)
                .withDateFormat("%Y--%m--%d")
                .build();
            NebulaClient client = pool.getClient();
            ResultSet    res    = client.execute("show session configs");
            Assert.assertEquals(1, res.rowSize());
            while (res.hasNext()) {
                ResultSet.Record record = res.next();
                if (record.get("name").asString().equals("date_format")) {
                    Assert.assertEquals("%Y--%m--%d", record.get("value").asString());
                }
            }
            pool.returnClient(client);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        } finally {
            if (pool != null) {
                pool.close();
            }
        }

        // test wrong graph
        try {
            pool = NebulaPool.builder(addresses, user, passwd)
                .withGraph("nba_not_exist")
                .build();
            pool.getClient();
            Assert.fail("get client should fail.");
        } catch (Exception e) {
            assert e.getMessage().contains("SESSION SET GRAPH \"nba_not_exist\" failed");
        } finally {
            if (pool != null) {
                pool.close();
            }
        }

        // test null format config
        try {
            pool = NebulaPool.builder(addresses, user, passwd)
                .withDateFormat(null)
                .withDateFormat("")
                .withLocalTimeFormat(null)
                .withLocalTimeFormat("")
                .withLocalDatetimeFormat(null)
                .withLocalDatetimeFormat("")
                .withZonedTimeFormat(null)
                .withZonedTimeFormat("")
                .withZonedDatetimeFormat(null)
                .withZonedDatetimeFormat("")
                .build();
            NebulaClient client = pool.getClient();
            ResultSet    res    = client.execute("show session configs");
            Assert.assertEquals(0, res.rowSize());
            pool.returnClient(client);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        } finally {
            if (pool != null) {
                pool.close();
            }
        }

    }

    @Test
    public void testPreStatement() {
        System.out.println("<==== testPreStatement ====>");
        NebulaPool   pool  = null;
        List<String> stmts = new ArrayList<>();
        stmts.add("return 1");
        stmts.add("session set timezone=\"Asia/Shanghai\"");
        try {
            pool = NebulaPool.builder(addresses, user, passwd)
                .withGraph("nba")
                .withPreStatements(stmts)
                .build();
            NebulaClient client = pool.getClient();
            Assert.assertEquals("Asia/Shanghai",
                                client
                                    .execute("show current_session")
                                    .next()
                                    .get("timezone")
                                    .asString());

            pool.returnClient(client);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        } finally {
            if (pool != null) {
                pool.close();
            }
        }
    }

    @Test
    public void testMaxLifeTime() {
        System.out.println("<==== testMaxLifeTime ====>");
        NebulaPool pool = null;
        try {
            pool = NebulaPool.builder(addresses, user, passwd)
                .withMaxLifeTimeMs(5 * 1000)
                .withMaxClientSize(1)
                .build();
            NebulaClient client     = pool.getClient();
            long         sessionId1 = client.getSessionId();
            Thread.sleep(6000);
            pool.returnClient(client);
            long sessionId2 = pool.getClient().getSessionId();
            assert (sessionId1 != sessionId2);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        } finally {
            if (pool != null) {
                pool.close();
            }
        }
    }

    @Test
    public void testWrongPreStatement() {
        System.out.println("<==== testWrongPreStatement ====>");
        NebulaPool   pool  = null;
        List<String> stmts = new ArrayList<>();
        stmts.add("wrong statement");
        try {
            pool = NebulaPool.builder(addresses, user, passwd)
                .withGraph("nba")
                .withPreStatements(stmts)
                .build();
            pool.getClient();
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("wrong statement")
                                  && e.getMessage().contains("syntax error"));
        } finally {
            if (pool != null) {
                pool.close();
            }
        }
    }

    @Test
    public void testStrictlyServerHealthy() {
        System.out.println("<==== testStrictlyServerHealthy ====>");
        // stop one graphd server
        Runtime runtime = Runtime.getRuntime();
        try {
            String  cmd = "docker stop docker-compose-graphd0-1";
            Process p   = runtime.exec(cmd);
            p.waitFor(10, TimeUnit.SECONDS);
            ProcessUtil.printProcessStatus(cmd, p);
            Thread.sleep(5000);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        try {
            NebulaPool pool = NebulaPool.builder(ServerConstant.addresses, user, passwd)
                .withStrictlyServerHealthy(false)
                .build();
            pool.close();
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        try {
            NebulaPool.builder(ServerConstant.addresses, user, passwd)
                .withStrictlyServerHealthy(true)
                .build();
            assert false;
        } catch (Exception e) {
            System.out.println("expect here");
            assert e.getMessage().contains("Connection refused (Connection refused)")
                || e.getMessage().contains("UNAVAILABLE: io exception");
        }

        // start the graphd server
        try {
            String  cmd = "docker start docker-compose-graphd0-1";
            Process p   = runtime.exec(cmd);
            p.waitFor(10, TimeUnit.SECONDS);
            ProcessUtil.printProcessStatus(cmd, p);
            Thread.sleep(30000);
            NebulaPool pool = NebulaPool.builder(addresses, user, passwd)
                .withStrictlyServerHealthy(true)
                .build();
            NebulaClient client    = pool.getClient();
            ResultSet    resultSet = client.execute("return 1");
            Assert.assertEquals(resultSet.getErrorCode(), ErrorCode.SUCCESSFUL_COMPLETION);
            pool.returnClient(client);
            pool.close();
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testMultiThreads() {
        System.out.println("<==== testMultiThreads ====>");
        NebulaPool pool = null;
        try {
            pool = NebulaPool.builder(addresses, user, passwd).build();
            ExecutorService executorService = Executors.newFixedThreadPool(10);
            AtomicInteger   failedCount     = new AtomicInteger(0);
            CountDownLatch  countDownLatch  = new CountDownLatch(10);
            for (int i = 0; i < 10; i++) {
                NebulaPool finalPool = pool;
                executorService.submit(() -> {
                    try {
                        NebulaClient client = finalPool.getClient();
                        client.execute("SHOW GRAPHS");
                        finalPool.returnClient(client);
                    } catch (Exception e) {
                        failedCount.incrementAndGet();
                    } finally {
                        countDownLatch.countDown();
                    }
                });
            }
            countDownLatch.await();
            executorService.shutdownNow();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        } finally {
            if (pool != null) {
                pool.close();
            }
        }
    }

    @Test
    public void testTlsServerWithNoTlsClient() {
        System.out.println("<==== testTlsServerWithNoTlsClient ====>");
        NebulaPool pool = null;
        try {
            pool = NebulaPool
                .builder(ServerConstant.sslAddress, user, passwd)
                .withEnableTls(false)
                .build();
            NebulaClient client = pool.getClient();
            assert false;
        } catch (Exception e) {
            assert true;
        } finally {
            if (pool != null) {
                pool.close();
            }
        }
    }

    @Test
    public void testTls() {
        System.out.println("<==== testTls ====>");
        String     tlsCa   = "../docker-compose-ssl/certs/ca.crt";
        String     tlsCert = "../docker-compose-ssl/certs/client.crt";
        String     tlsKey  = "../docker-compose-ssl/certs/client.key";
        NebulaPool pool    = null;
        try {
            pool = NebulaPool
                .builder(ServerConstant.sslAddress, user, passwd)
                .withEnableTls(true)
                .withTlsCa(tlsCa)
                .withTlsCert(tlsCert, tlsKey)
                .build();
            NebulaClient client = pool.getClient();
            client.execute("RETURN 1");
            assert true;
        } catch (Exception e) {
            e.printStackTrace();
            assert false;
        } finally {
            if (pool != null) {
                pool.close();
            }
        }
    }
}

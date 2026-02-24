/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.net;

import com.vesoft.nebula.driver.graph.ErrorCode;
import com.vesoft.nebula.driver.graph.ServerConstant;
import com.vesoft.nebula.driver.graph.data.ResultSet;
import com.vesoft.nebula.driver.graph.exception.AuthFailedException;
import com.vesoft.nebula.driver.graph.exception.IOErrorException;
import com.vesoft.nebula.driver.graph.util.ProcessUtil;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class NebulaClientTest {

    String addresses = ServerConstant.address;
    String user      = ServerConstant.user;
    String passwd    = ServerConstant.passwd;

    @Test
    public void testNullUser() {
        System.out.println("<==== testNullUser =====>");
        NebulaClient client = null;
        try {
            client = NebulaClient.builder(addresses, null, null)
                .withConnectTimeoutMills(1111)
                .withRequestTimeoutMills(2222)
                .withScanParallel(15)
                .build();
        } catch (AuthFailedException e) {
            Assert.assertTrue(true);
        } catch (Exception e) {
            Assert.fail();
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }

    @Test
    public void testBuilder() {
        System.out.println("<==== testBuilder =====>");
        NebulaClient client = null;
        try {
            client = NebulaClient.builder(addresses, user, passwd)
                .withConnectTimeoutMills(1111)
                .withRequestTimeoutMills(2222)
                .withScanParallel(15)
                .build();
            Assert.assertEquals(1111L, client.getConnectTimeoutMills());
            Assert.assertEquals(2222L, client.getRequestTimeoutMills());
            Assert.assertEquals(15, client.getScanParallel());
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        } finally {
            if (client != null) {
                client.close();
            }
        }

        try {
            client = NebulaClient.builder(addresses, user, passwd)
                .withConnectTimeoutMills(0)
                .withRequestTimeoutMills(-1)
                .build();
            Assert.assertEquals(Integer.MAX_VALUE, client.getConnectTimeoutMills());
            Assert.assertEquals(Integer.MAX_VALUE, client.getRequestTimeoutMills());
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }

    @Test()
    public void testBuildNebulaClient() {
        System.out.println("<==== testBuildNebulaClient ====>");
        NebulaClient client = null;
        try {
            client = NebulaClient.builder(addresses, user, passwd)
                .withRequestTimeoutMills(3000)
                .build();
            ResultSet resultSet = client.execute("return 1");
            Assert.assertEquals(resultSet.getErrorCode(), ErrorCode.SUCCESSFUL_COMPLETION);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }

    @Test()
    public void testBuildNebulaClientFailed() {
        System.out.println("<==== testBuildNebulaClientFailed ====>");
        // config illegal, illegal address
        String       illegalAddress = "127.0.0.1:100000";
        NebulaClient client         = null;
        try {
            client = NebulaClient.builder(illegalAddress, user, passwd).build();
        } catch (AuthFailedException | IOErrorException e) {
            Assert.fail(e.getMessage());
        } catch (IllegalArgumentException e) {
            assert true;
        } finally {
            if (client != null) {
                client.close();
            }
        }

        // config illegal, host name is not exist
        String illegalHostName = "hostname:9669";
        try {
            client = NebulaClient.builder(illegalHostName, user, passwd).build();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("UnknownHostException")) {
                Assert.assertTrue("expect reach here:" + e.getMessage(), true);
            }
        } catch (IOErrorException | AuthFailedException e) {
            Assert.fail(e.getMessage());
        } finally {
            if (client != null) {
                client.close();
            }
        }

        // common builder
        NebulaClient.Builder builder = null;
        try {
            builder = NebulaClient.builder(addresses, user, passwd);
        } catch (RuntimeException e) {
            Assert.fail(e.getMessage());
        }

        // config illegal, illegal requestTimeout
        try {
            client = builder.withRequestTimeoutMills(-1).build();
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }

    //@Test
    public void testReconnectWithMultiServices() {
        System.out.println("<==== testReconnectWithMultiServices ====>");
        Runtime runtime = Runtime.getRuntime();
        String  cmd     = "docker stop docker-compose-graphd0-1";

        NebulaClient client = null;
        try {
            client = NebulaClient.builder(addresses, user, passwd).build();
        } catch (Exception e) {
            assert true;
        } finally {
            if (client != null) {
                client.close();
            }
        }

        try {
            Process p = runtime.exec(cmd);
            p.waitFor(10, TimeUnit.SECONDS);
            ProcessUtil.printProcessStatus(cmd, p);
            Thread.sleep(5000);
            client = NebulaClient.builder(addresses, user, passwd).build();
            ResultSet resultSet = client.execute("RETURN 1");
            assert resultSet.isSucceeded();
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        } finally {
            if (client != null) {
                client.close();
            }
            try {
                cmd = "docker start docker-compose-graphd0-1";
                Process p = runtime.exec(cmd);
                p.waitFor(10, TimeUnit.SECONDS);
                ProcessUtil.printProcessStatus(cmd, p);
                Thread.sleep(10000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testCloseNebulaClient() {
        System.out.println("<==== testCloseNebulaClient ====>");
        NebulaClient client = null;
        try {
            client = NebulaClient.builder(addresses, user, passwd)
                .build();
        } catch (IOErrorException | AuthFailedException e) {
            Assert.fail(e.getMessage());
        }
        client.close();
        // test the idempotence of close()
        client.close();

        // test the check for execute using closed client
        try {
            client.execute("RETURN 1");
            Assert.fail("client execute should fail.");
        } catch (RuntimeException e) {
            Assert.assertEquals(e.getMessage(), "The NebulaClient already closed.");
        } catch (IOErrorException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testCloseClient() {
        NebulaClient nebulaClient = null;
        try {
            nebulaClient = NebulaClient.builder(addresses, user, passwd)
                .build();
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }

        // execution lasts 20 seconds
        long start = System.currentTimeMillis();
        while ((System.currentTimeMillis() - start) < 20000) {
            try {
                nebulaClient.execute("RETURN 1");
            } catch (IOErrorException e) {
                Assert.fail(e.getMessage());
            }
        }
        nebulaClient.close();

        try {
            nebulaClient.execute("RETURN 1");
            Assert.fail("client execute should fail.");
        } catch (RuntimeException e) {
            assert true;
        } catch (IOErrorException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testPingClient() {
        System.out.println("<==== testPingClient ====>");
        NebulaClient client = null;
        try {
            client = NebulaClient.builder(addresses, user, passwd)
                .build();
            assert client.ping();
        } catch (IOErrorException | AuthFailedException e) {
            Assert.fail(e.getMessage());
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }

    @Test
    public void testGetHost() {
        System.out.println("<==== testGetHost ====>");
        NebulaClient client = null;
        try {
            for (int i = 0; i < 10; i++) {
                client = NebulaClient.builder(addresses, user, passwd)
                    .build();
                assert client.getHost().equals("127.0.0.1:3820")
                    || client.getHost().equals("127.0.0.1:3821")
                    || client.getHost().equals("127.0.0.1:3822");
                assert client.ping();
                client.close();
            }
        } catch (IOErrorException | AuthFailedException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testTls() {
        System.out.println("<==== testTls ====>");
        String tlsCa   = "../docker-compose-ssl/certs/ca.crt";
        String tlsCert = "../docker-compose-ssl/certs/client.crt";
        String tlsKey  = "../docker-compose-ssl/certs/client.key";

        try {
            NebulaClient client = NebulaClient
                .builder(ServerConstant.sslAddress, user, ServerConstant.passwd)
                .withEnableTls(true)
                .withTlsCa(tlsCa)
                .withTlsCert(tlsCert, tlsKey)
                .build();
            client.execute("RETURN 1");
            client.close();
        } catch (Exception e) {
            e.printStackTrace();
            assert false;
        }
    }

    @Test
    public void testServerWithTlsClient() {
        System.out.println("<==== testServerWithTlsClient ====>");
        NebulaClient client = null;
        try {
            client = NebulaClient
                .builder(ServerConstant.address, user, passwd)
                .withEnableTls(true)
                .withTlsCa("ca.cert")
                .build();
            assert false;
        } catch (Exception e) {
            assert true;
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }

    @Test
    public void testTlsServerWithNoTlsClient() {
        System.out.println("<==== testTlsServerWithNoTlsClient ====>");
        NebulaClient client = null;
        try {
            client = NebulaClient
                .builder(ServerConstant.sslAddress, user, passwd)
                .withEnableTls(false)
                .build();
            assert false;
        } catch (Exception e) {
            assert true;
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }
}

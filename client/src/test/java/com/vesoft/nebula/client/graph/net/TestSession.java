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
    public void testReconnect() {
        Runtime runtime = Runtime.getRuntime();
        NebulaPool pool = new NebulaPool();
        try {
            NebulaPoolConfig nebulaPoolConfig = new NebulaPoolConfig();
            nebulaPoolConfig.setMaxConnSize(6);
            List<HostAddress> addresses = Arrays.asList(
                    new HostAddress("127.0.0.1", 3699),
                    new HostAddress("127.0.0.1", 3700),
                    new HostAddress("127.0.0.1", 3701));
            Assert.assertTrue(pool.init(addresses, nebulaPoolConfig));
            Session session = pool.getSession("root", "nebula", true);

            // test ping
            Assert.assertTrue(session.ping());

            for (int i = 0; i < 10; i++) {
                if (i == 3) {
                    runtime.exec("docker stop nebula-docker-compose_graphd_1")
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
            Session session1 = pool.getSession("root", "nebula", false);
            Assert.assertNotNull(session1);
            Session session2 = pool.getSession("root", "nebula", false);
            Assert.assertNotNull(session2);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertFalse(e.getMessage(),false);
        } finally {
            try {
                runtime.exec("docker start nebula-docker-compose_graphd_1")
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

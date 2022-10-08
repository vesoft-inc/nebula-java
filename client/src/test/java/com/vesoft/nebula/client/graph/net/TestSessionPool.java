/* Copyright (c) 2022 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.client.graph.net;

import com.vesoft.nebula.client.graph.NebulaPoolConfig;
import com.vesoft.nebula.client.graph.SessionPool;
import com.vesoft.nebula.client.graph.SessionPoolConfig;
import com.vesoft.nebula.client.graph.data.HostAddress;
import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.exception.AuthFailedException;
import com.vesoft.nebula.client.graph.exception.BindSpaceFailedException;
import com.vesoft.nebula.client.graph.exception.ClientServerIncompatibleException;
import com.vesoft.nebula.client.graph.exception.IOErrorException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Before;
import org.junit.Test;

public class TestSessionPool {
    private final String ip = "127.0.0.1";

    @Before
    public void beforeAll() {
        NebulaPoolConfig config = new NebulaPoolConfig();
        NebulaPool pool = new NebulaPool();
        try {
            pool.init(Arrays.asList(new HostAddress(ip, 9669)), config);
            Session session = pool.getSession("root", "nebula", true);
            ResultSet resultSet = session.execute(
                    "CREATE SPACE IF NOT EXISTS space_for_session_pool(vid_type=int);"
                            + "CREATE SPACE IF NOT EXISTS session_pool_test(vid_type=int)");
            if (!resultSet.isSucceeded()) {
                System.out.println("create space failed: " + resultSet.getErrorMessage());
                assert false;
            }
            Thread.sleep(3000);
        } catch (Exception e) {
            e.printStackTrace();
            assert false;
        }
    }

    @Test()
    public void testInitFailed() {
        // NebulaPool init failed
        List<HostAddress> addresses = Arrays.asList(new HostAddress(ip, 1000));
        SessionPoolConfig config =
                new SessionPoolConfig(addresses, "space", "user", "12345");
        SessionPool sessionPool = new SessionPool(config);
        assert (!sessionPool.init());
        sessionPool.close();

        // host unknown
        addresses = Arrays.asList(new HostAddress("host", 10000));
        config = new SessionPoolConfig(addresses, "space", "user", "12345");
        sessionPool = new SessionPool(config);
        assert (!sessionPool.init());
        sessionPool.close();

        // wrong user
        addresses = Arrays.asList(new HostAddress(ip, 9669));
        config = new SessionPoolConfig(addresses, "space", "user", "nebula")
                .setMinSessionSize(1);
        sessionPool = new SessionPool(config);
        assert (!sessionPool.init());
        sessionPool.close();

        // set MinSessionSize 0, and init will return true even if the user is wrong
        config = new SessionPoolConfig(addresses, "space", "user", "nebula")
                .setMinSessionSize(0);
        sessionPool = new SessionPool(config);
        assert (sessionPool.init());
        sessionPool.close();

        // init failed for not exist space
        config = new SessionPoolConfig(addresses, "space", "root", "nebula")
                .setMinSessionSize(2);
        sessionPool = new SessionPool(config);
        assert (!sessionPool.init());
        sessionPool.close();

        // init success
        config = new SessionPoolConfig(addresses, "space_for_session_pool", "root", "nebula")
                .setMinSessionSize(2);
        sessionPool = new SessionPool(config);
        assert (sessionPool.init());
        sessionPool.close();
    }

    @Test()
    public void testExecute() {
        List<HostAddress> addresses = Arrays.asList(new HostAddress(ip, 9669));
        SessionPoolConfig config = new SessionPoolConfig(addresses, "space_for_session_pool",
                "root", "nebula");
        SessionPool sessionPool = new SessionPool(config);
        assert sessionPool.init();
        try {
            ResultSet result = sessionPool.execute("SHOW SPACES");
        } catch (IOErrorException | AuthFailedException
                | ClientServerIncompatibleException | BindSpaceFailedException e) {
            e.printStackTrace();
            assert false;
        }
        sessionPool.close();
    }

    @Test
    public void testExecuteForSpace() {
        List<HostAddress> addresses = Arrays.asList(new HostAddress(ip, 9669));
        SessionPoolConfig config = new SessionPoolConfig(addresses, "session_pool_test", "root",
                "nebula");
        SessionPool sessionPool = new SessionPool(config);
        assert sessionPool.init();

        // test USE SPACE
        try {
            sessionPool.execute("USE TEST_USE_SPACE");
        } catch (IOErrorException | AuthFailedException | ClientServerIncompatibleException
                | BindSpaceFailedException e) {
            e.printStackTrace();
            assert false;
        } catch (IllegalArgumentException e) {
            assert true;
        }

        // test DROP SPACE
        try {
            sessionPool.execute("DROP SPACE session_pool_test");
            sessionPool.execute("YIELD 1");
        } catch (IOErrorException | AuthFailedException | ClientServerIncompatibleException
                | BindSpaceFailedException e) {
            e.printStackTrace();
            assert false;
        } catch (RuntimeException e) {
            assert true;
        }
    }

    @Test
    public void testThreadSafe() {
        List<HostAddress> addresses = Arrays.asList(new HostAddress(ip, 9669));
        SessionPoolConfig config = new SessionPoolConfig(addresses, "space_for_session_pool",
                "root", "nebula");
        SessionPool sessionPool = new SessionPool(config);
        assert sessionPool.init();

        // call execute() in multi threads
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        AtomicInteger failedCount = new AtomicInteger(0);
        for (int i = 0; i < 10; i++) {
            executorService.submit(() -> {
                try {
                    ResultSet resultSet = sessionPool.execute("SHOW SPACES;");
                    if (!resultSet.isSucceeded()) {
                        System.out.println("show spaces failed, ErrorCode:"
                                + resultSet.getErrorCode() + " ErrorMessage："
                                + resultSet.getErrorMessage());
                        failedCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    failedCount.incrementAndGet();
                }
            });
        }
        try {
            executorService.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            assert false;
        }
        executorService.shutdown();
        sessionPool.close();
        assert failedCount.get() == 0;
    }

}

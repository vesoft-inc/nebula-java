/* Copyright (c) 2023 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.examples;

import com.vesoft.nebula.client.graph.NebulaPoolConfig;
import com.vesoft.nebula.client.graph.SessionPool;
import com.vesoft.nebula.client.graph.SessionPoolConfig;
import com.vesoft.nebula.client.graph.data.CASignedSSLParam;
import com.vesoft.nebula.client.graph.data.HostAddress;
import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.data.SSLParam;
import com.vesoft.nebula.client.graph.exception.AuthFailedException;
import com.vesoft.nebula.client.graph.exception.BindSpaceFailedException;
import com.vesoft.nebula.client.graph.exception.ClientServerIncompatibleException;
import com.vesoft.nebula.client.graph.exception.IOErrorException;
import com.vesoft.nebula.client.graph.net.NebulaPool;
import com.vesoft.nebula.client.graph.net.Session;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * when use http2, please call System.exit(0) to exit your process.
 */
public class GraphSessionPoolWithHttp2Example {
    private static final Logger log = LoggerFactory.getLogger(GraphClientExample.class);

    private static String host = "192.168.8.202";

    private static int port = 9119;

    private static String user = "root";

    private static String password = "nebula";

    private static String spaceName = "test";

    private static int parallel = 20;

    private static int executeTimes = 20;

    private static boolean useHttp2 = false;

    private static boolean useSsl = false;

    public static void main(String[] args) {
        SSLParam sslParam = new CASignedSSLParam(
                "examples/src/main/resources/ssl/root.crt",
                "examples/src/main/resources/ssl/server.crt",
                "examples/src/main/resources/ssl/server.key");
        prepare(sslParam);

        SessionPoolConfig sessionPoolConfig = new SessionPoolConfig(
                Arrays.asList(new HostAddress(host, port)), spaceName, user, password)
                        .setMaxSessionSize(parallel)
                        .setMinSessionSize(parallel)
                        .setRetryConnectTimes(3)
                        .setWaitTime(100)
                        .setRetryTimes(3)
                        .setIntervalTime(100)
                        .setEnableSsl(useSsl)
                        .setSslParam(sslParam)
                        .setUseHttp2(useHttp2);
        SessionPool sessionPool = new SessionPool(sessionPoolConfig);
        executeForSingleThread(sessionPool);
        executeForMultiThreads(sessionPool);

        sessionPool.close();
        System.exit(0);
    }

    /**
     * execute in single thread
     */
    private static void executeForSingleThread(SessionPool sessionPool) {
        try {
            ResultSet resultSet = sessionPool.execute("match(v:player) return v limit 1;");
            System.out.println(resultSet.toString());
        } catch (IOErrorException | ClientServerIncompatibleException | AuthFailedException
                 | BindSpaceFailedException e) {
            e.printStackTrace();
            sessionPool.close();
            System.exit(1);
        }
    }

    /**
     * execute in mutil-threads
     */
    private static void executeForMultiThreads(SessionPool sessionPool) {
        ExecutorService executorService = Executors.newFixedThreadPool(parallel);
        CountDownLatch count = new CountDownLatch(parallel);
        for (int i = 0; i < parallel; i++) {
            executorService.submit(() -> {
                try {
                    for (int j = 0; j < executeTimes; j++) {
                        ResultSet result = sessionPool.execute("match(v:player) return v limit 1;");
                        System.out.println(result.toString());
                    }
                    count.countDown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        try {
            count.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        executorService.shutdown();
    }


    private static void prepare(SSLParam sslParam) {
        NebulaPool pool = new NebulaPool();
        Session session;
        NebulaPoolConfig nebulaPoolConfig = new NebulaPoolConfig();
        nebulaPoolConfig.setUseHttp2(useHttp2);
        nebulaPoolConfig.setEnableSsl(useSsl);
        nebulaPoolConfig.setSslParam(sslParam);
        nebulaPoolConfig.setMaxConnSize(10);
        List<HostAddress> addresses = Arrays.asList(new HostAddress(host, port));
        try {
            boolean initResult = pool.init(addresses, nebulaPoolConfig);
            if (!initResult) {
                log.error("pool init failed.");
                System.exit(-1);
            }

            session = pool.getSession("root", "nebula", false);
            ResultSet res = session.execute(
                    "CREATE SPACE IF NOT EXISTS " + spaceName + "(vid_type=fixed_string(20));"
                            + "USE test;"
                            + "CREATE TAG IF NOT EXISTS player(name string, age int);");
            session.execute("insert vertex player(name,age) values \"1\":(\"Tom\",20);");
            if (!res.isSucceeded()) {
                System.out.println(res.getErrorMessage());
                System.exit(-1);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        } finally {
            pool.close();
        }
    }
}

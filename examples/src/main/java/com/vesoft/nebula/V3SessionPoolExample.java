/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula;

import com.vesoft.nebula.driver.v3client.graph.NebulaPoolConfig;
import com.vesoft.nebula.driver.v3client.graph.SessionPool;
import com.vesoft.nebula.driver.v3client.graph.SessionPoolConfig;
import com.vesoft.nebula.driver.v3client.graph.data.HostAddress;
import com.vesoft.nebula.driver.v3client.graph.data.ResultSet;
import com.vesoft.nebula.driver.v3client.graph.exception.AuthFailedException;
import com.vesoft.nebula.driver.v3client.graph.exception.BindSpaceFailedException;
import com.vesoft.nebula.driver.v3client.graph.exception.ClientServerIncompatibleException;
import com.vesoft.nebula.driver.v3client.graph.exception.IOErrorException;
import com.vesoft.nebula.driver.v3client.graph.net.NebulaPool;
import com.vesoft.nebula.driver.v3client.graph.net.Session;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mirrors the v3 client's {@code GraphSessionPoolExample}, but runs against NebulaGraph v5 through
 * the v3-compatible namespace ({@code com.vesoft.nebula.driver.v3client}).
 *
 * <p>Usage: {@code V3SessionPoolExample [address] [user] [password]}. Defaults to
 * {@code 127.0.0.1:9669 root nebula}.
 */
public class V3SessionPoolExample {
    private static final Logger log = LoggerFactory.getLogger(V3SessionPoolExample.class);

    private static final String GRAPH_NAME = "test";

    public static void main(String[] args) {
        String address = args.length > 0 ? args[0] : "127.0.0.1:9669";
        String user = args.length > 1 ? args[1] : "root";
        String password = args.length > 2 ? args[2] : "nebula";

        prepare(address, user, password);

        List<HostAddress> addresses = toAddresses(address);
        SessionPoolConfig sessionPoolConfig =
            new SessionPoolConfig(addresses, GRAPH_NAME, user, password)
                .setMaxSessionSize(10)
                .setMinSessionSize(10)
                .setRetryConnectTimes(3)
                .setWaitTime(100)
                .setRetryTimes(3)
                .setIntervalTime(100);
        SessionPool sessionPool = new SessionPool(sessionPoolConfig);
        if (!sessionPool.init()) {
            log.error("session pool init failed.");
            return;
        }

        ResultSet resultSet;
        try {
            resultSet = sessionPool.execute("MATCH (v:player) RETURN v LIMIT 1");
            System.out.println(resultSet.toString());
        } catch (IOErrorException | ClientServerIncompatibleException | AuthFailedException
                 | BindSpaceFailedException e) {
            e.printStackTrace();
            sessionPool.close();
            System.exit(1);
        }

        // execute in multiple threads
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 5; i++) {
            executorService.submit(() -> {
                try {
                    ResultSet result = sessionPool.execute("MATCH (v:player) RETURN v LIMIT 1");
                    System.out.println(result.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        executorService.shutdown();
        try {
            executorService.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        sessionPool.close();
    }

    /**
     * Create the graph type, graph and sample data using the connection-level API
     * ({@code NebulaPool + Session}), mirroring the v3 example's prepare step.
     */
    private static void prepare(String address, String user, String password) {
        NebulaPool pool = new NebulaPool();
        NebulaPoolConfig nebulaPoolConfig = new NebulaPoolConfig();
        nebulaPoolConfig.setMaxConnSize(100);
        List<HostAddress> addresses = toAddresses(address);
        try {
            if (!pool.init(addresses, nebulaPoolConfig)) {
                log.error("pool init failed.");
                return;
            }
            Session session = pool.getSession(user, password, false);

            String createGraphType = "CREATE GRAPH TYPE IF NOT EXISTS graph_type_test AS {"
                + "NODE TYPE node_type_player (LABEL player {id INT PRIMARY KEY, "
                + "name STRING, age INT})}";
            ResultSet resp = session.execute(createGraphType);
            check(resp, createGraphType);

            String createGraph = "CREATE GRAPH IF NOT EXISTS " + GRAPH_NAME + " graph_type_test";
            resp = session.execute(createGraph);
            check(resp, createGraph);

            String insertNodes = "TABLE t{id,name,age} = "
                + "(1,\"Tim\",36),(2,\"Jerry\",24),(3,\"Kyle\",30) "
                + "USE " + GRAPH_NAME + " "
                + "FOR r IN t "
                + "INSERT OR IGNORE(@node_type_player{id:r.id,name:r.name,age:r.age})";
            resp = session.execute(insertNodes);
            check(resp, insertNodes);

            session.release();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        } finally {
            pool.close();
        }
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void check(ResultSet resp, String stmt) {
        if (!resp.isSucceeded()) {
            log.error(String.format("Execute: `%s', failed: %s", stmt, resp.getErrorMessage()));
            System.exit(1);
        }
    }

    private static List<HostAddress> toAddresses(String address) {
        String[] parts = address.split(":");
        return Arrays.asList(new HostAddress(parts[0], Integer.parseInt(parts[1])));
    }
}

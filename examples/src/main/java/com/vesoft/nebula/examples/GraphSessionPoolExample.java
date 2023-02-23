/* Copyright (c) 2022 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.examples;

import com.vesoft.nebula.client.graph.NebulaPoolConfig;
import com.vesoft.nebula.client.graph.SessionPool;
import com.vesoft.nebula.client.graph.SessionPoolConfig;
import com.vesoft.nebula.client.graph.data.HostAddress;
import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.exception.AuthFailedException;
import com.vesoft.nebula.client.graph.exception.BindSpaceFailedException;
import com.vesoft.nebula.client.graph.exception.ClientServerIncompatibleException;
import com.vesoft.nebula.client.graph.exception.IOErrorException;
import com.vesoft.nebula.client.graph.net.NebulaPool;
import com.vesoft.nebula.client.graph.net.Session;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GraphSessionPoolExample {
    private static final Logger log = LoggerFactory.getLogger(GraphClientExample.class);

    public static void main(String[] args) {
        prepare();

        List<HostAddress> addresses = Arrays.asList(new HostAddress("127.0.0.1", 9669));
        String spaceName = "test";
        String user = "root";
        String password = "nebula";
        SessionPoolConfig sessionPoolConfig = new SessionPoolConfig(addresses, spaceName, user,
                password);
        SessionPool sessionPool = new SessionPool(sessionPoolConfig);
        if (!sessionPool.init()) {
            log.error("session pool init failed.");
            return;
        }
        ResultSet resultSet;
        try {
            resultSet = sessionPool.execute("match (v:player) return v limit 1;");
            System.out.println(resultSet.toString());
        } catch (IOErrorException | ClientServerIncompatibleException | AuthFailedException
                | BindSpaceFailedException e) {
            e.printStackTrace();
            sessionPool.close();
            System.exit(1);
        }

        // execute in mutil-threads
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 5; i++) {
            executorService.submit(() -> {
                try {
                    ResultSet result = sessionPool.execute("match (v:player) return v limit 1");
                    System.out.println(result.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        try {
            executorService.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        executorService.shutdown();
        sessionPool.close();
    }


    private static void prepare() {
        NebulaPool pool = new NebulaPool();
        Session session;
        NebulaPoolConfig nebulaPoolConfig = new NebulaPoolConfig();
        nebulaPoolConfig.setMaxConnSize(100);
        List<HostAddress> addresses = Arrays.asList(new HostAddress("127.0.0.1", 9669));
        try {
            boolean initResult = pool.init(addresses, nebulaPoolConfig);
            if (!initResult) {
                log.error("pool init failed.");
                return;
            }

            session = pool.getSession("root", "nebula", false);
            session.execute("CREATE SPACE IF NOT EXISTS test(vid_type=fixed_string(20));"
                    + "USE test;"
                    + "CREATE TAG IF NOT EXISTS player(name string, age int);");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        } finally {
            pool.close();
        }

    }
}

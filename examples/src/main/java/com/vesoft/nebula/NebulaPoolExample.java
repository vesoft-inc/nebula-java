/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula;

import com.vesoft.nebula.driver.graph.data.ResultSet;
import com.vesoft.nebula.driver.graph.net.NebulaClient;
import com.vesoft.nebula.driver.graph.net.NebulaPool;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NebulaPoolExample {

    private static final Logger logger = LoggerFactory.getLogger(NebulaPoolExample.class);

    public static void main(String[] args) {
        String     addresses = "127.0.0.1:9669";
        String     userName  = "root";
        String     password  = "NebulaGraph01";
        NebulaPool pool      = null;
        try {
            pool = NebulaPool
                .builder(addresses, userName, password)
                .withMaxClientSize(10)
                .withMinClientSize(1)
                .withConnectTimeoutMills(1000)
                .withRequestTimeoutMills(30000)
                .withBlockWhenExhausted(true)
                .withMaxWaitMills(Long.MAX_VALUE)
                .build();
            queryWithMultipleThreads(pool);
        } catch (Exception e) {
            logger.error("failed :", e);
            System.exit(1);
        } finally {
            if (pool != null) {
                pool.close();
            }
        }
    }


    public static void queryWithMultipleThreads(NebulaPool pool) {
        String queryNode = "USE nba MATCH (v:player) RETURN v.id, v.name, v.score, v.gender, "
            + "v.rate";
        int parallel = 200;

        CountDownLatch  countDownLatch  = new CountDownLatch(parallel);
        ExecutorService executorService = Executors.newFixedThreadPool(parallel);
        AtomicInteger   failed          = new AtomicInteger(0);
        for (int i = 0; i < parallel; i++) {
            executorService.submit(() -> {
                NebulaClient client = null;
                try {
                    client = pool.getClient();
                    ResultSet result = client.execute(
                        "USE nba MATCH ()-[e:follow]->() RETURN e.followness, e.likeness");
                    if (!result.isSucceeded()) {
                        logger.error(String.format("Execute: `%s', failed: %s",
                                                   queryNode, result.getErrorMessage()));
                        failed.incrementAndGet();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    countDownLatch.countDown();
                    if (client != null) {
                        pool.returnClient(client);
                    }

                }
            });
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("failed execute: " + failed.get());

        executorService.shutdown();
    }
}

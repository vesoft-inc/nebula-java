/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.examples.database;

import com.vesoft.nebula.client.graph.pool.NebulaConnectionPool;
import javax.annotation.Resource;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

/**
 * @author huangzhaolai-jk
 * @version 1.0.0
 * @Description TestStarter
 * @Date 2020/3/23 - 10:35
 */
public class TestStarter implements ApplicationRunner {

    /**
     * nebulaDataSource NonEmpty
     * insert datasource
     */
    @Resource(name = "nebulaConnectionPool")
    private NebulaConnectionPool nebulaConnectionPool;

    /**
     * to run ExampleApplication than auto test this method
     *
     * @param args args
     * @throws Exception Exception
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("start run！");
        for (int i = 0; i < 1000; i++) {
            new Thread(new NebulaDatabasePoolTestTh(nebulaConnectionPool, i)).start();
        }
    }
}

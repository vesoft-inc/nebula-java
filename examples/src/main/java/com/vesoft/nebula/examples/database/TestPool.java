/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */
package com.vesoft.nebula.examples.database;

import com.vesoft.nebula.client.graph.NebulaGraphConnection;
import com.vesoft.nebula.client.graph.pool.NebulaGraphConnectionPool;
import com.vesoft.nebula.client.graph.pool.NebulaGraphPoolConnectionFactory;
import com.vesoft.nebula.client.graph.pool.config.NebulaConnectionPoolConfig;
import com.vesoft.nebula.client.graph.pool.entity.LinkDomain;

import java.util.Arrays;

/**
 * @author huangzhaolai-jk
 * @version 1.0.0
 * @Description TestPool is used for
 * @Date 2020/3/24 - 15:00
 */
public class TestPool {

    public static void main(String[] args) throws Exception {
        test2();
    }

    public static void test1() throws Exception {
        LinkDomain linkDomain = new LinkDomain("192.168.180.132", 3699,
                "user", "password");
        NebulaConnectionPoolConfig poolConfig =
                new NebulaConnectionPoolConfig(Arrays.asList(linkDomain));
        NebulaGraphPoolConnectionFactory nebulaGraphPoolConnectionFactory =
                new NebulaGraphPoolConnectionFactory(poolConfig);
        NebulaGraphConnectionPool objectPool =
                new NebulaGraphConnectionPool(nebulaGraphPoolConnectionFactory);
        NebulaGraphConnection nebulaGraphConnection = objectPool.borrowObject();
        System.out.println("test：" + nebulaGraphConnection);
    }

    public static void test2() {
        LinkDomain linkDomain = new LinkDomain("192.168.180.132", 3699,
                "user", "password");
        NebulaConnectionPoolConfig poolConfig =
                new NebulaConnectionPoolConfig(Arrays.asList(linkDomain));
        NebulaGraphPoolConnectionFactory nebulaGraphPoolConnectionFactory =
                new NebulaGraphPoolConnectionFactory(poolConfig);
        NebulaGraphConnectionPool objectPool =
                new NebulaGraphConnectionPool(nebulaGraphPoolConnectionFactory);
        System.out.println("start run！");
        for (int i = 0; i < 1000; i++) {
            new Thread(new NebulaDatabasePoolTestTh(objectPool, i)).start();
        }
    }

}

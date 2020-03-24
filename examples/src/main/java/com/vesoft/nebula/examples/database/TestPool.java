package com.vesoft.nebula.examples.database;

import com.vesoft.nebula.client.graph.NebulaConnection;
import com.vesoft.nebula.client.graph.pool.NebulaConnectionPool;
import com.vesoft.nebula.client.graph.pool.NebulaPoolConnectionFactory;
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
        NebulaPoolConnectionFactory nebulaPoolConnectionFactory =
                new NebulaPoolConnectionFactory(poolConfig);
        NebulaConnectionPool objectPool = new NebulaConnectionPool(nebulaPoolConnectionFactory);
        NebulaConnection nebulaConnection = objectPool.borrowObject();
        System.out.println("test：" + nebulaConnection);
    }

    public static void test2() {
        LinkDomain linkDomain = new LinkDomain("192.168.180.132", 3699,
                "user", "password");
        NebulaConnectionPoolConfig poolConfig =
                new NebulaConnectionPoolConfig(Arrays.asList(linkDomain));
        NebulaPoolConnectionFactory nebulaPoolConnectionFactory =
                new NebulaPoolConnectionFactory(poolConfig);
        NebulaConnectionPool objectPool = new NebulaConnectionPool(nebulaPoolConnectionFactory);
        System.out.println("start run！");
        for (int i = 0; i < 1000; i++) {
            new Thread(new NebulaDatabasePoolTestTh(objectPool, i)).start();
        }
    }

}

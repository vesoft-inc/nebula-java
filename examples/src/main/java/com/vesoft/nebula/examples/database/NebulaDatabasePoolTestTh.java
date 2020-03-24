/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.examples.database;


import com.vesoft.nebula.client.graph.NebulaConnection;
import com.vesoft.nebula.client.graph.pool.NebulaConnectionPool;

/**
 * @author huangzhaolai-jk
 * @version 1.0.0
 * @Description NebulaTestTh
 * @Date 2020/3/19 - 15:51
 */
public class NebulaDatabasePoolTestTh implements Runnable {

    private NebulaConnectionPool nebulaConnectionPool;
    private int no;

    public NebulaDatabasePoolTestTh(NebulaConnectionPool nebulaConnectionPool, int no) {
        this.nebulaConnectionPool = nebulaConnectionPool;
        this.no = no;
    }

    @Override
    public void run() {
        NebulaConnection connection = null;
        try {
            connection = nebulaConnectionPool.borrowObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("connection:::"
                + connection + ",maxSize:"
                + nebulaConnectionPool.getMaxTotal());
        connection.switchSpace("test");
        int code = connection.execute("CREATE TAG test_tag(name string, credits int);");
        nebulaConnectionPool.returnObject(connection);
        System.out.println("running！" + no);
    }
}

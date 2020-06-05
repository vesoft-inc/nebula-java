/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.examples.database;


import com.vesoft.nebula.client.graph.NebulaGraphConnection;
import com.vesoft.nebula.client.graph.pool.NebulaGraphConnectionPool;

/**
 * @author huangzhaolai-jk
 * @version 1.0.0
 * @Description NebulaTestTh
 * @Date 2020/3/19 - 15:51
 */
public class NebulaDatabasePoolTestTh implements Runnable {

    private NebulaGraphConnectionPool nebulaGraphConnectionPool;
    private int no;

    public NebulaDatabasePoolTestTh(NebulaGraphConnectionPool nebulaGraphConnectionPool, int no) {
        this.nebulaGraphConnectionPool = nebulaGraphConnectionPool;
        this.no = no;
    }

    @Override
    public void run() {
        NebulaGraphConnection connection = null;
        try {
            connection = this.nebulaGraphConnectionPool.borrowObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("connection:::"
                + connection + ",maxSize:"
                + this.nebulaGraphConnectionPool.getMaxTotal());
        connection.switchSpace("test");
        int code = connection.execute("CREATE TAG test_tag(name string, credits int);");
        this.nebulaGraphConnectionPool.returnObject(connection);
        System.out.println("running！" + this.no);
    }
}

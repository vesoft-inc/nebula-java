/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.examples.database;

import com.vesoft.nebula.database.NebulaConnection;
import com.vesoft.nebula.database.NebulaDataSource;

/**
 * @author huangzhaolai-jk
 * @version 1.0.0
 * @Description NebulaTestTh
 * @Date 2020/3/19 - 15:51
 */
public class NebulaDatabasePoolTestTh implements Runnable {

    private NebulaDataSource nebulaDataSource;
    private int no;

    public NebulaDatabasePoolTestTh(NebulaDataSource nebulaDataSource, int no) {
        this.nebulaDataSource = nebulaDataSource;
        this.no = no;
    }

    @Override
    public void run() {
        NebulaConnection connection = nebulaDataSource.getConnection();
        System.out.println("connection:::"
                + connection + ",maxSize:"
                + nebulaDataSource.maxPoolSize() + ",currentSize:"
                + nebulaDataSource.currentPoolSize()
                + ",freeSize:" + nebulaDataSource.freePoolSize());
        connection.switchSpace("test");
        int code = connection.execute("CREATE TAG test_tag(name string, credits int);");
        nebulaDataSource.release(connection);
        System.out.println("running！" + no);
    }
}

package com.vesoft.nebula.examples.database;

import com.vesoft.nebula.database.NebulaConnection;
import com.vesoft.nebula.database.NebulaDataSource;

/**
 * @author huangzhaolai-jk
 * @version 1.0.0
 * @Description NebulaTestTh is used for
 * @Date 2020/3/19 - 15:51
 */
public class NebulaDatabasePoolTestTh implements Runnable {

    private NebulaDataSource nebulaDataSource;
    private int i;

    public NebulaDatabasePoolTestTh(NebulaDataSource nebulaDataSource, int i){
        this.nebulaDataSource = nebulaDataSource;
        this.i = i;
    }

    @Override
    public void run() {
        NebulaConnection connection = nebulaDataSource.getConnection();
        System.out.println("connection:::" + connection + ",maxsize:" + nebulaDataSource.maxPoolSize() + ",currentsize:" + nebulaDataSource.currentPoolSize()
         + ",freesize:" + nebulaDataSource.freePoolSize());
        connection.switchSpace("test");
        int code = connection.execute("CREATE TAG test_tag(name string, credits int);");
        nebulaDataSource.release(connection);
        System.out.println("启动完成！" + i);
    }
}

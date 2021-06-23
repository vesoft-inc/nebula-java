/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.meta;

import com.vesoft.nebula.client.graph.NebulaPoolConfig;
import com.vesoft.nebula.client.graph.data.HostAddress;
import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.exception.AuthFailedException;
import com.vesoft.nebula.client.graph.exception.IOErrorException;
import com.vesoft.nebula.client.graph.exception.NotValidConnectionException;
import com.vesoft.nebula.client.graph.net.NebulaPool;
import com.vesoft.nebula.client.graph.net.Session;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

/**
 * two spaces: test1, test2, both have 2 parts
 * each space has one tag and one edge
 */
public class MockNebulaGraph {
    public static void initGraph() {

        NebulaPoolConfig nebulaPoolConfig = new NebulaPoolConfig();
        nebulaPoolConfig.setMaxConnSize(100);
        List<HostAddress> addresses = Arrays.asList(new HostAddress("127.0.0.1", 9669),
                new HostAddress("127.0.0.1", 9670));
        NebulaPool pool = new NebulaPool();
        Session session = null;
        try {
            pool.init(addresses, nebulaPoolConfig);
            session = pool.getSession("root", "nebula", true);

            ResultSet resp = session.execute(createSpace());
            if (!resp.isSucceeded()) {
                System.exit(1);
            }
        } catch (UnknownHostException | NotValidConnectionException
                | IOErrorException | AuthFailedException e) {
            e.printStackTrace();
        } finally {
            pool.close();
        }
    }

    public static String createSpace() {
        String exec = "CREATE SPACE IF NOT EXISTS testMeta(partition_num=10, "
                + "vid_type=fixed_string(10));"
                + "USE testMeta;"
                + "CREATE TAG IF NOT EXISTS person(name string, age int);"
                + "CREATE EDGE IF NOT EXISTS friend(likeness double);";
        return exec;
    }

    public static void createMultiVersionTagAndEdge() {
        NebulaPoolConfig nebulaPoolConfig = new NebulaPoolConfig();
        nebulaPoolConfig.setMaxConnSize(100);
        List<HostAddress> addresses = Arrays.asList(new HostAddress("127.0.0.1", 9669),
                new HostAddress("127.0.0.1", 9670));
        NebulaPool pool = new NebulaPool();
        Session session = null;
        try {
            pool.init(addresses, nebulaPoolConfig);
            session = pool.getSession("root", "nebula", true);

            String exec = "CREATE SPACE IF NOT EXISTS testMeta(partition_num=10, "
                    + "vid_type=fixed_string(10));"
                    + "USE testMeta;"
                    + "CREATE TAG IF NOT EXISTS player();"
                    + "CREATE EDGE IF NOT EXISTS couples()";
            ResultSet resp = session.execute(exec);
            if (!resp.isSucceeded()) {
                System.exit(1);
            }
            Thread.sleep(10000);
            String updateSchema = "USE testMeta;"
                    + "ALTER TAG player ADD(col1 string);"
                    + "ALTER EDGE couples ADD(col1 string)";
            ResultSet updateResp = session.execute(updateSchema);
            if (!updateResp.isSucceeded()) {
                if (!"Existed!".equals(updateResp.getErrorMessage())) {
                    System.exit(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close();
        }
    }
}

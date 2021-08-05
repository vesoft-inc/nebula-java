/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.storage;

import com.vesoft.nebula.client.graph.NebulaPoolConfig;
import com.vesoft.nebula.client.graph.data.HostAddress;
import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.exception.AuthFailedException;
import com.vesoft.nebula.client.graph.exception.IOErrorException;
import com.vesoft.nebula.client.graph.exception.NotValidConnectionException;
import com.vesoft.nebula.client.graph.net.NebulaPool;
import com.vesoft.nebula.client.graph.net.Session;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MockStorageData {
    private static final Logger LOGGER = LoggerFactory.getLogger(MockStorageData.class);

    public static void initGraph() {

        NebulaPoolConfig nebulaPoolConfig = new NebulaPoolConfig();
        nebulaPoolConfig.setMaxConnSize(100);
        List<HostAddress> addresses = Arrays.asList(new HostAddress("127.0.0.1", 9671));
        NebulaPool pool = new NebulaPool();
        Session session = null;
        try {
            pool.init(addresses, nebulaPoolConfig);
            session = pool.getSession("root", "nebula", true);

            ResultSet resp = session.execute(createSpace());

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            ResultSet insertVertexResult = session.execute(insertData());
            if (!resp.isSucceeded() || !insertVertexResult.isSucceeded()) {
                LOGGER.error(resp.getErrorMessage());
                LOGGER.error(insertVertexResult.getErrorMessage());
                assert (false);
            }
        } catch (UnknownHostException | NotValidConnectionException
                | IOErrorException | AuthFailedException e) {
            e.printStackTrace();
        }
    }

    public static String createSpace() {
        String exec = "CREATE SPACE IF NOT EXISTS testStorage(partition_num=10);"
                + "USE testStorage;"
                + "CREATE TAG IF NOT EXISTS person(name string, age int);"
                + "CREATE EDGE IF NOT EXISTS friend(likeness double);";
        return exec;
    }

    public static String insertData() {
        String exec = "INSERT VERTEX person(name, age) VALUES "
                + "\"1\":(\"Tom\", 18), "
                + "\"2\":(\"Jina\", 20), "
                + "\"3\":(\"Bob\", 23), "
                + "\"4\":(\"Tim\", 15), "
                + "\"5\":(\"Viki\", 25);"
                + "INSERT EDGE friend(likeness) VALUES "
                + "\"1\" -> \"2\":(1.0), "
                + "\"2\" -> \"3\":(2.1), "
                + "\"3\" -> \"4\":(3.2), "
                + "\"4\" -> \"2\":(4.5), "
                + "\"5\" -> \"1\":(5.9);";
        return exec;
    }
}

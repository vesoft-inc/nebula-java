/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.client.storage;

import com.vesoft.nebula.client.graph.NebulaPoolConfig;
import com.vesoft.nebula.client.graph.data.CASignedSSLParam;
import com.vesoft.nebula.client.graph.data.HostAddress;
import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.data.SelfSignedSSLParam;
import com.vesoft.nebula.client.graph.exception.AuthFailedException;
import com.vesoft.nebula.client.graph.exception.ClientServerIncompatibleException;
import com.vesoft.nebula.client.graph.exception.IOErrorException;
import com.vesoft.nebula.client.graph.exception.NotValidConnectionException;
import com.vesoft.nebula.client.graph.net.NebulaPool;
import com.vesoft.nebula.client.graph.net.Session;
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
        List<HostAddress> addresses = Arrays.asList(new HostAddress("127.0.0.1", 9669));
        NebulaPool pool = new NebulaPool();
        Session session = null;
        try {
            pool.init(addresses, nebulaPoolConfig);
            session = pool.getSession("root", "nebula", true);

            ResultSet resp = session.execute(createSpace());

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            ResultSet insertVertexResult = session.execute(insertData());
            if (!resp.isSucceeded() || !insertVertexResult.isSucceeded()) {
                LOGGER.error(resp.getErrorMessage());
                LOGGER.error(insertVertexResult.getErrorMessage());
                assert (false);
            }
        } catch (UnknownHostException
                | NotValidConnectionException
                | IOErrorException
                | AuthFailedException
                | ClientServerIncompatibleException e) {
            e.printStackTrace();
        } finally {
            pool.close();
        }
    }

    public static String createSpace() {
        String exec =
                "CREATE SPACE IF NOT EXISTS testStorage(partition_num=10,"
                        + "vid_type=fixed_string(8));"
                        + "USE testStorage;"
                        + "CREATE TAG IF NOT EXISTS person(name string, age int);"
                        + "CREATE EDGE IF NOT EXISTS friend(likeness double);";
        return exec;
    }

    public static String createSpaceCa() {
        String exec =
                "CREATE SPACE IF NOT EXISTS testStorageCA(partition_num=10,"
                        + "vid_type=fixed_string(8));"
                        + "USE testStorageCA;"
                        + "CREATE TAG IF NOT EXISTS person(name string, age int);"
                        + "CREATE EDGE IF NOT EXISTS friend(likeness double);";
        return exec;
    }

    public static String createSpaceSelf() {
        String exec =
                "CREATE SPACE IF NOT EXISTS testStorageSelf(partition_num=10,"
                        + "vid_type=fixed_string(8));"
                        + "USE testStorageSelf;"
                        + "CREATE TAG IF NOT EXISTS person(name string, age int);"
                        + "CREATE EDGE IF NOT EXISTS friend(likeness double);";
        return exec;
    }

    public static String insertData() {
        String exec =
                "INSERT VERTEX person(name, age) VALUES "
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

    // mock data for CA ssl nebula service
    public static void mockCASslData() {
        NebulaPoolConfig nebulaPoolConfig = new NebulaPoolConfig();
        nebulaPoolConfig.setMaxConnSize(100);
        nebulaPoolConfig.setEnableSsl(true);
        nebulaPoolConfig.setSslParam(
                new CASignedSSLParam(
                        "src/test/resources/ssl/casigned.pem",
                        "src/test/resources/ssl/casigned.crt",
                        "src/test/resources/ssl/casigned.key"));
        List<HostAddress> addresses = Arrays.asList(new HostAddress("127.0.0.1", 8669));
        NebulaPool pool = new NebulaPool();
        Session session = null;
        try {
            pool.init(addresses, nebulaPoolConfig);
            session = pool.getSession("root", "nebula", true);

            ResultSet resp = session.execute(createSpaceCa());
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            ResultSet insertVertexResult = session.execute(insertData());
            if (!resp.isSucceeded() || !insertVertexResult.isSucceeded()) {
                LOGGER.error("create space failed, {}", resp.getErrorMessage());
                LOGGER.error("insert vertex data failed, {}", insertVertexResult.getErrorMessage());
                assert (false);
            }
        } catch (UnknownHostException
                | NotValidConnectionException
                | IOErrorException
                | AuthFailedException
                | ClientServerIncompatibleException e) {
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.release();
            }
            pool.close();
        }
    }

    // mock data for Self ssl nebula service
    public static void mockSelfSslData() {
        NebulaPoolConfig nebulaPoolConfig = new NebulaPoolConfig();
        nebulaPoolConfig.setMaxConnSize(100);
        nebulaPoolConfig.setEnableSsl(true);
        nebulaPoolConfig.setSslParam(
                new SelfSignedSSLParam(
                        "src/test/resources/ssl/selfsigned.pem",
                        "src/test/resources/ssl/selfsigned.key",
                        "vesoft"));
        List<HostAddress> addresses = Arrays.asList(new HostAddress("127.0.0.1", 8669));
        NebulaPool pool = new NebulaPool();
        Session session = null;
        try {
            pool.init(addresses, nebulaPoolConfig);
            session = pool.getSession("root", "nebula", true);

            ResultSet resp = session.execute(createSpaceSelf());

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            ResultSet insertVertexResult = session.execute(insertData());
            if (!resp.isSucceeded() || !insertVertexResult.isSucceeded()) {
                LOGGER.error(resp.getErrorMessage());
                LOGGER.error(insertVertexResult.getErrorMessage());
                assert (false);
            }
        } catch (UnknownHostException
                | NotValidConnectionException
                | IOErrorException
                | AuthFailedException
                | ClientServerIncompatibleException e) {
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.release();
            }
            pool.close();
        }
    }
}

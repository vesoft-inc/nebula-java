/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.graph.sync;

import com.google.common.net.HostAndPort;
import com.vesoft.nebula.client.graph.Config;
import com.vesoft.nebula.client.graph.ConnectionPool;
import com.vesoft.nebula.client.graph.IOErrorException;
import com.vesoft.nebula.client.graph.ResultSet;
import com.vesoft.nebula.client.graph.Session;
import com.vesoft.nebula.graph.ColumnValue;
import com.vesoft.nebula.graph.ErrorCode;
import com.vesoft.nebula.graph.RowValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestSession {
    private ConnectionPool pool = new ConnectionPool();
    private final Logger log = LoggerFactory.getLogger(TestSession.class);
    private Session session  = null;

    @Test(timeout = 20000)
    public void testResultSet() {
        try {
            List<HostAndPort> addresses = Arrays.asList(HostAndPort.fromParts("127.0.0.1", 3699));
            Config config = new Config();
            config.maxConnectionPoolSize = 1;
            pool.init(addresses, "root", "nebula", config);
            session = pool.getSession(true);
            {
                ResultSet resp = session.execute("CREATE SPACE IF NOT EXISTS test");
                assert (resp.getErrorCode() == ErrorCode.SUCCEEDED);
            }
            {
                ResultSet resp = session.execute("USE test");
                assert (resp.getErrorCode() == ErrorCode.SUCCEEDED);
            }
            {
                String createSchema = "CREATE TAG IF NOT EXISTS person(name string, age int);"
                        + "CREATE EDGE IF NOT EXISTS classmate(likeness int)";
                ResultSet resp = session.execute(createSchema);
                assert (resp.getErrorCode() == ErrorCode.SUCCEEDED);
                TimeUnit.SECONDS.sleep(6);
            }

            // check result
            {
                ResultSet resp = session.execute("INSERT VERTEX person(name, age) "
                        + "VALUES 1:(\"Tom\", 18), 2:(\"Ann\", 19);");
                assert (resp.getErrorCode() == ErrorCode.SUCCEEDED);
            }
            {
                ResultSet resp = session.execute(
                        "INSERT EDGE classmate(likeness) VALUES 1->2:(95);");
                assert (resp.getErrorCode() == ErrorCode.SUCCEEDED);
            }
            {
                List<String> expectedColumnNames = new ArrayList<String>();
                expectedColumnNames.add("classmate._src");
                expectedColumnNames.add("classmate._dst");
                expectedColumnNames.add("classmate._rank");
                expectedColumnNames.add("classmate.likeness");

                List<ColumnValue> columns = new ArrayList<ColumnValue>();
                columns.add(ColumnValue.id(1));
                columns.add(ColumnValue.id(2));
                columns.add(ColumnValue.integer(0));
                columns.add(ColumnValue.integer(95));
                List<RowValue> expectedValues = new ArrayList<RowValue>();
                expectedValues.add(new RowValue(columns));

                ResultSet resp = session.execute("FETCH PROP ON classmate 1->2;");
                assert (resp.getErrorCode() == ErrorCode.SUCCEEDED);
                assert (resp.getColumns().equals(expectedColumnNames));
                assert (resp.getRows().equals(expectedValues));
            }
            session.release();
        } catch (Exception e) {
            e.printStackTrace();
            assert (false);
        } finally {
            if (pool != null) {
                pool.close();
            }
        }
    }

    @Test(timeout = 100000)
    public void testReconnect() {
        try {
            List<HostAndPort> addresses = Arrays.asList(HostAndPort.fromParts("127.0.0.1", 3699),
                    HostAndPort.fromParts("127.0.0.1", 3700));
            Config config = new Config();
            config.maxConnectionPoolSize = 2;
            pool.init(addresses, "root", "nebula", config);
            session = pool.getSession(false);
            for (int i = 0; i < 30; i++) {
                log.info("Execute: SHOW SPACES");
                try {
                    ResultSet resp = session.execute("SHOW SPACES");
                    if (resp.getErrorCode() != ErrorCode.SUCCEEDED) {
                        log.error(String.format("resp.getErrorCode() is failed: %d",
                                resp.getErrorCode()));
                    }
                } catch (IOErrorException ie) {
                    if (ie.getType() == IOErrorException.E_CONNECT_BROKEN) {
                        session = pool.getSession(false);
                        session.execute("USE test");
                    }
                }
                TimeUnit.SECONDS.sleep(2);
            }
            session.release();
        } catch (Exception e) {
            e.printStackTrace();
            assert (false);
        } finally {
            if (pool != null) {
                pool.close();
            }
        }
    }
}

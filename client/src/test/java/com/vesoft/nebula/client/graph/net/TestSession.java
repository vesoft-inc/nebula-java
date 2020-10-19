/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.graph.sync;

import com.vesoft.nebula.client.graph.NebulaPoolConfig;
import com.vesoft.nebula.client.graph.data.HostAddress;
import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.exception.IOErrorException;
import com.vesoft.nebula.client.graph.net.NebulaPool;
import com.vesoft.nebula.client.graph.net.Session;
import com.vesoft.nebula.graph.ColumnValue;
import com.vesoft.nebula.graph.RowValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestSession {
    private final Logger log = LoggerFactory.getLogger(TestSession.class);

    @Test(timeout = 20000)
    public void testResultSet() {
        NebulaPool pool = new NebulaPool();
        try {
            List<HostAddress> addresses = Arrays.asList(new HostAddress("127.0.0.1", 3699));
            NebulaPoolConfig nebulaPoolConfig = new NebulaPoolConfig();
            nebulaPoolConfig.setMinConnSize(0);
            nebulaPoolConfig.setMaxConnSize(1);
            assert pool.init(addresses, nebulaPoolConfig);
            Session session = pool.getSession("root", "nebula", true);
            {
                ResultSet resp = session.execute("CREATE SPACE IF NOT EXISTS test");
                assert (resp.isSucceeded());
            }
            {
                ResultSet resp = session.execute("USE test");
                assert (resp.isSucceeded());
            }
            {
                String createSchema = "CREATE TAG IF NOT EXISTS person(name string, age int);"
                        + "CREATE EDGE IF NOT EXISTS classmate(likeness int)";
                ResultSet resp = session.execute(createSchema);
                assert (resp.isSucceeded());
                TimeUnit.SECONDS.sleep(6);
            }

            // check result
            {
                ResultSet resp = session.execute("INSERT VERTEX person(name, age) "
                        + "VALUES 1:(\"Tom\", 18), 2:(\"Ann\", 19);");
                assert (resp.isSucceeded());
            }
            {
                ResultSet resp = session.execute(
                        "INSERT EDGE classmate(likeness) VALUES 1->2:(95);");
                assert (resp.isSucceeded());
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
                assert (resp.isSucceeded());
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

    @Test()
    public void testReconnect() {
        NebulaPool pool = new NebulaPool();
        try {
            NebulaPoolConfig nebulaPoolConfig = new NebulaPoolConfig();
            nebulaPoolConfig.setMinConnSize(2);
            nebulaPoolConfig.setMaxConnSize(2);
            nebulaPoolConfig.setIdleTime(2);
            List<HostAddress> addresses = Arrays.asList(new HostAddress("127.0.0.1", 3699),
                    new HostAddress("127.0.0.1", 3700));
            assert pool.init(addresses, nebulaPoolConfig);
            Session session = pool.getSession("root", "nebula", false);
            System.out.println("==================================");
            // TODO: Add a task to stop the graphd("127.0.0.1:3700") after 10 second

            // TODO: Add a task to start the graphd("127.0.0.1:3700") after 20 second
            for (int i = 0; i < 20; i++) {
                try {
                    ResultSet resp = session.execute("SHOW SPACES");
                    if (!resp.isSucceeded()) {
                        log.error(String.format("Execute `SHOW SPACES' failed: %s",
                                resp.getErrorMessage()));
                    }
                } catch (IOErrorException ie) {
                    if (ie.getType() == IOErrorException.E_CONNECT_BROKEN) {
                        session = pool.getSession("root", "nebula", false);
                        session.execute("USE test");
                    }
                }
                TimeUnit.SECONDS.sleep(2);
            }
            session.release();
            Session session1 = pool.getSession("root", "nebula", false);
            assert (session1 != null);
            Session session2 = pool.getSession("root", "nebula", false);
            assert (session2 != null);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertFalse(e.getMessage(),false);
        } finally {
            if (pool != null) {
                pool.close();
            }
        }
    }
}
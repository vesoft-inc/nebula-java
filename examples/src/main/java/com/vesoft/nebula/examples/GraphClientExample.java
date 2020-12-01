/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.examples;

import com.vesoft.nebula.DataSet;
import com.vesoft.nebula.Date;
import com.vesoft.nebula.DateTime;
import com.vesoft.nebula.Edge;
import com.vesoft.nebula.Map;
import com.vesoft.nebula.Path;
import com.vesoft.nebula.Set;
import com.vesoft.nebula.Time;
import com.vesoft.nebula.Value;
import com.vesoft.nebula.Vertex;
import com.vesoft.nebula.client.graph.NebulaPoolConfig;
import com.vesoft.nebula.client.graph.data.HostAddress;
import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.net.NebulaPool;
import com.vesoft.nebula.client.graph.net.Session;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GraphClientExample {
    private static final Logger log = LoggerFactory.getLogger(GraphClientExample.class);

    private static void printResult(ResultSet resultSet) {
        List<String> colNames = resultSet.getColumnNames();
        for (String name : colNames) {
            System.out.printf("%15s |", name);
        }
        System.out.println();
        for (ResultSet.Record record : resultSet.getRecords()) {
            for (Value rec : record) {
                Object value = rec.getFieldValue();
                if (value instanceof Integer) {
                    System.out.printf("%15s |", value);
                }
                if (value instanceof Boolean) {
                    System.out.printf("%15s |", value);
                }
                if (value instanceof Long) {
                    System.out.printf("%15s |", value);
                }
                if (value instanceof Double) {
                    System.out.printf("%15s |", value);
                }
                if (value instanceof byte[]) {
                    System.out.printf("%15s |", new String((byte[])value));
                }
                if (value instanceof Date) {
                    System.out.printf("%15s |", value);
                }
                if (value instanceof Time) {
                    System.out.printf("%15s |", value);
                }
                if (value instanceof DateTime) {
                    System.out.printf("%15s |", value);
                }
                if (value instanceof Vertex) {
                    System.out.printf("%15s |", value);
                }
                if (value instanceof Edge) {
                    System.out.printf("%15s |", value);
                }
                if (value instanceof Path) {
                    System.out.printf("%15s |", value);
                }
                if (value instanceof List) {
                    System.out.printf("%15s |", value);
                }
                if (value instanceof Map) {
                    System.out.printf("%15s |", value);
                }
                if (value instanceof Set) {
                    System.out.printf("%15s |", value);
                }
                if (value instanceof DataSet) {
                    System.out.printf("%15s |", value);
                }
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        NebulaPool pool = new NebulaPool();
        Session session = null;
        try {
            NebulaPoolConfig nebulaPoolConfig = new NebulaPoolConfig();
            nebulaPoolConfig.setMaxConnSize(100);
            List<HostAddress> addresses = Arrays.asList(new HostAddress("127.0.0.1", 3699),
                    new HostAddress("127.0.0.1", 3700));
            pool.init(addresses, nebulaPoolConfig);
            session = pool.getSession("root", "nebula", false);
            {
                String createSchema = "CREATE SPACE IF NOT EXISTS test; "
                                      + "USE test;"
                                      + "CREATE TAG IF NOT EXISTS person(name string, age int);"
                                      + "CREATE EDGE IF NOT EXISTS like(likeness double)";
                ResultSet resp = session.execute(createSchema);
                if (!resp.isSucceeded()) {
                    log.error(String.format("Execute: `%s', failed: %s",
                        createSchema, resp.getErrorMessage()));
                    System.exit(1);
                }
            }

            TimeUnit.SECONDS.sleep(5);
            {
                String insertVertexes = "INSERT VERTEX person(name, age) VALUES "
                    + "'Bob':('Bob', 10), "
                    + "'Lily':('Lily', 9), "
                    + "'Tom':('Tom', 10), "
                    + "'Jerry':('Jerry', 13), "
                    + "'John':('John', 11);";
                ResultSet resp = session.execute(insertVertexes);
                if (!resp.isSucceeded()) {
                    log.error(String.format("Execute: `%s', failed: %s",
                        insertVertexes, resp.getErrorMessage()));
                    System.exit(1);
                }
            }

            {
                String insertEdges = "INSERT EDGE like(likeness) VALUES "
                    + "'Bob'->'Lily':(80.0), "
                    + "'Bob'->'Tom':(70.0), "
                    + "'Lily'->'Jerry':(84.0), "
                    + "'Tom'->'Jerry':(68.3), "
                    + "'Bob'->'John':(97.2);";
                ResultSet resp = session.execute(insertEdges);
                if (!resp.isSucceeded()) {
                    log.error(String.format("Execute: `%s', failed: %s",
                        insertEdges, resp.getErrorMessage()));
                    System.exit(1);
                }
            }

            {
                String query = "GO FROM \"Bob\" OVER like "
                    + "YIELD $^.person.name, $^.person.age, like.likeness";
                ResultSet resp = session.execute(query);
                if (!resp.isSucceeded()) {
                    log.error(String.format("Execute: `%s', failed: %s",
                        query, resp.getErrorMessage()));
                    System.exit(1);
                }
                printResult(resp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            assert session != null;
            session.release();
            pool.close();
        }
    }
}

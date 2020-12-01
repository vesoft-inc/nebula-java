/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.examples;

import com.vesoft.nebula.client.graph.NebulaPoolConfig;
import com.vesoft.nebula.client.graph.data.HostAddress;
import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.data.ValueWrapper;
import com.vesoft.nebula.client.graph.net.NebulaPool;
import com.vesoft.nebula.client.graph.net.Session;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GraphClientExample {
    private static final Logger log = LoggerFactory.getLogger(GraphClientExample.class);

    private static void printResult(ResultSet resultSet) throws UnsupportedEncodingException {
        List<String> colNames = resultSet.keys();
        for (String name : colNames) {
            System.out.printf("%15s |", name);
        }
        System.out.println();
        for (int i = 0; i < resultSet.rowsSize(); i++) {
            ResultSet.Record record = resultSet.rowValues(i);
            for (ValueWrapper value : record.values()) {
                if (value.isLong()) {
                    System.out.printf("%15s |", value.asLong());
                }
                if (value.isBoolean()) {
                    System.out.printf("%15s |", value.asBoolean());
                }
                if (value.isDouble()) {
                    System.out.printf("%15s |", value.asDouble());
                }
                if (value.isString()) {
                    System.out.printf("%15s |", value.asString());
                }
                if (value.isTime()) {
                    System.out.printf("%15s |", value.asTime());
                }
                if (value.isDate()) {
                    System.out.printf("%15s |", value.asDate());
                }
                if (value.isDateTime()) {
                    System.out.printf("%15s |", value.asDateTime());
                }
                if (value.isVertex()) {
                    System.out.printf("%15s |", value.asNode());
                }
                if (value.isEdge()) {
                    System.out.printf("%15s |", value.asRelationship());
                }
                if (value.isPath()) {
                    System.out.printf("%15s |", value.asPath());
                }
                if (value.isList()) {
                    System.out.printf("%15s |", value.asList());
                }
                if (value.isSet()) {
                    System.out.printf("%15s |", value.asSet());
                }
                if (value.isMap()) {
                    System.out.printf("%15s |", value.asMap());
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

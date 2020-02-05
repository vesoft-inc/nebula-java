/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.examples;

import com.facebook.thrift.TException;
import com.google.common.base.Joiner;
import com.vesoft.nebula.client.graph.ConnectionException;
import com.vesoft.nebula.client.graph.GraphClient;
import com.vesoft.nebula.client.graph.GraphClientImpl;
import com.vesoft.nebula.client.graph.NGQLException;
import com.vesoft.nebula.client.graph.ResultSet;
import com.vesoft.nebula.graph.ErrorCode;
import com.vesoft.nebula.graph.RowValue;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GraphClientExample {
    private static final Logger LOGGER = LoggerFactory.getLogger(GraphClientExample.class);

    private static final String SPACE_NAME = "test";

    private static final String[] createTags = {
        "CREATE TAG IF NOT EXISTS course(name string, credits int);",
        "CREATE TAG IF NOT EXISTS building(name string);",
        "CREATE TAG IF NOT EXISTS student(name string, age int, gender string);",
    };

    private static final String[] createEdges = {
        "CREATE EDGE IF NOT EXISTS like(likeness double);",
        "CREATE EDGE IF NOT EXISTS select(grade int);"
    };

    private static final String[] insertVertices = {
        "INSERT VERTEX student(name, age, gender) VALUES 200:(\"Monica\", 16, \"female\");",
        "INSERT VERTEX student(name, age, gender) VALUES 201:(\"Mike\", 18, \"male\");",
        "INSERT VERTEX student(name, age, gender) VALUES 202:(\"Jane\", 17, \"female\");",
        "INSERT VERTEX course(name, credits),building(name) VALUES 101:(\"Math\", 3, \"No5\");",
        "INSERT VERTEX course(name, credits),building(name) VALUES 102:(\"English\", 6, \"No11\");"
    };

    private static final String[] insertEdges = {
        "INSERT EDGE select(grade) VALUES 200 -> 101:(5);",
        "INSERT EDGE select(grade) VALUES 200 -> 102:(3);",
        "INSERT EDGE select(grade) VALUES 201 -> 102:(3);",
        "INSERT EDGE select(grade) VALUES 202 -> 102:(3);",
        "INSERT EDGE like(likeness) VALUES 200 -> 201:(92.5);",
        "INSERT EDGE like(likeness) VALUES 201 -> 200:(85.6);",
        "INSERT EDGE like(likeness) VALUES 201 -> 202:(93.2);"
    };

    private static final String simpleQuery = "GO FROM 201 OVER like;";

    private static final String complexQuery = "GO FROM 201 OVER like "
        + "WHERE $$.student.age >= 17 YIELD $$.student.name AS Friend, "
        + "$$.student.age AS Age, $$.student.gender AS Gender;";

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: "
                    + "com.vesoft.nebula.examples.GraphClientExample <host> <graph service port>");
            return;
        }

        try (GraphClient client = new GraphClientImpl(args[0], Integer.valueOf(args[1]))) {
            client.setUser("user");
            client.setPassword("password");

            client.connect();
            int code = client.switchSpace(SPACE_NAME);
            if (ErrorCode.SUCCEEDED != code) {
                LOGGER.error(String.format("Switch Space %s Failed", SPACE_NAME));
                LOGGER.error(String.format("Please confirm %s have been created", SPACE_NAME));
                System.exit(-1);
            }

            for (String statement : createTags) {
                code = client.execute(statement);
                LOGGER.info(statement);
                if (ErrorCode.SUCCEEDED != code) {
                    LOGGER.error(String.format("Create Tag Failed: %s", statement));
                    System.exit(-1);
                }
            }

            for (String statement : createEdges) {
                LOGGER.info(statement);
                code = client.execute(statement);
                if (ErrorCode.SUCCEEDED != code) {
                    LOGGER.error(String.format("Create Edge Failed: %s", statement));
                    System.exit(-1);
                }
            }

            for (String statement : insertVertices) {
                LOGGER.info(statement);
                code = client.execute(statement);
                if (ErrorCode.SUCCEEDED != code) {
                    LOGGER.error(String.format("Insert Vertices Failed: %s", statement));
                    System.exit(-1);
                }
            }

            for (String statement : insertEdges) {
                LOGGER.info(statement);
                code = client.execute(statement);
                if (ErrorCode.SUCCEEDED != code) {
                    LOGGER.error(String.format("Insert Edges Failed: %s", statement));
                    System.exit(-1);
                }
            }

            ResultSet resultSet = null;
            try {
                LOGGER.info(simpleQuery);
                resultSet = client.executeQuery(simpleQuery);
            } catch (ConnectionException e) {
                LOGGER.error("Query Failed: ", e.getMessage());
            } catch (TException e) {
                e.printStackTrace();
            } catch (NGQLException e) {
                e.printStackTrace();
            }

            LOGGER.info(String.format("Columns: %s", Joiner.on(" ").join(resultSet.getColumns())));

            for (RowValue value : resultSet.getRows()) {
                LOGGER.info("ID: {}", value.columns.get(0).getId());
            }

            try {
                LOGGER.info(complexQuery);
                resultSet = client.executeQuery(complexQuery);
            } catch (NGQLException e) {
                e.printStackTrace();
            } catch (TException e) {
                e.printStackTrace();
            } catch (ConnectionException e) {
                e.printStackTrace();
            }

            LOGGER.info(String.format("Columns: %s", Joiner.on(" ").join(resultSet.getColumns())));

            for (RowValue value : resultSet.getRows()) {
                LOGGER.info(String.format("%s, %d, %s", new String(value.columns.get(0).getStr()),
                                                        value.columns.get(1).getInteger(),
                                                        new String(value.columns.get(2).getStr())));
            }

            // Batch insert some edges for ScanEdgeInSpaceExample and SparkExample
            for (long src = 1; src <= 10; src++) {
                StringBuilder stringBuilder = new StringBuilder();
                long dst = 1000 - src;
                // batch insert edges, we generate 10 edges for each src vertex
                for (int count = 0; count < 10; count++) {
                    Random random = new Random();
                    int value = random.nextInt(100);
                    stringBuilder.append(String.format(
                            "INSERT EDGE select(grade) VALUES %d -> %d:(%d);", src, dst, value));
                }

                String batchStatement = stringBuilder.toString();
                code = client.execute(batchStatement);
                if (ErrorCode.SUCCEEDED != code) {
                    LOGGER.error(String.format("Batch insert edges failed: %s", batchStatement));
                    System.exit(-1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

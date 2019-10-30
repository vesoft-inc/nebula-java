/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.example;

import com.facebook.thrift.TException;
import com.google.common.base.Joiner;
import com.vesoft.nebula.client.ConnectionException;
import com.vesoft.nebula.client.GraphClient;
import com.vesoft.nebula.client.GraphClientImpl;
import com.vesoft.nebula.client.NGQLException;
import com.vesoft.nebula.client.ResultSet;
import com.vesoft.nebula.graph.ErrorCode;
import com.vesoft.nebula.graph.RowValue;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GraphClientExample {
    private static final Logger LOGGER = LoggerFactory.getLogger(GraphClientImpl.class.getName());

    private static final String SPACE_NAME = "test";

    private static final String[] createTags = {
        "CREATE TAG course(name string, credits int);",
        "CREATE TAG building(name string);",
        "CREATE TAG student(name string, age int, gender string);",
    };

    private static final String[] createEdges = {
        "CREATE EDGE like(likeness double);",
        "CREATE EDGE select(grade int);"
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
        try {
            try (GraphClient client = new GraphClientImpl("127.0.0.1", 3699)) {
                client.connect("user", "password");
                int code = client.execute(SPACE_NAME);
                if (code != ErrorCode.SUCCEEDED) {
                    LOGGER.error(String.format("Switch Space %s Failed", SPACE_NAME));
                    LOGGER.error(String.format("Please confirm %s have been created", SPACE_NAME));
                    System.exit(-1);
                }

                for (String statement : createTags) {
                    code = client.execute(statement);
                    if (code != ErrorCode.SUCCEEDED) {
                        LOGGER.error(String.format("Create Tag Failed: %s", statement));
                        System.exit(-1);
                    }
                }

                for (String statement : createEdges) {
                    code = client.execute(statement);
                    if (code != ErrorCode.SUCCEEDED) {
                        LOGGER.error(String.format("Create Edge Failed: %s", statement));
                        System.exit(-1);
                    }
                }

                for (String statement : insertVertices) {
                    code = client.execute(statement);
                    if (code != ErrorCode.SUCCEEDED) {
                        LOGGER.error(String.format("Insert Vertices Failed: %s", statement));
                        System.exit(-1);
                    }
                }

                for (String statement : insertEdges) {
                    code = client.execute(statement);
                    if (code != ErrorCode.SUCCEEDED) {
                        LOGGER.error(String.format("Insert Edges Failed: %s", statement));
                        System.exit(-1);
                    }
                }

                ResultSet resultSet = null;
                try {
                    resultSet = client.executeQuery(simpleQuery);
                } catch (ConnectionException | NGQLException | TException e) {
                    LOGGER.error("Query Failed: ", e.getMessage());
                }
                List<String> columns = resultSet.getColumns()
                                                .stream()
                                                .map(String::new)
                                                .collect(Collectors.toList());
                LOGGER.info(String.format("Columns: %s", Joiner.on(" ").join(columns)));

                for (RowValue value : resultSet.getRows()) {
                    LOGGER.info("ID: %d", value.columns.get(0).getId());
                }

                try {
                    resultSet = client.executeQuery(complexQuery);
                } catch (ConnectionException | NGQLException | TException e) {
                    LOGGER.error("Query Failed: ", e.getMessage());
                }

                columns = resultSet.getColumns()
                                   .stream()
                                   .map(String::new)
                                   .collect(Collectors.toList());

                LOGGER.info(String.format("Columns: %s", Joiner.on(" ").join(columns)));

                for (RowValue value : resultSet.getRows()) {
                    LOGGER.info("%s, %d, %s", new String(value.columns.get(0).getStr()),
                            value.columns.get(1).getInteger(),
                            new String(value.columns.get(2).getStr()));
                }
            }
        } catch (Exception e) {
            LOGGER.error("Connection Failed: ", e.getMessage());
        }
    }
}

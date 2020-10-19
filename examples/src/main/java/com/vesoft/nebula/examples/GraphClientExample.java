/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.examples;

import com.google.common.base.Joiner;
import com.vesoft.nebula.client.graph.*;
import com.vesoft.nebula.client.graph.data.HostAddress;
import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.exception.IOErrorException;
import com.vesoft.nebula.client.graph.net.NebulaPool;
import com.vesoft.nebula.client.graph.net.Session;
import com.vesoft.nebula.graph.ErrorCode;
import com.vesoft.nebula.graph.RowValue;
import java.util.Arrays;
import java.util.List;
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

    private static final String emptyQuery = "GO FROM 2020 OVER select;";

    private static final String complexQuery = "GO FROM 201 OVER like "
        + "WHERE $$.student.age >= 17 YIELD $$.student.name AS Friend, "
        + "$$.student.age AS Age, $$.student.gender AS Gender;";

    // Batch insert some edges for ScanEdgeInSpaceExample and SparkExample
    private static final String batchInsertEdges =
        "INSERT EDGE select(grade) VALUES "
                + "1 -> 999:(60), 1 -> 999:(85), 1 -> 999:(97), 1 -> 999:(40), 1 -> 999:(23), "
                + "1 -> 999:(36), 1 -> 999:(37), 1 -> 999:(99), 1 -> 999:(81), 1 -> 999:(78), "
                + "2 -> 998:(17), 2 -> 998:(8), 2 -> 998:(46), 2 -> 998:(60), 2 -> 998:(17), "
                + "2 -> 998:(95), 2 -> 998:(70), 2 -> 998:(80), 2 -> 998:(51), 2 -> 998:(92), "
                + "3 -> 997:(26), 3 -> 997:(44), 3 -> 997:(11), 3 -> 997:(54), 3 -> 997:(52), "
                + "3 -> 997:(31), 3 -> 997:(85), 3 -> 997:(59), 3 -> 997:(92), 3 -> 997:(60), "
                + "4 -> 996:(36), 4 -> 996:(19), 4 -> 996:(18), 4 -> 996:(44), 4 -> 996:(59), "
                + "4 -> 996:(26), 4 -> 996:(5), 4 -> 996:(60), 4 -> 996:(55), 4 -> 996:(81), "
                + "5 -> 995:(60), 5 -> 995:(48), 5 -> 995:(40), 5 -> 995:(74), 5 -> 995:(35), "
                + "5 -> 995:(85), 5 -> 995:(86), 5 -> 995:(15), 5 -> 995:(50), 5 -> 995:(50), "
                + "6 -> 994:(38), 6 -> 994:(16), 6 -> 994:(22), 6 -> 994:(74), 6 -> 994:(28), "
                + "6 -> 994:(59), 6 -> 994:(53), 6 -> 994:(51), 6 -> 994:(68), 6 -> 994:(68), "
                + "7 -> 993:(1), 7 -> 993:(68), 7 -> 993:(40), 7 -> 993:(49), 7 -> 993:(5), "
                + "7 -> 993:(82), 7 -> 993:(80), 7 -> 993:(35), 7 -> 993:(20), 7 -> 993:(98), "
                + "8 -> 992:(38), 8 -> 992:(29), 8 -> 992:(41), 8 -> 992:(27), 8 -> 992:(21), "
                + "8 -> 992:(71), 8 -> 992:(81), 8 -> 992:(23), 8 -> 992:(31), 8 -> 992:(82), "
                + "9 -> 991:(70), 9 -> 991:(33), 9 -> 991:(42), 9 -> 991:(37), 9 -> 991:(11), "
                + "9 -> 991:(80), 9 -> 991:(12), 9 -> 991:(96), 9 -> 991:(43), 9 -> 991:(39), "
                + "10 -> 990:(83), 10 -> 990:(54), 10 -> 990:(2), 10 -> 990:(40), 10 -> 990:(82), "
                + "10 -> 990:(80), 10 -> 990:(28), 10 -> 990:(76), 10 -> 990:(27), 10 -> 990:(13);";

    public static void main(String[] args) {
        NebulaPool pool = new NebulaPool();
        try {
            List<HostAddress> addresses = Arrays.asList(
                    new HostAddress("127.0.0.1", 3699));
            NebulaPoolConfig nebulaPoolConfig = new NebulaPoolConfig();
            nebulaPoolConfig.setMaxConnSize(1);
            nebulaPoolConfig.setTimeout(10000);
            pool.init(addresses,  nebulaPoolConfig);
            Session session = pool.getSession("root", "nebula", true);
            ResultSet resp = session.execute(String.format("USE %s", SPACE_NAME));
            if (ErrorCode.SUCCEEDED != resp.getErrorCode()) {
                LOGGER.error(String.format("Switch Space %s Failed", SPACE_NAME));
                LOGGER.error(String.format("Please confirm %s have been created", SPACE_NAME));
                System.exit(-1);
            }

            for (String statement : createTags) {
                resp = session.execute(statement);
                LOGGER.info(statement);
                if (!resp.isSucceeded()) {
                    LOGGER.error(String.format("Create Tag Failed: %s", statement));
                    System.exit(-1);
                }
            }

            for (String statement : createEdges) {
                LOGGER.info(statement);
                resp = session.execute(statement);
                if (!resp.isSucceeded()) {
                    LOGGER.error(String.format("Create Edge Failed: %s", statement));
                    System.exit(-1);
                }
            }

            for (String statement : insertVertices) {
                LOGGER.info(statement);
                resp = session.execute(statement);
                if (!resp.isSucceeded()) {
                    LOGGER.error(String.format("Insert Vertices Failed: %s", statement));
                    System.exit(-1);
                }
            }

            for (String statement : insertEdges) {
                LOGGER.info(statement);
                resp = session.execute(statement);
                if (!resp.isSucceeded()) {
                    LOGGER.error(String.format("Insert Edges Failed: %s", statement));
                    System.exit(-1);
                }
            }

            try {
                LOGGER.info(simpleQuery);
                resp = session.execute(simpleQuery);
                if (!resp.isSucceeded()) {
                    LOGGER.error("Query Failed: ", resp.getErrorMessage());
                }
            } catch (IOErrorException e) {
                e.printStackTrace();
            }

            LOGGER.info(String.format("Columns: %s", Joiner.on(" ").join(resp.getColumns())));

            for (RowValue value : resp.getRows()) {
                LOGGER.info("ID: {}", value.columns.get(0).getId());
            }

            try {
                LOGGER.info(emptyQuery);
                resp = session.execute(emptyQuery);
                if (resp.isSucceeded()) {
                    LOGGER.error("Query Failed: ", resp.getErrorMessage());
                }
            } catch (IOErrorException e) {
                e.printStackTrace();
            }

            try {
                LOGGER.info(complexQuery);
                resp = session.execute(complexQuery);
                if (!resp.isSucceeded()) {
                    LOGGER.error("Query Failed: ", resp.getErrorMessage());
                }
            } catch (IOErrorException e) {
                e.printStackTrace();
            }

            LOGGER.info(String.format("Columns: %s", Joiner.on(" ").join(resp.getColumns())));
            for (ResultSet.Result value : resp.getResults()) {
                LOGGER.info(String.format("%s, %d, %s", value.getString("Friend"),
                        value.getInteger("Age"),
                        value.getString("Gender")));
            }

            LOGGER.info(batchInsertEdges);
            resp = session.execute(batchInsertEdges);
            if (!resp.isSucceeded()) {
                LOGGER.error(String.format("Batch Insert Edges Failed: %s", batchInsertEdges));
                System.exit(-1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.close();
        }
    }
}

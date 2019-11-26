/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.graph.client.async;

import com.facebook.thrift.TBase;
import com.facebook.thrift.TException;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.vesoft.nebula.graph.ErrorCode;
import com.vesoft.nebula.graph.ExecutionResponse;
import com.vesoft.nebula.graph.RowValue;
import com.vesoft.nebula.graph.client.ConnectionException;
import com.vesoft.nebula.graph.client.NGQLException;
import com.vesoft.nebula.graph.client.ResultSet;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import com.vesoft.nebula.graph.client.async.entry.ExecuteCallback;
import com.vesoft.nebula.meta.client.async.entry.ListSpaceCallback;
import org.checkerframework.checker.nullness.qual.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsyncGraphClientExample {
    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncGraphClientExample.class);

    private static final String SPACE_NAME = "test";

    private static final String[] createTags = {
        "CREATE TAG course(name string, credits int);",
        "CREATE TAG building(name string);",
        "CREATE TAG student(name string, age int, gender string);",
    };

    private static final String[] createEdges = {
        "CREATE EDGE like(likeness double);", "CREATE EDGE select(grade int);"
    };

    private static final String[] insertVertices = {
        "INSERT VERTEX student(name, age, gender) VALUES 200:(\"Monica\", 16, \"female\");",
        "INSERT VERTEX student(name, age, gender) VALUES 201:(\"Mike\", 18, \"male\");",
        "INSERT VERTEX student(name, age, gender) VALUES 202:(\"Jane\", 17, \"female\");",
        "INSERT VERTEX course(name, credits),building(name) " + "VALUES 101:(\"Math\", 3, "
            + "\"No5\");",
        "INSERT VERTEX course(name, credits),building(name) " + "VALUES 102:(\"English\", 6, "
            + "\"No11\");"
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

    private static final String complexQuery =
        "GO FROM 201 OVER like "
            + "WHERE $$.student.age >= 17 YIELD $$.student.name AS Friend, "
            + "$$.student.age AS Age, $$.student.gender AS Gender;";


    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("Usage: " + "com.vesoft.nebula.examples.GraphClientExample <host> "
                + "<port>");
            return;
        }
        try (AsyncGraphClient client = new AsyncGraphClientImpl(args[0],
            Integer.parseInt(args[1]))) {
            client.connect("user", "password");
            ExecuteCallback switchSpaceCallback = client.switchSpace(SPACE_NAME);
            Optional<TBase> respOption = Optional.absent();
            while (!switchSpaceCallback.checkReady()) {
                respOption = switchSpaceCallback.getResult();
            }

            for (String statement : createTags) {
                client.execute(statement);
            }

            for (String statement : createEdges) {
                client.execute(statement);
            }

            for (String statement : insertVertices) {
                client.execute(statement);
            }

            for (String statement : insertEdges) {
                client.execute(statement);
            }

            ExecuteCallback callback = client.execute(simpleQuery);
            Optional<TBase> simpleResp = Optional.absent();
            while (!callback.checkReady()) {
                simpleResp = callback.getResult();
            }
            if (simpleResp.isPresent()) {
                ExecutionResponse resp = (ExecutionResponse) simpleResp.get();
                int code = resp.getError_code();
                if (code == ErrorCode.SUCCEEDED) {
                    ResultSet resultSet = new ResultSet(resp.getColumn_names(), resp.getRows());
                    LOGGER.info(resultSet.toString());
                } else {
                    LOGGER.error("Execute error: " + resp.getError_msg());
                    throw new NGQLException(code);
                }
            } else {
                LOGGER.error("Execute error");
            }

            callback = client.execute(complexQuery);
            Optional<TBase> complexResp = Optional.absent();
            while (!callback.checkReady()) {
                complexResp = callback.getResult();
            }
            if (complexResp.isPresent()) {
                ExecutionResponse resp = (ExecutionResponse) complexResp.get();
                int code = resp.getError_code();
                if (code == ErrorCode.SUCCEEDED) {
                    ResultSet resultSet = new ResultSet(resp.getColumn_names(), resp.getRows());
                    LOGGER.info(resultSet.toString());
                } else {
                    LOGGER.error("Execute error: " + resp.getError_msg());
                    throw new NGQLException(code);
                }
            } else {
                LOGGER.error("Execute error");
            }
        }

    }
}

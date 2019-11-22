/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.graph.client.async;

import com.facebook.thrift.TException;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.vesoft.nebula.graph.ErrorCode;
import com.vesoft.nebula.graph.RowValue;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import com.vesoft.nebula.graph.client.ConnectionException;
import com.vesoft.nebula.graph.client.NGQLException;
import com.vesoft.nebula.graph.client.ResultSet;
import org.checkerframework.checker.nullness.qual.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsyncGraphClientExample {
    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncGraphClientExample.class);
    private static ListeningExecutorService EXECUTOR_SERVICE =
        MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());

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

    private static FutureCallback<Optional<Integer>> executeCallback =
        new FutureCallback<Optional<Integer>>() {
            @Override
            public void onSuccess(@Nullable Optional<Integer> integerOptional) {
                if (integerOptional.isPresent()) {
                    int code = integerOptional.get();
                    if (code != ErrorCode.SUCCEEDED) {
                        LOGGER.error("Execute Statement Failed");
                        System.exit(-1);
                    } else {
                        LOGGER.info("Statement Successfully Executed");
                    }
                } else {
                    LOGGER.error("Execute Statement Failed");
                    System.exit(-1);
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                LOGGER.error(String.format("Execute Statement Error: %s", throwable.getMessage()));
                System.exit(-1);
            }
        };

    private static FutureCallback<Optional<ResultSet>> executeQueryCallback =
        new FutureCallback<Optional<ResultSet>>() {
            @Override
            public void onSuccess(@Nullable Optional<ResultSet> resultSetOptional) {
                if (resultSetOptional.isPresent()) {
                    ResultSet resultSet = resultSetOptional.get();
                    List<String> columns =
                        resultSet.getColumns().stream().map(String::new).collect(
                            Collectors.toList());
                    LOGGER.info(String.format("Columns: %s", Joiner.on(" ").join(columns)));

                    for (RowValue value : resultSet.getRows()) {
                        LOGGER.info(String.format("ID: %d", value.columns.get(0).getId()));
                    }
                } else {
                    LOGGER.error("Execute Query Statement Failed");
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                LOGGER.error(String.format("Execute Query Statement Error: %s",
                    throwable.getMessage()));
                System.exit(-1);
            }
        };

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("Usage: " + "com.vesoft.nebula.examples.GraphClientExample <host> "
                + "<port>");
            return;
        }
        try (AsyncGraphClient client = new AsyncGraphClientImpl(args[0],
            Integer.parseInt(args[1]))) {
            client.connect("user", "password");
            ListenableFuture<Optional<Integer>> switchSpaceCodeFuture =
                client.switchSpace(SPACE_NAME);
            Futures.addCallback(
                switchSpaceCodeFuture,
                new FutureCallback<Optional<Integer>>() {
                    @Override
                    public void onSuccess(@Nullable Optional<Integer> integerOptional) {
                        if (integerOptional.isPresent()) {
                            int code = integerOptional.get();
                            if (code != ErrorCode.SUCCEEDED) {
                                LOGGER.error(String.format("Switch Space %s Failed",
                                    SPACE_NAME));
                                LOGGER.error(String.format("Please confirm %s have been "
                                    + "created", SPACE_NAME));
                                System.exit(-1);
                            } else {
                                LOGGER.info(String.format("Successfully Switched to Space %s",
                                    SPACE_NAME));
                            }
                        } else {
                            LOGGER.error("Switch Space Failed. No Code Returned");
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        LOGGER.error(String.format("Switch Space Error: %s",
                            throwable.getMessage()));
                    }
                },
                EXECUTOR_SERVICE);

            for (String statement : createTags) {
                ListenableFuture<Optional<Integer>> executeCodeFuture =
                    client.execute(statement);
                Futures.addCallback(executeCodeFuture, executeCallback, EXECUTOR_SERVICE);
            }

            for (String statement : createEdges) {
                ListenableFuture<Optional<Integer>> executeCodeFuture =
                    client.execute(statement);
                Futures.addCallback(executeCodeFuture, executeCallback, EXECUTOR_SERVICE);
            }

            for (String statement : insertVertices) {
                ListenableFuture<Optional<Integer>> executeCodeFuture =
                    client.execute(statement);
                Futures.addCallback(executeCodeFuture, executeCallback, EXECUTOR_SERVICE);
            }

            for (String statement : insertEdges) {
                ListenableFuture<Optional<Integer>> executeCodeFuture =
                    client.execute(statement);
                Futures.addCallback(executeCodeFuture, executeCallback, EXECUTOR_SERVICE);
            }

            try {
                ListenableFuture<Optional<ResultSet>> executeQueryCodeFuture =
                    client.executeQuery(simpleQuery);
                Futures.addCallback(executeQueryCodeFuture, executeQueryCallback,
                    EXECUTOR_SERVICE);
            } catch (ConnectionException | NGQLException | TException e) {
                LOGGER.error(String.format("Query Failed: %s", e.getMessage()));
            }

            try {
                ListenableFuture<Optional<ResultSet>> executeQueryCodeFuture =
                    client.executeQuery(complexQuery);
                Futures.addCallback(executeQueryCodeFuture, executeQueryCallback,
                    EXECUTOR_SERVICE);
            } catch (ConnectionException | NGQLException | TException e) {
                LOGGER.error(String.format("Query Failed: %s", e.getMessage()));
            }
        }

    }
}

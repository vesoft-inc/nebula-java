/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.examples.async;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.vesoft.nebula.client.graph.ResultSet;
import com.vesoft.nebula.client.graph.async.AsyncGraphClient;
import com.vesoft.nebula.client.graph.async.AsyncGraphClientImpl;
import com.vesoft.nebula.graph.ErrorCode;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AsyncGraphClientExample {
    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncGraphClientExample.class);

    private static ListeningExecutorService service;

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
        "INSERT VERTEX course(name, credits),building(name) "
                + "VALUES 101:(\"Math\", 3, \"No5\");",
        "INSERT VERTEX course(name, credits),building(name) "
                + "VALUES 102:(\"English\", 6, \"No11\");"
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
        service = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());

        AsyncGraphClient client = new AsyncGraphClientImpl(args[0], Integer.parseInt(args[1]));
        client.setUser("user");
        client.setPassword("password");

        try {
            client.connect();
            ListenableFuture<Optional<Integer>> listenableFuture = client.switchSpace(SPACE_NAME);
            Futures.addCallback(listenableFuture, new FutureCallback<Optional<Integer>>() {
                @Override
                public void onSuccess(Optional<Integer> integerOptional) {
                    if (integerOptional.isPresent()) {
                        if (integerOptional.get() == ErrorCode.SUCCEEDED) {
                            LOGGER.info("Switch Space Succeed");
                        } else {
                            LOGGER.error(String.format("Switch Space Error: %d",
                                    integerOptional.get()));
                        }
                    } else {
                        LOGGER.error("Switch Space Error");
                    }
                }

                @Override
                public void onFailure(Throwable throwable) {
                    LOGGER.error("Switch Space Error");
                }
            }, service);

            for (final String statement : createTags) {
                listenableFuture = client.execute(statement);
                Futures.addCallback(listenableFuture, new FutureCallback<Optional<Integer>>() {
                    @Override
                    public void onSuccess(Optional<Integer> integerOptional) {
                        if (integerOptional.isPresent()
                                && integerOptional.get() == ErrorCode.SUCCEEDED) {
                            LOGGER.info("Succeed");
                        } else {
                            LOGGER.error(String.format("Create Tag Failed: %s", statement));
                            System.exit(-1);
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        LOGGER.error(String.format("Create Tag Failed: %s", statement));
                        System.exit(-1);
                    }
                }, service);
            }

            for (final String statement : createEdges) {
                listenableFuture = client.execute(statement);
                Futures.addCallback(listenableFuture, new FutureCallback<Optional<Integer>>() {
                    @Override
                    public void onSuccess(Optional<Integer> integerOptional) {
                        if (integerOptional.isPresent()
                                && integerOptional.get() == ErrorCode.SUCCEEDED) {
                            LOGGER.info("Succeed");
                        } else {
                            LOGGER.error(String.format("Create Tag Failed: %s", statement));
                            System.exit(-1);
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        LOGGER.error(String.format("Create Tag Failed: %s", statement));
                        System.exit(-1);
                    }
                }, service);
            }

            for (final String statement : insertVertices) {
                listenableFuture = client.execute(statement);
                Futures.addCallback(listenableFuture, new FutureCallback<Optional<Integer>>() {
                    @Override
                    public void onSuccess(Optional<Integer> integerOptional) {
                        if (integerOptional.isPresent()
                                && integerOptional.get() == ErrorCode.SUCCEEDED) {
                            LOGGER.info("Succeed");
                        } else {
                            LOGGER.error(String.format("Create Tag Failed: %s", statement));
                            System.exit(-1);
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        LOGGER.error(String.format("Create Tag Failed: %s", statement));
                        System.exit(-1);
                    }
                }, service);
            }

            for (final String statement : insertEdges) {
                listenableFuture = client.execute(statement);
                Futures.addCallback(listenableFuture, new FutureCallback<Optional<Integer>>() {
                    @Override
                    public void onSuccess(Optional<Integer> integerOptional) {
                        if (integerOptional.isPresent()
                                && integerOptional.get() == ErrorCode.SUCCEEDED) {
                            LOGGER.info("Succeed");
                        } else {
                            LOGGER.error(String.format("Create Tag Failed: %s", statement));
                            System.exit(-1);
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        LOGGER.error(String.format("Create Tag Failed: %s", statement));
                        System.exit(-1);
                    }
                }, service);
            }

            ListenableFuture<Optional<ResultSet>> queryFuture = client.executeQuery(simpleQuery);
            Futures.addCallback(queryFuture, new FutureCallback<Optional<ResultSet>>() {
                @Override
                public void onSuccess(Optional<ResultSet> resultSetOptional) {
                    if (resultSetOptional.isPresent()) {
                        LOGGER.info(resultSetOptional.get().toString());
                    } else {
                        LOGGER.error("Execute Failed");
                    }
                }

                @Override
                public void onFailure(Throwable throwable) {
                    LOGGER.error("Execute Error");
                }
            }, service);

            queryFuture = client.executeQuery(complexQuery);
            Futures.addCallback(queryFuture, new FutureCallback<Optional<ResultSet>>() {
                @Override
                public void onSuccess(Optional<ResultSet> resultSetOptional) {
                    if (resultSetOptional.isPresent()) {
                        LOGGER.info(resultSetOptional.get().toString());
                    } else {
                        LOGGER.error("Execute Failed");
                    }
                }

                @Override
                public void onFailure(Throwable throwable) {
                    LOGGER.error("Execute Error");
                }
            }, service);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

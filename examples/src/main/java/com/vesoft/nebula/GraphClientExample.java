/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula;

import com.vesoft.nebula.driver.graph.data.Edge;
import com.vesoft.nebula.driver.graph.data.Node;
import com.vesoft.nebula.driver.graph.data.ResultSet;
import com.vesoft.nebula.driver.graph.data.ValueWrapper;
import com.vesoft.nebula.driver.graph.exception.IOErrorException;
import com.vesoft.nebula.driver.graph.net.NebulaClient;
import com.vesoft.nebula.driver.graph.scan.ScanEdgeResult;
import com.vesoft.nebula.driver.graph.scan.ScanEdgeResultIterator;
import com.vesoft.nebula.driver.graph.scan.ScanNodeResult;
import com.vesoft.nebula.driver.graph.scan.ScanNodeResultIterator;
import com.vesoft.nebula.driver.graph.scan.TableRow;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GraphClientExample {

    private static final Logger log    = LoggerFactory.getLogger(GraphClientExample.class);
    static               String host   = "127.0.0.1:9669";
    //    static               String multipleHosts = "ip1:9669,ip2:9669";
    static               String user   = "root";
    static               String passwd = "NebulaGraph01";

    public static void main(String[] args) {
        NebulaClient client = null;
        try {
            client = NebulaClient.builder(host, user, passwd)
                    .withAuthOptions(Collections.emptyMap())
                    .withConnectTimeoutMills(1000)
                    .withRequestTimeoutMills(3000)
                    .build();
            createGraphType(client);
            createGraph(client);
            insertData(client);
            query(client);
            queryWithMultiThread(client);
            queryWithMultiThread();
            scanNode(client);
            scanEdge(client);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }

    private static void createGraphType(NebulaClient client) throws IOErrorException,
                                                                    InterruptedException {
        String createSchema = "CREATE GRAPH TYPE IF NOT EXISTS graph_type_nba AS {"
                + "NODE TYPE node_type_player (LABEL player {id INT PRIMARY KEY, name STRING, "
                + "score FLOAT, gender bool, rate DOUBLE}),"
                + "EDGE TYPE edge_type_follow(node_type_player)-[LABEL follow "
                + "{followness INT, likeness FLOAT64}]->(node_type_player)}";
        ResultSet resp = client.execute(createSchema);
        if (!resp.isSucceeded()) {
            log.error(String.format("Execute: `%s', failed: %s",
                                    createSchema, resp.getErrorMessage()));
            System.out.println("create graph type failed, " + resp.getErrorMessage());
            System.exit(1);
        } else {
            log.info("create type succeed!");
        }
        TimeUnit.SECONDS.sleep(5);
    }

    private static void createGraph(NebulaClient client) throws IOErrorException,
                                                                InterruptedException {
        String    createGraph = "CREATE GRAPH IF NOT EXISTS nba graph_type_nba";
        ResultSet resp        = client.execute(createGraph);
        if (!resp.isSucceeded()) {
            log.error(String.format("Execute `%s`, failed: %s", createGraph,
                                    resp.getErrorMessage()));
            System.out.println("create graph failed, " + resp.getErrorMessage());
            System.exit(1);
        } else {
            log.info("create graph succeed!");
        }
        TimeUnit.SECONDS.sleep(5);
    }

    private static void insertData(NebulaClient client) throws IOErrorException {
        String insertVertexes = "TABLE t{id,name,score,gender,rate} =\n"
                + "(1,\"Tim\",87.0,true,7.32),\n"
                + "(2,\"Jerry\",95.0,false,4.01),\n"
                + "(3,\"Kyle\",100,true,9.99)\n"
                + "USE nba \n"
                + "FOR r IN t\n"
                + "INSERT OR IGNORE(@node_type_player"
                + "{id:r.id,name:r.name,score:r.score,gender:r.gender,rate:r.rate})";
        ResultSet resp = client.execute(insertVertexes);
        if (!resp.isSucceeded()) {
            log.error(String.format("Execute: `%s', failed: %s",
                                    insertVertexes, resp.getErrorMessage()));
            System.out.println("insert graph node failed, " + resp.getErrorMessage());
            System.exit(1);
        }
        log.info("insert graph node succeed!");

        String insertEdges = "TABLE t{id1,id2,followness,likeness}=\n"
                + "(1,2,90,66.8),\n"
                + "(2,3,100,93.35)\n"
                + "USE nba \n"
                + "FOR r IN t\n"
                + "OPTIONAL MATCH(src_node) WHERE src_node.id=r.id1 \n"
                + "OPTIONAL MATCH(dst_node) WHERE dst_node.id=r.id2\n"
                + "INSERT OR IGNORE (src_node)-"
                + "[@edge_type_follow{followness:r.followness,likeness:r.likeness}]"
                + "->(dst_node)";
        resp = client.execute(insertEdges);
        if (!resp.isSucceeded()) {
            log.error(String.format("Execute: `%s', failed: %s",
                                    insertEdges, resp.getErrorMessage()));
            System.out.println("insert graph edge failed, " + resp.getErrorMessage());
            System.exit(1);
        }
        log.info("insert graph edge succeed!");
    }

    private static void query(NebulaClient client) throws IOErrorException {
        String queryNode = "USE nba MATCH (v:player) RETURN v.id, v.name, v.score, v.gender, "
                + "v.rate";
        ResultSet resp = client.execute(queryNode);
        if (!resp.isSucceeded()) {
            log.error(String.format("Execute: `%s', failed: %s",
                                    queryNode, resp.getErrorMessage()));
        } else {
            log.info("query node succeed!");
            resolve(resp);
        }

        System.out.println("\n\n");

        String queryEdge = "USE nba MATCH ()-[e:follow]->() RETURN e.followness, e.likeness";
        resp = client.execute(queryEdge);
        if (!resp.isSucceeded()) {
            log.error(String.format("Execute: `%s', failed: %s",
                                    queryNode, resp.getErrorMessage()));
        } else {
            log.info("query edge succeed!");
            resolve(resp);
            System.out.println("\n\n");
        }

    }

    /**
     * Use ONE client for multi-threaded query. The queries here is actually sequential. There is a
     * lock on the execute interface to avoid to execute multiple requests at the same time for one
     * client. DO NOT SUGGEST THIS WAY FOR MULTI_THREAD.
     */
    private static void queryWithMultiThread(NebulaClient client) {
        String queryNode = "USE nba MATCH (v:player) RETURN v.id, v.name, v.score, v.gender, "
                + "v.rate";
        int parallel = 200;

        CountDownLatch  countDownLatch  = new CountDownLatch(parallel);
        ExecutorService executorService = Executors.newFixedThreadPool(parallel);
        AtomicInteger   failed          = new AtomicInteger(0);
        for (int i = 0; i < parallel; i++) {
            executorService.submit(() -> {
                try {
                    ResultSet result = client.execute(
                            "USE nba MATCH ()-[e:follow]->() RETURN e.followness, e.likeness");
                    if (!result.isSucceeded()) {
                        log.error(String.format("Execute: `%s', failed: %s",
                                                queryNode, result.getErrorMessage()));
                        failed.incrementAndGet();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("failed execute: " + failed.get());

        executorService.shutdown();
    }


    /**
     * use multiple client for multi-threaded query.
     */
    private static void queryWithMultiThread() {
        String queryNode = "USE nba MATCH (v:player) RETURN v.id, v.name, v.score, v.gender, "
                + "v.rate";
        int parallel = 200;

        CountDownLatch  countDownLatch  = new CountDownLatch(parallel);
        ExecutorService executorService = Executors.newFixedThreadPool(parallel);
        AtomicInteger   failed          = new AtomicInteger(0);
        for (int i = 0; i < parallel; i++) {
            executorService.submit(() -> {
                NebulaClient client = null;
                try {
                    client = NebulaClient.builder(host, user, passwd)
                            .withAuthOptions(Collections.emptyMap())
                            .withConnectTimeoutMills(5000)
                            .withRequestTimeoutMills(3000)
                            .build();
                    ResultSet result = client.execute(
                            "USE nba MATCH ()-[e:follow]->() RETURN e.followness, e.likeness");
                    if (!result.isSucceeded()) {
                        log.error(String.format("Execute: `%s', failed: %s",
                                                queryNode, result.getErrorMessage()));
                        failed.incrementAndGet();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    countDownLatch.countDown();
                    client.close();
                }
            });
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("failed execute: " + failed.get());

        executorService.shutdown();
    }


    private static void resolve(ResultSet resultSet) {
        if (!resultSet.isSucceeded()) {
            System.out.println("result is not succeed, status is : " + resultSet.getErrorMessage());
            return;
        }

        // resolve the resultSet content only when resultSet is succeed.
        System.out.println("query result row size: " + resultSet.rowSize());

        System.out.println("query latency: " + resultSet.getLatency());

        List<String> columns = resultSet.getColumnNames();
        System.out.println("result columns: " + columns);

        while (resultSet.hasNext()) {
            ResultSet.Record record = resultSet.next();
            // process each line
            List<ValueWrapper> values = record.values();
            for (ValueWrapper valueWrapper : values) {
                // process each property for one line
                if (valueWrapper.isNull()) {
                    System.out.printf("%15s |", "");
                } else if (valueWrapper.isInt()) {
                    System.out.printf("%15s |", valueWrapper.asInt());
                } else if (valueWrapper.isLong()) {
                    System.out.printf("%15s |", valueWrapper.asLong());
                } else if (valueWrapper.isBoolean()) {
                    System.out.printf("%15s |", valueWrapper.asBoolean());
                } else if (valueWrapper.isDouble()) {
                    System.out.printf("%15s |", valueWrapper.asDouble());
                } else if (valueWrapper.isString()) {
                    System.out.printf("%15s |", valueWrapper.asString());
                } else if (valueWrapper.isDate()) {
                    System.out.printf("%15s |", valueWrapper.asDate());
                } else if (valueWrapper.isLocalTime()) {
                    System.out.printf("%15s |", valueWrapper.asLocalTime());
                } else if (valueWrapper.isLocalDateTime()) {
                    System.out.printf("%15s |", valueWrapper.asLocalDateTime());
                } else if (valueWrapper.isList()) {
                    System.out.printf("%15s |", valueWrapper.asList());
                } else if (valueWrapper.isRecord()) {
                    System.out.printf("%15s |", valueWrapper.asRecord());
                } else if (valueWrapper.isNode()) {
                    Node                      node       = valueWrapper.asNode();
                    long                      nodeId     = node.getId();
                    String                    nodeType   = node.getType();
                    Map<String, ValueWrapper> properties = node.getProperties();
                    System.out.printf("%15s |", valueWrapper.asNode());
                } else if (valueWrapper.isEdge()) {
                    Edge                      relationship = valueWrapper.asEdge();
                    long                      srcId        = relationship.getSrcId();
                    long                      dstId        = relationship.getDstId();
                    Map<String, ValueWrapper> properties   = relationship.getProperties();
                    System.out.printf("%15s |", valueWrapper.asEdge());
                }
            }
        }
    }


    private static void scanNode(NebulaClient client) {
        String graphName = "nba";
        String nodeType  = "node_type_player";

        ScanNodeResultIterator iterator          = client.scanNode(graphName, nodeType);
        boolean                hasPrintPropNames = false;
        while (iterator.hasNext()) {
            ScanNodeResult result = iterator.next();
            if (!hasPrintPropNames) {
                System.out.println(result.getPropNames());
                hasPrintPropNames = true;
            }
            if (result.isEmpty()) {
                continue;
            }
            List<TableRow> tableRows = result.getTableRows();
            for (TableRow row : tableRows) {
                System.out.println(row.getValues());
            }
            System.out.println("\n");
        }
    }


    private static void scanEdge(NebulaClient client) {
        String graphName = "nba";
        String edgeType  = "edge_type_follow";

        ScanEdgeResultIterator iterator          = client.scanEdge(graphName, edgeType);
        boolean                hasPrintPropNames = false;
        while (iterator.hasNext()) {
            ScanEdgeResult result = iterator.next();
            if (!hasPrintPropNames) {
                System.out.println(result.getPropNames());
                hasPrintPropNames = true;
            }
            if (result.isEmpty()) {
                continue;
            }

            List<TableRow> tableRows = result.getTableRows();
            for (TableRow row : tableRows) {
                System.out.println(row.getValues());
            }
            System.out.println("\n");
        }
    }
}

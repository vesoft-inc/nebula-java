/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.util;

import com.vesoft.nebula.driver.graph.ServerConstant;
import com.vesoft.nebula.driver.graph.data.ResultSet;
import com.vesoft.nebula.driver.graph.net.NebulaClient;
import io.grpc.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MockGraph {

    private static final Logger log = LoggerFactory.getLogger(MockGraph.class);

    static String address = ServerConstant.address;
    static String user    = ServerConstant.user;
    static String passwd  = ServerConstant.passwd;

    public static void mockGraphData() {
        NebulaClient client = null;
        try {
            client = NebulaClient.builder(address, user, passwd).build();
            String createGraphType = "CREATE GRAPH TYPE IF NOT EXISTS graph_type_nba AS {"
                + "NODE node_type_player (LABEL player "
                + "{id INT PRIMARY KEY, name STRING, score FLOAT, gender bool, rate DOUBLE}),"
                + "EDGE edge_type_follow (node_type_player)-[LABEL follow "
                + "{followness INT, likeness FLOAT64}]->(node_type_player)"
                + "}";

            ResultSet resultSet = client.execute(createGraphType);
            if (!resultSet.isSucceeded()) {
                log.error("create graph type `graph_type_nba` failed:{}",
                          resultSet.getErrorMessage());
                System.exit(1);
            }

            String createGraph = "CREATE GRAPH IF NOT EXISTS nba TYPED graph_type_nba";
            resultSet = client.execute(createGraph);
            if (!resultSet.isSucceeded()) {
                log.error("create graph `nba` failed:{}", resultSet.getErrorMessage());
                System.exit(1);
            }

            String insertNode = "USE nba INSERT OR REPLACE "
                + "(@node_type_player{id:1, name:\"Tim\", "
                + "score: 87.0, gender: true, rate: 7.32}),"
                + "(@node_type_player{id:2, name:\"Jerry\", "
                + "score: 95.0, gender: false, rate: 4.01}),"
                + "(@node_type_player{id:3, name:\"Kyle\", "
                + "score: 100, gender: true, rate: 9.99})";
            resultSet = client.execute(insertNode);
            if (!resultSet.isSucceeded()) {
                log.error("insert node failed for node_type_player:{}",
                          resultSet.getErrorMessage());
                System.exit(1);
            }

            String insertEdge = "table t{id1,id2,followness,likeness} = \n"
                + "(1,2,90,66.8),(2,3,100,93.35)\n"
                + "USE nba\n"
                + "for r IN t\n"
                + "MATCH(src@node_type_player) where src.id=r.id1\n"
                + "MATCH(dst@node_type_player) where dst.id=r.id2\n"
                + "INSERT OR REPLACE (src)-[@edge_type_follow"
                + "{followness:r.followness,likeness:r.likeness}]->(dst)";
            resultSet = client.execute(insertEdge);
            if (!resultSet.isSucceeded()) {
                log.error("insert edge failed for edge_type_follow:{}",
                          resultSet.getErrorMessage());
                System.exit(1);
            }
        } catch (Exception e) {
            log.error("mock graph failed", e);
            System.exit(1);
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }

    public static void mockGraphDataWithSSL() {
        NebulaClient client = null;
        try {
            client = NebulaClient.builder(ServerConstant.sslAddress, user, passwd)
                .withEnableTls(true)
                .withDisableVerifyServerCert(true)
                .build();
            String createGraphType = "CREATE GRAPH TYPE IF NOT EXISTS nba_type_ssl AS {"
                + "NODE node_type_player (LABEL player "
                + "{id INT PRIMARY KEY, name STRING, score FLOAT, gender bool, rate DOUBLE}),"
                + "EDGE edge_type_follow (node_type_player)-[LABEL follow "
                + "{followness INT, likeness FLOAT64}]->(node_type_player)"
                + "}";

            ResultSet resultSet = client.execute(createGraphType);
            if (!resultSet.isSucceeded()) {
                log.error("create graph type `nba_type_ssl` failed:{}",
                          resultSet.getErrorMessage());
                System.exit(1);
            }

            String createGraph = "CREATE GRAPH IF NOT EXISTS nba_ssl TYPED nba_type_ssl";
            resultSet = client.execute(createGraph);
            if (!resultSet.isSucceeded()) {
                log.error("create graph `nba_ssl` failed:{}", resultSet.getErrorMessage());
                System.exit(1);
            }

            String insertNode = "USE nba_ssl INSERT OR REPLACE "
                + "(@node_type_player{id:1, name:\"Tim\", "
                + "score: 87.0, gender: true, rate: 7.32}),"
                + "(@node_type_player{id:2, name:\"Jerry\", "
                + "score: 95.0, gender: false, rate: 4.01}),"
                + "(@node_type_player{id:3, name:\"Kyle\", "
                + "score: 100, gender: true, rate: 9.99})";
            resultSet = client.execute(insertNode);
            if (!resultSet.isSucceeded()) {
                log.error("insert node failed for node_type_player:{}",
                          resultSet.getErrorMessage());
                System.exit(1);
            }

            String insertEdge = "table t{id1,id2,followness,likeness} = \n"
                + "(1,2,90,66.8),(2,3,100,93.35)\n"
                + "USE nba_ssl\n"
                + "for r IN t\n"
                + "MATCH(src@node_type_player) where src.id=r.id1\n"
                + "MATCH(dst@node_type_player) where dst.id=r.id2\n"
                + "INSERT OR REPLACE (src)-[@edge_type_follow"
                + "{followness:r.followness,likeness:r.likeness}]->(dst)";
            resultSet = client.execute(insertEdge);
            if (!resultSet.isSucceeded()) {
                log.error("insert edge failed for edge_type_follow:{}",
                          resultSet.getErrorMessage());
                System.exit(1);
            }
        } catch (Exception e) {
            log.error("mock graph failed", e);
            System.exit(1);
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }

    public static void mockSpecialGraphType() {
        NebulaClient client = null;
        try {
            client = NebulaClient.builder(address, user, passwd).build();
            String createGraphType = "CREATE GRAPH TYPE IF NOT EXISTS `中文_graph\\\"_type` AS {"
                + "NODE `user` (LABEL `user` "
                + "{id INT PRIMARY KEY, `type` STRING, `姓名` STRING, `age\\t` INT32}),"
                + "EDGE `edge` (`user`)-[LABEL `edge` "
                + "{followness INT, likeness FLOAT64, `type\\r` STRING}]->(`user`)"
                + "}";

            ResultSet resultSet = client.execute(createGraphType);
            if (!resultSet.isSucceeded()) {
                log.error("create graph type `中文_graph\"_type` failed:{}",
                          resultSet.getErrorMessage());
                System.exit(1);
            }

            String createGraph = "CREATE GRAPH IF NOT EXISTS `图` TYPED `中文_graph\"_type`";
            resultSet = client.execute(createGraph);
            if (!resultSet.isSucceeded()) {
                log.error("create graph `图` failed:{}", resultSet.getErrorMessage());
                System.exit(1);
            }

            String insertNode = "USE `图` INSERT OR REPLACE "
                + "(@`user`{id:1, `type`:\"点\", `姓名`: \"Tom\", `age\\t`: 18}),"
                + "(@`user`{id:2, `type`:\"node\", `姓名`: \"Bob\", `age\\t`: 19})";
            resultSet = client.execute(insertNode);
            if (!resultSet.isSucceeded()) {
                log.error("insert node failed for `user`:{}",
                          resultSet.getErrorMessage());
                System.exit(1);
            }

            String insertEdge = "table t{id1,id2,followness,likeness,ty} = \n"
                + "(1,2,90,66.8,\"a\"),(2,1,100,93.35,\"b\")\n"
                + "USE `图`\n"
                + "for r IN t\n"
                + "MATCH(src@`user`) where src.id=r.id1\n"
                + "MATCH(dst@`user`) where dst.id=r.id2\n"
                + "INSERT OR REPLACE (src)-[@`edge`"
                + "{followness:r.followness,likeness:r.likeness,`type\\r`:r.ty}]->(dst)";
            resultSet = client.execute(insertEdge);
            if (!resultSet.isSucceeded()) {
                log.error("insert edge failed for `edge`:{}",
                          resultSet.getErrorMessage());
                System.exit(1);
            }
        } catch (Exception e) {
            log.error("mock graph failed", e);
            System.exit(1);
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }


    public static void mockMoreGraphData() {
        NebulaClient client = null;
        try {
            client = NebulaClient.builder(address, user, passwd).build();
            String createGraphType = "CREATE GRAPH TYPE IF NOT EXISTS cursor_type AS {"
                + "NODE player (LABEL player{id INT PRIMARY KEY})}";

            ResultSet resultSet = client.execute(createGraphType);
            if (!resultSet.isSucceeded()) {
                log.error("create graph type `cursor_type` failed:{}",
                          resultSet.getErrorMessage());
                System.exit(1);
            }

            String createGraph = "CREATE GRAPH IF NOT EXISTS cursor TYPED cursor_type";
            resultSet = client.execute(createGraph);
            if (!resultSet.isSucceeded()) {
                log.error("create graph `cursor` failed:{}", resultSet.getErrorMessage());
                System.exit(1);
            }

            String insertNode = "USE cursor INSERT OR REPLACE(@player{id:3}),(@player{id:8}),"
                + "(@player{id:9}),(@player{id:10}),(@player{id:11})";
            resultSet = client.execute(insertNode);
            if (!resultSet.isSucceeded()) {
                log.error("insert node failed for node_type_player:{}",
                          resultSet.getErrorMessage());
                System.exit(1);
            }
        } catch (Exception e) {
            log.error("mock graph failed", e);
            System.exit(1);
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }
}

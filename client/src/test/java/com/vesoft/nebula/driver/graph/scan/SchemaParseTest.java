/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.scan;

import com.vesoft.nebula.driver.graph.ServerConstant;
import com.vesoft.nebula.driver.graph.exception.AuthFailedException;
import com.vesoft.nebula.driver.graph.exception.IOErrorException;
import com.vesoft.nebula.driver.graph.net.NebulaClient;
import com.vesoft.nebula.driver.graph.util.MockGraph;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SchemaParseTest {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    String address = ServerConstant.address;
    String user      = ServerConstant.user;
    String passwd    = ServerConstant.passwd;


    @Before
    public void setup() {
        MockGraph.mockGraphData();
        MockGraph.mockSpecialGraphType();
    }


    @Test()
    public void testGetGraphType() {
        NebulaClient client = null;
        try {
            client = NebulaClient.builder(address, user, passwd).build();
            Method getGraphType = client.getClass().getDeclaredMethod("getGraphType",
                                                                      String.class);
            getGraphType.setAccessible(true);
            String result = (String) getGraphType.invoke(client, "nba");
            assert result.equals("graph_type_nba");

            result = (String) getGraphType.invoke(client, "图");
            assert result.equals("中文_graph\"_type");
        } catch (IOErrorException | AuthFailedException | InvocationTargetException
                 | IllegalAccessException | NoSuchMethodException e) {
            log.error("test getNodeProperties error", e);
            assert false;
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }

    @Test()
    public void testGetGraphTypeWithWrongGraphName() {
        NebulaClient client = null;
        try {
            client = NebulaClient.builder(address, user, passwd).build();
            Method getGraphType = client.getClass().getDeclaredMethod("getGraphType",
                                                                      String.class);
            getGraphType.setAccessible(true);
            getGraphType.invoke(client, "nba_not_exist");
        } catch (InvocationTargetException e) {
            if (e.getTargetException().getMessage()
                    .contains("graphName nba_not_exist does not exist")) {
                log.info("expected to get InvocationTargetException");
                assert true;
            } else {
                log.error("test getGraphType error.");
                assert false;
            }
        } catch (IOErrorException | NoSuchMethodException
                 | AuthFailedException | IllegalAccessException e) {
            log.error("test getGraphType error", e);
            assert false;
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }

    @Test()
    public void testGetNodeProperties() {
        NebulaClient client = null;
        try {
            client = NebulaClient.builder(address, user, passwd).build();
            Method getNodeProperties = client.getClass().getDeclaredMethod("getNodeProperties",
                                                                           String.class,
                                                                           String.class);
            getNodeProperties.setAccessible(true);
            List<String> result = (List<String>) getNodeProperties.invoke(client,
                                                                          "nba",
                                                                          "node_type_player");
            assert result.size() == 5;
            assert result.stream().distinct().count() == 5;
            assert result.contains("id");

            result = (List<String>) getNodeProperties.invoke(client, "图", "user");
            assert result.size() == 4;
            assert result.contains("id");
            assert result.contains("type");
            assert result.contains("姓名");
            assert result.contains("age\t");
        } catch (IOErrorException | AuthFailedException | NoSuchMethodException
                 | InvocationTargetException | IllegalAccessException e) {
            log.error("test getNodeProperties error", e);
            assert false;
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }

    @Test()
    public void testGetEdgeProperties() {
        NebulaClient client = null;
        try {
            client = NebulaClient.builder(address, user, passwd).build();
            Method getEdgeProperties = client.getClass().getDeclaredMethod("getEdgeProperties",
                                                                           String.class,
                                                                           String.class);
            getEdgeProperties.setAccessible(true);
            List<String> result = (List<String>) getEdgeProperties.invoke(client,
                                                                          "nba",
                                                                          "edge_type_follow");
            assert result.size() == 2;
            assert result.stream().distinct().count() == 2;
            assert result.contains("followness");
            assert result.contains("likeness");

            result = (List<String>) getEdgeProperties.invoke(client, "图", "edge");
            assert result.size() == 3;
            assert result.contains("followness");
            assert result.contains("likeness");
            assert result.contains("type\r");
        } catch (IOErrorException | AuthFailedException | NoSuchMethodException
                 | InvocationTargetException | IllegalAccessException e) {
            log.error("test getEdgeProperties error", e);
            assert false;
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }

    @Test
    public void testGetAllParts() {
        NebulaClient client = null;
        try {
            client = NebulaClient.builder(address, user, passwd).build();
            Method getNodeProperties = client.getClass().getDeclaredMethod("getAllParts");
            getNodeProperties.setAccessible(true);
            List<Integer> result = (List<Integer>) getNodeProperties.invoke(client);
            assert result.size() == 10;
            Collections.sort(result);
            List<Integer> expectList = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
            Collections.sort(expectList);
            assert result.equals(expectList);
        } catch (IOErrorException | AuthFailedException | NoSuchMethodException
                 | InvocationTargetException | IllegalAccessException e) {
            log.error("test getAllParts error", e);
            assert false;
        } finally {
            if (client != null) {
                client.close();
            }
        }
    }


}

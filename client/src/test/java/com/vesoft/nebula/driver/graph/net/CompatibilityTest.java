/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.net;

import static com.vesoft.nebula.driver.graph.ServerConstant.address;
import static com.vesoft.nebula.driver.graph.ServerConstant.passwd;
import static com.vesoft.nebula.driver.graph.ServerConstant.user;

import com.vesoft.nebula.driver.graph.data.ResultSet;
import com.vesoft.nebula.driver.graph.decode.ColumnType;
import com.vesoft.nebula.driver.graph.util.MockGraph;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CompatibilityTest {

    private NebulaClient client;

    @Before
    public void setup() {
        MockGraph.mockGraphData();
        try {
            client = NebulaClient.builder(address, user, passwd).build();
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @After
    public void teardown() {
        if (client != null) {
            client.close();
        }
    }

    @Test
    public void testSessionSetSchema() {
        try {
            ResultSet res = client.execute("SESSION SET SCHEMA /default_schema");
            if (!res.isSucceeded()) {
                Assert.fail(res.getErrorMessage());
            }
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testSessionSetDateFormat() {
        try {
            ResultSet res = client.execute("SESSION SET date_format=\"%Y-%m-%d\"");
            if (!res.isSucceeded()) {
                Assert.fail(res.getErrorMessage());
            }
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testSessionSetLocalDateTimeFormat() {
        try {
            ResultSet res = client.execute(
                "SESSION SET local_datetime_format=\"%Y-%m-%dT%H:%M:%S\"");
            if (!res.isSucceeded()) {
                Assert.fail(res.getErrorMessage());
            }
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testSessionSetZonedDateTimeFormat() {
        try {
            ResultSet res = client.execute(
                "SESSION SET zoned_datetime_format=\"%Y-%m-%dT%H:%M:%S %z\"");
            if (!res.isSucceeded()) {
                Assert.fail(res.getErrorMessage());
            }
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testSessionSetLocalTimeFormat() {
        try {
            ResultSet res = client.execute(
                "SESSION SET local_time_format=\"%H:%M:%S\"");
            if (!res.isSucceeded()) {
                Assert.fail(res.getErrorMessage());
            }
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testSessionSetZonedTimeFormat() {
        try {
            ResultSet res = client.execute(
                "SESSION SET zoned_time_format=\"%H:%M:%S %z\"");
            if (!res.isSucceeded()) {
                Assert.fail(res.getErrorMessage());
            }
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testSessionSetTimeZoneFormat() {
        try {
            ResultSet res = client.execute(
                "SESSION SET timezone=\"UTC\"");
            if (!res.isSucceeded()) {
                Assert.fail(res.getErrorMessage());
            }
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testSessionSetParam() {
        try {
            ResultSet res = client.execute(
                "SESSION SET VALUE $a=\"abc\"");
            if (!res.isSucceeded()) {
                Assert.fail(res.getErrorMessage());
            }
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testGetGraphType() {
        try {
            ResultSet res = client.execute(
                String.format(
                    "CALL describe_graph_type(\"%s\") FILTER type_name=\"%s\" return *",
                    "graph_type_nba", "edge_type_follow"));
            if (!res.isSucceeded()) {
                Assert.fail(res.getErrorMessage());
            }
            Assert.assertEquals(1, res.rowSize());
            Assert.assertTrue(res.getColumnNames().contains("properties"));
            Assert.assertTrue(res.getColumnNames().contains("type_pattern"));

            res = client.execute(String.format("DESC GRAPH TYPE `%s`", "graph_type_nba"));
            if (!res.isSucceeded()) {
                Assert.fail(res.getErrorMessage());
            }
            Assert.assertTrue(res.rowSize() > 1);
            ResultSet.Record record = res.next();
            Assert.assertTrue(record.get("type_name").getDataType() != ColumnType.COLUMN_TYPE_NULL);
            Assert.assertTrue(
                record.get("properties").getDataType() != ColumnType.COLUMN_TYPE_NULL);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testDescGraph() {
        try {
            ResultSet res = client.execute(
                String.format("DESCRIBE GRAPH `%s`", "nba"));
            if (!res.isSucceeded()) {
                Assert.fail(res.getErrorMessage());
            }
            Assert.assertEquals(1, res.rowSize());
            Assert.assertTrue("graph_type_nba".equals(res.next().values().get(1).asString()));
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testDescNodeType() {
        try {
            ResultSet res = client.execute(
                "DESCRIBE NODE TYPE `node_type_player` OF `graph_type_nba`");
            if (!res.isSucceeded()) {
                Assert.fail(res.getErrorMessage());
            }
            Assert.assertEquals(5, res.rowSize());
            Assert.assertTrue(res.getColumnNames().contains("property_name"));
            Assert.assertTrue(res.getColumnNames().contains("primary_key"));
            Assert.assertTrue(res.getColumnNames().contains("data_type"));
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testDescEdgeType() {
        try {
            ResultSet res = client.execute(
                String.format("call describe_edge_type('%s', '%s') return *", "graph_type_nba",
                              "edge_type_follow"));
            if (!res.isSucceeded()) {
                Assert.fail(res.getErrorMessage());
            }
            Assert.assertEquals(2, res.rowSize());
            Assert.assertTrue(res.getColumnNames().contains("property_name"));
            Assert.assertTrue(res.getColumnNames().contains("data_type"));

            res = client.execute(String.format(
                "call describe_graph(\"%s\") return graph_type_name next "
                    + "call describe_graph_type(graph_type_name) filter entity_type=\"Edge\" "
                    + "return type_pattern",
                "nba"));
            if (!res.isSucceeded()) {
                Assert.fail(res.getErrorMessage());
            }
            ResultSet.Record record = res.next();
            Assert.assertTrue(
                record.get(0).asString().equals(record.get("type_pattern").asString()));
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testShowPartitions() {
        try {
            ResultSet res = client.execute(
                "CALL show_partitions() RETURN *");
            if (!res.isSucceeded()) {
                Assert.fail(res.getErrorMessage());
            }
            Assert.assertTrue(res.getColumnNames().contains("partition_id"));
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }


    @Test
    public void testShowRoles() {
        try {
            ResultSet res = client.execute(
                "SHOW ROLES");
            if (!res.isSucceeded()) {
                Assert.fail(res.getErrorMessage());
            }
            Assert.assertTrue(res.getColumnNames().contains("name"));
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }


    @Test
    public void testShowPrivileges() {
        try {
            ResultSet res = client.execute(
                "SHOW PRIVILEGES");
            if (!res.isSucceeded()) {
                Assert.fail(res.getErrorMessage());
            }
            Assert.assertTrue(res.getColumnNames().contains("type"));
            Assert.assertTrue(res.getColumnNames().contains("object"));
            Assert.assertTrue(res.getColumnNames().contains("actions"));
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

}

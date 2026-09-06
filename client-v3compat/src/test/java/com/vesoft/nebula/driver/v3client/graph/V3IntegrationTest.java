/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.v3client.graph;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.vesoft.nebula.driver.v3client.graph.data.HostAddress;
import com.vesoft.nebula.driver.v3client.graph.data.Node;
import com.vesoft.nebula.driver.v3client.graph.data.PathWrapper;
import com.vesoft.nebula.driver.v3client.graph.data.Relationship;
import com.vesoft.nebula.driver.v3client.graph.data.ResultSet;
import com.vesoft.nebula.driver.v3client.graph.data.ValueWrapper;
import com.vesoft.nebula.driver.v3client.graph.net.NebulaPool;
import com.vesoft.nebula.driver.v3client.graph.net.Session;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.AfterClass;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * End-to-end integration test for the v3-compatible layer against a live NebulaGraph v5 cluster.
 *
 * <p>Disabled by default (so plain {@code mvn test} stays green without a cluster). Enable and
 * point it at a cluster with:
 *
 * <pre>
 *   mvn -pl client-v3compat test \
 *       -Dnebula.it=true \
 *       -Dnebula.host=127.0.0.1 -Dnebula.port=9669 \
 *       -Dnebula.user=root -Dnebula.password=nebula \
 *       -Dtest=V3IntegrationTest
 * </pre>
 */
public class V3IntegrationTest {

    private static final String HOST = System.getProperty("nebula.host", "127.0.0.1");
    private static final int PORT = Integer.getInteger("nebula.port", 9669);
    private static final String USER = System.getProperty("nebula.user", "root");
    private static final String PASSWORD = System.getProperty("nebula.password", "nebula");

    private static final long SUFFIX = System.currentTimeMillis();
    private static final String GRAPH = "it_graph_" + SUFFIX;
    private static final String GRAPH_TYPE = "it_graph_type_" + SUFFIX;

    private static NebulaPool pool;
    private static Session session;

    @BeforeClass
    public static void setUp() throws Exception {
        Assume.assumeTrue("integration test disabled (run with -Dnebula.it=true)",
                          Boolean.getBoolean("nebula.it"));

        List<HostAddress> addresses = Arrays.asList(new HostAddress(HOST, PORT));
        NebulaPoolConfig poolConfig = new NebulaPoolConfig().setMaxConnSize(10).setTimeout(5000);
        pool = new NebulaPool();
        assertTrue("pool init failed", pool.init(addresses, poolConfig));
        session = pool.getSession(USER, PASSWORD, false);

        exec("CREATE GRAPH TYPE IF NOT EXISTS " + GRAPH_TYPE + " AS {"
             + "NODE TYPE nt_player (LABEL player {id INT PRIMARY KEY, name STRING, age INT}),"
             + "EDGE TYPE et_follow(nt_player)-[LABEL follow {degree INT}]->(nt_player)}");
        Thread.sleep(3000);
        exec("CREATE GRAPH IF NOT EXISTS " + GRAPH + " " + GRAPH_TYPE);
        Thread.sleep(3000);
        exec("TABLE t{id,name,age} = (1,\"Tim\",36),(2,\"Jerry\",24) "
             + "USE " + GRAPH + " "
             + "FOR r IN t INSERT OR IGNORE(@nt_player{id:r.id,name:r.name,age:r.age})");
        exec("TABLE t{id1,id2,degree} = (1,2,90) "
             + "USE " + GRAPH + " "
             + "FOR r IN t "
             + "OPTIONAL MATCH(src_node) WHERE src_node.id=r.id1 "
             + "OPTIONAL MATCH(dst_node) WHERE dst_node.id=r.id2 "
             + "INSERT OR IGNORE (src_node)-[@et_follow{degree:r.degree}]->(dst_node)");
    }

    @AfterClass
    public static void tearDown() {
        if (session != null) {
            try {
                exec("DROP GRAPH IF EXISTS " + GRAPH);
                exec("DROP GRAPH TYPE IF EXISTS " + GRAPH_TYPE);
            } catch (Exception ignored) {
                // ignore cleanup failures
            }
        }
        if (session != null) {
            session.release();
        }
        if (pool != null) {
            pool.close();
        }
    }

    @Test
    public void testQueryNode() throws Exception {
        ResultSet rs = exec("USE " + GRAPH + " MATCH (v:player) RETURN v ORDER BY v.id LIMIT 1");
        assertTrue(rs.isSucceeded());
        assertEquals(1, rs.rowsSize());
        assertTrue(rs.getColumnNames().contains("v"));

        ValueWrapper value = rs.rowValues(0).get("v");
        assertTrue(value.isVertex());
        assertFalse(value.isEdge());

        Node node = value.asNode();
        assertTrue(node.getId().isLong());
        assertTrue(node.getId().asLong() != 0);
        assertEquals(Arrays.asList("player"), node.tagNames());
        assertTrue(node.hasTagName("player"));

        Map<String, ValueWrapper> props = node.properties("player");
        assertEquals("Tim", props.get("name").asString());
        assertEquals(36L, props.get("age").asLong());
    }

    @Test
    public void testQueryEdge() throws Exception {
        ResultSet rs = exec("USE " + GRAPH + " MATCH ()-[e:follow]->() RETURN e LIMIT 1");
        assertTrue(rs.isSucceeded());
        assertEquals(1, rs.rowsSize());

        ValueWrapper value = rs.rowValues(0).get("e");
        assertTrue(value.isEdge());
        Relationship rel = value.asRelationship();
        assertEquals("follow", rel.edgeName());
        assertTrue(rel.srcId().asLong() != 0);
        assertTrue(rel.dstId().asLong() != 0);
        assertNotNull(rel.properties().get("degree"));
        assertEquals(90L, rel.properties().get("degree").asLong());
    }

    @Test
    public void testQueryPath() throws Exception {
        ResultSet rs = exec(
            "USE " + GRAPH + " MATCH p=(a:player)-[e:follow]->(b:player) RETURN p LIMIT 1");
        assertTrue(rs.isSucceeded());

        ValueWrapper value = rs.rowValues(0).get("p");
        assertTrue(value.isPath());
        PathWrapper path = value.asPath();
        assertEquals(1, path.length());
        assertEquals(2, path.getNodes().size());
        assertEquals(1, path.getRelationships().size());
        assertEquals("follow", path.getRelationships().get(0).edgeName());
        assertNotNull(path.getStartNode());
        assertNotNull(path.getEndNode());
    }

    @Test
    public void testScalarValueTypes() throws Exception {
        ResultSet rs = exec(
            "USE " + GRAPH + " RETURN 1 AS i, \"hello\" AS s, 3.5 AS d, true AS b LIMIT 1");
        assertTrue(rs.isSucceeded());
        ValueWrapper i = rs.rowValues(0).get("i");
        ValueWrapper s = rs.rowValues(0).get("s");
        ValueWrapper d = rs.rowValues(0).get("d");
        ValueWrapper b = rs.rowValues(0).get("b");

        assertTrue(i.isLong());
        assertEquals(1L, i.asLong());
        assertTrue(s.isString());
        assertEquals("hello", s.asString());
        assertTrue(d.isDouble());
        assertEquals(3.5d, d.asDouble(), 0.0);
        assertTrue(b.isBoolean());
        assertTrue(b.asBoolean());
    }

    @Test
    public void testSessionPool() throws Exception {
        SessionPoolConfig config = new SessionPoolConfig(
            Arrays.asList(new HostAddress(HOST, PORT)), GRAPH, USER, PASSWORD)
            .setMaxSessionSize(5).setMinSessionSize(1).setRetryConnectTimes(2).setWaitTime(100);
        SessionPool sessionPool = new SessionPool(config);
        try {
            assertTrue(sessionPool.isActive());
            ResultSet rs = sessionPool.execute("MATCH (v:player) RETURN v LIMIT 1");
            assertTrue(rs.isSucceeded());
            assertTrue(rs.rowValues(0).get("v").isVertex());
        } finally {
            sessionPool.close();
        }
    }

    private static ResultSet exec(String gql) throws Exception {
        ResultSet rs = session.execute(gql);
        assertTrue("query failed: " + gql + " -> " + rs.getErrorMessage(), rs.isSucceeded());
        return rs;
    }
}

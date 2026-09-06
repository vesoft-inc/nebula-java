/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.v3client.graph.net;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;

public class SessionParameterTest {

    @Test
    public void testScalarLiterals() {
        assertEquals("NULL", Session.value2GqlLiteral(null));
        assertEquals("true", Session.value2GqlLiteral(true));
        assertEquals("3", Session.value2GqlLiteral(3));
        assertEquals("3.3", Session.value2GqlLiteral(3.3d));
        assertEquals("\"hello\"", Session.value2GqlLiteral("hello"));
        assertEquals("\"a\\\"b\"", Session.value2GqlLiteral("a\"b"));
    }

    @Test
    public void testListLiteral() {
        List<Object> list = new ArrayList<>();
        list.add(1);
        list.add(true);
        assertEquals("[1, true]", Session.value2GqlLiteral(list));
    }

    @Test
    public void testMapLiteral() {
        Map<String, Object> map = new HashMap<>();
        map.put("a", 1);
        map.put("b", true);
        assertEquals("{a: 1, b: true}", Session.value2GqlLiteral(map));
    }

    @Test
    public void testInlineParameters() {
        Map<String, Object> params = new HashMap<>();
        params.put("p1", 3);
        params.put("p2", true);
        params.put("name", "Tom");
        String stmt = "RETURN $p1 + 1, $p2, $name";
        assertEquals("RETURN 3 + 1, true, \"Tom\"", Session.inlineParameters(stmt, params));
    }

    @Test
    public void testInlinePrefixCollision() {
        Map<String, Object> params = new HashMap<>();
        params.put("p1", 1);
        params.put("p10", 10);
        // longest key replaced first to avoid $p1 clobbering $p10
        assertEquals("RETURN 10, 1", Session.inlineParameters("RETURN $p10, $p1", params));
    }
}

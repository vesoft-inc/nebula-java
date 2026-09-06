/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.v3client.graph.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.vesoft.nebula.driver.graph.decode.ColumnType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Test;

public class ValueWrapperTest {

    private static com.vesoft.nebula.driver.graph.data.ValueWrapper v5(Object value,
                                                                      ColumnType type) {
        return new com.vesoft.nebula.driver.graph.data.ValueWrapper(value, type);
    }

    @Test
    public void testScalarTypes() throws Exception {
        ValueWrapper longValue = new ValueWrapper(v5(42L, ColumnType.COLUMN_TYPE_INT64));
        assertTrue(longValue.isLong());
        assertFalse(longValue.isDouble());
        assertEquals(42L, longValue.asLong());

        ValueWrapper intValue = new ValueWrapper(v5(7, ColumnType.COLUMN_TYPE_INT32));
        assertTrue(intValue.isLong());
        assertEquals(7L, intValue.asLong());

        ValueWrapper boolValue = new ValueWrapper(v5(true, ColumnType.COLUMN_TYPE_BOOL));
        assertTrue(boolValue.isBoolean());
        assertEquals(true, boolValue.asBoolean());

        ValueWrapper doubleValue = new ValueWrapper(v5(3.5d, ColumnType.COLUMN_TYPE_FLOAT64));
        assertTrue(doubleValue.isDouble());
        assertEquals(3.5d, doubleValue.asDouble(), 0.0);

        ValueWrapper stringValue = new ValueWrapper(v5("hello", ColumnType.COLUMN_TYPE_STRING));
        assertTrue(stringValue.isString());
        assertEquals("hello", stringValue.asString());

        ValueWrapper nullValue = new ValueWrapper(v5(null, ColumnType.COLUMN_TYPE_ANY));
        assertTrue(nullValue.isNull());
        assertEquals(ValueWrapper.NullType.__NULL__, nullValue.asNull().getNullType());
    }

    @Test
    public void testOfLong() throws Exception {
        ValueWrapper id = ValueWrapper.ofLong(123L);
        assertTrue(id.isLong());
        assertEquals(123L, id.asLong());
    }

    @Test
    public void testList() throws Exception {
        List<com.vesoft.nebula.driver.graph.data.ValueWrapper> list = new ArrayList<>();
        list.add(v5(1L, ColumnType.COLUMN_TYPE_INT64));
        list.add(v5("a", ColumnType.COLUMN_TYPE_STRING));
        ValueWrapper listValue = new ValueWrapper(v5(list, ColumnType.COLUMN_TYPE_LIST));
        assertTrue(listValue.isList());
        assertEquals(2, listValue.asList().size());
        assertEquals(1L, listValue.asList().get(0).asLong());
        assertEquals("a", listValue.asList().get(1).asString());
    }

    @Test
    public void testSet() throws Exception {
        Set<com.vesoft.nebula.driver.graph.data.ValueWrapper> set = new HashSet<>();
        set.add(v5(1L, ColumnType.COLUMN_TYPE_INT64));
        set.add(v5(2L, ColumnType.COLUMN_TYPE_INT64));
        ValueWrapper setValue = new ValueWrapper(v5(set, ColumnType.COLUMN_TYPE_SET));
        assertTrue(setValue.isSet());
        assertEquals(2, setValue.asSet().size());
    }

    @Test
    public void testMap() throws Exception {
        Map<com.vesoft.nebula.driver.graph.data.ValueWrapper,
            com.vesoft.nebula.driver.graph.data.ValueWrapper> map = new HashMap<>();
        map.put(v5("k", ColumnType.COLUMN_TYPE_STRING), v5(1L, ColumnType.COLUMN_TYPE_INT64));
        ValueWrapper mapValue = new ValueWrapper(v5(map, ColumnType.COLUMN_TYPE_MAP));
        assertTrue(mapValue.isMap());
        assertEquals(1L, mapValue.asMap().get("k").asLong());
    }
}

/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.graph.data;

import com.vesoft.nebula.DataSet;
import com.vesoft.nebula.Date;
import com.vesoft.nebula.DateTime;
import com.vesoft.nebula.Edge;
import com.vesoft.nebula.ErrorCode;
import com.vesoft.nebula.NList;
import com.vesoft.nebula.NMap;
import com.vesoft.nebula.NSet;
import com.vesoft.nebula.NullType;
import com.vesoft.nebula.Path;
import com.vesoft.nebula.Row;
import com.vesoft.nebula.Step;
import com.vesoft.nebula.Tag;
import com.vesoft.nebula.Time;
import com.vesoft.nebula.Value;
import com.vesoft.nebula.Vertex;
import com.vesoft.nebula.graph.ExecutionResponse;
import com.vesoft.nebula.graph.PlanDescription;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;


public class TestData {
    public Vertex getVertex(String vid) {
        List<Tag> tags = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Map<byte[], Value> props = new HashMap<>();
            for (int j = 0; j < 5; j++) {
                Value value = new Value();
                value.setIVal(j);
                props.put(String.format("prop%d", j).getBytes(), value);
            }
            Tag tag = new Tag(String.format("tag%d", i).getBytes(), props);
            tags.add(tag);
        }
        return new Vertex(new Value(Value.SVAL, vid.getBytes()), tags);
    }

    public Edge getEdge(String srcId, String dstId) {
        Map<byte[], Value> props = new HashMap<>();
        for (int i = 0; i < 5; i++) {
            Value value = new Value();
            value.setIVal(i);
            props.put(String.format("prop%d", i).getBytes(), value);
        }
        return new Edge(new Value(Value.SVAL, srcId.getBytes()),
                new Value(Value.SVAL, dstId.getBytes()),
                1,
                "classmate".getBytes(),
                100,
                props);
    }

    public Path getPath(String startId, int stepsNum) {
        List<Step> steps = new ArrayList<>();
        for (int i = 0; i < stepsNum; i++) {
            Map<byte[], Value> props = new HashMap<>();
            for (int j = 0; j < 5; j++) {
                Value value = new Value();
                value.setIVal(j);
                props.put(String.format("prop%d", j).getBytes(), value);
            }
            int type = 1;
            if (i % 2 != 0) {
                type = -1;
            }
            steps.add(new Step(getVertex(String.format("vertex%d", i)),
                    type,
                    ("classmate").getBytes(), 100, props));
        }
        return new Path(getVertex(startId), steps);
    }

    public Vertex getSimpleVertex() {
        Map<byte[], Value> props1 = new HashMap<>();
        props1.put("tag1_prop".getBytes(), new Value(Value.IVAL, (long)100));
        Map<byte[], Value> props2 = new HashMap<>();
        props2.put("tag2_prop".getBytes(), new Value(Value.IVAL, (long)200));
        List<Tag> tags = Arrays.asList(new Tag("tag1".getBytes(), props1),
            new Tag("tag2".getBytes(), props2));
        return new Vertex(new Value(Value.SVAL, "vertex".getBytes()), tags);
    }

    public Edge getSimpleEdge(boolean isReverse) {
        Map<byte[], Value> props = new HashMap<>();
        props.put("edge_prop".getBytes(), new Value(Value.IVAL, (long)100));
        int type = 1;
        if (isReverse) {
            type = -1;
        }
        return new Edge(new Value(Value.SVAL, "Tom".getBytes()),
            new Value(Value.SVAL, "Lily".getBytes()),
            type,
            "classmate".getBytes(),
            10,
            props);
    }

    public Path getSimplePath(boolean isReverse) {
        Map<byte[], Value> props1 = new HashMap<>();
        props1.put("tag1_prop".getBytes(), new Value(Value.IVAL, (long)200));
        List<Tag> tags2 = Collections.singletonList(new Tag("tag1".getBytes(), props1));
        Vertex vertex1 = new Vertex(new Value(Value.SVAL, "vertex1".getBytes()), tags2);
        List<Step> steps = new ArrayList<>();
        Map<byte[], Value> props3 = new HashMap<>();
        props3.put("edge1_prop".getBytes(), new Value(Value.IVAL, (long)100));
        steps.add(new Step(vertex1,
                1,
                "classmate".getBytes(),
                100,
                props3));
        Map<byte[], Value> props2 = new HashMap<>();
        props2.put("tag2_prop".getBytes(), new Value(Value.IVAL, (long)300));
        List<Tag> tags3 = Collections.singletonList(new Tag("tag2".getBytes(), props2));
        Vertex vertex2 = new Vertex(new Value(Value.SVAL, "vertex2".getBytes()), tags3);
        Map<byte[], Value> props4 = new HashMap<>();
        props4.put("edge2_prop".getBytes(), new Value(Value.IVAL, (long)200));
        steps.add(new Step(vertex2,
                isReverse ? -1 : 1,
                "classmate".getBytes(),
                10,
                props4));
        Map<byte[], Value> props0 = new HashMap<>();
        props0.put("tag0_prop".getBytes(), new Value(Value.IVAL, (long)100));
        List<Tag> tags1 = Collections.singletonList(new Tag("tag0".getBytes(), props0));
        Vertex vertex0 = new Vertex(new Value(Value.SVAL, "vertex0".getBytes()), tags1);
        return new Path(vertex0, steps);
    }

    public DataSet getDateset() {
        final ArrayList<Value> list = new ArrayList<>();
        list.add(new Value(Value.IVAL, 1L));
        list.add(new Value(Value.IVAL, 2L));
        final HashSet<Value> set = new HashSet<>();
        set.add(new Value(Value.IVAL, 1L));
        set.add(new Value(Value.IVAL, 2L));
        final HashMap<byte[], Value> map = new HashMap();
        map.put("key1".getBytes(), new Value(Value.IVAL, 1L));
        map.put("key2".getBytes(), new Value(Value.IVAL, 2L));
        final Row row = new Row(Arrays.asList(
                new Value(),
                new Value(Value.NVAL, NullType.OUT_OF_RANGE),
                new Value(Value.BVAL, false),
                new Value(Value.IVAL, 1L),
                new Value(Value.FVAL, 10.01),
                new Value(Value.SVAL, "value1".getBytes()),
                new Value(Value.LVAL, new NList(list)),
                new Value(Value.UVAL, new NSet(set)),
                new Value(Value.MVAL, new NMap(map)),
                new Value(Value.TVAL, new Time((byte)10, (byte)30, (byte)0, 100)),
                new Value(Value.DVAL, new Date((short)2020, (byte)10, (byte)10)),
                new Value(Value.DTVAL,
                    new DateTime((short)2020, (byte)10,
                        (byte)10, (byte)10, (byte)30, (byte)0, 100)),
                new Value(Value.VVAL, getVertex("Tom")),
                new Value(Value.EVAL, getEdge("Tom", "Lily")),
                new Value(Value.PVAL, getPath("Tom", 3))));
        final List<byte[]> columnNames = Arrays.asList(
            "col0_empty".getBytes(),
            "col1_null".getBytes(),
            "col2_bool".getBytes(),
            "col3_int".getBytes(),
            "col4_double".getBytes(),
            "col5_string".getBytes(),
            "col6_list".getBytes(),
            "col7_set".getBytes(),
            "col8_map".getBytes(),
            "col9_time".getBytes(),
            "col10_date".getBytes(),
            "col11_datetime".getBytes(),
            "col12_vertex".getBytes(),
            "col13_edge".getBytes(),
            "col14_path".getBytes());
        return new DataSet(columnNames, Collections.singletonList(row));
    }

    @Test
    public void testNode() {
        try {
            Node node = new Node(getVertex("Tom"));
            assert Objects.equals(node.getId().asString(), "Tom");
            assert node.hasTagName("tag1");
            List<String> names = Arrays.asList("prop0", "prop1", "prop2", "prop3", "prop4");
            assert Objects.equals(
                    node.keys("tag0").stream().sorted().collect(Collectors.toList()),
                    names.stream().sorted().collect(Collectors.toList()));
            List<Value> propValues = Arrays.asList(new Value(Value.IVAL, 0L),
                                                   new Value(Value.IVAL, 1L),
                                                   new Value(Value.IVAL, 2L),
                                                   new Value(Value.IVAL, 3L),
                                                   new Value(Value.IVAL, 4L));

            // TODO: Check the List<Value>
        } catch (Exception e) {
            e.printStackTrace();
            assert (false);
        }
    }

    @Test
    public void testRelationShip() {
        try {
            Edge edge = getEdge("Tom", "Lily");
            Relationship relationShip = new Relationship(edge);
            assert Objects.equals(relationShip.srcId().asString(), "Tom");
            assert Objects.equals(relationShip.dstId().asString(), "Lily");
            assert Objects.equals(relationShip.edgeName(), "classmate");
            assert relationShip.ranking() == 100;

            // check keys
            List<String> names = Arrays.asList("prop0", "prop1", "prop2", "prop3", "prop4");
            assert Objects.equals(
                    relationShip.keys().stream().sorted().collect(Collectors.toList()),
                    names.stream().sorted().collect(Collectors.toList()));

            // check get values
            List<ValueWrapper> values = relationShip.values();
            assert values.get(0).isLong();
            ArrayList<Long> longVals  = new ArrayList<>();
            for (ValueWrapper val : values) {
                assert val.isLong();
                longVals.add(val.asLong());
            }
            List<Long> expectVals = Arrays.asList(0L, 1L, 2L, 3L, 4L);
            assert Objects.equals(expectVals,
                longVals.stream().sorted().collect(Collectors.toList()));

            // check properties
            HashMap<String, ValueWrapper> properties = relationShip.properties();
            assert properties.containsKey("prop0");
            assert properties.get("prop0").isLong();
            Assert.assertEquals(properties.get("prop0").asLong(), 0L);
            assert properties.containsKey("prop1");
            assert properties.get("prop1").isLong();
            Assert.assertEquals(properties.get("prop1").asLong(), 1L);
            assert properties.containsKey("prop2");
            assert properties.get("prop2").isLong();
            Assert.assertEquals(properties.get("prop2").asLong(), 2L);
            assert properties.containsKey("prop3");
            assert properties.get("prop3").isLong();
            Assert.assertEquals(properties.get("prop3").asLong(), 3L);
            assert properties.containsKey("prop4");
            assert properties.get("prop4").isLong();
            Assert.assertEquals(properties.get("prop4").asLong(), 4L);
            assert properties.containsKey("prop4");
        } catch (Exception e) {
            e.printStackTrace();
            assert (false);
        }
    }

    @Test
    public void testPathWarpper() {
        try {
            Path path = getPath("Tom", 5);
            PathWrapper pathWrapper = new PathWrapper(path);
            assert pathWrapper.length() == 5;
            Node node = new Node(getVertex("Tom"));
            assert pathWrapper.containNode(node);
            Relationship relationShip = new Relationship(getEdge("Tom", "vertex0"));
            assert pathWrapper.containRelationship(relationShip);
            List<Node> nodes = new ArrayList<>();
            nodes.add(node);
            for (int i = 0; i < 5; i++) {
                nodes.add(new Node(getVertex(String.format("vertex%d", i))));
            }

            List<Relationship> relationships = new ArrayList<>();
            relationships.add(new Relationship(getEdge("Tom", "vertex0")));
            for (int i = 0; i < 4; i++) {
                if (i % 2 == 0) {
                    relationships.add(new Relationship(getEdge(String.format("vertex%d", i + 1),
                                                               String.format("vertex%d", i))));
                } else {
                    relationships.add(
                            new Relationship(getEdge(String.format("vertex%d", i),
                                             String.format("vertex%d", i + 1))));
                }
            }

            assert Objects.equals(nodes, pathWrapper.getNodes());
            assert Objects.equals(relationships, pathWrapper.getRelationships());
        } catch (Exception e) {
            e.printStackTrace();
            assert (false);
        }
    }

    @Test
    public void testResult() {
        try {
            ExecutionResponse resp =  new ExecutionResponse();
            resp.error_code = ErrorCode.SUCCEEDED;
            resp.error_msg = "test".getBytes();
            resp.comment = "test_comment".getBytes();
            resp.latency_in_us = 1000;
            resp.plan_desc = new PlanDescription();
            resp.space_name = "test_space".getBytes();
            resp.data = getDateset();
            ResultSet resultSet = new ResultSet(resp, 28800);
            assert resultSet.isSucceeded();
            assert resultSet.getErrorCode() == ErrorCode.SUCCEEDED.getValue();
            assert !resultSet.isEmpty();
            assert Objects.equals(resultSet.getComment(), "test_comment");
            assert Objects.equals(resultSet.getSpaceName(), "test_space");
            Assert.assertEquals(1000, resultSet.getLatency());
            assert resultSet.getPlanDesc() != null;
            List<String> expectColNames = Arrays.asList(
                "col0_empty", "col1_null", "col2_bool", "col3_int", "col4_double", "col5_string",
                "col6_list", "col7_set", "col8_map", "col9_time", "col10_date",
                "col11_datetime", "col12_vertex", "col13_edge", "col14_path");
            assert Objects.equals(resultSet.keys(), expectColNames);
            assert resultSet.getRows().size() == 1;
            ResultSet.Record record = resultSet.rowValues(0);
            assert record.size() == 15;
            assert record.get(0).isEmpty();

            assert record.get(1).isNull();
            assert record.get(1).asNull().getNullType() == ValueWrapper.NullType.OUT_OF_RANGE;

            assert record.get(2).isBoolean();
            assert !record.get(2).asBoolean();

            assert record.get(3).isLong();
            assert record.get(3).asLong() == 1;

            assert record.get(4).isDouble();
            assert Double.compare(record.get(4).asDouble(), 10.01) == 0;

            assert record.get(5).isString();
            assert Objects.equals("value1", record.get(5).asString());

            Assert.assertArrayEquals(
                record.get(6).asList().stream().map(ValueWrapper::asLong).toArray(),
                Arrays.asList((long)1, (long)2).toArray());

            assert record.get(7).isSet();
            Set<Long> set = record.get(7).asSet().stream().map(ValueWrapper::asLong)
                .collect(Collectors.toSet());
            assert set.size() == 2;
            assert set.contains((long) 1);
            assert set.contains((long) 2);

            assert record.get(8).isMap();
            HashMap<String, ValueWrapper> map = record.get(8).asMap();
            assert map.keySet().size() == 2;
            Assert.assertArrayEquals(map.keySet().toArray(),
                Arrays.asList("key1", "key2").toArray());
            Assert.assertArrayEquals(map.values().stream().map(ValueWrapper::asLong).toArray(),
                Arrays.asList((long)1, (long)2).toArray());

            assert record.get(9).isTime();
            assert record.get(9).asTime() instanceof TimeWrapper;
            TimeWrapper timeWrapper = (TimeWrapper) new TimeWrapper(
                new Time((byte)10, (byte)30, (byte)0, 100)).setTimezoneOffset(28800);
            assert Objects.equals(record.get(9).asTime(), timeWrapper);
            Assert.assertEquals("utc time: 10:30:00.000100, timezoneOffset: 28800",
                                timeWrapper.toString());
            Assert.assertEquals("18:30:00.000100", timeWrapper.getLocalTimeStr());
            Assert.assertEquals("10:30:00.000100", timeWrapper.getUTCTimeStr());
            Assert.assertEquals(new Time(
                (byte)18, (byte)30, (byte)0, 100), timeWrapper.getLocalTime());

            assert record.get(10).isDate();
            assert record.get(10).asDate() instanceof DateWrapper;
            DateWrapper dateWrapper = new DateWrapper(new Date((short)2020, (byte)10, (byte)10));
            assert Objects.equals(record.get(10).asDate(), dateWrapper);
            Assert.assertEquals("2020-10-10", dateWrapper.toString());

            assert record.get(11).isDateTime();
            DateTimeWrapper dateTimeWrapper = (DateTimeWrapper) new DateTimeWrapper(
                new DateTime((short)2020, (byte)10,
                (byte)10, (byte)10, (byte)30, (byte)0, 100)).setTimezoneOffset(28800);
            assert record.get(11).asDateTime() instanceof DateTimeWrapper;
            assert Objects.equals(record.get(11).asDateTime(), dateTimeWrapper);
            Assert.assertEquals("utc datetime: 2020-10-10T10:30:00.000100, timezoneOffset: 28800",
                                dateTimeWrapper.toString());
            Assert.assertEquals("2020-10-10T18:30:00.000100",
                dateTimeWrapper.getLocalDateTimeStr());
            Assert.assertEquals("2020-10-10T10:30:00.000100",
                dateTimeWrapper.getUTCDateTimeStr());
            Assert.assertEquals(
                new DateTime((short)2020, (byte)10,
                    (byte)10, (byte)18, (byte)30, (byte)0, 100),
                dateTimeWrapper.getLocalDateTime());

            assert record.get(12).isVertex();
            assert Objects.equals(record.get(12).asNode(),
                new Node(getVertex("Tom")));

            assert record.get(13).isEdge();
            assert Objects.equals(record.get(13).asRelationship(),
                new Relationship(getEdge("Tom", "Lily")));

            assert record.get(14).isPath();
            assert Objects.equals(record.get(14).asPath(),
                new PathWrapper(getPath("Tom", 3)));
            assert resultSet.toString().length() > 100;
        } catch (Exception e) {
            e.printStackTrace();
            assert (false);
        }
    }

    @Test
    public void testToString() {
        try {
            // test node
            ValueWrapper valueWrapper = new ValueWrapper(
                new Value(Value.VVAL, getSimpleVertex()),  "utf-8",28800);
            String expectString =
                "(\"vertex\" :tag1 {tag1_prop: 100} :tag2 {tag2_prop: 200})";
            Assert.assertEquals(expectString, valueWrapper.asNode().toString());

            // test relationship
            valueWrapper = new ValueWrapper(
                new Value(Value.EVAL, getSimpleEdge(false)),  "utf-8", 28800);
            expectString = "(\"Tom\")-[:classmate@10{edge_prop: 100}]->(\"Lily\")";
            Assert.assertEquals(expectString, valueWrapper.asRelationship().toString());

            valueWrapper = new ValueWrapper(
                new Value(Value.EVAL, getSimpleEdge(true)),  "utf-8", 28800);
            expectString = "(\"Lily\")-[:classmate@10{edge_prop: 100}]->(\"Tom\")";
            Assert.assertEquals(expectString, valueWrapper.asRelationship().toString());

            // test path
            valueWrapper = new ValueWrapper(
                new Value(Value.PVAL, getSimplePath(true)),  "utf-8", 28800);
            expectString =
                "(\"vertex0\" :tag0 {tag0_prop: 100})"
                    + "-[:classmate@100{edge1_prop: 100}]->"
                    + "(\"vertex1\" :tag1 {tag1_prop: 200})<-[:classmate@10{edge2_prop: 200}]-"
                    + "(\"vertex2\" :tag2 {tag2_prop: 300})";
            Assert.assertEquals(expectString, valueWrapper.asPath().toString());
            valueWrapper = new ValueWrapper(
                new Value(Value.PVAL, getSimplePath(false)),  "utf-8", 28800);
            expectString =
                "(\"vertex0\" :tag0 {tag0_prop: 100})"
                    + "-[:classmate@100{edge1_prop: 100}]->"
                    + "(\"vertex1\" :tag1 {tag1_prop: 200})-[:classmate@10{edge2_prop: 200}]->"
                    + "(\"vertex2\" :tag2 {tag2_prop: 300})";
            Assert.assertEquals(expectString, valueWrapper.asPath().toString());
        } catch (Exception e) {
            e.printStackTrace();
            assert (false);
        }
    }
}

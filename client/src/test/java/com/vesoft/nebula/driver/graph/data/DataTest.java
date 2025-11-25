/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.data;

import com.google.common.base.Charsets;
import com.google.protobuf.ByteString;
import com.vesoft.nebula.driver.graph.ErrorCode;
import com.vesoft.nebula.driver.graph.data.Geography.GeoShape;
import com.vesoft.nebula.driver.graph.decode.ColumnType;
import com.vesoft.nebula.driver.graph.decode.struct.ResultGraphSchemas;
import com.vesoft.nebula.proto.common.Status;
import com.vesoft.nebula.proto.graph.EdgeType;
import com.vesoft.nebula.proto.graph.ElapsedTime;
import com.vesoft.nebula.proto.graph.ExecuteResponse;
import com.vesoft.nebula.proto.graph.NodeType;
import com.vesoft.nebula.proto.graph.PropertyGraphSchema;
import com.vesoft.nebula.proto.graph.RowType;
import com.vesoft.nebula.proto.graph.Summary;
import com.vesoft.nebula.proto.graph.VectorResultTable;
import com.vesoft.nebula.proto.graph.VectorTableMetaData;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;

public class DataTest {

    ByteString graphName    = ByteString.copyFrom("test".getBytes(Charsets.UTF_8));
    ByteString nodeTypeName = ByteString.copyFrom("person".getBytes(Charsets.UTF_8));
    ByteString nodeLabel    = ByteString.copyFrom("person".getBytes(Charsets.UTF_8));
    ByteString edgeTypeName = ByteString.copyFrom("friend".getBytes(Charsets.UTF_8));
    ByteString edgeLabel1   = ByteString.copyFrom("friend1".getBytes(Charsets.UTF_8));
    ByteString edgeLabel2   = ByteString.copyFrom("friend2".getBytes(Charsets.UTF_8));

    PropertyGraphSchema graphSchema = PropertyGraphSchema
        .newBuilder()
        .setGraphId(1)
        .setGraphName(graphName)
        .addNodeType(NodeType
                         .newBuilder()
                         .setNodeTypeId(1024)
                         .setNodeTypeName(nodeTypeName)
                         .addLabel(nodeLabel)
                         .build())
        .addEdgeType(EdgeType
                         .newBuilder()
                         .setEdgeTypeId(1024)
                         .setEdgeTypeName(edgeTypeName)
                         .addLabel(edgeLabel1)
                         .addLabel(edgeLabel2)
                         .build())
        .addEdgeType(EdgeType
                         .newBuilder()
                         .setEdgeTypeId(1073740800)
                         .setEdgeTypeName(edgeTypeName)
                         .addLabel(edgeLabel1)
                         .addLabel(edgeLabel2)
                         .build())
        .addEdgeType(EdgeType
                         .newBuilder()
                         .setEdgeTypeId(0)
                         .setEdgeTypeName(edgeTypeName)
                         .addLabel(edgeLabel1)
                         .addLabel(edgeLabel2)
                         .build())
        .build();
    ResultGraphSchemas  schemas     = new ResultGraphSchemas(Arrays.asList(graphSchema));

    @Test
    public void testNode() {
        try {
            Node node = getNode(1);
            assert Objects.equals(node.getId(), 1L);
            assert (node.toString().startsWith("(1@person"));

            List<String> names = Arrays.asList("prop1", "prop2", "prop3", "prop4");
            assert Objects.equals(
                node.getColumnNames().stream().sorted().collect(Collectors.toList()),
                names.stream().sorted().collect(Collectors.toList()));

            for (String name : node.getProperties().keySet()) {
                if (name.equals("prop1")) {
                    assert node.getProperties().get(name).asLong() == 1;
                } else if (name.equals("prop2")) {
                    assert node.getProperties().get(name).asInt() == 2;
                } else if (name.equals("prop3")) {
                    assert node.getProperties().get(name).asInt() == 3;
                } else if (name.equals("prop4")) {
                    assert node.getProperties().get(name).asInt() == 4;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            assert (false);
        }
    }

    @Test
    public void testEdge() {
        try {
            Map<String, ValueWrapper> props = new HashMap<>();
            props.put("prop0", new ValueWrapper(1L, ColumnType.COLUMN_TYPE_INT64));
            props.put("prop1", new ValueWrapper(2, ColumnType.COLUMN_TYPE_INT32));
            props.put("prop2", new ValueWrapper(3, ColumnType.COLUMN_TYPE_INT16));
            props.put("prop3", new ValueWrapper(4, ColumnType.COLUMN_TYPE_INT8));
            Edge edge = new Edge(1, 1024, 10, 1, 2, props, schemas);

            assert edge.getSrcId() == 1L;
            assert edge.getDstId() == 2L;
            assert edge.getType().equals("friend");
            assert edge.getRank() == 10;

            // check keys
            List<String> names = Arrays.asList("prop0", "prop1", "prop2", "prop3");
            assert Objects.equals(
                edge.getColumnNames().stream().sorted().collect(Collectors.toList()),
                names.stream().sorted().collect(Collectors.toList()));

            // check get values
            List<ValueWrapper> values = edge.getPropertyValues();
            assert values.size() == 4;

            // check properties
            Map<String, ValueWrapper> properties = edge.getProperties();
            assert properties.containsKey("prop0");
            assert properties.get("prop0").isLong();
            Assert.assertEquals(properties.get("prop0").asLong(), 1L);
            assert properties.containsKey("prop1");
            assert properties.get("prop1").isInt();
            Assert.assertEquals(properties.get("prop1").asInt(), 2);
            assert properties.containsKey("prop2");
            assert properties.get("prop2").isInt();
            Assert.assertEquals(properties.get("prop2").asInt(), 3);
            assert properties.containsKey("prop3");
            assert properties.get("prop3").isInt();
            Assert.assertEquals(properties.get("prop3").asInt(), 4);
        } catch (Exception e) {
            e.printStackTrace();
            assert (false);
        }
    }


    @Test
    public void testResult() {
        try {
            VectorResultTable resultTable = VectorResultTable
                .newBuilder()
                .setMeta(VectorTableMetaData
                             .newBuilder()
                             .addGraphSchema(graphSchema)
                             .setNumRecords(10)
                             .setTableType(258)
                             .setRowType(RowType.newBuilder()
                                             .addColumnNames("p1")
                                             .addColumnNames("p2")
                                             .addColumnNames("p3")
                                             .addColumnNames("p4")
                                             .addColumnNames("p5")
                                             .build())
                             .build())
                .build();
            ExecuteResponse response = ExecuteResponse
                .newBuilder()
                .setResult(resultTable)
                .setSummary(Summary.newBuilder()
                                .setElapsedTime(ElapsedTime.newBuilder()
                                                    .setTotalServerTimeUs(5000)
                                                    .setBuildTimeUs(2000)
                                                    .setOptimizeTimeUs(1000)
                                                    .build()))
                .setStatus(Status.newBuilder()
                               .setCode(ByteString.copyFrom("00000", Charsets.UTF_8))
                               .build())
                .build();
            ResultSet resultSet = new ResultSet(response);
            assert resultSet.isSucceeded();
            assert resultSet.getErrorCode() == ErrorCode.SUCCESSFUL_COMPLETION;
            assert !resultSet.isEmpty();
            Assert.assertEquals(5000, resultSet.getLatency());
            List<String> expectColNames = Arrays.asList("p1", "p2", "p3", "p4", "p5");
            assert Objects.equals(resultSet.getColumnNames(), expectColNames);

            assert resultSet.rowSize() == 10;
        } catch (Exception e) {
            e.printStackTrace();
            assert (false);
        }
    }

    @Test
    public void testToString() {
        try {
            // test node
            ValueWrapper valueWrapper = new ValueWrapper(getNode(1),
                                                         ColumnType.COLUMN_TYPE_NODE);
            String expectString =
                "(1@person:person{prop1:1,prop2:2,prop3:3,prop4:4})";
            char[] expectChars = expectString.toCharArray();
            char[] actualChars = valueWrapper.asNode().toString().toCharArray();
            Arrays.sort(expectChars);
            Arrays.sort(actualChars);
            Assert.assertTrue(Arrays.equals(expectChars, actualChars));

            // test relationship
            valueWrapper = new ValueWrapper(getOutEdge(1, 2, 10),
                                            ColumnType.COLUMN_TYPE_EDGE);
            expectString = "(1)-[10@friend:friend1&friend2{prop1:1,prop2:2,prop3:3,prop4:4}]->(2)";

            expectChars = expectString.toCharArray();
            actualChars = valueWrapper.asEdge().toString().toCharArray();
            Arrays.sort(expectChars);
            Arrays.sort(actualChars);
            Assert.assertTrue(Arrays.equals(expectChars, actualChars));

            valueWrapper = new ValueWrapper(getInEdge(1, 2, 20),
                                            ColumnType.COLUMN_TYPE_EDGE);
            expectString = "(2)-[20@friend:friend1&friend2{prop1:1,prop2:2,prop3:3,prop4:4}]->(1)";
            expectChars = expectString.toCharArray();
            actualChars = valueWrapper.asEdge().toString().toCharArray();
            Arrays.sort(expectChars);
            Arrays.sort(actualChars);
            Assert.assertTrue(Arrays.equals(expectChars, actualChars));

            valueWrapper = new ValueWrapper(getUndirectedEdge(1, 2, 10),
                                            ColumnType.COLUMN_TYPE_EDGE);
            expectString = "(1)~[10@friend:friend1&friend2{prop1:1,prop2:2,prop3:3,prop4:4}]~(2)";
            expectChars = expectString.toCharArray();
            actualChars = valueWrapper.asEdge().toString().toCharArray();
            Arrays.sort(expectChars);
            Arrays.sort(actualChars);
            Assert.assertTrue(Arrays.equals(expectChars, actualChars));

            // test local time
            valueWrapper = new ValueWrapper(getSimpleLocalTime(),
                                            ColumnType.COLUMN_TYPE_LOCALTIME);
            expectString = "12:20:15.000030";
            Assert.assertEquals(expectString, valueWrapper.asLocalTime().toString());

            // test local datetime
            valueWrapper = new ValueWrapper(getSimpleLocalDateTime(),
                                            ColumnType.COLUMN_TYPE_LOCALDATETIME);
            expectString = "2024-01-01T12:20:15.000030";
            Assert.assertEquals(expectString, valueWrapper.asLocalDateTime().toString());

            // test date
            valueWrapper = new ValueWrapper(getSimpleDate(), ColumnType.COLUMN_TYPE_DATE);
            expectString = "2024-01-01";
            Assert.assertEquals(expectString, valueWrapper.asDate().toString());
        } catch (Exception e) {
            e.printStackTrace();
            assert (false);
        }
    }

    @Test
    public void testDuration() {
        // test duration of time-based duration
        NDuration    duration     = new NDuration(false, 0, 0, 1, 2, 3, 4, 5);
        ValueWrapper valueWrapper = new ValueWrapper(duration, ColumnType.COLUMN_TYPE_DURATION);
        String       expectString = "P1DT2H3M4.000005S";
        Assert.assertEquals(expectString, valueWrapper.asDuration().toString());

        // test duration with 5000 ms, test the number of digits after the decimal point
        duration = new NDuration(false, 0, 0, 0, 0, 0, 4, 5000);
        valueWrapper = new ValueWrapper(duration, ColumnType.COLUMN_TYPE_DURATION);
        expectString = "PT4.005S";
        Assert.assertEquals(expectString, valueWrapper.asDuration().toString());

        // tet duration of time-based duration only with day
        duration = new NDuration(false, 0, 0, 1, 0, 0, 0, 0);
        valueWrapper = new ValueWrapper(duration, ColumnType.COLUMN_TYPE_DURATION);
        expectString = "P1D";
        Assert.assertEquals(expectString, valueWrapper.asDuration().toString());

        // test duration of month-based duration
        duration = new NDuration(true, -1, -1, 0, 0, 0, 0, 0);
        valueWrapper = new ValueWrapper(duration, ColumnType.COLUMN_TYPE_DURATION);
        expectString = "P-1Y-1M";
        Assert.assertEquals(expectString, valueWrapper.asDuration().toString());
    }


    @Test
    public void testPath() {
        ValueWrapper valueWrapper = new ValueWrapper(
            getInPath(),
            ColumnType.COLUMN_TYPE_PATH);
        String expectString = "(1@person:person{prop1:1,prop2:2,prop3:3,prop4:4})"
            + "<-[10@friend:friend1&friend2{prop1:1,prop2:2,prop3:3,prop4:4}]"
            + "-(2@person:person{prop1:1,prop2:2,prop3:3,prop4:4})"
            + "<-[10@friend:friend1&friend2{prop1:1,prop2:2,prop3:3,prop4:4}]"
            + "-(3@person:person{prop1:1,prop2:2,prop3:3,prop4:4})";

        char[] expectChars = expectString.toCharArray();
        char[] actualChars = valueWrapper.asPath().toString().toCharArray();
        Arrays.sort(expectChars);
        Arrays.sort(actualChars);
        Assert.assertTrue(Arrays.equals(expectChars, actualChars));

        Path path = valueWrapper.asPath();
        Assert.assertEquals(3, path.nodes().size());
        Assert.assertEquals(2, path.edges().size());
        Assert.assertEquals(5, path.values().size());
    }

    @Test
    public void testUndirectedPath() {
        ValueWrapper valueWrapper = new ValueWrapper(getUnDirectedPath(),
                                                     ColumnType.COLUMN_TYPE_PATH);
        String expectString = "(1@person:person{prop1:1,prop2:2,prop3:3,prop4:4})"
            + "~[10@friend:friend1&friend2{prop1:1,prop2:2,prop3:3,prop4:4}]"
            + "~(2@person:person{prop1:1,prop2:2,prop3:3,prop4:4})";

        char[] expectChars = expectString.toCharArray();
        char[] actualChars = valueWrapper.asPath().toString().toCharArray();
        Arrays.sort(expectChars);
        Arrays.sort(actualChars);
        Assert.assertTrue(Arrays.equals(expectChars, actualChars));

        Path path = valueWrapper.asPath();
        Assert.assertEquals(2, path.nodes().size());
        Assert.assertEquals(1, path.edges().size());
        Assert.assertEquals(3, path.values().size());
    }

    @Test
    public void testRecord() {
        Map<String, ValueWrapper> map = new HashMap<>();
        map.put("prop1", new ValueWrapper(1, ColumnType.COLUMN_TYPE_INT32));
        map.put("prop2", new ValueWrapper(2L, ColumnType.COLUMN_TYPE_INT64));
        map.put("prop3", new ValueWrapper("Tom", ColumnType.COLUMN_TYPE_STRING));
        map.put("prop4", new ValueWrapper(true, ColumnType.COLUMN_TYPE_BOOL));
        map.put("prop5", new ValueWrapper(getNode(1L), ColumnType.COLUMN_TYPE_NODE));

        ValueWrapper valueWrapper = new ValueWrapper(new NRecord(map),
                                                     ColumnType.COLUMN_TYPE_RECORD);
        NRecord record = valueWrapper.asRecord();
        assert (!record.isEmpty());
        Assert.assertEquals(5, record.size());
        Assert.assertEquals(1, record.getValue("prop1").asInt());
        Assert.assertEquals(2L, record.getValue("prop2").asLong());
        Assert.assertEquals("Tom", record.getValue("prop3").asString());
        Assert.assertTrue(record.getValue("prop4").asBoolean());
        Assert.assertEquals(1, record.getValue("prop5").asNode().getId());
        Assert.assertEquals("person",
                            record.getValue("prop5").asNode().getType());
        Assert.assertEquals("person",
                            record.getValue("prop5").asNode().getLabels().get(0));

        Map<String, ValueWrapper> values = record.getValuesMap();
        List<String> expectMapKeys = Arrays.asList("prop1",
                                                   "prop2",
                                                   "prop3",
                                                   "prop4",
                                                   "prop5");
        Assert.assertEquals(expectMapKeys.stream().sorted().collect(Collectors.toList()),
                            values.keySet().stream().sorted().collect(Collectors.toList()));
    }

    @Test
    public void testEmptyRecord() {
        ValueWrapper valueWrapper = new ValueWrapper(new NRecord(new HashMap<>()),
                                                     ColumnType.COLUMN_TYPE_RECORD);
        NRecord record = valueWrapper.asRecord();
        assert (record.isEmpty());
    }

    @Test
    public void testEqualRecord() {
        NRecord record1 = getRecord();
        NRecord record2 = getRecord();
        Assert.assertEquals(record1, record2);
        Assert.assertEquals(record1.hashCode(), record2.hashCode());
    }


    @Test
    public void testList() {
        ValueWrapper valueWrapper = new ValueWrapper(getList(),
                                                     ColumnType.COLUMN_TYPE_LIST);
        List<ValueWrapper> values = valueWrapper.asList();
        Assert.assertEquals(5, values.size());
    }

    @Test
    public void testDecimal() {
        ValueWrapper valueWrapper = new ValueWrapper(getDecimal(),
                                                     ColumnType.COLUMN_TYPE_DECIMAL);
        BigDecimal decimal = valueWrapper.asDecimal();
        Assert.assertEquals(new BigDecimal("1.23456789"), decimal);
    }

    @Test
    public void testPoint() {
        NPoint       point        = new NPoint(116.41667, 39.91667);
        ValueWrapper valueWrapper = new ValueWrapper(point, ColumnType.COLUMN_TYPE_GEOGRAPHY);
        Geography    geo          = valueWrapper.asGeography();
        Assert.assertEquals(GeoShape.GeoShapePoint, geo.getShape());
        Assert.assertTrue(valueWrapper.isGeography());
        Assert.assertEquals(point, geo.asPoint());
        Assert.assertEquals("POINT(116.41667 39.91667)", geo.toString());
    }

    @Test
    public void testLineString() {
        List<NPoint> points = new ArrayList<>();
        points.add(new NPoint(1.0, 2.0));
        points.add(new NPoint(3.11, 4.22));
        NLineString  lineString   = new NLineString(points);
        ValueWrapper valueWrapper = new ValueWrapper(lineString, ColumnType.COLUMN_TYPE_GEOGRAPHY);
        Geography    geo          = valueWrapper.asGeography();
        Assert.assertTrue(valueWrapper.isGeography());
        Assert.assertEquals(GeoShape.GeoShapeLineString, geo.getShape());
        Assert.assertEquals(lineString, geo.asLineString());
        Assert.assertEquals("LINESTRING(1.0 2.0,3.11 4.22)",
                            geo.toString());
    }

    @Test
    public void testPolygon() {
        List<NPoint> linearRing1 = new ArrayList<>();
        linearRing1.add(new NPoint(1.11, 2.22));
        linearRing1.add(new NPoint(3.33, 4.44));
        linearRing1.add(new NPoint(7.87, 8.39));
        linearRing1.add(new NPoint(1.11, 2.22));
        List<NPoint> linearRing2 = new ArrayList<>();
        linearRing2.add(new NPoint(1.01, 2.02));
        linearRing2.add(new NPoint(3.03, 4.04));
        linearRing2.add(new NPoint(5.05, 6.06));
        linearRing2.add(new NPoint(1.01, 2.02));
        List<NPoint> linearRing3 = new ArrayList<>();
        linearRing3.add(new NPoint(7.88123, 8.9043));
        linearRing3.add(new NPoint(10.43, 11.66));
        linearRing3.add(new NPoint(15.43, 15.66));
        linearRing3.add(new NPoint(7.88123, 8.9043));
        List<List<NPoint>> linearRings = new ArrayList<>();

        linearRings.add(linearRing1);
        linearRings.add(linearRing2);
        linearRings.add(linearRing3);
        NPolygon     polygon      = new NPolygon(linearRings);
        ValueWrapper valueWrapper = new ValueWrapper(polygon, ColumnType.COLUMN_TYPE_GEOGRAPHY);
        Geography    geo          = valueWrapper.asGeography();
        Assert.assertTrue(valueWrapper.isGeography());
        Assert.assertEquals(GeoShape.GeoShapePolygon, geo.getShape());
        Assert.assertEquals(polygon, geo.asPolygon());
        Assert.assertEquals("POLYGON((1.11 2.22,3.33 4.44,7.87 8.39,1.11 2.22),"
                                + "(1.01 2.02,3.03 4.04,5.05 6.06,1.01 2.02),"
                                + "(7.88123 8.9043,10.43 11.66,15.43 15.66,7.88123 8.9043))",
                            geo.toString());
    }

    // mock data
    private Node getNode(long vid) {
        Map<String, ValueWrapper> props = new HashMap<>();
        props.put("prop1", new ValueWrapper(1L, ColumnType.COLUMN_TYPE_INT64));
        props.put("prop2", new ValueWrapper(2, ColumnType.COLUMN_TYPE_INT32));
        props.put("prop3", new ValueWrapper(3, ColumnType.COLUMN_TYPE_INT16));
        props.put("prop4", new ValueWrapper(4, ColumnType.COLUMN_TYPE_INT8));
        return new Node(1, 1024, vid, props, schemas);
    }

    private Edge getOutEdge(long srcId, long dstId, long rank) {
        Map<String, ValueWrapper> props = new HashMap<>();
        props.put("prop1", new ValueWrapper(1L, ColumnType.COLUMN_TYPE_INT64));
        props.put("prop2", new ValueWrapper(2, ColumnType.COLUMN_TYPE_INT32));
        props.put("prop3", new ValueWrapper(3, ColumnType.COLUMN_TYPE_INT16));
        props.put("prop4", new ValueWrapper(4, ColumnType.COLUMN_TYPE_INT8));
        return new Edge(1, 1024, rank, srcId, dstId, props, schemas);
    }


    private Edge getInEdge(long srcId, long dstId, long rank) {
        Map<String, ValueWrapper> props = new HashMap<>();
        props.put("prop1", new ValueWrapper(1L, ColumnType.COLUMN_TYPE_INT64));
        props.put("prop2", new ValueWrapper(2, ColumnType.COLUMN_TYPE_INT32));
        props.put("prop3", new ValueWrapper(3, ColumnType.COLUMN_TYPE_INT16));
        props.put("prop4", new ValueWrapper(4, ColumnType.COLUMN_TYPE_INT8));
        return new Edge(1, 1073742848, rank, srcId, dstId, props, schemas);
    }

    private Edge getUndirectedEdge(long srcId, long dstId, long rank) {
        Map<String, ValueWrapper> props = new HashMap<>();
        props.put("prop1", new ValueWrapper(1L, ColumnType.COLUMN_TYPE_INT64));
        props.put("prop2", new ValueWrapper(2, ColumnType.COLUMN_TYPE_INT32));
        props.put("prop3", new ValueWrapper(3, ColumnType.COLUMN_TYPE_INT16));
        props.put("prop4", new ValueWrapper(4, ColumnType.COLUMN_TYPE_INT8));
        return new Edge(1, -1073742848, rank, srcId, dstId, props, schemas);
    }

    private Path getPath() {
        List<ValueWrapper> values = new ArrayList<>();
        values.add(new ValueWrapper(getNode(1), ColumnType.COLUMN_TYPE_NODE));
        values.add(new ValueWrapper(getOutEdge(1L, 2L, 10),
                                    ColumnType.COLUMN_TYPE_EDGE));
        values.add(new ValueWrapper(getNode(2), ColumnType.COLUMN_TYPE_NODE));
        values.add(new ValueWrapper(getOutEdge(2L, 3L, 10),
                                    ColumnType.COLUMN_TYPE_EDGE));
        values.add(new ValueWrapper(getNode(3), ColumnType.COLUMN_TYPE_NODE));
        return new Path(values);
    }

    private Path getInPath() {
        List<ValueWrapper> values = new ArrayList<>();
        values.add(new ValueWrapper(getNode(1), ColumnType.COLUMN_TYPE_NODE));
        values.add(new ValueWrapper(getInEdge(1L, 2L, 10),
                                    ColumnType.COLUMN_TYPE_EDGE));
        values.add(new ValueWrapper(getNode(2), ColumnType.COLUMN_TYPE_NODE));
        values.add(new ValueWrapper(getInEdge(2L, 3L, 10),
                                    ColumnType.COLUMN_TYPE_EDGE));
        values.add(new ValueWrapper(getNode(3), ColumnType.COLUMN_TYPE_NODE));
        return new Path(values);
    }

    private Path getUnDirectedPath() {
        List<ValueWrapper> values = new ArrayList<>();
        values.add(new ValueWrapper(getNode(1), ColumnType.COLUMN_TYPE_NODE));
        values.add(new ValueWrapper(getUndirectedEdge(1L, 2L, 10),
                                    ColumnType.COLUMN_TYPE_EDGE));
        values.add(new ValueWrapper(getNode(2), ColumnType.COLUMN_TYPE_NODE));
        return new Path(values);
    }


    private NRecord getRecord() {
        Map<String, ValueWrapper> map = new HashMap<>();
        map.put("prop1", new ValueWrapper(1, ColumnType.COLUMN_TYPE_INT32));
        map.put("prop2", new ValueWrapper(2L, ColumnType.COLUMN_TYPE_INT64));
        map.put("prop3", new ValueWrapper("Tom", ColumnType.COLUMN_TYPE_STRING));
        map.put("prop4", new ValueWrapper(true, ColumnType.COLUMN_TYPE_BOOL));
        map.put("prop5", new ValueWrapper(getNode(1L), ColumnType.COLUMN_TYPE_NODE));

        return new NRecord(map);
    }

    private List<ValueWrapper> getList() {
        List<ValueWrapper> values = new ArrayList<>();
        values.add(new ValueWrapper(1, ColumnType.COLUMN_TYPE_INT32));
        values.add(new ValueWrapper(2, ColumnType.COLUMN_TYPE_INT32));
        values.add(new ValueWrapper(3, ColumnType.COLUMN_TYPE_INT32));
        values.add(new ValueWrapper(4, ColumnType.COLUMN_TYPE_INT32));
        values.add(new ValueWrapper(5, ColumnType.COLUMN_TYPE_INT32));
        return values;
    }

    private LocalTime getSimpleLocalTime() {
        return LocalTime.of(12, 20, 15, 30 * 1000);
    }

    private LocalDateTime getSimpleLocalDateTime() {
        return LocalDateTime.of(2024, 1, 1, 12, 20, 15, 30 * 1000);
    }

    private LocalDate getSimpleDate() {
        return LocalDate.of(2024, 1, 1);
    }

    private BigDecimal getDecimal() {
        return new BigDecimal("1.23456789");
    }

}

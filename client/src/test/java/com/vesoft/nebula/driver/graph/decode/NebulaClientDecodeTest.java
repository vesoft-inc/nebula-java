/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.decode;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import com.vesoft.nebula.driver.graph.ServerConstant;
import com.vesoft.nebula.driver.graph.data.Edge;
import com.vesoft.nebula.driver.graph.data.EmbeddingVector;
import com.vesoft.nebula.driver.graph.data.Geography.GeoShape;
import com.vesoft.nebula.driver.graph.data.NRecord;
import com.vesoft.nebula.driver.graph.data.Node;
import com.vesoft.nebula.driver.graph.data.Path;
import com.vesoft.nebula.driver.graph.data.ResultSet;
import com.vesoft.nebula.driver.graph.data.ValueWrapper;
import com.vesoft.nebula.driver.graph.net.NebulaClient;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class NebulaClientDecodeTest {

    String addresses = ServerConstant.address;
    String user      = ServerConstant.user;
    String passwd    = ServerConstant.passwd;

    NebulaClient client = null;

    @Before
    public void setUp() {
        try {
            client = NebulaClient
                .builder(addresses, user, passwd)
                .build();
            client.execute("drop graph decode");
            client.execute("drop graph type decode_type");
            String createGraphType =
                "create graph type if not exists decode_type AS {"
                    + "node player(label player{id int32 primary key, age int32, "
                    + "name string, p_bool bool, p_float float32, p_double float64, "
                    + "p_date Date, p_datetime Local DateTime, p_time LOCAL TIME,"
                    + "p_zonedTime ZONED TIME, p_ZonedDT ZONED DATETIME,"
                    + "p_list LIST<STRING>}),"
                    + "node person(label person{id int32 primary key, high int32}),"
                    + "edge friend(person)-[label friend{degree int32}]->(person)}";
            ResultSet res = client.execute(createGraphType);
            assert res.isSucceeded();
            String createGraph = "create graph if not exists decode TYPED decode_type";
            res = client.execute(createGraph);
            assert res.isSucceeded();

            String insertNode1 = "use decode insert or ignore "
                + "(@player{id:1,age:10,name:\"Tom\",p_bool:true, p_float:1.0, p_double:2.0,"
                + "p_date:DATE(\"2024-01-01\"), "
                + "p_datetime:local_datetime(\"2024-01-01T12:01:10\"),"
                + "p_time:local_time(\"10:12:13\"),"
                + "p_zonedTime:zoned_time(\"10:12:12+0800\"),"
                + "p_ZonedDT:zoned_datetime(\"2024-12-12T10:00:00+0800\"),"
                + "p_list:[\"a\",\"b\"]}) ";
            res = client.execute(insertNode1);
            assert res.isSucceeded();

            String insertNode2 = "use decode insert or ignore (@person{id:2,high:20}) ";
            res = client.execute(insertNode2);

            String insertEdge = "table t{src,dst,degree}= "
                + "(2,2,11) "
                + "use decode for r in t "
                + "match(v1:person) where v1.id=r.src match(v2:person) where v2.id=r.dst "
                + "insert or ignore(v1)-[@friend{degree:r.degree}]->(v2)";
            res = client.execute(insertEdge);
            assert res.isSucceeded();

            res = client.execute("SESSION SET TIME ZONE \"Asia/Shanghai\"");
            assert res.isSucceeded();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("create NebulaClient failed:" + e.getMessage());
        }
    }

    @After
    public void teardown() {
        if (client != null) {
            client.close();
            client = null;
        }
    }

    @Test
    public void testConstVectorIntResult() {
        System.out.println("<==== testConstVectorResult ====>");
        try {
            ResultSet     res     = client.execute("for i in range(1,100) return 1 as c");
            List<Integer> rowList = new ArrayList<>();
            while (res.hasNext()) {
                List<ValueWrapper> values = res.next().values();
                rowList.add(values.get(0).asInt());
            }

            Assert.assertEquals(100, rowList.size());
            for (int i = 1; i <= 100; i++) {
                Assert.assertEquals(1, rowList.get(i - 1).intValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testConstVectorLongResult() {
        System.out.println("<==== testConstVectorLongResult ====>");
        try {
            ResultSet res = client.execute(
                "for i in range(1,100) return CAST(1 AS int64) as c");
            List<Long> rowList = new ArrayList<>();
            while (res.hasNext()) {
                List<ValueWrapper> values = res.next().values();
                rowList.add(values.get(0).asLong());
            }

            Assert.assertEquals(100, rowList.size());
            for (int i = 1; i <= 100; i++) {
                Assert.assertEquals(1L, rowList.get(i - 1).intValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }


    @Test
    public void testConstVectorStringResult() {
        System.out.println("<==== testConstVectorStringResult ====>");
        try {
            ResultSet    res     = client.execute("return \"abc\" as v");
            List<String> rowList = new ArrayList<>();
            while (res.hasNext()) {
                List<ValueWrapper> values = res.next().values();
                rowList.add(values.get(0).asString());
            }

            Assert.assertEquals(1, rowList.size());
            Assert.assertEquals("abc", rowList.get(0));

            res = client.execute("return \"中文\"");
            Assert.assertEquals("中文", res.next().get(0).asString());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testConstVectorDecimalResult() {
        System.out.println("<==== testConstVectorDecimalResult ====>");
        try {
            ResultSet  res     = client.execute("return -9223372036854775808");
            BigDecimal decimal = res.next().values().get(0).asDecimal();
            Assert.assertEquals("-9223372036854775808", decimal.toPlainString());

            res = client.execute("return 1.7976931348623159e+308D as t "
                                     + "next return cast(t as decimal)");
            ResultSet finalPosInfRes = res;
            Exception exception = assertThrows(RuntimeException.class, () ->
                finalPosInfRes.next().values().get(0).asDecimal());
            assertTrue(exception.getMessage().contains("+Inf"));

            res = client.execute("return -1.7976931348623159e+308D  as t "
                                     + "next return cast(t as decimal)");
            ResultSet finalNegInfRes = res;
            exception = assertThrows(RuntimeException.class, () ->
                finalNegInfRes.next().values().get(0).asDecimal());
            assertTrue(exception.getMessage().contains("-Inf"));

            res = client.execute("return cast(asin(radians(180)) as decimal) ");
            ResultSet finalNanRes = res;
            exception = assertThrows(RuntimeException.class, () ->
                finalNanRes.next().values().get(0).asDecimal());
            assertTrue(exception.getMessage().contains("NaN"));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testConstVectorBoolResult() {
        System.out.println("<==== testConstVectorBoolResult ====>");
        try {
            ResultSet     res      = client.execute("return true as t, false as f");
            List<Boolean> rowList1 = new ArrayList<>();
            List<Boolean> rowList2 = new ArrayList<>();
            while (res.hasNext()) {
                List<ValueWrapper> values = res.next().values();
                rowList1.add(values.get(0).asBoolean());
                rowList2.add(values.get(1).asBoolean());
            }

            Assert.assertEquals(1, rowList1.size());
            Assert.assertEquals(1, rowList2.size());
            Assert.assertEquals(true, rowList1.get(0));
            Assert.assertEquals(false, rowList2.get(0));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testConstVectorDateResult() {
        System.out.println("<==== testConstVectorDateResult ====>");
        try {
            ResultSet       res      = client.execute("return date(\"2024-01-01\")");
            List<LocalDate> dateList = new ArrayList<>();

            while (res.hasNext()) {
                List<ValueWrapper> values = res.next().values();
                if (values.get(0).isNull()) {
                    dateList.add(null);
                } else {
                    dateList.add(values.get(0).asDate());
                }
            }
            Assert.assertEquals(1, dateList.size());
            Assert.assertEquals(0, dateList.get(0).compareTo(LocalDate.of(2024, 1, 1)));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testConstVectorDateTimeResult() {
        System.out.println("<==== testConstVectorDateTimeResult ====>");
        try {
            ResultSet res = client.execute(
                "for i in range (1,100) "
                    + "return local_datetime(\"2024-01-01T12:00:00.0001\")");
            List<LocalDateTime> dateTimeList = new ArrayList<>();

            while (res.hasNext()) {
                List<ValueWrapper> values = res.next().values();
                if (values.get(0).isNull()) {
                    dateTimeList.add(null);
                } else {
                    dateTimeList.add(values.get(0).asLocalDateTime());
                }
            }
            Assert.assertEquals(100, dateTimeList.size());
            for (LocalDateTime localDateTime : dateTimeList) {
                Assert.assertEquals(2024, localDateTime.getYear());
                Assert.assertEquals(1, localDateTime.getMonthValue());
                Assert.assertEquals(1, localDateTime.getDayOfMonth());
                Assert.assertEquals(12, localDateTime.getHour());
                Assert.assertEquals(0, localDateTime.getMinute());
                Assert.assertEquals(0, localDateTime.getSecond());
                Assert.assertEquals(100 * 1000, localDateTime.getNano());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testConstVectorZonedTimeResult() {
        System.out.println("<==== testConstVectorZonedTimeResult ====>");
        try {
            ResultSet res = client.execute(
                "for i in range(1,100) return zoned_time(\"12:01:59.0001+0800\")");
            List<OffsetTime> timeList = new ArrayList<>();

            while (res.hasNext()) {
                List<ValueWrapper> values = res.next().values();
                if (values.get(0).isNull()) {
                    timeList.add(null);
                } else {
                    timeList.add(values.get(0).asZonedTime());
                }
            }
            Assert.assertEquals(100, timeList.size());
            for (OffsetTime zonedTime : timeList) {
                Assert.assertEquals(12, zonedTime.getHour());
                Assert.assertEquals(1, zonedTime.getMinute());
                Assert.assertEquals(59, zonedTime.getSecond());
                Assert.assertEquals(100 * 1000, zonedTime.getNano());
            }
            ZoneOffset zoneOffset = ZoneOffset.of("+0800");
            Assert.assertEquals(zoneOffset.getTotalSeconds(),
                                timeList.get(0).getOffset().getTotalSeconds());

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }


    @Test
    public void testConstVectorZonedDateTimeResult() {
        System.out.println("<==== testConstVectorZonedDateTimeResult ====>");
        try {
            ResultSet res = client.execute(
                "for i in range(1,100) "
                    + "return zoned_datetime(\"2024-01-29T12:01:59.0001+0800\")");
            List<ZonedDateTime> datetimeList = new ArrayList<>();

            while (res.hasNext()) {
                List<ValueWrapper> values = res.next().values();
                if (values.get(0).isNull()) {
                    datetimeList.add(null);
                } else {
                    datetimeList.add(values.get(0).asZonedDateTime());
                }
            }

            Assert.assertEquals(100, datetimeList.size());
            for (ZonedDateTime dateTime : datetimeList) {
                Assert.assertEquals(2024, dateTime.getYear());
                Assert.assertEquals(1, dateTime.getMonthValue());
                Assert.assertEquals(29, dateTime.getDayOfMonth());
                Assert.assertEquals(1, dateTime.getMinute());
                Assert.assertEquals(59, dateTime.getSecond());
                Assert.assertEquals(100 * 1000, dateTime.getNano());
            }
            ZoneOffset zoneOffset = ZoneOffset.of("+0800");
            Assert.assertEquals(zoneOffset.getTotalSeconds(),
                                datetimeList.get(0).getOffset().getTotalSeconds());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testConstVectorDurationResult() {
        System.out.println("<==== testConstVectorDurationResult ====>");
        try {
            ResultSet    res   = client.execute("for i in range(1,100) return duration \"P1Y\"");
            int          count = 0;
            ValueWrapper value;
            while (res.hasNext()) {
                count++;
                value = res.next().get(0);
                if (value.isNull()) {
                    assert false;
                } else {
                    Assert.assertEquals("P1Y", value.asDuration().toString());
                }
            }
            Assert.assertEquals(100, count);

            count = 0;
            res = client.execute("for i in range(1,100) return duration \"PT1H2M3S\"");
            while (res.hasNext()) {
                count++;
                value = res.next().get(0);
                if (value.isNull()) {
                    assert false;
                } else {
                    Assert.assertEquals("PT1H2M3S", value.asDuration().toString());
                }
            }
            Assert.assertEquals(100, count);

            count = 0;
            res = client.execute("for i in range(1,100) return duration \"P1D\"");
            while (res.hasNext()) {
                count++;
                value = res.next().get(0);
                if (value.isNull()) {
                    assert false;
                } else {
                    Assert.assertEquals("P1D", value.asDuration().toString());
                }
            }
            Assert.assertEquals(100, count);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testConstVectorListResult() {
        System.out.println("<==== testConstVectorListResult ====>");
        try {
            // test list<Integer>
            String                   gql    = "for i in range (1,100) return LIST[0,1,2,3,4]";
            ResultSet                res    = client.execute(gql);
            List<List<ValueWrapper>> values = new ArrayList<>();
            while (res.hasNext()) {
                ValueWrapper value = res.next().get(0);
                if (value.isNull()) {
                    Assert.fail();
                } else {
                    values.add(value.asList());
                }
            }
            Assert.assertEquals(100, values.size());
            for (List<ValueWrapper> list : values) {
                Assert.assertEquals(5, list.size());
                Assert.assertTrue(list.contains(
                    new ValueWrapper(0, ColumnType.COLUMN_TYPE_INT32)));
                Assert.assertTrue(list.contains(
                    new ValueWrapper(1, ColumnType.COLUMN_TYPE_INT32)));
                Assert.assertTrue(list.contains(
                    new ValueWrapper(2, ColumnType.COLUMN_TYPE_INT32)));
                Assert.assertTrue(list.contains(
                    new ValueWrapper(3, ColumnType.COLUMN_TYPE_INT32)));
                Assert.assertTrue(list.contains(
                    new ValueWrapper(4, ColumnType.COLUMN_TYPE_INT32)));
            }

            // test list<String>
            values.clear();
            gql = "for i in range (1,100) return LIST[\"a\",\"b\",\"c\",\"d\",\"e\"]";
            res = client.execute(gql);
            while (res.hasNext()) {
                ValueWrapper value = res.next().get(0);
                if (value.isNull()) {
                    Assert.fail();
                } else {
                    values.add(value.asList());
                }
            }
            Assert.assertEquals(100, values.size());
            for (List<ValueWrapper> list : values) {
                Assert.assertEquals(5, list.size());
                Assert.assertTrue(list.contains(
                    new ValueWrapper("a", ColumnType.COLUMN_TYPE_STRING)));
                Assert.assertTrue(list.contains(
                    new ValueWrapper("b", ColumnType.COLUMN_TYPE_STRING)));
                Assert.assertTrue(list.contains(
                    new ValueWrapper("c", ColumnType.COLUMN_TYPE_STRING)));
                Assert.assertTrue(list.contains(
                    new ValueWrapper("d", ColumnType.COLUMN_TYPE_STRING)));
                Assert.assertTrue(list.contains(
                    new ValueWrapper("e", ColumnType.COLUMN_TYPE_STRING)));
            }

            // test list<boolean>
            values.clear();
            gql = "return LIST[true,false]";
            res = client.execute(gql);
            Assert.assertEquals(1, res.rowSize());
            List<ValueWrapper> value = res.next().get(0).asList();
            Assert.assertEquals(2, value.size());
            Assert.assertTrue(value.get(0).asBoolean());
            Assert.assertFalse(value.get(1).asBoolean());

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testConstVectorRecordResult() {
        System.out.println("<==== testConstVectorRecordResult ====>");
        try {
            String             gql    = "LET r={a:1, b:true, c:\"str literal\"} return r as r1";
            ResultSet          res    = client.execute(gql);
            List<ValueWrapper> values = res.next().values();
            Assert.assertEquals(1, values.size());
            Assert.assertEquals(1, values.get(0).asRecord().getValue("a").asInt());
            Assert.assertTrue(values.get(0).asRecord().getValue("b").asBoolean());
            Assert.assertEquals("str literal",
                                values.get(0).asRecord().getValue("c").asString());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testConstVectorVectorResult() {
        System.out.println("<==== testConstVectorVectorResult ====>");
        try {
            String             gql    = "LET r=VECTOR<3,FLOAT32>([1.0,2.0,3.0]) return r as r1";
            ResultSet          res    = client.execute(gql);
            List<ValueWrapper> values = res.next().values();
            Assert.assertEquals(1, values.size());
            Assert.assertEquals(3, values.get(0).asVector().size());
            List<Float> expectList = Arrays.asList(1.0f, 2.0f, 3.0f);
            Assert.assertTrue(expectList.stream().allMatch(values.get(0)
                                                               .asVector()
                                                               .getValues()::contains));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testConstVectorGeoPointResult() {
        System.out.println("<==== testConstVectorGeoPointResult ====>");
        try {
            String gql =
                "RETURN ST_GeogFromText(\"POINT(24.7 36.842)\") AS p";
            ResultSet          res    = client.execute(gql);
            List<ValueWrapper> values = res.next().values();
            Assert.assertEquals(1, values.size());
            Assert.assertTrue(values.get(0).isGeography());
            Assert.assertEquals(GeoShape.GeoShapePoint, values.get(0).asGeography().getShape());
            Assert.assertEquals("POINT(24.7 36.842)", values.get(0).asGeography().toString());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testConstVectorGeoLineStringResult() {
        System.out.println("<==== testConstVectorGeoLineStringResult ====>");
        try {
            String gql = "RETURN ST_GeogFromText("
                + "\"LINESTRING(-122.416667 37.783333, -122.383333 37.766667)\") AS p";
            ResultSet          res    = client.execute(gql);
            List<ValueWrapper> values = res.next().values();
            Assert.assertEquals(1, values.size());
            Assert.assertTrue(values.get(0).isGeography());
            Assert.assertEquals(GeoShape.GeoShapeLineString,
                                values.get(0).asGeography().getShape());
            Assert.assertEquals("LINESTRING(-122.416667 37.783333,-122.383333 37.766667)",
                                values.get(0).asGeography().toString());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testConstVectorGeoPolygonResult() {
        System.out.println("<==== testConstVectorGeoPolygonResult ====>");
        try {
            String gql =
                "RETURN ST_GeogFromText(\"POLYGON((0 0, 0 10, 10 10, 10 0, 0 0), "
                    + "(2 2, 2 4, 4 4, 4 2, 2 2))\") AS p";
            ResultSet          res    = client.execute(gql);
            List<ValueWrapper> values = res.next().values();
            Assert.assertEquals(1, values.size());
            Assert.assertTrue(values.get(0).isGeography());
            Assert.assertEquals(GeoShape.GeoShapePolygon, values.get(0).asGeography().getShape());
            Assert.assertEquals("POLYGON((0.0 0.0,0.0 10.0,10.0 10.0,10.0 0.0,0.0 0.0),"
                                    + "(2.0 2.0,2.0 4.0,4.0 4.0,4.0 2.0,2.0 2.0))",
                                values.get(0).asGeography().toString());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testDecodeIntResult() {
        System.out.println("<==== testDecodeIntResult ====>");
        try {
            ResultSet res = client.execute(
                "let a=1,b=2 for i in range(1,10000) return a+i as c, b+i as d ");
            List<Integer> valuesC = new ArrayList<>();
            List<Integer> valuesD = new ArrayList<>();
            while (res.hasNext()) {
                List<ValueWrapper> values = res.next().values();
                valuesC.add(values.get(0).asInt());
                valuesD.add(values.get(1).asInt());
            }

            Assert.assertEquals(10000, valuesC.size());
            Assert.assertEquals(10000, valuesD.size());
            for (int i = 1; i <= 10000; i++) {
                Assert.assertEquals(i + 1, valuesC.get(i - 1).intValue());
                Assert.assertEquals(i + 2, valuesD.get(i - 1).intValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testDecodeIntResultWithNull() {
        System.out.println("<==== testDecodeIntResult ====>");
        try {
            ResultSet res = client.execute(
                "for i in range(1, 100) return case when i < 50 then 32 end as c");
            List<Integer> valuesC = new ArrayList<>();
            while (res.hasNext()) {
                List<ValueWrapper> values = res.next().values();
                if (values.get(0).isNull()) {
                    valuesC.add(null);
                } else {
                    valuesC.add(values.get(0).asInt());
                }
            }

            Assert.assertEquals(100, valuesC.size());
            Assert.assertEquals(32, valuesC.get(0).intValue());
            for (int i = 0; i < 49; i++) {
                Assert.assertEquals(32, valuesC.get(i).intValue());
            }
            for (int i = 49; i < 100; i++) {
                Assert.assertNull(valuesC.get(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testDecodeNodeResult() {
        System.out.println("<==== testDecodeNodeResult ====>");
        try {
            ResultSet  res   = client.execute("use decode match(v) return v");
            List<Node> nodes = new ArrayList<>();

            while (res.hasNext()) {
                List<ValueWrapper> values = res.next().values();
                if (values.get(0).isNull()) {
                    nodes.add(null);
                } else {
                    nodes.add(values.get(0).asNode());
                }
            }
            Assert.assertEquals(2, nodes.size());
            for (Node node : nodes) {
                if (node.getType().equals("player")) {
                    Assert.assertEquals(12, node.getProperties().size());
                    Assert.assertEquals(1, node.getProperties().get("id").asInt());
                    Assert.assertEquals(10, node.getProperties().get("age").asInt());
                    Assert.assertEquals("Tom", node.getProperties().get("name").asString());
                    Assert.assertTrue(node.getProperties().get("p_bool").asBoolean());
                    Assert.assertEquals(1.0,
                                        node.getProperties().get("p_float").asFloat(),
                                        0.001);
                    Assert.assertEquals(2.0,
                                        node.getProperties().get("p_double").asDouble(),
                                        0.001);
                    Assert.assertEquals(LocalDate.of(2024, 1, 1),
                                        node.getProperties().get("p_date").asDate());
                    Assert.assertEquals(LocalDateTime.of(2024, 1, 1, 12, 1, 10),
                                        node.getProperties().get("p_datetime").asLocalDateTime());
                    Assert.assertEquals(LocalTime.of(10, 12, 13),
                                        node.getProperties().get("p_time").asLocalTime());
                    ZoneOffset offset = ZoneOffset.of("+0800");
                    Assert.assertEquals(OffsetTime.of(LocalTime.of(10, 12, 12), offset),
                                        node.getProperties().get("p_zonedTime").asZonedTime());
                    ZonedDateTime expectzdt = ZonedDateTime.of(
                        LocalDateTime.of(2024, 12, 12, 10, 0, 0), offset);
                    Assert.assertEquals(expectzdt,
                                        node.getProperties().get("p_ZonedDT").asZonedDateTime());

                    Assert.assertEquals(2,
                                        node.getProperties().get("p_list").asList().size());
                }
                if (node.getType().equals("person")) {
                    Assert.assertEquals(2, node.getProperties().size());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testDecodeEdgeResult() {
        System.out.println("<==== testDecodeEdgeResult ====>");
        try {
            ResultSet res = client.execute(
                "use decode match(v)-[e@friend]->(v1) return e");
            List<Edge> edges = new ArrayList<>();

            while (res.hasNext()) {
                List<ValueWrapper> values = res.next().values();
                if (values.get(0).isNull()) {
                    edges.add(null);
                } else {
                    edges.add(values.get(0).asEdge());
                }
            }
            Assert.assertEquals(1, edges.size());
            Assert.assertEquals(1, edges.get(0).getProperties().size());

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testDecodePathResult() {
        System.out.println("<==== testDecodePathResult ====>");
        try {
            ResultSet res = client.execute(
                "use decode match p=(v)-[e@friend]->(v1) return p");
            List<Path> paths = new ArrayList<>();

            while (res.hasNext()) {
                List<ValueWrapper> values = res.next().values();
                if (values.get(0).isNull()) {
                    paths.add(null);
                } else {
                    paths.add(values.get(0).asPath());
                }
            }
            Assert.assertEquals(1, paths.size());
            Assert.assertEquals(3, paths.get(0).values().size());
            Node vertex1 = paths.get(0).values().get(0).asNode();
            Node vertex2 = paths.get(0).values().get(2).asNode();
            Assert.assertEquals(2, vertex1.getProperties().size());
            Assert.assertEquals(vertex1.getId(), vertex2.getId());

            // test path with one node
            res = client.execute("use decode match p=(v:person) return p");
            paths.clear();
            while (res.hasNext()) {
                List<ValueWrapper> valueWrappers = res.next().values();
                if (valueWrappers.get(0).isNull()) {
                    paths.add(null);
                } else {
                    paths.add(valueWrappers.get(0).asPath());
                }
            }
            Assert.assertEquals(1, paths.size());
            Assert.assertEquals(1, paths.get(0).nodes().size());
            Assert.assertEquals(0, paths.get(0).edges().size());
            System.out.println(paths.get(0).toString());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testDecodeStringResult() {
        System.out.println("<==== testDecodeStringResult ====>");
        try {
            ResultSet res = client.execute(
                "let a=\"abcdefghij\" for i in range(1,10000) "
                    + "return a || cast(i as STRING) as c");
            List<String> strs = new ArrayList<>();

            while (res.hasNext()) {
                List<ValueWrapper> values = res.next().values();
                if (values.get(0).isNull()) {
                    strs.add(null);
                } else {
                    strs.add(values.get(0).asString());
                }
            }
            Assert.assertEquals(10000, strs.size());
            String prefix = "abcdefghij";
            for (int i = 1; i <= 10000; i++) {
                Assert.assertEquals(prefix + i, strs.get(i - 1));
            }

            strs.clear();
            res = client.execute(
                "let a=\"中文\" for i in range(1,10) return a || cast(i as STRING) as c");
            while (res.hasNext()) {
                List<ValueWrapper> values = res.next().values();
                if (values.get(0).isNull()) {
                    strs.add(null);
                } else {
                    strs.add(values.get(0).asString());
                }
            }
            Assert.assertEquals(10, strs.size());
            prefix = "中文";
            for (int i = 1; i <= 10; i++) {
                Assert.assertEquals(prefix + i, strs.get(i - 1));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }


    @Test
    public void testDecodeDecimalResult() {
        System.out.println("<==== testDecodeDecimalResult ====>");
        try {
            ResultSet  res     = client.execute("let a=-9223372036854775808 return a");
            BigDecimal decimal = res.next().values().get(0).asDecimal();
            Assert.assertEquals("-9223372036854775808", decimal.toPlainString());

            res = client.execute("let a=1.7976931348623159e+308D return cast(a as decimal)");
            ResultSet finalPosInfRes = res;
            Exception exception = assertThrows(RuntimeException.class, () ->
                finalPosInfRes.next().values().get(0).asDecimal());
            assertTrue(exception.getMessage().contains("+Inf"));

            res = client.execute("let a=-1.7976931348623159e+308D return cast(a as decimal)");
            ResultSet finalNegInfRes = res;
            exception = assertThrows(RuntimeException.class, () ->
                finalNegInfRes.next().values().get(0).asDecimal());
            assertTrue(exception.getMessage().contains("-Inf"));

            res = client.execute("let a=asin(radians(180)) return cast(a as decimal) ");
            ResultSet finalNanRes = res;
            exception = assertThrows(RuntimeException.class, () ->
                finalNanRes.next().values().get(0).asDecimal());
            assertTrue(exception.getMessage().contains("NaN"));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testDecodeDateResult() {
        System.out.println("<==== testDecodeDateResult ====>");
        try {
            ResultSet res = client.execute(
                "let a=\"2024-01-01\" for i in range(1,100) return date(a)");
            List<LocalDate> dateList = new ArrayList<>();

            while (res.hasNext()) {
                List<ValueWrapper> values = res.next().values();
                if (values.get(0).isNull()) {
                    dateList.add(null);
                } else {
                    dateList.add(values.get(0).asDate());
                }
            }
            Assert.assertEquals(100, dateList.size());
            for (LocalDate date : dateList) {
                Assert.assertEquals(0, date.compareTo(LocalDate.of(2024, 1, 1)));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testDecodeLocalDateTimeResult() {
        System.out.println("<==== testDecodeLocalDateTimeResult ====>");
        try {
            ResultSet res = client.execute(
                "let a=\"2024-01-01T12:00:00.0001\" for i in range(1,100) "
                    + "return local_datetime(a)");
            List<LocalDateTime> dateTimeList = new ArrayList<>();

            while (res.hasNext()) {
                List<ValueWrapper> values = res.next().values();
                if (values.get(0).isNull()) {
                    dateTimeList.add(null);
                } else {
                    dateTimeList.add(values.get(0).asLocalDateTime());
                }
            }
            Assert.assertEquals(100, dateTimeList.size());
            for (LocalDateTime localDateTime : dateTimeList) {
                Assert.assertEquals(2024, localDateTime.getYear());
                Assert.assertEquals(1, localDateTime.getMonthValue());
                Assert.assertEquals(1, localDateTime.getDayOfMonth());
                Assert.assertEquals(12, localDateTime.getHour());
                Assert.assertEquals(0, localDateTime.getMinute());
                Assert.assertEquals(0, localDateTime.getSecond());
                Assert.assertEquals(100 * 1000, localDateTime.getNano());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }


    @Test
    public void testDecodeLocalTimeResult() {
        System.out.println("<==== testDecodeLocalTimeResult ====>");
        try {
            ResultSet       res      = client.execute("return local_time(\"12:01:59.0001\")");
            List<LocalTime> timeList = new ArrayList<>();

            while (res.hasNext()) {
                List<ValueWrapper> values = res.next().values();
                if (values.get(0).isNull()) {
                    timeList.add(null);
                } else {
                    timeList.add(values.get(0).asLocalTime());
                }
            }
            Assert.assertEquals(12, timeList.get(0).getHour());
            Assert.assertEquals(1, timeList.get(0).getMinute());
            Assert.assertEquals(59, timeList.get(0).getSecond());
            Assert.assertEquals(100 * 1000, timeList.get(0).getNano());

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testDecodeZonedTimeResult() {
        System.out.println("<==== testDecodeZonedTimeResult ====>");
        try {
            ResultSet res = client.execute(
                "return zoned_time(\"12:01:59.0001+0800\")");
            List<OffsetTime> timeList = new ArrayList<>();

            while (res.hasNext()) {
                List<ValueWrapper> values = res.next().values();
                if (values.get(0).isNull()) {
                    timeList.add(null);
                } else {
                    timeList.add(values.get(0).asZonedTime());
                }
            }
            Assert.assertEquals(12, timeList.get(0).getHour());
            Assert.assertEquals(1, timeList.get(0).getMinute());
            Assert.assertEquals(59, timeList.get(0).getSecond());
            Assert.assertEquals(100 * 1000, timeList.get(0).getNano());
            ZoneOffset zoneOffset = ZoneOffset.of("+0800");
            Assert.assertEquals(zoneOffset.getTotalSeconds(),
                                timeList.get(0).getOffset().getTotalSeconds());
            Assert.assertEquals("12:01:59.000100+08:00", timeList.get(0).toString());

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }


    @Test
    public void testDecodeZonedDateTimeResult() {
        System.out.println("<==== testDecodeZonedDateTimeResult ====>");
        try {
            ResultSet res = client.execute(
                "return zoned_datetime(\"2024-01-29T12:01:59.0001+0800\")");
            List<ZonedDateTime> datetimeList = new ArrayList<>();

            while (res.hasNext()) {
                List<ValueWrapper> values = res.next().values();
                if (values.get(0).isNull()) {
                    datetimeList.add(null);
                } else {
                    datetimeList.add(values.get(0).asZonedDateTime());
                }
            }

            Assert.assertEquals(2024, datetimeList.get(0).getYear());
            Assert.assertEquals(1, datetimeList.get(0).getMonthValue());
            Assert.assertEquals(29, datetimeList.get(0).getDayOfMonth());
            Assert.assertEquals(12, datetimeList.get(0).getHour());
            Assert.assertEquals(1, datetimeList.get(0).getMinute());
            Assert.assertEquals(59, datetimeList.get(0).getSecond());
            Assert.assertEquals(100 * 1000, datetimeList.get(0).getNano());
            ZoneOffset zoneOffset = ZoneOffset.of("+0800");
            Assert.assertEquals(zoneOffset.getTotalSeconds(),
                                datetimeList.get(0).getOffset().getTotalSeconds());
            Assert.assertEquals("2024-01-29T12:01:59.000100+08:00",
                                datetimeList.get(0).toString());

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testDecodeDurationResult() {
        System.out.println("<==== testDecodeDurationResult ====>");
        try {
            ResultSet    res   = client.execute("return duration \"P1Y\"");
            ValueWrapper value = res.next().values().get(0);
            if (value.isNull()) {
                assert false;
            } else {
                Assert.assertEquals("P1Y", value.asDuration().toString());
            }

            res = client.execute("return duration \"PT1H2M3S\"");
            value = res.next().get(0);
            if (value.isNull()) {
                assert false;
            } else {
                Assert.assertEquals("PT1H2M3S", value.asDuration().toString());
            }

            res = client.execute("return duration \"P1D\"");
            value = res.next().get(0);
            if (value.isNull()) {
                assert false;
            } else {
                Assert.assertEquals("P1D", value.asDuration().toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }


    @Test
    public void testDecodeListResult() {
        System.out.println("<==== testDecodeListResult ====>");
        try {
            String gql = "LET l=[0,1,2,3,4] RETURN "
                + "l[0:2] AS a, l[0::2] AS b, l[::] AS c, l[::-2] AS d, "
                + "l[-1:-3:-1] AS e, l[1:0] AS f, l[1:1] AS g, l[6:10] AS h";
            ResultSet          res    = client.execute(gql);
            List<ValueWrapper> values = res.next().values();
            Assert.assertEquals(8, values.size());
            ValueWrapper value = values.get(0);
            Assert.assertEquals(3, value.asList().size());
            Assert.assertTrue(value.asList().contains(
                new ValueWrapper(0, ColumnType.COLUMN_TYPE_INT32)));
            Assert.assertTrue(value.asList().contains(
                new ValueWrapper(1, ColumnType.COLUMN_TYPE_INT32)));
            Assert.assertTrue(value.asList().contains(
                new ValueWrapper(2, ColumnType.COLUMN_TYPE_INT32)));

            value = values.get(1);
            Assert.assertEquals(3, value.asList().size());
            Assert.assertTrue(value.asList().contains(
                new ValueWrapper(0, ColumnType.COLUMN_TYPE_INT32)));
            Assert.assertTrue(value.asList().contains(
                new ValueWrapper(2, ColumnType.COLUMN_TYPE_INT32)));
            Assert.assertTrue(value.asList().contains(
                new ValueWrapper(4, ColumnType.COLUMN_TYPE_INT32)));

            value = values.get(2);
            Assert.assertEquals(5, value.asList().size());
            Assert.assertTrue(value.asList().contains(
                new ValueWrapper(0, ColumnType.COLUMN_TYPE_INT32)));
            Assert.assertTrue(value.asList().contains(
                new ValueWrapper(1, ColumnType.COLUMN_TYPE_INT32)));
            Assert.assertTrue(value.asList().contains(
                new ValueWrapper(2, ColumnType.COLUMN_TYPE_INT32)));
            Assert.assertTrue(value.asList().contains(
                new ValueWrapper(3, ColumnType.COLUMN_TYPE_INT32)));
            Assert.assertTrue(value.asList().contains(
                new ValueWrapper(4, ColumnType.COLUMN_TYPE_INT32)));

            value = values.get(3);
            Assert.assertEquals(3, value.asList().size());
            Assert.assertTrue(value.asList().contains(
                new ValueWrapper(4, ColumnType.COLUMN_TYPE_INT32)));
            Assert.assertTrue(value.asList().contains(
                new ValueWrapper(2, ColumnType.COLUMN_TYPE_INT32)));
            Assert.assertTrue(value.asList().contains(
                new ValueWrapper(0, ColumnType.COLUMN_TYPE_INT32)));

            value = values.get(4);
            Assert.assertEquals(3, value.asList().size());
            Assert.assertTrue(value.asList().contains(
                new ValueWrapper(4, ColumnType.COLUMN_TYPE_INT32)));
            Assert.assertTrue(value.asList().contains(
                new ValueWrapper(3, ColumnType.COLUMN_TYPE_INT32)));
            Assert.assertTrue(value.asList().contains(
                new ValueWrapper(2, ColumnType.COLUMN_TYPE_INT32)));

            value = values.get(5);
            Assert.assertEquals(0, value.asList().size());

            value = values.get(6);
            Assert.assertEquals(1, value.asList().size());
            Assert.assertEquals(1, value.asList().get(0).asInt());

            value = values.get(7);
            Assert.assertEquals(0, value.asList().size());


        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }


    @Test
    public void testDecodeRecordResult() {
        System.out.println("<==== testDecodeRecordResult ====>");
        try {
            String             gql    = "LET a1=1,b1=true,c1=\"str\" return{a:a1, b:b1, c:c1} as r";
            ResultSet          res    = client.execute(gql);
            List<ValueWrapper> values = res.next().values();
            Assert.assertEquals(1, values.size());
            Assert.assertEquals(3, values.get(0).asRecord().size());
            Assert.assertEquals(1, values.get(0).asRecord().getValue("a").asInt());
            Assert.assertEquals(true, values
                .get(0)
                .asRecord().getValue("b")
                .asBoolean());
            Assert.assertEquals("str", values
                .get(0)
                .asRecord()
                .getValue("c")
                .asString());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }


    @Test
    public void testDecodeEmbeddingVectorResult() {
        System.out.println("<==== testDecodeEmbeddingVectorResult ====>");
        try {
            ResultSet res = client.execute(
                "CREATE GRAPH TYPE IF NOT EXISTS sdk_vector_test_type AS {"
                    + "node type p(LABEL p{id INT32 PRIMARY KEY, vec VECTOR<3,FLOAT32>})}");
            Assert.assertTrue(res.isSucceeded());
            res = client.execute(
                "CREATE GRAPH IF NOT EXISTS sdk_vector_test sdk_vector_test_type");
            Assert.assertTrue(res.isSucceeded());
            res = client.execute("USE sdk_vector_test INSERT OR IGNORE "
                                     + "(@p{id:1,vec:VECTOR<3,FLOAT32>([1.0,2.0,3.0])})");
            Assert.assertTrue(res.isSucceeded());
            String gql = "USE sdk_vector_test MATCH(v) RETURN v.vec";
            res = client.execute(gql);
            List<ValueWrapper> values = res.next().values();
            Assert.assertEquals(1, values.size());
            Assert.assertEquals(3, values.get(0).asVector().size());
            List<Float> expectList = Arrays.asList(1.0f, 2.0f, 3.0f);
            Assert.assertTrue(expectList.stream().allMatch(values.get(0)
                                                               .asVector()
                                                               .getValues()::contains));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }


    @Test
    public void testDecodeAnyResult() {
        System.out.println("<==== testDecodeAnyResult ====>");
        try {
            ResultSet r = client.execute("DESCRIBE graph type decode_type");
            while (r.hasNext()) {
                r.next();
            }
            // set parameters with different data type
            ResultSet res = client.execute("SESSION SET value $a=true");
            Assert.assertTrue(res.isSucceeded());
            res = client.execute("SESSION SET value $b=1");
            Assert.assertTrue(res.isSucceeded());
            res = client.execute("SESSION SET value $c=CAST(2 AS INT64)");
            Assert.assertTrue(res.isSucceeded());
            res = client.execute("SESSION SET value $d=CAST(1.0 AS FLOAT)");
            Assert.assertTrue(res.isSucceeded());
            res = client.execute("SESSION SET value $e=CAST(2.0 AS DOUBLE)");
            Assert.assertTrue(res.isSucceeded());
            res = client.execute("SESSION SET value $f=\"test\"");
            Assert.assertTrue(res.isSucceeded());
            res = client.execute("SESSION SET value $g=date(\"2024-01-01\")");
            Assert.assertTrue(res.isSucceeded());
            res = client.execute("SESSION SET value $h=local_datetime(\"2024-01-01T12:01:01\")");
            Assert.assertTrue(res.isSucceeded());
            res = client.execute(
                "SESSION SET value $i=zoned_datetime(\"2024-01-01T12:01:02+0800\")");
            Assert.assertTrue(res.isSucceeded());
            res = client.execute("SESSION SET value $j=local_time(\"10:10:10\")");
            Assert.assertTrue(res.isSucceeded());
            res = client.execute("SESSION SET value $k=zoned_time(\"10:10:20+0800\")");
            Assert.assertTrue(res.isSucceeded());

            res = client.execute("SESSION SET value $l=List[1,2]");
            Assert.assertTrue(res.isSucceeded());

            res = client.execute("SESSION SET value $m=RECORD{a:1,"
                                     + "b:1.0,"
                                     + "c:false,"
                                     + "d:\"test\","
                                     + "e:date(\"2024-01-01\"),"
                                     + "f:local_datetime(\"2024-01-01T10:10:10\")}");
            Assert.assertTrue(res.isSucceeded());

            res = client.execute("SESSION SET value $n=List[\"a\",\"b\",\"中文\"]");
            Assert.assertTrue(res.isSucceeded());

            res = client.execute("SESSION SET value $o=VECTOR<3,FLOAT>([1.0,2.0,3.0])");
            Assert.assertTrue(res.isSucceeded());

            res = client.execute("SHOW PARAMETERS");
            List<ResultSet.Record> records = new ArrayList<>();
            while (res.hasNext()) {
                records.add(res.next());
            }

            for (int i = 0; i < records.size(); i++) {
                ValueWrapper param = records.get(i).get(0);
                ValueWrapper value = records.get(i).get(1);
                switch (param.asString()) {
                    case "a":
                        Assert.assertTrue(value.asBoolean());
                        break;
                    case "b":
                        Assert.assertEquals(1, value.asInt());
                        break;
                    case "c":
                        Assert.assertEquals(2L, value.asLong());
                        break;
                    case "d":
                        Assert.assertEquals(1.0, value.asFloat(), 0.001);
                        break;
                    case "e":
                        Assert.assertEquals(2.0, value.asDouble(), 0.001);
                        break;
                    case "f":
                        Assert.assertEquals("test", value.asString());
                        break;
                    case "g":
                        Assert.assertEquals(LocalDate.of(2024, 1, 1), value.asDate());
                        break;
                    case "h":
                        LocalDateTime localDateTime = value.asLocalDateTime();
                        Assert.assertEquals(2024, localDateTime.getYear());
                        Assert.assertEquals(1, localDateTime.getMonthValue());
                        Assert.assertEquals(1, localDateTime.getDayOfMonth());
                        Assert.assertEquals(12, localDateTime.getHour());
                        Assert.assertEquals(1, localDateTime.getMinute());
                        Assert.assertEquals(1, localDateTime.getSecond());
                        break;
                    case "i":
                        //2024-01-01T12:01:02+0800
                        ZonedDateTime zonedDateTime = value.asZonedDateTime();
                        Assert.assertEquals(2024, zonedDateTime.getYear());
                        Assert.assertEquals(1, zonedDateTime.getMonthValue());
                        Assert.assertEquals(1, zonedDateTime.getDayOfMonth());
                        Assert.assertEquals(12, zonedDateTime.getHour());
                        Assert.assertEquals(1, zonedDateTime.getMinute());
                        Assert.assertEquals(2, zonedDateTime.getSecond());
                        Assert.assertEquals(ZoneOffset.ofHours(8).getTotalSeconds(),
                                            zonedDateTime.getOffset().getTotalSeconds());
                        break;
                    case "j":
                        LocalTime localTime = value.asLocalTime();
                        Assert.assertEquals(10, localTime.getHour());
                        Assert.assertEquals(10, localTime.getMinute());
                        Assert.assertEquals(10, localTime.getSecond());
                        break;
                    case "k":
                        //10:10:20+0800
                        OffsetTime zonedTime = value.asZonedTime();
                        Assert.assertEquals(10, zonedTime.getHour());
                        Assert.assertEquals(10, zonedTime.getMinute());
                        Assert.assertEquals(20, zonedTime.getSecond());
                        Assert.assertEquals(ZoneOffset.ofHours(8).getTotalSeconds(),
                                            zonedTime.getOffset().getTotalSeconds());
                        break;
                    case "l":
                        List<ValueWrapper> values = value.asList();
                        Assert.assertEquals(2, values.size());
                        Assert.assertTrue(values.contains(
                            new ValueWrapper(1, ColumnType.COLUMN_TYPE_INT32)));
                        Assert.assertTrue(values.contains(
                            new ValueWrapper(2, ColumnType.COLUMN_TYPE_INT32)));
                        break;
                    case "m":
                        NRecord record = value.asRecord();
                        Assert.assertEquals(6, record.size());
                        Assert.assertEquals(1, record.getValue("a").asInt());
                        Assert.assertEquals("1.0", record.getValue("b")
                            .asDecimal()
                            .toPlainString());
                        Assert.assertFalse(record.getValue("c").asBoolean());
                        Assert.assertEquals("test", record.getValue("d").asString());
                        Assert.assertEquals(LocalDate.of(2024, 1, 1),
                                            record.getValue("e").asDate());
                        Assert.assertEquals(LocalDateTime.of(2024, 1, 1, 10, 10, 10),
                                            record.getValue("f").asLocalDateTime());
                        break;
                    case "n":
                        List<ValueWrapper> listValues = value.asList();
                        Assert.assertEquals(3, listValues.size());
                        Assert.assertTrue(listValues.contains(
                            new ValueWrapper("a", ColumnType.COLUMN_TYPE_STRING)));
                        Assert.assertTrue(listValues.contains(
                            new ValueWrapper("b", ColumnType.COLUMN_TYPE_STRING)));
                        Assert.assertTrue(listValues.contains(
                            new ValueWrapper("中文", ColumnType.COLUMN_TYPE_STRING)));
                        break;
                    case "o":
                        EmbeddingVector vector = value.asVector();
                        Assert.assertEquals(3, vector.size());
                        List<Float> expectList = Arrays.asList(1.0f, 2.0f, 3.0f);
                        Assert.assertTrue(expectList.stream()
                                              .allMatch(vector.getValues()::contains));
                        break;
                    default:
                        System.out.println("not defined.");
                        Assert.fail("not defined.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }


    @Test
    public void testDecodeAnyNodeResult() {
        System.out.println("<==== TestDecodeAnyNodeResult ====>");
        try {
            List<ResultSet.Record> records = new ArrayList<>();
            ResultSet              res;
            String format = "CALL cursor_node_scan(\"decode\",\"player\","
                + "LIST[\"id\"], %d, \"\", 10) return *";

            for (int part = 1; part <= 10; part++) {
                String gql = String.format(format, part);
                res = client.execute(gql);
                Assert.assertTrue(res.isSucceeded());
                while (res.hasNext()) {
                    records.add(res.next());
                }
            }

            Assert.assertEquals(1, records.size());
            Node v = records.get(0).get(0).asNode();
            Assert.assertEquals("player", v.getType());

        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }


    @Test
    public void testDecodeAnyEdgeResult() {
        System.out.println("<==== TestDecodeAnyEdgeResult ====>");
        try {
            List<ResultSet.Record> records = new ArrayList<>();
            ResultSet              res;
            String format = "CALL cursor_edge_scan(\"decode\",\"friend\","
                + "LIST[\"degree\"], %d, \"\", 10) return *";

            for (int part = 1; part <= 10; part++) {
                String gql = String.format(format, part);
                res = client.execute(gql);
                Assert.assertTrue(res.isSucceeded());
                while (res.hasNext()) {
                    records.add(res.next());
                }
            }

            Assert.assertEquals(1, records.size());
            Node srcNode = records.get(0).get(0).asNode();
            Assert.assertEquals("person", srcNode.getType());
            Edge e       = records.get(0).get(1).asEdge();
            Node dstNode = records.get(0).get(0).asNode();
            Assert.assertEquals("person", dstNode.getType());
            Assert.assertEquals("friend", e.getType());
            Assert.assertEquals(1, e.getProperties().size());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }


}

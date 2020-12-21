/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.graph.net;

import com.vesoft.nebula.Date;
import com.vesoft.nebula.DateTime;
import com.vesoft.nebula.Time;
import com.vesoft.nebula.Value;
import com.vesoft.nebula.client.graph.NebulaPoolConfig;
import com.vesoft.nebula.client.graph.data.DateTimeWrapper;
import com.vesoft.nebula.client.graph.data.DateWrapper;
import com.vesoft.nebula.client.graph.data.HostAddress;
import com.vesoft.nebula.client.graph.data.Node;
import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.data.TimeWrapper;
import com.vesoft.nebula.client.graph.data.ValueWrapper;
import com.vesoft.nebula.client.graph.exception.IOErrorException;
import com.vesoft.nebula.graph.ErrorCode;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class TestDataFromServer {
    private final NebulaPool pool = new NebulaPool();
    private Session session = null;

    @Before
    public void setUp() throws Exception {
        NebulaPoolConfig nebulaPoolConfig = new NebulaPoolConfig();
        nebulaPoolConfig.setMaxConnSize(1);
        Assert.assertTrue(pool.init(Arrays.asList(new HostAddress("127.0.0.1", 3701)),
            nebulaPoolConfig));
        session = pool.getSession("root", "nebula", true);
        ResultSet resp = session.execute("CREATE SPACE IF NOT EXISTS test_data; "
            + "USE test_data;"
            + "CREATE TAG IF NOT EXISTS person(name string, age int8, grade int16, "
            + "friends int32, book_num int64, birthday datetime, "
            + "start_school date, morning time, property double, "
            + "is_girl bool, child_name fixed_string(10), expend float, "
            + "first_out_city timestamp, hobby string);"
            + "CREATE TAG IF NOT EXISTS student(name string);"
            + "CREATE EDGE IF NOT EXISTS like(likeness double);"
            + "CREATE EDGE IF NOT EXISTS friend(start_year int, end_year int);"
            + "CREATE TAG INDEX IF NOT EXISTS person_name_index ON person(name(8));");
        Assert.assertTrue(resp.getErrorMessage(), resp.isSucceeded());
        TimeUnit.SECONDS.sleep(5);
        String insertVertexes = "INSERT VERTEX person(name, age, grade,friends, book_num, "
            + "birthday, start_school, morning, property,"
            + "is_girl, child_name, expend, first_out_city) VALUES "
            + "'Bob':('Bob', 10, 3, 10, 100, datetime(\"2010-09-10T10:08:02\"), "
            + "date(\"2017-09-10\"), time(\"07:10:00\"), "
            + "1000.0, false, \"Hello World!\", 100.0, 1111), "
            + "'Lily':('Lily', 9, 3, 10, 100, datetime(\"2010-09-10T10:08:02\"), "
            + "date(\"2017-09-10\"), time(\"07:10:00\"), "
            + "1000.0, false, \"Hello World!\", 100.0, 1111), "
            + "'Tom':('Tom', 10, 3, 10, 100, datetime(\"2010-09-10T10:08:02\"), "
            + "date(\"2017-09-10\"), time(\"07:10:00\"), "
            + "1000.0, false, \"Hello World!\", 100.0, 1111), "
            + "'Jerry':('Jerry', 9, 3, 10, 100, datetime(\"2010-09-10T10:08:02\"), "
            + "date(\"2017-09-10\"), time(\"07:10:00\"), "
            + "1000.0, false, \"Hello World!\", 100.0, 1111), "
            + "'John':('John', 10, 3, 10, 100, datetime(\"2010-09-10T10:08:02\"), "
            + "date(\"2017-09-10\"), time(\"07:10:00\"), "
            + "1000.0, false, \"Hello World!\", 100.0, 1111);";
        resp = session.execute(insertVertexes);
        Assert.assertTrue(resp.getErrorMessage(), resp.isSucceeded());

        insertVertexes = "INSERT VERTEX student(name) VALUES "
            + "'Bob':('Bob'), "
            + "'Lily':('Lily'), "
            + "'Tom':('Tom'), "
            + "'Jerry':('Jerry'), "
            + "'John':('John');";
        resp = session.execute(insertVertexes);
        Assert.assertTrue(resp.getErrorMessage(), resp.isSucceeded());

        String insertEdges = "INSERT EDGE like(likeness) VALUES "
            + "'Bob'->'Lily':(80.0), "
            + "'Bob'->'Tom':(70.0), "
            + "'Jerry'->'Lily':(84.0), "
            + "'Tom'->'Jerry':(68.3), "
            + "'Bob'->'John':(97.2);";
        resp = session.execute(insertEdges);
        Assert.assertTrue(resp.getErrorMessage(), resp.isSucceeded());
        insertEdges = "INSERT EDGE friend(start_year, end_year) VALUES "
            + "'Bob'->'Lily':(2018, 2020), "
            + "'Bob'->'Tom':(2018, 2020), "
            + "'Jerry'->'Lily':(2018, 2020), "
            + "'Tom'->'Jerry':(2018, 2020), "
            + "'Bob'->'John':(2018, 2020);";
        resp = session.execute(insertEdges);
        Assert.assertTrue(resp.getErrorMessage(), resp.isSucceeded());
    }

    @After
    public void tearDown() {
        if (session != null) {
            session.release();
        }
        pool.close();
    }

    @Test
    public void testAllSchemaType() {
        try {
            ResultSet result = session.execute("FETCH PROP ON person 'Bob';");
            Assert.assertTrue(result.isSucceeded());
            Assert.assertEquals("", result.getErrorMessage());
            Assert.assertFalse(result.getLatency() <= 0);
            Assert.assertEquals("", result.getComment());
            Assert.assertEquals(ErrorCode.SUCCEEDED, result.getErrorCode());
            Assert.assertEquals("test_data", result.getSpaceName());
            Assert.assertFalse(result.isEmpty());
            Assert.assertEquals(1, result.rowsSize());
            List<String> names = Arrays.asList("VertexID", "person.name", "person.age",
                "person.grade", "person.friends",
                "person.book_num", "person.birthday", "person.start_school",
                "person.morning", "person.property", "person.is_girl", "person.child_name",
                "person.expend", "person.first_out_city", "person.hobby");
            Assert.assertEquals(names.stream().sorted().collect(Collectors.toList()),
                result.keys().stream().sorted().collect(Collectors.toList()));

            Assert.assertEquals("Bob", result.rowValues(0).get(0).asString());
            Assert.assertEquals("Bob", result.rowValues(0).get(1).asString());
            Assert.assertEquals(10, result.rowValues(0).get(2).asLong());
            Assert.assertEquals(3, result.rowValues(0).get(3).asLong());
            Assert.assertEquals(10, result.rowValues(0).get(4).asLong());
            Assert.assertEquals(100, result.rowValues(0).get(5).asLong());
            DateTimeWrapper dateTimeWrapper = new DateTimeWrapper(
                new DateTime((short)2010, (byte)9,
                    (byte)10, (byte)10, (byte)8, (byte)2, 0));
            Assert.assertEquals(dateTimeWrapper, result.rowValues(0).get(6).asDateTime());

            DateWrapper dateWrapper = new DateWrapper(new Date((short)2017, (byte)9, (byte)10));
            Assert.assertEquals(dateWrapper, result.rowValues(0).get(7).asDate());

            TimeWrapper timeWrapper = new TimeWrapper(new Time((byte)7, (byte)10, (byte)0, 0));
            Assert.assertEquals(timeWrapper, result.rowValues(0).get(8).asTime());

            Assert.assertEquals(1000.0, result.rowValues(0).get(9).asDouble(), 0.0);
            Assert.assertEquals(false, result.rowValues(0).get(10).asBoolean());
            Assert.assertEquals("Hello Worl", result.rowValues(0).get(11).asString());
            Assert.assertEquals(100.0, result.rowValues(0).get(12).asDouble(), 0.0);
            Assert.assertEquals(1111, result.rowValues(0).get(13).asLong());
            Assert.assertEquals(ValueWrapper.NullType.__NULL__,
                result.rowValues(0).get(14).asNull().getNullType());
        } catch (IOErrorException | UnsupportedEncodingException e) {
            e.printStackTrace();
            assert false;
        }
    }

    @Test
    public void testList() {
        try {
            ResultSet result = session.execute("YIELD ['name', 'age', 'birthday'];");
            Assert.assertTrue(result.isSucceeded());
            Assert.assertEquals(1, result.rowsSize());
            List<String> names = Arrays.asList("name", "age", "birthday");
            Assert.assertTrue(result.rowValues(0).get(0).isList());
            List<String> listVal = new ArrayList<>();
            for (ValueWrapper val : result.rowValues(0).get(0).asList()) {
                assert val.isString();
                listVal.add(val.asString());
            }
            Assert.assertEquals(names.stream().sorted().collect(Collectors.toList()),
                listVal.stream().sorted().collect(Collectors.toList()));
        } catch (IOErrorException | UnsupportedEncodingException e) {
            e.printStackTrace();
            assert false;
        }
    }

    @Test
    public void testSet() {
        try {
            ResultSet result = session.execute("YIELD {'name', 'name', 'age', 'birthday'};");
            Assert.assertFalse(!result.isSucceeded());
            Assert.assertEquals(1, result.rowsSize());
            List<String> names = Arrays.asList("name", "age","birthday");
            Assert.assertFalse(!result.rowValues(0).get(0).isSet());
            List<String> setVal = new ArrayList<>();
            for (ValueWrapper val : result.rowValues(0).get(0).asSet()) {
                assert val.isString();
                setVal.add(val.asString());
            }

            Assert.assertEquals(names.stream().sorted().collect(Collectors.toList()),
                setVal.stream().sorted().collect(Collectors.toList()));
        } catch (IOErrorException | UnsupportedEncodingException e) {
            e.printStackTrace();
            assert false;
        }
    }

    @Test
    public void testMap() {
        try {
            ResultSet result = session.execute(
                "YIELD {name:'Tom', age:18, birthday: '2010-10-10'};");
            Assert.assertTrue(result.isSucceeded());
            Assert.assertEquals(1, result.rowsSize());
            Assert.assertTrue(result.rowValues(0).get(0).isMap());
            Map<String, ValueWrapper> mapVals = result.rowValues(0).get(0).asMap();
            Assert.assertEquals(3, mapVals.size());
            assert mapVals.containsKey("name");
            assert mapVals.get("name").isString();
            assert Objects.equals("Tom", mapVals.get("name").asString());
            assert mapVals.containsKey("age");
            assert mapVals.get("age").isLong();
            Assert.assertEquals(18, mapVals.get("age").asLong());
            assert mapVals.containsKey("birthday");
            assert mapVals.get("birthday").isString();
            Assert.assertEquals("2010-10-10", mapVals.get("birthday").asString());
        } catch (IOErrorException | UnsupportedEncodingException e) {
            e.printStackTrace();
            assert false;
        }
    }

    @Test
    public void testNode() {
        try {
            ResultSet result = session.execute(
                "MATCH (v:person {name: \"Bob\"}) RETURN v");
            Assert.assertTrue(result.isSucceeded());
            Assert.assertEquals(1, result.rowsSize());
            Assert.assertTrue(result.rowValues(0).get(0).isVertex());
            Node node = result.rowValues(0).get(0).asNode();
            Assert.assertEquals("Bob", node.getId());
            Assert.assertTrue(node.hasLabel("person"));
            Assert.assertTrue(node.hasLabel("student"));
            Assert.assertEquals(
                Arrays.asList("person", "student").stream().sorted().collect(Collectors.toList()),
                node.labels().stream().sorted().collect(Collectors.toList()));
            Assert.assertEquals(
                Arrays.asList("name").stream().sorted().collect(Collectors.toList()),
                node.keys("student").stream().sorted().collect(Collectors.toList()));
            Assert.assertEquals(14, node.properties("person").keySet().size());
            Assert.assertEquals(1, node.properties("student").keySet().size());
            String expectedStr = "(\"Bob\" :student {name: \"Bob\"} "
                + ":person {name: \"Bob\", birthday: 2010-09-10T10:08:02.000000, "
                + "start_school: 2017-09-10, is_girl: false, "
                + "child_name: \"Hello Worl\", friends: 10, morning: 07:10:00.000000, "
                + "book_num: 100, expend: 100.0, grade: 3, property: 1000.0, name: \"Bob\", "
                + "first_out_city: 1111, age: 10, hobby: __NULL__})";
            Assert.assertEquals(expectedStr, node.toString());
        } catch (IOErrorException | UnsupportedEncodingException e) {
            e.printStackTrace();
            assert false;
        }
    }

    @Test
    public void testRelationship() {
        try {
            ResultSet result = session.execute(
                "MATCH (:person{name:'Bob'}) -[e:friend]-> (:person{name:'Lily'}) RETURN e");
            Assert.assertTrue(result.isSucceeded());
            Assert.assertEquals(1, result.rowsSize());
            Assert.assertTrue(result.rowValues(0).get(0).isEdge());
            Assert.assertEquals(
                "(\"Bob\")-[:friend@0{start_year: 2018, end_year: 2020}]->(\"Lily\")",
                result.rowValues(0).get(0).asRelationship().toString());
        } catch (IOErrorException | UnsupportedEncodingException e) {
            e.printStackTrace();
            assert false;
        }
    }

    @Test
    public void testPath() {
        try {
            ResultSet result = session.execute(
                "MATCH p = (:person{name:'Bob'})-[:friend]->(:person{name:'Lily'}) return p");
            Assert.assertTrue(result.getErrorMessage(), result.isSucceeded());
            Assert.assertEquals(1, result.rowsSize());
            Assert.assertTrue(result.rowValues(0).get(0).isPath());
            String expectedStr = "(\"Bob\" :student {name: \"Bob\"} "
                + ":person {name: \"Bob\", birthday: 2010-09-10T10:08:02.000000, "
                + "start_school: 2017-09-10, is_girl: false, child_name: \"Hello Worl\", "
                + "friends: 10, morning: 07:10:00.000000, book_num: 100, expend: 100.0, "
                + "grade: 3, property: 1000.0, name: \"Bob\", first_out_city: 1111, "
                + "age: 10, hobby: __NULL__})"
                + "-[:friend@0{start_year:2018, end_year:2020}]->"
                + "(\"Lily\" :student {name: \"Lily\"} "
                + ":person {name: \"Lily\", birthday: 2010-09-10T10:08:02.000000, "
                + "start_school: 2017-09-10, is_girl: false, child_name: \"Hello Worl\", "
                + "friends: 10, morning: 07:10:00.000000, book_num: 100, expend: 100.0, "
                + "grade: 3, property: 1000.0, name: \"Lily\", "
                + "first_out_city: 1111, age: 9, hobby: __NULL__})";
            Assert.assertEquals(expectedStr, result.rowValues(0).get(0).asPath().toString());

            result = session.execute(
                "MATCH p = (:person{name:'Bob'})-[:friend]->(:person{name:'Lily'})"
                    + "<-[:friend]-(:person{name:'Jerry'}) return p");
            Assert.assertTrue(result.getErrorMessage(), result.isSucceeded());
            Assert.assertEquals(1, result.rowsSize());
            Assert.assertTrue(result.rowValues(0).get(0).isPath());
            expectedStr = "(\"Bob\" :student {name: \"Bob\"} "
                + ":person {name: \"Bob\", birthday: 2010-09-10T10:08:02.000000, "
                + "start_school: 2017-09-10, is_girl: false, child_name: \"Hello Worl\", "
                + "friends: 10, morning: 07:10:00.000000, book_num: 100, expend: 100.0, "
                + "grade: 3, property: 1000.0, name: \"Bob\", first_out_city: 1111, "
                + "age: 10, hobby: __NULL__})"
                + "-[:friend@0{start_year:2018, end_year:2020}]->"
                + "(\"Lily\" :student {name: \"Lily\"} "
                + ":person {name: \"Lily\", birthday: 2010-09-10T10:08:02.000000, "
                + "start_school: 2017-09-10, is_girl: false, child_name: \"Hello Worl\", "
                + "friends: 10, morning: 07:10:00.000000, book_num: 100, expend: 100.0, "
                + "grade: 3, name: \"Lily\", property: 1000.0, "
                + "first_out_city: 1111, age: 9, hobby: __NULL__})"
                + "<-[:friend@0{start_year:2018, end_year:2020}]-"
                + "(\"Jerry\" :student {name: \"Jerry\"} "
                + ":person {name: \"Jerry\", birthday: 2010-09-10T10:08:02.000000, "
                + "start_school: 2017-09-10, is_girl: false, child_name: \"Hello Worl\", "
                + "friends: 10, morning: 07:10:00.000000, book_num: 100, expend: 100.0, "
                + "grade: 3, property: 1000.0, name: \"Jerry\", first_out_city: 1111, "
                + "age: 9, hobby: __NULL__})";
            Assert.assertEquals(expectedStr, result.rowValues(0).get(0).asPath().toString());
        } catch (IOErrorException | UnsupportedEncodingException e) {
            e.printStackTrace();
            assert false;
        }
    }
}

/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.client.storage;

import com.vesoft.nebula.Coordinate;
import com.vesoft.nebula.DataSet;
import com.vesoft.nebula.Date;
import com.vesoft.nebula.DateTime;
import com.vesoft.nebula.Geography;
import com.vesoft.nebula.Point;
import com.vesoft.nebula.Polygon;
import com.vesoft.nebula.Row;
import com.vesoft.nebula.Time;
import com.vesoft.nebula.Value;
import java.util.ArrayList;
import java.util.List;

public class MockUtil {

    public static List<DataSet> mockVertexDataSets() {

        List<byte[]> columnNames = new ArrayList<>();
        columnNames.add("person._vid".getBytes());
        columnNames.add("person.boolean_col1".getBytes());
        columnNames.add("person.long_col2".getBytes());
        columnNames.add("person.double_col3".getBytes());
        columnNames.add("person.date_col4".getBytes());
        columnNames.add("person.time_col5".getBytes());
        columnNames.add("person.datetime_col6".getBytes());
        columnNames.add("person.geography_col7".getBytes());

        // row 1
        List<Value> values1 = new ArrayList<>();
        values1.add(Value.sVal("Tom".getBytes()));
        values1.add(Value.bVal(true));
        values1.add(Value.iVal(12));
        values1.add(Value.fVal(1.0));
        values1.add(Value.dVal(new Date((short) 2020, (byte) 1, (byte) 1)));
        values1.add(Value.tVal(new Time((byte) 12, (byte) 1, (byte) 1, 100)));
        values1.add(Value.dtVal(new DateTime((short) 2020, (byte) 1, (byte) 1, (byte) 12,
                (byte) 10, (byte) 30, 100)));
        values1.add(Value.ggVal(new Geography(Geography.PTVAL,
                new Point(new Coordinate(1.0,1.5)))));

        List<Row> rows = new ArrayList<>();
        rows.add(new Row(values1));

        List<DataSet> dataSets = new ArrayList<>();
        DataSet dataSet = new DataSet(columnNames, rows);
        dataSets.add(dataSet);
        return dataSets;
    }

    public static List<DataSet> mockEdgeDataSets() {

        List<byte[]> columnNames = new ArrayList<>();
        columnNames.add("friend._src".getBytes());
        columnNames.add("friend._dst".getBytes());
        columnNames.add("friend._rank".getBytes());
        columnNames.add("friend.col1".getBytes());
        columnNames.add("friend.col2".getBytes());

        // row 1
        List<Value> values1 = new ArrayList<>();
        values1.add(Value.sVal("Tom".getBytes()));
        values1.add(Value.sVal("Jina".getBytes()));
        values1.add(Value.iVal(1));
        values1.add(Value.bVal(true));
        values1.add(Value.iVal(12));

        // row 2
        List<Value> values2 = new ArrayList<>();
        values2.add(Value.sVal("Bob".getBytes()));
        values2.add(Value.sVal("Tina".getBytes()));
        values2.add(Value.iVal(2));
        values2.add(Value.bVal(false));
        values2.add(Value.iVal(15));

        // row 3
        List<Value> values3 = new ArrayList<>();
        values3.add(Value.sVal("Tina".getBytes()));
        values3.add(Value.sVal("Lei".getBytes()));
        values3.add(Value.iVal(3));
        values3.add(Value.bVal(true));
        values3.add(Value.iVal(19));

        List<Row> rows = new ArrayList<>();
        rows.add(new Row(values1));
        rows.add(new Row(values2));
        rows.add(new Row(values3));

        List<DataSet> dataSets = new ArrayList<>();
        DataSet dataSet = new DataSet(columnNames, rows);
        dataSets.add(dataSet);
        return dataSets;
    }
}

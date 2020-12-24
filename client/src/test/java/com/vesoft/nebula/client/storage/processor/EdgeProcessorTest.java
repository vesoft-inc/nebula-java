/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.storage.processor;

import com.vesoft.nebula.DataSet;
import com.vesoft.nebula.Row;
import com.vesoft.nebula.Value;
import com.vesoft.nebula.client.storage.data.EdgeRow;
import com.vesoft.nebula.client.storage.data.EdgeTableRow;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class EdgeProcessorTest {

    @Test
    public void testConstructEdgeRow() {
        List<DataSet> dataSets = mockDataSets();
        List<EdgeRow> edgeRows = EdgeProcessor.constructEdgeRow(dataSets, "utf-8");
        assert (edgeRows.size() == dataSets.get(0).getRows().size());
        try {
            assert (!edgeRows.get(0).getSrcId().contains("friend"));
            assert (!edgeRows.get(0).getDstId().contains("friend"));
            assert (edgeRows.get(0).getRank() == 1);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            assert (false);
        }
        assert (edgeRows.get(0).getProps().size() == 2);
        assert (edgeRows.get(0).getProps().containsKey("col1"));
        assert (edgeRows.get(0).getProps().containsKey("col2"));
    }

    @Test
    public void testConstructEdgeTableRow() {
        List<DataSet> dataSets = mockDataSets();
        List<EdgeTableRow> tableRows = EdgeProcessor.constructEdgeTableRow(dataSets, "utf-8");
        assert (tableRows.size() == dataSets.get(0).getRows().size());
        assert (tableRows.get(0).getValues().size() == 5);
        assert (tableRows.get(0).getSrcId().equals("Tom"));
        assert (tableRows.get(0).getDstId().equals("Jina"));
        assert (tableRows.get(0).getRank() == 1);
    }

    private List<DataSet> mockDataSets() {

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

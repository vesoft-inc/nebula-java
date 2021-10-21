/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.storage.processor;

import com.vesoft.nebula.DataSet;
import com.vesoft.nebula.client.graph.data.PointWrapper;
import com.vesoft.nebula.client.graph.data.ValueWrapper;
import com.vesoft.nebula.client.storage.MockUtil;
import com.vesoft.nebula.client.storage.data.VertexRow;
import com.vesoft.nebula.client.storage.data.VertexTableRow;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.Test;

public class VertexProcessorTest {

    @Test
    public void testConstructVertexRow() {
        List<DataSet> dataSets = MockUtil.mockVertexDataSets();
        Map<ValueWrapper, VertexRow> vertexRows = VertexProcessor.constructVertexRow(dataSets,
                "utf-8");
        List<VertexRow> rows = new ArrayList<>(vertexRows.values());
        assert (vertexRows.size() == dataSets.get(0).getRows().size());
        try {
            assert (rows.get(0).getVid().asString().equals("Tom"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            assert (false);
        }
        assert (rows.get(0).getProps().size() == 7);
        assert (rows.get(0).getProps().containsKey("boolean_col1"));
        assert (rows.get(0).getProps().containsKey("long_col2"));
    }

    @Test
    public void testConstructEdgeTableRow() {
        List<DataSet> dataSets = MockUtil.mockVertexDataSets();
        List<VertexTableRow> tableRows = VertexProcessor
                .constructVertexTableRow(dataSets, "utf-8");
        assert (tableRows.size() == dataSets.get(0).getRows().size());
        assert (tableRows.get(0).getValues().size() == 8);
        try {
            assert (tableRows.get(0).getVid().asString().equals("Tom"));
            assert (tableRows.get(0).getString(0).equals("Tom"));
        } catch (UnsupportedEncodingException e) {
            assert (false);
        }
        assert (tableRows.get(0).getBoolean(1));
        assert (tableRows.get(0).getLong(2) == 12);
        assert (tableRows.get(0).getDouble(3) < 1.1);
        assert (tableRows.get(0).getDate(4).getYear() == 2020);
        assert (tableRows.get(0).getTime(5).getSecond() == 1);
        assert (tableRows.get(0).getDateTime(6).getDay() == 1);
        assert (tableRows.get(0).getGeography(7).getPointWrapper().getCoordinate().getX() == 1.0);

    }
}

/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.data;

import com.vesoft.nebula.client.graph.ResultSet;
import com.vesoft.nebula.graph.ColumnValue;
import com.vesoft.nebula.graph.ErrorCode;
import com.vesoft.nebula.graph.ExecutionResponse;
import com.vesoft.nebula.graph.RowValue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class ResultSetTest {
    @Test
    public void testAll() {
        ExecutionResponse resp = new ExecutionResponse();
        resp.column_names = Arrays.asList("col1".getBytes(), "col2".getBytes(), "col3".getBytes());
        resp.latency_in_us = 100;
        resp.space_name = "space1";
        resp.error_msg = "hello world";
        resp.error_code = ErrorCode.E_EXECUTION_ERROR;
        resp.warning_msg = "warning";
        RowValue row = new RowValue();
        row.columns = new ArrayList<>();
        row.columns.add(new ColumnValue(ColumnValue.DOUBLE_PRECISION, 10.1));
        row.columns.add(new ColumnValue(ColumnValue.INTEGER, (long)8));
        row.columns.add(new ColumnValue(ColumnValue.BOOL_VAL, false));
        resp.rows = Arrays.asList(row);
        ResultSet result = new ResultSet(resp);
        Assert.assertEquals(Arrays.asList("col1", "col2", "col3"),
            result.getColumns());
        assert result.getLatency() == 100;
        assert result.getResultCode() == ErrorCode.E_EXECUTION_ERROR;
        Assert.assertEquals("hello world", result.getErrorMsg());
        Assert.assertEquals("warning", result.getWarnMsg());
        Assert.assertEquals("space1", result.getSpaceName());
        List<RowValue> rows = result.getRows();
        assert rows.get(0).columns.get(0).getDouble_precision() == 10.1;
        assert rows.get(0).columns.get(1).getInteger() == 8;
        Assert.assertEquals(1, result.getResults().size());
        ResultSet.Result resultValue = new ResultSet.Result(
            Arrays.asList("col1", "col2", "col3"), row);
        Assert.assertEquals(10.1,
            result.getResults().get(0).getDouble("col1"), 0.0);
        Assert.assertEquals(8, result.getResults().get(0).getInteger("col2"));
        Assert.assertEquals(false, result.getResults().get(0).getBoolean("col3"));
    }

    @Test
    public void testColNameRowSEmpty() {
        ExecutionResponse resp = new ExecutionResponse();
        ResultSet result = new ResultSet(resp);
        Assert.assertEquals(Arrays.asList(), result.getColumns());
        assert result.getLatency() == 0;
        assert result.getResultCode() == ErrorCode.SUCCEEDED;
        assert result.getErrorMsg() == null;
        assert result.getSpaceName() == null;
        assert result.getWarnMsg() == null;
        assert result.getLatency() == 0;
        Assert.assertTrue(result.getRows().isEmpty());
        Assert.assertTrue(result.getResults().isEmpty());
    }
}

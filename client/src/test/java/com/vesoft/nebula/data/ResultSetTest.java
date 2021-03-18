/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.data;

import static org.junit.Assert.assertEquals;

import com.vesoft.nebula.client.graph.ResultSet;
import com.vesoft.nebula.graph.ErrorCode;
import com.vesoft.nebula.graph.ExecutionResponse;
import java.util.ArrayList;
import java.util.Collections;

import org.junit.Test;


public class ResultSetTest {
    @Test
    public void testAll() {
        ExecutionResponse resp = new ExecutionResponse();
        resp.column_names = new ArrayList<byte[]>();
        resp.column_names.add("col1".getBytes());
        resp.column_names.add("col2".getBytes());
        resp.column_names.add("col3".getBytes());
        resp.latency_in_us = 100;
        resp.space_name = "space1";
        resp.error_msg = "hello world";
        resp.error_code = ErrorCode.E_EXECUTION_ERROR;
        resp.warning_msg = "warning";
        resp.rows = new ArrayList<>();
        ResultSet result = new ResultSet(resp);
        assert result.getLatency() == 100;
        assert result.getResultCode() == ErrorCode.E_EXECUTION_ERROR;
        assertEquals("hello world", result.getErrorMsg());
        assertEquals("warning", result.getWarnMsg());
        assertEquals("space1", result.getSpaceName());
    }
}

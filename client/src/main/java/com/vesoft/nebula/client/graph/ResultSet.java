/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.graph;

import com.google.common.collect.Lists;
import com.vesoft.nebula.graph.ColumnValue;
import com.vesoft.nebula.graph.DateTime;
import com.vesoft.nebula.graph.ExecutionResponse;
import com.vesoft.nebula.graph.RowValue;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ResultSet {

    private List<String> columns;
    private ExecutionResponse response;
    private List<RowValue> rows;
    private List<Result> results;

    public static class Result {
        private final RowValue row;
        private final List<String> columns;

        public Result(List<String> columns, RowValue row) {
            this.row = row;
            this.columns = columns;
        }

        public ColumnValue get(int index) {
            return this.row.columns.get(index);
        }

        public ColumnValue get(String key) {
            int index = columns.indexOf(key);
            if (index == -1) {
                throw new IllegalArgumentException(
                        "Cannot get field because the key '" + key + "' is not exist");
            }
            return this.row.columns.get(index);
        }

        public String getString(String key) {
            return new String(this.get(key).getStr()).intern();
        }

        public long getInteger(String key) {
            return this.get(key).getInteger();
        }

        public long getId(String key) {
            return this.get(key).getId();
        }

        public double getDouble(String key) {
            return this.get(key).getDouble_precision();
        }

        public float getFloat(String key) {
            return this.get(key).getSingle_precision();
        }

        public DateTime getDateTime(String key) {
            return this.get(key).getDatetime();
        }

        public boolean getBoolean(String key) {
            return this.get(key).isBool_val();
        }

        public long getTimestamp(String key) {
            return this.get(key).getTimestamp();
        }

        public int size() {
            return row.columns.size();
        }

        public boolean contains(String key) {
            return columns.contains(key);
        }
    }

    /**
     * Constructor
     */
    public ResultSet() {
        this(null);
    }

    public ResultSet(ExecutionResponse resp) {
        response = resp;
        if (resp != null && resp.column_names != null) {
            columns = Lists.newArrayListWithCapacity(resp.column_names.size());
            for (byte[] column : resp.column_names) {
                columns.add(new String(column).intern());
            }
        } else {
            columns = Lists.newArrayList();
        }
    }

    /**
     * Get Column Names
     * @return
     */
    public List<String> getColumns() {
        return this.columns;
    }

    public List<RowValue> getRows() {
        if (response == null) {
            return Lists.newArrayList();
        }
        return response.getRows();
    }

    public List<Result> getResults() {
        return this.results;
    }

    public int getResultCode() {
        if (response == null) {
            return 0;
        }
        return response.getError_code();
    }

    public String getErrorMsg() {
        if (response == null) {
            return null;
        }
        return response.getError_msg();
    }

    public String getWarnMsg() {
        if (response == null) {
            return null;
        }
        return response.getWarning_msg();
    }

    /**
     * @return the latency, unit is us
     */
    public int getLatency() {
        if (response == null) {
            return 0;
        }
        return response.getLatency_in_us();
    }

    public String getSpaceName() {
        if (response == null) {
            return null;
        }
        return response.getSpace_name();
    }

    @Override
    public String toString() {
        return "ResultSet{"
                + "columns=" + this.columns
                + ", rows=" + this.rows
                + '}';
    }
}

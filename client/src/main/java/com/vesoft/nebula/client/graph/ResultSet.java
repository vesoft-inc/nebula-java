/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.graph;

import com.google.common.collect.Lists;
import com.vesoft.nebula.graph.ColumnValue;
import com.vesoft.nebula.graph.DateTime;
import com.vesoft.nebula.graph.RowValue;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ResultSet {

    private List<String> columns;
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
        this(Lists.newArrayList(), Lists.newArrayList());
    }

    /**
     * @param columns schema info
     * @param rows    field values
     */
    public ResultSet(List<byte[]> columns, List<RowValue> rows) {
        if (columns == null) {
            columns = Lists.newArrayList();
        }
        this.columns = Lists.newArrayListWithCapacity(columns.size());
        for (byte[] column : columns) {
            this.columns.add(new String(column).intern());
        }
        this.rows = rows;
        this.results = new ArrayList<>(rows.size());
        for (RowValue row : this.rows) {
            this.results.add(new Result(this.columns, row));
        }
    }

    /**
     * Get Column Names
     *
     * @return
     */
    public List<String> getColumns() {
        return this.columns;
    }

    public List<RowValue> getRows() {
        return this.rows;
    }

    public List<Result> getResults() {
        return this.results;
    }

    @Override
    public String toString() {
        return "ResultSet{"
                + "columns=" + this.columns
                + ", rows=" + this.rows
                + '}';
    }
}

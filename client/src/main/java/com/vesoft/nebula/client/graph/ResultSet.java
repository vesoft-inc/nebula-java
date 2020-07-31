/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.graph;

import com.google.common.collect.Lists;
import com.vesoft.nebula.graph.ColumnValue;
import com.vesoft.nebula.graph.RowValue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ResultSet implements Serializable, Cloneable{

    private List<String> columns;
    private List<RowValue> rows;
    private List<Result> results;

    public static class Result implements Serializable, Comparable<Result>, Cloneable {
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
            return this.row.columns.get(columns.indexOf(key));
        }

        public int size() {
            return row.columns.size();
        }

        public boolean contains(String key) {
            return columns.contains(key);
        }

        @Override
        public int compareTo(Result o) {
            return this.row.compareTo(o.row);
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

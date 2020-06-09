/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.graph;

import com.google.common.collect.Lists;
import com.vesoft.nebula.graph.RowValue;
import java.util.Iterator;
import java.util.List;

/**
 *
 */
public class ResultSet implements Iterator {

    private List<String> columns;
    private List<RowValue> rows;

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

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public Object next() {
        return null;
    }

    @Override
    public void remove() {

    }

    @Override
    public String toString() {
        return "ResultSet{"
                + "columns=" + this.columns
                + ", rows=" + this.rows
                + '}';
    }
}

/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.graph.data;

import com.google.common.collect.Lists;
import com.vesoft.nebula.Row;
import com.vesoft.nebula.Value;
import com.vesoft.nebula.graph.ErrorCode;
import com.vesoft.nebula.graph.ExecutionResponse;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

public class ResultSet {
    private int code = ErrorCode.SUCCEEDED;
    private String errorMessage;
    private List<String> columnNames;
    private List<Record> records;

    public static class Record implements Iterable<Value> {
        private final Row row;
        private final List<String> columnNames;
        private int curror = -1;

        public Record(List<String> columnNames, Row row) {
            this.row = row;
            this.columnNames = columnNames;
        }

        @Override
        public Iterator<Value> iterator() {
            return this.row.values.iterator();
        }

        @Override
        public void forEach(Consumer<? super Value> action) {
            this.row.values.forEach(action);
        }

        @Override
        public Spliterator<Value> spliterator() {
            return this.row.values.spliterator();
        }

        public Value get(int index) {
            return this.row.values.get(index);
        }

        public Value get(String key) {
            int index = columnNames.indexOf(key);
            if (index == -1) {
                throw new IllegalArgumentException(
                        "Cannot get field because the key '" + key + "' is not exist");
            }
            return this.row.values.get(index);
        }

        public int size() {
            return this.columnNames.size();
        }

        public boolean contains(String key) {
            return this.columnNames.contains(key);
        }
    }

    /**
     * Constructor
     */
    public ResultSet() {
        this(new ExecutionResponse());
    }

    public ResultSet(ExecutionResponse resp) {
        if (resp.error_msg != null) {
            errorMessage = new String(resp.error_msg).intern();
        }
        code = resp.error_code;
        if (resp.data != null) {
            this.columnNames = Lists.newArrayListWithCapacity(resp.data.column_names.size());
            this.records = Lists.newArrayListWithCapacity(resp.data.rows.size());
            for (byte[] column : resp.data.column_names) {
                this.columnNames.add(new String(column).intern());
            }
            this.records = new ArrayList<>(resp.data.rows.size());
            for (Row row : resp.data.rows) {
                this.records.add(new Record(this.columnNames, row));
            }
        }
    }

    public boolean isSucceeded() {
        return this.code == ErrorCode.SUCCEEDED;
    }

    public int getErrorCode() {
        return this.code;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public List<String> getColumnNames() {
        return this.columnNames;
    }

    public List<Record> getRecords() {
        return this.records;
    }
}

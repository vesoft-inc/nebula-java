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
import com.vesoft.nebula.graph.PlanDescription;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

public class ResultSet {
    private final ExecutionResponse response;
    private List<String> columnNames;
    private final String decodeType = "utf-8";

    public static class Record implements Iterable<ValueWrapper> {
        private final List<ValueWrapper> colValues = new ArrayList<>();
        private List<String> columnNames = new ArrayList<>();

        public Record(List<String> columnNames, Row row, String decodeType) {
            if (columnNames == null) {
                return;
            }

            if (row == null || row.values == null) {
                return;
            }

            for (Value value : row.values) {
                this.colValues.add(new ValueWrapper(value, decodeType));
            }

            this.columnNames = columnNames;
        }

        @Override
        public Iterator<ValueWrapper> iterator() {
            return this.colValues.iterator();
        }

        @Override
        public void forEach(Consumer<? super ValueWrapper> action) {
            this.colValues.forEach(action);
        }

        @Override
        public Spliterator<ValueWrapper> spliterator() {
            return this.colValues.spliterator();
        }


        @Override
        public String toString() {
            StringBuilder rowStr = new StringBuilder();
            for (ValueWrapper v : colValues) {
                rowStr.append(v.toString()).append(',');
            }
            return "Record{row=" + rowStr
                    + ", columnNames=" + columnNames.toString()
                    + '}';
        }

        public ValueWrapper get(int index) {
            if (index >= columnNames.size()) {
                throw new IllegalArgumentException(
                        String.format("Cannot get field because the key '%d' out of range", index));
            }
            return this.colValues.get(index);
        }

        public ValueWrapper get(String key) {
            int index = columnNames.indexOf(key);
            if (index == -1) {
                throw new IllegalArgumentException(
                        "Cannot get field because the key '" + key + "' is not exist");
            }
            return this.colValues.get(index);
        }

        public List<ValueWrapper> values() {
            return colValues;
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
    public ResultSet(ExecutionResponse resp) {
        if (resp == null) {
            throw new RuntimeException("Input an null `ExecutionResponse' object");
        }
        this.response = resp;
        if (resp.data != null) {
            this.columnNames = Lists.newArrayListWithCapacity(resp.data.column_names.size());
            // space name's charset is 'utf-8'
            for (byte[] column : resp.data.column_names) {
                this.columnNames.add(new String(column));
            }
        }
    }

    public boolean isSucceeded() {
        return response.error_code == ErrorCode.SUCCEEDED;
    }

    public boolean isEmpty() {
        return response.data == null || response.data.rows.isEmpty();
    }

    public int getErrorCode() {
        return response.error_code;
    }

    public String getSpaceName() {
        if (response.space_name == null) {
            return "";
        }
        // space name's charset is 'utf-8'
        return new String(response.space_name);
    }

    public String getErrorMessage() {
        if (response.error_msg == null) {
            return "";
        }
        // error message's charset is 'utf-8'
        return new String(response.error_msg);
    }

    public String getComment() {
        if (response.comment == null) {
            return "";
        }
        // error message's charset is 'utf-8'
        return new String(response.comment);
    }

    public int getLatency() {
        return response.latency_in_us;
    }

    public PlanDescription getPlanDesc() {
        return response.getPlan_desc();
    }

    public List<String> keys() {
        return columnNames;
    }

    public int rowsSize() {
        if (response.data == null) {
            return 0;
        }
        return response.data.rows.size();
    }

    /**
     * get all row values on the row index
     */
    public Record rowValues(int index) {
        if (response.data == null) {
            throw new RuntimeException("Empty data");
        }
        if (index >= response.data.rows.size()) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return new Record(columnNames, response.data.rows.get(index), decodeType);
    }

    /**
     * get all col values on the col key
     */
    public List<ValueWrapper> colValues(String key) {
        if (response.data == null) {
            throw new RuntimeException("Empty data");
        }
        int index = columnNames.indexOf(key);
        if (index < 0) {
            throw new ArrayIndexOutOfBoundsException();
        }
        List<ValueWrapper> values = new ArrayList<>();
        for (int i = 0; i < response.data.rows.size(); i++) {
            values.add(new ValueWrapper(response.data.rows.get(i).values.get(index), decodeType));
        }
        return values;
    }

    /**
     * get all rows, the value is the origin value, the string is bianry
     */
    public List<Row> getRows() {
        if (response.data == null) {
            throw new RuntimeException("Empty data");
        }
        return response.data.rows;
    }
}

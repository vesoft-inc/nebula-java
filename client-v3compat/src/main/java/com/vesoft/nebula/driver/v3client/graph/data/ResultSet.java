/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.v3client.graph.data;

import com.vesoft.nebula.driver.v3client.graph.ErrorCode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * The result of a query, matching the v3 client's {@code ResultSet} API.
 *
 * <p>The v5 driver returns a one-shot forward iterator; this wrapper eagerly materializes all rows
 * so that the v3 index-based accessors ({@link #rowValues(int)}, {@link #colValues(String)}) and
 * repeated iteration keep working.
 */
public class ResultSet {

    public static class Record implements Iterable<ValueWrapper> {

        private final List<ValueWrapper> colValues   = new ArrayList<>();
        private final List<String>       columnNames;

        public Record(List<String> columnNames,
                      com.vesoft.nebula.driver.graph.data.ResultSet.Record record,
                      int timezoneOffset) {
            this.columnNames = columnNames;
            if (record == null) {
                return;
            }
            for (com.vesoft.nebula.driver.graph.data.ValueWrapper value : record.values()) {
                this.colValues.add(new ValueWrapper(value, timezoneOffset));
            }
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
            List<String> valueStr = new ArrayList<>();
            for (ValueWrapper v : colValues) {
                valueStr.add(v.toString());
            }
            return String.format("ColumnName: %s, Values: %s",
                                 columnNames.toString(), valueStr.toString());
        }

        public ValueWrapper get(int index) {
            if (index >= columnNames.size()) {
                throw new IllegalArgumentException(
                    String.format("Cannot get field because the key '%d' out of range", index));
            }
            return this.colValues.get(index);
        }

        public ValueWrapper get(String columnName) {
            int index = columnNames.indexOf(columnName);
            if (index == -1) {
                throw new IllegalArgumentException(
                    "Cannot get field because the columnName '"
                        + columnName + "' is not exists");
            }
            return this.colValues.get(index);
        }

        public List<ValueWrapper> values() {
            return colValues;
        }

        public int size() {
            return this.columnNames.size();
        }

        public boolean contains(String columnName) {
            return this.columnNames.contains(columnName);
        }
    }

    private final List<String> columnNames = new ArrayList<>();
    private final List<Record> records     = new ArrayList<>();
    private final int          timezoneOffset;
    private final boolean      succeeded;
    private final boolean      empty;
    private final int          errorCode;
    private final String       errorMessage;
    private final long         latency;
    private final com.vesoft.nebula.driver.graph.data.PlanInfoNode planDesc;

    public ResultSet(com.vesoft.nebula.driver.graph.data.ResultSet resultSet) {
        this(resultSet, 0);
    }

    public ResultSet(com.vesoft.nebula.driver.graph.data.ResultSet resultSet, int timezoneOffset) {
        if (resultSet == null) {
            throw new RuntimeException("Input an null `ResultSet' object");
        }
        this.timezoneOffset = timezoneOffset;
        this.succeeded = resultSet.isSucceeded();
        this.empty = resultSet.isEmpty();
        this.errorCode = ErrorCode.fromV5ErrorCode(resultSet.getErrorCode());
        this.errorMessage = resultSet.getErrorMessage();
        this.latency = resultSet.getLatency();
        this.planDesc = resultSet.getPlanDesc();
        this.columnNames.addAll(resultSet.getColumnNames());
        while (resultSet.hasNext()) {
            records.add(new Record(columnNames, resultSet.next(), timezoneOffset));
        }
    }

    public boolean isSucceeded() {
        return succeeded;
    }

    public boolean isEmpty() {
        return empty;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getSpaceName() {
        return "";
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getComment() {
        return "";
    }

    public long getLatency() {
        return latency;
    }

    public com.vesoft.nebula.driver.graph.data.PlanInfoNode getPlanDesc() {
        return planDesc;
    }

    public List<String> keys() {
        return columnNames;
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    public int rowsSize() {
        return records.size();
    }

    public Record rowValues(int index) {
        if (index >= records.size()) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return records.get(index);
    }

    public List<ValueWrapper> colValues(String columnName) {
        int index = columnNames.indexOf(columnName);
        if (index < 0) {
            throw new ArrayIndexOutOfBoundsException();
        }
        List<ValueWrapper> values = new ArrayList<>();
        for (Record record : records) {
            values.add(record.get(index));
        }
        return values;
    }

    @Override
    public String toString() {
        if (!isSucceeded()) {
            return getErrorMessage();
        }
        int i = 0;
        List<String> rowStrs = new ArrayList<>();
        while (i < rowsSize()) {
            List<String> valueStrs = new ArrayList<>();
            for (ValueWrapper value : rowValues(i)) {
                valueStrs.add(value.toString());
            }
            rowStrs.add(String.join(",", valueStrs));
            i++;
        }
        return String.format("ColumnName: %s, Rows: %s",
                             columnNames.toString(), rowStrs.toString());
    }
}

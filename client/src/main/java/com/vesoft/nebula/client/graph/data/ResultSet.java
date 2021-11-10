/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.client.graph.data;

import com.vesoft.nebula.ErrorCode;
import com.vesoft.nebula.Row;
import com.vesoft.nebula.Value;
import com.vesoft.nebula.graph.ExecutionResponse;
import com.vesoft.nebula.graph.PlanDescription;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

public class ResultSet {
    private final ExecutionResponse response;
    private final List<String> columnNames = new ArrayList<>();
    private final String decodeType = "utf-8";
    private final int timezoneOffset;

    public static class Record implements Iterable<ValueWrapper> {
        private final List<ValueWrapper> colValues = new ArrayList<>();
        private List<String> columnNames = new ArrayList<>();

        public Record(List<String> columnNames, Row row, String decodeType, int timezoneOffset) {
            if (columnNames == null) {
                return;
            }

            if (row == null || row.values == null) {
                return;
            }

            for (Value value : row.values) {
                this.colValues.add(new ValueWrapper(value, decodeType, timezoneOffset));
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
            List<String> valueStr = new ArrayList<>();
            for (ValueWrapper v : colValues) {
                valueStr.add(v.toString());
            }
            return String.format("ColumnName: %s, Values: %s",
                columnNames.toString(), valueStr.toString());
        }

        /**
         * get column value by index
         * @param index the index of the rows
         * @return ValueWrapper
         */
        public ValueWrapper get(int index) {
            if (index >= columnNames.size()) {
                throw new IllegalArgumentException(
                        String.format("Cannot get field because the key '%d' out of range", index));
            }
            return this.colValues.get(index);
        }

        /**
         * get column value by column name
         * @param columnName the columna name
         * @return ValueWrapper
         */
        public ValueWrapper get(String columnName) {
            int index = columnNames.indexOf(columnName);
            if (index == -1) {
                throw new IllegalArgumentException(
                        "Cannot get field because the columnName '"
                            + columnName + "' is not exists");
            }
            return this.colValues.get(index);
        }

        /**
         * get all values
         * @return the list of ValueWrapper
         */
        public List<ValueWrapper> values() {
            return colValues;
        }

        /**
         * get the size of record
         * @return int the size of columns
         */
        public int size() {
            return this.columnNames.size();
        }

        /**
         * if the column name exists
         * @param columnName the column name
         * @return boolean
         */
        public boolean contains(String columnName) {
            return this.columnNames.contains(columnName);
        }

    }

    public ResultSet(ExecutionResponse resp, int timezoneOffset) {
        if (resp == null) {
            throw new RuntimeException("Input an null `ExecutionResponse' object");
        }
        this.response = resp;
        this.timezoneOffset = timezoneOffset;
        if (resp.data != null) {
            // space name's charset is 'utf-8'
            for (byte[] column : resp.data.column_names) {
                this.columnNames.add(new String(column));
            }
        }
    }

    /**
     * the execute result is succeeded
     * @return boolean
     */
    public boolean isSucceeded() {
        return response.error_code == ErrorCode.SUCCEEDED;
    }

    /**
     * the result data is empty
     * @return boolean
     */
    public boolean isEmpty() {
        return response.data == null || response.data.rows.isEmpty();
    }

    /**
     * get errorCode of execute result
     * @return int
     */
    public int getErrorCode() {
        return response.error_code.getValue();
    }

    /**
     * get the space name of current session
     * @return String
     */
    public String getSpaceName() {
        if (response.space_name == null) {
            return "";
        }
        // space name's charset is 'utf-8'
        return new String(response.space_name);
    }

    /**
     * get the error message of the execute result
     * @return String
     */
    public String getErrorMessage() {
        if (response.error_msg == null) {
            return "";
        }
        // error message's charset is 'utf-8'
        return new String(response.error_msg);
    }

    /**
     * get the comment from the server
     * @return String
     */
    public String getComment() {
        if (response.comment == null) {
            return "";
        }
        // error message's charset is 'utf-8'
        return new String(response.comment);
    }

    /**
     * get latency of the query execute time
     * @return int
     */
    public int getLatency() {
        return response.latency_in_us;
    }

    /**
     * get the PalnDesc
     * @return PlanDescription
     */
    public PlanDescription getPlanDesc() {
        return response.getPlan_desc();
    }

    /**
     * get keys of the dataset
     * @return the list of String
     */
    public List<String> keys() {
        return columnNames;
    }

    /**
     * get column names of the dataset
     * @return the
     */
    public List<String> getColumnNames() {
        return columnNames;
    }

    /**
     * get the size of rows
     * @return int
     */
    public int rowsSize() {
        if (response.data == null) {
            return 0;
        }
        return response.data.rows.size();
    }

    /**
     * get row values with the row index
     * @param index the index of the rows
     * @return Record
     */
    public Record rowValues(int index) {
        if (response.data == null) {
            throw new RuntimeException("Empty data");
        }
        if (index >= response.data.rows.size()) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return new Record(columnNames, response.data.rows.get(index), decodeType, timezoneOffset);
    }

    /**
     * get col values on the column key
     * @param columnName the column name
     * @return the list of ValueWrapper
     */
    public List<ValueWrapper> colValues(String columnName) {
        if (response.data == null) {
            throw new RuntimeException("Empty data");
        }
        int index = columnNames.indexOf(columnName);
        if (index < 0) {
            throw new ArrayIndexOutOfBoundsException();
        }
        List<ValueWrapper> values = new ArrayList<>();
        for (int i = 0; i < response.data.rows.size(); i++) {
            values.add(new ValueWrapper(response.data.rows.get(i).values.get(index),
                                        decodeType,
                                        timezoneOffset));
        }
        return values;
    }

    /**
     * get all rows, the value is the origin value, the string is binary
     * @return the list of Row
     */
    public List<Row> getRows() {
        if (response.data == null) {
            throw new RuntimeException("Empty data");
        }
        return response.data.rows;
    }

    @Override
    public String toString() {
        // When error, print the raw data directly
        if (!isSucceeded()) {
            return response.toString();
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

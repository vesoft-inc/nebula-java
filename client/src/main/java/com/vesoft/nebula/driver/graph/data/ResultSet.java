/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.data;

import com.google.common.base.Charsets;
import com.vesoft.nebula.driver.graph.ErrorCode;
import com.vesoft.nebula.driver.graph.decode.ResultTable;
import com.vesoft.nebula.driver.graph.decode.Row;
import com.vesoft.nebula.proto.graph.ExecuteResponse;
import com.vesoft.nebula.proto.graph.QueryStats;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class ResultSet {

    private final ExecuteResponse response;
    private final ResultTable     resultTable;
    private final List<String>    columnNames = new ArrayList<>();
    private final Charset         charset     = Charsets.UTF_8;

    private boolean isEmpty = false;

    private volatile AtomicInteger index = new AtomicInteger(0);

    private final long size;

    public static class Record implements Iterable<ValueWrapper> {

        private final List<ValueWrapper> colValues   = new ArrayList<>();
        private       List<String>       columnNames = new ArrayList<>();

        public Record(List<String> columnNames, Row row) {
            if (columnNames == null) {
                return;
            }

            if (row == null || row.getValues().isEmpty()) {
                return;
            }

            for (ValueWrapper value : row.getValues()) {
                this.colValues.add(value);
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
         *
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
         *
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
         *
         * @return the list of ValueWrapper
         */
        public List<ValueWrapper> values() {
            return colValues;
        }

        /**
         * get the size of record columns
         *
         * @return int the size of columns
         */
        public int size() {
            return this.columnNames.size();
        }

        /**
         * if the column name exists
         *
         * @param columnName the column name
         * @return boolean
         */
        public boolean contains(String columnName) {
            return this.columnNames.contains(columnName);
        }

    }

    public ResultSet(ExecuteResponse resp) {
        if (resp == null) {
            throw new RuntimeException("got null object for server's response");
        }
        this.resultTable = new ResultTable(resp.getResult());
        this.response = resp;
        if (!resp.hasResult()) {
            size = 0;
        } else {
            this.columnNames.addAll(resultTable.getColumnNames());
            size = resultTable.getTotalNumRecords();
        }
        isEmpty = size == 0;
    }

    /**
     * the execute result is succeeded
     *
     * @return boolean
     */
    public boolean isSucceeded() {
        return ErrorCode.SUCCESSFUL_COMPLETION.code.equals(
            response.getStatus().getCode().toString(charset));
    }

    /**
     * the result data is empty
     *
     * @return boolean
     */
    public boolean isEmpty() {
        return isEmpty;
    }

    /**
     * get error code of execute result
     * return {@link ErrorCode}
     *
     * @return String
     */
    public ErrorCode getErrorCode() {
        return ErrorCode.find(
            response.getStatus().getCode().toString(charset));
    }

    /**
     * get error message of execute result
     *
     * @return String
     */
    public String getErrorMessage() {
        return response.getStatus().getMessage().toString(charset);
    }


    /**
     * get latency of the query execute time
     *
     * @return int
     */
    public long getLatency() {
        return response.getSummary().getElapsedTime().getTotalServerTimeUs();
    }

    /**
     * get the PlanDesc
     *
     * @return string
     */
    public PlanInfoNode getPlanDesc() {
        return new PlanInfoNode(response.getSummary().getPlanInfo());
    }

    /**
     * get column names of the dataset
     *
     * @return the list of result columns
     */
    public List<String> getColumnNames() {
        return columnNames;
    }

    /**
     * get the size of rows
     *
     * @return int
     */
    public long rowSize() {
        if (isEmpty) {
            return 0;
        }
        return size;
    }

    /**
     * if the ResultSet has row Record
     */
    public boolean hasNext() {
        if (isEmpty) {
            return false;
        }
        return index.get() < size;
    }

    /**
     * get the next row Record
     */
    public Record next() {
        if (!hasNext()) {
            throw new NoSuchElementException("no more row record data");
        }
        Row row = this.resultTable.next();
        index.getAndIncrement();
        return new Record(columnNames, row);
    }


    /**
     * get extra info for result
     *
     * @return map for extra info
     */
    public ExtraInfo getExtraInfo() {
        if (!response.hasSummary()) {
            return new ExtraInfo();
        }
        ExtraInfo  extraInfo  = new ExtraInfo();
        QueryStats queryStats = response.getSummary().getQueryStats();
        extraInfo.setAffectedNodes(queryStats.getNumAffectedNodes());
        extraInfo.setAffectedEdges(queryStats.getNumAffectedEdges());
        extraInfo.setCursor(response.getCursor().toString(charset));
        extraInfo.setParseTimeUs(response.getSummary().getElapsedTime().getParseTimeUs());
        extraInfo.setBuildTimeUs(response.getSummary().getElapsedTime().getBuildTimeUs());
        extraInfo.setOptimizeTimeUs(response.getSummary().getElapsedTime().getOptimizeTimeUs());
        extraInfo.setSerializeTimeUs(response.getSummary().getElapsedTime().getSerializeTimeUs());
        extraInfo.setTotalServerTimeUs(response.getSummary()
                                           .getElapsedTime()
                                           .getTotalServerTimeUs());
        extraInfo.setNumWarnings(response.getSummary().getNumWarnings());
        extraInfo.setNumExportedRecords(queryStats.getNumExportedRecords());
        extraInfo.setExportPaths(queryStats.getExportedPathsList());
        return extraInfo;
    }

    @Override
    public String toString() {
        if (!isSucceeded()) {
            return response.getStatus().getMessage().toString(charset);
        }

        return String.format("ColumnName: %s, RowSize: %s, Latency: %d",
                             columnNames, rowSize(),
                             getLatency());
    }
}

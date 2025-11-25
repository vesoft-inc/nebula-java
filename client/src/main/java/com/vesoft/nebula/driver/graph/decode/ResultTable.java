/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.decode;

import com.google.common.base.Charsets;
import com.vesoft.nebula.driver.graph.data.ValueWrapper;
import com.vesoft.nebula.driver.graph.decode.datatype.DataType;
import com.vesoft.nebula.driver.graph.decode.datatype.ValueTypeParser;
import com.vesoft.nebula.driver.graph.decode.struct.ResultGraphSchemas;
import com.vesoft.nebula.proto.graph.ValueType;
import com.vesoft.nebula.proto.graph.VectorBatch;
import com.vesoft.nebula.proto.graph.VectorResultTable;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class ResultTable {

    private final VectorResultTable resultTable;
    private       ByteOrder         byteOrder;
    private       ValueParser       parser;
    private       int               numBatches;
    private final List<String>      columnNames          = new ArrayList<>();
    private final List<DataType>    columnDataTypes      = new ArrayList<>();
    private       int               batchIndex           = 0;
    private       Batch             currentBatch;
    private       int               currentBatchRowIndex = 0;


    public ResultTable(VectorResultTable table) {
        if (table == null || !table.hasMeta()) {
            this.resultTable = null;
            return;
        }

        this.resultTable = table;
        ResultGraphSchemas graphSchemas =
                new ResultGraphSchemas(table.getMeta().getGraphSchemaList());
        int timeZoneOffset = table.getMeta().getTimeZoneOffset();
        if (table.getMeta().getIsLittleEndian()) {
            this.byteOrder = ByteOrder.LITTLE_ENDIAN;
        } else {
            this.byteOrder = ByteOrder.BIG_ENDIAN;
        }

        this.parser = new ValueParser(graphSchemas, timeZoneOffset, byteOrder);
        this.numBatches = table.getBatchCount();
        List<VectorBatch> batches = new ArrayList<>(table.getBatchList());
        if (this.numBatches != batches.size()) {
            throw new RuntimeException("the number of batch if not equal to numBatches");
        }
        this.columnNames.addAll(table.getMeta().getRowType().getColumnNamesList());
        ValueTypeParser valueTypeParser = new ValueTypeParser(byteOrder);
        for (ValueType type : table.getMeta().getRowType().getColumnTypesList()) {
            DataType dataType = valueTypeParser.getDataType(new BytesReader(type.getValueType()));
            this.columnDataTypes.add(dataType);
        }
        if (!table.getBatchList().isEmpty()) {
            currentBatch = new Batch(table.getBatchList().get(0), byteOrder);
        }
    }

    /**
     * get the column names of the response
     *
     * @return list of column names
     */
    public List<String> getColumnNames() {
        return this.columnNames;
    }


    /**
     * get the total data records size of the response
     *
     * @return total number of records
     */
    public long getTotalNumRecords() {
        return resultTable.getMeta().getNumRecords();
    }


    /**
     * parse row record from batch
     *
     * @param index the position of each vector in current batch
     * @return Row
     */
    private Row getRowByIndex(int index) {
        Row row = new com.vesoft.nebula.driver.graph.decode.Row();
        for (int i = 0; i < currentBatch.getVectorsCount(); i++) {
            ValueWrapper value = parser.decodeValueWrapper(currentBatch.getVectors(i),
                                                           columnDataTypes.get(i),
                                                           index);
            row.addValue(value);
        }
        return row;
    }


    /**
     * get the next row data
     *
     * @return Row
     */
    public Row next() {
        if (currentBatch == null) {
            throw new RuntimeException("no more batch data");
        }
        // each VectorMetaData has the same numRecords value,
        // just use the first one th get the numRecord for this batch
        int currentBatchRowSize = 0;
        if (currentBatch.getVectorsCount() != 0) {
            currentBatchRowSize = currentBatch.getBatchRowSize();
        }
        // the current batch is empty or already finished the batch, jump the batch
        if (currentBatch.getVectorsCount() == 0 || currentBatchRowIndex >= currentBatchRowSize) {
            batchIndex++;
            if (batchIndex >= numBatches) {
                throw new RuntimeException("no more batch data");
            }
            // reset currentBatchRowIndex to 0
            currentBatchRowIndex = 0;
            currentBatch = new Batch(resultTable.getBatch(batchIndex), byteOrder);
        }

        // resolve the current batch
        Row row = getRowByIndex(currentBatchRowIndex);
        currentBatchRowIndex++;
        return row;
    }

}

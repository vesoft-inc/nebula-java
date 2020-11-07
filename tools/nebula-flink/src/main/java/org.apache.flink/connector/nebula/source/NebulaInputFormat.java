/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package org.apache.flink.connector.nebula.source;

import com.facebook.thrift.TException;
import com.vesoft.nebula.Client;
import com.vesoft.nebula.client.graph.ResultSet;
import com.vesoft.nebula.client.meta.MetaClientImpl;
import com.vesoft.nebula.client.storage.StorageClientImpl;
import com.vesoft.nebula.client.storage.processor.Processor;
import com.vesoft.nebula.client.storage.processor.ScanEdgeProcessor;
import com.vesoft.nebula.client.storage.processor.ScanVertexProcessor;
import com.vesoft.nebula.data.Property;
import com.vesoft.nebula.data.PropertyDef;
import com.vesoft.nebula.data.Result;
import org.apache.flink.api.common.io.DefaultInputSplitAssigner;
import org.apache.flink.api.common.io.RichInputFormat;
import org.apache.flink.api.common.io.statistics.BaseStatistics;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.nebula.connection.NebulaConnectionProvider;
import org.apache.flink.connector.nebula.sink.AbstractNebulaOutPutFormat;
import org.apache.flink.connector.nebula.statement.ExecutionOptions;
import org.apache.flink.core.io.GenericInputSplit;
import org.apache.flink.core.io.InputSplit;
import org.apache.flink.core.io.InputSplitAssigner;
import org.apache.flink.types.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class NebulaInputFormat extends RichInputFormat<Row, InputSplit> {
    protected static final Logger LOG = LoggerFactory.getLogger(NebulaInputFormat.class);
    private static final long serialVersionUID = 902031944252613459L;


    protected ExecutionOptions executionOptions;
    protected NebulaConnectionProvider connectionProvider;


    protected transient Client client;
    private transient MetaClientImpl metaClient;
    private transient StorageClientImpl storageClient;

    protected RowTypeInfo rowTypeInfo;
    protected Boolean hasNext = false;
    protected List<com.vesoft.nebula.data.Row> rows;

    protected Iterator<com.vesoft.nebula.data.Row> resultIter;
    private NebulaInputFormatConverter formatConverter;

    private long scannedRows;

    public NebulaInputFormat(NebulaConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public void configure(Configuration configuration) {
        // do nothing
    }

    @Override
    public void openInputFormat() throws IOException {
        try {
            client = connectionProvider.getClient();
            metaClient = (MetaClientImpl) client;
            storageClient = new StorageClientImpl(metaClient);
        } catch (TException e) {
            LOG.error("connect storage client error, ", e);
            throw new IOException("connect storage client error, ", e);
        }
        rows = new ArrayList<>();
        if(executionOptions.getDataType().isVertex()){
            formatConverter = new NebulaRowVertexInputFormatConverter();
        } else{
            formatConverter = new NebulaRowEdgeInputFormatConverter();
        }
    }

    @Override
    public void closeInputFormat() throws IOException {
        try {
            if (client != null) {
                connectionProvider.close();
            }
        } catch (Exception e) {
            LOG.error("close client error,", e);
            throw new IOException("close client error,", e);
        }
    }

    @Override
    public BaseStatistics getStatistics(BaseStatistics baseStatistics) throws IOException {
        return baseStatistics;
    }

    @Override
    public InputSplit[] createInputSplits(int numSplit) throws IOException {
        return new GenericInputSplit[]{new GenericInputSplit(0,1)};
    }

    @Override
    public InputSplitAssigner getInputSplitAssigner(InputSplit[] inputSplits) {
        return new DefaultInputSplitAssigner(inputSplits);
    }

    @Override
    public void open(InputSplit inputSplit) throws IOException {
        if (inputSplit != null) {
            Map<String, List<String>> returnCols = new HashMap<>();
            returnCols.put(executionOptions.getLabel(), executionOptions.getFields());

            Processor processor;
            Iterator iterator;
            if (executionOptions.getDataType().isVertex()) {
                iterator = storageClient.scanVertex(executionOptions.getGraphSpace(), returnCols, executionOptions.isAllCols(), executionOptions.getLimit(), executionOptions.getStartTime(), executionOptions.getEndTime());
                processor = new ScanVertexProcessor(metaClient);
            } else {
                iterator = storageClient.scanEdge(executionOptions.getGraphSpace(), returnCols, executionOptions.isAllCols(), executionOptions.getLimit(), executionOptions.getStartTime(), executionOptions.getEndTime());
                processor = new ScanEdgeProcessor(metaClient);
            }

            while (iterator.hasNext()) {
                Result result = processor.process(executionOptions.getGraphSpace(), iterator.next());
                List<com.vesoft.nebula.data.Row> dataset = result.getRows(executionOptions.getLabel());
                rows.addAll(dataset);
            }
            resultIter = rows.iterator();
            hasNext = resultIter.hasNext();
        }
    }

    @Override
    public boolean reachedEnd() throws IOException {
        return !hasNext;
    }

    @Override
    public Row nextRecord(Row reuse) throws IOException {
        if (!hasNext) {
            return null;
        }
        scannedRows++;
        Row row = (Row) formatConverter.convert(resultIter.next());
        hasNext = resultIter.hasNext();
        return row;
    }

    @Override
    public void close() throws IOException {
        LOG.info("Closing split (scanned {} rows)", scannedRows);
    }

    public NebulaInputFormat setExecutionOptions(ExecutionOptions executionOptions) {
        this.executionOptions = executionOptions;
        return this;
    }

}

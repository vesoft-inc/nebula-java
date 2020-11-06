/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package org.apache.flink.connector.nebula.source;

import com.vesoft.nebula.client.meta.MetaClientImpl;
import com.vesoft.nebula.client.storage.StorageClientImpl;
import com.vesoft.nebula.client.storage.processor.Processor;
import com.vesoft.nebula.client.storage.processor.ScanEdgeProcessor;
import com.vesoft.nebula.client.storage.processor.ScanVertexProcessor;
import com.vesoft.nebula.data.Result;
import com.vesoft.nebula.data.Row;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.nebula.connection.NebulaConnectionProvider;
import org.apache.flink.connector.nebula.connection.NebulaStorageConnectionProvider;
import org.apache.flink.connector.nebula.statement.ExecutionOptions;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class NebulaSourceFunction extends RichSourceFunction<Row> {

    private static final Logger LOG = LoggerFactory.getLogger(NebulaSourceFunction.class);

    private static final long serialVersionUID = -4864517634021753949L;

    private MetaClientImpl metaClient;
    private StorageClientImpl storageClient;
    private final NebulaConnectionProvider connectionProvider;
    private ExecutionOptions executionOptions;

    public NebulaSourceFunction(NebulaConnectionProvider connectionProvider) {
        super();
        this.connectionProvider = connectionProvider;
    }


    /**
     * open nebula client
     */
    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);

        metaClient = (MetaClientImpl) connectionProvider.getClient();
        storageClient = (StorageClientImpl) new NebulaStorageConnectionProvider().getClient(metaClient);
    }

    /**
     * close nebula client
     */
    @Override
    public void close() throws Exception {
        connectionProvider.close();
    }

    /**
     * execute nebula statement
     */
    @Override
    public void run(SourceContext<Row> sourceContext) throws Exception {

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
            List<Row> rows = result.getRows(executionOptions.getLabel());
            for (Row row : rows) {
                sourceContext.collect(row);
            }
        }
    }

    @Override
    public void cancel() {
        try {
            connectionProvider.close();
        } catch (Exception e) {
            LOG.error("cancel exception:{}", e);
        }
    }

    public NebulaSourceFunction setExecutionOptions(ExecutionOptions executionOptions) {
        this.executionOptions = executionOptions;
        return this;
    }
}

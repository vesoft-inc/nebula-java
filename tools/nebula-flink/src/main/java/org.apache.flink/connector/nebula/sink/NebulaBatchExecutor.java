/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package org.apache.flink.connector.nebula.sink;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.vesoft.nebula.client.graph.async.AsyncGraphClientImpl;
import com.vesoft.nebula.graph.ErrorCode;
import org.apache.flink.connector.nebula.statement.EdgeExecutionOptions;
import org.apache.flink.connector.nebula.statement.ExecutionOptions;
import org.apache.flink.connector.nebula.statement.VertexExecutionOptions;
import org.apache.flink.connector.nebula.utils.NebulaConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NebulaBatchExecutor<T> {
    private static final Logger LOG = LoggerFactory.getLogger(NebulaBatchOutputFormat.class);

    final private ExecutionOptions executionOptions;
    final private NebulaBufferedRow nebulaBufferedRow;

    public NebulaBatchExecutor(ExecutionOptions executionOptions) {
        this.executionOptions = executionOptions;
        this.nebulaBufferedRow = new NebulaBufferedRow();
    }

    /**
     * put record into buffer
     *
     * @param record represent vertex or edge
     */
    void addToBatch(T record) {
        boolean isVertex = executionOptions.getDataType().isVertex();

        NebulaOutputFormatConverter converter;
        if (isVertex) {
            converter = new NebulaRowVertexOutputFormatConverter((VertexExecutionOptions) executionOptions);
        } else {
            converter = new NebulaRowEdgeOutputFormatConverter((EdgeExecutionOptions) executionOptions);
        }
        String value = converter.createValue(record, executionOptions.getPolicy());
        if (value == null) {
            return;
        }
        nebulaBufferedRow.putRow(value);
    }

    /**
     * execute the insert statement
     *
     * @param client Asynchronous graph client
     */
    ListenableFuture executeBatch(AsyncGraphClientImpl client) {
        String propNames = String.join(NebulaConstant.COMMA, executionOptions.getFields());
        String values = String.join(NebulaConstant.COMMA, nebulaBufferedRow.getRows());
        String exec = String.format(NebulaConstant.BATCH_INSERT_TEMPLATE, executionOptions.getDataType(), executionOptions.getLabel(), propNames, values);
        LOG.debug("insert statement={}", exec);
        ListenableFuture<Optional<Integer>> execResult = client.execute(exec);
        Futures.addCallback(execResult, new FutureCallback<Optional<Integer>>() {
            @Override
            public void onSuccess(Optional<Integer> integerOptional) {
                if (integerOptional.isPresent()) {
                    if (integerOptional.get() == ErrorCode.SUCCEEDED) {
                        LOG.info("batch insert Succeed");
                    } else {
                        LOG.error(String.format("batch insert Error: %d",
                                integerOptional.get()));
                    }
                } else {
                    LOG.error("batch insert Error");
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                LOG.error("batch insert Error");
            }
        });
        nebulaBufferedRow.clean();
        return execResult;
    }
}

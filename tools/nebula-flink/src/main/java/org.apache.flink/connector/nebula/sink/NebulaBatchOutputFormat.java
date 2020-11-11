/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package org.apache.flink.connector.nebula.sink;

import com.google.common.util.concurrent.ListenableFuture;
import com.vesoft.nebula.client.graph.async.AsyncGraphClientImpl;
import org.apache.flink.connector.nebula.connection.NebulaConnectionProvider;
import org.apache.flink.connector.nebula.statement.ExecutionOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

public class NebulaBatchOutputFormat<T> extends AbstractNebulaOutPutFormat<T>{
    private static final Logger LOG = LoggerFactory.getLogger(NebulaBatchOutputFormat.class);
    private static final long serialVersionUID = 8846672119763512586L;

    private  volatile AtomicLong numPendingRow;
    private AsyncGraphClientImpl graphClient;
    private NebulaBatchExecutor nebulaBatchExecutor;
    public ListenableFuture future;

    public NebulaBatchOutputFormat(NebulaConnectionProvider connectionProvider){
        super(connectionProvider);
    }

    /**
     * prepare all resources
     */
    @Override
    public void open(int i, int i1) throws IOException {
        super.open(i, i1);
        graphClient = (AsyncGraphClientImpl)client;
        numPendingRow = new AtomicLong(0);
        nebulaBatchExecutor = new NebulaBatchExecutor(executionOptions);
    }

    /**
     * write one record to buffer
     */
    @Override
    public final synchronized void writeRecord(T row) throws IOException {
        nebulaBatchExecutor.addToBatch(row);

        if (numPendingRow.incrementAndGet() >= executionOptions.getBatch()) {
            commit();
        }
    }

    /**
     * commit batch insert statements
     */
    private synchronized void commit() throws IOException {
        graphClient.switchSpace(executionOptions.getGraphSpace());
        future = nebulaBatchExecutor.executeBatch(graphClient);
        numPendingRow.compareAndSet(executionOptions.getBatch(),0);
    }

    /**
     * commit the batch write operator before release connection
     */
    @Override
    public  final synchronized void close() throws IOException {
        if(numPendingRow.get() > 0){
            commit();
        }
        while(!future.isDone()){
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                LOG.error("sleep interrupted, ", e);
            }
        }

        super.close();
    }

    /**
     * commit the batch write operator
     */
    @Override
    public synchronized void flush() throws IOException {
        while(numPendingRow.get() != 0){
            commit();
        }
    }

    public AbstractNebulaOutPutFormat setExecutionOptions(ExecutionOptions executionOptions) {
        this.executionOptions = executionOptions;
        return this;
    }
}

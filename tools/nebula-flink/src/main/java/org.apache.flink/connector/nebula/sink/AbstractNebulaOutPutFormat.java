/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package org.apache.flink.connector.nebula.sink;

import com.facebook.thrift.TException;
import com.vesoft.nebula.Client;
import org.apache.flink.api.common.io.RichOutputFormat;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.nebula.connection.NebulaConnectionProvider;
import org.apache.flink.connector.nebula.statement.ExecutionOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Flushable;
import java.io.IOException;

public class AbstractNebulaOutPutFormat<T> extends RichOutputFormat<T> implements Flushable {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractNebulaOutPutFormat.class);

    private static final long serialVersionUID = 2351208692013860405L;

    protected transient Client client;

    protected ExecutionOptions executionOptions;

    final private NebulaConnectionProvider connectionProvider;

    public AbstractNebulaOutPutFormat(NebulaConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public void flush() throws IOException {
    }

    @Override
    public void configure(Configuration configuration) {

    }

    /**
     * prepare the connection resource
     */
    @Override
    public void open(int i, int i1) throws IOException {
        try {
            establishConnection();
        } catch (TException e) {
            LOG.error("connect client error,", e);
            throw new IOException("connect client error", e);
        }
    }

    protected void establishConnection() throws TException {
        client = connectionProvider.getClient();
    }

    /**
     * write data into NebulaBufferedRow
     */
    @Override
    public synchronized void writeRecord(T row) throws IOException {
    }

    /**
     * release the connection resource
     */
    @Override
    public void close() throws IOException{
        try {
            client.close();
        } catch (Exception e) {
            throw new IOException("failed to close client", e);
        }
    }

    /**
     * set {@link ExecutionOptions} for AbstractNebulaOutPutFormat
     */
    public AbstractNebulaOutPutFormat setExecutionOptions(ExecutionOptions executionOptions) {
        this.executionOptions = executionOptions;
        return this;
    }

}

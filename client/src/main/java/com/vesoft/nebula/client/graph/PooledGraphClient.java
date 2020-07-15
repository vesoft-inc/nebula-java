/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.graph;

import com.facebook.thrift.TException;
import com.vesoft.nebula.graph.ErrorCode;
import com.vesoft.nebula.utils.GraphConstants;
import java.util.Properties;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Graph Client with thread safe and connection failover able
 */
public class PooledGraphClient implements GraphClient {
    private static final Logger LOG = LoggerFactory.getLogger(PooledGraphClient.class);
    private final GenericObjectPool<GraphClientImpl> pool;
    private final ConnectionContext context;
    private volatile boolean closed = false;

    public PooledGraphClient(Properties props) {
        this.context = new ConnectionContext()
                .withHosts(props.getProperty(GraphConstants.CONN_ADDRESS))
                .withConnectionRetry(props.getProperty(GraphConstants.CONN_RETRY_COUNT, "4"))
                .withPoolSize(props.getProperty(GraphConstants.CONN_POOL_SIZE, "4"))
                .withExecutionRetry(props.getProperty(GraphConstants.EXEC_RETRY_COUNT, "4"))
                .withTimeout(props.getProperty(GraphConstants.EXEC_TIMEOUT, "10000"))
                .withFailoverRetry(props.getProperty(GraphConstants.FAILOVER_RETRY_COUNT, "3"))
                .withUser(props.getProperty(GraphConstants.CONN_USERNAME, "nebula"))
                .withPassword(props.getProperty(GraphConstants.CONN_PASSWORD, "nebula"));
        this.pool = new GenericObjectPool<GraphClientImpl>(new GraphClientFactory(context));
        pool.setMaxTotal(context.getPoolSize());
        pool.setTestOnReturn(true); // do validate when return GraphClient to the pool
        pool.setLifo(false); // borrow connection in FIFO model
    }

    @Override
    public void setUser(String user) {
        context.withUser(user);
    }

    @Override
    public void setPassword(String password) {
        context.withPassword(password);
    }

    @Override
    public void close() throws Exception {
        if (closed) {
            return;
        }
        pool.close();
        closed = true;
    }

    @Override
    public int switchSpace(String space) {
        context.withSpace(space);
        return 0;
    }

    @Override
    public int execute(String statement) {
        int res = ErrorCode.E_EXECUTION_ERROR;
        try {
            int failoverCount = context.getFailoverRetry();
            for (int retry = 0; retry <= failoverCount; retry++) {
                GraphClientImpl client = null;
                try {
                    client = pool.borrowObject();
                    if (client.getUsedSpace() != context.getSpace()) {
                        client.switchSpace(context.getSpace());
                    }
                    res = client.execute(statement);
                } finally {
                    returnback(client);
                }
                if (res == ErrorCode.SUCCEEDED) {
                    break;
                } else {
                    LOG.warn("execute statement on target host {} failed, "
                            + "will failover to another host, return code is {}.",
                            client.getAddress(), res);
                }
            }
        } catch (Exception e) {
            LOG.error("execute statement (" + statement + ") failed.", e);
        }
        return res;
    }

    @Override
    public ResultSet executeQuery(String statement)
            throws ConnectionException, NGQLException, TException {
        ResultSet resSet = null;
        try {
            int failoverCount = context.getFailoverRetry();
            for (int retry = 0; retry <= failoverCount; retry++) {
                GraphClientImpl client = null;
                try {
                    client = pool.borrowObject();
                    if (client.getUsedSpace() != context.getSpace()) {
                        client.switchSpace(context.getSpace());
                    }
                    resSet = client.executeQuery(statement);
                } catch (ConnectionException | TException e) {
                    LOG.warn("execute query on target host {} failed, "
                            + "will failover to another host, exception message is {}",
                            client.getAddress(), e.getMessage());
                } finally {
                    returnback(client);
                }
                if (resSet != null) {
                    break;
                }
            }
        } catch (Exception e) {
            throw new NGQLException("execute query (" + statement + ") failed.",
                    e, ErrorCode.E_EXECUTION_ERROR);
        }
        return resSet;
    }

    private void returnback(GraphClientImpl client) {
        if (client != null) {
            try {
                pool.returnObject(client);
            } catch (Exception e) {
                LOG.error("returnback client to pool error.", e);
            }
        }
    }

    @Override
    public int connect() throws TException {
        return 0; // NOOP
    }

    /**
     * change the target graphd hosts that client will connect to
     */
    public void changeAddress(String hosts) {
        GraphClientFactory factory = (GraphClientFactory) pool.getFactory();
        context.withHosts(hosts);
        factory.setConnectionHosts(context.getHosts());
    }
}

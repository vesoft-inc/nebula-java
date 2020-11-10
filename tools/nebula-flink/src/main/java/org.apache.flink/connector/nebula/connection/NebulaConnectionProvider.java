/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package org.apache.flink.connector.nebula.connection;

import com.facebook.thrift.TException;
import com.vesoft.nebula.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class NebulaConnectionProvider {
    private static final Logger LOG = LoggerFactory.getLogger(NebulaConnectionProvider.class);

    protected transient volatile Client client;

    abstract public Client getClient() throws TException;

    Client reConnectClient() throws Exception {
        try {
            client.close();
        } catch (TException e) {
            LOG.info("Nebula connection close failed. ", e);
        } finally {
            client = null;
        }
        client = getClient();
        return client;
    }

    public void close() throws Exception {
        client.close();
    }
}

/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package org.apache.flink.connector.nebula.connection.client;

import com.facebook.thrift.TException;
import com.vesoft.nebula.Client;
import com.vesoft.nebula.client.meta.MetaClientImpl;
import org.apache.flink.connector.nebula.utils.NebulaUtils;

public class NebulaMetaClient extends AbstractNebulaClient {
    @Override
    public Client connectClient(String address, String username, String password) throws TException {
        Client client = new MetaClientImpl(NebulaUtils.getHostAndPorts(address), timeout, connectTimeout, connectionRetry, executionRetry);
        client.connect();
        return client;
    }
}

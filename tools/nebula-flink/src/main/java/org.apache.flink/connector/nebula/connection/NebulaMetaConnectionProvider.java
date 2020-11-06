/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package org.apache.flink.connector.nebula.connection;

import com.facebook.thrift.TException;
import com.vesoft.nebula.Client;
import com.vesoft.nebula.client.meta.MetaClientImpl;
import com.vesoft.nebula.client.storage.StorageClientImpl;
import org.apache.flink.connector.nebula.connection.client.AbstractNebulaClient;
import org.apache.flink.connector.nebula.connection.client.NebulaMetaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class NebulaMetaConnectionProvider extends NebulaConnectionProvider implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(NebulaMetaConnectionProvider.class);
    private static final long serialVersionUID = -1045337416133033961L;

    private final NebulaClientOptions nebulaClientOptions;

    public NebulaMetaConnectionProvider(NebulaClientOptions nebulaClientOptions){
        this.nebulaClientOptions = nebulaClientOptions;
    }

    @Override
    public Client getClient() throws TException {
        if(client == null){
            synchronized (this){
                if(client == null){
                    AbstractNebulaClient nebulaClient = new NebulaMetaClient();
                    client = nebulaClient.connectClient(nebulaClientOptions.getAddress(), nebulaClientOptions.getUsername(), nebulaClientOptions.getPassword());
                }
            }
        }
        return client;
    }
}

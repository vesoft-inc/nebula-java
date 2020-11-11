/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package org.apache.flink.connector.nebula.connection;

import com.facebook.thrift.TException;
import com.vesoft.nebula.Client;
import com.vesoft.nebula.client.meta.MetaClientImpl;
import org.apache.flink.connector.nebula.connection.client.AbstractNebulaClient;
import org.apache.flink.connector.nebula.connection.client.NebulaGraphClient;
import org.apache.flink.connector.nebula.connection.client.NebulaMetaClient;
import org.apache.flink.connector.nebula.connection.client.NebulaStorageClient;

import java.io.Serializable;

public class NebulaStorageConnectionProvider extends NebulaConnectionProvider implements Serializable {

    private static final long serialVersionUID = -3822165815516596188L;

    private NebulaClientOptions nebulaClientOptions;

    public NebulaStorageConnectionProvider(NebulaClientOptions nebulaClientOptions){
        this.nebulaClientOptions = nebulaClientOptions;
    }

    public NebulaStorageConnectionProvider(){}

    @Override
    public Client getClient() throws TException {
        if(client == null){
            synchronized (this){
                if(client == null){
                    AbstractNebulaClient nebulaClient = new NebulaStorageClient();
                    client = nebulaClient.connectClient(nebulaClientOptions.getAddress(), nebulaClientOptions.getUsername(), nebulaClientOptions.getPassword());
                }
            }
        }
        return client;
    }

    public Client getClient(MetaClientImpl metaClient) throws TException{
        if(client == null){
            synchronized (this){
                if(client == null){
                    if(!metaClient.isConnected()){
                        metaClient.connect();
                    }
                    NebulaStorageClient nebulaClient = new NebulaStorageClient();
                    client = nebulaClient.connectClient(metaClient);
                }
            }
        }
        return client;
    }
}

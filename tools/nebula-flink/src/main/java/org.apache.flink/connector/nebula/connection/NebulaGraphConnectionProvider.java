/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package org.apache.flink.connector.nebula.connection;

import com.facebook.thrift.TException;
import com.vesoft.nebula.Client;
import org.apache.flink.connector.nebula.connection.client.AbstractNebulaClient;
import org.apache.flink.connector.nebula.connection.client.NebulaGraphClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class NebulaGraphConnectionProvider extends NebulaConnectionProvider implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(NebulaGraphConnectionProvider.class);

    private static final long serialVersionUID = 8392002706492085208L;

    private final NebulaClientOptions nebulaClientOptions;

    public NebulaGraphConnectionProvider(NebulaClientOptions nebulaClientOptions){
        this.nebulaClientOptions = nebulaClientOptions;
    }

    @Override
    public Client getClient() throws TException {
        if(client == null || !client.isConnected()){
            synchronized (this){
                if(client == null){
                    AbstractNebulaClient nebulaClient = new NebulaGraphClient();
                    client = nebulaClient.connectClient(nebulaClientOptions.getAddress(), nebulaClientOptions.getUsername(), nebulaClientOptions.getPassword());
                } else{
                	client.connect();
				}
            }
        }
        return client;
    }
}

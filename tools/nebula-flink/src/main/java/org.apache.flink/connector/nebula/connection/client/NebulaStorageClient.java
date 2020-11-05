package org.apache.flink.connector.nebula.connection.client;

import com.facebook.thrift.TException;
import com.vesoft.nebula.Client;
import com.vesoft.nebula.client.meta.MetaClientImpl;
import com.vesoft.nebula.client.storage.StorageClientImpl;

public class NebulaStorageClient extends AbstractNebulaClient {
    @Override
    public Client connectClient(String address, String username, String password) throws TException {
        AbstractNebulaClient Client = new NebulaMetaClient();
        return new StorageClientImpl((MetaClientImpl) Client.connectClient(address, username, password));
    }

    public Client connectClient(MetaClientImpl metaClient){
        return new StorageClientImpl(metaClient);
    }
}

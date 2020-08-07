package com.vesoft.nebula.tools.importer.test.mock;

import com.facebook.thrift.TException;
import com.facebook.thrift.protocol.TBinaryProtocol;
import com.facebook.thrift.protocol.TProtocol;
import com.facebook.thrift.transport.TSocket;
import com.facebook.thrift.transport.TTransport;
import com.facebook.thrift.transport.TTransportException;
import com.vesoft.nebula.client.meta.MetaClientImpl;
import com.vesoft.nebula.client.storage.StorageClientImpl;
import com.vesoft.nebula.graph.AuthResponse;
import com.vesoft.nebula.graph.GraphService;
import com.vesoft.nebula.storage.StorageService;

public class MockQueryClient {
    static private MetaClientImpl metaClient;
    static private StorageClientImpl storageClient;
    static private GraphService.Client rpcClient;
    static private int port=9999;

    public static void main(String[] args) throws TException {
        metaClient = new MetaClientImpl("127.0.0.1", port);

        TTransport transport = new TSocket("127.0.0.1", port);
        TProtocol protocol = new TBinaryProtocol(transport);
        rpcClient = new GraphService.Client(protocol);
        transport.open();
        AuthResponse rep = rpcClient.authenticate("use1r", "password");
        System.out.println(rep.error_msg);
        System.out.println(rep.session_id);

    }
}

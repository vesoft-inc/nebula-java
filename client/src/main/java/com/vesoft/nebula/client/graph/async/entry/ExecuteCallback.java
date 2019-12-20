package com.vesoft.nebula.client.graph.async.entry;

import com.facebook.thrift.TException;
import com.facebook.thrift.async.TAsyncMethodCall;
import com.vesoft.nebula.entry.AbstractNebulaCallback;
import com.vesoft.nebula.graph.GraphService.AsyncClient;

public class ExecuteCallback extends AbstractNebulaCallback {
    @Override
    public void doComplete(TAsyncMethodCall response) throws TException {
        AsyncClient.execute_call call = (AsyncClient.execute_call) response;
        try {
            result = call.getResult();
        } catch (TException e) {
            e.printStackTrace();
        }
    }
}

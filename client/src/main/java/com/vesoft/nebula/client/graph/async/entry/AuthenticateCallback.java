package com.vesoft.nebula.client.graph.async.entry;

import com.facebook.thrift.TException;
import com.facebook.thrift.async.TAsyncMethodCall;
import com.vesoft.nebula.entry.AbstractNebulaCallback;
import com.vesoft.nebula.graph.GraphService.AsyncClient;

public class AuthenticateCallback extends AbstractNebulaCallback {
    @Override
    public void doComplete(TAsyncMethodCall response) throws TException {
        AsyncClient.authenticate_call call = (AsyncClient.authenticate_call) response;
        try {
            result = call.getResult();
        } catch (TException e) {
            e.printStackTrace();
        }
    }
}

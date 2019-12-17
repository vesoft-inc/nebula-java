package com.vesoft.nebula.client.storage.async.entry;

import com.facebook.thrift.TException;
import com.facebook.thrift.async.TAsyncMethodCall;
import com.vesoft.nebula.entry.AbstractNebulaCallback;
import com.vesoft.nebula.storage.StorageService;

public class PutCallback extends AbstractNebulaCallback {
    @Override
    public void doComplete(TAsyncMethodCall response) throws TException {
        StorageService.AsyncClient.put_call call = (StorageService.AsyncClient.put_call) response;
        try {
            result = call.getResult();
        } catch (TException e) {
            e.printStackTrace();
        }
    }
}

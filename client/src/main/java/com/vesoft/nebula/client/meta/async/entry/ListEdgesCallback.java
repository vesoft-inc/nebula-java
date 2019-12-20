/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.meta.async.entry;

import com.facebook.thrift.TException;
import com.facebook.thrift.async.TAsyncMethodCall;
import com.vesoft.nebula.entry.AbstractNebulaCallback;
import com.vesoft.nebula.meta.MetaService.AsyncClient;

public class ListEdgesCallback extends AbstractNebulaCallback {
    @Override
    public void doComplete(TAsyncMethodCall response) {
        AsyncClient.listEdges_call call = (AsyncClient.listEdges_call) response;
        try {
            result = call.getResult();
        } catch (TException e) {
            e.printStackTrace();
        }
    }
}

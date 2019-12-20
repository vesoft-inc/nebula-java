/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.examples.async;

import com.facebook.thrift.TException;
import com.google.common.base.Optional;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.vesoft.nebula.client.meta.async.AsyncMetaClientImpl;
import com.vesoft.nebula.meta.ErrorCode;
import com.vesoft.nebula.meta.IdName;
import com.vesoft.nebula.meta.ListSpacesResp;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsyncMetaClientExample {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncMetaClientExample.class);

    private static ListeningExecutorService service;

    public static void main(String[] args) throws TException {
        if (args.length != 2) {
            System.out.println("Usage: "
                    + "com.vesoft.nebula.examples.async.AsyncMetaClientExample "
                    + "<host> <port>");
            return;
        }

        service = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());
        AsyncMetaClientImpl asyncMetaClient = new AsyncMetaClientImpl(args[0],
                Integer.parseInt(args[1]));
        asyncMetaClient.connect();

        ListenableFuture<Optional<ListSpacesResp>> future = asyncMetaClient.listSpaces();
        Futures.addCallback(future, new FutureCallback<Optional<ListSpacesResp>>() {
            @Override
            public void onSuccess(Optional<ListSpacesResp> listSpacesRespOptional) {
                if (listSpacesRespOptional.isPresent()) {
                    ListSpacesResp resp = listSpacesRespOptional.get();
                    if (resp.getCode() != ErrorCode.SUCCEEDED) {
                        LOGGER.error(String.format("List Spaces Error Code: %s", resp.getCode()));
                        return;
                    }
                    for (IdName space : resp.getSpaces()) {
                        LOGGER.info(String.format("Space name: %s, Space Id: %d", space.name,
                                space.id.getSpace_id()));
                    }
                } else {
                    LOGGER.info(String.format("No Space Founded"));
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                LOGGER.error("List Spaces Error");
            }
        }, service);
    }
}

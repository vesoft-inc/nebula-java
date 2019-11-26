/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.meta.client.async;

import com.facebook.thrift.TBase;
import com.facebook.thrift.TException;
import com.google.common.base.Optional;
import com.google.common.net.HostAndPort;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.vesoft.nebula.meta.ErrorCode;
import com.vesoft.nebula.meta.IdName;
import com.vesoft.nebula.meta.ListSpacesResp;
import com.vesoft.nebula.meta.client.async.entry.ListSpaceCallback;
import com.vesoft.nebula.meta.client.entry.GetPartsAllocResult;
import com.vesoft.nebula.meta.client.entry.ListSpaceResult;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import org.checkerframework.checker.nullness.qual.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsyncMetaClientExample {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncMetaClientExample.class);

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: " + "com.vesoft.nebula.examples.AsyncMetaClientExample " +
                "<host> " + "<port>");
            return;
        }
        AsyncMetaClientImpl asyncMetaClient = new AsyncMetaClientImpl(args[0],
            Integer.parseInt(args[1]));

        try {
            ListSpaceCallback callback = asyncMetaClient.listSpaces();
            Optional<TBase> respOption = Optional.absent();
            while (!callback.checkReady()) {
                respOption = callback.getResult();
            }
            if (respOption.isPresent()) {
                ListSpacesResp resp = (ListSpacesResp) respOption.get();
                if (resp.getCode() != ErrorCode.SUCCEEDED) {
                    LOGGER.error(String.format("List Spaces Error Code: %s", resp.getCode()));
                }
                ListSpaceResult result = new ListSpaceResult();
                for (IdName space : resp.getSpaces()) {
                    result.add(space.id, space.name);
                }
                Map<Integer, String> map = result.getResult();
                LOGGER.info("---------------Spaces:--------------");
                for (Map.Entry<Integer, String> entry : map.entrySet()) {
                    LOGGER.info(String.format("Space ID: %d, ", entry.getKey())
                        + String.format("Space name: %s", entry.getValue()));
                }
                LOGGER.info("------------------------------------");
            } else {
                LOGGER.info(String.format("No Space Founded"));
            }
            asyncMetaClient.close();
        } catch (TException e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        }


        /*
        ListenableFuture<Optional<GetPartsAllocResult>> getPartsAllocResult =
                asyncMetaClient.getPartsAlloc(1);
        Futures.addCallback(
                getPartsAllocResult,
                new FutureCallback<Optional<GetPartsAllocResult>>() {
                    @Override
                    public void onSuccess(
                            @Nullable Optional<GetPartsAllocResult> getPartsAllocResultOptional) {
                        if (getPartsAllocResultOptional.isPresent()) {
                            GetPartsAllocResult result = getPartsAllocResultOptional.get();
                            Map<Integer, List<HostAndPort>> map = result.getResult();
                            LOGGER.info("---------------Spaces:--------------");
                            for (Map.Entry<Integer, List<HostAndPort>> entry : map.entrySet()) {
                                LOGGER.info(String.format("Part ID: %d, ", entry.getKey())
                                        + String.format("Addresses: %s", entry.getValue()));
                            }
                            LOGGER.info("------------------------------------");
                        } else {
                            LOGGER.info(String.format("No Part Founded"));
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        LOGGER.error("Get Parts Failed");
                    }
                },
                EXECUTOR_SERVICE);

         */
    }
}

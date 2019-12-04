/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.storage.client.handler;

import com.vesoft.nebula.data.Result;

import java.util.Iterator;

public abstract class ScanConsumer<ReqT, RespT> {

    protected abstract Result handleResponse(ReqT curRequest, RespT response);

    protected abstract Result handleResponse(ReqT curRequest, RespT response,
                                             Iterator<Integer> partIt);

    protected abstract boolean hasNext();

    public Iterator<Result<ReqT>> handle(ReqT curRequest, RespT response) {
        final Result<ReqT> result = handleResponse(curRequest, response);
        final boolean hasNext = hasNext();
        return new Iterator<Result<ReqT>>() {
            @Override
            public boolean hasNext() {
                return hasNext;
            }

            @Override
            public Result<ReqT> next() {
                return result;
            }

            @Override
            public void remove() {

            }
        };
    }

    public Iterator<Result<ReqT>> handle(ReqT curRequest, RespT response,
                                         Iterator<Integer> partIt) {
        final Result<ReqT> result = handleResponse(curRequest, response, partIt);
        final boolean hasNext = hasNext();
        return new Iterator<Result<ReqT>>() {
            @Override
            public boolean hasNext() {
                return hasNext;
            }

            @Override
            public Result<ReqT> next() {
                return result;
            }

            @Override
            public void remove() {

            }
        };
    }
}

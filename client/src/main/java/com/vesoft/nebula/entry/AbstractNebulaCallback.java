/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.entry;

import com.facebook.thrift.TBase;
import com.facebook.thrift.TException;
import com.facebook.thrift.async.AsyncMethodCallback;
import com.facebook.thrift.async.TAsyncMethodCall;
import com.google.common.base.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractNebulaCallback implements AsyncMethodCallback {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractNebulaCallback.class);

    protected volatile TBase result;

    protected AtomicBoolean isReady = new AtomicBoolean(false);

    public Optional<?> getResult() throws InterruptedException {
        while (!checkReady()) {
            Thread.sleep(100);
        }
        if (result == null) {
            return Optional.absent();
        }
        return Optional.of(result);
    }

    @Override
    public void onComplete(TAsyncMethodCall response) {
        try {
            doComplete(response);
        } catch (TException e) {
            e.printStackTrace();
        } finally {
            isReady.compareAndSet(false, true);
        }
    }

    public abstract void doComplete(TAsyncMethodCall response) throws TException;

    public boolean checkReady() {
        if (isReady.get()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onError(Exception exception) {
        LOGGER.error(String.format("onError: %s", exception.toString()));
        exception.printStackTrace();
        isReady.compareAndSet(false, true);
    }
}

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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractNebulaCallback implements AsyncMethodCallback {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractNebulaCallback.class);

    protected Lock lock = new ReentrantLock();
    protected TBase result;

    public Optional<TBase> getResult() {
        try {
            if (lock.tryLock()) {
                return Optional.of(result);
            }
        } finally {
            lock.unlock();
        }
        return Optional.absent();
    }

    @Override
    public void onComplete(TAsyncMethodCall response) {
        try {
            try {
                if (lock.tryLock()) {
                    doComplete(response);
                }
            } finally {
                lock.unlock();
            }
        } catch (TException e) {
            // Method call not finished!
            e.printStackTrace();
        }
    }

    public abstract void doComplete(TAsyncMethodCall response) throws TException;

    @Override
    public void onError(Exception exception) {
        LOGGER.error(String.format("onError: %s", exception.toString()));
        exception.printStackTrace();
    }
}

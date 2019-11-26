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
    protected boolean isReady = false;


    public Optional<TBase> getResult() {
        return result == null ? Optional.absent() : Optional.of(result);
    }

    @Override
    public void onComplete(TAsyncMethodCall response) {
        if (lock.tryLock()) {
            try {
                doComplete(response);
                isReady = true;
            } catch (TException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }

    public abstract void doComplete(TAsyncMethodCall response) throws TException;

    public boolean checkReady() {
        if (lock.tryLock()) {
            try {
                return isReady;
            } finally {
                lock.unlock();
            }
        }
        return false;
    }

    @Override
    public void onError(Exception exception) {
        LOGGER.error(String.format("onError: %s", exception.toString()));
        exception.printStackTrace();
    }
}

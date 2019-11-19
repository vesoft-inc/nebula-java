/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula;

/**
 *
 */
public class SimpleQuerySession implements Session {

    @Override
    public void connect() {

    }

    @Override
    public int execute(String sentence) {
        return 0;
    }

    @Override
    public int executeQuery(String sentence) {
        return 0;
    }

    @Override
    public void close() throws Exception {

    }
}

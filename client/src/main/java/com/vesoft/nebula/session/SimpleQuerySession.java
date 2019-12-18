/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.session;

import com.vesoft.nebula.client.graph.GraphClient;

/**
 *
 */
public class SimpleQuerySession implements Session {
    private GraphClient client;

    @Override
    public void connect() {

    }

    public int execute(String sentence) {
        return 0;
    }

    public int executeQuery(String sentence) {
        return 0;
    }

    @Override
    public void close() throws Exception {
        client.close();
    }
}

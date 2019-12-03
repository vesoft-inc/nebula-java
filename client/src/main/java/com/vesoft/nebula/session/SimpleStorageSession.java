/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.session;

import com.vesoft.nebula.storage.client.StorageClient;
import java.io.IOException;

public class SimpleStorageSession implements Session {

    private StorageClient client;

    @Override
    public void connect() {

    }

    @Override
    public void close() throws IOException {
        client.close();
    }
}

/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.storage.client;

import com.vesoft.nebula.meta.client.MetaClientImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class StorageClientExample {
    private static final Logger LOGGER = LoggerFactory.getLogger(StorageClientExample.class);
    private static final int SPACE = 1;
/*
    public static void main(String[] args) {

        try {
            MetaClientImpl metaClient = new MetaClientImpl("127.0.0.1", 28910);
            StorageClient storageClient = new StorageClientImpl(metaClient);
            if (!storageClient.put(SPACE, "f", 1)) {
                LOGGER.info("put failed");
                return;
            }
            if (storageClient.cas(SPACE, "f", 2, 1)) {
                Integer v = storageClient.get(SPACE, "f");
                LOGGER.info("CAS SUCC " + v);
            } else {
                LOGGER.info("CAS Failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

 */
}

/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.examples;

import com.google.common.base.Optional;
import com.vesoft.nebula.client.meta.MetaClientImpl;
import com.vesoft.nebula.client.storage.StorageClient;
import com.vesoft.nebula.client.storage.StorageClientImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StorageClientExample {
    private static final Logger LOGGER = LoggerFactory.getLogger(StorageClientExample.class);
    private static final String SPACE_NAME = "test";

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: com.vesoft.nebula.examples.StorageClientExample "
                    + "<meta_server_ip> <meta_server_port>");
            return;
        }

        try {
            MetaClientImpl metaClient = new MetaClientImpl(args[0], Integer.valueOf(args[1]));
            metaClient.connect();
            StorageClient storageClient = new StorageClientImpl(metaClient);
            final int count = 1000;
            for (int i = 0; i < count; i++) {
                if (!storageClient.put(SPACE_NAME, String.valueOf(i), String.valueOf(i))) {
                    LOGGER.info("put failed " + i);
                    return;
                }
            }
            for (int i = 0; i < count; i++) {
                Optional<String> valueOpt = storageClient.get(SPACE_NAME, String.valueOf(i));
                if (!valueOpt.isPresent() || !valueOpt.get().equals(String.valueOf(i))) {
                    LOGGER.info("get failed " + i);
                    return;
                }
            }
            LOGGER.info("Done");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

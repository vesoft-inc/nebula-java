/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.examples;

import com.vesoft.nebula.storage.client.StorageClient;
import com.vesoft.nebula.storage.client.StorageClientImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class StorageClientExample {
    private static final Logger LOGGER = LoggerFactory.getLogger(StorageClientExample.class.getName());
    private static final int PART = 1;

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: com.vesoft.nebula.examples.StorageClientExample <host> <port>");
        }

        try {
            try (StorageClient client = new StorageClientImpl(args[0], Integer.valueOf(args[1]))) {
                if (client.put(PART, "key", "value")) {
                    Optional<String> valueOpt = client.get(PART, "key");
                    System.out.println(valueOpt.get());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

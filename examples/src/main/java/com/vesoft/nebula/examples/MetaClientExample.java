/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.examples;

import com.vesoft.nebula.client.meta.MetaClient;
import com.vesoft.nebula.client.meta.MetaClientImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetaClientExample {
    private static final Logger LOGGER = LoggerFactory.getLogger(MetaClientExample.class);

    public static void main(String[] args) {
        try {
            MetaClient metaClient = new MetaClientImpl("127.0.0.1", 45500);
            LOGGER.info(metaClient.getPartAllocFromCache("test", 1).toString());
            LOGGER.info(metaClient.getTag("test", "test_tag").toString());
            LOGGER.info(metaClient.getEdge("test", "test_edge").toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


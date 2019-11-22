/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.meta.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetaClientExample {
    private static final Logger LOGGER = LoggerFactory.getLogger(MetaClientExample.class);

    public static void main(String[] args) {
        MetaClientImpl metaClient = new MetaClientImpl("127.0.0.1", 28910);
        if (metaClient.listSpaces()) {
            LOGGER.info("List Space SUCC");
        }

        LOGGER.info(metaClient.getPart(1, 1).toString());
        //LOGGER.info(metaClient.getTagId(1, "test").toString());
        //LOGGER.info(metaClient.getEdgeType(1, "test").toString());
    }
}


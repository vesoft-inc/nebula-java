/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.meta;

import com.facebook.thrift.TException;
import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestMetaClient extends TestCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestMetaClient.class);

    private final String address = "127.0.0.1";

    private MetaClient metaClient;

    public void setUp() throws Exception {
    }

    public void tearDown() throws Exception {
    }

    public void testFailConnect() {
        int port = 1111;
        metaClient = new MetaClient(address, port);
        try {
            metaClient.connect();
        } catch (TException e) {
            assert (true);
        }
    }

    public void testConnect() {
        int port = 45500;
        metaClient = new MetaClient(address, port);
        try {
            assert metaClient.connect() == 0;
        } catch (TException e) {
            LOGGER.error(e.getMessage());
            assert (false);
        }
    }
}

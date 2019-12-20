/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.storage.client;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class StorageClientTest {
    // TODO: We'd better mock a StorageClientImpl
    StorageTestUtils testUtils;
    private static final Logger LOGGER = LoggerFactory.getLogger(StorageClientTest.class);

    @Before
    public void before() throws Exception {
        testUtils = new StorageTestUtils();
    }

    @After
    public void after() {
        testUtils.stop();
    }

    @Test
    public void singlePartScanEdgeTest() {
    }

    @Test
    public void multiPartScanEdgeTest() {
    }

    @Test
    public void singlePartScanVertexTest() {
    }

    @Test
    public void multiPartScanVertexTest() {
    }

}

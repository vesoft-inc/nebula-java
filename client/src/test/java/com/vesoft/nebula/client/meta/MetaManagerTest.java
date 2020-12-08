/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.meta;

import com.vesoft.nebula.client.graph.data.HostAddress;
import java.util.Arrays;
import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetaManagerTest extends TestCase {
    private static final Logger LOGGER = LoggerFactory.getLogger(MetaManagerTest.class);
    MetaManager metaManager = null;


    public void setUp() throws Exception {
        MockNebulaGraph.initGraph();
        metaManager = MetaManager.getMetaManager(Arrays.asList(new HostAddress("127.0.0.1",45500)));
    }

    public void tearDown() {
    }

    public void testGetSpaceId() {
        assert (metaManager.getSpaceId("testMeta") >= 0);
    }

    public void testGetEdge() {
        assert (metaManager.getEdge("testMeta", "friend").getEdge_type() >= 0);
    }

    public void testGetSpaceParts() {
        assert (metaManager.getSpaceParts("testMeta").size() == 10);
    }
}
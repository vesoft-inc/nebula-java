/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.meta;

import com.google.common.net.HostAndPort;
import java.util.Arrays;
import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetaManagerTest extends TestCase {
    private static final Logger LOGGER = LoggerFactory.getLogger(MetaManagerTest.class);
    MetaManager metaManager = null;


    public void setUp() throws Exception {
        metaManager = MetaManager.getMetaManager(Arrays.asList(HostAndPort.fromString("127.0.0"
                + ".1:45500")));
        metaManager.metaInfo = MockMetaInfo.createMetaInfo();
    }

    public void tearDown() {
    }


    public void testGetSpaceId() {
        assert (metaManager.getSpaceId("test1") == 1);
        assert (metaManager.getSpaceId("test2") == 2);
    }

    public void testGetTagIds() {
        assert (metaManager.getTagIds("test1").size() == 1);
        assert (metaManager.getTagIds("test2").size() == 1);
    }

    public void testGetTagId() {
        assert (metaManager.getTagId("test1", "tag") == MockMetaInfo.tagId);
        assert (metaManager.getTagId("test2", "tag") == MockMetaInfo.tagId);
    }

    public void testGetTag() {
        assert (metaManager.getTag("test1", "tag").getTag_id() == MockMetaInfo.tagId);
        assert (metaManager.getTag("test2", "tag").getTag_id() == MockMetaInfo.tagId);
    }

    public void testGetEdgeIds() {
        assert (metaManager.getEdgeIds("test1").size() == 1);
        assert (metaManager.getEdgeIds("test2").size() == 1);
    }

    public void testGetEdgeId() {
        assert (metaManager.getEdgeId("test1", "edge") == MockMetaInfo.edgeId);
        assert (metaManager.getEdgeId("test2", "edge") == MockMetaInfo.edgeId);
    }

    public void testGetEdge() {
        assert (metaManager.getEdge("test1", "edge").getEdge_type() == MockMetaInfo.edgeId);
    }

    public void testGetTagName() {
        assert (metaManager.getTagName("test1", MockMetaInfo.tagId).equals("tag"));
        assert (metaManager.getTagName("test2", MockMetaInfo.tagId).equals("tag"));
    }

    public void testGetEdgeName() {
        assert (metaManager.getEdgeName("test1", MockMetaInfo.edgeId).equals("edge"));
        assert (metaManager.getEdgeName("test2", MockMetaInfo.edgeId).equals("edge"));
    }

    public void testGetLeader() {
        assert (metaManager.getLeader("test1", 1).getHostText().equals("127.0.0.1"));
        assert (metaManager.getLeader("test1", 1).getPort() == 45500);
        assert (metaManager.getLeader("test2", 1).getHostText().equals("127.0.0.1"));
        assert (metaManager.getLeader("test2", 1).getPort() == 45500);
    }

    public void testGetSpaceParts() {
        assert (metaManager.getSpaceParts("test1").size() == 2);
        assert (metaManager.getSpaceParts("test2").size() == 2);
    }

    public void testGetDecodeType() {
        assert (metaManager.getDecodeType().equals("utf-8"));
    }
}
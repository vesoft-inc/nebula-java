/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.meta;

import com.facebook.thrift.TException;
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
        try {
            assert (metaManager.getSpaceId("test1") == 1);
            assert (metaManager.getSpaceId("test2") == 2);
        } catch (TException e) {
            LOGGER.error("get spaceId error, ", e);
            assert (false);
        }
    }

    public void testGetTagIds() {
        try {
            assert (metaManager.getTagIds("test1").size() == 1);
            assert (metaManager.getTagIds("test2").size() == 1);
        } catch (TException e) {
            LOGGER.error("get tag ids error, ", e);
            assert (false);
        }
    }

    public void testGetTagId() {
        try {
            assert (metaManager.getTagId("test1", "tag") == MockMetaInfo.tagId);
            assert (metaManager.getTagId("test2", "tag") == MockMetaInfo.tagId);
        } catch (TException e) {
            LOGGER.error("get tag id error, ", e);
            assert (false);
        }
    }

    public void testGetTag() {
        try {
            assert (metaManager.getTag("test1", "tag").getTag_id() == MockMetaInfo.tagId);
            assert (metaManager.getTag("test2", "tag").getTag_id() == MockMetaInfo.tagId);
        } catch (TException e) {
            LOGGER.error("get tag error, ", e);
            assert (false);
        }
    }

    public void testGetEdgeIds() {
        try {
            assert (metaManager.getEdgeIds("test1").size() == 1);
            assert (metaManager.getEdgeIds("test2").size() == 1);
        } catch (TException e) {
            LOGGER.error("get edge ids error, ", e);
            assert (false);
        }
    }

    public void testGetEdgeId() {
        try {
            assert (metaManager.getEdgeId("test1", "edge") == MockMetaInfo.edgeId);
            assert (metaManager.getEdgeId("test2", "edge") == MockMetaInfo.edgeId);
        } catch (TException e) {
            LOGGER.error("get edge id error, ", e);
            assert (false);
        }
    }

    public void testGetEdge() {
        try {
            assert (metaManager.getEdge("test1", "edge").getEdge_type() == MockMetaInfo.edgeId);
        } catch (TException e) {
            LOGGER.error("get edge error, ", e);
            assert (false);
        }
    }

    public void testGetTagName() {
        try {
            assert (metaManager.getTagName("test1", MockMetaInfo.tagId).equals("tag"));
            assert (metaManager.getTagName("test2", MockMetaInfo.tagId).equals("tag"));
        } catch (TException e) {
            LOGGER.error("get tag name error, ", e);
            assert (false);
        }
    }

    public void testGetEdgeName() {
        try {
            assert (metaManager.getEdgeName("test1", MockMetaInfo.edgeId).equals("edge"));
            assert (metaManager.getEdgeName("test2", MockMetaInfo.edgeId).equals("edge"));
        } catch (TException e) {
            LOGGER.error("get edge name error, ", e);
            assert (false);
        }
    }

    public void testGetLeader() {
        try {
            assert (metaManager.getLeader("test1", 1).getHostText().equals("127.0.0.1"));
            assert (metaManager.getLeader("test1", 1).getPort() == 45500);
            assert (metaManager.getLeader("test2", 1).getHostText().equals("127.0.0.1"));
            assert (metaManager.getLeader("test2", 1).getPort() == 45500);
        } catch (TException e) {
            LOGGER.error("get leader error, ", e);
            assert (false);
        }
    }

    public void testGetSpaceParts() {
        try {
            assert (metaManager.getSpaceParts("test1").size() == 2);
            assert (metaManager.getSpaceParts("test2").size() == 2);
        } catch (TException e) {
            LOGGER.error("get space parts error, ", e);
            assert (false);
        }
    }

    public void testGetDecodeType() {
        assert (metaManager.getDecodeType().equals("utf-8"));
    }
}
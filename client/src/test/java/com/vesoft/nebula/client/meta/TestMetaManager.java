/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.meta;

import com.vesoft.nebula.HostAddr;
import com.vesoft.nebula.client.graph.data.HostAddress;
import com.vesoft.nebula.meta.EdgeItem;
import com.vesoft.nebula.meta.SpaceItem;
import com.vesoft.nebula.meta.TagItem;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import junit.framework.TestCase;
import org.junit.Assert;

public class TestMetaManager extends TestCase {
    MetaManager metaManager = null;

    public void setUp() throws Exception {
        MockNebulaGraph.initGraph();
        metaManager = MetaManager.getMetaManager(
                Collections.singletonList(new HostAddress("127.0.0.1", 9559)));
    }

    public void tearDown() {
    }

    public void testGetSpace() {
        assert (metaManager.getSpaceId("testMeta") > 0);
        SpaceItem spaceItem = metaManager.getSpace("testMeta");
        assert Objects.equals("testMeta", new String(spaceItem.properties.getSpace_name()));
        Assert.assertEquals(8, spaceItem.properties.getVid_type().getType_length());
        Assert.assertEquals(10, spaceItem.properties.getPartition_num());

        // test get not existed space
        try {
            metaManager.getSpace("not_existed");
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertTrue("We expected here", true);
        }
    }

    public void testGetTag() {
        // get tag id
        int tagId = metaManager.getTagId("testMeta", "person");
        assert (tagId > 0);
        // get tag item
        TagItem tag = metaManager.getTag("testMeta", "person");
        Assert.assertArrayEquals("person".getBytes(), tag.getTag_name());
        Assert.assertEquals(0, tag.getVersion());
        // test get not existed tag
        try {
            metaManager.getTag("testMeta", "not_existed");
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertTrue("We expected here", true);
        }
    }

    public void testGetEdge() {
        // get edge type
        int edgeType = metaManager.getEdgeType("testMeta", "friend");
        assert (edgeType > 0);
        // get tag item
        EdgeItem edge = metaManager.getEdge("testMeta", "friend");
        Assert.assertArrayEquals("friend".getBytes(), edge.getEdge_name());
        Assert.assertEquals(0, edge.getVersion());
        // test get not existed edge
        try {
            metaManager.getEdge("testMeta", "not_existed");
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.assertTrue("We expected here", true);
        }
    }

    public void testGetSpaceParts() {
        assert (metaManager.getSpaceParts("testMeta").size() == 10);
        Assert.assertArrayEquals(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).toArray(),
                metaManager.getSpaceParts("testMeta").toArray());

        // test get leader
        HostAddr hostAddr = metaManager.getLeader("testMeta", 1);
        Assert.assertNotNull(hostAddr);
        Assert.assertEquals(hostAddr.port, 9779);

        // test update leader
        HostAddr newHostAddr = new HostAddr("127.0.0.1", 4400);
        metaManager.updateLeader("testMeta", 1, newHostAddr);
        hostAddr = metaManager.getLeader("testMeta", 1);
        Assert.assertNotNull(hostAddr);
        Assert.assertEquals(hostAddr.port, 4400);
    }

    public void testMultiVersionSchema() {
        MockNebulaGraph.createMultiVersionTagAndEdge();
        metaManager.close();
        metaManager = MetaManager.getMetaManager(
                Collections.singletonList(new HostAddress("127.0.0.1", 9559)));
        TagItem tagItem = metaManager.getTag("testMeta", "player");
        assert (tagItem.getVersion() == 1);
        assert (tagItem.schema.getColumns().size() == 1);

        EdgeItem edgeItem = metaManager.getEdge("testMeta", "couples");
        assert (edgeItem.getVersion() == 1);
        assert (edgeItem.schema.getColumns().size() == 1);
    }
}

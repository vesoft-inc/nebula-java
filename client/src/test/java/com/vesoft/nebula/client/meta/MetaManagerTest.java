/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.meta;

import com.vesoft.nebula.client.graph.data.HostAddress;
import com.vesoft.nebula.meta.EdgeItem;
import com.vesoft.nebula.meta.TagItem;
import java.util.Arrays;
import java.util.Objects;
import junit.framework.TestCase;
import org.junit.Assert;
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

    public void testGetSpace() {
        assert (metaManager.getSpaceId("testMeta") > 0);
        assert Objects.equals("testMeta",
            new String(metaManager.getSpace("testMeta").properties.getSpace_name()));
        Assert.assertEquals(8,
            metaManager.getSpace("testMeta").properties.getVid_type().getType_length());
        Assert.assertEquals(10,
            metaManager.getSpace("testMeta").properties.getPartition_num());
    }

    public void testGetTag() {
        // get tag id
        int tagId = metaManager.getTagId("testMeta", "person");
        assert (tagId > 0);
        // get tag item
        TagItem tag = metaManager.getTag("testMeta", "person");
        Assert.assertArrayEquals("person".getBytes(), tag.getTag_name());
        Assert.assertEquals(0, tag.getVersion());
        String tagName = metaManager.getTagName("testMeta", tagId);
        Objects.equals(tagName, "person");
    }

    public void testGetEdge() {
        // get edge type
        int edgeType = metaManager.getEdgeType("testMeta", "friend");
        assert (edgeType > 0);
        // get tag item
        EdgeItem edge = metaManager.getEdge("testMeta", "friend");
        Assert.assertArrayEquals("friend".getBytes(), edge.getEdge_name());
        Assert.assertEquals(0, edge.getVersion());
        Objects.equals(metaManager.getEdgeName("testMeta", edgeType), "friend");
    }

    public void testGetSpaceParts() {
        assert (metaManager.getSpaceParts("testMeta").size() == 10);
        Assert.assertArrayEquals(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).toArray(),
            metaManager.getSpaceParts("testMeta").toArray());
    }
}
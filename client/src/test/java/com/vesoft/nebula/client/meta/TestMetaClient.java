/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.meta;

import com.facebook.thrift.TException;
import com.vesoft.nebula.client.meta.exception.ExecuteFailedException;
import com.vesoft.nebula.meta.EdgeItem;
import com.vesoft.nebula.meta.IdName;
import com.vesoft.nebula.meta.TagItem;
import java.util.List;
import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestMetaClient extends TestCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestMetaClient.class);

    private final String address = "127.0.0.1";
    private final int port = 9559;

    private MetaClient metaClient;

    public void setUp() throws Exception {
        MockNebulaGraph.initGraph();
        connect();
    }

    public void tearDown() throws Exception {
    }

    private void connect() {
        metaClient = new MetaClient(address, port);
        try {
            metaClient.connect();
        } catch (TException e) {
            assert (false);
        }
    }

    public void testFailConnect() {
        int port = 1111;
        MetaClient client = new MetaClient(address, port);
        try {
            client.connect();
        } catch (TException e) {
            assert (true);
        }
    }

    public void testGetSpaces() {
        try {
            List<IdName> spaces = metaClient.getSpaces();
            assert (spaces.size() >= 1);
            assert (metaClient.getSpace("testMeta") != null);
        } catch (TException e) {
            LOGGER.error(e.getMessage());
            assert (false);
        }
    }

    public void testGetTags() {
        try {
            List<TagItem> tags = metaClient.getTags("testMeta");
            assert (tags.size() == 1);
            assert (metaClient.getTag("testMeta", "person") != null);
        } catch (TException | ExecuteFailedException e) {
            LOGGER.error(e.getMessage());
            assert (false);
        }
    }

    public void testGetEdges() {
        try {
            List<EdgeItem> edges = metaClient.getEdges("testMeta");
            assert (edges.size() == 1);
            assert (metaClient.getEdge("testMeta", "friend") != null);
        } catch (TException | ExecuteFailedException e) {
            LOGGER.error(e.getMessage());
            assert (false);
        }
    }

    public void testGetPartsAlloc() {
        try {
            assert (metaClient.getPartsAlloc("testMeta").size() == 10);
        } catch (ExecuteFailedException | TException e) {
            LOGGER.error(e.getMessage());
            assert (false);
        }
    }

    public void testListHosts() {
        if (metaClient == null) {
            metaClient = new MetaClient(address, port);
        }
        assert (metaClient.listHosts().size() == 3);
    }
}

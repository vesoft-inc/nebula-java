/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.client.meta;

import com.facebook.thrift.TException;
import com.vesoft.nebula.client.graph.data.CASignedSSLParam;
import com.vesoft.nebula.client.graph.data.HostAddress;
import com.vesoft.nebula.client.graph.data.SSLParam;
import com.vesoft.nebula.client.graph.data.SelfSignedSSLParam;
import com.vesoft.nebula.client.graph.exception.ClientServerIncompatibleException;
import com.vesoft.nebula.client.meta.exception.ExecuteFailedException;
import com.vesoft.nebula.client.util.ProcessUtil;
import com.vesoft.nebula.meta.EdgeItem;
import com.vesoft.nebula.meta.IdName;
import com.vesoft.nebula.meta.TagItem;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import junit.framework.TestCase;
import org.junit.Assert;
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
        try {
            metaClient = new MetaClient(address, port);
            metaClient.connect();
        } catch (TException | UnknownHostException | ClientServerIncompatibleException e) {
            e.printStackTrace();
            assert (false);
        }
    }

    public void testFailConnect() {
        int port = 1111;

        try {
            MetaClient client = new MetaClient(address, port);
            client.connect();
        } catch (TException | UnknownHostException | ClientServerIncompatibleException e) {
            assert (true);
        }
    }


    public void testConnectWithVersionInWhiteList() {
        int port = 9559;
        try {
            MetaClient client = new MetaClient(address, port);
            client.setHandshakeKey("3.0.0");
            client.connect();

            client.setHandshakeKey("test");
            client.connect();
        } catch (TException | UnknownHostException | ClientServerIncompatibleException e) {
            e.printStackTrace();
            assert (false);
        }
    }


    public void testConnectWithVersionNotInWhiteList() {
        int port = 9559;
        try {
            MetaClient client = new MetaClient(address, port);
            client.setHandshakeKey("INVALID_VERSION");
            client.connect();
        } catch (ClientServerIncompatibleException e) {
            e.printStackTrace();
            assert (true);
        } catch (TException | UnknownHostException e) {
            e.printStackTrace();
            assert (false);
        }
    }

    public void testGetSpaces() {
        try {
            List<IdName> spaces = metaClient.getSpaces();
            assert (spaces.size() >= 1);
            assert (metaClient.getSpace("testMeta") != null);
        } catch (TException | ExecuteFailedException e) {
            LOGGER.error(e.getMessage());
            e.printStackTrace();
            assert (false);
        }
    }

    public void testGetTags() {
        try {
            List<TagItem> tags = metaClient.getTags("testMeta");
            Assert.assertTrue(tags.size() >= 1);
            assert (metaClient.getTag("testMeta", "person") != null);
        } catch (TException | ExecuteFailedException e) {
            e.printStackTrace();
            assert (false);
        }
    }

    public void testGetEdges() {
        try {
            List<EdgeItem> edges = metaClient.getEdges("testMeta");
            Assert.assertTrue(edges.size() >= 1);
            assert (metaClient.getEdge("testMeta", "friend") != null);
        } catch (TException | ExecuteFailedException e) {
            e.printStackTrace();
            assert (false);
        }
    }

    public void testGetPartsAlloc() {
        try {
            assert (metaClient.getPartsAlloc("testMeta").size() == 10);
        } catch (ExecuteFailedException | TException e) {
            e.printStackTrace();
            assert (false);
        }
    }

    public void testListHosts() {
        if (metaClient == null) {
            try {
                metaClient = new MetaClient(address, port);
            } catch (UnknownHostException e) {
                assert (false);
            }
        }
        assert (metaClient.listHosts().size() == 3);
    }

    public void testListOnlineHosts() {
        // stop one storage server
        String cmd = "docker stop nebula-docker-compose_storaged0_1";
        Runtime runtime = Runtime.getRuntime();
        try {
            Process p = runtime.exec(cmd);
            p.waitFor(5, TimeUnit.SECONDS);
            ProcessUtil.printProcessStatus(cmd, p);
            Thread.sleep(5000); // wait to update the storaged's status to OFFLINE
        } catch (Exception e) {
            LOGGER.error("stop docker service error, ", e);
            assert (false);
        }
        if (metaClient == null) {
            try {
                metaClient = new MetaClient(address, port);
            } catch (UnknownHostException e) {
                assert (false);
            }
        }
        assert (metaClient.listHosts().size() == 2);

        try {
            runtime.exec("docker start nebula-docker-compose_storaged0_1")
                    .waitFor(5, TimeUnit.SECONDS);
            Thread.sleep(5000);
        } catch (Exception e) {
            LOGGER.error("start docker service error,", e);
            assert (false);
        }
    }

    public void testCASignedSSLMetaClient() {
        MetaClient metaClient = null;
        try {

            // mock data with CA ssl
            MockNebulaGraph.createSpaceWithCASSL();

            SSLParam sslParam = new CASignedSSLParam(
                    "src/test/resources/ssl/root.crt",
                    "src/test/resources/ssl/client.crt",
                    "src/test/resources/ssl/client.key");

            metaClient = new MetaClient(Arrays.asList(new HostAddress(address, 8559)),
                    3000, 1, 1, true, sslParam);
            metaClient.connect();

            List<TagItem> tags = metaClient.getTags("testMetaCA");
            Assert.assertTrue(tags.size() >= 1);
            assert (metaClient.getTag("testMetaCA", "person") != null);
        } catch (Exception e) {
            LOGGER.error("test CA signed ssl meta client failed, ", e);
            assert (false);
        } finally {
            if (metaClient != null) {
                metaClient.close();
            }
        }
    }

    public void testSelfSignedSSLMetaClient() {
        MetaClient metaClient = null;
        try {

            // mock data with Self ssl
            MockNebulaGraph.createSpaceWithSelfSSL();

            SSLParam sslParam = new SelfSignedSSLParam(
                    "src/test/resources/ssl/root.crt",
                    "src/test/resources/ssl/client.key",
                    "");
            metaClient = new MetaClient(Arrays.asList(new HostAddress(address, 7559)),
                    3000, 1, 1, true, sslParam);
            metaClient.connect();

            List<TagItem> tags = metaClient.getTags("testMetaSelf");
            Assert.assertTrue(tags.size() >= 1);
            assert (metaClient.getTag("testMetaSelf", "person") != null);
        } catch (Exception e) {
            LOGGER.error("test Self signed ssl meta client failed, ", e);
            assert (false);
        } finally {
            if (metaClient != null) {
                metaClient.close();
            }
        }
    }
}

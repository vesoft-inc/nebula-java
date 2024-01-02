/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.client.storage;

import com.vesoft.nebula.client.graph.data.CASignedSSLParam;
import com.vesoft.nebula.client.graph.data.HostAddress;
import com.vesoft.nebula.client.graph.data.SSLParam;
import com.vesoft.nebula.client.storage.data.EdgeRow;
import com.vesoft.nebula.client.storage.data.EdgeTableRow;
import com.vesoft.nebula.client.storage.data.VertexRow;
import com.vesoft.nebula.client.storage.data.VertexTableRow;
import com.vesoft.nebula.client.storage.scan.ScanEdgeResult;
import com.vesoft.nebula.client.storage.scan.ScanEdgeResultIterator;
import com.vesoft.nebula.client.storage.scan.ScanVertexResult;
import com.vesoft.nebula.client.storage.scan.ScanVertexResultIterator;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class StorageClientTest {

    private final String ip = "127.0.0.1";
    private StorageClient client = null;

    @Before
    public void before() {
        MockStorageData.initGraph();
        List<HostAddress> address = Arrays.asList(new HostAddress(ip, 9559));
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            assert (false);
        }
        client = new StorageClient(address);
        client.setGraphAddress(ip + ":9669");
        client.setUser("root");
        client.setPassword("nebula");
    }

    @After
    public void after() {
        if (client != null) {
            client.close();
        }
    }

    @Test
    public void testStorageClientWithVersionInWhiteList() {
        List<HostAddress> address = Arrays.asList(new HostAddress(ip, 9559));
        StorageClient storageClient = new StorageClient(address);
        try {
            storageClient.setGraphAddress("127.0.0.1:9669");
            storageClient.setUser("root");
            storageClient.setPassword("nebula");
            storageClient.setVersion("3.0.0");
            assert (storageClient.connect());

            storageClient.setVersion("test");
            assert (storageClient.connect());
        } catch (Exception e) {
            e.printStackTrace();
            assert false;
        }
    }

    @Test
    public void testStorageClientWithVersionNotInWhiteList() {
        List<HostAddress> address = Arrays.asList(new HostAddress(ip, 9559));
        StorageClient storageClient = new StorageClient(address);
        try {
            storageClient.setGraphAddress("127.0.0.1:9669");
            storageClient.setUser("root");
            storageClient.setPassword("nebula");
            storageClient.setVersion("INVALID_VERSION");
            storageClient.connect();
            assert false;
        } catch (Exception e) {
            e.printStackTrace();
            assert (e.getMessage()
                    .contains("Current client is not compatible with the remote server"));
        }
    }

    @Test
    public void testScanVertexWithNoCol() {
        try {
            client.connect();
        } catch (Exception e) {
            e.printStackTrace();
            assert (false);
        }

        ScanVertexResultIterator resultIterator = client.scanVertex(
                "testStorage",
                "person");
        while (resultIterator.hasNext()) {
            ScanVertexResult result = null;
            try {
                result = resultIterator.next();
            } catch (Exception e) {
                e.printStackTrace();
                assert (false);
            }
            if (result.isEmpty()) {
                continue;
            }
            Assert.assertEquals(1, result.getPropNames().size());
            assert (result.getPropNames().get(0).equals("_vid"));
            assert (result.isAllSuccess());

            List<VertexRow> rows = result.getVertices();
            for (VertexRow row : rows) {
                try {
                    assert (Arrays.asList("1", "2", "3", "4", "5")
                            .contains(row.getVid().asString()));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    assert (false);
                }
                assert (row.getProps().size() == 0);
            }

            List<VertexTableRow> tableRows = result.getVertexTableRows();
            for (VertexTableRow tableRow : tableRows) {
                try {
                    assert (Arrays.asList("1", "2", "3", "4", "5")
                            .contains(tableRow.getVid().asString()));
                    assert (Arrays.asList("1", "2", "3", "4", "5")
                            .contains(tableRow.getString(0)));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    assert (false);
                }
            }
        }
    }

    @Test
    public void testScanVertexWithCols() {
        try {
            client.connect();
        } catch (Exception e) {
            e.printStackTrace();
            assert (false);
        }

        ScanVertexResultIterator resultIterator = client.scanVertex(
                "testStorage",
                "person",
                Arrays.asList("name", "age"));
        while (resultIterator.hasNext()) {
            ScanVertexResult result = null;
            try {
                result = resultIterator.next();
            } catch (Exception e) {
                e.printStackTrace();
                assert (false);
            }
            if (result.isEmpty()) {
                continue;
            }
            Assert.assertEquals(3, result.getPropNames().size());
            assert (result.getPropNames().get(0).equals("_vid"));
            assert (result.getPropNames().get(1).equals("name"));
            assert (result.getPropNames().get(2).equals("age"));
            assert (result.isAllSuccess());

            List<VertexRow> rows = result.getVertices();
            for (VertexRow row : rows) {
                try {
                    assert (Arrays.asList("1", "2", "3", "4", "5")
                            .contains(row.getVid().asString()));
                    assert (row.getProps().size() == 2);
                    assert (Arrays.asList("Tom", "Jina", "Bob", "Tim", "Viki")
                            .contains(row.getProps().get("name").asString()));
                    assert (Arrays.asList(18L, 20L, 23L, 15L, 25L)
                            .contains(row.getProps().get("age").asLong()));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    assert (false);
                }
            }

            List<VertexTableRow> tableRows = result.getVertexTableRows();
            for (VertexTableRow tableRow : tableRows) {
                try {
                    assert (Arrays.asList("1", "2", "3", "4", "5")
                            .contains(tableRow.getVid().asString()));
                    assert (Arrays.asList("1", "2", "3", "4", "5")
                            .contains(tableRow.getString(0)));
                    assert (Arrays.asList("Tom", "Jina", "Bob", "Tim", "Viki")
                            .contains(tableRow.getString(1)));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    assert (false);
                }
                assert (Arrays.asList(18L, 20L, 23L, 15L, 25L).contains(tableRow.getLong(2)));
            }
        }
    }

    @Test
    public void testScanVertexWithAllCol() {
        try {
            client.connect();
        } catch (Exception e) {
            e.printStackTrace();
            assert (false);
        }

        ScanVertexResultIterator resultIterator = client.scanVertex(
                "testStorage",
                "person",
                Arrays.asList());
        while (resultIterator.hasNext()) {
            ScanVertexResult result = null;
            try {
                result = resultIterator.next();
            } catch (Exception e) {
                e.printStackTrace();
                assert (false);
            }
            if (result.isEmpty()) {
                continue;
            }
            Assert.assertEquals(3, result.getPropNames().size());
            assert (result.getPropNames().get(0).equals("_vid"));
            assert (Arrays.asList("name", "age").contains(result.getPropNames().get(1)));
            assert (Arrays.asList("name", "age").contains(result.getPropNames().get(2)));
            assert (result.isAllSuccess());
        }
    }

    @Test
    public void testScanEdgeWithoutCol() {
        try {
            client.connect();
        } catch (Exception e) {
            e.printStackTrace();
            assert (false);
        }

        ScanEdgeResultIterator resultIterator = client.scanEdge(
                "testStorage",
                "friend");
        while (resultIterator.hasNext()) {
            ScanEdgeResult result = null;
            try {
                result = resultIterator.next();
            } catch (Exception e) {
                e.printStackTrace();
                assert (false);
            }
            if (result.isEmpty()) {
                continue;
            }
            Assert.assertEquals(3, result.getPropNames().size());
            assert (result.getPropNames().get(0).equals("_src"));
            assert (result.getPropNames().get(1).equals("_dst"));
            assert (result.getPropNames().get(2).equals("_rank"));
            assert (result.isAllSuccess());

            List<EdgeRow> rows = result.getEdges();
            for (EdgeRow row : rows) {
                try {
                    assert (Arrays.asList("1", "2", "3", "4", "5")
                            .contains(row.getSrcId().asString()));
                    assert (Arrays.asList("1", "2", "3", "4", "5")
                            .contains(row.getDstId().asString()));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    assert (false);
                }
                Assert.assertEquals(0, row.getProps().size());
            }

            List<EdgeTableRow> tableRows = result.getEdgeTableRows();
            for (EdgeTableRow tableRow : tableRows) {
                try {
                    assert (Arrays.asList("1", "2", "3", "4", "5")
                            .contains(tableRow.getSrcId().asString()));
                    assert (Arrays.asList("1", "2", "3", "4", "5")
                            .contains(tableRow.getDstId().asString()));
                    assert (Arrays.asList("1", "2", "3", "4", "5")
                            .contains(tableRow.getString(0)));
                    assert (Arrays.asList("1", "2", "3", "4", "5")
                            .contains(tableRow.getString(1)));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    assert (false);
                }
            }
        }
    }

    @Test
    public void testScanEdgeWithCols() {
        try {
            client.connect();
        } catch (Exception e) {
            e.printStackTrace();
            assert (false);
        }

        ScanEdgeResultIterator resultIterator = client.scanEdge(
                "testStorage",
                "friend",
                Arrays.asList("likeness"));
        while (resultIterator.hasNext()) {
            ScanEdgeResult result = null;
            try {
                result = resultIterator.next();
            } catch (Exception e) {
                e.printStackTrace();
                assert (false);
            }
            if (result.isEmpty()) {
                continue;
            }
            Assert.assertEquals(4, result.getPropNames().size());
            assert (result.getPropNames().get(0).equals("_src"));
            assert (result.getPropNames().get(1).equals("_dst"));
            assert (result.getPropNames().get(2).equals("_rank"));
            assert (result.getPropNames().get(3).equals("likeness"));
            assert (result.isAllSuccess());


            List<EdgeRow> rows = result.getEdges();
            for (EdgeRow row : rows) {
                try {
                    assert (Arrays.asList("1", "2", "3", "4", "5")
                            .contains(row.getSrcId().asString()));
                    assert (Arrays.asList("1", "2", "3", "4", "5")
                            .contains(row.getDstId().asString()));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    assert (false);
                }
                assert (Arrays.asList(0L).contains(row.getRank()));
                Assert.assertEquals(1, row.getProps().size());
                assert (Arrays.asList(1.0, 2.1, 3.2, 4.5, 5.9)
                        .contains(row.getProps().get("likeness").asDouble()));
            }

            List<EdgeTableRow> tableRows = result.getEdgeTableRows();
            for (EdgeTableRow tableRow : tableRows) {
                try {
                    assert (Arrays.asList("1", "2", "3", "4", "5")
                            .contains(tableRow.getSrcId().asString()));
                    assert (Arrays.asList("1", "2", "3", "4", "5")
                            .contains(tableRow.getDstId().asString()));
                    assert (Arrays.asList("1", "2", "3", "4", "5")
                            .contains(tableRow.getString(0)));
                    assert (Arrays.asList("1", "2", "3", "4", "5")
                            .contains(tableRow.getString(1)));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    assert (false);
                }
                assert (Arrays.asList(1.0, 2.1, 3.2, 4.5, 5.9).contains(tableRow.getDouble(3)));
            }
        }
    }

    @Test
    public void testScanEdgeWithAllCols() {
        try {
            client.connect();
        } catch (Exception e) {
            e.printStackTrace();
            assert (false);
        }

        ScanEdgeResultIterator resultIterator = client.scanEdge(
                "testStorage",
                "friend",
                Arrays.asList());
        while (resultIterator.hasNext()) {
            ScanEdgeResult result = null;
            try {
                result = resultIterator.next();
            } catch (Exception e) {
                e.printStackTrace();
                assert (false);
            }
            if (result.isEmpty()) {
                continue;
            }
            Assert.assertEquals(4, result.getPropNames().size());
            assert (Arrays.asList("_src", "_dst", "_rank", "likeness")
                    .contains(result.getPropNames().get(0)));
            assert (Arrays.asList("_src", "_dst", "_rank", "likeness")
                    .contains(result.getPropNames().get(1)));
            assert (Arrays.asList("_src", "_dst", "_rank", "likeness")
                    .contains(result.getPropNames().get(2)));
            assert (Arrays.asList("_src", "_dst", "_rank", "likeness")
                    .contains(result.getPropNames().get(3)));
            assert (result.isAllSuccess());
        }
    }

    @Test
    public void testCASignedSSL() {
        // start nebula service with ssl enable
        List<HostAddress> address = null;
        StorageClient sslClient = null;
        try {
            address = Arrays.asList(new HostAddress(ip, 8559));

            // mock graph data
            MockStorageData.mockCASslData();

            SSLParam sslParam = new CASignedSSLParam(
                    "src/test/resources/ssl/root.crt",
                    "src/test/resources/ssl/client.crt",
                    "src/test/resources/ssl/client.key");
            sslClient = new StorageClient(address, 1000, 1, 1, true, sslParam);
            sslClient.setGraphAddress("127.0.0.1:8669");
            sslClient.setUser("root");
            sslClient.setPassword("nebula");
            sslClient.setVersion("3.0.0");
            sslClient.connect();

            ScanVertexResultIterator resultIterator = sslClient.scanVertex(
                    "testStorageCA",
                    "person");
            assertIterator(resultIterator);
        } catch (Exception e) {
            e.printStackTrace();
            assert (false);
        } finally {
            if (sslClient != null) {
                try {
                    sslClient.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void assertIterator(ScanVertexResultIterator resultIterator) {
        int count = 0;
        while (resultIterator.hasNext()) {
            ScanVertexResult result = null;
            try {
                result = resultIterator.next();
            } catch (Exception e) {
                e.printStackTrace();
                assert (false);
            }
            if (result.isEmpty()) {
                continue;
            }
            count += result.getVertices().size();
            Assert.assertEquals(1, result.getPropNames().size());
            assert (result.getPropNames().get(0).equals("_vid"));
            assert (result.isAllSuccess());

            List<VertexRow> rows = result.getVertices();
            for (VertexRow row : rows) {
                try {
                    assert (Arrays.asList("1", "2", "3", "4", "5")
                            .contains(row.getVid().asString()));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    assert (false);
                }
                assert (row.getProps().size() == 0);
            }

            List<VertexTableRow> tableRows = result.getVertexTableRows();
            for (VertexTableRow tableRow : tableRows) {
                try {
                    assert (Arrays.asList("1", "2", "3", "4", "5")
                            .contains(tableRow.getVid().asString()));
                    assert (Arrays.asList("1", "2", "3", "4", "5")
                            .contains(tableRow.getString(0)));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    assert (false);
                }
            }
        }
        System.out.println("count:" + count);
        assert (count == 5);
    }
}

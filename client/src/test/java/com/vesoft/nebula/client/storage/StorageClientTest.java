/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.storage;

import com.vesoft.nebula.client.graph.data.HostAddress;
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
import org.junit.Before;
import org.junit.Test;

public class StorageClientTest {

    private final String ip = "127.0.0.1";
    private StorageClient client;

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
    }


    @Test
    public void testScanVertexWithNoCol() {
        try {
            client.connect();
        } catch (Exception e) {
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
            assert (result.getPropNames().size() == 1);
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
            assert (result.getPropNames().size() == 3);
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
            assert (result.getPropNames().size() == 3);
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
            assert (result.getPropNames().size() == 3);
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
                assert (row.getProps().size() == 0);
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
            assert (result.getPropNames().size() == 4);
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
                assert (row.getProps().size() == 1);
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
            assert (result.getPropNames().size() == 4);
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
}

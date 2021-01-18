/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.examples;

import com.vesoft.nebula.client.storage.StorageClient;
import com.vesoft.nebula.client.storage.data.EdgeTableRow;
import com.vesoft.nebula.client.storage.data.VertexTableRow;
import com.vesoft.nebula.client.storage.scan.ScanEdgeResult;
import com.vesoft.nebula.client.storage.scan.ScanEdgeResultIterator;
import com.vesoft.nebula.client.storage.scan.ScanVertexResult;
import com.vesoft.nebula.client.storage.scan.ScanVertexResultIterator;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StorageClientExample {
    private static final Logger LOGGER = LoggerFactory.getLogger(StorageClientExample.class);

    public static void main(String[] args) {
        StorageClient client = new StorageClient("127.0.0.1", 45509);
        try {
            client.connect();
        } catch (Exception e) {
            LOGGER.error("storage client connect error, ", e);
            System.exit(1);
        }
        scanVertex(client);
        scanEdge(client);
    }

    /**
     * Vertex Person's property in Nebula Graph:
     * first_name, last_name, gender, birthday
     * Tom          Li        男       2010
     */
    public static void scanVertex(StorageClient client) {
        ScanVertexResultIterator iterator = client.scanVertex(
                "test",
                "person",
                Arrays.asList("first_name", "last_name", "gender", "birthday"));

        while (iterator.hasNext()) {
            ScanVertexResult result = null;
            try {
                result = iterator.next();
            } catch (Exception e) {
                LOGGER.error("scan error, ", e);
                System.exit(1);
            }
            System.out.println(result.getPropNames());
            System.out.println("vid : " + result.getVertex("Tom"));
            System.out.println(result.getVidVertices());

            System.out.println("result vertex table view:");
            List<VertexTableRow> vertexTableRows = result.getVertexTableRows();
            for (VertexTableRow vertex : vertexTableRows) {
                System.out.println("_vid: " + vertex.getVid());
                System.out.println(vertex.getValues());
                try {
                    System.out.println("last_name: " + vertex.getString(1));
                } catch (UnsupportedEncodingException e) {
                    LOGGER.error("decode String error, ", e);
                }
            }
            System.out.println(result.getVertices());
        }
    }

    /**
     * Edge Friend's property in Nebula Graph:
     * degree
     * 1.0
     */
    public static void scanEdge(StorageClient client) {
        ScanEdgeResultIterator iterator = client.scanEdge(
                "test",
                "friend",
                Arrays.asList("degree"));

        while (iterator.hasNext()) {
            ScanEdgeResult result = null;
            try {
                result = iterator.next();
            } catch (Exception e) {
                LOGGER.error("scan error, ", e);
                System.exit(1);
            }
            System.out.println(result.getPropNames());
            System.out.println(result.getEdges());

            System.out.println("result edge table view:");
            List<EdgeTableRow> edgeTableRows = result.getEdgeTableRows();
            for (EdgeTableRow edge : edgeTableRows) {
                System.out.println("_src:" + edge.getDstId());
                System.out.println("_dst:" + edge.getDstId());
                System.out.println("_rank:" + edge.getRank());
                System.out.println(edge.getValues());
            }
        }
    }
}

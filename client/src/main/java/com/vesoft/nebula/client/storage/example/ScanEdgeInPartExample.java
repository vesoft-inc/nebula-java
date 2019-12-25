/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.storage.example;

import com.google.common.net.HostAndPort;
import com.vesoft.nebula.client.meta.MetaClient;
import com.vesoft.nebula.client.meta.MetaClientImpl;
import com.vesoft.nebula.client.storage.StorageClient;
import com.vesoft.nebula.client.storage.StorageClientImpl;
import com.vesoft.nebula.storage.ScanEdge;
import com.vesoft.nebula.storage.ScanEdgeResponse;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScanEdgeInPartExample {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScanEdgeInPartExample.class);
    private static MetaClient metaClient;
    private static StorageClient storageClient;

    private static void scanEdge(String space, int part) {
        LOGGER.info("Start to scan space " + space + " part " + part);
        try {
            Iterator<ScanEdgeResponse> iterator = storageClient.scanEdge(space, part);
            while (iterator.hasNext()) {
                process(iterator.next());
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private static void process(ScanEdgeResponse result) {
        for (ScanEdge row : result.edge_data) {
            LOGGER.info("row: " + new String(row.getKey()));
        }
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: + com.vesoft.nebula.examples.StorageClientExample "
                    + "<meta_server_ip> <meta_server_port>");
            return;
        }

        try {
            MetaClientImpl metaClientImpl = new MetaClientImpl(args[0], Integer.valueOf(args[1]));
            metaClientImpl.connect();
            metaClient = metaClientImpl;

            StorageClientImpl storageClientImpl = new StorageClientImpl(metaClientImpl);
            storageClient = storageClientImpl;

            for (Map.Entry<String, Map<Integer, List<HostAndPort>>> spaceEntry :
                    metaClient.getPartsAllocFromCache().entrySet()) {
                String space = spaceEntry.getKey();
                for (Integer part : spaceEntry.getValue().keySet()) {
                    scanEdge(space, part);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

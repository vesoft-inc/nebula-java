/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.storage.client.example;

import com.vesoft.nebula.HostAddr;
import com.vesoft.nebula.data.Result;
import com.vesoft.nebula.data.Row;
import com.vesoft.nebula.meta.client.MetaClient;
import com.vesoft.nebula.meta.client.MetaClientImpl;
import com.vesoft.nebula.storage.ScanEdgeRequest;
import com.vesoft.nebula.storage.client.StorageClient;
import com.vesoft.nebula.storage.client.StorageClientImpl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScanEdgeInPartExample {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScanEdgeInPartExample.class);
    private static MetaClient metaClient;
    private static StorageClient storageClient;
    private static int count = 0;

    private static void scanEdge(int space, int part) {
        LOGGER.info("Start to scan space " + space  + " part " + part);
        try {
            Iterator<Result<ScanEdgeRequest>> iterator = storageClient.scanEdge(space, part);
            Result<ScanEdgeRequest> result = iterator.next();
            process(result);
            while (iterator.hasNext()) {
                ScanEdgeRequest nextRequest = iterator.next().getNextRequest();
                iterator = storageClient.scanEdge(nextRequest);
                result = iterator.next();
                process(result);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private static void process(Result<ScanEdgeRequest> result) {
        for (List<Row> rows : result.getRows().values()) {
            count += rows.size();
        }
        LOGGER.info("scanned " + count + " rows");
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: + com.vesoft.nebula.examples.StorageClientExample "
                    + "<meta_server_ip> <meta_server_port>");
            return;
        }

        try {
            MetaClientImpl metaClientImpl = new MetaClientImpl(args[0], Integer.valueOf(args[1]));
            metaClient = metaClientImpl;
            storageClient = new StorageClientImpl(metaClientImpl);

            for (Map.Entry<Integer, Map<Integer, List<HostAddr>>> spaceEntry :
                    metaClient.getParts().entrySet()) {
                int space = spaceEntry.getKey();
                for (Integer part : spaceEntry.getValue().keySet()) {
                    scanEdge(space, part);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

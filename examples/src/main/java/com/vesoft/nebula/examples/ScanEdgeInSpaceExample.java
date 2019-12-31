/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.examples;

import com.vesoft.nebula.client.meta.MetaClient;
import com.vesoft.nebula.client.meta.MetaClientImpl;
import com.vesoft.nebula.client.storage.StorageClient;
import com.vesoft.nebula.client.storage.StorageClientImpl;
import com.vesoft.nebula.client.storage.processor.ScanEdgeProcessor;
import com.vesoft.nebula.data.Result;
import com.vesoft.nebula.storage.ScanEdgeResponse;
import java.util.Iterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScanEdgeInSpaceExample {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScanEdgeInSpaceExample.class);
    private static MetaClient metaClient;
    private static StorageClient storageClient;
    private static ScanEdgeProcessor processor;

    private static void scanEdge(String space) {
        LOGGER.info("Start to scan space " + space);
        try {
            Iterator<ScanEdgeResponse> iterator =
                    storageClient.scanEdge(space, 100, 0L, Long.MAX_VALUE);
            ScanEdgeResponse result = iterator.next();
            process(space, result);
            while (iterator.hasNext()) {
                ScanEdgeResponse response = iterator.next();
                process(space, response);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private static void process(String space, ScanEdgeResponse response) {
        Result result = processor.process(space, response);
        LOGGER.info("process " + result.getSize() + " edges");
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

            processor = new ScanEdgeProcessor(metaClientImpl);

            for (String space : metaClient.getPartsAllocFromCache().keySet()) {
                scanEdge(space);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

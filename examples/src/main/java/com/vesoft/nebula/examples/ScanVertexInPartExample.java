/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.examples;

import com.google.common.net.HostAndPort;
import com.vesoft.nebula.client.meta.MetaClient;
import com.vesoft.nebula.client.meta.MetaClientImpl;
import com.vesoft.nebula.client.storage.StorageClient;
import com.vesoft.nebula.client.storage.StorageClientImpl;
import com.vesoft.nebula.client.storage.processor.ScanVertexProcessor;
import com.vesoft.nebula.data.Result;
import com.vesoft.nebula.storage.ScanVertexResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScanVertexInPartExample {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScanVertexInPartExample.class);
    private static MetaClient metaClient;
    private static StorageClient storageClient;
    private static ScanVertexProcessor processor;

    private static void scanVertex(String space, int part, Map<String, List<String>> returnCols,
                                   boolean allCols) {
        LOGGER.info("Start to scan space " + space + " part " + part);
        try {
            Iterator<ScanVertexResponse> iterator =
                    storageClient.scanVertex(space, part, returnCols, allCols,
                            100, 0L, Long.MAX_VALUE);
            while (iterator.hasNext()) {
                ScanVertexResponse response = iterator.next();
                if (response == null) {
                    LOGGER.error("Error occurs while scan vertex");
                    break;
                }
                process(space, response);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private static void process(String space, ScanVertexResponse response) {
        Result result = processor.process(space, response);
        LOGGER.info("process " + result.getSize() + " vertices");
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: + com.vesoft.nebula.examples.ScanVertexInPartExample "
                    + "<meta_server_ip> <meta_server_port>");
            return;
        }

        try {
            MetaClientImpl metaClientImpl = new MetaClientImpl(args[0], Integer.valueOf(args[1]));
            metaClientImpl.connect();
            metaClient = metaClientImpl;

            StorageClientImpl storageClientImpl = new StorageClientImpl(metaClientImpl);
            storageClient = storageClientImpl;

            processor = new ScanVertexProcessor(metaClientImpl);

            // Specify the tag name and prop name we need, Map<tagNem, List<propName>>
            // If no property name is specified, none of the property returned. Or you can
            // set the allCols to true, which will return all columns of edge types in returnCols
            // no matter what in propNames
            Map<String, List<String>> returnCols = new HashMap<>();
            String tagName = "student";
            List<String> propNames = new ArrayList<>();
            propNames.add("gender");
            propNames.add("name");
            returnCols.put(tagName, propNames);

            boolean allCols = false;

            for (Map.Entry<String, Map<Integer, List<HostAndPort>>> spaceEntry :
                    metaClient.getPartsAllocFromCache().entrySet()) {
                String space = spaceEntry.getKey();
                for (Integer part : spaceEntry.getValue().keySet()) {
                    scanVertex(space, part, returnCols, allCols);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

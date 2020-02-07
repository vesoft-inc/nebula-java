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
import com.vesoft.nebula.data.Row;
import com.vesoft.nebula.storage.ScanEdgeResponse;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScanEdgeInSpaceExample {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScanEdgeInSpaceExample.class);
    private static MetaClient metaClient;
    private static StorageClient storageClient;
    private static ScanEdgeProcessor processor;
    private static String CSV_PATH = "edge.csv";
    private static FileWriter fileWriter;
    private static CSVPrinter csvPrinter;

    private static void scanEdge(String space, Map<String, List<String>> returnCols,
                                 boolean allCols) {
        LOGGER.info("Start to scan space " + space);
        try {
            Iterator<ScanEdgeResponse> iterator =
                    storageClient.scanEdge(space, returnCols, allCols, 100, 0L, Long.MAX_VALUE);
            ScanEdgeResponse result = iterator.next();
            process(space, result);
            while (iterator.hasNext()) {
                ScanEdgeResponse response = iterator.next();
                if (response == null) {
                    LOGGER.error("Error occurs while scan edge");
                    break;
                }
                process(space, response);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    // Put result into a csv file
    private static void process(String space, ScanEdgeResponse response) throws IOException {
        // Convert the response to a Result
        Result result = processor.process(space, response);
        // Get the corresponding rows by edgeName
        List<Row> edgeRows = result.getRows("select");
        for (Row row : edgeRows) {
            List<String> fields = new ArrayList<>();
            // For an edge, we have 3 default properties: src, type, dst,
            fields.add(String.valueOf(row.getDefaultProperties()[0].getValue()));
            fields.add(String.valueOf(row.getDefaultProperties()[2].getValue()));
            // Get the specified property: "grade"
            fields.add(String.valueOf(row.getProperties()[0].getValue()));
            csvPrinter.printRecord(fields);
        }
        LOGGER.info("process " + result.getSize() + " edges");
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: + com.vesoft.nebula.examples.ScanEdgeInSpaceExample "
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

            // Specify the edge name and prop name we need, Map<edgeName, List<propName>>
            // If no property name is specified, none of the property returned. Or you can
            // set the allCols to true, which will return all columns of edge types in returnCols
            // no matter what in propNames
            Map<String, List<String>> returnCols = new HashMap<>();
            List<String> propNames = new ArrayList<>();
            propNames.add("grade");
            returnCols.put("select", propNames);

            boolean allCols = false;

            CSVFormat csvFormat = CSVFormat.DEFAULT;
            fileWriter = new FileWriter(CSV_PATH);
            csvPrinter = new CSVPrinter(fileWriter, csvFormat);

            for (String space : metaClient.getPartsAllocFromCache().keySet()) {
                scanEdge(space, returnCols, allCols);
            }

            fileWriter.flush();
            fileWriter.close();
            csvPrinter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

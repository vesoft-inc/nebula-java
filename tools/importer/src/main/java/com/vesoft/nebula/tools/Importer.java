/* Copyright (c) 2018 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.geometry.S2CellId;
import com.google.common.geometry.S2LatLng;
import com.google.common.net.HostAndPort;

import com.vesoft.nebula.graph.client.GraphClient;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.vesoft.nebula.tools.reader.Reader;
import com.vesoft.nebula.tools.reader.CsvReader;
import com.vesoft.nebula.tools.reader.ReaderFactory;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.log4j.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

public class Importer {

    private static final Logger LOGGER = Logger.getLogger(Importer.class.getClass());
    private static final Importer INSTANCE = new Importer();

    private ExecutorService executor;
    private Options options;
    private List<HostAndPort> hostAndPorts;
    private Reader reader;
    private CSVPrinter csvPrinter;


    private Importer() {
    }

    public static Importer getInstance() {
        return INSTANCE;
    }

    private List<Long> indexCells(double lat, double lng) {
        S2LatLng s2LatLng = S2LatLng.fromDegrees(lat, lng);
        S2CellId s2CellId = S2CellId.fromLatLng(s2LatLng);

        ArrayList<Long> cellIds = new ArrayList<Long>();
        for (int i = Constant.minCellLevel; i < Constant.maxCellLevel; ++i) {
            cellIds.add(s2CellId.parent(i).id());
        }

        return cellIds;
    }

    public void runMultiJob(Options options) throws Exception {
        final long startTime = System.currentTimeMillis();

        this.options = options;
        this.hostAndPorts = options.getHostPort();
        long rowCounter = 0;
        int taskCnt = 0;
        int failedTaskCnt = 0;


        try {
            reader = ReaderFactory.get(ReaderFactory.ReaderType.CSV, options);
            FileWriter fileWriter = new FileWriter(options.errorPath.toFile(), true);
            csvPrinter = new CSVPrinter(fileWriter, CSVFormat.DEFAULT);

            executor = Executors.newFixedThreadPool(options.jobNum);
            CompletionService<Integer> completionService = new ExecutorCompletionService<>(executor);

            while (reader.hasNext()) {
                List<List<String>> records = reader.next();
                rowCounter += records.size();
                completionService.submit(new InsertTask(records));
                taskCnt++;
            }

            for (int i = 0; i < taskCnt; ++i) {
                Integer code = completionService.take().get();
                if (code != 0) {
                    failedTaskCnt++;
                }
            }

            long interval = System.currentTimeMillis() - startTime;
            if (options.isStatistic) {
                LOGGER.info(String.format("Row Counter   : %d", rowCounter));
                LOGGER.info(String.format("Time Interval : %d", interval));
                LOGGER.info(String.format("Total task    : %d, Failed : %d", taskCnt, failedTaskCnt));
            }

        } finally {
            executor.shutdown();

            if (failedTaskCnt == 0) {
                Files.delete(options.errorPath);
            } else {
                csvPrinter.flush();
            }

            csvPrinter.close();
            reader.close();
        }
    }

    private class InsertTask implements Callable {
        private List<List<String>> records;

        public InsertTask(List<List<String>> records) {
            this.records = records;
        }

        private List<String> buildGeoEdgeKey(List<String> record) {
            double lat = Double.parseDouble(record.get(0));
            double lng = Double.parseDouble(record.get(1));
            long dstId = Long.parseLong(record.get(2));

            List<Long> cells = indexCells(lat, lng);
            ArrayList<String> geoKeys = new ArrayList<>();
            for (Long cellId : cells) {
                geoKeys.add(String.format(Constant.INSERT_EDGE_VALUE_TEMPLATE, cellId, dstId, 0, ""));
            }

            return geoKeys;
        }

        private String buildVertex(List<String> record) {
            long id = Long.parseLong(record.get(0));
            String vertexValue = Joiner.on(", ").join(record.subList(1, record.size()));
            LOGGER.trace(String.format("vertex id: %d, value: %s", id,
                    vertexValue));

            return String.format(Constant.INSERT_VERTEX_VALUE_TEMPLATE, id, vertexValue);
        }

        private String buildEdge(List<String> record) {
            long source = Long.parseLong(record.get(0));
            long target = Long.parseLong(record.get(1));
            if (options.hasRanking) {
                long ranking = Long.parseLong(record.get(2));
                List<String> edgeList = record.subList(3, record.size());
                String edgeValue = Joiner.on(", ").join(edgeList);
                LOGGER.trace(String.format("edge source: %d, target: %d, ranking:"
                                + " %d, value: %s", source, target,
                        ranking, edgeValue));
                return String.format(Constant.INSERT_EDGE_VALUE_TEMPLATE, source,
                        target, ranking, edgeValue);
            } else {
                List<String> edgeList = record.subList(2, record.size());
                String edgeValue = Joiner.on(", ").join(edgeList);
                LOGGER.trace(String.format("edge source: %d, target: %d, value:"
                        + " %s", source, target, edgeValue));
                return String.format(
                        Constant.INSERT_EDGE_VALUE_WITHOUT_RANKING_TEMPLATE, source,
                        target, edgeValue);
            }

        }

        @Override
        public Integer call() {
            final long startTime = System.currentTimeMillis();
            Integer retCode = 0;
            try {
                GraphClient client = ClientManager.getClient(hostAndPorts, options);
                List<String> values = new ArrayList<>(records.size());

                for (List<String> record : records) {
                    if ((options.type.equals(Constant.VERTEX) && record.size() < 1)
                            || (options.type.equals(Constant.EDGE) && record.size() < 2)) {
                        // We support empty property now.
                        LOGGER.warn(String.format("Skip : %s", record));
                        continue;
                    }


                    if (options.isGeo) {
                        List<String> vals = buildGeoEdgeKey(record);
                        values.addAll(vals);
                    } else {
                        switch (options.type.toLowerCase()) {
                            case Constant.VERTEX:
                                values.add(buildVertex(record));
                                break;
                            case Constant.EDGE:
                                values.add(buildEdge(record));
                                break;
                            default:
                                LOGGER.error("Type should be vertex or edge");
                                break;
                        }
                    }
                }

                String exec = String.format(Constant.BATCH_INSERT_TEMPLATE,
                        options.type, options.schemaName, options.columns,
                        Joiner.on(", ").join(values));
                LOGGER.debug(String.format("Execute: %s", exec));
                retCode = client.execute(exec);
                if (retCode != 0) {
                    synchronized (csvPrinter) {
                        csvPrinter.printRecords(records);
                    }
                    LOGGER.info(String.format("Insert batch failed: %d, cost %d ms",
                            records.size(), System.currentTimeMillis() - startTime));
                }
            } catch (ClientManager.GetClientFailException e) {
                throw new RuntimeException(e.getMessage());
            } catch (IOException e) {
                LOGGER.error("IOException: ", e);
            } finally {
                return retCode;
            }
        }
    }

    public static void main(String[] args) {
        Options options = new Options();
        CmdLineParser cmdLineParser = new CmdLineParser(options);
        try {
            cmdLineParser.parseArgument(args);
            if (options.help) {
                cmdLineParser.printUsage(System.out);
                return;
            }
            options.checkOptions();
            LOGGER.info(options.toString());

            Importer.getInstance().runMultiJob(options);
        } catch (CmdLineException e) {
            LOGGER.error("Parse options error: ", e);
            cmdLineParser.printUsage(System.err);
        } catch (Exception e) {
            LOGGER.error("Import error: ", e);
            cmdLineParser.printUsage(System.err);
        }
    }


}


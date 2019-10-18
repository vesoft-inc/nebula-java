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
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.log4j.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

public class Importer {

    private static final Logger LOGGER = Logger.getLogger(Importer.class.getClass());
    private static final Importer INSTANCE = new Importer();

    private ExecutorService executor;
    private Options options;
    private List<HostAndPort> hostAndPorts;
    private CSVParser csvParser;
    private CSVPrinter csvPrinter;


    private Importer() {
    }

    public static Importer getInstance() {
        return INSTANCE;
    }

    private void readContent(String file) throws IOException {
        if (file.toLowerCase().startsWith("hdfs://")) {
            dfsProcessor(file);
        } else {
            localProcessor(file);
        }
    }

    private void localProcessor(String file) throws IOException {
        Path filePath = Paths.get(file);
        if (!Files.exists(filePath) || !Files.isReadable(filePath)) {
            throw new IllegalArgumentException(file + " is not exist or not readable");
        }
        LOGGER.info(String.format("Reading Local FileSystem: %s", file));
        this.csvParser = CSVParser.parse(
                new File(file),
                Charset.forName("UTF-8"),
                CSVFormat.DEFAULT
                         .withFirstRecordAsHeader()
                         .withIgnoreEmptyLines()
                         .withTrim());
    }

    private void dfsProcessor(String filePath) throws IOException {
        Configuration configuration = new Configuration();
        FileSystem fileSystem = FileSystem.get(URI.create(filePath), configuration);
        LOGGER.info(String.format("Reading HDFS: %s", filePath));
        try (InputStream inputStream = fileSystem.open(
                new org.apache.hadoop.fs.Path(filePath))) {
             this.csvParser = CSVParser.parse(
                     inputStream,
                     Charset.forName("UTF-8"),
                     CSVFormat.DEFAULT
                             .withFirstRecordAsHeader()
                             .withIgnoreEmptyLines()
                             .withTrim());
        }
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

        try {
            this.options = options;
            this.hostAndPorts = options.getHostPort();
            long rowCounter = 0;
            Integer taskCnt = 0;

            readContent(options.file);
            FileWriter fileWriter = new FileWriter(options.errorPath.toFile(), true);
            csvPrinter = new CSVPrinter(fileWriter, CSVFormat.DEFAULT);

            executor = Executors.newFixedThreadPool(options.jobNum);
            CompletionService<Integer> completionService = new ExecutorCompletionService<>(executor);
            CsvRecordBatchIterator iterator = new CsvRecordBatchIterator(csvParser.iterator(), options.batchSize);

            while (iterator.hasNext()) {
                List<CSVRecord> records = iterator.next();
                rowCounter += records.size();
                completionService.submit(new InsertTask(records));
                taskCnt++;
            }

            Integer failedTaskCnt = 0;
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

            if (failedTaskCnt == 0) {
                Files.delete(options.errorPath);
            } else {
                csvPrinter.flush();
            }
        } finally {
            csvParser.close();
            csvPrinter.close();
            executor.shutdown();
        }
    }

    private class InsertTask implements Callable {
        private List<CSVRecord> records;

        public InsertTask(List<CSVRecord> records) {
            this.records = records;
        }

        private List<String> buildGeoEdgeKey(List<Long> cellIds, Long dstId) {
            ArrayList<String> geoKeys = new ArrayList<>();
            for (Long cellId : cellIds) {
                geoKeys.add(String.format(Constant.INSERT_EDGE_VALUE_TEMPLATE, cellId, dstId, 0, ""));
            }

            return geoKeys;
        }

        @Override
        public Integer call() {
            final long startTime = System.currentTimeMillis();
            Integer retCode = 0;
            try {
                GraphClient client = ClientManager.getClient(hostAndPorts, options);
                List<String> values = new ArrayList<>(records.size());

                for (CSVRecord record : records) {
                    if ((options.type.equals(Constant.VERTEX) && record.size() < 1)
                            || (options.type.equals(Constant.EDGE) && record.size() < 2)) {
                        // We support empty property now.
                        LOGGER.warn(String.format("Skip : %s", record));
                        continue;
                    }


                    if (options.isGeo) {
                        double lat = Double.parseDouble(record.get(0));
                        double lng = Double.parseDouble(record.get(1));
                        List<Long> cells = indexCells(lat, lng);
                        List<String> vals = buildGeoEdgeKey(cells, Long.parseLong(record.get(2)));
                        values.addAll(vals);
                    } else {
                        Iterator<String> iter = record.iterator();

                        switch (options.type.toLowerCase()) {
                            case Constant.VERTEX:
                                long id = Long.parseLong(record.get(0));
                                String vertexValue =
                                        Joiner.on(", ").join(Lists.newArrayList(iter).subList(1, record.size()));
                                LOGGER.trace(String.format("vertex id: %d, value: %s", id,
                                        vertexValue));
                                values.add(String.format(Constant.INSERT_VERTEX_VALUE_TEMPLATE, id,
                                        vertexValue));
                                break;
                            case Constant.EDGE:
                                long source = Long.parseLong(record.get(0));
                                long target = Long.parseLong(record.get(1));
                                if (options.hasRanking) {
                                    long ranking = Long.parseLong(record.get(2));
                                    List<String> edgeList = Lists.newArrayList(iter).subList(3, record.size());
                                    String edgeValue = Joiner.on(", ").join(edgeList);
                                    LOGGER.trace(String.format("edge source: %d, target: %d, ranking:"
                                                    + " %d, value: %s", source, target,
                                            ranking, edgeValue));
                                    values.add(String.format(Constant.INSERT_EDGE_VALUE_TEMPLATE, source,
                                            target, ranking, edgeValue));
                                } else {
                                    List<String> edgeList = Lists.newArrayList(iter).subList(2, record.size());
                                    String edgeValue = Joiner.on(", ").join(edgeList);
                                    LOGGER.trace(String.format("edge source: %d, target: %d, value:"
                                            + " %s", source, target, edgeValue));
                                    values.add(String.format(
                                            Constant.INSERT_EDGE_VALUE_WITHOUT_RANKING_TEMPLATE, source,
                                            target, edgeValue));
                                }
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

    private class CsvRecordBatchIterator implements Iterator<List<CSVRecord>> {
        private Iterator<CSVRecord> iter;
        private Integer size;

        public CsvRecordBatchIterator(Iterator<CSVRecord> iter, Integer size) {
            this.iter = iter;
            this.size = size;
        }

        @Override
        public boolean hasNext() {
            return iter.hasNext();
        }

        @Override
        public List<CSVRecord> next() {
            if (!iter.hasNext()) {
                throw new RuntimeException("No element exist.");
            } else {
                List<CSVRecord> csvRecords = new ArrayList<>();
                int count = 0;
                while (iter.hasNext() && count < size) {
                    csvRecords.add(iter.next());
                    ++count;
                }

                return csvRecords;
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
            LOGGER.error("Parse options error: " + e.getMessage());
            cmdLineParser.printUsage(System.err);
        } catch (Exception e) {
            LOGGER.error("Import error: " + e.getMessage());
            cmdLineParser.printUsage(System.err);
        }
    }


}


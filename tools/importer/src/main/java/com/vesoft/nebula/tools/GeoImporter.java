package com.vesoft.nebula.tools;

import com.google.common.collect.Lists;
import com.google.common.geometry.S2CellId;
import com.google.common.geometry.S2LatLng;
import com.google.common.net.HostAndPort;
import com.vesoft.nebula.graph.client.GraphClient;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.log4j.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import java.io.FileWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.*;

public class GeoImporter {
    private static final Logger LOGGER = Logger.getLogger(GeoImporter.class.getClass());

    private static final GeoImporter INSTANCE = new GeoImporter();
    private static final List<String> header = new ArrayList<String>(
            Arrays.asList("lat", "lng", "dst"));
    private static Integer maxCellCoverNums = 18;
    private static Integer minCellLevel = 5;
    private static Integer maxCellLevel = 24;

    private ExecutorService executor;
    private GeoOptions geoOptions;
    private List<HostAndPort> hostAndPorts;
    private CSVParser csvParser;
    private CSVPrinter csvPrinter;

    private GeoImporter() {
    }

    public static GeoImporter getInstance() {
        return INSTANCE;
    }

    public void setGeoOptions(GeoOptions geoOptions) {
        this.geoOptions = geoOptions;
    }

    private List<Long> indexCells(double lat, double lng) {
        S2LatLng s2LatLng = S2LatLng.fromDegrees(lat, lng);
        S2CellId s2CellId = S2CellId.fromLatLng(s2LatLng);

        ArrayList<Long> cellIds = new ArrayList<Long>();
        for (int i = minCellLevel; i < maxCellLevel; ++i) {
            cellIds.add(s2CellId.parent(i).id());
        }

        return cellIds;
    }

    private void readContent() throws Exception {
        csvParser = CSVParser.parse(
                geoOptions.file,
                Charset.forName("UTF-8"),
                CSVFormat.DEFAULT
                        .withFirstRecordAsHeader()
                        .withIgnoreEmptyLines()
                        .withTrim());

        if (!header.equals(csvParser.getHeaderNames())) {
            throw new Exception("Header should be [lat,lng,dst]");
        }
    }

    private List<String> buildGeoEdgeKey(List<Long> cellIds, Long dstId) {
        ArrayList<String> geoKeys = new ArrayList<>();
        for (Long cellId : cellIds) {
            geoKeys.add(String.format(Constant.INSERT_EDGE_VALUE_TEMPLATE, cellId, dstId, 0, ""));
        }

        return geoKeys;
    }

    private void checkOptions() throws Exception {
        hostAndPorts = Lists.newLinkedList();
        for (String address : geoOptions.addresses.split(",")) {
            String[] hostAndPort = address.split(":");
            if (hostAndPort.length != 2) {
                LOGGER.error(String.format("Address format error: %s", address));
                return;
            }
            hostAndPorts.add(HostAndPort.fromParts(hostAndPort[0],
                    Integer.valueOf(hostAndPort[1])));
        }

        if (Files.exists(geoOptions.errorPath)) {
            String errMsg = String.format("%s have existed", geoOptions.errorPath);
            LOGGER.error(errMsg);
            throw new Exception(errMsg);
        }

        if (Files.isDirectory(geoOptions.errorPath)) {
            String errMsg = String.format("%s is a directory", geoOptions.errorPath);
            LOGGER.error(errMsg);
            throw new Exception(errMsg);
        }

        FileWriter fileWriter=new FileWriter(geoOptions.errorPath.toFile());
        csvPrinter = new CSVPrinter(fileWriter, CSVFormat.DEFAULT);
    }

    private void runMultiJob() throws Exception {
        final long startTime = System.currentTimeMillis();
        checkOptions();
        readContent();

        executor = Executors.newFixedThreadPool(geoOptions.jobNum);
        CompletionService<Integer> completionService = new ExecutorCompletionService<>(executor);
        Iterator<CSVRecord> iterator = csvParser.iterator();
        Integer count = geoOptions.batchSize;
        Integer taskCnt = 0;
        Integer recordCnt = 0;
        List<CSVRecord> records;
        while (true) {
            records = new ArrayList<>();
            while (iterator.hasNext()) {
                CSVRecord record = iterator.next();
                records.add(record);
                recordCnt++;
                if (--count == 0 || !iterator.hasNext()) {
                    completionService.submit(new InsertTask(records));
                    taskCnt++;
                    count = geoOptions.batchSize;
                    break;
                }
            }

            if (!iterator.hasNext()) {
                break;
            }
        }

        Integer failedTaskCnt = 0;
        for (int i = 0; i < taskCnt; ++i) {
            Integer code = completionService.take().get();
            if (code != 0) {
                failedTaskCnt++;
            }
        }

        LOGGER.info(String.format("Row Counts : %d", recordCnt));
        LOGGER.info(String.format("Time Interval : %d ms", System.currentTimeMillis() - startTime));
        LOGGER.info(String.format("Total task : %d, Failed : %d", taskCnt, failedTaskCnt));

        if (failedTaskCnt == 0) {
            Files.delete(geoOptions.errorPath);
        }
        executor.shutdown();
    }

    private class InsertTask implements Callable<Integer> {
        private List<CSVRecord> records;

        public InsertTask(List<CSVRecord> records) {
            this.records = records;
        }

        @Override
        public Integer call() {
            final long startTime = System.currentTimeMillis();
            List<String> values = new ArrayList<>();
            for (CSVRecord record : records) {
                double lat = Double.parseDouble(record.get(0));
                double lng = Double.parseDouble(record.get(1));
                List<Long> cells = indexCells(lat, lng);
                List<String> vals = buildGeoEdgeKey(cells, Long.parseLong(record.get(2)));
                values.addAll(vals);
            }
            String exec = String.format(
                    Constant.BATCH_INSERT_TEMPLATE, "EDGE", "locate", "", String.join(",", values));

            try {
                GraphClient client = ClientManager.getClient(hostAndPorts, geoOptions);
                Integer code = client.execute(exec);
                if (code != 0) {
                    synchronized (csvPrinter) {
                        csvPrinter.printRecords(records);
                    }
                    LOGGER.info(String.format("Insert batch failed: %d, cost %d ms",
                            records.size(), System.currentTimeMillis() - startTime));

                }
                return code;
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        GeoOptions geoOptions = new GeoOptions();
        CmdLineParser cmdLineParser = new CmdLineParser(geoOptions);
        try {
            cmdLineParser.parseArgument(args);
            if (geoOptions.help) {
                cmdLineParser.printUsage(System.out);
                return;
            }
            LOGGER.info(geoOptions.toString());

            GeoImporter.INSTANCE.setGeoOptions(geoOptions);
            GeoImporter.INSTANCE.runMultiJob();
        } catch (CmdLineException e) {
            LOGGER.error("Parse options error: " + e.getMessage());
            cmdLineParser.printUsage(System.err);
        } catch (Exception e) {
            LOGGER.error("Import error: " + e.getMessage());
            cmdLineParser.printUsage(System.err);
        }
    }
}

package com.vesoft.nebula.tools;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.geometry.S2CellId;
import com.google.common.geometry.S2LatLng;
import com.google.common.net.HostAndPort;
import com.vesoft.nebula.graph.client.GraphClient;
import com.vesoft.nebula.graph.client.GraphClientImpl;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.log4j.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GeoImporter {
    private static final Logger LOGGER = Logger.getLogger(GeoImporter.class.getClass());

    private static final GeoImporter INSTANCE = new GeoImporter();
    private static final List<String> header = new ArrayList<String>(
            Arrays.asList("lat", "lng", "dst"));
    private static Integer maxCellCoverNums = 18;
    private static Integer minCellLevel = 5;
    private static Integer maxCellLevel = 24;

    private static ExecutorService executor = Executors.newFixedThreadPool(4);

    private GeoOptions geoOptions;

    private CSVParser csvParser;

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

    private CSVParser readContent() throws Exception {
        CSVParser parser = CSVParser.parse(
                geoOptions.file,
                Charset.forName("UTF-8"),
                CSVFormat.DEFAULT
                        .withFirstRecordAsHeader()
                        .withIgnoreEmptyLines()
                        .withTrim());

        if (!header.equals(parser.getHeaderNames())) {
            throw new Exception("Header should be [lat,lng,dst]");
        }

        return parser;
    }

    private List<String> buildGeoEdgeKey(List<Long> cellIds, Long dstId) {
        ArrayList<String> geoKeys = new ArrayList<>();
        for (Long cellId : cellIds) {
            geoKeys.add(String.format(Constant.INSERT_EDGE_VALUE_TEMPLATE, cellId, dstId, 0, ""));
        }

        return geoKeys;
    }

    private void run() throws Exception {
        List<HostAndPort> hostAndPorts = Lists.newLinkedList();
        for (String address : geoOptions.addresses.split(",")) {
            String[] hostAndPort = address.split(":");
            if (hostAndPort.length != 2) {
                LOGGER.error(String.format("Address format error: %s", address));
                return;
            }
            hostAndPorts.add(HostAndPort.fromParts(hostAndPort[0],
                    Integer.valueOf(hostAndPort[1])));
        }

        GraphClient client = new GraphClientImpl(hostAndPorts, geoOptions.timeout, 3, 1);

        if (client.connect(geoOptions.user, geoOptions.password) == 0) {
            LOGGER.debug(String.format("%s connect to thrift service", geoOptions.user));
        } else {
            LOGGER.error("Connection or Authenticate Failed");
            return;
        }

        if (client.execute(String.format(Constant.USE_TEMPLATE, geoOptions.spaceName)) == 0) {
            LOGGER.info(String.format("Switch Space to %s", geoOptions.spaceName));
        } else {
            LOGGER.error(String.format("USE %s Failed", geoOptions.spaceName));
            return;
        }

        csvParser = readContent();

        Iterator<CSVRecord> iterator = csvParser.iterator();
        while (iterator.hasNext()) {
            CSVRecord record = iterator.next();
            double lat = Double.parseDouble(record.get(0));
            double lng = Double.parseDouble(record.get(1));
            List<Long> cells = indexCells(lat, lng);
            List<String> values = buildGeoEdgeKey(cells, Long.parseLong(record.get(2)));
            String exec = String.format(
                    Constant.BATCH_INSERT_TEMPLATE, "EDGE", "locate", "", String.join(",", values));
            client.execute(exec);
        }
    }

    public static void main(String[] args) {
        GeoOptions geoOptions = new GeoOptions();
        CmdLineParser cmdLineParser = new CmdLineParser(geoOptions);
        try {
            cmdLineParser.parseArgument(args);
            LOGGER.info(geoOptions.toString());

            if (geoOptions.help) {
                cmdLineParser.printUsage(System.out);
                return;
            }

            GeoImporter.INSTANCE.setGeoOptions(geoOptions);
            GeoImporter.INSTANCE.run();
        } catch (CmdLineException e) {
            LOGGER.error("Parse options error: ", e);
            cmdLineParser.printUsage(System.err);
        } catch (Exception e) {
            LOGGER.error("Import error: ", e);
        }
    }
}

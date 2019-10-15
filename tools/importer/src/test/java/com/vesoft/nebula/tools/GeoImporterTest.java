package com.vesoft.nebula.tools;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kohsuke.args4j.CmdLineParser;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GeoImporterTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {

    }

    @Test
    public void testIndexCells() throws Exception {
        Method method = GeoImporter.class.getDeclaredMethod("indexCells", double.class, double.class);
        method.setAccessible(true);
        Object obj = method.invoke(GeoImporter.getInstance(), 30.28522, 120.01338);
        List<Long> result = (ArrayList<Long>) obj;
        Assert.assertTrue(result.size() > 0);
    }

    @Test
    public void testOptions() throws Exception {
        GeoOptions geoOptions = new GeoOptions();
        CmdLineParser cmdLineParser = new CmdLineParser(geoOptions);

        try {
            String[] argsEmpty = {};
            cmdLineParser.parseArgument(argsEmpty);

            String[] argsHelp = {"-h"};
            cmdLineParser.parseArgument(argsHelp);
        } catch (Exception e) {
            cmdLineParser.printUsage(System.err);
        }

        String[] args = {
                "-a=127.0.0.1:3699",
                "-f=./src/test/resources/geo.csv",
                "-b=16",
                "-name=geo",
                "-d=.",
                "-u=user",
                "-p=password"};
        cmdLineParser.parseArgument(args);
        Assert.assertEquals(geoOptions.addresses, "127.0.0.1:3699");
        Assert.assertEquals(geoOptions.file.getPath(), "./src/test/resources/geo.csv");
        Assert.assertEquals(geoOptions.batchSize, 16);
        Assert.assertEquals(geoOptions.spaceName, "geo");
        Assert.assertEquals(geoOptions.user, "user");
        Assert.assertEquals(geoOptions.password, "password");
    }

    @Test
    public void testReadContent() throws Exception {
        File file = new File("/Users/chenpengwei/Documents/project/nebula-java/tools/importer/src/test/Resources/geo.csv");
        CSVParser csvParser = CSVParser.parse(
                file,
                Charset.forName("UTF-8"),
                CSVFormat.DEFAULT
                        .withFirstRecordAsHeader()
                        .withIgnoreEmptyLines()
                        .withTrim()
        );

        Iterator iterator = csvParser.iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }

        System.out.println(csvParser.getHeaderNames());
    }

}

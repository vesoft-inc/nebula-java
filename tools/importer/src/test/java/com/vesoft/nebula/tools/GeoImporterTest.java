package com.vesoft.nebula.tools;

import com.sun.codemodel.internal.JMethod;
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

        String[] argsEmpty = {"-h"};
        cmdLineParser.parseArgument(argsEmpty);
        Assert.assertTrue(geoOptions.help);

        String[] args = {
                "-a=127.0.0.1:3699",
                "-f=./tools/importer/src/test/Resources/geo.csv",
                "-b=16",
                "-name=geo",
                "-d=./error",
                "-u=user",
                "-p=password"};
        cmdLineParser.parseArgument(args);
        Assert.assertEquals(geoOptions.addresses, "127.0.0.1:3699");
        Assert.assertEquals(geoOptions.file.getPath(), "./tools/importer/src/test/Resources/geo.csv");
        Assert.assertEquals(geoOptions.batchSize, 16);
        Assert.assertEquals(geoOptions.spaceName, "geo");
        Assert.assertEquals(geoOptions.user, "user");
        Assert.assertEquals(geoOptions.password, "password");
    }

    @Test
    public void testReadContent() throws Exception {
        GeoOptions geoOptions = new GeoOptions();
        CmdLineParser cmdLineParser = new CmdLineParser(geoOptions);

        String[] args = {
                "-a=127.0.0.1:3699",
                "-f=./src/test/Resources/geo.csv",
                "-b=16",
                "-name=geo",
                "-d=./error",
                "-u=user",
                "-p=password"};
        cmdLineParser.parseArgument(args);

        Method setGeoOptions = GeoImporter.class.getDeclaredMethod("setGeoOptions", GeoOptions.class);
        setGeoOptions.setAccessible(true);
        setGeoOptions.invoke(GeoImporter.getInstance(), geoOptions);

        Method readContent = GeoImporter.class.getDeclaredMethod("readContent");
        readContent.setAccessible(true);
        readContent.invoke(GeoImporter.getInstance());
    }
}

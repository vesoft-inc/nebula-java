package com.vesoft.nebula.tools;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.junit.Assert;
import org.junit.Test;
import org.kohsuke.args4j.CmdLineParser;

import java.io.FileWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ImporterTest {

    @Test
    public void testHelp() throws Exception {
        Options options = new Options();
        CmdLineParser cmdLineParser = new CmdLineParser(options);

        String[] args = {"-h"};
        cmdLineParser.parseArgument(args);
        Assert.assertTrue(options.help);

        options.help = false;

        args[0] = "--help";
        cmdLineParser.parseArgument(args);
        Assert.assertTrue(options.help);
    }

    @Test
    public void testGeoOptions() throws Exception {
        Options options = new Options();
        CmdLineParser cmdLineParser = new CmdLineParser(options);

        String[] args = {
                "-g",
                "-a=127.0.0.1:3699",
                "-f=./tools/importer/src/test/Resources/geo.csv",
                "-b=16",
                "-n=geo",
                "-d=./error",
                "-u=user",
                "-p=password"};
        cmdLineParser.parseArgument(args);
        Assert.assertEquals(options.addresses, "127.0.0.1:3699");
        Assert.assertEquals(options.file, "./tools/importer/src/test/Resources/geo.csv");
        Assert.assertEquals(options.batchSize.intValue(), 16);
        Assert.assertEquals(options.spaceName, "geo");
        Assert.assertEquals(options.user, "user");
        Assert.assertEquals(options.password, "password");
    }

    @Test
    public void testNormalOptions() throws Exception {
        Options options = new Options();
        CmdLineParser cmdLineParser = new CmdLineParser(options);

        String[] args = {
                "-a=127.0.0.1:3699",
                "-f=./tools/importer/src/test/Resources/vertex.csv",
                "-b=16",
                "-n=geo",
                "-d=./error",
                "-u=user",
                "-p=password",
                "-t=vertex",
                "-m=person",
                "-c=name"
        };

        cmdLineParser.parseArgument(args);
        options.checkOptions();
        Assert.assertEquals(options.addresses, "127.0.0.1:3699");
        Assert.assertEquals(options.file,
                "./tools/importer/src/test/Resources/vertex.csv");
        Assert.assertEquals(options.batchSize.intValue(), 16);
        Assert.assertEquals(options.spaceName, "geo");
        Assert.assertEquals(options.user, "user");
        Assert.assertEquals(options.password, "password");
        Assert.assertEquals(options.type, "vertex");
        Assert.assertEquals(options.schemaName, "person");
        Assert.assertEquals(options.columns, "name");
    }

    @Test
    public void testIndexCells() throws Exception {
        Method method = Importer.class.getDeclaredMethod("indexCells",
                double.class, double.class);
        method.setAccessible(true);
        Object obj = method.invoke(Importer.getInstance(), 30.28522, 120.01338);
        List<Long> result = (ArrayList<Long>) obj;
        Assert.assertTrue(result.size() > 0);
    }

    @Test
    public void testReadContent() throws Exception {
        Method readContent = Importer.class.getDeclaredMethod("readContent", String.class);
        readContent.setAccessible(true);
        readContent.invoke(Importer.getInstance(), "./src/test/Resources/geo.csv");
    }
}

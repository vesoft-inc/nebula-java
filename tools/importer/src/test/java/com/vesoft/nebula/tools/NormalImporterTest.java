package com.vesoft.nebula.tools;

import org.junit.Assert;
import org.junit.Test;
import org.kohsuke.args4j.CmdLineParser;

public class NormalImporterTest {

    @Test
    public void testOptions() throws Exception {
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
        Assert.assertEquals(options.file.getPath(),
                "./tools/importer/src/test/Resources/vertex.csv");
        Assert.assertEquals(options.batchSize.intValue(), 16);
        Assert.assertEquals(options.spaceName, "geo");
        Assert.assertEquals(options.user, "user");
        Assert.assertEquals(options.password, "password");
        Assert.assertEquals(options.type, "vertex");
        Assert.assertEquals(options.schemaName, "person");
        Assert.assertEquals(options.columns, "name");
    }
}

package com.vesoft.nebula.tools;

import org.apache.log4j.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

public class Importer {
    private static final Logger LOGGER = Logger.getLogger(Importer.class);

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

            if (options.isGeo) {
                GeoImporter.getInstance().runMultiJob(options);
            } else {
                NormalImporter.getInstance().runMultiJob(options);
            }
        } catch (CmdLineException e) {
            LOGGER.error("Parse options error: " + e.getMessage());
            cmdLineParser.printUsage(System.err);
        } catch (Exception e) {
            LOGGER.error("Import error: " + e.getMessage());
            cmdLineParser.printUsage(System.err);
        }
    }

}

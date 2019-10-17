package com.vesoft.nebula.tools;

import org.kohsuke.args4j.Option;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Options {
    @Option(name = "-a", aliases = "--address", usage = "thrift service addresses. ip:port", required = true)
    String addresses;
    @Option(name = "-u", aliases = "--user", usage = "thrift service username.", required = true)
    String user;
    @Option(name = "-p", aliases = "--password", usage = "thrift service password.", required = true)
    String password;
    @Option(name = "-n", aliases = "--name", usage = "specify the space name.", required = true)
    String spaceName;
    @Option(name = "-f", aliases = "--file", usage = "data file path with file name.", required = true)
    File file;
    @Option(name = "-b", aliases = "--batch", usage = "batch insert size.")
    Integer batchSize = Constant.DEFAULT_INSERT_BATCH_SIZE;
    @Option(name = "-o", aliases = "--timeout", usage = "thrift connection timeout with ms.")
    Integer timeout = Constant.DEFAULT_CONNECTION_TIMEOUT_MS;
    @Option(name = "-d", aliases = "--errorPath", usage = "save the failed record in error path.")
    Path errorPath = Paths.get("./error");
    @Option(name = "-j", aliases = "--job", usage = "job number")
    Integer jobNum = 1;
    @Option(name = "-s", aliases = "--stat", usage = "print statistics info.")
    Boolean isPrintStatistic = true;
    @Option(name = "-r", aliases = "--connectionRetry", usage = "thrift connection retry number.")
    Integer connectionRetry = Constant.DEFAULT_CONNECTION_RETRY;
    @Option(name = "-e", aliases = "--executionRetry", usage = "thrift execution retry number.")
    Integer executionRetry = Constant.DEFAULT_EXECUTION_RETRY;

    /**
     * Specified for normal importer
     */
    @Option(name = "-t", aliases = "--type", usage = "data type. vertex or edge.")
    Integer type;
    @Option(name = "-m", aliases = "--schema", usage = "specify the schema name.")
    String schemaName;
    @Option(name = "-c", aliases = "--column", usage = "vertex and edge's column.")
    String columnName;
    @Option(name = "-k", aliases = "--ranking", usage = "the edge have ranking data.")
    Boolean hasRanking = false;

    @Option(name = "-g", aliases = "--geo", usage = "indicate if import geo.")
    Boolean isGeo = false;

    @Option(name = "-h", aliases = "--help", help = true)
    Boolean help;

    @Override
    public String toString() {
        return "Options{" +
                "addresses='" + addresses + '\'' +
                ", user='" + user + '\'' +
                ", password='" + password + '\'' +
                ", spaceName='" + spaceName + '\'' +
                ", file=" + file +
                ", batchSize=" + batchSize +
                ", timeout=" + timeout +
                ", errorPath=" + errorPath +
                ", jobNum=" + jobNum +
                ", isPrintStatistic=" + isPrintStatistic +
                ", connectionRetry=" + connectionRetry +
                ", executionRetry=" + executionRetry +
                ", type=" + type +
                ", schemaName='" + schemaName + '\'' +
                ", columnName='" + columnName + '\'' +
                ", hasRanking=" + hasRanking +
                ", isGeo=" + isGeo +
                ", help=" + help +
                '}';
    }
}

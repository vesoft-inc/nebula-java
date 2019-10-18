package com.vesoft.nebula.tools;

import com.google.common.collect.Lists;
import com.google.common.net.HostAndPort;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.kohsuke.args4j.Option;


public class Options {
    @Option(name = "-a", aliases = "--address", usage = "thrift service addresses. ip:port",
            required = true)
    String addresses;

    @Option(name = "-u", aliases = "--user", usage = "thrift service username.",
            required = true)
    String user;

    @Option(name = "-p", aliases = "--password", usage = "thrift service password.",
            required = true)
    String password;

    @Option(name = "-n", aliases = "--name", usage = "specify the space name.",
            required = true)
    String spaceName;

    @Option(name = "-f", aliases = "--file", usage = "data file path with file name.",
            required = true)
    String file;

    @Option(name = "-b", aliases = "--batch", usage = "batch insert size.")
    Integer batchSize = Constant.DEFAULT_INSERT_BATCH_SIZE;

    @Option(name = "-o", aliases = "--timeout", usage = "thrift connection timeout with ms.")
    Integer timeout = Constant.DEFAULT_CONNECTION_TIMEOUT_MS;

    @Option(name = "-d", aliases = "--errorPath", usage = "save the failed record in error path.")
    Path errorPath = Paths.get("./error");

    @Option(name = "-j", aliases = "--job", usage = "job number")
    Integer jobNum = 1;

    @Option(name = "-s", aliases = "--stat", usage = "print statistics info.")
    Boolean isStatistic = true;

    @Option(name = "-r", aliases = "--connectionRetry", usage = "thrift connection retry number.")
    Integer connectionRetry = Constant.DEFAULT_CONNECTION_RETRY;

    @Option(name = "-e", aliases = "--executionRetry", usage = "thrift execution retry number.")
    Integer executionRetry = Constant.DEFAULT_EXECUTION_RETRY;

    /**
     * Specified for normal importer
     */
    @Option(name = "-t", aliases = "--type", usage = "data type. vertex or edge.",
            forbids = {"-g", "--geo"})
    String type;

    @Option(name = "-m", aliases = "--schema", usage = "specify the schema name.",
            forbids = {"-g", "--geo"})
    String schemaName;

    @Option(name = "-c", aliases = "--column", usage = "vertex and edge's column.",
            forbids = {"-g", "--geo"})
    String columns;

    @Option(name = "-k", aliases = "--ranking", usage = "the edge have ranking data.",
            forbids = {"-g", "--geo"})
    Boolean hasRanking = false;

    @Option(name = "-g", aliases = "--geo", usage = "indicate if import geo.",
            forbids = {"-t","--type","-m","--schema","-c","--column","-k","--ranking"})
    Boolean isGeo = false;

    @Option(name = "-h", aliases = "--help", help = true)
    Boolean help = false;

    public void checkOptions() throws Exception {
        if (!isGeo) {
            if (type == null) {
                throw new Exception("Option \"-t (--type)\" is required when not import geo.");
            }

            if (schemaName == null) {
                throw new Exception("Option \"-m (--schema)\" is required when not import geo.");
            }

            if (columns == null) {
                throw new Exception("Option \"-c (--column)\" is required when not import geo.");
            }
        } else {
            type = Constant.EDGE;
            schemaName = "locate";
            columns = "";
        }

        if (Files.exists(errorPath)) {
            String errMsg = String.format("%s have existed", errorPath);
            throw new Exception(errMsg);
        }

        if (Files.isDirectory(errorPath)) {
            String errMsg = String.format("%s is a directory", errorPath);
            throw new Exception(errMsg);
        }
    }

    public List<HostAndPort> getHostPort() throws Exception {
        List<HostAndPort> hostAndPorts = Lists.newLinkedList();
        for (String address : addresses.split(",")) {
            String[] hostAndPort = address.split(":");
            if (hostAndPort.length != 2) {
                throw new Exception(String.format("Address format error: %s", address));
            }
            hostAndPorts.add(HostAndPort.fromParts(hostAndPort[0],
                    Integer.valueOf(hostAndPort[1])));
        }

        return hostAndPorts;
    }

    @Override
    public String toString() {
        return "Options{"
                + "\naddresses='" + addresses + '\''
                + "\nuser='" + user + '\''
                + "\npassword='" + password + '\''
                + "\nspaceName='" + spaceName + '\''
                + "\nfile=" + file
                + "\nbatchSize=" + batchSize
                + "\ntimeout=" + timeout
                + "\nerrorPath=" + errorPath
                + "\njobNum=" + jobNum
                + "\nisPrintStatistic=" + isStatistic
                + "\nconnectionRetry=" + connectionRetry
                + "\nexecutionRetry=" + executionRetry
                + "\ntype=" + type
                + "\nschemaName='" + schemaName + '\''
                + "\ncolumnName='" + columns + '\''
                + "\nhasRanking=" + hasRanking
                + "\nisGeo=" + isGeo
                + '}';
    }
}

package com.vesoft.nebula.tools;

import org.kohsuke.args4j.Option;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class GeoOptions {
    @Option(name = "-a", aliases = "-address", usage = "thrift service addresses. ip:port", required = true)
    String addresses;
    @Option(name = "-u", aliases = "-user", usage = "thrift service username.", required = true)
    String user;
    @Option(name = "-p", aliases = "-password", usage = "thrift service password.", required = true)
    String password;
    @Option(name = "-n", aliases = "-name", usage = "specify the space name.", required = true)
    String spaceName = "geo";
    @Option(name = "-f", aliases = "-file", usage = "data file path with file name.", required = true)
    File file;
    @Option(name = "-b", aliases = "-batch", usage = "batch insert size.")
    int batchSize = Constant.DEFAULT_INSERT_BATCH_SIZE;
    @Option(name = "-o", aliases = "-timeout", usage = "thrift connection timeout with ms.")
    int timeout = Constant.DEFAULT_CONNECTION_TIMEOUT_MS;
    @Option(name = "-d", aliases = "-errorPath", usage = "save the failed record in error path.")
    Path errorPath = Paths.get("./error");
    @Option(name = "-j", aliases = "-job", usage = "insert job number")
    Integer jobNum = 1;

    @Option(name = "-h", aliases = "-help", help = true)
    boolean help;

    @Override
    public String toString() {
        return "GeoOptions{" +
                "addresses='" + addresses + '\'' +
                ", user='" + user + '\'' +
                ", password='" + password + '\'' +
                ", spaceName='" + spaceName + '\'' +
                ", file=" + file +
                ", batchSize=" + batchSize +
                ", timeout=" + timeout +
                ", errorPath=" + errorPath +
                ", jobNum=" + jobNum +
                ", help=" + help +
                '}';
    }
}

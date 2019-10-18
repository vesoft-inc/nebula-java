/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools;

import com.google.common.collect.Lists;
import com.google.common.net.HostAndPort;

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

    public String getAddresses() {
        return addresses;
    }

    public void setAddresses(String addresses) {
        this.addresses = addresses;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSpaceName() {
        return spaceName;
    }

    public void setSpaceName(String spaceName) {
        this.spaceName = spaceName;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public Integer getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(Integer batchSize) {
        this.batchSize = batchSize;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public Path getErrorPath() {
        return errorPath;
    }

    public void setErrorPath(Path errorPath) {
        this.errorPath = errorPath;
    }

    public Integer getJobNum() {
        return jobNum;
    }

    public void setJobNum(Integer jobNum) {
        this.jobNum = jobNum;
    }

    public Boolean getStatistic() {
        return isStatistic;
    }

    public void setStatistic(Boolean statistic) {
        isStatistic = statistic;
    }

    public Integer getConnectionRetry() {
        return connectionRetry;
    }

    public void setConnectionRetry(Integer connectionRetry) {
        this.connectionRetry = connectionRetry;
    }

    public Integer getExecutionRetry() {
        return executionRetry;
    }

    public void setExecutionRetry(Integer executionRetry) {
        this.executionRetry = executionRetry;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getColumns() {
        return columns;
    }

    public void setColumns(String columns) {
        this.columns = columns;
    }

    public Boolean getHasRanking() {
        return hasRanking;
    }

    public void setHasRanking(Boolean hasRanking) {
        this.hasRanking = hasRanking;
    }

    public Boolean getGeo() {
        return isGeo;
    }

    public void setGeo(Boolean geo) {
        isGeo = geo;
    }

    public Boolean getHelp() {
        return help;
    }

    public void setHelp(Boolean help) {
        this.help = help;
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

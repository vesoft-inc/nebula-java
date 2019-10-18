/* Copyright (c) 2018 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.tools;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.SYNC;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.net.HostAndPort;

import com.vesoft.nebula.graph.client.GraphClient;
import com.vesoft.nebula.graph.client.GraphClientImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.log4j.Logger;

public class NormalImporter {

    private static final Logger LOGGER = Logger.getLogger(NormalImporter.class.getClass());
    private static final NormalImporter INSTANCE = new NormalImporter();

    private ExecutorService executor;
    private Options options;
    private List<HostAndPort> hostAndPorts;

    private NormalImporter() {
    }

    public static NormalImporter getInstance() {
        return INSTANCE;
    }

    private Stream<String> readContent(String file) throws IOException {
        if (file.toLowerCase().startsWith("hdfs://")) {
            return dfsProcessor(file);
        } else {
            return localProcessor(file);
        }
    }

    public void runMultiJob(Options options) throws Exception {
        final long startTime = System.currentTimeMillis();

        this.options = options;
        this.hostAndPorts = options.getHostPort();
        long rowCounter = 0;
        Integer taskCnt = 0;

        Stream<String> lines = readContent(options.file.getPath());
        executor = Executors.newFixedThreadPool(options.jobNum);
        CompletionService<Integer> completionService = new ExecutorCompletionService<>(executor);

        Iterable<List<String>> iterable = Iterables.partition(lines::iterator, options.batchSize);
        Iterator<List<String>> portion = iterable.iterator();
        while (portion.hasNext()) {
            List<String> segment = portion.next();
            if (options.isStatistic) {
                rowCounter += segment.size();
            }

            completionService.submit(new InsertTask(segment));
            taskCnt++;
        }

        Integer failedTaskCnt = 0;
        for (int i = 0; i < taskCnt; ++i) {
            Integer code = completionService.take().get();
            if (code != 0) {
                failedTaskCnt++;
            }
        }

        long interval = System.currentTimeMillis() - startTime;
        if (options.isStatistic) {
            LOGGER.info(String.format("Row Counter   : %d", rowCounter));
            LOGGER.info(String.format("Time Interval : %d", interval));
            LOGGER.info(String.format("Total task : %d, Failed : %d", taskCnt, failedTaskCnt));
        }
        executor.shutdown();
    }

    private class InsertTask implements Callable {
        private List<String> segment;

        public InsertTask(List<String> segment) {
            this.segment = segment;
        }

        @Override
        public Integer call() {
            Integer retCode = 0;
            try {
                GraphClient client = ClientManager.getClient(hostAndPorts, options);
                List<String> values = new ArrayList<>(segment.size());

                for (String line : segment) {
                    if (line.trim().length() == 0) {
                        LOGGER.warn("Skip empty line");
                        continue;
                    }

                    Iterable<String> tokens = Splitter.on(",").split(line);
                    if (Iterables.isEmpty(tokens)
                            || (options.type.equals(Constant.VERTEX) && Iterables.size(tokens) < 2)
                            || (options.type.equals(Constant.EDGE) && Iterables.size(tokens) < 3)) {
                        LOGGER.warn(String.format("Skip : %s", line));
                        continue;
                    }

                    List<String> tokenList = StreamSupport
                            .stream(tokens.spliterator(), false)
                            .map(String::toString)
                            .collect(Collectors.toList());

                    switch (options.type.toLowerCase()) {
                      case Constant.VERTEX:
                          long id = Long.parseLong(tokenList.get(0));
                          List<String> vertexValueList = tokenList.subList(1, tokenList.size());
                          String vertexValue = Joiner.on(", ").join(vertexValueList);
                          LOGGER.trace(String.format("vertex id: %d, value: %s", id,
                                  vertexValue));
                          values.add(String.format(Constant.INSERT_VERTEX_VALUE_TEMPLATE, id,
                                  vertexValue));
                          break;
                      case Constant.EDGE:
                          long source = Long.parseLong(tokenList.get(0));
                          long target = Long.parseLong(tokenList.get(1));
                          if (options.hasRanking) {
                              long ranking = Long.parseLong(tokenList.get(2));
                              List<String> edgeList = tokenList.subList(3, tokenList.size());
                              String edgeValue = Joiner.on(", ").join(edgeList);
                              LOGGER.trace(String.format("edge source: %d, target: %d, ranking:"
                                              + " %d, value: %s", source, target,
                                      ranking, edgeValue));
                              values.add(String.format(Constant.INSERT_EDGE_VALUE_TEMPLATE, source,
                                      target, ranking, edgeValue));
                          } else {
                              List<String> edgeList = tokenList.subList(2, tokenList.size());
                              String edgeValue = Joiner.on(", ").join(edgeList);
                              LOGGER.trace(String.format("edge source: %d, target: %d, value:"
                                      + " %s", source, target, edgeValue));
                              values.add(String.format(
                                      Constant.INSERT_EDGE_VALUE_WITHOUT_RANKING_TEMPLATE, source,
                                      target, edgeValue));
                          }
                          break;
                      default:
                          LOGGER.error("Type should be vertex or edge");
                          break;
                    }
                }

                String exec = String.format(Constant.BATCH_INSERT_TEMPLATE,
                        options.type, options.schemaName, options.columns,
                        Joiner.on(", ").join(values));
                LOGGER.debug(String.format("Execute: %s", exec));
                retCode = client.execute(exec);
                if (retCode != 0) {
                    Files.write(options.errorPath, segment, Charset.forName("UTF-8"),
                            CREATE, APPEND, SYNC);
                    LOGGER.error("Graph Client Execution Failed !");
                }
            } catch (ClientManager.GetClientFailException e) {
                throw new RuntimeException(e.getMessage());
            } catch (IOException e) {
                LOGGER.error("IOException: ", e);
            } finally {
                return retCode;
            }
        }
    }

    private Stream<String> localProcessor(String file) throws IOException {
        Path filePath = Paths.get(file);
        if (!Files.exists(filePath) || !Files.isReadable(filePath)) {
            throw new IllegalArgumentException(file + " is not exist or not readable");
        }
        LOGGER.info(String.format("Reading Local FileSystem: %s", file));
        return Files.lines(filePath, Charsets.UTF_8);
    }

    private Stream<String> dfsProcessor(String filePath) throws IOException {
        Configuration configuration = new Configuration();
        FileSystem fileSystem = FileSystem.get(URI.create(filePath), configuration);
        LOGGER.info(String.format("Reading HDFS: %s", options.file));
        try (InputStream stream = fileSystem.open(
                new org.apache.hadoop.fs.Path(options.file.getPath()));
             Reader reader = new InputStreamReader(stream);
             BufferedReader buffered = new BufferedReader(reader)) {
            return buffered.lines();
        }
    }

}


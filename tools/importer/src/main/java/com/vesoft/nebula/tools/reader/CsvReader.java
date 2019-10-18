package com.vesoft.nebula.tools.reader;

import com.google.common.collect.Lists;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CsvReader implements Reader {
    private static final Logger LOGGER = Logger.getLogger(CsvReader.class);

    private String filePath;
    private Integer size;
    private CSVParser csvParser;
    private Iterator<CSVRecord> iter;

    public CsvReader(String filePath, Integer size) {
        this.filePath = filePath;
        this.size = size;
    }

    public void init() throws Exception {
        if (filePath.toLowerCase().startsWith("hdfs://")) {
            dfsProcessor();
        } else {
            localProcessor();
        }

        this.iter = csvParser.iterator();
    }

    private void localProcessor() throws IOException {
        Path path = Paths.get(filePath);
        if (!Files.exists(path) || !Files.isReadable(path)) {
            throw new IllegalArgumentException(filePath + " is not exist or not readable");
        }
        LOGGER.info(String.format("Reading Local FileSystem: %s", filePath));
        this.csvParser = CSVParser.parse(
                new File(filePath),
                Charset.forName("UTF-8"),
                CSVFormat.DEFAULT
                        .withFirstRecordAsHeader()
                        .withIgnoreEmptyLines()
                        .withTrim());
    }

    private void dfsProcessor() throws IOException {
        Configuration configuration = new Configuration();
        FileSystem fileSystem = FileSystem.get(URI.create(filePath), configuration);
        LOGGER.info(String.format("Reading HDFS: %s", filePath));
        try (InputStream inputStream = fileSystem.open(
                new org.apache.hadoop.fs.Path(filePath))) {
            this.csvParser = CSVParser.parse(
                    inputStream,
                    Charset.forName("UTF-8"),
                    CSVFormat.DEFAULT
                            .withFirstRecordAsHeader()
                            .withIgnoreEmptyLines()
                            .withTrim());
        }
    }

    @Override
    public boolean hasNext() {
        return iter.hasNext();
    }

    @Override
    public List<List<String>> next() {
        if (!iter.hasNext()) {
            throw new RuntimeException("No element exist.");
        } else {
            List<List<String>> csvRecords = new ArrayList<>();
            int count = 0;
            while (iter.hasNext() && count < size) {
                csvRecords.add(Lists.newArrayList(iter.next().iterator()));
                ++count;
            }

            return csvRecords;
        }
    }

    @Override
    public void close() throws Exception {
        csvParser.close();
    }
}

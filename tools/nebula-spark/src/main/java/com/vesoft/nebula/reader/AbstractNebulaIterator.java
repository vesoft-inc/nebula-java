/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.reader;

import com.facebook.thrift.TException;
import com.vesoft.nebula.bean.DataSourceConfig;
import com.vesoft.nebula.exception.GraphConnectException;
import com.vesoft.nebula.client.meta.MetaClientImpl;
import com.vesoft.nebula.client.storage.StorageClientImpl;
import com.vesoft.nebula.client.storage.processor.Processor;
import com.vesoft.nebula.data.Result;

import java.util.*;

import org.apache.spark.Partition;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.collection.AbstractIterator;

public abstract class AbstractNebulaIterator extends AbstractIterator<Row> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractNebulaIterator.class);

    protected Iterator<List<Object>> dataIterator;
    protected Iterator<Integer> scanPartIterator;

    protected List<List<Object>> resultValues = new ArrayList<>();
    protected StorageClientImpl storageClient;
    protected MetaClientImpl metaClient;
    protected Processor processor;

    protected Map<String, List<String>> returnCols;

    public AbstractNebulaIterator(Partition split,
                                  DataSourceConfig dataSourceConfig) {
        this.returnCols = dataSourceConfig.getReturnColMap();

        metaClient = new MetaClientImpl(dataSourceConfig.getHostAndPorts());
        try {
            metaClient.connect();
        } catch (TException e) {
            throw new GraphConnectException(e.getMessage(), e);
        }
        storageClient = new StorageClientImpl(metaClient);

        // allocate scanPart to this partition
        int totalPart = metaClient.getPartsAlloc(dataSourceConfig.getNameSpace()).size();
        NebulaPartition nebulaPartition = (NebulaPartition) split;
        List<Integer> scanParts = nebulaPartition.getScanParts(totalPart,
                dataSourceConfig.getPartitionNumber());
        LOGGER.info("partition index: {}, scanPart: {}", split.index(), scanParts.toString());
        scanPartIterator = scanParts.iterator();
    }

    @Override
    public abstract boolean hasNext();

    @Override
    public Row next() {
        return RowFactory.create(dataIterator.next().toArray());
    }

    protected abstract Iterator<List<Object>> process(Result result);
}

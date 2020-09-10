/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.reader;

import com.facebook.thrift.TException;
import com.google.common.net.HostAndPort;
import com.vesoft.nebula.bean.ConnectInfo;
import com.vesoft.nebula.bean.ScanInfo;
import com.vesoft.nebula.exception.GraphConnectException;
import com.vesoft.nebula.client.meta.MetaClientImpl;
import com.vesoft.nebula.client.storage.StorageClientImpl;
import com.vesoft.nebula.client.storage.processor.Processor;
import com.vesoft.nebula.data.Result;

import java.util.*;

import org.apache.spark.Partition;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.types.StructField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.collection.AbstractIterator;

public abstract class AbstractNebulaIterator extends AbstractIterator<Row> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractNebulaIterator.class);

    protected Iterator<String> dataIterator;
    protected Iterator<Integer> scanPartIterator;
    protected Map<String, List<String>> resultValues = new HashMap<>();

    protected StorageClientImpl storageClient;
    protected MetaClientImpl metaClient;
    protected Processor processor;

    protected ConnectInfo connectInfo;
    protected Map<String, List<String>> returnCols;

    public AbstractNebulaIterator(ConnectInfo connectInfo, Partition split,
                                  ScanInfo scanInfo) {
        this.connectInfo = connectInfo;
        this.returnCols = scanInfo.getReturnColMap();

        metaClient = new MetaClientImpl(connectInfo.getHostAndPorts());
        try {
            metaClient.connect();
        } catch (TException e) {
            throw new GraphConnectException(e.getMessage(), e);
        }
        storageClient = new StorageClientImpl(metaClient);

        // allocate scanPart to this partition
        int totalPart = metaClient.getPartsAlloc(connectInfo.getSpaceName()).size();
        NebulaPartition nebulaPartition = (NebulaPartition) split;
        List<Integer> scanParts = nebulaPartition.getScanParts(totalPart,
                                                                scanInfo.getPartitionNumber());
        LOGGER.info("partition index: {}, scanPart: {}", split.index(), scanParts.toString());
        scanPartIterator = scanParts.iterator();
    }

    @Override
    public abstract boolean hasNext();

    @Override
    public Row next() {
        return RowFactory.create(resultValues.get(dataIterator.next()).toArray());
    }

    protected abstract Iterator<String> process(Result result);
}

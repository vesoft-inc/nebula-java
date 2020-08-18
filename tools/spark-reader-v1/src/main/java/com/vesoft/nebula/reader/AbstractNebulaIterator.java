package com.vesoft.nebula.reader;

import com.facebook.thrift.TException;
import com.vesoft.nebula.bean.ConnectInfo;
import com.vesoft.nebula.bean.ScanInfo;
import com.vesoft.nebula.client.meta.MetaClientImpl;
import com.vesoft.nebula.client.storage.StorageClientImpl;
import com.vesoft.nebula.client.storage.processor.Processor;
import com.vesoft.nebula.data.Result;
import com.vesoft.nebula.exception.GraphOperateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.spark.Partition;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.collection.AbstractIterator;

public abstract class AbstractNebulaIterator extends AbstractIterator<Row> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractNebulaIterator.class);

    protected Iterator<String[]> dataIterator;
    protected Iterator<Integer> scanPartIterator;

    protected StorageClientImpl storageClient;
    protected MetaClientImpl metaClient;
    protected Processor processor;

    protected ConnectInfo connectInfo;
    protected Map<String, List<String>> returnCols;
    protected Map<String, List<Integer>> labelPropIndexMap;
    protected int propSize;

    public AbstractNebulaIterator(ConnectInfo connectInfo, Partition split,
                                  ScanInfo scanInfo, Map<String, Integer> propIndexMap) {
        init(connectInfo, split, scanInfo, propIndexMap);
    }

    private void init(ConnectInfo connectInfo, Partition split,
                      ScanInfo scanInfo, Map<String, Integer> propIndexMap) {
        this.propSize = propIndexMap.size();
        this.connectInfo = connectInfo;
        this.returnCols = scanInfo.getReturnColMap();
        this.labelPropIndexMap = new HashMap<>();
        for (Map.Entry<String, List<String>> colEntry : returnCols.entrySet()) {
            List<Integer> colIndexs = new ArrayList<>();
            List<String> propNames = colEntry.getValue();
            for (String propName : propNames) {
                colIndexs.add(propIndexMap.get(propName));
            }
            labelPropIndexMap.put(colEntry.getKey(), colIndexs);
        }
        LOGGER.info("labelPropIndexMap: {}", labelPropIndexMap);
        metaClient = new MetaClientImpl(connectInfo.getIp(), connectInfo.getStoragePort());
        try {
            metaClient.connect();
        } catch (TException e) {
            throw new GraphOperateException(e.getMessage(), e);
        }
        storageClient = new StorageClientImpl(metaClient);

        // allocate scanPart to this partition
        int totalPart = metaClient.getPartsAlloc(connectInfo.getSpaceName()).size();
        NebulaPartition nebulaPartition = (NebulaPartition) split;
        List<Integer> scanParts = nebulaPartition.getScanParts(totalPart);
        LOGGER.info("partition index: {}, scanPart: {}", split.index(), scanParts);
        scanPartIterator = scanParts.iterator();
    }

    @Override
    public abstract boolean hasNext();

    @Override
    public Row next() {
        return RowFactory.create(dataIterator.next());
    }

    protected abstract Iterator<String[]> process(Result result);
}

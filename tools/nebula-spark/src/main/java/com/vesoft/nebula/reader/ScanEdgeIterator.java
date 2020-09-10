/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.reader;

import com.vesoft.nebula.bean.ConnectInfo;
import com.vesoft.nebula.bean.ScanInfo;
import com.vesoft.nebula.client.storage.processor.ScanEdgeProcessor;
import com.vesoft.nebula.data.Property;
import com.vesoft.nebula.data.Result;
import com.vesoft.nebula.data.Row;
import com.vesoft.nebula.storage.ScanEdgeResponse;

import java.io.IOException;
import java.util.*;

import org.apache.spark.Partition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScanEdgeIterator extends AbstractNebulaIterator {

    private final Logger LOGGER = LoggerFactory.getLogger(ScanEdgeIterator.class);

    private Iterator<ScanEdgeResponse> responseIterator;

    private ScanInfo scanInfo;

    public ScanEdgeIterator(ConnectInfo connectInfo, Partition split,
                            ScanInfo scanInfo) {
        super(connectInfo, split, scanInfo);
        this.scanInfo = scanInfo;
    }

    @Override
    public boolean hasNext() {
        if (dataIterator == null && responseIterator == null && !scanPartIterator.hasNext()) {
            return false;
        }

        while (dataIterator == null || !dataIterator.hasNext()) {
            if (responseIterator == null || !responseIterator.hasNext()) {
                if (scanPartIterator.hasNext()) {
                    try {
                        responseIterator = storageClient.scanEdge(connectInfo.getSpaceName(),
                                scanPartIterator.next(), returnCols, scanInfo.getAllCols(),
                                1000, 0L, Long.MAX_VALUE);
                    } catch (IOException e) {
                        LOGGER.error(e.getMessage(), e);
                    }
                    continue;
                }
                break;
            } else {
                ScanEdgeResponse next = responseIterator.next();
                if (next != null) {
                    processor = new ScanEdgeProcessor(metaClient);
                    Result processResult = processor.process(connectInfo.getSpaceName(), next);
                    dataIterator = process(processResult);
                }
            }
        }
        if (dataIterator == null) {
            return false;
        }
        return dataIterator.hasNext();
    }

    @Override
    protected Iterator<String> process(Result result) {
        Map<String, List<Row>> dataMap = result.getRows();
        for (Map.Entry<String, List<Row>> dataEntry : dataMap.entrySet()) {
            String labelName = dataEntry.getKey();
            List<Row> rows = dataEntry.getValue();
            for (Row row : rows) {
                List<String> fields = new ArrayList<>(returnCols.get(labelName).size() + 2);
                // add default property _srcId and _dstId for egde
                fields.add(String.valueOf(row.getDefaultProperties()[0].getValue()));
                fields.add(String.valueOf(row.getDefaultProperties()[2].getValue()));
                Property[] properties = row.getProperties();
                for (int i = 0; i < properties.length; i++) {
                    fields.add(String.valueOf(properties[i].getValue()));
                }
                resultValues.put(labelName, fields);
            }
        }
        LOGGER.info("edge info ={}", resultValues.toString());
        return resultValues.keySet().iterator();
    }
}

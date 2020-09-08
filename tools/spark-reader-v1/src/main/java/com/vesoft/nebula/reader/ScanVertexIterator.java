/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.reader;

import com.vesoft.nebula.bean.ConnectInfo;
import com.vesoft.nebula.bean.ScanInfo;
import com.vesoft.nebula.client.storage.processor.ScanVertexProcessor;
import com.vesoft.nebula.data.Property;
import com.vesoft.nebula.data.Result;
import com.vesoft.nebula.exception.GraphOperateException;
import com.vesoft.nebula.storage.ScanVertexResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.spark.Partition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScanVertexIterator extends AbstractNebulaIterator {

    private Logger logger = LoggerFactory.getLogger(ScanVertexIterator.class);

    private Iterator<ScanVertexResponse> responseIterator;

    public ScanVertexIterator(ConnectInfo connectInfo, Partition split,
                              ScanInfo scanInfo, Map<String, Integer> propIndexMap) {
        super(connectInfo, split, scanInfo, propIndexMap);
        processor = new ScanVertexProcessor(metaClient);
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
                        responseIterator = storageClient.scanVertex(connectInfo.getSpaceName(),
                                scanPartIterator.next(), returnCols,
                                false, 1000, 0L, Long.MAX_VALUE);
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                        throw new GraphOperateException(e.getMessage(), e);
                    }
                    continue;
                }
                break;
            } else if (responseIterator.hasNext()) {
                ScanVertexResponse next = responseIterator.next();
                if (next != null) {
                    Result processResult = processor.process(connectInfo.getSpaceName(), next);
                    dataIterator = process(processResult);
                }
                continue;
            }
        }

        if (dataIterator == null) {
            return false;
        }
        return dataIterator.hasNext();
    }

    @Override
    protected Iterator<String[]> process(Result result) {
        List<String[]> resultValues = new ArrayList<>();

        Map<String, List<com.vesoft.nebula.data.Row>> dataMap = result.getRows();
        for (Map.Entry<String, List<com.vesoft.nebula.data.Row>> dataEntry : dataMap.entrySet()) {
            String labelName = dataEntry.getKey();
            List<Integer> propIndexs = labelPropIndexMap.get(labelName);
            List<com.vesoft.nebula.data.Row> rows = dataEntry.getValue();
            for (com.vesoft.nebula.data.Row row : rows) {
                Iterator<Integer> nameIndexIterator = propIndexs.iterator();
                String[] fields = new String[propSize + 1];
                fields[0] = String.valueOf(row.getDefaultProperties()[0].getValue());
                Property[] properties = row.getProperties();
                for (int i = 0; i < properties.length; i++) {
                    fields[nameIndexIterator.next()] = String.valueOf(properties[i].getValue());
                }
                resultValues.add(fields);
            }
        }
        return resultValues.iterator();
    }
}

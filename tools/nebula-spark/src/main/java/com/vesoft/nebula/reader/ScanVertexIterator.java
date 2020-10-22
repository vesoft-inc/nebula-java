/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.reader;

import com.vesoft.nebula.bean.DataSourceConfig;
import com.vesoft.nebula.data.Row;
import com.vesoft.nebula.exception.GraphOperateException;
import com.vesoft.nebula.client.storage.processor.ScanVertexProcessor;
import com.vesoft.nebula.data.Property;
import com.vesoft.nebula.data.Result;
import com.vesoft.nebula.storage.ScanVertexResponse;

import java.io.IOException;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.spark.Partition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScanVertexIterator extends AbstractNebulaIterator {

    private final Logger LOGGER = LoggerFactory.getLogger(ScanVertexIterator.class);

    private Iterator<ScanVertexResponse> responseIterator;

    private DataSourceConfig dataSourceConfig;

    public ScanVertexIterator(Partition split,
                              DataSourceConfig dataSourceConfig) {
        super(split, dataSourceConfig);
        this.dataSourceConfig = dataSourceConfig;
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
                        responseIterator = storageClient.scanVertex(dataSourceConfig.getNameSpace(),
                                scanPartIterator.next(), returnCols, dataSourceConfig.getAllCols(),
                                1000, 0L, Long.MAX_VALUE);
                    } catch (IOException e) {
                        LOGGER.error(e.getMessage(), e);
                        throw new GraphOperateException(e.getMessage(), e);
                    }
                    continue;
                }
                break;
            } else {
                ScanVertexResponse next = responseIterator.next();
                if (next != null) {
                    processor = new ScanVertexProcessor(metaClient);
                    Result processResult = processor.process(dataSourceConfig.getNameSpace(), next);
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
    protected Iterator<List<Object>> process(Result result) {
        Map<String, List<Row>> dataMap = result.getRows();
        for (Map.Entry<String, List<Row>> dataEntry : dataMap.entrySet()) {
            for (Row row : dataEntry.getValue()) {
                List<Object> fields = new ArrayList<>();
                // add default property _vertexId for tag
                fields.add(String.valueOf(row.getDefaultProperties()[0].getValue()));
                Property[] properties = row.getProperties();
                for (int i = 0; i < properties.length; i++) {
                    fields.add(properties[i].getValue());
                }
                resultValues.add(fields);
            }
        }
        LOGGER.info("tag info ={}", resultValues);
        return resultValues.iterator();
    }
}

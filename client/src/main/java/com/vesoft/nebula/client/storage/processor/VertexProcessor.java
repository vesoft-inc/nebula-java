/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.storage.processor;

import com.google.common.collect.Maps;
import com.vesoft.nebula.DataSet;
import com.vesoft.nebula.Row;
import com.vesoft.nebula.Value;
import com.vesoft.nebula.client.storage.data.VertexRow;
import com.vesoft.nebula.client.storage.data.VertexTableRow;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VertexProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(VertexProcessor.class);

    public static Map<String, VertexRow> constructVertexRow(List<DataSet> dataSets,
                                                            String decodeType) {
        Map<String, VertexRow> vidVertices = Maps.newHashMap();
        // todo List<byte[]> columnNames = dataSet.getColumn_names();
        for (DataSet dataSet : dataSets) {
            List<Row> rows = dataSet.getRows();
            List<byte[]> colNames = dataSet.getColumn_names();
            for (Row row : rows) {
                List<Value> values = row.getValues();
                if (values.size() < 1) {
                    LOGGER.error("values size error for row: " + row.toString());
                } else {
                    Value vid = values.get(0);

                    Map<String, Object> props = Maps.newHashMap();
                    for (int i = 0; i < values.size(); i++) {
                        String colName = new String(colNames.get(i)).split("\\.")[1];
                        if (!colName.equals("_vid")) {
                            props.put(colName, getField(values.get(i).getFieldValue(), decodeType));
                        }
                    }
                    VertexRow vertexRow = new VertexRow(vid, props);
                    try {
                        vidVertices.put(new String(vid.getSVal(), decodeType),
                                vertexRow);
                    } catch (UnsupportedEncodingException e) {
                        LOGGER.error("encode error with " + decodeType, e);
                    }
                }
            }
        }
        return vidVertices;
    }

    public static List<VertexTableRow> constructVertexTableRow(List<DataSet> dataSets,
                                                               String decodeType) {
        List<VertexTableRow> vertexRows = new ArrayList<>();
        for (DataSet dataSet : dataSets) {
            List<Row> rows = dataSet.getRows();
            for (Row row : rows) {
                List<Value> values = row.getValues();
                List<Object> props = new ArrayList<>();
                for (int i = 0; i < values.size(); i++) {
                    props.add(getField(values.get(i).getFieldValue(), decodeType));
                }
                vertexRows.add(new VertexTableRow(props));
            }
        }
        return vertexRows;
    }

    private static Object getField(Object obj, String decodeType) {
        if (obj.getClass().getTypeName().equals("byte[]")) {
            try {
                return new String((byte[]) obj, decodeType);
            } catch (UnsupportedEncodingException e) {
                LOGGER.error("encode error with " + decodeType, e);
                return null;
            }
        }
        return obj;
    }


}

/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.client.storage.processor;

import com.google.common.collect.Maps;
import com.vesoft.nebula.DataSet;
import com.vesoft.nebula.Row;
import com.vesoft.nebula.Value;
import com.vesoft.nebula.client.graph.data.ValueWrapper;
import com.vesoft.nebula.client.storage.data.VertexRow;
import com.vesoft.nebula.client.storage.data.VertexTableRow;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VertexProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(VertexProcessor.class);

    public static Map<ValueWrapper, VertexRow> constructVertexRow(List<DataSet> dataSets,
                                                                  String decodeType) {
        Map<ValueWrapper, VertexRow> vidVertices = Maps.newHashMap();
        for (DataSet dataSet : dataSets) {
            List<Row> rows = dataSet.getRows();
            List<byte[]> colNames = dataSet.getColumn_names();
            for (Row row : rows) {
                List<Value> values = row.getValues();
                if (values.size() < 1) {
                    LOGGER.error("values size error for row: " + row.toString());
                } else {
                    Value vid = values.get(0);
                    Map<String, ValueWrapper> props = Maps.newHashMap();
                    for (int i = 1; i < values.size(); i++) {
                        String colName = new String(colNames.get(i)).split("\\.")[1];
                        props.put(colName, new ValueWrapper(values.get(i), decodeType));
                    }
                    VertexRow vertexRow = new VertexRow(new ValueWrapper(vid, decodeType), props);
                    vidVertices.put(new ValueWrapper(vid, decodeType), vertexRow);
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
                List<ValueWrapper> props = new ArrayList<>();
                for (int i = 0; i < values.size(); i++) {
                    props.add(new ValueWrapper(values.get(i), decodeType));
                }
                vertexRows.add(new VertexTableRow(props));
            }
        }
        return vertexRows;
    }
}

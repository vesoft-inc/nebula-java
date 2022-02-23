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
import com.vesoft.nebula.client.storage.data.EdgeRow;
import com.vesoft.nebula.client.storage.data.EdgeTableRow;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EdgeProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(EdgeProcessor.class);

    public static List<EdgeRow> constructEdgeRow(List<DataSet> dataSets, String decodeType) {
        List<EdgeRow> edgeRows = new ArrayList<>();
        for (DataSet dataSet : dataSets) {
            List<Row> rows = dataSet.getRows();
            List<byte[]> colNames = dataSet.getColumn_names();
            for (Row row : rows) {
                List<Value> values = row.getValues();
                if (values.size() < 3) {
                    LOGGER.error("values size error for row: " + row.toString());
                } else {
                    Value srcId = null;
                    Value dstId = null;
                    Value rank = null;
                    Map<String, ValueWrapper> props = Maps.newHashMap();
                    for (int i = 0; i < values.size(); i++) {
                        String colName = new String(colNames.get(i));
                        if (!colName.contains(".")) {
                            continue;
                        }
                        if ("_src".equals(colName.split("\\.")[1])) {
                            srcId = values.get(i);
                        } else if ("_dst".equals(colName.split("\\.")[1])) {
                            dstId = values.get(i);
                        } else if ("_rank".equals(colName.split("\\.")[1])) {
                            rank = values.get(i);
                        } else {
                            props.put(colName.split("\\.")[1],
                                    new ValueWrapper(values.get(i), decodeType));
                        }
                    }
                    EdgeRow edgeRow = new EdgeRow(new ValueWrapper(srcId, decodeType),
                            new ValueWrapper(dstId, decodeType), rank.getIVal(), props);
                    edgeRows.add(edgeRow);
                }
            }
        }
        return edgeRows;
    }

    public static List<EdgeTableRow> constructEdgeTableRow(List<DataSet> dataSets,
                                                           String decodeType) {
        List<EdgeTableRow> edgeRows = new ArrayList<>();
        for (DataSet dataSet : dataSets) {
            List<Row> rows = dataSet.getRows();
            for (Row row : rows) {
                List<Value> values = row.getValues();
                List<ValueWrapper> props = new ArrayList<>();
                for (int i = 0; i < values.size(); i++) {
                    props.add(new ValueWrapper(values.get(i), decodeType));
                }
                edgeRows.add(new EdgeTableRow(props));
            }
        }
        return edgeRows;
    }
}

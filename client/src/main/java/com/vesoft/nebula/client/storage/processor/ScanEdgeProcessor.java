/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.storage.processor;

import com.google.common.collect.Maps;
import com.vesoft.nebula.Schema;
import com.vesoft.nebula.client.meta.MetaClientImpl;
import com.vesoft.nebula.data.Property;
import com.vesoft.nebula.data.Result;
import com.vesoft.nebula.data.Row;
import com.vesoft.nebula.data.RowReader;
import com.vesoft.nebula.meta.EdgeItem;
import com.vesoft.nebula.storage.ScanEdge;
import com.vesoft.nebula.storage.ScanEdgeResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScanEdgeProcessor implements Processor<ScanEdgeResponse> {
    private MetaClientImpl metaClient;

    private static final Logger LOGGER = LoggerFactory.getLogger(ScanEdgeProcessor.class);

    public ScanEdgeProcessor(MetaClientImpl metaClient) {
        this.metaClient = metaClient;
    }

    @Override
    public Result process(String spaceName, ScanEdgeResponse response) {
        Map<Integer, RowReader> readers = Maps.newHashMap();
        Map<String, List<Row>> rows = Maps.newHashMap();
        Map<Integer, String> edgeTypeNameMap = Maps.newHashMap();

        if (response.edge_schema != null) {
            for (Map.Entry<Integer, Schema> entry : response.edge_schema.entrySet()) {
                int edgeType = entry.getKey();
                Schema schema = entry.getValue();
                String edgeName = metaClient.getEdgeNameFromCache(spaceName, edgeType);
                EdgeItem edgeItem = metaClient.getEdgeItemFromCache(spaceName, edgeName);
                long schemaVersion = edgeItem.version;
                readers.put(edgeType, new RowReader(schema, schemaVersion));
                rows.put(edgeName, new ArrayList<>());
                edgeTypeNameMap.put(edgeType, edgeName);
            }
        }

        if (response.edge_data != null) {
            for (ScanEdge scanEdge : response.edge_data) {
                int edgeType = scanEdge.type;
                if (!readers.containsKey(edgeType)) {
                    continue;
                }
                RowReader reader = readers.get(edgeType);
                Property[] defaultProperties = reader.edgeKey(
                        scanEdge.src, scanEdge.type, scanEdge.dst);
                Property[] properties = reader.decodeValue(scanEdge.value);
                String edgeName = edgeTypeNameMap.get(edgeType);
                rows.get(edgeName).add(new Row(defaultProperties, properties));
            }
        }
        return new Result(rows);
    }
}

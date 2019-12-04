/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.storage.client.handler;

import com.vesoft.nebula.Schema;
import com.vesoft.nebula.data.Property;
import com.vesoft.nebula.data.Result;
import com.vesoft.nebula.data.Result.RowDesc;
import com.vesoft.nebula.data.Result.RowType;
import com.vesoft.nebula.data.Row;
import com.vesoft.nebula.data.RowReader;
import com.vesoft.nebula.meta.EdgeItem;
import com.vesoft.nebula.meta.client.MetaClientImpl;
import com.vesoft.nebula.storage.ScanEdge;
import com.vesoft.nebula.storage.ScanEdgeRequest;
import com.vesoft.nebula.storage.ScanEdgeResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScanEdgeConsumer extends ScanConsumer<ScanEdgeRequest, ScanEdgeResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScanEdgeConsumer.class);
    private MetaClientImpl metaClient;
    protected boolean hasNext = false;

    public ScanEdgeConsumer(MetaClientImpl metaClient) {
        this.metaClient = metaClient;
    }

    @Override
    protected Result handleResponse(ScanEdgeRequest curRequest, ScanEdgeResponse response) {
        Map<RowDesc, List<Row>> rows = generateRows(curRequest, response);
        Result result = new Result(rows);

        if (hasNext(response)) {
            curRequest.setCursor(response.next_cursor);
            result.setNextRequest(curRequest);
            hasNext = true;
        } else {
            hasNext = false;
        }
        return result;
    }

    @Override
    protected Result handleResponse(ScanEdgeRequest curRequest, ScanEdgeResponse response,
                                    Iterator<Integer> partIt) {
        Map<RowDesc, List<Row>> rows = generateRows(curRequest, response);
        Result result = new Result(rows, partIt);

        if (hasNext(response, partIt)) {
            hasNext = true;
            if (!response.has_next) {
                ScanEdgeRequest nextRequest = new ScanEdgeRequest();
                nextRequest.space_id = curRequest.space_id;
                nextRequest.part_id = partIt.next();
                nextRequest.row_limit = curRequest.row_limit;
                nextRequest.start_time = curRequest.start_time;
                nextRequest.end_time = curRequest.end_time;
                result.setNextRequest(nextRequest);
            } else {
                curRequest.setCursor(response.next_cursor);
                result.setNextRequest(curRequest);
            }
        } else {
            hasNext = false;
        }
        return result;
    }

    protected Map<RowDesc, List<Row>> generateRows(ScanEdgeRequest curRequest,
                                                   ScanEdgeResponse response) {
        Map<Integer, RowReader> readers = new HashMap<Integer, RowReader>();
        Map<RowDesc, List<Row>> rows = new HashMap<RowDesc, List<Row>>();
        Map<Integer, RowDesc> edgeTypeIndex = new HashMap<Integer, RowDesc>();
        final int space = curRequest.space_id;
        if (response.edge_schema != null) {
            for (Map.Entry<Integer, Schema> entry : response.edge_schema.entrySet()) {
                int edgeType = entry.getKey();
                Schema schema = entry.getValue();
                String edgeName = metaClient.getEdgeNameFromCache(space, edgeType);
                EdgeItem edgeItem = metaClient.getEdgeItemCache(space, edgeName);
                long schemaVersion = edgeItem.version;
                readers.put(edgeType, new RowReader(schema, schemaVersion));
                RowDesc desc = new RowDesc(RowType.EDGE, edgeName);
                rows.put(desc, new ArrayList<Row>());
                edgeTypeIndex.put(edgeType, desc);
            }
        }

        if (response.edge_data != null) {
            for (ScanEdge scanEdge : response.edge_data) {
                int edgeType = scanEdge.type;
                if (!readers.containsKey(edgeType)) {
                    continue;
                }
                RowReader reader = readers.get(edgeType);
                Property[] defaultProperties = reader.decodeEdgeKey(scanEdge.key);
                Property[] properties = reader.decodeValue(scanEdge.value);
                RowDesc desc = edgeTypeIndex.get(edgeType);
                rows.get(desc).add(new Row(defaultProperties, properties));
            }
        }
        return rows;
    }

    @Override
    protected boolean hasNext() {
        return hasNext;
    }

    private boolean hasNext(ScanEdgeResponse response) {
        return response.has_next;
    }

    private boolean hasNext(ScanEdgeResponse response, Iterator<Integer> partIt) {
        return response.has_next || partIt.hasNext();
    }
}

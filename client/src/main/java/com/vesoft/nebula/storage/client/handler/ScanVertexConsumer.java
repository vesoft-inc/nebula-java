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
import com.vesoft.nebula.meta.TagItem;
import com.vesoft.nebula.meta.client.MetaClientImpl;
import com.vesoft.nebula.storage.ScanTag;
import com.vesoft.nebula.storage.ScanVertexRequest;
import com.vesoft.nebula.storage.ScanVertexResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScanVertexConsumer extends ScanConsumer<ScanVertexRequest, ScanVertexResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScanVertexConsumer.class);
    private MetaClientImpl metaClient;
    protected boolean hasNext = false;

    public ScanVertexConsumer(MetaClientImpl metaClient) {
        this.metaClient = metaClient;
    }

    @Override
    protected Result handleResponse(ScanVertexRequest curRequest, ScanVertexResponse response) {
        Map<Result.RowDesc, List<Row>> rows = generateRows(curRequest, response);
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
    protected Result handleResponse(ScanVertexRequest curRequest, ScanVertexResponse response,
                                    Iterator<Integer> partIt) {
        Map<Result.RowDesc, List<Row>> rows = generateRows(curRequest, response);
        Result result = new Result(rows);

        if (hasNext(response, partIt)) {
            hasNext = true;
            if (!response.has_next) {
                ScanVertexRequest nextRequest = new ScanVertexRequest();
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

    protected Map<RowDesc, List<Row>> generateRows(ScanVertexRequest curRequest,
                                         ScanVertexResponse response) {
        Map<Integer, RowReader> readers = new HashMap<Integer, RowReader>();
        Map<RowDesc, List<Row>> rows = new HashMap<RowDesc, List<Row>>();
        Map<Integer, RowDesc> tagIdIndex = new HashMap<Integer, RowDesc>();
        final int space = curRequest.space_id;
        if (response.vertex_schema != null) {
            for (Map.Entry<Integer, Schema> entry : response.vertex_schema.entrySet()) {
                int tagId = entry.getKey();
                Schema schema = entry.getValue();
                String tagName = metaClient.getTagNameFromCache(space, tagId);
                TagItem tagItem = metaClient.getTagItemFromCache(space, tagName);
                long schemaVersioon = tagItem.version;
                readers.put(tagId, new RowReader(schema, schemaVersioon));
                RowDesc desc = new RowDesc(RowType.VERTEX, tagName);
                rows.put(desc, new ArrayList<Row>());
                tagIdIndex.put(tagId, desc);
            }
        }

        if (response.tag_data != null) {
            for (ScanTag scanTag : response.tag_data) {
                int tagId = scanTag.tagId;
                if (!readers.containsKey(tagId)) {
                    continue;
                }
                RowReader reader = readers.get(tagId);
                Property[] defaultProperties = reader.decodeVertexKey(scanTag.key);
                Property[] properties = reader.decodeValue(scanTag.value);
                RowDesc desc = tagIdIndex.get(tagId);
                rows.get(desc).add(new Row(defaultProperties, properties));
            }
        }
        return rows;
    }

    @Override
    protected boolean hasNext() {
        return hasNext;
    }

    private boolean hasNext(ScanVertexResponse response) {
        return response.has_next;
    }

    private boolean hasNext(ScanVertexResponse response, Iterator<Integer> partIt) {
        return response.has_next || partIt.hasNext();
    }
}


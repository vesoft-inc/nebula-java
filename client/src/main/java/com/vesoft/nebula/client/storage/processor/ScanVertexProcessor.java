/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.storage.processor;

import com.vesoft.nebula.Schema;
import com.vesoft.nebula.client.meta.MetaClientImpl;
import com.vesoft.nebula.data.Property;
import com.vesoft.nebula.data.Result;
import com.vesoft.nebula.data.Row;
import com.vesoft.nebula.data.RowReader;
import com.vesoft.nebula.meta.TagItem;
import com.vesoft.nebula.storage.ScanTag;
import com.vesoft.nebula.storage.ScanVertexResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ScanVertexProcessor implements Processor<ScanVertexResponse> {
    private Map<Integer, Schema> schema;
    private List<ScanTag> tags;
    private MetaClientImpl metaClient;

    private static final Logger LOGGER = LoggerFactory.getLogger(ScanEdgeProcessor.class);

    public ScanVertexProcessor(MetaClientImpl metaClient) {
        this.metaClient = metaClient;
    }

    @Override
    public Result process(String spaceName, ScanVertexResponse response) {
        Map<Integer, RowReader> readers = new HashMap<>();
        Map<Result.RowDesc, List<Row>> rows = new HashMap<>();
        Map<Integer, Result.RowDesc> vertexTypeIndex = new HashMap<>();
        if (response.vertex_schema != null) {
            for (Map.Entry<Integer, Schema> entry : response.vertex_schema.entrySet()) {
                int tagId = entry.getKey();
                Schema schema = entry.getValue();
                String tagName = metaClient.getTagNameFromCache(spaceName, tagId);
                TagItem tagItem = metaClient.getTagItemFromCache(spaceName, tagName);
                long schemaVersion = tagItem.version;
                readers.put(tagId, new RowReader(schema, schemaVersion));
                Result.RowDesc desc = new Result.RowDesc(Result.RowType.VERTEX, tagName);
                rows.put(desc, new ArrayList<>());
                vertexTypeIndex.put(tagId, desc);
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
                Result.RowDesc desc = vertexTypeIndex.get(tagId);
                rows.get(desc).add(new Row(defaultProperties, properties));
            }
        }
        return new Result(rows);
    }
}

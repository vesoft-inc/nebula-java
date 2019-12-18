package com.vesoft.nebula.client.storage.processor;

import com.vesoft.nebula.Schema;
import com.vesoft.nebula.storage.ScanTag;
import com.vesoft.nebula.storage.ScanVertexResponse;
import java.util.List;
import java.util.Map;

public class ScanVertexProcessor implements Processor<ScanVertexResponse> {
    private Map<Integer, Schema> schema;
    private List<ScanTag> tags;

    public ScanVertexProcessor() {
    }

    public void processor(ScanVertexResponse response) {
        schema = response.getVertex_schema();
        tags = response.getTag_data();
    }

    public Map<Integer, Schema> getSchema() {
        return schema;
    }

    public List<ScanTag> getTags() {
        return tags;
    }
}
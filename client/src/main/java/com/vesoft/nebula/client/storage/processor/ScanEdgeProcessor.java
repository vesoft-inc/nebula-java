package com.vesoft.nebula.client.storage.processor;

import com.vesoft.nebula.Schema;
import com.vesoft.nebula.storage.ScanEdge;
import com.vesoft.nebula.storage.ScanEdgeResponse;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class ScanEdgeProcessor implements Processor<ScanEdgeResponse> {
    private Map<Integer, Schema> schema;
    private List<ScanEdge> edges;

    @Override
    public void processor(ScanEdgeResponse response) {
        schema = response.getEdge_schema();
        edges = response.getEdge_data();
    }

    public Map<Integer, Schema> getSchema() {
        return schema;
    }

    public List<ScanEdge> getEdges() {
        return edges;
    }
}

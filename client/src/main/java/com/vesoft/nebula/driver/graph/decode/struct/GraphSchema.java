/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.decode.struct;

import static com.vesoft.nebula.driver.graph.decode.DecodeUtils.charset;

import com.google.common.base.Charsets;
import com.vesoft.nebula.proto.graph.EdgeType;
import com.vesoft.nebula.proto.graph.NodeType;
import com.vesoft.nebula.proto.graph.PropertyGraphSchema;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class GraphSchema {

    private int                      graphId;
    private String                   graphName;
    private Map<Integer, NodeSchema> nodeSchemas = new HashMap<>();

    private Map<Integer, EdgeSchema> edgeSchemas = new HashMap<>();

    public GraphSchema(PropertyGraphSchema graphSchema) {
        this.graphId = graphSchema.getGraphId();
        this.graphName = graphSchema.getGraphName().toString(charset);
        for (NodeType nodeType : graphSchema.getNodeTypeList()) {
            nodeSchemas.put(nodeType.getNodeTypeId(), new NodeSchema(nodeType));
        }
        for (EdgeType edgeType : graphSchema.getEdgeTypeList()) {
            edgeSchemas.put(edgeType.getEdgeTypeId(), new EdgeSchema(edgeType));
        }
    }

    public int getGraphId() {
        return graphId;
    }

    public String getGraphName() {
        return graphName;
    }

    public NodeSchema getNodeSchema(int nodeTypeId) {
        return nodeSchemas.get(nodeTypeId);
    }

    public EdgeSchema getEdgeSchema(int edgeTypeId) {
        return edgeSchemas.get(edgeTypeId);
    }
}

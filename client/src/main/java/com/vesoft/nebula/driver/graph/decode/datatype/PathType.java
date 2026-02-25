/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.decode.datatype;

import com.vesoft.nebula.driver.graph.decode.ColumnType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PathType extends DataType {

    private final List<DataType> dataTypes;

    private final List<NodeType> nodeTypes = new ArrayList<>();
    private final List<EdgeType> edgeTypes = new ArrayList<>();

    private final Map<Integer, Map<Integer, Map<String, DataType>>> nodeTypesMap = new HashMap<>();
    private final Map<Integer, Map<Integer, Map<String, DataType>>> edgeTypesMap = new HashMap<>();

    public PathType(List<DataType> dataTypes) {
        super(ColumnType.COLUMN_TYPE_PATH);
        this.dataTypes = dataTypes;
        for (DataType dataType : dataTypes) {
            if (dataType.getType() == ColumnType.COLUMN_TYPE_NODE) {
                nodeTypes.add((NodeType) dataType);
            }
            if (dataType.getType() == ColumnType.COLUMN_TYPE_EDGE) {
                edgeTypes.add((EdgeType) dataType);
            }
        }
        getNodeTypes();
        getEdgeTypes();
    }

    public List<DataType> getDataTypes() {
        return dataTypes;
    }

    public Map<Integer, Map<Integer, Map<String, DataType>>> getNodeTypes() {
        if (nodeTypesMap.isEmpty()) {
            for (NodeType nodeType : nodeTypes) {
                for (Map.Entry<Integer, Map<Integer, Map<String, DataType>>> graphEntry :
                        nodeType.getNodeTypes().entrySet()) {
                    int                                 graphId     = graphEntry.getKey();
                    Map<Integer, Map<String, DataType>> nodeTypeMap = graphEntry.getValue();
                    if (nodeTypesMap.containsKey(graphId)) {
                        Map<Integer, Map<String, DataType>> existNodeTypeMap =
                                nodeTypesMap.get(graphId);
                        for (Map.Entry<Integer, Map<String, DataType>> nodeTypeEntry :
                                nodeTypeMap.entrySet()) {
                            int                   nodeTypeId = nodeTypeEntry.getKey();
                            Map<String, DataType> propMap    = nodeTypeEntry.getValue();
                            if (existNodeTypeMap.containsKey(nodeTypeId)) {
                                existNodeTypeMap.get(nodeTypeId).putAll(propMap);
                            } else {
                                existNodeTypeMap.put(nodeTypeId, propMap);
                            }
                        }
                    } else {
                        nodeTypesMap.put(graphId, nodeTypeMap);
                    }
                }
            }
        }
        return nodeTypesMap;
    }

    public Map<Integer, Map<Integer, Map<String, DataType>>> getEdgeTypes() {
        if (edgeTypesMap.isEmpty()) {
            for (EdgeType edgeType : edgeTypes) {
                for (Map.Entry<Integer, Map<Integer, Map<String, DataType>>> graphEntry :
                        edgeType.getEdgeTypes()
                                .entrySet()) {
                    Integer                             graphId     = graphEntry.getKey();
                    Map<Integer, Map<String, DataType>> edgeTypeMap = graphEntry.getValue();

                    if (edgeTypesMap.containsKey(graphId)) {
                        Map<Integer, Map<String, DataType>> existEdgeTypeMap =
                                edgeTypesMap.get(graphId);

                        for (Map.Entry<Integer, Map<String, DataType>> edgeTypeEntry :
                                edgeTypeMap.entrySet()) {
                            Integer               edgeTypeId = edgeTypeEntry.getKey();
                            Map<String, DataType> propMap    = edgeTypeEntry.getValue();

                            if (existEdgeTypeMap.containsKey(edgeTypeId)) {
                                existEdgeTypeMap.get(edgeTypeId).putAll(propMap);
                            } else {
                                existEdgeTypeMap.put(edgeTypeId, propMap);
                            }
                        }
                    } else {
                        edgeTypesMap.put(graphId, new HashMap<>(edgeTypeMap));
                    }
                }
            }
        }
        return edgeTypesMap;
    }
}

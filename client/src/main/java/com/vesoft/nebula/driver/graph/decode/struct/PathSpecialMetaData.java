/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.decode.struct;

import static com.vesoft.nebula.driver.graph.decode.DecodeUtils.bytesToInt16;
import static com.vesoft.nebula.driver.graph.decode.DecodeUtils.bytesToInt32;
import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.EDGE_TYPE_ID_SIZE;
import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.GRAPH_ELEMENT_TYPE_NUM_SIZE;
import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.GRAPH_ID_SIZE;
import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.NODE_TYPE_ID_SIZE;
import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.PATH_META_DATA_NODE_EDGE_TYPE_INDEX;

import com.google.protobuf.ByteString;
import com.vesoft.nebula.driver.graph.decode.BytesReader;
import com.vesoft.nebula.driver.graph.decode.VectorWrapper;
import com.vesoft.nebula.proto.graph.NestedVector;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

/**
 * decode path's special metadata
 * special meta:
 * |num of node|graph Id|node typeId|pari index|...|num of edge|graph Id|edge typeId|pair index|...
 * |   4B      | 4B     | 2B        | 2B       |...|    4B     | 4B     | 4B       | 2B       |...
 */
public class PathSpecialMetaData {
    // node type id -> pair index
    private final Map<Integer, Map<Integer, Integer>> graphIdAndNodeTypes = new HashMap<>();
    // edge type id -> pair index
    private final Map<Integer, Map<Integer, Integer>> graphIdAndEdgeTypes = new HashMap<>();
    // pair index -> node vector and adj vector pair
    private final Map<Integer, PathVectorPair>        indexAndNodes       = new HashMap<>();
    // pair index -> edge vector and adj vector pair
    private final Map<Integer, PathVectorPair>        indexAndEdges       = new HashMap<>();

    public PathSpecialMetaData(NestedVector vector, ByteOrder byteOrder) {
        ByteString  metaData          = vector.getSpecialMetaData();
        BytesReader reader            = new BytesReader(metaData);
        int         nestedVectorIndex = 0;
        int nodeTypeNum = bytesToInt32(
                reader.read(GRAPH_ELEMENT_TYPE_NUM_SIZE), byteOrder);
        for (int i = 0; i < nodeTypeNum; i++) {
            int graphId    = bytesToInt32(reader.read(GRAPH_ID_SIZE), byteOrder);
            int nodeTypeId = bytesToInt16(reader.read(NODE_TYPE_ID_SIZE), byteOrder);
            int nodeTypePairIndex = bytesToInt16(
                    reader.read(PATH_META_DATA_NODE_EDGE_TYPE_INDEX), byteOrder);
            if (!graphIdAndNodeTypes.containsKey(graphId)) {
                graphIdAndNodeTypes.put(graphId, new HashMap<>());
            }
            graphIdAndNodeTypes.get(graphId).put(nodeTypeId, nodeTypePairIndex);
            PathVectorPair pair = new PathVectorPair(
                    new VectorWrapper(vector.getNestedVectors(nestedVectorIndex++), byteOrder),
                    new VectorWrapper(vector.getNestedVectors(nestedVectorIndex++), byteOrder));
            indexAndNodes.put(nodeTypePairIndex, pair);
        }

        int edgeTypeNum = bytesToInt32(reader.read(GRAPH_ELEMENT_TYPE_NUM_SIZE), byteOrder);
        for (int i = 0; i < edgeTypeNum; i++) {
            int graphId    = bytesToInt32(reader.read(GRAPH_ID_SIZE), byteOrder);
            int edgeTypeId = bytesToInt32(reader.read(EDGE_TYPE_ID_SIZE), byteOrder);
            int edgeTypePairIndex = bytesToInt16(
                    reader.read(PATH_META_DATA_NODE_EDGE_TYPE_INDEX), byteOrder);
            if (!graphIdAndEdgeTypes.containsKey(graphId)) {
                graphIdAndEdgeTypes.put(graphId, new HashMap<>());
            }
            graphIdAndEdgeTypes.get(graphId).put(edgeTypeId, edgeTypePairIndex);
            PathVectorPair pair = new PathVectorPair(
                    new VectorWrapper(vector.getNestedVectors(nestedVectorIndex++), byteOrder),
                    new VectorWrapper(vector.getNestedVectors(nestedVectorIndex++), byteOrder));
            indexAndEdges.put(edgeTypePairIndex, pair);
        }
    }

    public Map<Integer, Map<Integer, Integer>> getGraphIdAndNodeTypes() {
        return graphIdAndNodeTypes;
    }

    public Map<Integer, Map<Integer, Integer>> getGraphIdAndEdgeTypes() {
        return graphIdAndEdgeTypes;
    }

    public Map<Integer, PathVectorPair> getIndexAndNodes() {
        return indexAndNodes;
    }

    public Map<Integer, PathVectorPair> getIndexAndEdges() {
        return indexAndEdges;
    }
}

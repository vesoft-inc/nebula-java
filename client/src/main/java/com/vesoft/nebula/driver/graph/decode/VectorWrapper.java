/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.decode;

import static com.vesoft.nebula.driver.graph.decode.DecodeUtils.bytesToInt16;
import static com.vesoft.nebula.driver.graph.decode.DecodeUtils.bytesToInt32;
import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.GRAPH_ID_SIZE;
import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.INT32_SIZE;
import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.PROPERTY_NUM_SIZE;
import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.VECTOR_INDEX_SIZE;

import com.google.protobuf.ByteString;
import com.vesoft.nebula.driver.graph.decode.datatype.VectorType;
import com.vesoft.nebula.driver.graph.decode.struct.PathSpecialMetaData;
import com.vesoft.nebula.proto.graph.NestedVector;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * the wrapper of NestedVector, maintains some metadata information of the vector
 * to avoid repeated decoding
 */
public class VectorWrapper {
    private final ByteOrder    byteOrder;
    private final NestedVector vector;

    // sub vectors of the vector
    private final List<VectorWrapper> vectorWrappers = new ArrayList<>();

    // whether all the values in this vector is not null
    private final boolean nullAllSet;

    // the map of graph id to graph element type id and its property name and property vector index,
    // useful for node vector and edge vector
    // graphId -> (nodeTypeId/edgeTypeId -> (prop name -> prop vector index))
    private Map<Integer, Map<Integer, Map<String, Integer>>>
            graphElementTypeIdAndPropVectorIndexMap = null;

    // the meta data information of path
    private PathSpecialMetaData pathSpecialMetaData = null;

    private Object constValue = null;


    public VectorWrapper(NestedVector vector, ByteOrder order) {
        this.byteOrder = order;
        this.vector = vector;
        this.nullAllSet = DecodeUtils.isNullBitMapAllSet(vector);
        for (NestedVector vec : vector.getNestedVectorsList()) {
            vectorWrappers.add(new VectorWrapper(vec, byteOrder));
        }
    }

    public NestedVector getVector() {
        return vector;
    }

    public int getNumNestedVectors() {
        return vector.getNumNestedVectors();
    }

    public int getVectorNumRecords() {
        return vector.getCommonMetaData().getNumRecords();
    }

    public VectorType getVectorType() {
        return DecodeUtils.getVectorType(vector);
    }

    public ByteString getVectorData() {
        return vector.getVectorData();
    }

    public ByteString getSpecialMetaData() {
        return vector.getSpecialMetaData();
    }

    public ByteString getNullBitMap() {
        return vector.getNullBitMap();
    }

    /**
     * get the sub vectors
     */
    public List<VectorWrapper> getNestedVectors() {
        return vectorWrappers;
    }

    public VectorWrapper getVectorWrapper(int index) {
        return vectorWrappers.get(index);
    }

    /**
     * whether all value is not null
     *
     * @return true is all value is not null
     */
    public boolean isNullAllSet() {
        return nullAllSet;
    }


    /**
     * set value for constValue
     *
     * @param value Object value
     */
    public void setConstValue(Object value) {
        this.constValue = value;
    }


    /**
     * get value of constValue
     */
    public Object getConstValue() {
        return constValue;
    }

    /**
     * used for node vector or edge vector
     * construct the property vector index map from vector's special metadata
     *
     * @param typeIdSize node type id size(2 bytes) or edge type id size(4 bytes)
     * @return Map : graphId -> (node/edge type id -> (property name -> property vector index))
     */
    public Map<Integer, Map<Integer, Map<String, Integer>>>
        getGraphElementTypeIdAndPropVectorIndexMap(int typeIdSize) {
        if (graphElementTypeIdAndPropVectorIndexMap == null) {
            graphElementTypeIdAndPropVectorIndexMap = new HashMap<>();
            BytesReader reader = new BytesReader(vector.getSpecialMetaData());
            // the first 4 bytes is total number of property name of all node types or edge types
            int propertyNum = bytesToInt32(reader.read(INT32_SIZE), byteOrder);
            // compute all the property names index and jump the length of all property names
            String[] propNames = new String[propertyNum];
            for (int i = 0; i < propertyNum; i++) {
                propNames[i] = reader.readSizedString(byteOrder);
            }
            // node/edge type nums, 4 bytes
            int typeNum = bytesToInt32(reader.read(INT32_SIZE), byteOrder);

            for (int i = 0; i < typeNum; i++) {
                // get the graph id
                int graphId = bytesToInt32(reader.read(GRAPH_ID_SIZE), byteOrder);
                // get the node/edge type id
                final int typeId =
                        typeIdSize == 2
                                ? bytesToInt16(reader.read(typeIdSize), byteOrder)
                                : bytesToInt32(reader.read(typeIdSize), byteOrder);
                int nodePropNum = bytesToInt32(reader.read(PROPERTY_NUM_SIZE), byteOrder);

                Map<String, Integer> propNameToVectorIndex = new HashMap<>();
                for (int j = 0; j < nodePropNum; j++) {
                    int propVectorIndex = bytesToInt32(reader.read(VECTOR_INDEX_SIZE), byteOrder);
                    propNameToVectorIndex.put(propNames[propVectorIndex], propVectorIndex);
                }
                if (!graphElementTypeIdAndPropVectorIndexMap.containsKey(graphId)) {
                    graphElementTypeIdAndPropVectorIndexMap.put(graphId, new HashMap<>());
                }
                graphElementTypeIdAndPropVectorIndexMap
                        .get(graphId)
                        .put(typeId, propNameToVectorIndex);
            }
        }
        return graphElementTypeIdAndPropVectorIndexMap;
    }


    /**
     * used for path vector
     * construct the meta data of path
     *
     * @return {@link PathSpecialMetaData}
     */
    public PathSpecialMetaData getPathSpecialMetaData() {
        if (pathSpecialMetaData == null) {
            pathSpecialMetaData = new PathSpecialMetaData(vector, byteOrder);
        }
        return pathSpecialMetaData;
    }
}

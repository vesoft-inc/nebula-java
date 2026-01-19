/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.decode;

import static com.vesoft.nebula.driver.graph.decode.DecodeUtils.bytesToBool;
import static com.vesoft.nebula.driver.graph.decode.DecodeUtils.bytesToDouble;
import static com.vesoft.nebula.driver.graph.decode.DecodeUtils.bytesToFloat;
import static com.vesoft.nebula.driver.graph.decode.DecodeUtils.bytesToInt16;
import static com.vesoft.nebula.driver.graph.decode.DecodeUtils.bytesToInt32;
import static com.vesoft.nebula.driver.graph.decode.DecodeUtils.bytesToInt64;
import static com.vesoft.nebula.driver.graph.decode.DecodeUtils.bytesToInt8;
import static com.vesoft.nebula.driver.graph.decode.DecodeUtils.bytesToUInt16;
import static com.vesoft.nebula.driver.graph.decode.DecodeUtils.bytesToUInt8;
import static com.vesoft.nebula.driver.graph.decode.DecodeUtils.charset;
import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.ANY_HEADER_SIZE;
import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.BOOL_SIZE;
import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.CHUNK_INDEX_LENGTH_IN_STRING_HEADER;
import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.CHUNK_INDEX_START_POSITION_IN_STRING_HEADER;
import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.CHUNK_OFFSET_LENGTH_IN_STRING_HEADER;
import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.CHUNK_OFFSET_START_POSITION_IN_STRING_HEADER;
import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.DATE_SIZE;
import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.DATE_TIME_SIZE;
import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.DAY_SIZE;
import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.DOUBLE_SIZE;
import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.DURATION_SIZE;
import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.EDGE_TYPE_ID_SIZE;
import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.ELEMENT_NUMBER_SIZE_FOR_ANY_VALUE;
import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.ELEMENT_NUMBER_SIZE_FOR_VECTOR_VALUE;
import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.EMBEDDING_VECTOR_FLOAT_VALUE_SIZE;
import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.FLOAT_SIZE;
import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.GEO_COORDINATE_NUMBER_SIZE;
import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.GEO_HEADER_SIZE;
import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.GEO_LINAER_RING_INDEX_SIZE;
import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.GEO_LINEAR_RING_NUMBER_SIZE;
import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.GEO_POINT_COORDINATE_SIZE;
import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.GEO_SHAPE_SIZE;
import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.GEO_SRID_SIZE;
import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.GRAPH_ID_SIZE;
import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.INT16_SIZE;
import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.INT32_SIZE;
import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.INT64_SIZE;
import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.INT8_SIZE;
import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.LIST_HEADER_SIZE;
import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.LOCAL_TIME_SIZE;
import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.MICRO_SECONDS_OF_DAY;
import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.MICRO_SECONDS_OF_HOUR;
import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.MICRO_SECONDS_OF_MINUTE;
import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.MICRO_SECONDS_OF_SECOND;
import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.MONTH_SIZE;
import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.NODE_ID_SIZE;
import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.NODE_TYPE_ID_SIZE;
import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.RANK_SIZE;
import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.STRING_MAX_VALUE_LENGTH_IN_HEADER;
import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.STRING_SIZE;
import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.STRING_VALUE_LENGTH_SIZE;
import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.VALUE_TYPE_SIZE;
import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.VECTOR_EDGE_HEADER_SIZE;
import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.VECTOR_NODE_HEADER_SIZE;
import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.VECTOR_PATH_HEADER_SIZE;
import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.YEAR_SIZE;
import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.ZONED_DATE_TIME_SIZE;
import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.ZONED_TIME_SIZE;

import com.google.protobuf.ByteString;
import com.vesoft.nebula.driver.graph.data.Edge;
import com.vesoft.nebula.driver.graph.data.EmbeddingVector;
import com.vesoft.nebula.driver.graph.data.Geography;
import com.vesoft.nebula.driver.graph.data.Geography.GeoShape;
import com.vesoft.nebula.driver.graph.data.NDuration;
import com.vesoft.nebula.driver.graph.data.NLineString;
import com.vesoft.nebula.driver.graph.data.NPoint;
import com.vesoft.nebula.driver.graph.data.NPolygon;
import com.vesoft.nebula.driver.graph.data.NRecord;
import com.vesoft.nebula.driver.graph.data.Node;
import com.vesoft.nebula.driver.graph.data.Path;
import com.vesoft.nebula.driver.graph.data.ValueWrapper;
import com.vesoft.nebula.driver.graph.decode.datatype.BasicType;
import com.vesoft.nebula.driver.graph.decode.datatype.DataType;
import com.vesoft.nebula.driver.graph.decode.datatype.EdgeType;
import com.vesoft.nebula.driver.graph.decode.datatype.EmbeddingVectorType;
import com.vesoft.nebula.driver.graph.decode.datatype.ListType;
import com.vesoft.nebula.driver.graph.decode.datatype.NodeType;
import com.vesoft.nebula.driver.graph.decode.datatype.PathType;
import com.vesoft.nebula.driver.graph.decode.datatype.RecordType;
import com.vesoft.nebula.driver.graph.decode.struct.AnyHeader;
import com.vesoft.nebula.driver.graph.decode.struct.AnyValue;
import com.vesoft.nebula.driver.graph.decode.struct.EdgeHeader;
import com.vesoft.nebula.driver.graph.decode.struct.ListHeader;
import com.vesoft.nebula.driver.graph.decode.struct.NodeHeader;
import com.vesoft.nebula.driver.graph.decode.struct.PathAdjHeader;
import com.vesoft.nebula.driver.graph.decode.struct.PathHeader;
import com.vesoft.nebula.driver.graph.decode.struct.PathSpecialMetaData;
import com.vesoft.nebula.driver.graph.decode.struct.PathVectorPair;
import com.vesoft.nebula.driver.graph.decode.struct.ResultGraphSchemas;
import com.vesoft.nebula.proto.graph.NestedVector;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValueParser {

    private ResultGraphSchemas graphSchemas;
    private int                timeZoneOffset;
    private ByteOrder          byteOrder;

    private static final byte[] kOneBitmasks = {
        (byte) (1 << 0), // 0000 0001
        (byte) (1 << 1), // 0000 0010
        (byte) (1 << 2), // 0000 0100
        (byte) (1 << 3), // 0000 1000
        (byte) (1 << 4), // 0001 0000
        (byte) (1 << 5), // 0010 0000
        (byte) (1 << 6), // 0100 0000
        (byte) (1 << 7)  // 1000 0000
    };


    public ValueParser(ResultGraphSchemas graphSchemas,
                       int timeZoneOffset,
                       ByteOrder byteOrder) {
        this.graphSchemas = graphSchemas;
        this.timeZoneOffset = timeZoneOffset;
        this.byteOrder = byteOrder;
    }

    public ValueWrapper decodeValueWrapper(VectorWrapper vector, DataType type, int rowIndex) {
        Object value = decodeValue(vector, type, rowIndex);
        if (value == null) {
            return new ValueWrapper(null, ColumnType.COLUMN_TYPE_NULL);
        }
        if (type.getType() == ColumnType.COLUMN_TYPE_ANY) {
            AnyValue anyValue = (AnyValue) value;
            return new ValueWrapper(anyValue.getValue(), anyValue.getType());
        } else {
            return new ValueWrapper(value, type.getType());
        }
    }

    /**
     * decode the binary at the specified position of the vector
     *
     * @param vector   vector data
     * @param type     data type of the vector
     * @param rowIndex position of the vector need to be decoded
     * @return Object value being decoded
     */
    public Object decodeValue(VectorWrapper vector,
                              DataType type,
                              int rowIndex) {
        // check if the value at index is null.
        if (!vector.isNullAllSet() && !vector.getNullBitMap().toString(charset).isEmpty()) {
            int byteIndex = rowIndex / 8;
            int bitIndex  = rowIndex % 8;
            if ((vector.getNullBitMap().byteAt(byteIndex) & kOneBitmasks[bitIndex]) == 0) {
                return null;
            }
        }

        Object value = null;
        switch (vector.getVectorType()) {
            case FLAT_VECTOR:
                value = decodeFlatValue(vector, type, rowIndex);
                break;
            case CONST_VECTOR:
                if (vector.getConstValue() == null) {
                    ByteString  vectorData = vector.getVectorData();
                    BytesReader reader     = new BytesReader(vectorData);
                    Object      constValue = decodeConstValue(reader, type.getType());
                    vector.setConstValue(constValue);
                }
                value = vector.getConstValue();
                break;
            default:
                throw new RuntimeException("do not support vector type: " + vector.getVectorType());
        }
        return value;
    }


    /**
     * decode the binary in FLAT VECTOR
     *
     * @param vector   vector data
     * @param type     data type of the vector
     * @param rowIndex position of the vector need to be decoded
     * @return Object value being decoded
     */
    private Object decodeFlatValue(VectorWrapper vector,
                                   DataType type,
                                   int rowIndex) {
        ByteString vectorData = vector.getVectorData();
        ByteString valueData;
        switch (type.getType()) {
            case COLUMN_TYPE_NULL:
                return null;
            case COLUMN_TYPE_INT8:
                valueData = getSubBytes(vectorData, INT8_SIZE, rowIndex);
                return bytesToInt8(valueData);
            case COLUMN_TYPE_UINT8:
                valueData = getSubBytes(vectorData, INT8_SIZE, rowIndex);
                return bytesToUInt8(valueData);
            case COLUMN_TYPE_INT16:
                valueData = getSubBytes(vectorData, INT16_SIZE, rowIndex);
                return bytesToInt16(valueData, byteOrder);
            case COLUMN_TYPE_UINT16:
                valueData = getSubBytes(vectorData, INT16_SIZE, rowIndex);
                return bytesToUInt16(valueData, byteOrder);
            case COLUMN_TYPE_INT32:
            case COLUMN_TYPE_UINT32:
                valueData = getSubBytes(vectorData, INT32_SIZE, rowIndex);
                return bytesToInt32(valueData, byteOrder);
            case COLUMN_TYPE_INT64:
            case COLUMN_TYPE_UINT64:
                valueData = getSubBytes(vectorData, INT64_SIZE, rowIndex);
                return bytesToInt64(valueData, byteOrder);
            case COLUMN_TYPE_FLOAT32:
                valueData = getSubBytes(vectorData, FLOAT_SIZE, rowIndex);
                return bytesToFloat(valueData, byteOrder);
            case COLUMN_TYPE_FLOAT64:
                valueData = getSubBytes(vectorData, DOUBLE_SIZE, rowIndex);
                return bytesToDouble(valueData, byteOrder);
            case COLUMN_TYPE_BOOL:
                valueData = getSubBytes(vectorData, BOOL_SIZE, rowIndex);
                return bytesToBool(valueData);
            case COLUMN_TYPE_DECIMAL:
                valueData = getSubBytes(vectorData, STRING_SIZE, rowIndex);
                return stringToDecimal(bytesToString(valueData, vector.getVector()));
            case COLUMN_TYPE_STRING:
                valueData = getSubBytes(vectorData, STRING_SIZE, rowIndex);
                return bytesToString(valueData, vector.getVector());
            case COLUMN_TYPE_DATE:
                valueData = getSubBytes(vectorData, DATE_SIZE, rowIndex);
                return bytesToDate(valueData);
            case COLUMN_TYPE_LOCALTIME:
                valueData = getSubBytes(vectorData, LOCAL_TIME_SIZE, rowIndex);
                return bytesToLocalTime(valueData);
            case COLUMN_TYPE_ZONEDTIME:
                valueData = getSubBytes(vectorData, ZONED_TIME_SIZE, rowIndex);
                return bytesToZonedTime(valueData);
            case COLUMN_TYPE_LOCALDATETIME:
                valueData = getSubBytes(vectorData, DATE_TIME_SIZE, rowIndex);
                return bytesToLocalDateTime(valueData);
            case COLUMN_TYPE_ZONEDDATETIME:
                valueData = getSubBytes(vectorData, ZONED_DATE_TIME_SIZE, rowIndex);
                return bytesToZonedDateTime(valueData);
            case COLUMN_TYPE_DURATION:
                valueData = getSubBytes(vectorData, DURATION_SIZE, rowIndex);
                return bytesToDuration(valueData);
            case COLUMN_TYPE_LIST:
                // get the type for list element
                ListType listType = (ListType) type;
                List<ValueWrapper> list = new ArrayList<>();
                // parse list header：offset + list size
                valueData = getSubBytes(vectorData, LIST_HEADER_SIZE, rowIndex);
                ListHeader listHeader = new ListHeader(valueData, byteOrder);

                for (int i = 0; i < listHeader.getSize(); i++) {
                    list.add(new ValueWrapper(decodeValue(vector.getVectorWrapper(0),
                                                          listType.getValueType(),
                                                          listHeader.getOffset() + i),
                                              listType.getValueType().getType()));
                }
                return list;
            case COLUMN_TYPE_RECORD:
                ByteString specialMetaData = vector.getSpecialMetaData();
                RecordType recordType = (RecordType) type;
                // get the types for record keys: field name -> field data type
                Map<String, DataType> fieldAndDataType = recordType.getFieldTypes();
                Map<String, ValueWrapper> map = new HashMap<>();
                // parse each field of record
                BytesReader reader = new BytesReader(specialMetaData);
                for (int i = 0; i < fieldAndDataType.size(); i++) {
                    String fieldName = reader.readSizedString(byteOrder);
                    Object value = decodeValue(vector.getVectorWrapper(i),
                                               fieldAndDataType.get(fieldName),
                                               rowIndex);
                    map.put(fieldName,
                            new ValueWrapper(value, fieldAndDataType.get(fieldName).getType()));
                }
                return new NRecord(map);
            case COLUMN_TYPE_NODE:
                NodeType nodeType = (NodeType) type;
                // nodePropColumnType: graphId->(nodeTypeId -> (propName-> propType))
                Map<Integer, Map<Integer, Map<String, DataType>>> nodePropColumnType =
                    nodeType.getNodeTypes();
                // nodePropVectorIndex: nodeTypeId -> (propName -> prop vector index)
                Map<Integer, Map<Integer, Map<String, Integer>>> nodePropVectorIndex =
                    vector.getGraphElementTypeIdAndPropVectorIndexMap(NODE_TYPE_ID_SIZE);

                // decode the node's nodeId and graphId from node header
                ByteString nodeHeaderBinary = getSubBytes(vectorData,
                                                          VECTOR_NODE_HEADER_SIZE,
                                                          rowIndex);
                NodeHeader nodeHeader = new NodeHeader(nodeHeaderBinary, byteOrder);
                // decode the record node's property values from sub vectors
                if (!nodePropColumnType.containsKey(nodeHeader.getGraphId())
                    || !nodePropColumnType.get(nodeHeader.getGraphId())
                    .containsKey(nodeHeader.getNodeTypeId())) {
                    throw new RuntimeException(String.format(
                        "Value type for NODE does not contain graphId %d or node type id %d",
                        nodeHeader.getGraphId(),
                        nodeHeader.getNodeTypeId()));
                }
                Map<String, DataType> propTypeMap = nodePropColumnType
                    .get(nodeHeader.getGraphId())
                    .get(nodeHeader.getNodeTypeId());
                Map<String, ValueWrapper> props = new HashMap<>();
                for (String propName : propTypeMap.keySet()) {
                    int vectorIndex = nodePropVectorIndex
                        .get(nodeHeader.getGraphId())
                        .get(nodeHeader.getNodeTypeId())
                        .get(propName);
                    Object propValue = decodeValue(vector.getVectorWrapper(vectorIndex),
                                                   propTypeMap.get(propName),
                                                   rowIndex);
                    props.put(propName, new ValueWrapper(propValue,
                                                         propTypeMap.get(propName).getType()));
                }
                return new Node(nodeHeader.getGraphId(),
                                nodeHeader.getNodeTypeId(),
                                nodeHeader.getNodeId(),
                                props,
                                graphSchemas);
            case COLUMN_TYPE_EDGE:
                EdgeType edgeType = (EdgeType) type;
                // edgePropColumnType: graphId -> (edgeTypeId -> (propName-> propType))
                Map<Integer, Map<Integer, Map<String, DataType>>> edgePropColumnType =
                    edgeType.getEdgeTypes();
                // edgePropVectorIndex: edgeTypeId -> (propName -> prop vector index)
                Map<Integer, Map<Integer, Map<String, Integer>>> edgePropVectorIndex =
                    vector.getGraphElementTypeIdAndPropVectorIndexMap(EDGE_TYPE_ID_SIZE);

                // decode the record edge's edgeTypeId from edge header.
                // edgeTypeID+graphID+rank+dstID+srcID
                ByteString edgeHeaderBinary = getSubBytes(vectorData,
                                                          VECTOR_EDGE_HEADER_SIZE,
                                                          rowIndex);
                EdgeHeader edgeHeader = new EdgeHeader(edgeHeaderBinary, byteOrder);

                // decode the record edge's property values from sub vectors
                int noDirectedTypeId = edgeHeader.getEdgeTypeId() & 0x3FFFFFFF;
                if (!edgePropColumnType.containsKey(edgeHeader.getGraphId())
                    || !edgePropColumnType.get(edgeHeader.getGraphId())
                    .containsKey(noDirectedTypeId)) {
                    throw new RuntimeException(String.format(
                        "Value type for NODE does not contain graphId %d or edge type id %d",
                        edgeHeader.getGraphId(),
                        noDirectedTypeId));
                }

                Map<String, DataType> edgePropTypeMap = edgePropColumnType
                    .get(edgeHeader.getGraphId())
                    .get(noDirectedTypeId);
                Map<String, ValueWrapper> edgeProps = new HashMap<>();
                for (String propName : edgePropTypeMap.keySet()) {
                    int vectorIndex = edgePropVectorIndex
                        .get(edgeHeader.getGraphId())
                        .get(noDirectedTypeId)
                        .get(propName);
                    Object propValue = decodeValue(vector.getVectorWrapper(vectorIndex),
                                                   edgePropTypeMap.get(propName),
                                                   rowIndex);
                    edgeProps.put(propName, new ValueWrapper(propValue,
                                                             edgePropTypeMap
                                                                 .get(propName)
                                                                 .getType()));
                }
                Edge edgeValue = new Edge(edgeHeader.getGraphId(),
                                          edgeHeader.getEdgeTypeId(),
                                          edgeHeader.getRank(),
                                          edgeHeader.getSrcId(),
                                          edgeHeader.getDstId(),
                                          edgeProps,
                                          graphSchemas);
                return edgeValue;

            case COLUMN_TYPE_PATH:
                PathType pathType = (PathType) type;

                // decode the vector data: path header
                ByteString pathHeaderBinary = getSubBytes(vectorData,
                                                          VECTOR_PATH_HEADER_SIZE,
                                                          rowIndex);
                PathHeader pathHeader = new PathHeader(pathHeaderBinary, byteOrder);

                // decode the special meta data into:
                PathSpecialMetaData pathSpecialMetaData = vector.getPathSpecialMetaData();
                // graphId -> (NodeTypeId -> vecIndex),  graphId -> (EdgeTypeId -> vecIndex)
                Map<Integer, Map<Integer, Integer>> nodeTypes =
                    pathSpecialMetaData.getGraphIdAndNodeTypes();
                Map<Integer, Map<Integer, Integer>> edgeTypes =
                    pathSpecialMetaData.getGraphIdAndEdgeTypes();

                // construct map: uint16 pair index-> (node vector, adj vector)
                Map<Integer, PathVectorPair> indexAndNodes = pathSpecialMetaData.getIndexAndNodes();
                // construct map: uint16 pair index-> (edge vector, adj vector)
                Map<Integer, PathVectorPair> indexAndEdges = pathSpecialMetaData.getIndexAndEdges();

                // decode path value
                List<ValueWrapper> elements = new ArrayList<>();
                final DataType adjDataType = new BasicType(ColumnType.COLUMN_TYPE_INT64);
                Object firstNode = null;

                // if path has no element, return empty path
                if (pathHeader.getSize() <= 0) {
                    return new Path(elements);
                }
                // decode the first node of path
                PathVectorPair firstNodePair = indexAndNodes.get(pathHeader.getHeadNodeIndex());
                VectorWrapper firstNodeVector = firstNodePair.getVector();
                VectorWrapper firstNodeAdjVector = firstNodePair.getAdjVector();
                firstNode = decodeValue(firstNodeVector,
                                        pathType.getDataTypes().get(0),
                                        pathHeader.getHeadOffset());
                elements.add(new ValueWrapper(firstNode, ColumnType.COLUMN_TYPE_NODE));
                PathAdjHeader pathAdjHeader = new PathAdjHeader(
                    new ValueWrapper(decodeValue(firstNodeAdjVector,
                                                 adjDataType,
                                                 pathHeader.getHeadOffset()),
                                     adjDataType.getType()).asLong());

                VectorWrapper adjVector = null;
                while (!pathAdjHeader.isEnd()) {
                    int vecIndex  = pathAdjHeader.getVecIdxOfNextEle();
                    int vecOffset = pathAdjHeader.getOffsetOfNextEle();
                    if (pathAdjHeader.isNextEdge()) {
                        PathVectorPair edgeVectorPair = indexAndEdges.get(vecIndex);
                        Object edge = decodeValue(edgeVectorPair.getVector(),
                                                  new EdgeType(pathType.getEdgeTypes()),
                                                  vecOffset);
                        adjVector = edgeVectorPair.getAdjVector();
                        elements.add(new ValueWrapper(edge, ColumnType.COLUMN_TYPE_EDGE));
                        // update the adj header
                        pathAdjHeader = new PathAdjHeader(
                            new ValueWrapper(decodeValue(adjVector,
                                                         adjDataType,
                                                         vecOffset),
                                             adjDataType.getType()).asLong());
                    } else {
                        PathVectorPair nodeVectorPair = indexAndNodes.get(vecIndex);
                        Object node = decodeValue(nodeVectorPair.getVector(),
                                                  new NodeType(pathType.getNodeTypes()),
                                                  vecOffset);
                        adjVector = nodeVectorPair.getAdjVector();
                        elements.add(new ValueWrapper(node, ColumnType.COLUMN_TYPE_NODE));
                        // update the adj header
                        pathAdjHeader = new PathAdjHeader(
                            new ValueWrapper(decodeValue(adjVector,
                                                         adjDataType,
                                                         vecOffset),
                                             adjDataType.getType()).asLong());
                    }
                }
                return new Path(elements);
            case COLUMN_TYPE_ANY:
                valueData = getSubBytes(vector.getVectorData(),
                                        ANY_HEADER_SIZE,
                                        rowIndex);
                return bytesToAny(valueData, vector, rowIndex);
            case COLUMN_TYPE_EMBEDDINGVECTOR:
                EmbeddingVectorType vectorType = (EmbeddingVectorType) type;
                int dim = vectorType.getDim();
                valueData = getSubBytes(vectorData, dim * EMBEDDING_VECTOR_FLOAT_VALUE_SIZE,
                                        rowIndex);
                return bytesToEmbeddingVector(new BytesReader(valueData), dim);
            case COLUMN_TYPE_GEOGRAPHY:
                ByteString header = getSubBytes(vectorData, GEO_HEADER_SIZE, rowIndex);
                int chunkIndex = bytesToInt32(
                    header.substring(0, CHUNK_INDEX_LENGTH_IN_STRING_HEADER), byteOrder);
                int chunkOffset = bytesToInt32(
                    header.substring(CHUNK_INDEX_LENGTH_IN_STRING_HEADER), byteOrder);
                ByteString data = vector
                    .getNestedVectors()
                    .get(chunkIndex)
                    .getVectorData()
                    .substring(chunkOffset);
                return bytesToGeography(new BytesReader(data));
            default:
                throw new RuntimeException("do not support type: " + type);
        }
    }


    private Object decodeConstValue(BytesReader reader,
                                    ColumnType type) {
        Object obj;
        if (ColumnType.isBasic(type)) {
            obj = bytesBasicToObject(reader, type);
        } else if (type == ColumnType.COLUMN_TYPE_STRING) {
            obj = reader.readSizedString(byteOrder);
        } else if (type == ColumnType.COLUMN_TYPE_DECIMAL) {
            obj = bytesToDecimal(reader);
        } else if (ColumnType.isComposite(type)) {
            obj = decodeCompositeValue(reader, type);
        } else if (type == ColumnType.COLUMN_TYPE_ANY) {
            obj = bytesToConstAny(reader);
        } else {
            throw new RuntimeException("do not support type:" + type);
        }
        return obj;
    }


    private ByteString getSubBytes(ByteString vectorData, int byteSize, int rowIndex) {
        return vectorData.substring(rowIndex * byteSize, rowIndex * byteSize + byteSize);
    }


    private int getNodeTypeIdFromNodeId(long nodeId) {
        return (int) (nodeId >> 48);
    }


    /**
     * decode String vector binary to String
     *
     * @param stringHeader binary data, the data is String's header
     * @param vector       String vector
     * @return String value
     */
    public String bytesToString(ByteString stringHeader, NestedVector vector) {
        // if the string is less than 12 bytes, no need to get data from chunk,
        // else get data from chunk and no need to decode the data of 4:8.
        int stringValueLength = bytesToInt32(
            stringHeader.substring(0, STRING_VALUE_LENGTH_SIZE),
            byteOrder);
        if (stringValueLength <= STRING_MAX_VALUE_LENGTH_IN_HEADER) {
            return stringHeader.substring(STRING_VALUE_LENGTH_SIZE,
                                          STRING_VALUE_LENGTH_SIZE + stringValueLength)
                .toString(charset);
        }

        int chunkIndex = bytesToInt32(
            stringHeader.substring(
                CHUNK_INDEX_START_POSITION_IN_STRING_HEADER,
                CHUNK_INDEX_START_POSITION_IN_STRING_HEADER
                    + CHUNK_INDEX_LENGTH_IN_STRING_HEADER),
            byteOrder);
        int chunkOffset = bytesToInt32(
            stringHeader.substring(
                CHUNK_OFFSET_START_POSITION_IN_STRING_HEADER,
                CHUNK_OFFSET_START_POSITION_IN_STRING_HEADER
                    + CHUNK_OFFSET_LENGTH_IN_STRING_HEADER),
            byteOrder);
        NestedVector stringChunkVector = vector.getNestedVectors(chunkIndex);
        ByteString valueData = stringChunkVector
            .getVectorData()
            .substring(chunkOffset, chunkOffset + stringValueLength);
        return valueData.toString(charset);
    }


    /**
     * decode binary to Date
     *
     * @param data binary data
     * @return {@link Date} value
     */
    private LocalDate bytesToDate(ByteString data) {
        int year  = bytesToUInt16(data.substring(0, YEAR_SIZE), byteOrder);
        int month = bytesToUInt8(data.substring(YEAR_SIZE, YEAR_SIZE + MONTH_SIZE));
        int day = bytesToUInt8(data.substring(YEAR_SIZE + MONTH_SIZE,
                                              YEAR_SIZE + MONTH_SIZE + DAY_SIZE));
        return LocalDate.of(year, month, day);
    }

    /**
     * decode binary to local time
     *
     * @param data binary data
     * @return {@link LocalTime} value
     */
    private LocalTime bytesToLocalTime(ByteString data) {
        ByteBuffer buffer = ByteBuffer.wrap(data.toByteArray()).order(byteOrder);
        int        hour   = buffer.get();
        int        minute = buffer.get();
        int        second = buffer.get();
        buffer.get(); // Skip the padding byte
        int microsecond = buffer.getInt();
        return LocalTime.of(hour, minute, second, microsecond * 1000);
    }

    /**
     * decode binary to zoned time
     *
     * @param data binary data
     * @return {@link OffsetTime}value
     */
    private OffsetTime bytesToZonedTime(ByteString data) {
        ByteBuffer buffer        = ByteBuffer.wrap(data.toByteArray()).order(byteOrder);
        int        hour          = buffer.get();
        int        currentOffset = timeZoneOffset;
        if (hour < 0) {
            hour = -hour;
        }
        int minute = buffer.get();
        int second = buffer.get();
        buffer.get(); // Skip the padding byte
        int microsecond = buffer.getInt();
        LocalTime localUtcTime = LocalTime
            .of(hour % 24, minute, second, microsecond * 1000)
            .plusMinutes(currentOffset);
        ZoneOffset offset = ZoneOffset.ofTotalSeconds(timeZoneOffset * 60);
        return OffsetTime.of(localUtcTime, offset);
    }

    /**
     * decode binary to LocalDateTime
     *
     * @param data binary data
     * @return DateTime value
     */
    private LocalDateTime bytesToLocalDateTime(ByteString data) {
        long      qword = ByteBuffer.wrap(data.toByteArray()).order(byteOrder).getLong();
        final int year  = (int) (qword & 0xFFFF);
        qword = qword >> 16;
        final int month = (int) (qword & 0xF);
        qword = qword >> 4;
        final int day = (int) (qword & 0x1F);
        qword = qword >> 5;
        final int hour = (int) (qword & 0x1F);
        qword = qword >> 5;
        final int minute = (int) (qword & 0x3F);
        qword = qword >> 6;
        final int second = (int) (qword & 0x3F);
        qword = qword >> 6;
        final int microsecond = (int) (qword & 0x3FFFFF);

        return LocalDateTime.of(year,
                                month,
                                day,
                                hour,
                                minute,
                                second,
                                microsecond * 1000);
    }

    /**
     * decode binary to ZonedDateTime
     *
     * @param data binary data
     * @return ZonedDateTime value
     */
    private ZonedDateTime bytesToZonedDateTime(ByteString data) {
        LocalDateTime localDateTime = bytesToLocalDateTime(data).plusSeconds(timeZoneOffset * 60L);
        ZoneOffset    offset        = ZoneOffset.ofTotalSeconds(timeZoneOffset * 60);
        return ZonedDateTime.of(localDateTime, offset);
    }


    /**
     * decode binary to Duration
     *
     * @param data binary data
     * @return Duration value
     */
    private NDuration bytesToDuration(ByteString data) {
        ByteBuffer buffer = ByteBuffer.wrap(data.toByteArray()).order(byteOrder);
        long       qword  = buffer.getLong();

        boolean isMonthBased  = (qword & 0x1) == 1;
        long    durationValue = qword >> 1;

        int year     = 0;
        int month    = 0;
        int day      = 0;
        int hour     = 0;
        int minute   = 0;
        int second   = 0;
        int microSec = 0;
        if (isMonthBased) {
            year = (int) (durationValue / 12);
            month = (int) (durationValue % 12);
        } else {
            day = (int) (durationValue / MICRO_SECONDS_OF_DAY);
            hour = (int) (durationValue % MICRO_SECONDS_OF_DAY / MICRO_SECONDS_OF_HOUR);
            minute = (int) (durationValue % MICRO_SECONDS_OF_HOUR / MICRO_SECONDS_OF_MINUTE);
            second = (int) (durationValue % MICRO_SECONDS_OF_MINUTE / MICRO_SECONDS_OF_SECOND);
            microSec = (int) (durationValue % MICRO_SECONDS_OF_SECOND);
        }
        return new NDuration(isMonthBased, year, month, day, hour, minute, second, microSec);
    }


    /**
     * decode binary to EmbeddingVector
     *
     * @param reader BytesReader
     * @return EmbeddingVector
     */
    private EmbeddingVector bytesToEmbeddingVector(BytesReader reader, int dim) {
        List<Float> vector = new ArrayList<>(dim);
        for (int i = 0; i < dim; i++) {
            vector.add(bytesToFloat(reader.read(FLOAT_SIZE), byteOrder));
        }
        return new EmbeddingVector(dim, vector);
    }

    /**
     * decode binary to Geography
     *
     * @param reader BytesReader
     * @return Geography
     */
    private Geography bytesToGeography(BytesReader reader) {
        // get the shape type of Geography
        int shapeType = bytesToInt8(reader.read(GEO_SHAPE_SIZE));
        // get SRID(4 bytes) of Geography
        int      srid  = bytesToInt32(reader.read(GEO_SRID_SIZE), byteOrder);
        GeoShape shape = Geography.GeoShape.getGeoShape(shapeType);
        switch (shape) {
            case GeoShapePoint: {
                double x = bytesToDouble(reader.read(GEO_POINT_COORDINATE_SIZE), byteOrder);
                double y = bytesToDouble(reader.read(GEO_POINT_COORDINATE_SIZE), byteOrder);
                return new NPoint(x, y);
            }
            case GeoShapeLineString: {
                int numCoords = bytesToInt32(
                    reader.read(GEO_COORDINATE_NUMBER_SIZE), byteOrder);
                List<NPoint> points = new ArrayList<>();
                for (int i = 0; i < numCoords; i++) {
                    double x = bytesToDouble(reader.read(GEO_POINT_COORDINATE_SIZE), byteOrder);
                    double y = bytesToDouble(reader.read(GEO_POINT_COORDINATE_SIZE), byteOrder);
                    points.add(new NPoint(x, y));
                }
                return new NLineString(points);
            }
            case GeoShapePolygon: {
                int numLinearRing = bytesToInt32(
                    reader.read(GEO_LINEAR_RING_NUMBER_SIZE), byteOrder);
                List<List<NPoint>> loops = new ArrayList<>();
                // row index stores the different linearRing points' start index and end index.
                int           numRowIndexes = numLinearRing + 1;
                List<Integer> rowIndex      = new ArrayList<>();
                for (int i = 0; i < numRowIndexes; i++) {
                    rowIndex.add(bytesToInt32(reader.read(GEO_LINAER_RING_INDEX_SIZE), byteOrder));
                }
                int numPoints = rowIndex.get(numRowIndexes - 1);
                // The binary stores flat points, and each linearRing is distinguished according
                // to rowIndex. Each linearRing is a set of point.
                List<NPoint> points = new ArrayList<>();
                for (int i = 0; i < numPoints; i++) {
                    double x = bytesToDouble(reader.read(GEO_POINT_COORDINATE_SIZE), byteOrder);
                    double y = bytesToDouble(reader.read(GEO_POINT_COORDINATE_SIZE), byteOrder);
                    points.add(new NPoint(x, y));
                }

                for (int i = 0; i < numLinearRing; i++) {
                    List<NPoint> loop = new ArrayList<>();
                    for (int index = rowIndex.get(i); index < rowIndex.get(i + 1); index++) {
                        loop.add(points.get(index));
                    }
                    loops.add(loop);
                }
                return new NPolygon(loops);
            }
            default:
                throw new RuntimeException("does not support geography shape:" + shapeType);
        }
    }

    /**
     * decode binary to Any Object
     *
     * @param value binary data
     */
    private AnyValue bytesToAny(ByteString value, VectorWrapper vector, int rowIndex) {
        VectorWrapper dataTypeVector = vector.getVectorWrapper(0);
        ColumnType valueType = ColumnType.getColumnType(bytesToInt8(
            getSubBytes(dataTypeVector.getVectorData(), VALUE_TYPE_SIZE, rowIndex)));
        AnyHeader anyHeader = new AnyHeader(value, valueType, byteOrder);
        Object    obj       = null;

        if (ColumnType.isBasic(valueType)) {
            BytesReader basicReader = new BytesReader(value);
            obj = bytesBasicToObject(basicReader, valueType);
        }
        if (valueType == ColumnType.COLUMN_TYPE_STRING
            || valueType == ColumnType.COLUMN_TYPE_DECIMAL) {
            VectorWrapper stringVec = vector.getVectorWrapper((int) anyHeader.getChunkIndex());
            obj = DecodeUtils.bytesToSizedString(stringVec.getVectorData(),
                                                 (int) anyHeader.getOffset(),
                                                 byteOrder);
        }
        if (ColumnType.isComposite(valueType)) {
            VectorWrapper subVector = vector.getVectorWrapper((int) anyHeader.getChunkIndex());
            BytesReader reader = new BytesReader(
                subVector
                    .getVectorData()
                    .substring((int) anyHeader.getOffset()));
            obj = decodeCompositeValue(reader, valueType);
        }
        return new AnyValue(obj, valueType);
    }

    /**
     * decode const vector to any object
     *
     * @param reader bytes reader for any vector data
     * @return ValueWrapper
     */
    private AnyValue bytesToConstAny(BytesReader reader) {
        ColumnType columnType = ColumnType.getColumnType(
            bytesToUInt8(reader.read(VALUE_TYPE_SIZE)));
        Object obj;
        if (ColumnType.isBasic(columnType)) {
            obj = bytesBasicToObject(reader, columnType);
        } else if (columnType == ColumnType.COLUMN_TYPE_STRING) {
            obj = reader.readSizedString(byteOrder);
        } else if (columnType == ColumnType.COLUMN_TYPE_DECIMAL) {
            obj = bytesToDecimal(reader);
        } else if (ColumnType.isComposite(columnType)) {
            obj = decodeCompositeValue(reader, columnType);
        } else {
            throw new RuntimeException("do not support type:" + columnType);
        }
        return new AnyValue(obj, columnType);
    }


    private Object bytesBasicToObject(BytesReader reader, ColumnType type) {
        Object obj = null;
        switch (type) {
            case COLUMN_TYPE_NULL:
                break;
            case COLUMN_TYPE_BOOL:
                obj = bytesToBool(reader.read(BOOL_SIZE));
                break;
            case COLUMN_TYPE_INT8:
                obj = bytesToInt8(reader.read(INT8_SIZE));
                break;
            case COLUMN_TYPE_UINT8:
                obj = bytesToUInt8(reader.read(INT8_SIZE));
                break;
            case COLUMN_TYPE_INT16:
                obj = bytesToInt16(reader.read(INT16_SIZE), byteOrder);
                break;
            case COLUMN_TYPE_UINT16:
                obj = bytesToUInt16(reader.read(INT16_SIZE), byteOrder);
                break;
            case COLUMN_TYPE_INT32:
            case COLUMN_TYPE_UINT32:
                obj = bytesToInt32(reader.read(INT32_SIZE), byteOrder);
                break;
            case COLUMN_TYPE_INT64:
            case COLUMN_TYPE_UINT64:
                obj = bytesToInt64(reader.read(INT64_SIZE), byteOrder);
                break;
            case COLUMN_TYPE_FLOAT32:
                obj = bytesToFloat(reader.read(FLOAT_SIZE), byteOrder);
                break;
            case COLUMN_TYPE_FLOAT64:
                obj = bytesToDouble(reader.read(DOUBLE_SIZE), byteOrder);
                break;
            case COLUMN_TYPE_DATE:
                obj = bytesToDate(reader.read(DATE_SIZE));
                break;
            case COLUMN_TYPE_LOCALTIME:
                obj = bytesToLocalTime(reader.read(LOCAL_TIME_SIZE));
                break;
            case COLUMN_TYPE_ZONEDTIME:
                obj = bytesToZonedTime(reader.read(ZONED_TIME_SIZE));
                break;
            case COLUMN_TYPE_LOCALDATETIME:
                obj = bytesToLocalDateTime(reader.read(DATE_TIME_SIZE));
                break;
            case COLUMN_TYPE_ZONEDDATETIME:
                obj = bytesToZonedDateTime(reader.read(ZONED_DATE_TIME_SIZE));
                break;
            case COLUMN_TYPE_DURATION:
                obj = bytesToDuration(reader.read(DURATION_SIZE));
                break;
            case COLUMN_TYPE_GEOGRAPHY:
                obj = bytesToGeography(reader);
                break;
            default:
                throw new RuntimeException("type is not basic:" + type);
        }
        return obj;
    }


    /**
     * decode binary to object for decimal
     *
     * @param reader BinaryReader with cursor
     * @return Object
     * @throws NumberFormatException if decimal is Infinity or Nan.
     */
    private Object bytesToDecimal(BytesReader reader) {
        String decimalStr = reader.readSizedString(byteOrder);
        return stringToDecimal(decimalStr);
    }

    /**
     * convert string to decimal
     *
     * @param decimalStr decimal string value
     * @return Object
     * @throws NumberFormatException if decimal is Infinity or Nan.
     */
    private Object stringToDecimal(String decimalStr) {
        if (decimalStr.equals("NaN") || decimalStr.equals("+Inf") || decimalStr.equals("-Inf")) {
            throw new NumberFormatException(decimalStr);
        }
        return new BigDecimal(decimalStr);
    }


    /**
     * decode binary to object for composite type
     *
     * @param reader BinaryReader with cursor
     * @param type   ColumnType for the element
     * @return Object value
     */
    private Object decodeCompositeValue(BytesReader reader, ColumnType type) {
        switch (type) {
            case COLUMN_TYPE_NULL:
                return null;
            case COLUMN_TYPE_BOOL:
                return bytesToBool(reader.read(BOOL_SIZE));
            case COLUMN_TYPE_INT8:
                return bytesToInt8(reader.read(INT8_SIZE));
            case COLUMN_TYPE_UINT8:
                return bytesToUInt8(reader.read(INT8_SIZE));
            case COLUMN_TYPE_INT16:
                return bytesToInt16(reader.read(INT16_SIZE), byteOrder);
            case COLUMN_TYPE_UINT16:
                return bytesToUInt16(reader.read(INT16_SIZE), byteOrder);
            case COLUMN_TYPE_INT32:
            case COLUMN_TYPE_UINT32:
                return bytesToInt32(reader.read(INT32_SIZE), byteOrder);
            case COLUMN_TYPE_INT64:
            case COLUMN_TYPE_UINT64:
                return bytesToInt64(reader.read(INT64_SIZE), byteOrder);
            case COLUMN_TYPE_FLOAT32:
                return bytesToFloat(reader.read(FLOAT_SIZE), byteOrder);
            case COLUMN_TYPE_FLOAT64:
                return bytesToDouble(reader.read(DOUBLE_SIZE), byteOrder);
            case COLUMN_TYPE_DATE:
                return bytesToDate(reader.read(DATE_SIZE));
            case COLUMN_TYPE_LOCALDATETIME:
                return bytesToLocalDateTime(reader.read(DATE_TIME_SIZE));
            case COLUMN_TYPE_ZONEDDATETIME:
                return bytesToZonedDateTime(reader.read(ZONED_DATE_TIME_SIZE));
            case COLUMN_TYPE_LOCALTIME:
                return bytesToLocalTime(reader.read(LOCAL_TIME_SIZE));
            case COLUMN_TYPE_ZONEDTIME:
                return bytesToZonedTime(reader.read(ZONED_TIME_SIZE));
            case COLUMN_TYPE_DURATION:
                return bytesToDuration(reader.read(DURATION_SIZE));
            case COLUMN_TYPE_DECIMAL:
                return new BigDecimal(reader.readSizedString(byteOrder));
            case COLUMN_TYPE_STRING:
                return reader.readSizedString(byteOrder);
            case COLUMN_TYPE_LIST:
                ColumnType eleType = ColumnType.getColumnType(
                    bytesToInt8(reader.read(VALUE_TYPE_SIZE)));
                int listSize = bytesToUInt16(
                    reader.read(ELEMENT_NUMBER_SIZE_FOR_ANY_VALUE), byteOrder);
                int nullBitSize = (listSize % 8 == 0) ? (listSize / 8) : (listSize / 8 + 1);
                ByteString nullBitBytes = reader.read(nullBitSize);
                List<ValueWrapper> values = new ArrayList<>();
                for (int i = 0; i < listSize; i++) {
                    if ((nullBitBytes.byteAt(i / 8) & (1 << (i % 8))) == 0) {
                        values.add(new ValueWrapper(null, ColumnType.COLUMN_TYPE_NULL));
                    } else {
                        values.add(new ValueWrapper(decodeCompositeValue(reader, eleType),
                                                    eleType));
                    }
                }
                return values;
            case COLUMN_TYPE_RECORD:
                int recordSize = bytesToUInt16(
                    reader.read(ELEMENT_NUMBER_SIZE_FOR_ANY_VALUE), byteOrder);
                Map<String, ValueWrapper> map = new HashMap<>();
                for (int i = 0; i < recordSize; i++) {
                    String fieldName = reader.readSizedString(byteOrder);
                    ColumnType fieldType = ColumnType.getColumnType(
                        bytesToUInt8(reader.read(VALUE_TYPE_SIZE)));
                    Object fieldValue = decodeCompositeValue(reader, fieldType);
                    map.put(fieldName, new ValueWrapper(fieldValue, fieldType));
                }
                return new NRecord(map);
            case COLUMN_TYPE_NODE:
                // nodeID 8B + graphId 4B + prop_Size 2B
                long nodeId = bytesToInt64(reader.read(NODE_ID_SIZE), byteOrder);
                int nodeTypeId = getNodeTypeIdFromNodeId(nodeId);
                int nodeGraphId = bytesToInt32(reader.read(GRAPH_ID_SIZE), byteOrder);
                int nodePropNum = bytesToUInt16(
                    reader.read(ELEMENT_NUMBER_SIZE_FOR_ANY_VALUE), byteOrder);
                Map<String, ValueWrapper> nodeProperties = new HashMap<>();
                for (int i = 0; i < nodePropNum; i++) {
                    String propName = reader.readSizedString(byteOrder);
                    ColumnType propType = ColumnType.getColumnType(
                        bytesToUInt8(reader.read(VALUE_TYPE_SIZE)));
                    Object propValue = decodeCompositeValue(reader, propType);
                    nodeProperties.put(propName, new ValueWrapper(propValue, propType));
                }
                return new Node(nodeGraphId, nodeTypeId, nodeId, nodeProperties, graphSchemas);
            case COLUMN_TYPE_EDGE:
                // srcNodeID 8B+dstNodeID 8B+edgeRank 8B+graphId 4B+edgeTypeID 4B+prop_size 2B
                long srcNodeId = bytesToInt64(reader.read(NODE_ID_SIZE), byteOrder);
                long dstNodeId = bytesToInt64(reader.read(NODE_ID_SIZE), byteOrder);
                long rank = bytesToInt64(reader.read(RANK_SIZE), byteOrder);
                int edgeGraphId = bytesToInt32(reader.read(GRAPH_ID_SIZE), byteOrder);
                int edgeTypeId = bytesToInt32(reader.read(EDGE_TYPE_ID_SIZE), byteOrder);
                int edgePropNum = bytesToUInt16(
                    reader.read(ELEMENT_NUMBER_SIZE_FOR_ANY_VALUE), byteOrder);
                Map<String, ValueWrapper> edgeProperties = new HashMap<>();
                for (int i = 0; i < edgePropNum; i++) {
                    String propName = reader.readSizedString(byteOrder);
                    ColumnType propType = ColumnType.getColumnType(
                        bytesToUInt8(reader.read(VALUE_TYPE_SIZE)));
                    Object propValue = decodeCompositeValue(reader, propType);
                    edgeProperties.put(propName, new ValueWrapper(propValue, propType));
                }
                return new Edge(edgeGraphId,
                                edgeTypeId,
                                rank,
                                srcNodeId,
                                dstNodeId,
                                edgeProperties,
                                graphSchemas);
            case COLUMN_TYPE_PATH:
                int elementNum = bytesToUInt16(
                    reader.read(ELEMENT_NUMBER_SIZE_FOR_ANY_VALUE), byteOrder);
                List<ValueWrapper> eleValues = new ArrayList<>();
                for (int i = 0; i < elementNum; i++) {
                    ColumnType elementType = ColumnType.getColumnType(
                        bytesToUInt8(reader.read(VALUE_TYPE_SIZE)));
                    Object element = decodeCompositeValue(reader, elementType);
                    eleValues.add(new ValueWrapper(element, elementType));
                }
                return new Path(eleValues);
            case COLUMN_TYPE_EMBEDDINGVECTOR:
                int vectorEleNum = bytesToInt16(reader.read(ELEMENT_NUMBER_SIZE_FOR_VECTOR_VALUE),
                                                byteOrder);
                return bytesToEmbeddingVector(reader, vectorEleNum);
            default:
                throw new RuntimeException("do not support type:" + type);
        }
    }


}

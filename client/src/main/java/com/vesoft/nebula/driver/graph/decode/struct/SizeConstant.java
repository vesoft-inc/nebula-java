/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.decode.struct;

public class SizeConstant {

    public static final int VALUE_TYPE_SIZE                              = 1;
    // Byte size for different data type
    public static final int INT8_SIZE                                    = 1;
    public static final int INT16_SIZE                                   = 2;
    public static final int INT32_SIZE                                   = 4;
    public static final int INT64_SIZE                                   = 8;
    public static final int FLOAT_SIZE                                   = 4;
    public static final int DOUBLE_SIZE                                  = 8;
    public static final int BOOL_SIZE                                    = 1;
    // string size: 4 byte string value length + 4 byte prefix string
    // + 4 byte chunk offset + 4 byte chunk index
    public static final int STRING_SIZE                                  = 16;
    public static final int CHUNK_INDEX_LENGTH_IN_STRING_HEADER          = 4;
    public static final int CHUNK_INDEX_START_POSITION_IN_STRING_HEADER  = 12;
    public static final int CHUNK_OFFSET_LENGTH_IN_STRING_HEADER         = 4;
    public static final int CHUNK_OFFSET_START_POSITION_IN_STRING_HEADER = 8;
    public static final int STRING_VALUE_LENGTH_SIZE                     = 4;
    public static final int STRING_MAX_VALUE_LENGTH_IN_HEADER            = 12;

    public static final int YEAR_SIZE     = 2;
    public static final int MONTH_SIZE    = 1;
    public static final int DAY_SIZE      = 1;
    public static final int HOUR_SIZE     = 1;
    public static final int MINUTE_SIZE   = 1;
    public static final int SECOND_SIZE   = 1;
    public static final int MICROSEC_SIZE = 4;

    public static final int DATE_SIZE            = YEAR_SIZE + MONTH_SIZE + DAY_SIZE;
    public static final int DATE_TIME_SIZE       = 8;
    public static final int ZONED_DATE_TIME_SIZE = 8;
    public static final int LOCAL_TIME_SIZE      = 8;

    public static final int ZONED_TIME_SIZE = 8;

    public static final int DURATION_SIZE = 8;


    // list
    // list header size
    public static final int LIST_HEADER_SIZE = 8;
    // list size size
    public static final int LIST_SIZE_SIZE   = 4;

    // set
    // set header size
    public static final int SET_HEADER_SIZE = 8;
    public static final int SET_SIZE_SIZE   = 4;
    public static final int ELEMENT_NUMBER_SIZE_FOR_SET = 4;

    // map
    // set header size
    public static final int MAP_HEADER_SIZE = 8;
    public static final int MAP_SIZE_SIZE   = 4;

    public static final int ELEMENT_NUMBER_SIZE_FOR_MAP = 4;

    public static final int RECORD_HEADER_SIZE = 8;
    public static final int ANY_HEADER_SIZE    = 8;

    // Byte size for the element
    public static final int GRAPH_ID_SIZE     = 4;
    public static final int NODE_TYPE_ID_SIZE = 2;
    public static final int EDGE_TYPE_ID_SIZE = 4;
    public static final int NODE_ID_SIZE      = 8;


    public static final int RANK_SIZE = 8;


    // node or edge type num size
    public static final int GRAPH_ELEMENT_TYPE_NUM_SIZE = 4;
    public static final int PROPERTY_NUM_SIZE           = 4;

    // path
    // element num size for path
    public static final int PATH_ELEMENT_NUM_SIZE               = 4;
    // size for value for nodeTypes or edgeTypes for path: map<NodeTypeId, uint16_t> in server
    public static final int PATH_META_DATA_NODE_EDGE_TYPE_INDEX = 2;
    public static final int PATH_HEADER_SIZE_SIZE               = 4;
    public static final int PATH_HEADER_LENGTH_SIZE             = 4;
    public static final int PATH_HEADER_HEAD_OFFSET_SIZE        = 4;
    public static final int PATH_HEADER_TAIL_OFFSET_SIZE        = 4;


    // filed num size for record
    public static final int RECORD_FIELD_NUM_SIZE = 4;

    // size for vector
    public static final int VECTOR_NODE_HEADER_SIZE = 16;
    public static final int VECTOR_EDGE_HEADER_SIZE = 32;
    public static final int VECTOR_PATH_HEADER_SIZE = 16;
    public static final int VECTOR_INDEX_SIZE       = 4;

    // value for time
    public static final long MICRO_SECONDS_OF_SECOND = 1_000_000L;
    public static final long MICRO_SECONDS_OF_MINUTE = 60L * MICRO_SECONDS_OF_SECOND;
    public static final long MICRO_SECONDS_OF_HOUR   = 60L * MICRO_SECONDS_OF_MINUTE;
    public static final long MICRO_SECONDS_OF_DAY    = 24L * MICRO_SECONDS_OF_HOUR;

    // size for any
    public static final int ELEMENT_NUMBER_SIZE_FOR_ANY_VALUE = 2;

    // dim size for embeddingVector
    public static final int EMBEDDING_VECTOR_DIM_SIZE            = 4;
    public static final int ELEMENT_NUMBER_SIZE_FOR_VECTOR_VALUE = 2;
    public static final int EMBEDDING_VECTOR_FLOAT_VALUE_SIZE    = 4;

    // size for geo
    public static final int GEO_HEADER_SIZE             = 8;
    public static final int GEO_SHAPE_SIZE              = 1;
    public static final int GEO_SRID_SIZE               = 4;
    public static final int GEO_POINT_COORDINATE_SIZE   = 8;
    public static final int GEO_COORDINATE_NUMBER_SIZE  = 4;
    public static final int GEO_LINEAR_RING_NUMBER_SIZE = 4;
    public static final int GEO_LINAER_RING_INDEX_SIZE  = 4;


}

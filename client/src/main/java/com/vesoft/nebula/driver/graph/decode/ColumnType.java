/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.decode;

public enum ColumnType {
    COLUMN_TYPE_NODE(0x1),
    COLUMN_TYPE_EDGE(0x2),
    COLUMN_TYPE_NULL(0x3),
    COLUMN_TYPE_BOOL(0x4),
    COLUMN_TYPE_INT8(0x5),
    COLUMN_TYPE_UINT8(0x6),
    COLUMN_TYPE_INT16(0x7),
    COLUMN_TYPE_UINT16(0x8),
    COLUMN_TYPE_INT32(0x9),
    COLUMN_TYPE_UINT32(0xA),
    COLUMN_TYPE_INT64(0xB),
    COLUMN_TYPE_UINT64(0xC),
    COLUMN_TYPE_FLOAT32(0xD),
    COLUMN_TYPE_FLOAT64(0xE),
    // not support
    // COLUMN_TYPE_BYTES(0xF),
    COLUMN_TYPE_STRING(0x10),
    COLUMN_TYPE_LIST(0x11),
    COLUMN_TYPE_PATH(0x12),
    COLUMN_TYPE_RECORD(0x13),
    COLUMN_TYPE_EMBEDDINGVECTOR(0x14),
    COLUMN_TYPE_LOCALTIME(0x15),
    COLUMN_TYPE_DURATION(0x16),
    COLUMN_TYPE_DATE(0x17),
    COLUMN_TYPE_LOCALDATETIME(0x18),
    COLUMN_TYPE_ZONEDTIME(0x19),
    COLUMN_TYPE_ZONEDDATETIME(0x20),
    COLUMN_TYPE_REFERENCE(0x21),
    COLUMN_TYPE_DECIMAL(0x22),
    // Geography represents spatial data tied to specific locations on Earth,
    // while Geometry is reserved for abstract spatial data not tied to Earth.
    // Geometry may be added in the future.
    COLUMN_TYPE_GEOGRAPHY(0x24),
    COLUMN_TYPE_SET(0x25),
    COLUMN_TYPE_MAP(0x26),
    COLUMN_TYPE_ANY(0xFE),
    COLUMN_TYPE_INVALID(0xFF),
    ;

    private int type;

    private ColumnType(int type) {
        this.type = type;
    }


    public static ColumnType getColumnType(int type) {
        for (ColumnType columnType : values()) {
            if (columnType.type == type) {
                return columnType;
            }
        }
        throw new RuntimeException("does not define the column type:" + type);
    }


    public static boolean isBasic(ColumnType type) {
        switch (type) {
            case COLUMN_TYPE_NULL:
            case COLUMN_TYPE_BOOL:
            case COLUMN_TYPE_INT8:
            case COLUMN_TYPE_UINT8:
            case COLUMN_TYPE_INT16:
            case COLUMN_TYPE_UINT16:
            case COLUMN_TYPE_INT32:
            case COLUMN_TYPE_UINT32:
            case COLUMN_TYPE_INT64:
            case COLUMN_TYPE_UINT64:
            case COLUMN_TYPE_FLOAT32:
            case COLUMN_TYPE_FLOAT64:
            case COLUMN_TYPE_DATE:
            case COLUMN_TYPE_LOCALTIME:
            case COLUMN_TYPE_ZONEDTIME:
            case COLUMN_TYPE_LOCALDATETIME:
            case COLUMN_TYPE_ZONEDDATETIME:
            case COLUMN_TYPE_DURATION:
            case COLUMN_TYPE_GEOGRAPHY:
                return true;
            default:
                return false;
        }
    }

    public static boolean isComposite(ColumnType type) {
        switch (type) {
            case COLUMN_TYPE_LIST:
            case COLUMN_TYPE_RECORD:
            case COLUMN_TYPE_SET:
            case COLUMN_TYPE_MAP:
            case COLUMN_TYPE_EMBEDDINGVECTOR:
            case COLUMN_TYPE_NODE:
            case COLUMN_TYPE_EDGE:
            case COLUMN_TYPE_PATH:
                return true;
            default:
                return false;
        }
    }
}

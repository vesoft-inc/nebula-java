/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.decode.struct;

import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.INT32_SIZE;

import com.google.protobuf.ByteString;
import com.vesoft.nebula.driver.graph.decode.BytesReader;
import com.vesoft.nebula.driver.graph.decode.ColumnType;
import com.vesoft.nebula.driver.graph.decode.DecodeUtils;
import java.nio.ByteOrder;

/**
 * fixed-width header of anyHeader, 8 bytes. The header is saved in vector_data.
 *
 * <p>If the actual data type is fixed and is <= 8bytes, like int,bool, the value data is stored in
 * vector_data and there's no chunkIndex and offset information.
 * If the actual data type is String or Composite, the value data is stored in sub-nestedVector,
 * and the header stores the value's chunkIndex and offset.
 */
public class AnyHeader {

    // uint32, the index of vector where the value data located, start from 1.
    // index 0 indicate the data type vector
    private long chunkIndex;

    // uint 32, the offset of the vector where the value data located.
    private long offset;


    public AnyHeader(ByteString data, ColumnType type, ByteOrder byteOrder) {
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
            case COLUMN_TYPE_DECIMAL:
                break;
            default:
                BytesReader reader = new BytesReader(data);
                this.chunkIndex = DecodeUtils.bytesToUInt32(reader.read(INT32_SIZE), byteOrder) + 1;
                this.offset = DecodeUtils.bytesToUInt32(reader.read(INT32_SIZE), byteOrder);
        }
    }

    public long getChunkIndex() {
        return chunkIndex;
    }

    public long getOffset() {
        return offset;
    }
}

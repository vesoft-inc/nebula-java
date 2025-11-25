/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.decode.struct;

import com.google.protobuf.ByteString;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class MapHeader {
    // uint32, the offset of vector where the first element of list is located.
    private int offset;

    // uint32, the number of element in the list
    private int size;

    public MapHeader(ByteString byteString, ByteOrder order) {
        ByteBuffer buffer = ByteBuffer
            .wrap(byteString.toByteArray())
            .order(order);
        offset = buffer.getInt();
        size = buffer.getInt();
    }

    public int getOffset() {
        return offset;
    }

    public int getSize() {
        return size;
    }
}

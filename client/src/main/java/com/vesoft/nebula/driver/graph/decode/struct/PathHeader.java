/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.decode.struct;

import com.google.protobuf.ByteString;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * PathHeader is stored in Vector data, path header is 16 bytes.
 * headNodeId: 8 bytes
 * tailNodeId: 8 bytes
 * size: 4 bytes
 * length: 4 bytes
 * headOffset: 4 bytes
 * tailOffset: 4 bytes
 */
public class PathHeader {
    private long size; // numNodes + numEdges, uint 32
    private int  headNodeIndex; // uint16
    private int  tailNodeIndex; // uint16

    // head node offset in the vector, uint 32
    private int headOffset;
    // tail node offset in the vector, uint 32
    private int tailOffset;

    public PathHeader(ByteString byteString, ByteOrder order) {
        ByteBuffer buffer = ByteBuffer
                .wrap(byteString.toByteArray())
                .order(order);
        this.size = Integer.toUnsignedLong(buffer.getInt());
        this.headNodeIndex = buffer.getShort() & 0xFFFF;
        this.tailNodeIndex = buffer.getShort() & 0xFFFF;
        this.headOffset = buffer.getInt();
        this.tailOffset = buffer.getInt();
    }


    public int getHeadNodeIndex() {
        return headNodeIndex;
    }

    public int getTailNodeIndex() {
        return tailNodeIndex;
    }

    public long getSize() {
        return size;
    }


    public int getHeadOffset() {
        return headOffset;
    }

    public int getTailOffset() {
        return tailOffset;
    }
}

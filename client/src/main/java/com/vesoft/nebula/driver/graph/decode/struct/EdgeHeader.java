/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.decode.struct;

import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.EDGE_TYPE_ID_SIZE;
import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.GRAPH_ID_SIZE;
import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.NODE_ID_SIZE;
import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.RANK_SIZE;

import com.google.protobuf.ByteString;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * The header information of each edge stored in vector data of EDGE type
 * This EdgeHeader's struct is:
 * |edgeTypeId|graphId| rank|dstId|srcId|
 * |  int32   | int32 |int64|int64|int64|
 */
public class EdgeHeader {
    private final ByteOrder byteOrder;

    // int32, edge type id
    private       int       edgeTypeId;
    // int32, graph id
    private       int       graphId;

    // int64, rank
    private long rank;

    // int64, dst node id
    private long dstId;
    // int64, src node id
    private long srcId;

    public EdgeHeader(ByteString byteString, ByteOrder order) {
        this.byteOrder = order;
        ByteBuffer buffer = ByteBuffer
                .wrap(byteString.toByteArray())
                .order(byteOrder);

        this.srcId = buffer.getLong();

        this.dstId = bytesToLong(buffer.array(), 8, NODE_ID_SIZE);
        this.rank = bytesToLong(buffer.array(), 16, RANK_SIZE);

        this.graphId = bytesToInt(buffer.array(), 24, GRAPH_ID_SIZE);

        this.edgeTypeId = bytesToInt(buffer.array(), 28, EDGE_TYPE_ID_SIZE);

    }

    private long bytesToLong(byte[] bytes, int offset, int size) {
        ByteBuffer buffer = ByteBuffer.allocate(size).order(byteOrder);
        buffer.put(bytes, offset, size);
        buffer.flip();
        return buffer.getLong();
    }

    private int bytesToInt(byte[] bytes, int offset, int size) {
        ByteBuffer buffer = ByteBuffer.allocate(size).order(byteOrder);
        buffer.put(bytes, offset, size);
        buffer.flip();
        return buffer.getInt();
    }

    public int getEdgeTypeId() {
        return edgeTypeId;
    }

    public int getGraphId() {
        return graphId;
    }

    public long getRank() {
        return rank;
    }

    public long getDstId() {
        return dstId;
    }

    public long getSrcId() {
        return srcId;
    }
}

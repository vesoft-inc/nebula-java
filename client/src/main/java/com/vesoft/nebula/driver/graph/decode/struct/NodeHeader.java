/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.decode.struct;

import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.GRAPH_ID_SIZE;
import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.NODE_ID_SIZE;

import com.google.protobuf.ByteString;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * NodeHeader includes nodeId, graphId， NodeHeader is 16 bytes.
 * The nodeTypeId is not stored in the header, but exists in nodeId.
 * NodeId = NodeTypeId + BucketId + NodeSeqId, the first 16 bytes of nodeId is node type id.
 */
public class NodeHeader {
    private final ByteOrder byteOrder;

    // int64, node id
    private long nodeId;
    // int32, graph id
    private int  graphId;
    // int16, node type id
    private int  nodeTypeId;

    public NodeHeader(ByteString byteString, ByteOrder order) {
        this.byteOrder = order;
        ByteBuffer buffer = ByteBuffer
                .wrap(byteString.toByteArray())
                .order(byteOrder);
        this.nodeId = buffer.getLong();
        this.nodeTypeId = (int) (nodeId >> 48);
        this.graphId = bytesToInt(buffer.array(), NODE_ID_SIZE, GRAPH_ID_SIZE);
    }

    private int bytesToInt(byte[] bytes, int offset, int size) {
        ByteBuffer buffer = ByteBuffer.allocate(size).order(byteOrder);
        buffer.put(bytes, offset, size);
        buffer.flip();
        return buffer.getInt();
    }

    public long getNodeId() {
        return nodeId;
    }

    public int getGraphId() {
        return graphId;
    }

    public int getNodeTypeId() {
        return nodeTypeId;
    }
}

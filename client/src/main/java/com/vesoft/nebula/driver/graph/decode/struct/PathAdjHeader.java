/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.decode.struct;

/**
 * The PathAdjHeader is stored in the path adj vector, the adj vector is Long type,
 * each value is a int64 number, includes isEnd, isEdge, vecIdx, offset.
 *
 * <p>Considering a path: node1 -> edge1 -> node2 -> edge2 -> edge3 -> edge4 -> node3
 *
 * <p>For each element (node1, node2, ..., edge1, ...), we use an int64 to encode its
 * neighbor's information. It helps us to construct a path from a set of nodes/edges.
 *
 * <p>The structure of this int64 is:
 * | isEnd | isEdge | padding | vecIdx  | offset  |
 * | 1 bit | 1 bit  | 14 bits | 16 bits | 32 bits |
 * `isEnd` : whether this element is the end of path, true for node3
 * `isEdge`: whether the next element is edge, true for node1, node2, edge2, edge3
 * `vecIdx`: which Vector the next element belongs to
 * `offset`: which offset the next element is placed at (in its Vector)
 */
public class PathAdjHeader {
    // whether this element is the end of path. 1 bit
    private boolean isEnd;

    // whether the next element is edge. 1 bit
    private boolean isNextEdge;

    // padding 14 bits between isEdge and vecIdx

    // which PathVectorPair the next element belongs to. 16 bits. NOTE:note the vector index.
    private int vecIdxOfNextEle;

    // which offset the next element is placed at (in its vector). 32 bits
    private int offsetOfNextEle;


    public PathAdjHeader(long value) {
        this.isEnd = ((value >> 63) & 1) == 1;
        this.isNextEdge = ((value >> 62) & 1) == 1;
        this.vecIdxOfNextEle = (int) ((value >> 32) & 0xFFFF);
        this.offsetOfNextEle = (int) (value & 0xFFFFFFFFL);
    }

    public boolean isEnd() {
        return isEnd;
    }

    public boolean isNextEdge() {
        return isNextEdge;
    }

    public int getVecIdxOfNextEle() {
        return vecIdxOfNextEle;
    }

    public int getOffsetOfNextEle() {
        return offsetOfNextEle;
    }
}

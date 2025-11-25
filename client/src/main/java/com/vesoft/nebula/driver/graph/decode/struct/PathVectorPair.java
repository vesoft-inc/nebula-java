/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.decode.struct;

import com.vesoft.nebula.driver.graph.decode.VectorWrapper;

/**
 * the PathVectorPair represents a Node or Edge element and its neighbor information in the path
 */
public class PathVectorPair {
    // Node or Edge Vector
    private VectorWrapper nodeVector;
    // the adj Long Vector
    private VectorWrapper adjVector;

    public PathVectorPair(VectorWrapper nodeVector, VectorWrapper adjVector) {
        this.nodeVector = nodeVector;
        this.adjVector = adjVector;
    }

    public VectorWrapper getVector() {
        return nodeVector;
    }

    public VectorWrapper getAdjVector() {
        return adjVector;
    }
}

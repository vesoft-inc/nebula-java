/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.decode.datatype;

public enum VectorType {
    INVALID_VECTOR(0),
    FLAT_VECTOR(1),
    CONST_VECTOR(2),
    PARALLEL_VECTOR(3);

    private int type;

    VectorType(int type) {
        this.type = type;
    }

    public static VectorType getVectorType(int type) {
        switch (type) {
            case 0:
                return INVALID_VECTOR;
            case 1:
                return CONST_VECTOR;
            case 2:
                return FLAT_VECTOR;
            case 3:
                return PARALLEL_VECTOR;
            default:
                throw new RuntimeException("do not support vector type: " + type);
        }
    }

}

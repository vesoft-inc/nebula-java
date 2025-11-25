/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.data;

import com.vesoft.nebula.driver.graph.decode.datatype.EmbeddingVectorType;
import java.util.List;

public class EmbeddingVector {

    private int         dim;
    private List<Float> values;

    public EmbeddingVector(int dim, List<Float> values) {
        this.dim = dim;
        this.values = values;
    }

    public int getDim() {
        return dim;
    }

    public List<Float> getValues() {
        return values;
    }

    public int size() {
        return values.size();
    }

    @Override
    public String toString() {
        return values.toString();
    }
}

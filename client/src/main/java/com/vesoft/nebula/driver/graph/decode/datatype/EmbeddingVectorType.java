/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.decode.datatype;

import com.vesoft.nebula.driver.graph.decode.ColumnType;

public class EmbeddingVectorType extends DataType {
    private final  int      dim;
    private final  DataType valueType;

    public EmbeddingVectorType(int dim, DataType valueType) {
        super(ColumnType.COLUMN_TYPE_EMBEDDINGVECTOR);
        this.dim = dim;
        this.valueType = valueType;
    }

    public int getDim() {
        return dim;
    }

    public DataType getValueType() {
        return valueType;
    }
}

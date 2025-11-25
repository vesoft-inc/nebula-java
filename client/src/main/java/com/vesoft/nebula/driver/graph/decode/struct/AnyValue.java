/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.decode.struct;

import com.vesoft.nebula.driver.graph.decode.ColumnType;

public class AnyValue {
    // the value for any type
    private Object     value;

    // the actual data type for any type
    private ColumnType type;


    public AnyValue(Object value, ColumnType type) {
        this.value = value;
        this.type = type;
    }

    public ColumnType getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }
}

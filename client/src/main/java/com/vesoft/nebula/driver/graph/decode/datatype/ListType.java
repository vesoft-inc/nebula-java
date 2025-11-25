/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.decode.datatype;

import com.vesoft.nebula.driver.graph.decode.ColumnType;

public class ListType extends DataType {
    private final DataType valueType;

    public ListType(DataType valueType) {
        super(ColumnType.COLUMN_TYPE_LIST);
        this.valueType = valueType;
    }

    public DataType getValueType() {
        return valueType;
    }
}

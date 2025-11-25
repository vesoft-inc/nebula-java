/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.decode.datatype;

import com.vesoft.nebula.driver.graph.decode.ColumnType;

public class MapType extends DataType {

    private final DataType keyType;
    private final DataType valueType;

    public MapType(DataType keyType, DataType valueType) {
        super(ColumnType.COLUMN_TYPE_MAP);
        this.keyType = keyType;
        this.valueType = valueType;
    }

    public DataType getKeyType() {
        return keyType;
    }

    public DataType getValueType() {
        return valueType;
    }

}

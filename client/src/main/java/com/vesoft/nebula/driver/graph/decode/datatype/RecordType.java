/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.decode.datatype;

import static com.vesoft.nebula.driver.graph.decode.ColumnType.COLUMN_TYPE_RECORD;

import java.util.HashMap;
import java.util.Map;

public class RecordType extends DataType {
    private Map<String, DataType> fieldTypes = new HashMap<>();

    public RecordType(Map<String, DataType> fieldTypes) {
        super(COLUMN_TYPE_RECORD);
        this.fieldTypes = fieldTypes;
    }

    public Map<String, DataType> getFieldTypes() {
        return fieldTypes;
    }
}

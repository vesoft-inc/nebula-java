/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.decode;

import com.vesoft.nebula.driver.graph.data.ValueWrapper;
import java.util.ArrayList;
import java.util.List;

/**
 * The row data with multiple values in one row record
 */
public class Row {
    private List<ValueWrapper> values = new ArrayList<>();

    public Row() {
    }

    public Row(List<ValueWrapper> values) {
        if (values != null) {
            this.values = values;
        }
    }


    /**
     * append one value into row
     *
     * @param value one value of the row
     */
    public void addValue(ValueWrapper value) {
        values.add(value);
    }

    /**
     * get the values of this row
     *
     * @return list of {@link ValueWrapper}
     */
    public List<ValueWrapper> getValues() {
        return values;
    }
}

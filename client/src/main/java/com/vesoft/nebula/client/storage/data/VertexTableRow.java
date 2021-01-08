/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.storage.data;

import java.util.List;

public class VertexTableRow extends BaseTableRow {

    public VertexTableRow(List<Object> values) {
        super(values);
    }

    public VertexTableRow(List<Object> values, String decodeType) {
        super(values, decodeType);
    }

    /**
     * vertex id in vertexTableRow
     */
    public Object getVid() {
        if (values.size() < 1) {
            throw new IllegalArgumentException("no vertex id is returned");
        }
        return values.get(0);
    }


    @Override
    public String toString() {
        return "VertexTableView{"
                + "vid=" + getVid()
                + ", values=" + mkString(",")
                + '}';
    }

}

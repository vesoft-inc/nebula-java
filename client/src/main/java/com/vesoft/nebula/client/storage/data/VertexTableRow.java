/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.client.storage.data;

import com.vesoft.nebula.client.graph.data.ValueWrapper;
import java.util.List;

public class VertexTableRow extends BaseTableRow {

    public VertexTableRow(List<ValueWrapper> values) {
        super(values);
    }

    public VertexTableRow(List<ValueWrapper> values, String decodeType) {
        super(values, decodeType);
    }

    /** vertex id in vertexTableRow */
    public ValueWrapper getVid() {
        if (values.size() < 1) {
            throw new IllegalArgumentException("no vertex id is returned");
        }
        return values.get(0);
    }

    @Override
    public String toString() {
        return "VertexTableView{"
                + "vid="
                + getVid().toString()
                + ", values="
                + mkString(",")
                + '}';
    }
}

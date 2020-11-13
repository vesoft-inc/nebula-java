/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package org.apache.flink.connector.nebula.sink;

import com.esotericsoftware.minlog.Log;
import org.apache.flink.connector.nebula.statement.VertexExecutionOptions;
import org.apache.flink.connector.nebula.utils.NebulaConstant;
import org.apache.flink.connector.nebula.utils.PolicyEnum;
import org.apache.flink.types.Row;

import java.util.ArrayList;
import java.util.List;

public class NebulaRowVertexOutputFormatConverter implements NebulaOutputFormatConverter<Row> {

    private static final long serialVersionUID = -7728344698410737677L;

    private final int idIndex;

    public NebulaRowVertexOutputFormatConverter(VertexExecutionOptions executionOptions) {
        this.idIndex = executionOptions.getIdIndex();
    }


    @Override
    public String createValue(Row row, PolicyEnum policy) {
        if (row == null || row.getArity() == 0) {
            Log.error("empty row");
            return null;
        }
        Object id = row.getField(idIndex);
        if (id == null) {
            return null;
        }
        List<String> vertexProps = new ArrayList<>();
        for (int i = 0; i < row.getArity(); i++) {
            if (i != idIndex) {
                vertexProps.add(String.valueOf(row.getField(i)));
            }
        }
        if (policy == null) {
            return String.format(NebulaConstant.VERTEX_VALUE_TEMPLATE, id.toString(), String.join(",", vertexProps));
        } else {
            return String.format(NebulaConstant.VERTEX_VALUE_TEMPLATE_WITH_POLICY, policy, id.toString(), String.join(",", vertexProps));
        }

    }
}

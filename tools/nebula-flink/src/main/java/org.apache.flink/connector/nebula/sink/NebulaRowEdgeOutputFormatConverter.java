/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package org.apache.flink.connector.nebula.sink;

import com.esotericsoftware.minlog.Log;
import org.apache.flink.connector.nebula.statement.EdgeExecutionOptions;
import org.apache.flink.connector.nebula.utils.NebulaConstant;
import org.apache.flink.connector.nebula.utils.PolicyEnum;
import org.apache.flink.types.Row;

import java.util.ArrayList;
import java.util.List;

public class NebulaRowEdgeOutputFormatConverter implements NebulaOutputFormatConverter<Row> {

    private final int srcIdIndex;
    private final int dstIdIndex;
    private final int rankIndex;

    public NebulaRowEdgeOutputFormatConverter(EdgeExecutionOptions executionOptions) {
        this.srcIdIndex = executionOptions.getSrcIndex();
        this.dstIdIndex = executionOptions.getDstIndex();
        this.rankIndex = executionOptions.getRankIndex();
    }

    @Override
    public String createValue(Row row, PolicyEnum policy) {
        if (row == null || row.getArity() == 0) {
            Log.error("empty row");
            return null;
        }

        Object srcId = row.getField(srcIdIndex);
        Object dstId = row.getField(dstIdIndex);
        if (srcId == null || dstId == null) {
            return null;
        }
        List<String> edgeProps = new ArrayList<>();
        for (int i = 0; i < row.getArity(); i++) {
            if (i != srcIdIndex && i != dstIdIndex) {
                edgeProps.add(row.getField(i).toString());
            }
        }
        String srcFormatId = srcId.toString();
        String dstFormatId = dstId.toString();
        if (policy == null) {
            srcFormatId = String.format(NebulaConstant.ENDPOINT_TEMPLATE, policy.policy(), srcId.toString());
            dstFormatId = String.format(NebulaConstant.ENDPOINT_TEMPLATE, policy.policy(), dstId.toString());
        }
        if (rankIndex >= 0) {
            assert row.getField(rankIndex) != null;
            String rank = row.getField(rankIndex).toString();
            return String.format(NebulaConstant.EDGE_VALUE_TEMPLATE, srcFormatId, dstFormatId, rank, edgeProps);
        } else {
            return String.format(NebulaConstant.EDGE_VALUE_WITHOUT_RANKING_TEMPLATE, srcFormatId, dstFormatId, edgeProps);
        }
    }
}

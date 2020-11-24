/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package org.apache.flink.connector.nebula.statement;

import org.apache.flink.connector.nebula.utils.DataTypeEnum;
import org.apache.flink.connector.nebula.utils.PolicyEnum;

import java.util.List;

import static org.apache.flink.connector.nebula.utils.NebulaConstant.*;
import static org.apache.flink.connector.nebula.utils.NebulaConstant.DEFAULT_ROW_INFO_INDEX;

public class VertexExecutionOptions extends ExecutionOptions {

    /**
     * nebula vertex tag
     */
    private String tag;

    /**
     * id index for nebula vertex sink
     */
    private int idIndex;

    public VertexExecutionOptions(String graphSpace, String queryStatement,
                                  String executeStatement, List<String> returnFields,
                                  boolean allCols, int limit, long startTime, long endTime, long batch,
                                  PolicyEnum policy, String tag, int idIndex) {
        super(graphSpace, queryStatement, executeStatement, returnFields, allCols, limit, startTime, endTime, batch, policy);
        this.tag = tag;
        this.idIndex = idIndex;
    }

    public String getTag() {
        return tag;
    }

    public int getIdIndex() {
        return idIndex;
    }

    @Override
    public String getLabel() {
        return tag;
    }

    @Override
    public DataTypeEnum getDataType() {
        return DataTypeEnum.VERTEX;
    }

    public static class ExecutionOptionBuilder {
        private String graphSpace;
        private String queryStatement;
        private String executeStatement;
        private String tag;
        private List<String> fields;
        private boolean allCols = false;
        private int limit = DEFAULT_SCAN_LIMIT;
        private long startTime = 0;
        private long endTime = Long.MAX_VALUE;
        private int batch = DEFAULT_WRITE_BATCH;
        private PolicyEnum policy = null;
        private int idIndex = DEFAULT_ROW_INFO_INDEX;

        public ExecutionOptionBuilder setGraphSpace(String graphSpace) {
            this.graphSpace = graphSpace;
            return this;
        }

        public ExecutionOptionBuilder setQueryStatement(String queryStatement) {
            this.queryStatement = queryStatement;
            return this;
        }

        public ExecutionOptionBuilder setExecuteStatement(String executeStatement) {
            this.executeStatement = executeStatement;
            return this;
        }

        public ExecutionOptionBuilder setTag(String tag) {
            this.tag = tag;
            return this;
        }


        public ExecutionOptionBuilder setFields(List<String> fields) {
            this.fields = fields;
            return this;
        }

        public ExecutionOptionBuilder setAllCols(boolean allCols) {
            this.allCols = allCols;
            return this;
        }

        public ExecutionOptionBuilder setLimit(int limit) {
            this.limit = limit;
            return this;
        }

        public ExecutionOptionBuilder setStartTime(long startTime) {
            this.startTime = startTime;
            return this;
        }

        public ExecutionOptionBuilder setEndTime(long endTime) {
            this.endTime = endTime;
            return this;
        }

        public ExecutionOptionBuilder setBatch(int batch) {
            this.batch = batch;
            return this;
        }

        public ExecutionOptionBuilder setPolicy(String policy) {
            this.policy = PolicyEnum.valueOf(policy);
            return this;
        }

        public ExecutionOptionBuilder setIdIndex(int idIndex) {
            this.idIndex = idIndex;
            return this;
        }

        public ExecutionOptions builder() {
            return new VertexExecutionOptions(graphSpace, queryStatement, executeStatement, fields,
                    allCols, limit, startTime, endTime, batch, policy, tag, idIndex);
        }
    }
}

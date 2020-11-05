package org.apache.flink.connector.nebula.statement;

import org.apache.flink.connector.nebula.utils.DataTypeEnum;
import org.apache.flink.connector.nebula.utils.PolicyEnum;

import java.io.Serializable;
import java.util.List;

import static org.apache.flink.connector.nebula.utils.NebulaConstant.*;

public class ExecutionOptions implements Serializable {
    private static final long serialVersionUID = 6958907525999542402L;

    /** nebula graph space */
    private String graphSpace;

    /** data type: VERTEX or EDGE, see {@link DataTypeEnum} */
    private DataTypeEnum dataType;

    /** query statement with return data */
    private String queryStatement;

    /** execute statement without return data */
    private String executeStatement;

    /** nebula vertex tag or edge type */
    private String label;

    /** fields of one label */
    private List<String> fields;

    /** if read all cols */
    private boolean allCols;

    /** data amount one scan for read */
    private int limit;

    /** parameter for scan operator */
    private long startTime;

    /** parameter for scan operator */
    private long endTime;

    /** data amount one batch for insert */
    private long batch;

    /** policy for vertexId or edge src、 dst, see {@link PolicyEnum} */
    private PolicyEnum policy;

    /** id index for nebula vertex sink */
    private int idIndex;

    /** src index for nebula edge sink */
    private int srcIndex;

    /** dst index for nebula edge sink */
    private int dstIndex;

    /** rank index for nebula edge sink */
    private int rankIndex;


    private ExecutionOptions(String graphSpace, DataTypeEnum dataType, String queryStatement,
                             String executeStatement, String label, List<String> returnFields,
                             boolean allCols, int limit, long startTime, long endTime, long batch,
                             PolicyEnum policy, int idIndex, int srcIndex, int dstIndex, int rankIndex) {
        this.graphSpace = graphSpace;
        this.dataType = dataType;
        this.queryStatement = queryStatement;
        this.executeStatement = executeStatement;
        this.label = label;
        this.fields = returnFields;
        this.allCols = allCols;
        this.limit = limit;
        this.startTime = startTime;
        this.endTime = endTime;
        this.batch = batch;
        this.policy = policy;
        this.idIndex = idIndex;
        this.srcIndex = srcIndex;
        this.dstIndex = dstIndex;
        this.rankIndex = rankIndex;
    }

    public String getGraphSpace() {
        return graphSpace;
    }

    public DataTypeEnum getDataType() {
        return dataType;
    }

    public String getQueryStatement() {
        return queryStatement;
    }

    public String getExecuteStatement() {
        return executeStatement;
    }

    public String getLabel() {
        return label;
    }

    public List<String> getFields() {
        return fields;
    }

    public boolean isAllCols() {
        return allCols;
    }

    public int getLimit() {
        return limit;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public long getBatch() {
        return batch;
    }

    public PolicyEnum getPolicy() {
        return policy;
    }

    public int getIdIndex() {
        return idIndex;
    }

    public int getSrcIndex() {
        return srcIndex;
    }

    public int getDstIndex() {
        return dstIndex;
    }

    public int getRankIndex() {
        return rankIndex;
    }

    public static class ExecutionOptionBuilder {
        private String graphSpace;
        private DataTypeEnum dataType;
        private String queryStatement;
        private String executeStatement;
        private String label;
        private List<String> fields;
        private boolean allCols = false;
        private int limit = DEFAULT_SCAN_LIMIT;
        private long startTime = 0;
        private long endTime = Long.MAX_VALUE;
        private int batch = DEFAULT_WRITE_BATCH;
        private PolicyEnum policy = null;
        private int idIndex = DEFAULT_ROW_INFO_INDEX;
        private int srcIndex = DEFAULT_ROW_INFO_INDEX;
        private int dstIndex = DEFAULT_ROW_INFO_INDEX;
        private int rankIndex = DEFAULT_ROW_INFO_INDEX;

        public ExecutionOptionBuilder setGraphSpace(String graphSpace) {
            this.graphSpace = graphSpace;
            return this;
        }

        public ExecutionOptionBuilder setDataType(String dataType) {
            this.dataType = DataTypeEnum.valueOf(dataType);
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

        public ExecutionOptionBuilder setLabel(String label) {
            this.label = label;
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

        public ExecutionOptionBuilder setSrcIndex(int srcIndex) {
            this.srcIndex = srcIndex;
            return this;
        }

        public ExecutionOptionBuilder setDstIndex(int dstIndex) {
            this.dstIndex = dstIndex;
            return this;
        }

        public ExecutionOptionBuilder setRankIndex(int rankIndex) {
            this.rankIndex = rankIndex;
            return this;
        }

        public ExecutionOptions builder() {
            return new ExecutionOptions(graphSpace, dataType, queryStatement, executeStatement, label, fields,
                    allCols, limit, startTime, endTime, batch, policy, idIndex, srcIndex, dstIndex, rankIndex);
        }
    }
}

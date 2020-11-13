/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package org.apache.flink.connector.nebula.statement;

import org.apache.flink.connector.nebula.utils.DataTypeEnum;
import org.apache.flink.connector.nebula.utils.PolicyEnum;

import java.io.Serializable;
import java.util.List;

import static org.apache.flink.connector.nebula.utils.NebulaConstant.*;

public abstract class ExecutionOptions implements Serializable {
    private static final long serialVersionUID = 6958907525999542402L;

    /**
     * nebula graph space
     */
    private String graphSpace;

    /**
     * query statement with return data
     */
    private String queryStatement;

    /**
     * execute statement without return data
     */
    private String executeStatement;

    /**
     * fields of one label
     */
    private List<String> fields;

    /**
     * if read all cols
     */
    private boolean allCols;

    /**
     * data amount one scan for read
     */
    private int limit;

    /**
     * parameter for scan operator
     */
    private long startTime;

    /**
     * parameter for scan operator
     */
    private long endTime;

    /**
     * data amount one batch for insert
     */
    private long batch;

    /**
     * policy for vertexId or edge src、 dst, see {@link PolicyEnum}
     */
    private PolicyEnum policy;


    protected ExecutionOptions(String graphSpace, String queryStatement,
                               String executeStatement, List<String> returnFields,
                               boolean allCols, int limit, long startTime, long endTime, long batch,
                               PolicyEnum policy) {
        this.graphSpace = graphSpace;
        this.queryStatement = queryStatement;
        this.executeStatement = executeStatement;
        this.fields = returnFields;
        this.allCols = allCols;
        this.limit = limit;
        this.startTime = startTime;
        this.endTime = endTime;
        this.batch = batch;
        this.policy = policy;
    }

    public String getGraphSpace() {
        return graphSpace;
    }

    public String getQueryStatement() {
        return queryStatement;
    }

    public String getExecuteStatement() {
        return executeStatement;
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

    public abstract String getLabel();

    public abstract DataTypeEnum getDataType();

    @Override
    public String toString() {
        return "ExecutionOptions{" +
                "graphSpace='" + graphSpace + '\'' +
                ", queryStatement='" + queryStatement + '\'' +
                ", executeStatement='" + executeStatement + '\'' +
                ", fields=" + fields +
                ", allCols=" + allCols +
                ", limit=" + limit +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", batch=" + batch +
                ", policy=" + policy +
                '}';
    }
}

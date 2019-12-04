/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.data;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Result<ReqT> {

    public enum RowType {
        UNKNOWN,
        VERTEX,
        EDGE,
    }

    public static class RowDesc {
        private RowType type;
        private String name;

        public RowDesc(RowType type, String name) {
            this.type = type;
            this.name = name;
        }

        public RowType getType() {
            return type;
        }

        public String getName() {
            return name;
        }
    }

    private Map<RowDesc, List<Row>> rows;
    private ReqT nextRequest = null;
    private Iterator<Integer> partIt;

    public Result(Map<RowDesc, List<Row>> rows) {
        this.rows = rows;
    }

    public Result(Map<RowDesc, List<Row>> rows, Iterator<Integer> partIt) {
        this.rows = rows;
        this.partIt = partIt;
    }

    public Map<RowDesc, List<Row>> getRows() {
        return rows;
    }

    public ReqT getNextRequest() {
        return nextRequest;
    }

    public void setNextRequest(ReqT nextRequest) {
        this.nextRequest = nextRequest;
    }

    public Iterator<Integer> getPartIt() {
        return partIt;
    }
}

/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.data;

import java.util.List;
import java.util.Map;

public class Result<ReqT> {

    public enum RowType {
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
    private int size = 0;

    public Result(Map<RowDesc, List<Row>> rows) {
        this.rows = rows;
        for (Map.Entry<RowDesc, List<Row>> entry : rows.entrySet()) {
            size += entry.getValue().size();
        }
    }

    public Map<RowDesc, List<Row>> getRows() {
        return rows;
    }

    public List<Row> getRows(RowDesc desc) {
        return rows.get(desc);
    }

    public int getSize() {
        return size;
    }
}

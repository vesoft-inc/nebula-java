/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.data;

import java.util.List;
import java.util.Map;

public class Result<ReqT> {
    private Map<String, List<Row>> rows;
    private int size = 0;

    public Result(Map<String, List<Row>> rows) {
        this.rows = rows;
        for (Map.Entry<String, List<Row>> entry : this.rows.entrySet()) {
            size += entry.getValue().size();
        }
    }

    public Map<String, List<Row>> getRows() {
        return rows;
    }

    public List<Row> getRows(String name) {
        return rows.get(name);
    }

    public int getSize() {
        return size;
    }
}

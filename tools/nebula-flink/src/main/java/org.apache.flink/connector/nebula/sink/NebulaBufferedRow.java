/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package org.apache.flink.connector.nebula.sink;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * buffer for batch write
 */
public class NebulaBufferedRow implements Serializable {

    private static final long serialVersionUID = -1364277720478588644L;

    private final List<String> rows = new ArrayList<>();

    public void putRow(String row) {
        rows.add(row);
    }


    public List<String> getRows() {
        return rows;
    }

    public void clean() {
        rows.clear();
    }

    public int bufferSize() {
        return rows.size();
    }

}

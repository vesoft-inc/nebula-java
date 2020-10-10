/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.reader;

import java.util.ArrayList;
import java.util.List;

import org.apache.spark.Partition;

public class NebulaPartition implements Partition {

    private int index;

    public NebulaPartition(int index) {
        this.index = index;
    }

    @Override
    public int index() {
        return index;
    }

    /**
     * allocate scanPart to partition
     *
     * @param totalPart nebula data part num
     * @return scan data part list
     */
    public List<Integer> getScanParts(int totalPart, int totalPartition) {
        List<Integer> scanParts = new ArrayList<>();
        int currentPart = index + 1;
        while (currentPart <= totalPart) {
            scanParts.add(currentPart);
            currentPart += totalPartition;
        }
        return scanParts;
    }
}

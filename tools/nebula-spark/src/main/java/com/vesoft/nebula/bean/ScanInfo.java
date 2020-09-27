/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.bean;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.*;

public class ScanInfo implements Serializable {

    private final String nameSpace;

    private final String type;

    private final String label;

    private final String returnColString;

    private boolean allCols = false;

    private final int partitionNumber;

    /**
     * @param nameSpace       nameSpace
     * @param type        scan element type
     * @param label           vertex or edge label
     * @param partitionNumber partition number
     * @param returnColString scan col string example: name,age
     */
    public ScanInfo(String nameSpace, String type, String label, String returnColString, int partitionNumber) {
        this.nameSpace = nameSpace;
        this.type = type;
        this.label = label;
        this.returnColString = returnColString;
        this.partitionNumber = partitionNumber;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public String getType() {
        return type;
    }

    public String getLabel() {
        return label;
    }

    public int getPartitionNumber() {
        return partitionNumber;
    }

    public boolean getAllCols() {
        return allCols;
    }

    public Map<String, List<String>> getReturnColMap() {
        Map<String, List<String>> result = new HashMap<>(1);
        if(StringUtils.isEmpty(returnColString)){
            allCols = true;
            result.put(label, new ArrayList<>());
        } else{
            List<String> properties = Arrays.asList(returnColString.split(","));
            result.put(label, properties);
        }
        return result;
    }
}

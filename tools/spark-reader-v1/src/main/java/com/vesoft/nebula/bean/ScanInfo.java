/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.bean;

import com.google.common.base.Preconditions;
import com.vesoft.nebula.common.Checkable;
import com.vesoft.nebula.common.Type;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class ScanInfo implements Checkable, Serializable {

    private String scanType;

    private String returnColString;

    private int partitionNumber;

    private static final String RETURN_COL_REGEX = "(\\w+=(\\w+)(,\\w+)*)(;\\w+=(\\w+)(,\\w+)*)*";

    /**
     * @param scanType    scan element type
     * @param returnColString  return col string example: labela=name,age;labelb=name
     */
    public ScanInfo(String scanType, String returnColString, int partitionNumber) {
        this.scanType = scanType;
        this.returnColString = returnColString;
        this.partitionNumber = partitionNumber;
    }

    @Override
    public void check() throws IllegalArgumentException {
        boolean isLegalType = Type.VERTEX.getType().equalsIgnoreCase(scanType)
                || Type.EDGE.getType().equalsIgnoreCase(scanType);
        Preconditions.checkArgument(isLegalType,
                "scan type ‘%s’ is illegal, it should be '%s' or '%s'",
                scanType, Type.VERTEX.getType(), Type.EDGE.getType());

        boolean isReturnColLegal = Pattern.matches(RETURN_COL_REGEX, returnColString);
        Preconditions.checkArgument(isReturnColLegal,
                "return col string '%s' is illegal, the pattern should like a=b,c;d=e",
                returnColString);
    }

    public String getScanType() {
        return scanType;
    }

    public int getPartitionNumber() {
        return partitionNumber;
    }

    public Map<String, List<String>> getReturnColMap() {
        check();

        Map<String, List<String>> result = new HashMap<>();
        String[] returnColSplits = returnColString.split(";");
        for (String returnColSplit : returnColSplits) {
            String[] labelPropertyMap = returnColSplit.split("=");
            String label = labelPropertyMap[0];
            List<String> properties = Arrays.asList(labelPropertyMap[1].split(","));
            result.put(label, properties);
        }
        return result;
    }
}

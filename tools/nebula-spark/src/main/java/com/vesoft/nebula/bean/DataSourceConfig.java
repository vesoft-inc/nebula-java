/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.bean;

import com.google.common.net.HostAndPort;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

public class DataSourceConfig implements Serializable {

    private final String nameSpace;

    private final String type;

    private final String label;

    private final String returnColString;

    private boolean allCols = false;

    private final int partitionNumber;

    private final String hostAndPorts;

    /**
     * @param nameSpace       nameSpace
     * @param type            scan element type
     * @param label           vertex or edge label
     * @param partitionNumber partition number
     * @param returnColString scan col string example: name,age
     * @param hostAndPorts    host and port
     */
    public DataSourceConfig(String nameSpace, String type, String label, String returnColString, int partitionNumber, String hostAndPorts) {
        this.nameSpace = nameSpace;
        this.type = type;
        this.label = label;
        this.returnColString = returnColString;
        this.partitionNumber = partitionNumber;
        this.hostAndPorts = hostAndPorts;
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
        if (StringUtils.isBlank(returnColString)) {
            allCols = true;
            result.put(label, new ArrayList<>());
        } else {
            List<String> properties = Arrays.asList(returnColString.split(","));
            result.put(label, properties);
        }
        return result;
    }

    public List<HostAndPort> getHostAndPorts() {
        List<HostAndPort> hostAndPortList = new ArrayList<>();
        String[] hostAndPortArray = hostAndPorts.split(",");
        for (String hostAndPort : hostAndPortArray) {
            hostAndPortList.add(HostAndPort.fromString(hostAndPort));
        }
        return hostAndPortList;
    }
}

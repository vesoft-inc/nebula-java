/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.client.storage.scan;

import com.vesoft.nebula.client.graph.data.HostAddress;
import java.io.Serializable;
import java.util.Set;

public class PartScanQueue implements Serializable {

    private static final long serialVersionUID = 4817985176727025002L;

    private final Set<PartScanInfo> partScanInfoSet;

    public PartScanQueue(Set<PartScanInfo> partScanInfoSet) {
        this.partScanInfoSet = partScanInfoSet;
    }

    /**
     * get part according to leader
     *
     * @return null if no match part
     */
    public synchronized PartScanInfo getPart(HostAddress leader) {
        for (PartScanInfo partScanInfo : partScanInfoSet) {
            if (partScanInfo.getLeader().equals(leader)) {
                return partScanInfo;
            }
        }
        return null;
    }

    /** delete part from set */
    public synchronized void dropPart(PartScanInfo partScanInfo) {
        partScanInfoSet.remove(partScanInfo);
    }

    public int size() {
        return partScanInfoSet.size();
    }
}

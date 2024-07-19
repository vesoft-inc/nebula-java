/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.client.storage.scan;

import com.vesoft.nebula.ErrorCode;
import com.vesoft.nebula.HostAddr;
import com.vesoft.nebula.client.graph.data.HostAddress;
import com.vesoft.nebula.client.meta.MetaManager;
import com.vesoft.nebula.client.meta.exception.ExecuteFailedException;
import com.vesoft.nebula.client.storage.StorageConnPool;
import com.vesoft.nebula.storage.PartitionResult;
import com.vesoft.nebula.storage.ScanResponse;
import com.vesoft.nebula.util.NetUtil;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScanResultIterator implements Serializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScanResultIterator.class);

    protected boolean hasNext = true;

    protected final Map<Integer, byte[]> partCursor;

    protected final MetaManager       metaManager;
    protected final StorageConnPool   pool;
    protected final PartScanQueue     partScanQueue;
    protected final List<HostAddress> addresses;
    protected final String            spaceName;
    protected final String            labelName;
    protected final boolean           partSuccess;

    protected final String user;
    protected final String password;

    protected final Map<HostAddr, HostAddr> storageAddressMapping = new ConcurrentHashMap<>();

    protected ScanResultIterator(MetaManager metaManager,
                                 StorageConnPool pool,
                                 PartScanQueue partScanQueue,
                                 List<HostAddress> addresses,
                                 String spaceName,
                                 String labelName,
                                 boolean partSuccess,
                                 String user,
                                 String password,
                                 Map<String, String> storageAddrMapping) {
        this.metaManager = metaManager;
        this.pool = pool;
        this.partScanQueue = partScanQueue;
        this.addresses = addresses;
        this.spaceName = spaceName;
        this.labelName = labelName;
        this.partSuccess = partSuccess;
        this.partCursor = new HashMap<>(partScanQueue.size());
        this.user = user;
        this.password = password;
        if (storageAddrMapping != null && !storageAddrMapping.isEmpty()) {
            for (Map.Entry<String, String> et : storageAddrMapping.entrySet()) {
                storageAddressMapping.put(NetUtil.parseHostAddr(et.getKey()),
                                          NetUtil.parseHostAddr(et.getValue()));
            }
        }
    }


    /**
     * if iter has more vertex data
     *
     * @return true if the scan cursor is not at end.
     */
    public boolean hasNext() {
        return hasNext;
    }


    /**
     * fresh leader for part
     *
     * @param spaceName nebula graph space
     * @param part      part
     * @param leader    part new leader
     */
    protected void freshLeader(String spaceName, int part, HostAddr leader) {
        metaManager.updateLeader(spaceName, part, leader);
    }

    protected HostAddress getLeader(HostAddr leader) {
        return new HostAddress(leader.getHost(), leader.getPort());
    }

    protected void handleNullResponse(PartScanInfo partInfo, List<Exception> exceptions) {
        LOGGER.error("part scan failed, response is null");
        partScanQueue.dropPart(partInfo);
        exceptions.add(new Exception("null scan response"));
    }

    protected void handleNullResult(PartScanInfo partInfo, List<Exception> exceptions) {
        LOGGER.error("part scan failed, response result is null");
        partScanQueue.dropPart(partInfo);
        exceptions.add(new Exception("null scan response result"));
    }

    protected void throwExceptions(List<Exception> exceptions) throws ExecuteFailedException {
        StringBuilder errorMsg = new StringBuilder();
        for (int i = 0; i < exceptions.size(); i++) {
            if (i != 0) {
                errorMsg.append(",");
            }
            errorMsg.append(exceptions.get(i).getMessage());
        }
        throw new ExecuteFailedException("no parts succeed, error message: " + errorMsg.toString());
    }

    protected boolean isSuccessful(ScanResponse response) {
        return response != null && response.result.failed_parts.size() <= 0;
    }

    protected void handleSucceedResult(AtomicInteger existSuccess, ScanResponse response,
                                       PartScanInfo partInfo) {
        existSuccess.addAndGet(1);
        if (response.getCursors().get(partInfo.getPart()).next_cursor == null) {
            partScanQueue.dropPart(partInfo);
        } else {
            partInfo.setCursor(response.getCursors().get(partInfo.getPart()));
        }
    }

    protected void handleFailedResult(ScanResponse response, PartScanInfo partInfo,
                                      List<Exception> exceptions) {
        for (PartitionResult partResult : response.getResult().getFailed_parts()) {
            if (partResult.code == ErrorCode.E_LEADER_CHANGED) {
                freshLeader(spaceName, partInfo.getPart(), partResult.getLeader());
                partInfo.setLeader(getLeader(partResult.getLeader()));
            } else {
                ErrorCode code = partResult.getCode();
                LOGGER.error(String.format("part scan failed, error code=%s", code));
                partScanQueue.dropPart(partInfo);
                exceptions.add(new Exception(String.format("part scan, error code=%s", code)));
            }
        }
    }
}

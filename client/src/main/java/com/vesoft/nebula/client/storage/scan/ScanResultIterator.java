/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.storage.scan;

import com.facebook.thrift.TException;
import com.vesoft.nebula.HostAddr;
import com.vesoft.nebula.client.graph.data.HostAddress;
import com.vesoft.nebula.client.meta.MetaManager;
import com.vesoft.nebula.client.meta.exception.ExecuteFailedException;
import com.vesoft.nebula.client.storage.StorageConnPool;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScanResultIterator {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScanResultIterator.class);

    protected boolean hasNext = true;

    protected final Map<Integer, byte[]> partCursor;
    protected final ExecutorService threadPool;

    protected final MetaManager metaManager;
    protected final StorageConnPool pool;
    protected final PartScanQueue partScanQueue;
    protected final Set<HostAddress> addresses;
    protected final String spaceName;
    protected final String labelName;
    protected final boolean partSuccess;

    protected ScanResultIterator(MetaManager metaManager, StorageConnPool pool,
                                 PartScanQueue partScanQueue, Set<HostAddress> addresses,
                                 String spaceName, String labelName, boolean partSuccess) {
        this.metaManager = metaManager;
        this.pool = pool;
        this.partScanQueue = partScanQueue;
        this.addresses = addresses;
        this.spaceName = spaceName;
        this.labelName = labelName;
        this.partSuccess = partSuccess;

        this.threadPool = Executors.newFixedThreadPool(addresses.size());
        this.partCursor = new HashMap<>(partScanQueue.size());
    }


    /**
     * if iter has more vertex data
     *
     * @return boolean
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
        HostAddress newLeader = getLeader(leader);
        try {
            metaManager.freshLeader(spaceName, part, newLeader);
        } catch (TException e) {
            //todo exception thrown by metaManager is no need
            LOGGER.error("fresh leader error", e);
        }
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
}

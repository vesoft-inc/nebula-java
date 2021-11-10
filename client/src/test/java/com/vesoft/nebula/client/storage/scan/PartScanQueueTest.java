/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.client.storage.scan;

import com.vesoft.nebula.client.graph.data.HostAddress;
import java.util.HashSet;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

public class PartScanQueueTest {

    PartScanQueue queue;

    @Before
    public void before() {
        Set<PartScanInfo> partScanInfoSet = mockPartScanInfo();
        queue = new PartScanQueue(partScanInfoSet);
    }

    @Test
    public void testGetPart() {
        // test invalidate leader
        HostAddress wrongAddr = new HostAddress("1.1.1.1", 1);
        assert (queue.getPart(wrongAddr) == null);

        // test validate leader
        HostAddress rightAddr = new HostAddress("127.0.0.1", 1);
        assert (queue.getPart(rightAddr).getLeader().getPort() == 1);
        assert (queue.getPart(rightAddr).getCursor() == null);

        // test cursor
        HostAddress addr = new HostAddress("127.0.0.1", 3);
        assert (queue.getPart(addr).getLeader().getPort() == 3);
        assert (new String(queue.getPart(addr).getCursor()).equals("cursor"));
    }

    @Test
    public void testDropPart() {
        // drop not existed part
        queue.dropPart(new PartScanInfo(1, new HostAddress("127.0.0.1", 1)));
        assert (queue.size() == 5);

        // drop existed part
        HostAddress addr = new HostAddress("127.0.0.1", 3);
        PartScanInfo partScanInfo = queue.getPart(addr);
        queue.dropPart(partScanInfo);
        assert (queue.size() == 4);
    }


    private Set<PartScanInfo> mockPartScanInfo() {
        Set<PartScanInfo> partScanInfoSet = new HashSet<>();
        partScanInfoSet.add(new PartScanInfo(1, new HostAddress("127.0.0.1", 1)));
        partScanInfoSet.add(new PartScanInfo(2, new HostAddress("127.0.0.1", 2)));
        partScanInfoSet.add(new PartScanInfo(3, new HostAddress("127.0.0.1", 1)));
        partScanInfoSet.add(new PartScanInfo(4, new HostAddress("127.0.0.1", 2)));

        PartScanInfo partScanInfo = new PartScanInfo(5, new HostAddress("127.0.0.1", 3));
        partScanInfo.setCursor("cursor".getBytes());
        partScanInfoSet.add(partScanInfo);
        return partScanInfoSet;
    }
}

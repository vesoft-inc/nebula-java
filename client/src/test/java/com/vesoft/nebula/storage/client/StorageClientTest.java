/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.storage.client;

import com.vesoft.nebula.client.storage.StorageClient;
import com.vesoft.nebula.storage.ScanEdgeRequest;
import com.vesoft.nebula.storage.ScanEdgeResponse;
import java.util.Iterator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class StorageClientTest {
    // TODO: We'd better mock a StorageClientImpl
    StorageTestUtils testUtils;
    private static final Logger LOGGER = LoggerFactory.getLogger(StorageClientTest.class);

    @Before
    public void before() throws Exception {
        testUtils = new StorageTestUtils();
    }

    @After
    public void after() {
        testUtils.stop();
    }

    @Test
    public void singlePartScanEdgeTest() {
        StorageClient client = testUtils.getStorageClient();
        try {
            ScanEdgeRequest request = new ScanEdgeRequest();
            Iterator<ScanEdgeResponse> iterator = client.scanEdge("");

            while (iterator.hasNext()) {
                ScanEdgeResponse result = iterator.next();
            }
            // The mock server will return 16 times of `has_next`, so in total 17 requests are sent
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void multiPartScanEdgeTest() {
//        StorageService.Client client = testUtils.getClient();
//        Set<Integer> partIds = new HashSet<>();
//        partIds.add(1);
//        partIds.add(2);
//        partIds.add(3);
//        Iterator<Integer> partIt = partIds.iterator();
//        partIt.next();
//
//        try {
//            ScanEdgeConsumer consumer = new TestScanEdgeConsumer();
//            ScanEdgeRequest request = new ScanEdgeRequest();
//            ScanEdgeResponse response = client.scanEdge(request);
//            int sendRequestCount = 1;
//            Iterator<Result<ScanEdgeRequest>> iterator = consumer.handle(request, response, partIt);
//
//            while (iterator.hasNext()) {
//                Result<ScanEdgeRequest> result = iterator.next();
//                request = result.getNextRequest();
//                response = client.scanEdge(request);
//                iterator = consumer.handle(request, response, partIt);
//                sendRequestCount++;
//            }
//            // For a single part, the mock server will return 16 times of `has_next`, so in total
//            // we need to send 17 requests to scan over a part. And 17 * 3 part in total.
//            Assert.assertEquals(51, sendRequestCount);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @Test
    public void singlePartScanVertexTest() {
//        StorageService.Client client = testUtils.getClient();
//        try {
//            ScanVertexConsumer consumer = new TestScanVertexConsumer();
//            ScanVertexRequest request = new ScanVertexRequest();
//            ScanVertexResponse response = client.scanVertex(request);
//            int sendRequestCount = 1;
//            Iterator<Result<ScanVertexRequest>> iterator = consumer.handle(request, response);
//
//            while (iterator.hasNext()) {
//                Result<ScanVertexRequest> result = iterator.next();
//                request = result.getNextRequest();
//                Assert.assertEquals(sendRequestCount, request.cursor[0]);
//                response = client.scanVertex(request);
//                iterator = consumer.handle(request, response);
//                sendRequestCount++;
//            }
//            // The mock server will return 16 times of `has_next`, so in total 17 requests are sent
//            Assert.assertEquals(17, sendRequestCount);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @Test
    public void multiPartScanVertexTest() {
//        StorageService.Client client = testUtils.getClient();
//        Set<Integer> partIds = new HashSet<>();
//        partIds.add(1);
//        partIds.add(2);
//        partIds.add(3);
//        Iterator<Integer> partIt = partIds.iterator();
//        partIt.next();
//
//        try {
//            ScanVertexConsumer consumer = new TestScanVertexConsumer();
//            ScanVertexRequest request = new ScanVertexRequest();
//            ScanVertexResponse response = client.scanVertex(request);
//            int sendRequestCount = 1;
//            Iterator<Result<ScanVertexRequest>> iterator =
//                    consumer.handle(request, response, partIt);
//
//            while (iterator.hasNext()) {
//                Result<ScanVertexRequest> result = iterator.next();
//                request = result.getNextRequest();
//                response = client.scanVertex(request);
//                iterator = consumer.handle(request, response, partIt);
//                sendRequestCount++;
//            }
//            // For a single part, the mock server will return 16 times of `has_next`, so in total
//            // we need to send 17 requests to scan over a part. And 17 * 3 part in total.
//            Assert.assertEquals(51, sendRequestCount);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}

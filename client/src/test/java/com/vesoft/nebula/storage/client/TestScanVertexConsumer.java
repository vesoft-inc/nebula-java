///* Copyright (c) 2019 vesoft inc. All rights reserved.
// *
// * This source code is licensed under Apache 2.0 License,
// * attached with Common Clause Condition 1.0, found in the LICENSES directory.
// */
//
//package com.vesoft.nebula.storage.client;
//
//import com.vesoft.nebula.data.Result;
//import com.vesoft.nebula.data.Row;
//import com.vesoft.nebula.storage.ScanEdgeRequest;
//import com.vesoft.nebula.storage.ScanEdgeResponse;
//import com.vesoft.nebula.storage.ScanVertexRequest;
//import com.vesoft.nebula.storage.ScanVertexResponse;
//import com.vesoft.nebula.storage.client.handler.ScanVertexConsumer;
//
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//
//public class TestScanVertexConsumer extends ScanVertexConsumer {
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(TestScanVertexConsumer.class);
//
//    public TestScanVertexConsumer() {
//        super(null);
//    }
//
//    @Override
//    protected Result handleResponse(ScanVertexRequest curRequest, ScanVertexResponse response) {
//        Map<Result.RowDesc, List<Row>> rows = new HashMap<Result.RowDesc, List<Row>>();
//        Result result = new Result(rows);
//
//        if (hasNext(response)) {
//            curRequest.setCursor(response.next_cursor);
//            result.setNextRequest(curRequest);
//            hasNext = true;
//        } else {
//            hasNext = false;
//        }
//        return result;
//    }
//
//    @Override
//    protected Result handleResponse(ScanVertexRequest curRequest, ScanVertexResponse response,
//                                    Iterator<Integer> partIt) {
//        Map<Result.RowDesc, List<Row>> rows = new HashMap<Result.RowDesc, List<Row>>();
//        Result result = new Result(rows);
//
//        if (hasNext(response, partIt)) {
//            hasNext = true;
//            if (!response.has_next) {
//                ScanVertexRequest nextRequest = new ScanVertexRequest();
//                nextRequest.space_id = curRequest.space_id;
//                nextRequest.part_id = partIt.next();
//                nextRequest.row_limit = curRequest.row_limit;
//                nextRequest.start_time = curRequest.start_time;
//                nextRequest.end_time = curRequest.end_time;
//                result.setNextRequest(nextRequest);
//            } else {
//                curRequest.setCursor(response.next_cursor);
//                result.setNextRequest(curRequest);
//            }
//        } else {
//            hasNext = false;
//        }
//        return result;
//    }
//
//    private boolean hasNext(ScanVertexResponse response) {
//        return response.has_next;
//    }
//
//    private boolean hasNext(ScanVertexResponse response, Iterator<Integer> partIt) {
//        return response.has_next || partIt.hasNext();
//    }
//}

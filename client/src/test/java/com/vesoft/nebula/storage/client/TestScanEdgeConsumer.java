/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.storage.client;

import com.vesoft.nebula.data.Result;
import com.vesoft.nebula.data.Row;
import com.vesoft.nebula.storage.ScanEdgeRequest;
import com.vesoft.nebula.storage.ScanEdgeResponse;
import com.vesoft.nebula.storage.client.handler.ScanEdgeConsumer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestScanEdgeConsumer extends ScanEdgeConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestScanEdgeConsumer.class);

    public TestScanEdgeConsumer() {
        super(null);
    }

    @Override
    protected Result handleResponse(ScanEdgeRequest curRequest, ScanEdgeResponse response) {
        Map<Result.RowDesc, List<Row>> rows = new HashMap<Result.RowDesc, List<Row>>();
        Result result = new Result(rows);

        if (hasNext(response)) {
            curRequest.setCursor(response.next_cursor);
            result.setNextRequest(curRequest);
            hasNext = true;
        } else {
            hasNext = false;
        }
        return result;
    }

    @Override
    protected Result handleResponse(ScanEdgeRequest curRequest, ScanEdgeResponse response,
                                    Iterator<Integer> partIt) {
        Map<Result.RowDesc, List<Row>> rows = new HashMap<Result.RowDesc, List<Row>>();
        Result result = new Result(rows);

        if (hasNext(response, partIt)) {
            hasNext = true;
            if (!response.has_next) {
                ScanEdgeRequest nextRequest = new ScanEdgeRequest();
                nextRequest.space_id = curRequest.space_id;
                nextRequest.part_id = partIt.next();
                nextRequest.row_limit = curRequest.row_limit;
                nextRequest.start_time = curRequest.start_time;
                nextRequest.end_time = curRequest.end_time;
                result.setNextRequest(nextRequest);
            } else {
                curRequest.setCursor(response.next_cursor);
                result.setNextRequest(curRequest);
            }
        } else {
            hasNext = false;
        }
        return result;
    }

    private boolean hasNext(ScanEdgeResponse response) {
        return response.has_next;
    }

    private boolean hasNext(ScanEdgeResponse response, Iterator<Integer> partIt) {
        return response.has_next || partIt.hasNext();
    }
}

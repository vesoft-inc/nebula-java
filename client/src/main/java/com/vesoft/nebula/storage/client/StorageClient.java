/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.storage.client;

import com.google.common.base.Optional;
import com.vesoft.nebula.Client;
import com.vesoft.nebula.data.Result;
import com.vesoft.nebula.storage.ScanEdge;
import com.vesoft.nebula.storage.ScanEdgeRequest;
import com.vesoft.nebula.storage.ScanVertexRequest;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public interface StorageClient extends Client {

    public boolean put(int space, String key, String value);

    public boolean put(int space, Map<String, String> kvs);

    public Optional<String> get(int space, String key);

    public Optional<Map<String, String>> get(int space, List<String> keys);

    public boolean remove(int space, String key);

    public boolean remove(int space, List<String> keys);

    // public boolean removeRange(int space, String start, String end);

    public Iterator<Result<ScanEdgeRequest>> scanEdge(int space) throws Exception;

    public Iterator<Result<ScanEdgeRequest>> scanEdge(
            int space, int rowLimit, long startTime, long endTime) throws Exception;

    public Iterator<Result<ScanEdgeRequest>> scanEdge(int space, int part) throws Exception;

    public Iterator<Result<ScanEdgeRequest>> scanEdge(
            int space, int part, int rowLimit, long startTime, long endTime) throws Exception;

    public Iterator<Result<ScanEdgeRequest>> scanEdge(ScanEdgeRequest request) throws Exception;

    public Iterator<Result<ScanEdgeRequest>> scanEdge(
            ScanEdgeRequest request, Iterator<Integer> iterator) throws Exception;

    public Iterator<Result<ScanVertexRequest>> scanVertex(int space) throws Exception;

    public Iterator<Result<ScanVertexRequest>> scanVertex(
            int space, int rowLimit, long startTime, long endTime) throws Exception;

    public Iterator<Result<ScanVertexRequest>> scanVertex(int space, int part) throws Exception;

    public Iterator<Result<ScanVertexRequest>> scanVertex(
            int space, int part, int rowLimit, long startTime, long endTime) throws Exception;

    public Iterator<Result<ScanVertexRequest>> scanVertex(
            ScanVertexRequest request) throws Exception;

    public Iterator<Result<ScanVertexRequest>> scanVertex(
            ScanVertexRequest request, Iterator<Integer> iterator) throws Exception;

    public static final int DEFAULT_SCAN_ROW_LIMIT = 1000;
    public static final long DEFAULT_SCAN_START_TIME = 0;
    public static final long DEFAULT_SCAN_END_TIME = Long.MAX_VALUE;
}

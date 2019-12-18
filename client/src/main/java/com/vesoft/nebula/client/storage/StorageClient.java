/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.storage;

import com.google.common.base.Optional;
import com.vesoft.nebula.storage.ScanEdgeResponse;
import com.vesoft.nebula.storage.ScanVertexResponse;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public interface StorageClient extends AutoCloseable {
    public static final int DEFAULT_SCAN_ROW_LIMIT = 1000;
    public static final long DEFAULT_SCAN_START_TIME = 0;
    public static final long DEFAULT_SCAN_END_TIME = Long.MAX_VALUE;

    public boolean put(String space, String key, String value);

    public boolean put(String space, Map<String, String> kvs);

    public Optional<String> get(String space, String key);

    public Optional<Map<String, String>> get(String space, List<String> keys);

    public boolean remove(String space, String key);

    public boolean remove(String space, List<String> keys);

    public Iterator<ScanEdgeResponse> scanEdge(String space) throws IOException;

    public Iterator<ScanEdgeResponse> scanEdge(String space, int rowLimit,
                                               long startTime, long endTime) throws IOException;

    public Iterator<ScanEdgeResponse> scanEdge(String space, int part) throws IOException;

    public Iterator<ScanEdgeResponse> scanEdge(String space, int part, int rowLimit,
                                               long startTime, long endTime) throws IOException;

    public Iterator<ScanVertexResponse> scanVertex(String space) throws IOException;

    public Iterator<ScanVertexResponse> scanVertex(String space, int rowLimit,
                                                   long startTime, long endTime) throws IOException;

    public Iterator<ScanVertexResponse> scanVertex(String space, int part) throws IOException;

    public Iterator<ScanVertexResponse> scanVertex(String space, int part, int rowLimit,
                                                   long startTime, long endTime) throws IOException;

}

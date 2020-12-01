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
    boolean DEFAULT_RETURN_ALL_COLUMNS = false;
    int DEFAULT_SCAN_ROW_LIMIT = 1000;
    long DEFAULT_SCAN_START_TIME = 0;
    long DEFAULT_SCAN_END_TIME = Long.MAX_VALUE;

    boolean put(String space, String key, String value);

    boolean put(String space, Map<String, String> kvs);

    Optional<String> get(String space, String key);

    Optional<Map<String, String>> get(String space, List<String> keys);

    boolean remove(String space, String key);

    boolean remove(String space, List<String> keys);

    Iterator<ScanEdgeResponse> scanEdge(
            String space, Map<String, List<String>> returnCols) throws IOException;

    Iterator<ScanEdgeResponse> scanEdge(
            String space, Map<String, List<String>> returnCols, boolean allCols,
            int limit, long startTime, long endTime) throws IOException;

    Iterator<ScanEdgeResponse> scanEdge(
            String space, int part, Map<String, List<String>> returnCols) throws IOException;

    Iterator<ScanEdgeResponse> scanEdge(
            String space, int part, Map<String, List<String>> returnCols, boolean allCols,
            int limit, long startTime, long endTime) throws IOException;

    Iterator<ScanVertexResponse> scanVertex(
            String space, Map<String, List<String>> returnCols) throws IOException;

    Iterator<ScanVertexResponse> scanVertex(
            String space, Map<String, List<String>> returnCols, boolean allCols,
            int limit, long startTime, long endTime) throws IOException;

    Iterator<ScanVertexResponse> scanVertex(
            String space, int part, Map<String, List<String>> returnCols) throws IOException;

    Iterator<ScanVertexResponse> scanVertex(
            String space, int part, Map<String, List<String>> returnCols, boolean allCols,
            int limit, long startTime, long endTime) throws IOException;
}

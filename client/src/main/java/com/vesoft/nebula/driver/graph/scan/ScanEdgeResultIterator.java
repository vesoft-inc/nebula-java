/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.scan;

import com.vesoft.nebula.driver.graph.data.HostAddress;
import com.vesoft.nebula.driver.graph.data.ResultSet;
import com.vesoft.nebula.driver.graph.net.NebulaClient;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.CountDownLatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScanEdgeResultIterator extends ScanResultIterator {

    private static final Logger logger = LoggerFactory.getLogger(ScanEdgeResultIterator.class);

    private static final String SCAN_EDGE_TEMPLATE =
        "USE `%s` CALL cursor_edge_scan(\"%s\",\"%s\",%s,%d,\"%s\", %d) return *";

    public ScanEdgeResultIterator(String schema,
                                  String graphName,
                                  String label,
                                  List<String> propNames,
                                  List<Integer> parts,
                                  int batchSize,
                                  int parallel,
                                  List<HostAddress> servers,
                                  NebulaClient.Builder builder) {
        super(schema, graphName, label, propNames, parts, batchSize, parallel, servers, builder);
    }


    public ScanEdgeResult next() {
        if (!hasNext) {
            throw new NoSuchElementException("iterator has no more data");
        }
        final List<ResultSet> results =
            Collections.synchronizedList(new ArrayList<>(partCursor.size()));
        List<Exception> exceptions =
            Collections.synchronizedList(new ArrayList<>(partCursor.size()));
        CountDownLatch countDownLatch = new CountDownLatch(partCursor.size());
        for (Map.Entry<Integer, String> partCur : partCursor.entrySet()) {
            threadPool.submit(() -> {
                try {
                    ResultSet result = scan(SCAN_EDGE_TEMPLATE, partCur);
                    // collect results and update the cursor
                    if (result.isSucceeded()) {
                        String cursor = getCursor(result);
                        partCursor.put(partCur.getKey(), cursor);
                        results.add(result);
                    } else {
                        logger.error(String.format("Scan part %d of edge %s failed for %s, "
                                                       + "scan again in the next next()",
                                                   partCur.getKey(),
                                                   labelName,
                                                   result.getErrorMessage()));
                        exceptions.add(new Exception(String.format("part %d of %s scan error: %s",
                                                                   partCur.getKey(), labelName,
                                                                   result.getErrorMessage())));
                    }
                } catch (Exception e) {
                    logger.error(String.format("Scan edge error for %s", e.getMessage()), e);
                    exceptions.add(new Exception(String.format("part %d of %s scan failed: %s",
                                                               partCur.getKey(), labelName,
                                                               e.getMessage()), e));
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException interruptedException) {
            logger.error("scan interrupted:", interruptedException);
            throw new RuntimeException("scan interrupted", interruptedException);
        }

        // As long as one part fails, the current iteration is considered as failed.
        if (!exceptions.isEmpty()) {
            List<String> exceptionMsg = new ArrayList<>();
            for (Exception e : exceptions) {
                exceptionMsg.add(e.getMessage());
            }
            throw new RuntimeException("scan edge failed for current iterator: " + exceptionMsg);
        }

        // check if the iterator of part has more data
        hasNext = false;
        List<Integer> partKeyNeedToRemove = new ArrayList<>();
        for (Map.Entry<Integer, String> partCur : partCursor.entrySet()) {
            if (!"".equals(partCur.getValue())) {
                hasNext = true;
            } else {
                partKeyNeedToRemove.add(partCur.getKey());
            }
        }
        for (Integer part : partKeyNeedToRemove) {
            partCursor.remove(part);
        }
        if (!hasNext && !threadPool.isShutdown()) {
            threadPool.shutdown();
        }
        if (!hasNext) {
            close();
        }
        return new ScanEdgeResult(results, propNames);
    }

}

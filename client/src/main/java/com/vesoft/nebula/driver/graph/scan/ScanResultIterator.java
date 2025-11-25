/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.scan;

import com.vesoft.nebula.driver.graph.data.ExtraInfo;
import com.vesoft.nebula.driver.graph.data.HostAddress;
import com.vesoft.nebula.driver.graph.data.ResultSet;
import com.vesoft.nebula.driver.graph.net.NebulaClient;
import com.vesoft.nebula.driver.graph.utils.GqlUtil;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScanResultIterator implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(ScanResultIterator.class);

    String serversAddress;

    protected boolean hasNext = true;

    protected final Map<Integer, String> partCursor = new HashMap<>();
    protected final String               schema;

    protected final String                                   graphName;
    protected final String                                   labelName;
    protected       List<String>                             propNames;
    protected       int                                      batchSize;
    protected final ExecutorService                          threadPool;
    protected final ConcurrentHashMap<Integer, NebulaClient> partClient = new ConcurrentHashMap<>();

    protected ScanResultIterator(String schema,
                                 String graphName,
                                 String labelName,
                                 List<String> propNames,
                                 List<Integer> parts,
                                 int batchSize,
                                 int parallel,
                                 List<HostAddress> servers,
                                 NebulaClient.Builder builder) {
        this.schema = schema;
        this.graphName = graphName;
        this.labelName = labelName;
        this.propNames = propNames;
        this.batchSize = batchSize;
        this.threadPool = Executors.newFixedThreadPool(parallel);
        this.serversAddress = servers
            .stream()
            .map(HostAddress::toString)
            .collect(Collectors.joining(","));
        for (int part : parts) {
            partCursor.put(part, "");
            try {
                NebulaClient client = NebulaClient.builder(builder).build();
                if (schema != null && !schema.isEmpty()) {
                    ResultSet res = client.execute("SESSION SET SCHEMA `" + schema + "`");
                    if (!res.isSucceeded()) {
                        throw new RuntimeException(
                            "SESSION SET SCHEMA failed:" + res.getErrorMessage());
                    }
                }
                partClient.put(part, client);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * if iter has more data
     *
     * @return true if the scan cursor is not at end.
     */
    public boolean hasNext() {
        return hasNext;
    }

    protected String getPropertyListString() {
        StringBuilder properties         = new StringBuilder();
        String        propertyListPrefix = "list[";
        properties.append(propertyListPrefix);
        for (String column : propNames) {
            properties.append("\"");
            properties.append(GqlUtil.escape(column));
            properties.append("\"");
            properties.append(",");
        }
        if (properties.length() > propertyListPrefix.length()) {
            properties.deleteCharAt(properties.length() - 1);
        }
        String propertyListSuffix = "]";
        properties.append(propertyListSuffix);
        return properties.toString();
    }

    protected ResultSet scan(String scanTemplate, Map.Entry<Integer, String> partCur)
        throws Exception {
        // construct the scan producer
        String producer = String.format(scanTemplate,
                                        GqlUtil.escape(graphName),
                                        GqlUtil.escape(graphName),
                                        GqlUtil.escape(labelName),
                                        getPropertyListString(),
                                        partCur.getKey(),
                                        partCur.getValue(),
                                        batchSize);
        NebulaClient client = partClient.get(partCur.getKey());
        return client.execute(producer);
    }

    protected String getCursor(ResultSet resultSet) {
        ExtraInfo extraInfo = resultSet.getExtraInfo();
        if (extraInfo.getCursor() == null) {
            throw new RuntimeException("result does not contain cursor in extra info.");
        }
        return extraInfo.getCursor();
    }

    protected void close() {
        for (NebulaClient client : partClient.values()) {
            client.close();
        }
    }
}

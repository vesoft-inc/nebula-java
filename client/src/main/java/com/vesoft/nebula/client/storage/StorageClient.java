/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.storage;

import com.vesoft.nebula.client.graph.data.HostAddress;
import com.vesoft.nebula.client.meta.MetaManager;
import com.vesoft.nebula.client.storage.scan.PartScanInfo;
import com.vesoft.nebula.client.storage.scan.ScanEdgeResultIterator;
import com.vesoft.nebula.client.storage.scan.ScanVertexResultIterator;
import com.vesoft.nebula.storage.EdgeProp;
import com.vesoft.nebula.storage.ScanEdgeRequest;
import com.vesoft.nebula.storage.ScanVertexRequest;
import com.vesoft.nebula.storage.VertexProp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StorageClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(StorageClient.class);

    private final GraphStorageConnection connection;
    private StorageConnPool pool;
    private MetaManager metaManager;
    private final List<HostAddress> addresses;
    private int timeout = 10000; // ms

    public StorageClient(String ip, int port) {
        this(Arrays.asList(new HostAddress(ip, port)));
    }

    public StorageClient(List<HostAddress> addresses) {
        this.connection = new GraphStorageConnection();
        this.addresses = addresses;
    }

    public StorageClient(List<HostAddress> addresses, int timeout) {
        this.connection = new GraphStorageConnection();
        this.addresses = addresses;
        this.timeout = timeout;
    }

    public boolean connect() throws Exception {
        connection.open(addresses.get(0), timeout);
        StoragePoolConfig config = new StoragePoolConfig();
        pool = new StorageConnPool(config);
        metaManager = MetaManager.getMetaManager(addresses);
        return true;
    }


    /**
     * scan vertex of all parts with specific return cols
     */
    public ScanVertexResultIterator scanVertex(String spaceName, String tagName,
                                               List<String> returnCols) {

        return scanVertex(spaceName, tagName, returnCols,
                DEFAULT_LIMIT, DEFAULT_START_TIME, DEFAULT_END_TIME);
    }

    /**
     * scan vertex of specific part with specific return cols
     */
    public ScanVertexResultIterator scanVertex(String spaceName, int part, String tagName,
                                               List<String> returnCols) {
        return scanVertex(spaceName, part, tagName, returnCols,
                DEFAULT_LIMIT, DEFAULT_START_TIME, DEFAULT_END_TIME);
    }

    /**
     * scan vertex of all parts with no return cols
     */
    public ScanVertexResultIterator scanVertex(String spaceName, String tagName) {
        return scanVertex(spaceName, tagName, true, DEFAULT_LIMIT,
                DEFAULT_START_TIME, DEFAULT_END_TIME, false, true);
    }

    /**
     * scan vertex of specific part with no return cols
     */
    public ScanVertexResultIterator scanVertex(String spaceName, int part, String tagName) {
        return scanVertex(spaceName, part, tagName, true, DEFAULT_LIMIT,
                DEFAULT_START_TIME, DEFAULT_END_TIME, false, true);
    }

    /**
     * scan vertex of all part with specific return cols and limit
     */
    public ScanVertexResultIterator scanVertex(String spaceName,
                                               String tagName,
                                               List<String> returnCols,
                                               int limit) {
        return scanVertex(spaceName, tagName, returnCols,
                limit, DEFAULT_START_TIME, DEFAULT_END_TIME);
    }

    /**
     * scan vertex of all part with specific return cols and limit
     */
    public ScanVertexResultIterator scanVertex(String spaceName, String tagName, int limit) {
        return scanVertex(spaceName, tagName, true, limit, DEFAULT_START_TIME,
                DEFAULT_END_TIME, false, true);
    }

    /**
     * scan vertex of specific part
     *
     * @param spaceName  nebula graph space
     * @param part       part to scan
     * @param tagName    nebula tag name
     * @param returnCols return cols
     * @param limit      return data limit
     * @param startTime  start time
     * @param endTime    end time
     * @return
     */
    public ScanVertexResultIterator scanVertex(String spaceName,
                                               int part,
                                               String tagName,
                                               List<String> returnCols,
                                               int limit,
                                               long startTime,
                                               long endTime) {
        assert (returnCols != null);

        return scanVertex(spaceName, Arrays.asList(part), tagName, returnCols, false,
                limit, startTime, endTime, false, true);
    }


    /**
     * scan vertex of all parts
     *
     * @param spaceName  nebula graph space
     * @param returnCols return cols
     * @param limit      return data limit
     * @param startTime  start time
     * @param endTime    end time
     * @return
     */
    public ScanVertexResultIterator scanVertex(String spaceName,
                                               String tagName,
                                               List<String> returnCols,
                                               int limit,
                                               long startTime,
                                               long endTime) {
        assert (returnCols != null);

        List<Integer> parts = metaManager.getSpaceParts(spaceName);
        if (parts.isEmpty()) {
            throw new IllegalArgumentException("No valid part in space " + spaceName);
        }
        return scanVertex(spaceName, parts, tagName,
                returnCols, false, limit, startTime, endTime, false, true);
    }


    /**
     * scan vertex of specific part with no return cols
     *
     * @param spaceName nebula graph space
     * @param part      part to scan
     * @param tagName   nebula tag name
     * @param noColumns return no cols
     * @param limit     return data limit
     * @param startTime start time
     * @param endTime   end time
     * @return
     */
    public ScanVertexResultIterator scanVertex(String spaceName,
                                               int part,
                                               String tagName,
                                               boolean noColumns,
                                               int limit,
                                               long startTime,
                                               long endTime,
                                               boolean allowPartSuccess,
                                               boolean allowReadFromFollower) {
        return scanVertex(spaceName,
                Arrays.asList(part),
                tagName,
                new ArrayList<>(),
                noColumns,
                limit,
                startTime,
                endTime,
                allowPartSuccess,
                allowReadFromFollower);
    }


    /**
     * scan vertex of all parts with no return cols
     *
     * @param spaceName nebula graph space
     * @param tagName   nebula tag name
     * @param noColumns return no cols
     * @param limit     return data limit
     * @param startTime start time
     * @param endTime   end time
     * @return
     */
    public ScanVertexResultIterator scanVertex(String spaceName,
                                               String tagName,
                                               boolean noColumns,
                                               int limit,
                                               long startTime,
                                               long endTime,
                                               boolean allowPartSuccess,
                                               boolean allowReadFromFollower) {
        List<Integer> parts = metaManager.getSpaceParts(spaceName);
        if (parts.isEmpty()) {
            throw new IllegalArgumentException("No valid part in space " + spaceName);
        }
        return scanVertex(
                spaceName,
                parts,
                tagName,
                new ArrayList<>(),
                noColumns,
                limit,
                startTime,
                endTime,
                allowPartSuccess,
                allowReadFromFollower);
    }


    /**
     * scan vertex with parts
     *
     * @param spaceName  nebula graph space
     * @param parts      parts to scan
     * @param tagName    nebula tag name
     * @param returnCols return cols
     * @param noColumns  if return no col
     * @param limit      return data limit
     * @param startTime  start time
     * @param endTime    end time
     * @return
     */
    private ScanVertexResultIterator scanVertex(String spaceName,
                                                List<Integer> parts,
                                                String tagName,
                                                List<String> returnCols,
                                                boolean noColumns,
                                                int limit,
                                                long startTime,
                                                long endTime,
                                                boolean allowPartSuccess,
                                                boolean allowReadFromFollower) {

        Set<PartScanInfo> partScanInfoSet = new HashSet<>();
        for (int part : parts) {
            partScanInfoSet.add(new PartScanInfo(part, metaManager.getLeader(spaceName, part)));
        }
        Set<HostAddress> addrs;
        try {
            addrs = metaManager.listHosts();
        } catch (Exception e) {
            LOGGER.error("list hosts error,", e);
            throw e;
        }

        long tag = metaManager.getTag(spaceName, tagName).getTag_id();
        List<byte[]> props = new ArrayList<>();
        props.add("_vid".getBytes());
        if (!noColumns) {
            for (String prop : returnCols) {
                props.add(prop.getBytes());
            }
        }
        VertexProp vertexCols = new VertexProp((int) tag, props);

        ScanVertexRequest request = new ScanVertexRequest();
        request
                .setSpace_id(getSpaceId(spaceName))
                .setReturn_columns(vertexCols)
                .setLimit(limit)
                .setStart_time(startTime)
                .setEnd_time(endTime)
                .setEnable_read_from_follower(allowReadFromFollower);

        return doScanVertex(spaceName, tagName, partScanInfoSet, request, addrs, allowPartSuccess);
    }


    /**
     * do scan vertex
     *
     * @param spaceName       nebula graph space
     * @param tagName         nebula tag name
     * @param partScanInfoSet leaders of all parts
     * @param request         {@link ScanVertexRequest}
     * @param addrs           storage address set
     * @return result iterator
     */
    private ScanVertexResultIterator doScanVertex(String spaceName,
                                                  String tagName,
                                                  Set<PartScanInfo> partScanInfoSet,
                                                  ScanVertexRequest request,
                                                  Set<HostAddress> addrs,
                                                  boolean allowPartSuccess) {
        assert (spaceName != null && tagName != null);

        return new ScanVertexResultIterator.ScanVertexResultBuilder()
                .withMetaClient(metaManager)
                .withPool(pool)
                .withPartScanInfo(partScanInfoSet)
                .withRequest(request)
                .withAddresses(addrs)
                .withSpaceName(spaceName)
                .withTagName(tagName)
                .withPartSuccess(allowPartSuccess)
                .build();
    }


    /**
     * scan edge of all parts with specific return cols
     */
    public ScanEdgeResultIterator scanEdge(String spaceName, String edgeName,
                                           List<String> returnCols) {

        return scanEdge(spaceName, edgeName, returnCols,
                DEFAULT_LIMIT, DEFAULT_START_TIME, DEFAULT_END_TIME);
    }

    /**
     * scan edge of specific part with specific return cols
     */
    public ScanEdgeResultIterator scanEdge(String spaceName, int part, String edgeName,
                                           List<String> returnCols) {

        return scanEdge(spaceName, part, edgeName, returnCols,
                DEFAULT_LIMIT, DEFAULT_START_TIME, DEFAULT_END_TIME);
    }

    /**
     * scan edge of all parts with no return cols
     */
    public ScanEdgeResultIterator scanEdge(String spaceName, String edgeName) {
        return scanEdge(spaceName, edgeName, true,
                DEFAULT_LIMIT, DEFAULT_START_TIME, DEFAULT_END_TIME, false, true);
    }

    /**
     * scan edge of specific part with no return cols
     */
    public ScanEdgeResultIterator scanEdge(String spaceName, int part, String edgeName) {
        return scanEdge(spaceName, part, edgeName, true,
                DEFAULT_LIMIT, DEFAULT_START_TIME, DEFAULT_END_TIME, false);
    }

    /**
     * scan edge of all part with specific return cols and limit
     */
    public ScanEdgeResultIterator scanEdge(String spaceName, String edgeName,
                                           List<String> returnCols,
                                           int limit) {
        return scanEdge(spaceName, edgeName, returnCols,
                limit, DEFAULT_START_TIME, DEFAULT_END_TIME);
    }

    /**
     * scan edge of all part with specific return cols and limit
     */
    public ScanEdgeResultIterator scanEdge(String spaceName, String edgeName, int limit)
            throws Exception {
        return scanEdge(spaceName, edgeName, true, limit,
                DEFAULT_START_TIME, DEFAULT_END_TIME, false, true);
    }

    /**
     * scan edge of specific part
     *
     * @param spaceName  nebula graph space
     * @param part       part to scan
     * @param returnCols return cols
     * @param limit      return data limit
     * @param startTime  start time
     * @param endTime    end time
     * @return
     */
    public ScanEdgeResultIterator scanEdge(String spaceName,
                                           int part,
                                           String edgeName,
                                           List<String> returnCols,
                                           int limit,
                                           long startTime,
                                           long endTime) {
        assert (returnCols != null);

        return scanEdge(spaceName, Arrays.asList(part), edgeName, returnCols, false, limit,
                startTime, endTime, false, true);
    }


    /**
     * scan edge of all parts
     *
     * @param spaceName  nebula graph space
     * @param edgeName   nebula edge name
     * @param returnCols return cols
     * @param limit      return data limit
     * @param startTime  start time
     * @param endTime    end time
     * @return
     */
    public ScanEdgeResultIterator scanEdge(String spaceName,
                                           String edgeName,
                                           List<String> returnCols,
                                           int limit,
                                           long startTime,
                                           long endTime) {
        assert (returnCols != null);

        List<Integer> parts = metaManager.getSpaceParts(spaceName);
        if (parts.isEmpty()) {
            throw new IllegalArgumentException("No valid part in space " + spaceName);
        }
        return scanEdge(spaceName, parts, edgeName,
                returnCols, false, limit, startTime, endTime, false, true);
    }


    /**
     * scan edge of specific part with no return cols
     *
     * @param spaceName nebula graph space
     * @param edgeName  nebula edge name
     * @param part      part to scan
     * @param noColumns return no cols
     * @param limit     return data limit
     * @param startTime start time
     * @param endTime   end time
     * @return
     */
    public ScanEdgeResultIterator scanEdge(String spaceName,
                                           int part,
                                           String edgeName,
                                           boolean noColumns,
                                           int limit,
                                           long startTime,
                                           long endTime,
                                           boolean allowPartSuccess) {
        return scanEdge(spaceName, Arrays.asList(part), edgeName, new ArrayList<>(), noColumns,
                limit, startTime, endTime, allowPartSuccess, true);
    }


    /**
     * scan edge of all parts with no return cols
     *
     * @param spaceName nebula graph space
     * @param edgeName  nebula edge name
     * @param noColumns return no cols
     * @param limit     return data limit
     * @param startTime start time
     * @param endTime   end time
     * @return
     */
    public ScanEdgeResultIterator scanEdge(String spaceName,
                                           String edgeName,
                                           boolean noColumns,
                                           int limit,
                                           long startTime,
                                           long endTime,
                                           boolean allowPartSuccess,
                                           boolean allowReadFromFollower) {

        List<Integer> parts = metaManager.getSpaceParts(spaceName);
        if (parts.isEmpty()) {
            throw new IllegalArgumentException("No valid part in space " + spaceName);
        }
        return scanEdge(spaceName, parts, edgeName, new ArrayList<>(), noColumns,
                limit, startTime, endTime, allowPartSuccess, allowReadFromFollower);
    }


    /**
     * scan edge with parts
     *
     * @param spaceName  nebula graph space
     * @param parts      parts to scan
     * @param edgeName   nebula edge name
     * @param returnCols return cols
     * @param noColumns  if return no col, if noColumns is true, returnCols is useless
     * @param limit      return data limit
     * @param startTime  start time
     * @param endTime    end time
     * @return
     */
    private ScanEdgeResultIterator scanEdge(String spaceName,
                                            List<Integer> parts,
                                            String edgeName,
                                            List<String> returnCols,
                                            boolean noColumns,
                                            int limit,
                                            long startTime,
                                            long endTime,
                                            boolean allowPartSuccess,
                                            boolean allowReadFromFollower) {

        Set<PartScanInfo> partScanInfoSet = new HashSet<>();
        for (int part : parts) {
            partScanInfoSet.add(new PartScanInfo(part, metaManager.getLeader(spaceName, part)));
        }
        Set<HostAddress> addrs;
        try {
            addrs = metaManager.listHosts();
        } catch (Exception e) {
            LOGGER.error("list hosts error,", e);
            throw e;
        }

        List<byte[]> props = new ArrayList<>();
        props.add("_src".getBytes());
        props.add("_dst".getBytes());
        props.add("_rank".getBytes());
        if (!noColumns) {
            for (String prop : returnCols) {
                props.add(prop.getBytes());
            }
        }
        long edgeId = getEdgeId(spaceName, edgeName);
        EdgeProp edgeCols = new EdgeProp((int) edgeId, props);

        ScanEdgeRequest request = new ScanEdgeRequest();
        request
                .setSpace_id(getSpaceId(spaceName))
                .setReturn_columns(edgeCols)
                .setLimit(limit)
                .setStart_time(startTime)
                .setEnd_time(endTime)
                .setEnable_read_from_follower(allowReadFromFollower);

        return doScanEdge(spaceName, edgeName, partScanInfoSet, request, addrs, allowPartSuccess);
    }


    /**
     * do scan edge
     *
     * @param spaceName       nebula graph space
     * @param edgeName        nebula edge name
     * @param partScanInfoSet leaders of all parts
     * @param request         {@link ScanVertexRequest}
     * @param addrs           storage server list
     * @return result iterator
     */
    private ScanEdgeResultIterator doScanEdge(String spaceName,
                                              String edgeName,
                                              Set<PartScanInfo> partScanInfoSet,
                                              ScanEdgeRequest request,
                                              Set<HostAddress> addrs,
                                              boolean allowPartSuccess) {
        assert (spaceName != null && edgeName != null && addrs != null);

        return new ScanEdgeResultIterator.ScanEdgeResultBuilder()
                .withMetaClient(metaManager)
                .withPool(pool)
                .withPartScanInfo(partScanInfoSet)
                .withRequest(request)
                .withAddresses(addrs)
                .withSpaceName(spaceName)
                .withEdgeName(edgeName)
                .withPartSuccess(allowPartSuccess)
                .build();
    }


    /**
     * release storage client
     */
    public void close() {
        pool.close();
        connection.close();
    }


    /**
     * return client's connection
     *
     * @return StorageConnection
     */
    public GraphStorageConnection getConnection() {
        return this.connection;
    }


    /**
     * get space id from space name
     */
    private int getSpaceId(String spaceName) {
        return metaManager.getSpaceId(spaceName);
    }

    /**
     * get edge id
     */
    private long getEdgeId(String spaceName, String edgeName) {
        return metaManager.getEdge(spaceName, edgeName).getEdge_type();
    }

    private static final int DEFAULT_LIMIT = 1000;
    private static final long DEFAULT_START_TIME = 0;
    private static final long DEFAULT_END_TIME = Long.MAX_VALUE;
}

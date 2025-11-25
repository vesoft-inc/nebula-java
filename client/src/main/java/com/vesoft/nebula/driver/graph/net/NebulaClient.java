/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.net;

import static com.vesoft.nebula.driver.graph.net.Constants.DEFAULT_BATCH_SIZE;
import static com.vesoft.nebula.driver.graph.net.Constants.DEFAULT_CONNECT_TIMEOUT_MS;
import static com.vesoft.nebula.driver.graph.net.Constants.DEFAULT_DISABLE_VERIFY_SERVER_CERT;
import static com.vesoft.nebula.driver.graph.net.Constants.DEFAULT_ENABLE_TLS;
import static com.vesoft.nebula.driver.graph.net.Constants.DEFAULT_MAX_TIMEOUT_MS;
import static com.vesoft.nebula.driver.graph.net.Constants.DEFAULT_PING_TIMEOUT_MS;
import static com.vesoft.nebula.driver.graph.net.Constants.DEFAULT_REQUEST_TIMEOUT_MS;
import static com.vesoft.nebula.driver.graph.net.Constants.DEFAULT_SCAN_PARALLEL;

import com.vesoft.nebula.driver.graph.data.HostAddress;
import com.vesoft.nebula.driver.graph.data.ResultSet;
import com.vesoft.nebula.driver.graph.data.ValueWrapper;
import com.vesoft.nebula.driver.graph.exception.AuthFailedException;
import com.vesoft.nebula.driver.graph.exception.IOErrorException;
import com.vesoft.nebula.driver.graph.scan.ScanEdgeResultIterator;
import com.vesoft.nebula.driver.graph.scan.ScanNodeResultIterator;
import com.vesoft.nebula.driver.graph.utils.AddressUtil;
import com.vesoft.nebula.driver.graph.utils.GqlUtil;
import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client to connect to NebulaGraph and send request to NebulaGraph.
 */
public class NebulaClient implements Serializable {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final long closeTimeout = 1000L;

    private List<HostAddress> servers;
    private Builder           builder;
    private GrpcConnection    connection;
    private long              sessionId;
    private String            version;

    private long createTime;

    private boolean isClosed = false;

    public static Builder builder(String addresses, String userName, String password) {
        return new Builder(addresses, userName, password);
    }

    public static Builder builder(String addresses, String userName) {
        return new Builder(addresses, userName, null);
    }

    public static Builder builder(NebulaClient.Builder builder) {
        return builder;
    }

    private NebulaClient(NebulaClient.Builder builder)
        throws AuthFailedException, IOErrorException {
        this.servers = builder.address;
        this.builder = builder;
        initClient();
    }


    public ResultSet execute(String gql) throws IOErrorException {
        return execute(gql, builder.requestTimeoutMills);
    }

    public synchronized ResultSet execute(String gql, long requestTimeout) throws IOErrorException {
        checkClosed();
        return new ResultSet(connection.execute(sessionId, gql, requestTimeout));
    }

    /**
     * get the SessionId of the Client
     *
     * @return session id
     */
    public long getSessionId() {
        return sessionId;
    }

    /**
     * get the version of server
     *
     * @return version info
     */
    public String getVersion() {
        return version;
    }

    /**
     * get the creation time of the client
     *
     * @return creation time
     */
    public long getCreateTime() {
        return createTime;
    }

    /**
     * get the NebulaGraph host address that this client is connecting to.
     *
     * @return NebulaGraph server host.
     */
    public String getHost() {
        return connection.getServerAddress().toString();
    }

    public long getConnectTimeoutMills() {
        return builder.connectTimeoutMills;
    }

    public long getRequestTimeoutMills() {
        return builder.requestTimeoutMills;
    }

    public long getScanParallel() {
        return builder.scanParallel;
    }

    /**
     * ping the NebulaGraph server
     *
     * @param timeoutMs timeout, unit: ms
     * @return true when client can ping server succeed.
     */

    public synchronized boolean ping(long timeoutMs) {
        checkClosed();
        try {
            return connection.ping(sessionId, timeoutMs);
        } catch (IOErrorException e) {
            logger.error("ping error for host {}", getHost(), e);
            return false;
        }
    }

    public boolean ping() {
        return ping(DEFAULT_PING_TIMEOUT_MS);
    }

    /**
     * release and close the connection with NebulaGraph server.
     */
    public synchronized void close() {
        if (!isClosed) {
            isClosed = true;
            if (connection != null) {
                try {
                    connection.execute(sessionId, "SESSION CLOSE", closeTimeout);
                    connection.close();
                } catch (Exception e) {
                    logger.warn("signout failed,", e);
                }
            }
            connection = null;
        }
    }

    /**
     * get the Client status
     *
     * @reuturn true if client is closed
     */
    public boolean isClosed() {
        return isClosed;
    }

    /**
     * check if the client already closed
     *
     * @throws RuntimeException if the client is closed.
     */
    private void checkClosed() {
        if (isClosed) {
            throw new RuntimeException("The NebulaClient already closed.");
        }
    }

    private void initClient() throws AuthFailedException, IOErrorException {
        // create connection with NebulaGraph Server
        AuthResult authResult = null;
        connection = new GrpcConnection();
        int tryConnectTimes = servers.size();
        Collections.shuffle(servers);
        while (tryConnectTimes-- > 0) {
            try {
                connection.open(servers.get(tryConnectTimes), builder);
                authResult = connection.authenticate(builder.userName, builder.authOptions);
                sessionId = authResult.getSessionId();
                version = authResult.getVersion();
                createTime = System.currentTimeMillis();
                break;
            } catch (AuthFailedException e) {
                logger.error("create NebulaClient failed.", e);
                throw e;
            } catch (Exception e) {
                if (tryConnectTimes == 0) {
                    logger.error("create NebulaClient failed.", e);
                    throw e;
                }
            }
        }
    }


    public ScanNodeResultIterator scanNode(String graphName, String nodeType) {
        List<Integer> parts = getAllParts();
        return scanNode(null, graphName, nodeType, new ArrayList<>(), true, parts,
                        DEFAULT_BATCH_SIZE);
    }

    public ScanNodeResultIterator scanNode(String schema, String graphName, String nodeType) {
        List<Integer> parts = getAllParts();
        return scanNode(schema, graphName, nodeType, new ArrayList<>(),
                        true, parts, DEFAULT_BATCH_SIZE);
    }

    public ScanNodeResultIterator scanNode(String graphName,
                                           String nodeType,
                                           List<String> returnProperties) {
        List<Integer> parts         = getAllParts();
        boolean       allProperties = false;
        if (returnProperties == null) {
            allProperties = true;
        }
        return scanNode(null, graphName, nodeType, returnProperties,
                        allProperties, parts, DEFAULT_BATCH_SIZE);
    }

    /**
     * scan the data of specific nodeType. the result will contain all property of nodeType.
     *
     * @param graphName        NebulaGraph name
     * @param nodeType         node type name
     * @param returnProperties the property list to scan, if list is empty, then the result just
     *                         contain primary key.
     * @param batchSize        the data size for one request for one part, the
     *                         ScanNodeResultIterator.next() will return at most batchSize node
     *                         records
     * @return ScanNodeResultIterator
     */
    public ScanNodeResultIterator scanNode(String graphName,
                                           String nodeType,
                                           List<String> returnProperties,
                                           int batchSize) {
        List<Integer> parts         = getAllParts();
        boolean       allProperties = false;
        if (returnProperties == null) {
            allProperties = true;
        }
        return scanNode(null, graphName, nodeType, returnProperties,
                        allProperties, parts, batchSize);
    }

    public ScanNodeResultIterator scanNode(String schema,
                                           String graphName,
                                           String nodeType,
                                           List<String> returnProperties,
                                           int batchSize) {
        List<Integer> parts         = getAllParts();
        boolean       allProperties = false;
        if (returnProperties == null) {
            allProperties = true;
        }
        return scanNode(schema, graphName, nodeType, returnProperties,
                        allProperties, parts, batchSize);
    }

    /**
     * scan the data of specific nodeType the result will contain primary key and specific return
     * properties.
     *
     * @param graphName        NebulaGraph name
     * @param nodeType         node type name
     * @param returnProperties the property list to scan, if list is empty, then the result just
     *                         contain primary key.
     * @param part             graph part id
     * @param batchSize        the data size for one request for one part, the
     *                         ScanNodeResultIterator.next() will return at most batchSize node
     *                         records
     * @return ScanNodeResultIterator
     */
    public ScanNodeResultIterator scanNode(String graphName,
                                           String nodeType,
                                           List<String> returnProperties,
                                           int part,
                                           int batchSize) {
        return scanNode(graphName, nodeType, returnProperties,
                        Collections.singletonList(part), batchSize);
    }

    /**
     * scan the data of specific nodeType the result will contain primary key and specific return
     * properties.
     *
     * @param graphName        NebulaGraph name
     * @param nodeType         node type name
     * @param returnProperties the property list to scan, if list is empty, then the result just
     *                         contain primary key.
     * @param part             graph part id
     * @param batchSize        the data size for one request for one part, the
     *                         ScanNodeResultIterator.next() will return at most batchSize node
     *                         records
     * @return ScanNodeResultIterator
     */
    public ScanNodeResultIterator scanNode(String schema,
                                           String graphName,
                                           String nodeType,
                                           List<String> returnProperties,
                                           int part,
                                           int batchSize) {
        return scanNode(schema, graphName, nodeType, returnProperties,
                        Collections.singletonList(part), batchSize);
    }


    /**
     * scan the data of specific nodeType the result will contain primary key and specific return
     * properties.
     *
     * @param graphName        NebulaGraph name
     * @param nodeType         node type name
     * @param returnProperties the property list to scan, if list is empty, then the result just
     *                         contain primary key.
     * @param parts            list of graph part id
     * @param batchSize        the data size for one request for one part, the
     *                         ScanNodeResultIterator.next() will return at most batchSize node
     *                         records
     * @return ScanNodeResultIterator
     */
    public ScanNodeResultIterator scanNode(String graphName,
                                           String nodeType,
                                           List<String> returnProperties,
                                           List<Integer> parts,
                                           int batchSize) {
        boolean allProperties = false;
        if (returnProperties == null) {
            allProperties = true;
        }
        return scanNode(null, graphName, nodeType, returnProperties, allProperties, parts,
                        batchSize);
    }


    public ScanNodeResultIterator scanNode(String schema,
                                           String graphName,
                                           String nodeType,
                                           List<String> returnProperties,
                                           List<Integer> parts,
                                           int batchSize) {
        boolean allProperties = false;
        if (returnProperties == null) {
            allProperties = true;
        }
        return scanNode(schema, graphName, nodeType, returnProperties, allProperties, parts,
                        batchSize);
    }


    /**
     * scan the data of specific nodeType the result will contain primary key and specific return
     * properties.
     *
     * @param graphName        NebulaGraph name
     * @param nodeType         node type name
     * @param returnProperties the property list to scan, if list is empty, then the result just
     *                         contain primary key.
     * @param allProperties    if return all the properties of node type
     * @param parts            part list to scan
     * @param batchSize        the data size for one request for one part, the
     *                         ScanNodeResultIterator.next() will return at most batchSize node
     *                         records
     * @return ScanNodeResultIterator
     */
    private ScanNodeResultIterator scanNode(String schema,
                                            String graphName,
                                            String nodeType,
                                            List<String> returnProperties,
                                            boolean allProperties,
                                            List<Integer> parts,
                                            int batchSize) {
        // get node type's all property names
        List<String> nodeProperties = null;
        try {
            nodeProperties = getNodeProperties(graphName, nodeType);
        } catch (IOErrorException e) {
            logger.error("get node schema failed.", e);
            throw new RuntimeException(e);
        }

        // construct the return property list for scan
        List<String> propertyList = new ArrayList<>();
        if (allProperties) {
            propertyList.addAll(nodeProperties);
        } else {
            String primaryKey = nodeProperties.get(0);
            // put the primary key always on the head of the propertyList for scan
            propertyList.add(primaryKey);
            for (String propName : returnProperties) {
                if (propName.trim().equals(primaryKey)) {
                    continue;
                }
                propertyList.add(propName);
            }
        }

        return new ScanNodeResultIterator(schema,
                                          graphName,
                                          nodeType,
                                          propertyList,
                                          parts,
                                          batchSize,
                                          builder.scanParallel,
                                          servers,
                                          builder);
    }


    public ScanEdgeResultIterator scanEdge(String graphName, String edgeType) {
        List<Integer> parts = getAllParts();
        return scanEdge(null, graphName, edgeType, new ArrayList<>(), true, parts,
                        DEFAULT_BATCH_SIZE);
    }

    public ScanEdgeResultIterator scanEdge(String schema, String graphName, String edgeType) {
        List<Integer> parts = getAllParts();
        return scanEdge(schema, graphName, edgeType, new ArrayList<>(),
                        true, parts, DEFAULT_BATCH_SIZE);
    }

    public ScanEdgeResultIterator scanEdge(String graphName,
                                           String edgeType,
                                           List<String> returnProperties) {
        List<Integer> parts         = getAllParts();
        boolean       allProperties = false;
        if (returnProperties == null) {
            allProperties = true;
        }
        return scanEdge(null, graphName, edgeType, returnProperties,
                        allProperties, parts, DEFAULT_BATCH_SIZE);
    }

    /**
     * scan the data of specific edgeType the result will contain src node's primary key, dst node's
     * primary key, edge's all property.
     *
     * @param graphName        NebulaGraph name
     * @param edgeType         edge type name
     * @param returnProperties the property list to scan, if list is empty, then the result will
     *                         just contain src node's primary key and dst node's primary key
     * @param batchSize        the data size for one request for one part, the
     *                         ScanEdgeResultIterator.next() will return at most batchSize edge
     *                         records
     * @return ScanEdgeResultIterator
     */
    public ScanEdgeResultIterator scanEdge(String graphName,
                                           String edgeType,
                                           List<String> returnProperties,
                                           int batchSize) {
        boolean allProperties = false;
        if (returnProperties == null) {
            allProperties = true;
        }
        List<Integer> parts = getAllParts();
        return scanEdge(null, graphName, edgeType, returnProperties,
                        allProperties, parts, batchSize);
    }


    public ScanEdgeResultIterator scanEdge(String schema,
                                           String graphName,
                                           String edgeType,
                                           List<String> returnProperties,
                                           int batchSize) {
        boolean allProperties = false;
        if (returnProperties == null) {
            allProperties = true;
        }
        List<Integer> parts = getAllParts();
        return scanEdge(schema,
                        graphName,
                        edgeType,
                        returnProperties,
                        allProperties,
                        parts, batchSize);
    }

    /**
     * scan the data of specific edgeType
     *
     * @param graphName        NebulaGraph name
     * @param edgeType         edge type name
     * @param returnProperties the property list to scan, if list is empty, then the result will
     *                         just contain src node's primary key and dst node's primary key
     * @param part             graph part id
     * @param batchSize        the data size for one request for one part, the
     *                         ScanEdgeResultIterator.next() will return at most batchSize edge
     *                         records
     * @return ScanEdgeResultIterator
     */
    public ScanEdgeResultIterator scanEdge(String graphName,
                                           String edgeType,
                                           List<String> returnProperties,
                                           int part,
                                           int batchSize) {
        boolean allProperties = false;
        if (returnProperties == null) {
            allProperties = true;
        }
        return scanEdge(null,
                        graphName,
                        edgeType,
                        returnProperties,
                        allProperties,
                        Collections.singletonList(part),
                        batchSize);
    }

    public ScanEdgeResultIterator scanEdge(String schema,
                                           String graphName,
                                           String edgeType,
                                           List<String> returnProperties,
                                           int part,
                                           int batchSize) {
        boolean allProperties = false;
        if (returnProperties == null) {
            allProperties = true;
        }
        return scanEdge(schema,
                        graphName,
                        edgeType,
                        returnProperties,
                        allProperties,
                        Collections.singletonList(part),
                        batchSize);
    }


    /**
     * scan the data of specific edgeType
     *
     * @param graphName        NebulaGraph name
     * @param edgeType         edge type name
     * @param returnProperties the property list to scan, if list is empty, then the result will
     *                         just contain src node's primary key and dst node's primary key
     * @param parts            part list to scan
     * @param batchSize        the data size for one request for one part, the
     *                         ScanEdgeResultIterator.next() will return at most batchSize edge
     *                         records
     * @return ScanEdgeResultIterator
     */
    public ScanEdgeResultIterator scanEdge(String graphName,
                                           String edgeType,
                                           List<String> returnProperties,
                                           List<Integer> parts,
                                           int batchSize) {
        boolean allProperties = false;
        if (returnProperties == null) {
            allProperties = true;
        }
        return scanEdge(null,
                        graphName,
                        edgeType,
                        returnProperties,
                        allProperties,
                        parts,
                        batchSize);
    }


    public ScanEdgeResultIterator scanEdge(String schema,
                                           String graphName,
                                           String edgeType,
                                           List<String> returnProperties,
                                           List<Integer> parts,
                                           int batchSize) {
        boolean allProperties = false;
        if (returnProperties == null) {
            allProperties = true;
        }
        return scanEdge(schema,
                        graphName,
                        edgeType,
                        returnProperties,
                        allProperties,
                        parts,
                        batchSize);
    }


    /**
     * scan the data of specific edgeType
     *
     * @param graphName        NebulaGraph name
     * @param edgeType         edge type name
     * @param returnProperties the property list to scan, if list is empty, then the result will
     *                         just contain src node's primary key and dst node's primary key
     * @param allProperties    if return all the properties of edge type
     * @param parts            part list to scan
     * @param batchSize        the data size for one request for one part, the
     *                         ScanEdgeResultIterator.next() will return at most batchSize edge
     *                         records
     * @return ScanEdgeResultIterator
     */
    private ScanEdgeResultIterator scanEdge(String schema,
                                            String graphName,
                                            String edgeType,
                                            List<String> returnProperties,
                                            boolean allProperties,
                                            List<Integer> parts,
                                            int batchSize) {
        // get edge type's all property names
        List<String> edgeProperties = null;
        try {
            edgeProperties = getEdgeProperties(graphName, edgeType);
        } catch (IOErrorException e) {
            logger.error("get node schema failed.", e);
            throw new RuntimeException(e);
        }

        // construct the return property list for scan
        List<String> propertyList = new ArrayList<>();
        if (allProperties) {
            propertyList.addAll(edgeProperties);
        } else {
            propertyList.addAll(returnProperties);
        }
        return new ScanEdgeResultIterator(schema,
                                          graphName,
                                          edgeType,
                                          propertyList,
                                          parts,
                                          batchSize,
                                          builder.scanParallel,
                                          servers,
                                          builder);
    }


    /**
     * get all parts
     *
     * @return list of part id
     */
    private List<Integer> getAllParts() {
        String    showPartitions = "CALL show_partitions() RETURN *";
        ResultSet resultSet;
        try {
            resultSet = execute(showPartitions);
        } catch (Exception e) {
            logger.error("get all partitions error", e);
            throw new RuntimeException("get all partitions error", e);
        }
        if (!resultSet.isSucceeded() || resultSet.isEmpty()) {
            logger.error("get all partitions failed for {}", resultSet.getErrorMessage());
            throw new RuntimeException(
                "get all partitions failed for " + resultSet.getErrorMessage());
        }

        List<Integer> partitions = new ArrayList<>();
        while (resultSet.hasNext()) {
            partitions.add(resultSet.next().get("partition_id").asInt());
        }
        if (partitions.contains(0)) {
            partitions.remove(Integer.valueOf(0));
        }
        return partitions;
    }


    /**
     * get node type's properties
     *
     * @param graphName NebulaGraph name
     * @param nodeType  node type name
     * @return List of property name
     */
    private List<String> getNodeProperties(String graphName, String nodeType)
        throws IOErrorException {
        String graphType = getGraphType(graphName);
        String descNodeType = String.format("DESCRIBE NODE TYPE `%s` OF `%s`",
                                            GqlUtil.escape(nodeType),
                                            GqlUtil.escape(graphType));
        ResultSet resultSet = execute(descNodeType);
        if (!resultSet.isSucceeded() || resultSet.isEmpty()) {
            logger.error(String.format("get description of %s failed for %s", nodeType,
                                       resultSet.getErrorMessage()));
            throw new IllegalArgumentException(String.format("node type %s does not exist in %s",
                                                             nodeType, graphName));
        }

        List<String> pks       = new ArrayList<>();
        List<String> propNames = new ArrayList<>();
        while (resultSet.hasNext()) {
            ResultSet.Record record = resultSet.next();
            propNames.add(record.get("property_name").asString());
            if ("Y".equals(record.get("primary_key").asString())) {
                pks.add(record.get("property_name").asString());
            }
        }

        if (pks.isEmpty()) {
            logger.error("node type " + nodeType + " has no primary key.");
            throw new RuntimeException("node type " + nodeType + " has no primary key");
        }

        // define the property name list, and put the pk on the head of list.
        List<String> propertyNames = new ArrayList<>(pks);
        for (String property : propNames) {
            if (pks.contains(property)) {
                continue;
            }
            propertyNames.add(property);
        }
        return propertyNames;
    }


    /**
     * get edge type's properties
     *
     * @param graphName NebulaGraph name
     * @param edgeType  edge type name
     * @return List of property name
     */
    private List<String> getEdgeProperties(String graphName, String edgeType)
        throws IOErrorException {
        String graphType = getGraphType(graphName);

        String descEdgeType = String.format(
            "CALL describe_graph_type(\"%s\") FILTER type_name=\"%s\" return properties",
            GqlUtil.escape(graphType), GqlUtil.escape(edgeType));
        ResultSet resultSet = execute(descEdgeType);
        if (!resultSet.isSucceeded() || resultSet.isEmpty()) {
            logger.error(String.format("get description of %s failed for %s", edgeType,
                                       resultSet.getErrorMessage()));
            throw new IllegalArgumentException(String.format("edge type %s does not exist in %s",
                                                             edgeType, graphName));
        }

        List<ValueWrapper> properties = resultSet.next().get("properties").asList();
        return properties.stream().map(ValueWrapper::asString).collect(Collectors.toList());
    }


    /**
     * get the graph's graph type
     *
     * @param graphName NebulaGraph name
     * @return String
     */
    private String getGraphType(String graphName) throws IOErrorException {
        ResultSet resultSet = execute(String.format("DESCRIBE GRAPH `%s`",
                                                    GqlUtil.escape(graphName)));
        String graphType;
        if (resultSet.isSucceeded() && !resultSet.isEmpty()) {
            graphType = resultSet.next().values().get(1).asString();
        } else {
            throw new IllegalArgumentException("graphName " + graphName + " does not exist.");
        }
        return graphType;
    }

    public static class Builder {

        protected final List<HostAddress>   address;
        protected final String              userName;
        protected final String              password;
        protected       Map<String, Object> authOptions             = new HashMap<>();
        protected       long                connectTimeoutMills     = DEFAULT_CONNECT_TIMEOUT_MS;
        // ms timeout for rpc request, the value must be larger than 0, smaller than 100000001000L
        protected       long                requestTimeoutMills     = DEFAULT_REQUEST_TIMEOUT_MS;
        protected       int                 scanParallel            = DEFAULT_SCAN_PARALLEL;
        protected       boolean             enableTls               = DEFAULT_ENABLE_TLS;
        protected       boolean             disableVerifyServerCert =
            DEFAULT_DISABLE_VERIFY_SERVER_CERT;
        protected       String              tlsCa;
        protected       String              tlsCert;
        protected       String              tlsKey;

        /**
         * Builder for {@link NebulaClient}
         *
         * @param addresses graphd servers address
         * @param userName  username
         * @param password  password for user
         */
        public Builder(String addresses, String userName, String password) {
            try {
                this.address = AddressUtil.validateAddress(addresses);
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
            this.userName = userName;
            this.password = password;
        }

        /**
         * config the auth options for user
         *
         * @param authOptions map of auth options
         * @return NebulaClient.Builder
         */
        public Builder withAuthOptions(Map<String, Object> authOptions) {
            if (authOptions != null) {
                this.authOptions.putAll(authOptions);
            }
            return this;
        }

        /**
         * config the timeout for tcp connect, unit: ms the value must be larger than 0 and smaller
         * than Integer.MAX_VALUE in jdk 8.
         *
         * @param connectTimeoutMills timeout ms
         * @return NebulaClient.Builder
         */
        public Builder withConnectTimeoutMills(long connectTimeoutMills) {
            if (connectTimeoutMills <= 0 || connectTimeoutMills > DEFAULT_MAX_TIMEOUT_MS) {
                this.connectTimeoutMills = DEFAULT_MAX_TIMEOUT_MS;
            } else {
                this.connectTimeoutMills = connectTimeoutMills;
            }
            return this;
        }

        /**
         * config the timeout for rpc request, unit: ms the value must be larger than 0 and smaller
         * than Integer.MAX_VALUE in jdk 8.
         *
         * @param requestTimeoutMills timeout ms
         * @return NebulaClient.Builder
         */
        public Builder withRequestTimeoutMills(long requestTimeoutMills) {
            if (requestTimeoutMills < 0 || requestTimeoutMills > DEFAULT_MAX_TIMEOUT_MS) {
                this.requestTimeoutMills = DEFAULT_MAX_TIMEOUT_MS;
            } else {
                this.requestTimeoutMills = requestTimeoutMills;
            }
            return this;
        }

        /**
         * config the parallel for data scan
         *
         * @param scanParallel number of the concurrency for data scan
         * @return NebulaClient.Builder
         */
        public Builder withScanParallel(int scanParallel) {
            this.scanParallel = scanParallel;
            return this;
        }

        /**
         * config whether enable tls
         *
         * @param enableTls true if enable the tls
         * @return NebulaClient.Builder
         */
        public Builder withEnableTls(boolean enableTls) {
            this.enableTls = enableTls;
            return this;
        }

        /**
         * disable the verification of server cert
         *
         * @param disableVerifyServerCert true if not verify the server cert
         * @return NebulaClient.Builder
         */
        public Builder withDisableVerifyServerCert(boolean disableVerifyServerCert) {
            this.disableVerifyServerCert = disableVerifyServerCert;
            return this;
        }

        /**
         * config the ca certificate for TLS
         *
         * @param ca path to the trusted CA certificate file used to authenticate the server
         * @return NebulaClient.Builder
         */
        public Builder withTlsCa(String ca) {
            this.tlsCa = ca;
            return this;
        }

        /**
         * config the TLS Cert options, necessary only if mTLS is enabled on Graph Server side
         *
         * @param cert certificate of client
         * @param key  private key of client certificate
         * @return NebulaClient.Builder
         */
        public Builder withTlsCert(String cert, String key) {
            this.tlsCert = cert;
            this.tlsKey = key;
            return this;
        }


        public void check() {
            if (address == null) {
                throw new IllegalArgumentException("Graph addresses cannot be empty.");
            }

            if (enableTls && !disableVerifyServerCert && tlsCa == null) {
                throw new IllegalArgumentException("TLS is enable, tlsCa cannot be empty.");
            }
        }

        /**
         * build a new {@link NebulaClient} with configs
         *
         * @return {@link NebulaClient}
         */
        public NebulaClient build() throws AuthFailedException, IOErrorException {
            check();
            if (password != null) {
                authOptions.put("password", password);
            }
            return new NebulaClient(this);
        }

        public List<HostAddress> getAddress() {
            return address;
        }

        public String getUserName() {
            return userName;
        }

        public String getPassword() {
            return password;
        }

        public Map<String, Object> getAuthOptions() {
            return authOptions;
        }

        public long getConnectTimeoutMills() {
            return connectTimeoutMills;
        }

        public long getRequestTimeoutMills() {
            return requestTimeoutMills;
        }

        public int getScanParallel() {
            return scanParallel;
        }

        public boolean isEnableTls() {
            return enableTls;
        }

        public boolean isDisableVerifyServerCert() {
            return disableVerifyServerCert;
        }

        public String getTlsCa() {
            return tlsCa;
        }

        public String getTlsCert() {
            return tlsCert;
        }

        public String getTlsKey() {
            return tlsKey;
        }
    }
}

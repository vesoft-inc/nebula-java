package com.vesoft.nebula.database.pool;

import com.facebook.thrift.TException;
import com.facebook.thrift.protocol.TCompactProtocol;
import com.facebook.thrift.protocol.TProtocol;
import com.facebook.thrift.transport.TSocket;
import com.facebook.thrift.transport.TTransport;
import com.facebook.thrift.transport.TTransportException;
import com.google.common.collect.Lists;
import com.google.common.net.InetAddresses;
import com.vesoft.nebula.database.ConnectionManager;
import com.vesoft.nebula.database.NebulaConnection;
import com.vesoft.nebula.database.NebulaDataSource;
import com.vesoft.nebula.database.constant.DataBaseConstant;
import com.vesoft.nebula.database.constant.ErrorEnum;
import com.vesoft.nebula.database.entity.LinkDomain;
import com.vesoft.nebula.database.exception.ConnectException;
import com.vesoft.nebula.graph.AuthResponse;
import com.vesoft.nebula.graph.ErrorCode;
import com.vesoft.nebula.graph.GraphService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Random;
import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author huangzhaolai-jk
 * @version 1.0.0
 * @Description NebulaPoolDataSource is used for
 * @Date 2020/3/17 - 14:17
 */
@Data
@Slf4j
public class NebulaPoolDataSource implements NebulaDataSource {

    private final ConnectionManager connectionManager;

    /**
     * connection addresses
     */
    private List<LinkDomain> addresses;
    /**
     * retry connect time
     */
    private int connectionRetry = 3;

    /**
     * socket timeout
     */
    private int timeout = 10000;


    private String host;


    private int port;

    public NebulaPoolDataSource(LinkDomain linkDomain, int timeout, int connectionRetry, int poolSize) {
        this(Lists.newArrayList(linkDomain), timeout, connectionRetry, poolSize);
    }

    /**
     * Parameter constructor
     *
     * @param timeout
     * @param connectionRetry
     */
    public NebulaPoolDataSource(List<LinkDomain> addresses, int timeout, int connectionRetry, int poolSize) {

        checkArgument(timeout > DataBaseConstant.ZERO);
        checkArgument(connectionRetry > DataBaseConstant.ZERO);
        checkArgument(addresses != null && addresses.size() > DataBaseConstant.ZERO);
        for (LinkDomain linkDomain : addresses) {
            String host = linkDomain.getHost();
            int port = linkDomain.getPort();
            if (!InetAddresses.isInetAddress(host) || isInValidPort(port)) {
                throw new IllegalArgumentException(String.format("%s:%d is not a valid address",
                        host, port));
            }
        }
        if (poolSize <= DataBaseConstant.ZERO || poolSize > DataBaseConstant.MAX_POOL_SIZE) {
            poolSize = DataBaseConstant.DEFAULT_POOL_SIZE;
        }
        this.connectionManager = new PoolConnectionManager(poolSize);
        this.addresses = addresses;
        this.timeout = timeout;
        this.connectionRetry = connectionRetry;
    }

    private boolean isInValidPort(int port) {
        return port <= DataBaseConstant.ZERO || port >= DataBaseConstant.MAX_PORT;
    }

    @Override
    public NebulaConnection getConnection() throws ConnectException {
        NebulaConnection connection = null;
        int retry = connectionRetry;
        while (connection == null && retry-- >= DataBaseConstant.ZERO) {
            //get from connectionManager
            connection = connectionManager.getConnection();
            if (connection != null) {
                break;
            }
            if (connectionManager.canAddConnection()) {
                //create a new connection
                synchronized (connectionManager) {
                    connection = connect();
                    if (connection != null && connection.isOpened() && connectionManager.canAddConnection()) {
                        boolean added = connectionManager.addConnection(connection);
                        if (!added) {
                            connection = null;
                        }
                    } else {
                        connection = null;
                    }
                }
            }
        }
        if (connection != null) {
            return connection;
        } else {
            throw new ConnectException(ErrorEnum.CONNECT_LACK);
        }
    }

    @Override
    public void release(NebulaConnection connection) {
        connectionManager.releaseConnection(connection);
    }

    @Override
    public int currentPoolSize() {
        return connectionManager.currentConnectionCount();
    }

    @Override
    public int maxPoolSize() {
        return connectionManager.maxConnectionCount();
    }

    @Override
    public int freePoolSize() {
        return connectionManager.freeConnectionCount();
    }

    /**
     * connect to nebula
     *
     * @return
     */
    private NebulaConnection connect() {
        GraphService.Client client;
        TTransport transport;
        Random random = new Random(System.currentTimeMillis());
        int position = random.nextInt(addresses.size());
        LinkDomain linkDomain = addresses.get(position);
        transport = new TSocket(linkDomain.getHost(), linkDomain.getPort(), timeout);
        TProtocol protocol = new TCompactProtocol(transport);
        try {
            transport.open();
            client = new GraphService.Client(protocol);
            AuthResponse result = client.authenticate(linkDomain.getUserName(), linkDomain.getPassword());
            if (result.getError_code() == ErrorCode.E_BAD_USERNAME_PASSWORD) {
                log.error("User name or password error");
            }
            if (result.getError_code() != ErrorCode.SUCCEEDED) {
                log.error(String.format("Connect address %s failed : %s",
                        linkDomain.toString(), result.getError_msg()));
            } else {
                //succeed
                long sessionId = result.getSession_id();
                return new NebulaPoolConnection(client, transport, sessionId, connectionRetry);
            }
        } catch (TTransportException tte) {
            log.error("Connect failed: " + tte.getMessage());
        } catch (TException te) {
            log.error("Connect failed: " + te.getMessage());
        }
        return null;
    }


}

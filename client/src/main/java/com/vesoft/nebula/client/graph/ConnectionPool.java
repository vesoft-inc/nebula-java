/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.graph;

import com.facebook.thrift.TException;
import com.google.common.net.HostAndPort;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ConnectionPool {
    private static final int S_OK = 0;
    private static final int S_BAD = 1;
    private List<HostAndPort> addresses = new ArrayList<HostAndPort>();
    private String userName;
    private String password;
    private Config config;
    private int pos = 0;
    private Map<HostAndPort, Integer> serversStatus = new HashMap<HostAndPort, Integer>();
    private Map<HostAndPort, List<Connection>> connections =
            new HashMap<HostAndPort, List<Connection>>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private Boolean isClosed = false;
    private final Logger log = LoggerFactory.getLogger(ConnectionPool.class);

    /**
     * init the connection pool.
     *
     * @param addresses The graphd servers addresses
     * @param userName The userName to authenticate
     * @param password The password to authenticate
     * @param config The config for connectionPool
     * @return Boolean if all addresses are ok, return true else return.
     */
    public Boolean init(List<HostAndPort> addresses,
                        String userName,
                        String password,
                        Config config) {
        this.addresses = addresses;
        this.userName = userName;
        this.password = password;
        this.config = config;
        for (HostAndPort addr : addresses) {
            serversStatus.put(addr, S_BAD);
            connections.put(addr, new ArrayList<Connection>());
        }
        updateServersStatus();
        if (getOkServersNum() != this.addresses.size()) {
            return false;
        }
        return true;
    }

    /**
     * getSession the query sentence.
     *
     * @param retryConnection if need to do reconnect.
     *                        The 1.0 nebula-graph doesn't support.
     *                        So user need to do reconnect to call getSession
     * @return The Session.
     */
    public Session getSession(Boolean retryConnection)
            throws NotValidConnectionException, IOErrorException, AuthFailedException {
        Connection conn = getConnection();
        if (conn == null) {
            throw new NotValidConnectionException();
        }
        try {
            long sessionId = conn.authenticate(userName, password);
            return new Session(conn, sessionId, this, retryConnection);
        } catch (Exception e) {
            throw e;
        }

    }

    public Connection getConnection() {
        try {
            lock.writeLock().lock();
            if (isClosed) {
                return null;
            }
            int okNum = getOkServersNum();
            if (okNum == 0) {
                return null;
            }

            int maxConnPerAddr = (int)(config.maxConnectionPoolSize / okNum);
            int tryCount = 0;
            while (tryCount <= addresses.size()) {
                pos = (pos + 1) %  addresses.size();
                HostAndPort addr = addresses.get(pos);
                List<Connection> conns = connections.get(addr);
                if (serversStatus.get(addr) == S_OK) {
                    if (conns.size() < maxConnPerAddr) {
                        Connection connection = new Connection();
                        connection.open(addr, config.timeout);
                        conns.add(connection);
                    }
                    for (Connection conn : conns) {
                        if (!conn.isUsed()) {
                            conn.setUsed();
                            log.info(String.format("Get connection from address [%s:%d]",
                                    addr.getHostText(), addr.getPort()));
                            return conn;
                        }
                    }
                } else {
                    for (Connection conn : conns) {
                        if (!conn.isUsed()) {
                            conns.remove(conn);
                        }
                    }
                }
                tryCount++;
            }
            return null;
        } catch (TException e) {
            return null;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void updateServersStatus() {
        try {
            lock.writeLock().lock();
            for (HostAndPort addr : addresses) {
                if (ping(addr)) {
                    serversStatus.put(addr, S_OK);
                } else {
                    serversStatus.put(addr, S_BAD);
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public int getOkServersNum() {
        try {
            lock.readLock().lock();
            int okNum = 0;
            for (HostAndPort addr : serversStatus.keySet()) {
                if (serversStatus.get(addr) == S_OK) {
                    okNum++;
                }
            }
            return okNum;
        } finally {
            lock.readLock().unlock();
        }
    }

    public int getInUsedNum() {
        try {
            lock.readLock().lock();
            int usedNum = 0;
            for (HostAndPort addr : connections.keySet()) {
                List<Connection> conns = connections.get(addr);
                for (Connection conn : conns) {
                    if (conn.isUsed()) {
                        usedNum++;
                    }
                }
            }
            return usedNum;
        } finally {
            lock.readLock().unlock();
        }
    }

    private Boolean ping(HostAndPort addr) {
        try {
            Connection connection = new Connection();
            connection.open(addr, config.timeout);
            connection.close();
            return true;
        } catch (TException e) {
            return false;
        }
    }

    public void close() {
        try {
            lock.readLock().lock();
            for (HostAndPort addr : connections.keySet()) {
                for (Connection conn : connections.get(addr)) {
                    conn.close();
                }
            }
            isClosed = true;
        } finally {
            lock.readLock().unlock();
        }
    }
}

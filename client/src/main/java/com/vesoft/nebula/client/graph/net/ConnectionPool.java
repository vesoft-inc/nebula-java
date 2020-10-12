/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.graph.net;

import com.vesoft.nebula.client.graph.Config;
import com.vesoft.nebula.client.graph.data.HostAddress;
import com.vesoft.nebula.client.graph.exception.AuthFailedException;
import com.vesoft.nebula.client.graph.exception.IOErrorException;
import com.vesoft.nebula.client.graph.exception.NotValidConnectionException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ConnectionPool {
    private static final int S_OK = 0;
    private static final int S_BAD = 1;
    private String userName;
    private String password;
    private Config config;
    private int pos = 0;
    private Boolean isClosed = false;
    // the period time to check connections
    private int delayTime = 60;  // unit seconds
    // save the servers' address
    private List<HostAddress> addresses = new ArrayList<>();
    // save the servers' status
    private final Map<HostAddress, Integer> serversStatus = new HashMap<>();
    private final Map<HostAddress, List<Connection>> connections = new HashMap<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Logger log = LoggerFactory.getLogger(ConnectionPool.class);
    private final ScheduledExecutorService schedule = Executors.newScheduledThreadPool(1);

    /**
     * init the connection pool.
     *
     * @param addresses The graphd servers addresses
     * @param userName The userName to authenticate
     * @param password The password to authenticate
     * @param config The config for connectionPool
     * @return Boolean if all addresses are ok, return true else return.
     */
    public Boolean init(List<HostAddress> addresses,
                        String userName,
                        String password,
                        Config config) {
        this.addresses = addresses;
        this.userName = userName;
        this.password = password;
        this.config = config;
        for (HostAddress addr : addresses) {
            serversStatus.put(addr, S_BAD);
            connections.put(addr, new ArrayList<>());
        }
        updateServersStatus();
        int okNum = getOkServersNum();
        if (getOkServersNum() != this.addresses.size()) {
            return false;
        }

        int maxConnPerAddr = config.getMinConnSize() / okNum;
        // Init the min conns num
        for (HostAddress addr : addresses) {
            List<Connection> conns = connections.get(addr);
            try {
                int i = 0;
                while (i < maxConnPerAddr) {
                    Connection connection = new Connection();
                    connection.open(addr, config.getTimeout());
                    conns.add(connection);
                    i++;
                }
            } catch (IOErrorException e) {
                log.error(String.format("Init connection failed: %s", e.getMessage()));
                return false;
            }
        }

        schedule.scheduleAtFixedRate(this::scheduleTask, delayTime, delayTime, TimeUnit.SECONDS);
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
        long sessionId = conn.authenticate(userName, password);
        conn.setUsed(true);
        return new Session(conn, sessionId, this, retryConnection);
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
            log.info(String.format("getMaxConnSize is %d", config.getMaxConnSize()));
            int maxConnPerAddr = config.getMaxConnSize() / okNum;
            int tryCount = 0;
            while (tryCount <= addresses.size()) {
                pos = (pos + 1) %  addresses.size();
                HostAddress addr = addresses.get(pos);
                List<Connection> conns = connections.get(addr);
                if (serversStatus.get(addr) == S_OK) {
                    for (Connection conn : conns) {
                        if (!conn.isUsed()) {
                            conn.setUsed(true);
                            log.info(String.format("Get connection to address [%s:%d]",
                                    addr.getHost(), addr.getPort()));
                            return conn;
                        }
                    }
                    if (conns.size() < maxConnPerAddr) {
                        try {
                            Connection connection = new Connection();
                            connection.open(addr, config.getTimeout());
                            conns.add(connection);
                        } catch (IOErrorException e) {
                            log.error(String.format("Connect to [%s:%d] failed: %s.",
                                    addr.getHost(), addr.getPort(), e.getMessage()));
                        }
                    }
                } else {
                    conns.removeIf(conn -> !conn.isUsed());
                }
                tryCount++;
            }
            return null;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void updateServersStatus() {
        try {
            lock.writeLock().lock();
            for (HostAddress addr : addresses) {
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
            for (HostAddress addr : serversStatus.keySet()) {
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
            for (HostAddress addr : connections.keySet()) {
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

    public void close() {
        try {
            lock.readLock().lock();
            for (HostAddress addr : connections.keySet()) {
                for (Connection conn : connections.get(addr)) {
                    conn.close();
                }
            }
            isClosed = true;
        } finally {
            lock.readLock().unlock();
        }
    }

    public int getConnectionsNum() {
        try {
            lock.readLock().lock();
            int connectionNum = 0;
            for (HostAddress addr : connections.keySet()) {
                connectionNum += connections.get(addr).size();
            }
            return connectionNum;
        } finally {
            lock.readLock().unlock();
        }
    }

    private boolean ping(HostAddress addr) {
        try {
            Connection connection = new Connection();
            connection.open(addr, config.getTimeout());
            connection.close();
            return true;
        } catch (IOErrorException e) {
            return false;
        }
    }

    private void scheduleTask() {
        updateServersStatus();
        log.debug("scheduleTask start");
        try {
            lock.writeLock().lock();
            for (HostAddress addr : connections.keySet()) {
                List<Connection> conns = connections.get(addr);
                conns.removeIf(conn -> !conn.isUsed() && conn.idleTime() > config.getIdleTime());
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
}

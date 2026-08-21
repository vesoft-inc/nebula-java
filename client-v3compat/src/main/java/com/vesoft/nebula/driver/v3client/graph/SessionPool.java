/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.v3client.graph;

import com.alibaba.fastjson.JSON;
import com.vesoft.nebula.driver.v3client.graph.data.CASignedSSLParam;
import com.vesoft.nebula.driver.v3client.graph.data.HostAddress;
import com.vesoft.nebula.driver.v3client.graph.data.ResultSet;
import com.vesoft.nebula.driver.v3client.graph.data.SSLParam;
import com.vesoft.nebula.driver.v3client.graph.data.SelfSignedSSLParam;
import com.vesoft.nebula.driver.v3client.graph.exception.AuthFailedException;
import com.vesoft.nebula.driver.v3client.graph.exception.BindSpaceFailedException;
import com.vesoft.nebula.driver.v3client.graph.exception.ClientServerIncompatibleException;
import com.vesoft.nebula.driver.v3client.graph.exception.IOErrorException;
import com.vesoft.nebula.driver.v3client.graph.net.Session;
import com.vesoft.nebula.driver.v3client.graph.net.SessionState;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A pool of sessions, matching the v3 client's {@code SessionPool}.
 */
public class SessionPool implements Serializable {

    private static final long serialVersionUID = 6051248334277617891L;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final ScheduledExecutorService healthCheckSchedule =
        Executors.newScheduledThreadPool(1);
    private final ScheduledExecutorService sessionQueueMaintainSchedule =
        Executors.newScheduledThreadPool(1);

    public CopyOnWriteArrayList<NebulaSession> sessionList = new CopyOnWriteArrayList<>();
    public AtomicInteger idleSessionSize = new AtomicInteger(0);
    public AtomicBoolean hasInit = new AtomicBoolean(false);
    public AtomicBoolean isClosed = new AtomicBoolean(false);

    private final AtomicInteger pos = new AtomicInteger(0);

    private final SessionPoolConfig sessionPoolConfig;
    private final int minSessionSize;
    private final int maxSessionSize;
    private final int cleanTime;
    private final int healthCheckTime;
    private final int retryTimes;
    private final int intervalTime;
    private final boolean reconnect;
    private final String spaceName;

    public SessionPool(SessionPoolConfig poolConfig) {
        this.sessionPoolConfig = poolConfig;
        this.minSessionSize = poolConfig.getMinSessionSize();
        this.maxSessionSize = poolConfig.getMaxSessionSize();
        this.cleanTime = poolConfig.getCleanTime();
        this.retryTimes = poolConfig.getRetryTimes();
        this.intervalTime = poolConfig.getIntervalTime();
        this.reconnect = poolConfig.isReconnect();
        this.healthCheckTime = poolConfig.getHealthCheckTime();
        this.spaceName = poolConfig.getSpaceName();
        init();
    }

    private synchronized NebulaSession getSession()
        throws ClientServerIncompatibleException, AuthFailedException, IOErrorException,
        BindSpaceFailedException {
        int retry = sessionPoolConfig.getRetryConnectTimes();
        while (retry-- >= 0) {
            if (idleSessionSize.get() > 0) {
                for (NebulaSession nebulaSession : sessionList) {
                    if (nebulaSession.isIdleAndSetUsed()) {
                        idleSessionSize.decrementAndGet();
                        return nebulaSession;
                    }
                }
            }
            if (sessionList.size() < maxSessionSize) {
                return createSessionObject(SessionState.USED);
            }
            try {
                Thread.sleep(sessionPoolConfig.getWaitTime());
            } catch (InterruptedException e) {
                log.error("getSession error when wait for idle sessions, ", e);
                throw new RuntimeException(e);
            }
        }
        throw new RuntimeException("no extra session available");
    }

    @Deprecated
    public boolean init() {
        if (hasInit.get()) {
            return true;
        }
        while (sessionList.size() < minSessionSize) {
            try {
                createSessionObject(SessionState.IDLE);
                idleSessionSize.incrementAndGet();
            } catch (Exception e) {
                log.error("SessionPool init failed. ", e);
                throw new RuntimeException("create session failed.", e);
            }
        }
        healthCheckSchedule.scheduleAtFixedRate(this::checkSession, 0, healthCheckTime,
                                                TimeUnit.SECONDS);
        sessionQueueMaintainSchedule.scheduleAtFixedRate(this::updateSessionQueue, 0, cleanTime,
                                                        TimeUnit.SECONDS);
        hasInit.compareAndSet(false, true);
        return true;
    }

    public ResultSet execute(String stmt) throws IOErrorException,
        ClientServerIncompatibleException, AuthFailedException, BindSpaceFailedException {
        return execute(stmt, Collections.emptyMap());
    }

    public ResultSet execute(String stmt, Map<String, Object> parameterMap)
        throws ClientServerIncompatibleException, AuthFailedException,
        IOErrorException, BindSpaceFailedException {
        stmtCheck(stmt);
        checkSessionPool();
        NebulaSession nebulaSession = null;
        ResultSet resultSet = null;
        int tryTimes = 0;
        while (tryTimes++ <= retryTimes) {
            try {
                nebulaSession = getSession();
                resultSet = nebulaSession.executeWithParameter(stmt, parameterMap);
                if (resultSet.isSucceeded()
                    || resultSet.getErrorCode() == ErrorCode.E_SEMANTIC_ERROR.getValue()
                    || resultSet.getErrorCode() == ErrorCode.E_SYNTAX_ERROR.getValue()
                    || resultSet.getErrorCode() == ErrorCode.E_QUERY_TIMEDOUT.getValue()) {
                    releaseSession(nebulaSession);
                    return resultSet;
                }
                log.warn(String.format("execute error, code: %d, message: %s, retry: %d",
                                       resultSet.getErrorCode(), resultSet.getErrorMessage(),
                                       tryTimes));
                nebulaSession.release();
                sessionList.remove(nebulaSession);
                try {
                    Thread.sleep(intervalTime);
                } catch (InterruptedException ignored) {
                    // ignore
                }
            } catch (ClientServerIncompatibleException e) {
                // will never get here.
            } catch (AuthFailedException | BindSpaceFailedException e) {
                throw e;
            } catch (IOErrorException e) {
                if (nebulaSession != null) {
                    nebulaSession.release();
                    sessionList.remove(nebulaSession);
                }
                if (tryTimes < retryTimes) {
                    log.warn(String.format("execute failed for IOErrorException, message: %s, "
                                               + "retry: %d", e.getMessage(), tryTimes));
                    try {
                        Thread.sleep(intervalTime);
                    } catch (InterruptedException ignored) {
                        // ignore
                    }
                } else {
                    throw e;
                }
            }
        }
        if (nebulaSession != null) {
            nebulaSession.release();
            sessionList.remove(nebulaSession);
        }
        return resultSet;
    }

    public ResultSet executeWithTimeout(String stmt, long timeoutMs)
        throws IOErrorException, AuthFailedException, BindSpaceFailedException {
        return executeWithParameterTimeout(stmt, Collections.emptyMap(), timeoutMs);
    }

    public ResultSet executeWithParameterTimeout(String stmt,
                                                 Map<String, Object> parameterMap,
                                                 long timeoutMs)
        throws IOErrorException, AuthFailedException, BindSpaceFailedException {
        if (timeoutMs <= 0) {
            throw new IllegalArgumentException("timeout should be a positive number");
        }
        stmtCheck(stmt);
        checkSessionPool();
        NebulaSession nebulaSession = null;
        ResultSet resultSet = null;
        int tryTimes = 0;
        while (tryTimes++ <= retryTimes) {
            try {
                nebulaSession = getSession();
                resultSet = nebulaSession.executeWithParameterTimeout(stmt, parameterMap, timeoutMs);
                if (resultSet.isSucceeded()
                    || resultSet.getErrorCode() == ErrorCode.E_SEMANTIC_ERROR.getValue()
                    || resultSet.getErrorCode() == ErrorCode.E_SYNTAX_ERROR.getValue()
                    || resultSet.getErrorCode() == ErrorCode.E_QUERY_TIMEDOUT.getValue()) {
                    releaseSession(nebulaSession);
                    return resultSet;
                }
                log.warn(String.format("execute error, code: %d, message: %s, retry: %d",
                                       resultSet.getErrorCode(), resultSet.getErrorMessage(),
                                       tryTimes));
                nebulaSession.release();
                sessionList.remove(nebulaSession);
                try {
                    Thread.sleep(intervalTime);
                } catch (InterruptedException ignored) {
                    // ignore
                }
            } catch (ClientServerIncompatibleException e) {
                // will never get here.
            } catch (AuthFailedException | BindSpaceFailedException e) {
                throw e;
            } catch (IOErrorException e) {
                if (nebulaSession != null) {
                    nebulaSession.release();
                    sessionList.remove(nebulaSession);
                }
                if (tryTimes < retryTimes) {
                    log.warn(String.format("execute failed for IOErrorException, message: %s, "
                                               + "retry: %d", e.getMessage(), tryTimes));
                    try {
                        Thread.sleep(intervalTime);
                    } catch (InterruptedException ignored) {
                        // ignore
                    }
                } else {
                    throw e;
                }
            }
        }
        if (nebulaSession != null) {
            nebulaSession.release();
            sessionList.remove(nebulaSession);
        }
        return resultSet;
    }

    public String executeJson(String stmt)
        throws ClientServerIncompatibleException, AuthFailedException,
        IOErrorException, BindSpaceFailedException {
        return executeJsonWithParameter(stmt, Collections.emptyMap());
    }

    public String executeJsonWithParameter(String stmt, Map<String, Object> parameterMap)
        throws ClientServerIncompatibleException, AuthFailedException,
        IOErrorException, BindSpaceFailedException {
        stmtCheck(stmt);
        checkSessionPool();
        NebulaSession nebulaSession = getSession();
        String result;
        try {
            result = nebulaSession.executeJsonWithParameter(stmt, parameterMap);
            if (isSessionErrorForJson(result)) {
                sessionList.remove(nebulaSession);
                nebulaSession = getSession();
                result = nebulaSession.executeJsonWithParameter(stmt, parameterMap);
            }
        } catch (IOErrorException e) {
            if (nebulaSession != null) {
                nebulaSession.release();
                sessionList.remove(nebulaSession);
            }
            throw e;
        }
        releaseSession(nebulaSession);
        return result;
    }

    public void close() {
        if (isClosed.get()) {
            return;
        }
        if (isClosed.compareAndSet(false, true)) {
            for (NebulaSession nebulaSession : sessionList) {
                nebulaSession.release();
            }
            sessionList.clear();
            if (!healthCheckSchedule.isShutdown()) {
                healthCheckSchedule.shutdown();
            }
            if (!sessionQueueMaintainSchedule.isShutdown()) {
                sessionQueueMaintainSchedule.shutdown();
            }
        }
    }

    public boolean isActive() {
        return hasInit.get();
    }

    public boolean isClosed() {
        return isClosed.get();
    }

    public int getSessionNums() {
        return sessionList.size();
    }

    public int getIdleSessionNums() {
        return idleSessionSize.get();
    }

    private void releaseSession(NebulaSession nebulaSession) {
        nebulaSession.isUsedAndSetIdle();
        idleSessionSize.incrementAndGet();
    }

    private void checkSession() {
        for (NebulaSession nebulaSession : sessionList) {
            if (nebulaSession.isIdleAndSetUsed()) {
                try {
                    idleSessionSize.decrementAndGet();
                    nebulaSession.execute("RETURN 1");
                    nebulaSession.isUsedAndSetIdle();
                    idleSessionSize.incrementAndGet();
                } catch (IOErrorException e) {
                    log.error("session ping error, {}, remove current session.", e.getMessage());
                    nebulaSession.release();
                    sessionList.remove(nebulaSession);
                }
            }
        }
    }

    private void updateSessionQueue() {
        if (idleSessionSize.get() > minSessionSize) {
            synchronized (this) {
                for (NebulaSession nebulaSession : sessionList) {
                    if (nebulaSession.isIdle()) {
                        nebulaSession.release();
                        sessionList.remove(nebulaSession);
                        if (idleSessionSize.decrementAndGet() <= minSessionSize) {
                            break;
                        }
                    }
                }
            }
        }
    }

    private NebulaSession createSessionObject(SessionState state)
        throws ClientServerIncompatibleException, AuthFailedException,
        IOErrorException, BindSpaceFailedException {
        com.vesoft.nebula.driver.graph.net.NebulaClient client = buildClient();

        NebulaSession nebulaSession = new NebulaSession(client, state);
        ResultSet result;
        try {
            result = nebulaSession.execute(
                String.format("SESSION SET GRAPH \"%s\"", spaceName));
        } catch (IOErrorException e) {
            log.error("binding graph failed,", e);
            nebulaSession.release();
            throw new BindSpaceFailedException("binding graph failed:" + e.getMessage());
        }
        if (!result.isSucceeded()) {
            nebulaSession.release();
            throw new BindSpaceFailedException(result.getErrorMessage());
        }
        sessionList.add(nebulaSession);
        return nebulaSession;
    }

    private com.vesoft.nebula.driver.graph.net.NebulaClient buildClient()
        throws AuthFailedException, IOErrorException {
        StringBuilder sb = new StringBuilder();
        for (HostAddress address : sessionPoolConfig.getGraphAddressList()) {
            if (sb.length() > 0) {
                sb.append(',');
            }
            sb.append(address.toString());
        }
        com.vesoft.nebula.driver.graph.net.NebulaClient.Builder builder =
            com.vesoft.nebula.driver.graph.net.NebulaClient.builder(
                sb.toString(), sessionPoolConfig.getUsername(), sessionPoolConfig.getPassword());
        if (sessionPoolConfig.getTimeout() > 0) {
            builder.withConnectTimeoutMills(sessionPoolConfig.getTimeout());
            builder.withRequestTimeoutMills(sessionPoolConfig.getTimeout());
        }
        applyTls(builder, sessionPoolConfig);
        try {
            return builder.build();
        } catch (com.vesoft.nebula.driver.graph.exception.AuthFailedException e) {
            throw Session.toCompatAuth(e);
        } catch (com.vesoft.nebula.driver.graph.exception.IOErrorException e) {
            throw Session.toCompat(e);
        }
    }

    private void applyTls(com.vesoft.nebula.driver.graph.net.NebulaClient.Builder builder,
                          SessionPoolConfig config) {
        if (!config.isEnableSsl()) {
            return;
        }
        builder.withEnableTls(true);
        SSLParam ssl = config.getSslParam();
        if (ssl == null) {
            builder.withDisableVerifyServerCert(true);
            return;
        }
        if (ssl.isSkipVerifyServer()) {
            builder.withDisableVerifyServerCert(true);
        }
        if (ssl instanceof CASignedSSLParam) {
            CASignedSSLParam ca = (CASignedSSLParam) ssl;
            if (ca.getCaCrtFilePath() != null) {
                builder.withTlsCa(ca.getCaCrtFilePath());
            }
            if (ca.getCrtFilePath() != null && ca.getKeyFilePath() != null) {
                builder.withTlsCert(ca.getCrtFilePath(), ca.getKeyFilePath());
            }
        } else if (ssl instanceof SelfSignedSSLParam) {
            SelfSignedSSLParam self = (SelfSignedSSLParam) ssl;
            builder.withDisableVerifyServerCert(true);
            if (self.getCrtFilePath() != null && self.getKeyFilePath() != null) {
                builder.withTlsCert(self.getCrtFilePath(), self.getKeyFilePath());
            }
        }
    }

    public HostAddress getAddress() {
        List<HostAddress> addresses = sessionPoolConfig.getGraphAddressList();
        int newPos = (pos.getAndIncrement()) % addresses.size();
        return addresses.get(newPos);
    }

    private boolean isSessionError(ResultSet resultSet) {
        return resultSet != null
            && (resultSet.getErrorCode() == ErrorCode.E_SESSION_INVALID.getValue()
            || resultSet.getErrorCode() == ErrorCode.E_SESSION_NOT_FOUND.getValue()
            || resultSet.getErrorCode() == ErrorCode.E_SESSION_TIMEOUT.getValue());
    }

    private boolean isSessionErrorForJson(String result) {
        if (result == null) {
            return true;
        }
        int code = JSON.parseObject(result).getJSONArray("errors")
            .getJSONObject(0).getIntValue("code");
        return code == ErrorCode.E_SESSION_INVALID.getValue()
            || code == ErrorCode.E_SESSION_NOT_FOUND.getValue()
            || code == ErrorCode.E_SESSION_TIMEOUT.getValue();
    }

    private void checkSessionPool() {
        if (!hasInit.get()) {
            throw new RuntimeException("The SessionPool has not been initialized, "
                                           + "please call init() first.");
        }
        if (isClosed.get()) {
            throw new RuntimeException("The SessionPool has been closed.");
        }
    }

    private void stmtCheck(String stmt) {
        if (stmt == null || stmt.trim().isEmpty()) {
            throw new IllegalArgumentException("statement is null.");
        }
        String trimmed = stmt.trim();
        if (trimmed.toLowerCase().startsWith("use ")) {
            throw new IllegalArgumentException("`USE SPACE`/`USE GRAPH` alone is forbidden.");
        }
        if (trimmed.toLowerCase().startsWith("session set graph")) {
            throw new IllegalArgumentException("`SESSION SET GRAPH` alone is forbidden.");
        }
    }
}

/* Copyright (c) 2022 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.client.graph;

import com.alibaba.fastjson.JSON;
import com.vesoft.nebula.ErrorCode;
import com.vesoft.nebula.Value;
import com.vesoft.nebula.client.graph.data.HostAddress;
import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.exception.AuthFailedException;
import com.vesoft.nebula.client.graph.exception.BindSpaceFailedException;
import com.vesoft.nebula.client.graph.exception.ClientServerIncompatibleException;
import com.vesoft.nebula.client.graph.exception.IOErrorException;
import com.vesoft.nebula.client.graph.net.AuthResult;
import com.vesoft.nebula.client.graph.net.Session;
import com.vesoft.nebula.client.graph.net.SessionState;
import com.vesoft.nebula.client.graph.net.SyncConnection;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
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
    private final String useSpace;


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
        useSpace = "USE `" + spaceName + "`;";
        init();
    }


    /**
     * return an idle session
     */
    private synchronized NebulaSession getSession() throws ClientServerIncompatibleException,
            AuthFailedException, IOErrorException, BindSpaceFailedException {
        int retry = sessionPoolConfig.getRetryConnectTimes();
        while (retry-- >= 0) {
            // if there are idle sessions, get session from queue
            if (idleSessionSize.get() > 0) {
                for (NebulaSession nebulaSession : sessionList) {
                    if (nebulaSession.isIdleAndSetUsed()) {
                        idleSessionSize.decrementAndGet();
                        return nebulaSession;
                    }
                }
            }
            // if session size is less than max size, get session from pool
            if (sessionList.size() < maxSessionSize) {
                return createSessionObject(SessionState.USED);
            }
            // there's no available session, wait for SessionPoolConfig.getWaitTime and re-get
            try {
                Thread.sleep(sessionPoolConfig.getWaitTime());
            } catch (InterruptedException e) {
                log.error("getSession error when wait for idle sessions, ", e);
                throw new RuntimeException(e);
            }
        }

        // if session size is equal to max size and no idle session here, throw exception
        throw new RuntimeException("no extra session available");
    }


    /**
     * init the SessionPool
     * this function is moved into SessionPool's constructor, no need to call it manually.
     */
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
                log.error("SessionPool init failed. ");
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


    /**
     * Execute the nGql sentence.
     *
     * @param stmt The nGql sentence.
     *             such as insert ngql `INSERT VERTEX person(name) VALUES "Tom":("Tom");`
     * @return The ResultSet
     */
    public ResultSet execute(String stmt) throws IOErrorException,
            ClientServerIncompatibleException, AuthFailedException, BindSpaceFailedException {
        stmtCheck(stmt);
        checkSessionPool();
        NebulaSession nebulaSession = null;
        ResultSet resultSet = null;
        int tryTimes = 0;
        while (tryTimes++ <= retryTimes) {
            try {
                nebulaSession = getSession();
                resultSet = nebulaSession.execute(stmt);
                if (resultSet.isSucceeded()
                        || resultSet.getErrorCode() == ErrorCode.E_SEMANTIC_ERROR.getValue()
                        || resultSet.getErrorCode() == ErrorCode.E_SYNTAX_ERROR.getValue()
                        || resultSet.getErrorCode() == ErrorCode.E_QUERY_TIMEDOUT.getValue()) {
                    releaseSession(nebulaSession);
                    return resultSet;
                }
                log.warn(String.format("execute error, code: %d, message: %s, retry: %d",
                        resultSet.getErrorCode(), resultSet.getErrorMessage(), tryTimes));
                nebulaSession.release();
                sessionList.remove(nebulaSession);
                try {
                    Thread.sleep(intervalTime);
                } catch (InterruptedException interruptedException) {
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
                    } catch (InterruptedException interruptedException) {
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


    /**
     * Execute the nGql sentence with parameter
     *
     * @param stmt         The nGql sentence.
     * @param parameterMap The nGql parameter map
     * @return The ResultSet
     */
    public ResultSet execute(String stmt, Map<String, Object> parameterMap)
            throws ClientServerIncompatibleException, AuthFailedException,
            IOErrorException, BindSpaceFailedException {
        stmtCheck(stmt);
        checkSessionPool();
        NebulaSession nebulaSession = getSession();
        ResultSet resultSet;
        try {
            resultSet = nebulaSession.executeWithParameter(stmt, parameterMap);

            // re-execute for session error
            if (isSessionError(resultSet)) {
                sessionList.remove(nebulaSession);
                nebulaSession = getSession();
                resultSet = nebulaSession.executeWithParameter(stmt, parameterMap);
            }
        } catch (IOErrorException e) {
            useSpace(nebulaSession, null);
            throw e;
        }

        useSpace(nebulaSession, resultSet);
        return resultSet;
    }

    public ResultSet executeWithTimeout(String stmt,
                                        long timeoutMs)
            throws IOErrorException, AuthFailedException, BindSpaceFailedException {
        return executeWithParameterTimeout(stmt,
                                           (Map<String, Object>) Collections.EMPTY_MAP,
                                           timeoutMs);
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
                resultSet = nebulaSession.executeWithParameterTimeout(stmt,
                                                                      parameterMap,
                                                                      timeoutMs);
                if (resultSet.isSucceeded()
                        || resultSet.getErrorCode() == ErrorCode.E_SEMANTIC_ERROR.getValue()
                        || resultSet.getErrorCode() == ErrorCode.E_SYNTAX_ERROR.getValue()) {
                    releaseSession(nebulaSession);
                    return resultSet;
                }
                log.warn(String.format("execute error, code: %d, message: %s, retry: %d",
                                       resultSet.getErrorCode(),
                                       resultSet.getErrorMessage(),
                                       tryTimes));
                nebulaSession.release();
                sessionList.remove(nebulaSession);
                try {
                    Thread.sleep(intervalTime);
                } catch (InterruptedException interruptedException) {
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
                    } catch (InterruptedException interruptedException) {
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
        return executeJsonWithParameter(stmt, (Map<String, Object>) Collections.EMPTY_MAP);
    }

    public String executeJsonWithParameter(String stmt,
                                           Map<String, Object> parameterMap)
            throws ClientServerIncompatibleException, AuthFailedException,
            IOErrorException, BindSpaceFailedException {
        stmtCheck(stmt);
        checkSessionPool();
        NebulaSession nebulaSession = getSession();
        String result;
        try {
            result = nebulaSession.executeJsonWithParameter(stmt, parameterMap);

            // re-execute for session error
            if (isSessionErrorForJson(result)) {
                sessionList.remove(nebulaSession);
                nebulaSession = getSession();
                result = nebulaSession.executeJsonWithParameter(stmt, parameterMap);
            }
        } catch (IOErrorException e) {
            if (e.getType() == IOErrorException.E_CONNECT_BROKEN) {
                sessionList.remove(nebulaSession);
                nebulaSession = getSession();
                result = nebulaSession.executeJsonWithParameter(stmt, parameterMap);
                return result;
            }
            useSpace(nebulaSession, null);
            throw e;
        }

        useSpaceForJson(nebulaSession, result);
        return result;
    }


    /**
     * close the session pool
     */
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


    /**
     * if the SessionPool has been initialized
     */
    public boolean isActive() {
        return hasInit.get();
    }

    /**
     * if the SessionPool is closed
     */
    public boolean isClosed() {
        return isClosed.get();
    }

    /**
     * get the number of all Session
     */
    public int getSessionNums() {
        return sessionList.size();
    }

    /**
     * get the number of idle Session
     */
    public int getIdleSessionNums() {
        return idleSessionSize.get();
    }


    /**
     * release the NebulaSession when finished the execution.
     */
    private void releaseSession(NebulaSession nebulaSession) {
        nebulaSession.isUsedAndSetIdle();
        idleSessionSize.incrementAndGet();
    }


    /**
     * check if session is valid, if session is invalid, remove it.
     */
    private void checkSession() {
        for (NebulaSession nebulaSession : sessionList) {
            if (nebulaSession.isIdleAndSetUsed()) {
                try {
                    idleSessionSize.decrementAndGet();
                    nebulaSession.execute("YIELD 1");
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

    /**
     * update the session queue according to minSessionSize
     */
    private void updateSessionQueue() {
        // remove the idle sessions
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

    /**
     * create a {@link NebulaSession} with specified state
     *
     * @param state {@link SessionState}
     * @return NebulaSession
     */
    private NebulaSession createSessionObject(SessionState state)
            throws ClientServerIncompatibleException, AuthFailedException,
            IOErrorException, BindSpaceFailedException {
        SyncConnection connection = new SyncConnection();
        int tryConnect = sessionPoolConfig.getGraphAddressList().size();
        // reconnect with all available address
        while (tryConnect-- > 0) {
            try {
                if (sessionPoolConfig.isEnableSsl()) {
                    connection.open(getAddress(), sessionPoolConfig.getTimeout(),
                            sessionPoolConfig.getSslParam(),
                            sessionPoolConfig.isUseHttp2(),
                            sessionPoolConfig.getCustomHeaders());
                } else {
                    connection.open(getAddress(), sessionPoolConfig.getTimeout(),
                            sessionPoolConfig.isUseHttp2(),
                            sessionPoolConfig.getCustomHeaders());
                }
                break;
            } catch (Exception e) {
                if (tryConnect == 0 || !reconnect) {
                    throw e;
                } else {
                    log.warn("connect failed, " + e.getMessage());
                }
            }
        }

        AuthResult authResult;
        try {
            authResult = connection.authenticate(sessionPoolConfig.getUsername(),
                    sessionPoolConfig.getPassword());
        } catch (AuthFailedException e) {
            log.error(e.getMessage());
            if (e.getMessage().toLowerCase().contains("user not exist")
                    || e.getMessage().toLowerCase().contains("invalid password")) {
                // close the session pool
                close();
            } else {
                // just close the connection
                connection.close();
            }
            throw e;
        }

        NebulaSession nebulaSession = new NebulaSession(connection, authResult.getSessionId(),
                authResult.getTimezoneOffset(), state);
        ResultSet result = null;
        try {
            result = nebulaSession.execute(useSpace);
        } catch (IOErrorException e) {
            log.error("binding space failed,", e);
            nebulaSession.release();
            throw new BindSpaceFailedException("binding space failed:" + e.getMessage());
        }
        if (!result.isSucceeded()) {
            nebulaSession.release();
            throw new BindSpaceFailedException(result.getErrorMessage());
        }
        sessionList.add(nebulaSession);
        return nebulaSession;
    }


    public HostAddress getAddress() {
        List<HostAddress> addresses = sessionPoolConfig.getGraphAddressList();
        int newPos = (pos.getAndIncrement()) % addresses.size();
        return addresses.get(newPos);
    }

    /**
     * execute the "USE SPACE_NAME" when session's space changed.
     *
     * @param nebulaSession NebulaSession
     * @param resultSet     execute response
     */
    private void useSpace(NebulaSession nebulaSession, ResultSet resultSet)
            throws IOErrorException {
        if (resultSet == null) {
            nebulaSession.release();
            sessionList.remove(nebulaSession);
            return;
        }
        // space has been drop, close the SessionPool
        if (resultSet.getSpaceName().trim().isEmpty()) {
            log.warn("space {} has been drop, close the SessionPool.", spaceName);
            close();
            return;
        }
        // re-bind the configured spaceName, if bind failed, then remove this session.
        if (!spaceName.equals(resultSet.getSpaceName())) {
            ResultSet switchSpaceResult = nebulaSession.execute(useSpace);
            if (!switchSpaceResult.isSucceeded()) {
                log.warn("Bind Space failed, {}", switchSpaceResult.getErrorMessage());
                nebulaSession.release();
                sessionList.remove(nebulaSession);
                return;
            }
        }
        releaseSession(nebulaSession);
    }

    /**
     * execute the "USE SPACE_NAME" when session's space changed for Json interface
     *
     * @param nebulaSession NebulaSession
     * @param result        execute response
     */
    private void useSpaceForJson(NebulaSession nebulaSession, String result)
            throws IOErrorException {
        String responseSpaceName =
                (String) JSON.parseObject(result).getJSONArray("results")
                        .getJSONObject(0).get("spaceName");
        if (!spaceName.equals(responseSpaceName)) {
            nebulaSession.execute(useSpace);
        }
        releaseSession(nebulaSession);
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

        if (stmt.trim().toLowerCase().startsWith("use") && stmt.trim().split(" ").length == 2) {
            throw new IllegalArgumentException("`USE SPACE` alone is forbidden.");
        }
    }
}

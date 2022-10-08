/* Copyright (c) 2022 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.client.graph;

import com.alibaba.fastjson.JSON;
import com.vesoft.nebula.ErrorCode;
import com.vesoft.nebula.client.graph.data.HostAddress;
import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.exception.AuthFailedException;
import com.vesoft.nebula.client.graph.exception.BindSpaceFailedException;
import com.vesoft.nebula.client.graph.exception.ClientServerIncompatibleException;
import com.vesoft.nebula.client.graph.exception.IOErrorException;
import com.vesoft.nebula.client.graph.exception.NotValidConnectionException;
import com.vesoft.nebula.client.graph.net.AuthResult;
import com.vesoft.nebula.client.graph.net.SessionState;
import com.vesoft.nebula.client.graph.net.SyncConnection;
import java.io.Serializable;
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
    private final int delayTime;
    private final String spaceName;


    public SessionPool(SessionPoolConfig poolConfig) {
        this.sessionPoolConfig = poolConfig;
        this.minSessionSize = poolConfig.getMinSessionSize();
        this.maxSessionSize = poolConfig.getMaxSessionSize();
        this.delayTime = poolConfig.getIdleTime();
        this.spaceName = poolConfig.getSpaceName();
    }


    /**
     * return an idle session
     */
    private synchronized NebulaSession getSession() throws ClientServerIncompatibleException,
            AuthFailedException, NotValidConnectionException, IOErrorException,
            BindSpaceFailedException {
        int retry = 1;
        while (retry-- > 0) {
            // if there are idle sessions, get session from queue
            if (idleSessionSize.get() > 0) {
                for (NebulaSession nebulaSession : sessionList) {
                    if (nebulaSession.isIdle()) {
                        nebulaSession.setUsed();
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
     */
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
                return false;
            }
        }
        healthCheckSchedule.scheduleAtFixedRate(this::checkSession, 0, delayTime, TimeUnit.SECONDS);
        sessionQueueMaintainSchedule.scheduleAtFixedRate(this::updateSessionQueue, 0, delayTime,
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
    public ResultSet execute(String stmt) throws IOErrorException, NotValidConnectionException,
            ClientServerIncompatibleException, AuthFailedException, BindSpaceFailedException {
        stmtCheck(stmt);
        checkSessionPool();
        NebulaSession nebulaSession = getSession();
        ResultSet resultSet;
        try {
            resultSet = nebulaSession.execute(stmt);


            // re-execute for session error
            if (isSessionError(resultSet)) {
                sessionList.remove(nebulaSession);
                nebulaSession = getSession();
                resultSet = nebulaSession.execute(stmt);
            }
        } catch (IOErrorException e) {
            useSpace(nebulaSession, null);
            throw e;
        }
        useSpace(nebulaSession, resultSet);
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
            NotValidConnectionException, IOErrorException, BindSpaceFailedException {
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


    /**
     * close the session pool
     */
    public void close() {
        if (isClosed.get()) {
            return;
        }

        isClosed.compareAndSet(false, true);
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
    private synchronized void releaseSession(NebulaSession nebulaSession) {
        nebulaSession.setIdle();
        idleSessionSize.incrementAndGet();
    }


    /**
     * check if session is valid, if session is invalid, remove it.
     */
    private void checkSession() {
        for (NebulaSession nebulaSession : sessionList) {
            if (nebulaSession.isIdle()) {
                try {
                    nebulaSession.execute("YIELD 1");
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

    /**
     * create a {@link NebulaSession} with specified state
     *
     * @param state {@link SessionState}
     * @return NebulaSession
     */
    private NebulaSession createSessionObject(SessionState state)
            throws ClientServerIncompatibleException, AuthFailedException,
            NotValidConnectionException, IOErrorException, BindSpaceFailedException {
        SyncConnection connection = new SyncConnection();
        connection.open(getAddress(), sessionPoolConfig.getTimeout());
        AuthResult authResult = null;
        try {
            authResult = connection.authenticate(sessionPoolConfig.getUsername(),
                    sessionPoolConfig.getPassword());
        } catch (AuthFailedException e) {
            log.error(e.getMessage());
            close();
            throw e;
        }

        NebulaSession nebulaSession = new NebulaSession(connection, authResult.getSessionId(),
                authResult.getTimezoneOffset(), state);
        ResultSet result = nebulaSession.execute("USE " + spaceName);
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
            releaseSession(nebulaSession);
            return;
        }
        // space has been drop, close the SessionPool
        if (resultSet.getSpaceName().isEmpty()) {
            log.warn("space {} has been drop, close the SessionPool.", spaceName);
            close();
            return;
        }
        // re-bind the configured spaceName, if bind failed, then remove this session.
        if (!spaceName.equals(resultSet.getSpaceName())) {
            ResultSet switchSpaceResult = nebulaSession.execute("USE " + spaceName);
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
            nebulaSession.execute("USE " + spaceName);
        }
        releaseSession(nebulaSession);
    }

    private boolean isSessionError(ResultSet resultSet) {
        return resultSet != null
                && (resultSet.getErrorCode() == ErrorCode.E_SESSION_INVALID.getValue()
                || resultSet.getErrorCode() == ErrorCode.E_SESSION_NOT_FOUND.getValue()
                || resultSet.getErrorCode() == ErrorCode.E_SESSION_TIMEOUT.getValue());
    }

    private boolean isSessionErrorForJson(String resultSet) {
        if (resultSet == null || "".equals(resultSet)) {
            return false;
        }
        long errorCode =
                (Long) JSON.parseObject(resultSet).getJSONArray("errors")
                        .getJSONObject(0).get("code");
        return errorCode == ErrorCode.E_SESSION_INVALID.getValue()
                || errorCode == ErrorCode.E_SESSION_NOT_FOUND.getValue()
                || errorCode == ErrorCode.E_SESSION_TIMEOUT.getValue();
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

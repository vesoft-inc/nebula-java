/* Copyright (c) 2022 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.client.graph;

import com.alibaba.fastjson.JSON;
import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.client.graph.exception.AuthFailedException;
import com.vesoft.nebula.client.graph.exception.ClientServerIncompatibleException;
import com.vesoft.nebula.client.graph.exception.IOErrorException;
import com.vesoft.nebula.client.graph.exception.NotValidConnectionException;
import com.vesoft.nebula.client.graph.net.NebulaPool;
import com.vesoft.nebula.client.graph.net.Session;
import com.vesoft.nebula.client.graph.net.SessionState;
import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionPool implements Serializable {

    private static final long serialVersionUID = 6051248334277617891L;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final int delayTime = 60;
    private final ScheduledExecutorService healthCheckSchedule =
            Executors.newScheduledThreadPool(1);
    private final ScheduledExecutorService sessionQueueMaintainSchedule =
            Executors.newScheduledThreadPool(1);

    public CopyOnWriteArrayList<NebulaSession> sessionQueue = new CopyOnWriteArrayList<>();
    public static AtomicInteger idleSessionSize;

    private final SessionPoolConfig sessionPoolConfig;
    private final NebulaPool nebulaPool;
    private int minSessionSize;
    private int maxSessionSize;
    private String spaceName;

    public SessionPool(SessionPoolConfig poolConfig) {
        this.sessionPoolConfig = poolConfig;
        this.minSessionSize = poolConfig.getMinSessionSize();
        this.maxSessionSize = poolConfig.getMaxSessionSize();
        this.spaceName = poolConfig.getSpaceName();
        nebulaPool = new NebulaPool();
    }


    /**
     * return an idle session
     */
    private synchronized NebulaSession getSession() throws ClientServerIncompatibleException,
            AuthFailedException, NotValidConnectionException, IOErrorException {
        int retry = 1;
        while (retry-- > 0) {
            // if there are idle sessions, get session from queue
            if (idleSessionSize.get() > 0) {
                for (NebulaSession nebulaSession : sessionQueue) {
                    if (nebulaSession.isIdle()) {
                        nebulaSession.setUsed();
                        idleSessionSize.decrementAndGet();
                        return nebulaSession;
                    }
                }
            }
            // if session size is less than max size, get session from pool
            if (sessionQueue.size() < maxSessionSize) {
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
        // init the nebulapool
        NebulaPoolConfig nebulaPoolConfig = NebulaPoolConfig.copy(sessionPoolConfig);
        try {
            if (!nebulaPool.init(sessionPoolConfig.getGraphAddressList(), nebulaPoolConfig)) {
                log.error("NebulaPool init failed.");
                return false;
            }
        } catch (UnknownHostException e) {
            log.error("NebulaPool init error.", e);
            return false;
        }

        // generate sessions
        while (sessionQueue.size() < minSessionSize) {
            try {
                createSessionObject(SessionState.IDLE);
                idleSessionSize.incrementAndGet();
            } catch (Exception e) {
                log.error("SessionPool init failed.", e);
                return false;
            }
        }
        healthCheckSchedule.scheduleAtFixedRate(this::checkSession, 0, delayTime, TimeUnit.SECONDS);
        sessionQueueMaintainSchedule.scheduleAtFixedRate(this::updateSessionQueue, 0, delayTime,
                TimeUnit.SECONDS);
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
            ClientServerIncompatibleException, AuthFailedException {
        NebulaSession nebulaSession = getSession();
        ResultSet resultSet = nebulaSession.getSession().execute(stmt);
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
            NotValidConnectionException, IOErrorException {
        NebulaSession nebulaSession = getSession();
        ResultSet resultSet = nebulaSession.getSession().executeWithParameter(stmt, parameterMap);
        useSpace(nebulaSession, resultSet);
        return resultSet;
    }


    /**
     * Execute the nGql sentence for json result
     *
     * @param stmt The nGql sentence.
     * @return The json result
     */
    public String executeJson(String stmt) throws ClientServerIncompatibleException,
            AuthFailedException, NotValidConnectionException, IOErrorException {
        NebulaSession nebulaSession = getSession();
        String result = nebulaSession.getSession().executeJson(stmt);
        useSpaceForJson(nebulaSession, result);
        return result;
    }


    /**
     * close the session pool
     */
    public void close() {
        for (NebulaSession nebulaSession : sessionQueue) {
            nebulaSession.getSession().release();
        }
        sessionQueue.clear();
        healthCheckSchedule.shutdown();
        sessionQueueMaintainSchedule.shutdown();
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
        for (NebulaSession nebulaSession : sessionQueue) {
            if (nebulaSession.isIdle()) {
                boolean isOk = nebulaSession.getSession().pingSession();
                if (!isOk) {
                    nebulaSession.getSession().release();
                    sessionQueue.remove(nebulaSession);
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
            for (NebulaSession nebulaSession : sessionQueue) {
                if (nebulaSession.isIdle()) {
                    sessionQueue.remove(nebulaSession);
                    if (idleSessionSize.decrementAndGet() <= minSessionSize) {
                        break;
                    }
                }
            }
        }
        // add more sessions to minSessionSize
        if (sessionQueue.size() < minSessionSize) {
            try {
                createSessionObject(SessionState.IDLE);
                idleSessionSize.incrementAndGet();
            } catch (Exception e) {
                log.warn("update Session pool failed, {}", e.getMessage());
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
            NotValidConnectionException, IOErrorException {
        Session session = nebulaPool.getSession(sessionPoolConfig.getUsername(),
                sessionPoolConfig.getPassword(), sessionPoolConfig.isReConnect());
        session.execute("USE " + spaceName);
        NebulaSession nebulaSession = new NebulaSession(session, state);
        sessionQueue.add(nebulaSession);
        return nebulaSession;
    }


    /**
     * execute the "USE SPACE_NAME" when session's space changed.
     *
     * @param nebulaSession NebulaSession
     * @param resultSet     execute response
     */
    private void useSpace(NebulaSession nebulaSession, ResultSet resultSet)
            throws IOErrorException {
        if (!spaceName.equals(resultSet.getSpaceName())) {
            nebulaSession.getSession().execute("USE " + spaceName);
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
            nebulaSession.getSession().execute("USE " + spaceName);
        }
        releaseSession(nebulaSession);
    }

}

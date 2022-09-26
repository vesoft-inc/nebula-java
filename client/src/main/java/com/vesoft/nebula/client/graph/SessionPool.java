/* Copyright (c) 2022 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.client.graph;

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
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionPool implements Serializable {

    private static final long serialVersionUID = 6051248334277617891L;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public LinkedBlockingQueue<NebulaSession> sessionQueue = new LinkedBlockingQueue<>();
    public static AtomicInteger idleSessionSize;

    private final SessionPoolConfig sessionPoolConfig;
    private final NebulaPool nebulaPool;

    public SessionPool(SessionPoolConfig poolConfig) {
        this.sessionPoolConfig = poolConfig;
        nebulaPool = new NebulaPool();
    }


    /**
     * return an idle session
     */
    private synchronized Session getSession() throws ClientServerIncompatibleException,
            AuthFailedException, NotValidConnectionException, IOErrorException {
        // if there are idle sessions, get session from queue
        if (idleSessionSize.get() > 0) {
            for (NebulaSession nebulaSession : sessionQueue) {
                if (nebulaSession.isIdle()) {
                    nebulaSession.setUsed();
                    idleSessionSize.decrementAndGet();
                    return nebulaSession.getSession();
                }
            }
        }
        // if session size is less than max size, get session from pool
        if (sessionQueue.size() < sessionPoolConfig.getMaxSessionSize()) {
            Session session = nebulaPool.getSession(sessionPoolConfig.getUsername(),
                    sessionPoolConfig.getPassword(), sessionPoolConfig.isReConnect());
            session.execute("USE " + sessionPoolConfig.getSpaceName());
            NebulaSession nebulaSession = new NebulaSession(session, SessionState.USED);
            sessionQueue.offer(nebulaSession);
            return nebulaSession.getSession();
        }

        // if session size is equal to max size and no idle session here, throw exception
        throw new RuntimeException("no extra session available");
    }


    /**
     * release the NebulaSession when finished the execution.
     */
    private synchronized void releaseSession(NebulaSession nebulaSession) {
        nebulaSession.setIdle();
        idleSessionSize.incrementAndGet();
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
        while (sessionQueue.size() < sessionPoolConfig.getMinSessionSize()) {
            try {
                Session session = nebulaPool.getSession(sessionPoolConfig.getUsername(),
                        sessionPoolConfig.getPassword(), sessionPoolConfig.isReConnect());
                NebulaSession nebulaSession = new NebulaSession(session, SessionState.IDLE);
                sessionQueue.offer(nebulaSession);
                idleSessionSize.incrementAndGet();
            } catch (Exception e) {
                log.error("SessionPool init failed.", e);
                return false;
            }
        }
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
        Session session = getSession();
        return session.execute(stmt);
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
        return getSession().executeWithParameter(stmt, parameterMap);
    }

    /**
     * Execute the nGql sentence for json result
     *
     * @param stmt The nGql sentence.
     * @return The json result
     */
    public String executeJson(String stmt) throws ClientServerIncompatibleException,
            AuthFailedException, NotValidConnectionException, IOErrorException {
        return getSession().executeJson(stmt);
    }

    // TODO use space when execution response's space name is not equal to config's space name.

    /**
     * close the session pool
     */
    public void close() {
        for (NebulaSession nebulaSession : sessionQueue) {
            nebulaSession.getSession().release();
        }
    }

}

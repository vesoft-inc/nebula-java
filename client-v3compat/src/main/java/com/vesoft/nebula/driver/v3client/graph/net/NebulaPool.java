/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.v3client.graph.net;

import com.vesoft.nebula.driver.v3client.graph.NebulaPoolConfig;
import com.vesoft.nebula.driver.v3client.graph.data.CASignedSSLParam;
import com.vesoft.nebula.driver.v3client.graph.data.HostAddress;
import com.vesoft.nebula.driver.v3client.graph.data.SSLParam;
import com.vesoft.nebula.driver.v3client.graph.data.SelfSignedSSLParam;
import com.vesoft.nebula.driver.v3client.graph.exception.AuthFailedException;
import com.vesoft.nebula.driver.v3client.graph.exception.ClientServerIncompatibleException;
import com.vesoft.nebula.driver.v3client.graph.exception.IOErrorException;
import com.vesoft.nebula.driver.v3client.graph.exception.InvalidConfigException;
import com.vesoft.nebula.driver.v3client.graph.exception.NotValidConnectionException;
import java.io.Serializable;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A pool of connections/sessions, matching the v3 client's {@code NebulaPool} API.
 *
 * <p>The v3 pool is credential-less at init time and authenticates per {@link #getSession}; the v5
 * driver bakes credentials into its client pool. This adapter therefore lazily builds one v5 pool
 * per distinct username on the first {@code getSession} call.
 */
public class NebulaPool implements Serializable {

    private static final long serialVersionUID = 6226487268001127885L;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private List<HostAddress> addresses;
    private NebulaPoolConfig config;

    private final Map<String, com.vesoft.nebula.driver.graph.net.NebulaPool> pools =
        new HashMap<>();

    private final AtomicBoolean hasInit = new AtomicBoolean(false);
    private final AtomicBoolean isClosed = new AtomicBoolean(false);

    private void checkConfig(NebulaPoolConfig config) {
        if (config.getIdleTime() < 0) {
            throw new InvalidConfigException(
                "Config idleTime:" + config.getIdleTime() + " is illegal");
        }
        if (config.getMaxConnSize() <= 0) {
            throw new InvalidConfigException(
                "Config maxConnSize:" + config.getMaxConnSize() + " is illegal");
        }
        if (config.getMinConnSize() < 0 || config.getMinConnSize() > config.getMaxConnSize()) {
            throw new InvalidConfigException(
                "Config minConnSize:" + config.getMinConnSize() + " is illegal");
        }
        if (config.getTimeout() < 0) {
            throw new InvalidConfigException(
                "Config timeout:" + config.getTimeout() + " is illegal");
        }
        if (config.getWaitTime() < 0) {
            throw new InvalidConfigException(
                "Config waitTime:" + config.getWaitTime() + " is illegal");
        }
        if (config.getMinClusterHealthRate() < 0) {
            throw new InvalidConfigException(
                "Config minClusterHealthRate:" + config.getMinClusterHealthRate() + " is illegal");
        }
    }

    /**
     * @param addresses the graphd services addresses
     * @param config the config for the pool
     * @return {@code true} if the config is valid. The v5 driver performs the actual server
     *     health check lazily when the first session is requested.
     * @throws UnknownHostException if host address is illegal
     * @throws InvalidConfigException if config is illegal
     */
    public boolean init(List<HostAddress> addresses, NebulaPoolConfig config)
        throws UnknownHostException, InvalidConfigException {
        checkInit();
        checkConfig(config);
        this.addresses = new ArrayList<>(addresses);
        this.config = config;
        hasInit.set(true);
        return true;
    }

    /**
     * close the pool, all connections will be closed.
     */
    public void close() {
        if (isClosed.compareAndSet(false, true)) {
            synchronized (pools) {
                for (com.vesoft.nebula.driver.graph.net.NebulaPool pool : pools.values()) {
                    pool.close();
                }
                pools.clear();
            }
        }
    }

    /**
     * get a session from the pool.
     *
     * @param userName the userName to authenticate with graphd
     * @param password the password to authenticate with graphd
     * @param reconnect whether to retry after the connection is disconnected
     * @return Session
     * @throws NotValidConnectionException if get connection failed
     * @throws IOErrorException if an IO error occurs
     * @throws AuthFailedException if authentication failed
     */
    public Session getSession(String userName, String password, boolean reconnect)
        throws NotValidConnectionException, IOErrorException, AuthFailedException,
        ClientServerIncompatibleException {
        checkNoInitAndClosed();
        com.vesoft.nebula.driver.graph.net.NebulaPool pool = getOrCreatePool(userName, password);
        com.vesoft.nebula.driver.graph.net.NebulaClient client;
        try {
            client = pool.getClient();
        } catch (Exception e) {
            throw new NotValidConnectionException(e.getMessage());
        }
        return new Session(client, pool, reconnect);
    }

    public int getActiveConnNum() {
        checkNoInitAndClosed();
        int total = 0;
        synchronized (pools) {
            for (com.vesoft.nebula.driver.graph.net.NebulaPool pool : pools.values()) {
                total += pool.getActiveSessions();
            }
        }
        return total;
    }

    public int getIdleConnNum() {
        checkNoInitAndClosed();
        int total = 0;
        synchronized (pools) {
            for (com.vesoft.nebula.driver.graph.net.NebulaPool pool : pools.values()) {
                total += pool.getIdleSessions();
            }
        }
        return total;
    }

    public int getWaitersNum() {
        checkNoInitAndClosed();
        int total = 0;
        synchronized (pools) {
            for (com.vesoft.nebula.driver.graph.net.NebulaPool pool : pools.values()) {
                total += pool.getWaiters();
            }
        }
        return total;
    }

    private com.vesoft.nebula.driver.graph.net.NebulaPool getOrCreatePool(String user,
                                                                          String password)
        throws AuthFailedException, IOErrorException {
        synchronized (pools) {
            com.vesoft.nebula.driver.graph.net.NebulaPool pool = pools.get(user);
            if (pool == null) {
                pool = buildPool(user, password);
                pools.put(user, pool);
            }
            return pool;
        }
    }

    private com.vesoft.nebula.driver.graph.net.NebulaPool buildPool(String user, String password)
        throws AuthFailedException, IOErrorException {
        StringBuilder sb = new StringBuilder();
        for (HostAddress address : addresses) {
            if (sb.length() > 0) {
                sb.append(',');
            }
            sb.append(address.toString());
        }
        com.vesoft.nebula.driver.graph.net.NebulaPool.Builder builder =
            com.vesoft.nebula.driver.graph.net.NebulaPool.builder(sb.toString(), user, password);
        builder.withMinClientSize(config.getMinConnSize());
        builder.withMaxClientSize(config.getMaxConnSize());
        if (config.getTimeout() > 0) {
            builder.withConnectTimeoutMills(config.getTimeout());
            builder.withRequestTimeoutMills(config.getTimeout());
        }
        if (config.getIdleTime() > 0) {
            builder.withMinEvictableIdleTimeMillis(config.getIdleTime());
        }
        if (config.getIntervalIdle() > 0) {
            builder.withIdleEvictScheduleMills(config.getIntervalIdle());
        }
        if (config.getWaitTime() > 0) {
            builder.withMaxWaitMills(config.getWaitTime());
        }
        builder.withStrictlyServerHealthy(config.getMinClusterHealthRate() >= 1.0);
        applyTls(builder, config);
        try {
            return builder.build();
        } catch (com.vesoft.nebula.driver.graph.exception.AuthFailedException e) {
            throw Session.toCompatAuth(e);
        } catch (com.vesoft.nebula.driver.graph.exception.IOErrorException e) {
            throw Session.toCompat(e);
        }
    }

    private void applyTls(com.vesoft.nebula.driver.graph.net.NebulaPool.Builder builder,
                          NebulaPoolConfig config) {
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

    private void checkNoInit() {
        if (!hasInit.get()) {
            throw new RuntimeException(
                "The pool has not been initialized, please initialize it first.");
        }
    }

    private void checkInit() {
        if (hasInit.get()) {
            throw new RuntimeException(
                "The pool has already been initialized. "
                    + "Please do not initialize the pool repeatedly.");
        }
    }

    private void checkNoInitAndClosed() {
        checkNoInit();
        if (isClosed.get()) {
            throw new RuntimeException("The pool has closed. Couldn't use again.");
        }
    }
}

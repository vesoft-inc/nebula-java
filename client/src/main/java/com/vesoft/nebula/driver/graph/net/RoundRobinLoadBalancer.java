/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.net;

import com.vesoft.nebula.driver.graph.data.HostAddress;
import com.vesoft.nebula.driver.graph.exception.AuthFailedException;
import com.vesoft.nebula.driver.graph.exception.IOErrorException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoundRobinLoadBalancer implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(RoundRobinLoadBalancer.class);

    private final List<HostAddress> addresses = new ArrayList<>();
    private final boolean           strictlyServerHealthy;

    private final String              userName;
    private final Map<String, Object> authOptions;

    private final AtomicInteger pos = new AtomicInteger(0);

    private boolean enableTls;
    private boolean disableVerifyServerCert;
    private String  tlsCa;
    private String  tlsCert;
    private String  tlsKey;
    private long    connectionTimeout;

    public RoundRobinLoadBalancer(NebulaPool.Builder builder) {
        this.addresses.addAll(builder.address);
        this.strictlyServerHealthy = builder.strictlyServerHealthy;
        this.userName = builder.userName;
        this.authOptions = builder.authOptions;
        this.connectionTimeout = builder.connectTimeoutMills;
        enableTls = builder.enableTls;
        disableVerifyServerCert = builder.disableVerifyServerCert;
        tlsCa = builder.tlsCa;
        tlsCert = builder.tlsCert;
        tlsKey = builder.tlsKey;
    }

    public int addressSize() {
        return addresses.size();
    }

    public HostAddress getAddress() {
        if (pos.get() == Integer.MAX_VALUE) {
            pos.set(0);
        }
        int newPos = (pos.getAndIncrement()) % addresses.size();
        return addresses.get(newPos);
    }


    public boolean ping(HostAddress addr) throws AuthFailedException, IOErrorException {
        NebulaClient client = NebulaClient
            .builder(addr.toString(), userName)
            .withConnectTimeoutMills(connectionTimeout)
            .withAuthOptions(authOptions)
            .withEnableTls(enableTls)
            .withDisableVerifyServerCert(disableVerifyServerCert)
            .withTlsCa(tlsCa)
            .withTlsCert(tlsCert, tlsKey)
            .build();
        client.close();
        return true;
    }

    public void checkServers() throws AuthFailedException, IOErrorException {
        AuthFailedException lastAuthE   = null;
        IOErrorException    lastIOE     = null;
        int                 goodAddress = 0;
        for (HostAddress hostAddress : addresses) {
            try {
                ping(hostAddress);
                goodAddress++;
            } catch (AuthFailedException e) {
                lastAuthE = e;
            } catch (IOErrorException ioe) {
                lastIOE = ioe;
            }
        }
        if (strictlyServerHealthy) {
            if (goodAddress == addressSize()) {
                return;
            }
        } else {
            if (goodAddress >= 1) {
                return;
            }
        }
        if (lastAuthE != null) {
            throw lastAuthE;
        }
        if (lastIOE != null) {
            throw lastIOE;
        }
    }
}

/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */
package com.vesoft.nebula.client.graph.pool;

import com.facebook.thrift.TException;
import com.facebook.thrift.protocol.TCompactProtocol;
import com.facebook.thrift.protocol.TProtocol;
import com.facebook.thrift.transport.TSocket;
import com.facebook.thrift.transport.TTransport;
import com.facebook.thrift.transport.TTransportException;
import com.vesoft.nebula.client.graph.pool.config.NebulaConnectionPoolConfig;
import com.vesoft.nebula.client.graph.pool.entity.LinkDomain;
import com.vesoft.nebula.graph.AuthResponse;
import com.vesoft.nebula.graph.ErrorCode;
import com.vesoft.nebula.graph.GraphService;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * @author huangzhaolai-jk
 * @version 1.0.0
 * @Description NebulaGraphPoolConnectionFactory is used for
 * @Date 2020/3/24 - 11:49
 */
public class NebulaGraphPoolConnectionFactory
        extends BasePooledObjectFactory<NebulaGraphPoolConnection> {

    private final Logger log = LogManager.getLogger(NebulaGraphPoolConnectionFactory.class);

    private NebulaConnectionPoolConfig config;


    public NebulaGraphPoolConnectionFactory(NebulaConnectionPoolConfig config) {
        this.config = config;
    }

    @Override
    public NebulaGraphPoolConnection create() throws Exception {
        if (this.config == null) {
            return null;
        }
        List<LinkDomain> addresses = this.config.getAddresses();
        if (addresses == null || addresses.size() == 0) {
            this.log.error("Connect failed: addresses is empty");
            return null;
        }
        GraphService.Client client;
        TTransport transport;
        Random random = new Random(System.currentTimeMillis());
        int position = random.nextInt(addresses.size());
        LinkDomain linkDomain = addresses.get(position);
        transport = new TSocket(linkDomain.getHost(), linkDomain.getPort(),
                this.config.getTimeout());
        TProtocol protocol = new TCompactProtocol(transport);
        try {
            transport.open();
            client = new GraphService.Client(protocol);
            AuthResponse result = client.authenticate(linkDomain.getUserName(),
                    linkDomain.getPassword());
            if (result.getError_code() == ErrorCode.E_BAD_USERNAME_PASSWORD) {
                this.log.error("User name or password error");
            }
            if (result.getError_code() != ErrorCode.SUCCEEDED) {
                this.log.error(String.format("Connect address %s failed : %s",
                        linkDomain.toString(), result.getError_msg()));
            } else {
                //succeed
                long sessionId = result.getSession_id();
                return new NebulaGraphPoolConnection(client, transport,
                        sessionId, this.config.getConnectionRetry());
            }
        } catch (TTransportException tte) {
            this.log.error("Connect failed: " + tte.getMessage());
        } catch (TException te) {
            this.log.error("Connect failed: " + te.getMessage());
        }
        return null;
    }

    @Override
    public PooledObject<NebulaGraphPoolConnection> wrap(
            NebulaGraphPoolConnection nebulaPoolConnection) {
        return new DefaultPooledObject<>(nebulaPoolConnection);
    }

    @Override
    public boolean validateObject(PooledObject<NebulaGraphPoolConnection> p) {
        if (p == null) {
            return false;
        }
        NebulaGraphPoolConnection object = p.getObject();
        return object != null && object.isOpened();
    }

    @Override
    public void destroyObject(PooledObject<NebulaGraphPoolConnection> p) throws Exception {
        if (p != null) {
            NebulaGraphPoolConnection object = p.getObject();
            object.close();
        }
    }


    public NebulaConnectionPoolConfig getConfig() {
        return this.config;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        NebulaGraphPoolConnectionFactory that = (NebulaGraphPoolConnectionFactory) o;
        return Objects.equals(this.config, that.config);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.config);
    }
}

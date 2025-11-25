/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.net;

import static com.vesoft.nebula.driver.graph.exception.IOErrorException.E_ALL_BROKEN;

import com.vesoft.nebula.driver.graph.data.ResultSet;
import com.vesoft.nebula.driver.graph.exception.AuthFailedException;
import com.vesoft.nebula.driver.graph.exception.IOErrorException;
import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientPoolFactory extends BasePooledObjectFactory<NebulaClient>
    implements Serializable {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final RoundRobinLoadBalancer loadBalancer;
    private final NebulaPool.Builder     builder;

    public ClientPoolFactory(
        RoundRobinLoadBalancer loadBalancer,
        NebulaPool.Builder builder) {
        this.loadBalancer = loadBalancer;
        this.builder = builder;
    }


    @Override
    public NebulaClient create() throws IOErrorException, AuthFailedException {
        int                 tryCreate     = 0;
        IOErrorException    ioException   = null;
        AuthFailedException authException = null;
        while (tryCreate++ < loadBalancer.addressSize()) {
            try {
                return createClient();
            } catch (IOErrorException e) {
                ioException = e;
            } catch (AuthFailedException e) {
                authException = e;
            }
        }
        if (authException != null) {
            throw authException;
        }
        if (ioException != null) {
            throw ioException;
        }
        throw new IOErrorException(E_ALL_BROKEN, "No servers host is available, please check your"
            + " servers is up and network between client and server is connected.");
    }


    private NebulaClient createClient() throws AuthFailedException, IOErrorException {
        NebulaClient client = NebulaClient
            .builder(loadBalancer.getAddress().toString(), builder.userName)
            .withAuthOptions(builder.authOptions)
            .withConnectTimeoutMills(builder.connectTimeoutMills)
            .withRequestTimeoutMills(builder.requestTimeoutMills)
            .withScanParallel(builder.scanParallel)
            .withEnableTls(builder.enableTls)
            .withDisableVerifyServerCert(builder.disableVerifyServerCert)
            .withTlsCa(builder.tlsCa)
            .withTlsCert(builder.tlsCert, builder.tlsKey)
            .build();

        // set home schema、home graph and time zone for session
        switch (client.getVersion()) {
            // todo remove the version return by auth
            case "5.1.0":
            default: {
                String    stmt;
                ResultSet resultSet;
                try {
                    if (builder.schema != null && !builder.schema.isEmpty()) {
                        stmt = String.format("SESSION SET SCHEMA `%s`", builder.schema);
                        resultSet = client.execute(stmt);
                        if (!resultSet.isSucceeded()) {
                            throw new RuntimeException(String.format("%s failed for %s",
                                                                     stmt,
                                                                     resultSet.getErrorMessage()));
                        }
                    }

                    if (builder.graph != null && !builder.graph.isEmpty()) {
                        stmt = String.format("SESSION SET GRAPH \"%s\"", builder.graph);
                        resultSet = client.execute(stmt);
                        if (!resultSet.isSucceeded()) {
                            throw new RuntimeException(String.format("%s failed for %s",
                                                                     stmt,
                                                                     resultSet.getErrorMessage()));
                        }
                    }

                    for (Entry<String, String> config : builder.sessionConfigs.entrySet()) {
                        stmt = String.format("SESSION SET %s=%s",
                                             config.getKey(),
                                             config.getValue());
                        resultSet = client.execute(stmt);
                        if (!resultSet.isSucceeded()) {
                            throw new RuntimeException(String.format("%s failed for %s",
                                                                     stmt,
                                                                     resultSet.getErrorMessage()));
                        }
                    }

                    if (!builder.parameters.isEmpty()) {
                        StringBuilder parametersSetStatement = new StringBuilder();
                        parametersSetStatement.append("SESSION SET VALUE ");
                        for (Map.Entry<String, String> parameter : builder.parameters.entrySet()) {
                            parametersSetStatement
                                .append("$")
                                .append(parameter.getKey())
                                .append("=")
                                .append(parameter.getValue())
                                .append(",");
                        }
                        parametersSetStatement.deleteCharAt(
                            parametersSetStatement.length() - 1);
                        if (!parametersSetStatement.toString().isEmpty()) {
                            ResultSet result = client.execute(
                                parametersSetStatement.toString());
                            if (!result.isSucceeded()) {
                                throw new RuntimeException(String.format("%s failed for %s",
                                                                         parametersSetStatement,
                                                                         result.getErrorMessage()));
                            }
                        }
                    }
                    for (String preStmt : builder.preStatements) {
                        ResultSet res = client.execute(preStmt);
                        if (!res.isSucceeded()) {
                            throw new RuntimeException(String.format("%s failed for %s",
                                                                     preStmt,
                                                                     res.getErrorMessage()));
                        }
                    }
                } catch (IOErrorException e) {
                    client.close();
                    throw e;
                }
            }
        }
        return client;
    }

    @Override
    public PooledObject<NebulaClient> wrap(NebulaClient client) {
        return new DefaultPooledObject<>(client);
    }

    @Override
    public void destroyObject(PooledObject<NebulaClient> clientObject) throws Exception {
        NebulaClient client = clientObject.getObject();
        try {
            client.close();
        } catch (Exception e) {
            logger.warn("session release failed ", e);
        }
        super.destroyObject(clientObject);
    }

    @Override
    public boolean validateObject(PooledObject<NebulaClient> clientObject) {
        NebulaClient client = clientObject.getObject();
        boolean isAlive =
            (System.currentTimeMillis() - client.getCreateTime()) < builder.maxLifeTimeMs;
        return client.ping(builder.serverPingTimeoutMills) && isAlive;
    }
}

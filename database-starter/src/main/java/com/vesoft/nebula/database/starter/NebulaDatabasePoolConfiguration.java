/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.database.starter;

import com.vesoft.nebula.client.graph.NebulaConnection;
import com.vesoft.nebula.client.graph.pool.NebulaConnectionPool;
import com.vesoft.nebula.client.graph.pool.NebulaPoolConnectionFactory;
import com.vesoft.nebula.client.graph.pool.config.NebulaConnectionPoolConfig;
import com.vesoft.nebula.client.graph.pool.constant.ErrorEnum;
import com.vesoft.nebula.client.graph.pool.entity.LinkDomain;
import com.vesoft.nebula.client.graph.pool.exception.LinkConfigException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Anyzm
 * @version 1.0.0
 * @Description NebulaDatabasePoolConfiguration is used for create a datasource
 * @Date 2020/3/19 - 11:27
 */
@Configuration
@AutoConfigureAfter
public class NebulaDatabasePoolConfiguration {

    @Value("${nebula.host:null}")
    private String host;

    @Value("${nebula.port:0}")
    private int port;

    @Value("${nebula.userName:user}")
    private String userName;

    @Value("${nebula.password:password}")
    private String password;

    @Value("${nebula.pool.maxIdle:20}")
    private int maxIdle;

    @Value("${nebula.pool.minIdle:2}")
    private int minIdle;

    @Value("${nebula.pool.maxTotal:50}")
    private int maxTotal;

    /**
     * multi address configuration
     * eg  127.0.0.1::3699@@user::paassword;;192.168.180.1::3699@@user::paassword
     */
    @Value("${nebula.links:null}")
    private String links;

    @Value("${nebula.socketTimeout:10000}")
    private int timeout;

    @Value("${nebula.connectionRetry:3}")
    private int connectionRetry;

    private static final String NULL = "null";

    private static final String LOCAL = "127.0.0.1";

    private static final int DEFAULT_PORT = 3699;

    private NebulaConnectionPool createDefaultPool(List<LinkDomain> linkDomains) {
        NebulaConnectionPoolConfig poolConfig =
                new NebulaConnectionPoolConfig(linkDomains,
                        connectionRetry, timeout, maxIdle, minIdle, maxTotal);
        NebulaPoolConnectionFactory nebulaPoolConnectionFactory =
                new NebulaPoolConnectionFactory(poolConfig);
        return new NebulaConnectionPool(nebulaPoolConnectionFactory);
    }

    @Bean(name = "nebulaConnectionPool")
    public NebulaConnectionPool configNebulaConnectionPool() throws Exception {
        NebulaConnectionPool nebulaConnectionPool;
        if (!NULL.equals(host) && port > 0) {
            LinkDomain linkDomain = new LinkDomain(host, port, userName, password);
            nebulaConnectionPool = createDefaultPool(Arrays.asList(linkDomain));
        } else {
            if (!NULL.equals(links)) {
                //split address
                List<LinkDomain> address = splitLinks();
                NebulaConnectionPoolConfig poolConfig =
                        new NebulaConnectionPoolConfig(address,
                                connectionRetry, timeout, maxIdle, minIdle, maxTotal);
                NebulaPoolConnectionFactory nebulaPoolConnectionFactory =
                        new NebulaPoolConnectionFactory(poolConfig);
                return new NebulaConnectionPool(nebulaPoolConnectionFactory);
            } else {
                LinkDomain linkDomain = new LinkDomain(LOCAL, DEFAULT_PORT, userName, password);
                nebulaConnectionPool = createDefaultPool(Arrays.asList(linkDomain));
            }
        }
        NebulaConnection nebulaConnection = nebulaConnectionPool.borrowObject();
        nebulaConnectionPool.returnObject(nebulaConnection);
        return nebulaConnectionPool;
    }

    private List<LinkDomain> splitLinks() throws LinkConfigException {
        List<LinkDomain> address = new ArrayList<>();
        String[] linkArray = links.split(";;");
        for (String link : linkArray) {
            LinkDomain linkDomain = new LinkDomain();
            String[] linkGroup = link.split("@@");
            if (linkGroup.length != 2) {
                //configuration error
                throw new LinkConfigException(ErrorEnum.LINK_ERROR);
            }
            String[] hostPort = linkGroup[0].split("::");
            if (hostPort.length != 2) {
                //configuration error
                throw new LinkConfigException(ErrorEnum.LINK_ERROR);
            }
            linkDomain.setHost(hostPort[0]);
            try {
                int port = Integer.parseInt(hostPort[1]);
                linkDomain.setPort(port);
            } catch (Exception e) {
                //configuration port error
                throw new LinkConfigException(ErrorEnum.LINK_ERROR);
            }
            String[] userInfo = linkGroup[1].split("::");
            if (userInfo.length != 2) {
                //configuration user or password error
                throw new LinkConfigException(ErrorEnum.LINK_ERROR);
            }
            linkDomain.setUserName(userInfo[0]);
            linkDomain.setPassword(userInfo[1]);
            address.add(linkDomain);
        }
        return address;
    }

}

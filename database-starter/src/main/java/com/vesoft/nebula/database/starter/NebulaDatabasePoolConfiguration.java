/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.database.starter;

import com.vesoft.nebula.database.NebulaConnection;
import com.vesoft.nebula.database.NebulaDataSource;
import com.vesoft.nebula.database.constant.ErrorEnum;
import com.vesoft.nebula.database.entity.LinkDomain;
import com.vesoft.nebula.database.exception.LinkConfigException;
import com.vesoft.nebula.database.pool.NebulaPoolDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.ArrayList;
import java.util.List;

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

    @Value("${nebula.databasePoolSize:50}")
    private int poolSize;

    private static final String NULL = "null";

    @Bean(name = "nebulaPoolDataSource")
    public NebulaDataSource configNebulaDataSource() throws LinkConfigException{
        NebulaPoolDataSource nebulaPoolDataSource = null;
        if (!NULL.equals(host) && port > 0) {
            LinkDomain linkDomain = new LinkDomain();
            linkDomain.setHost(host);
            linkDomain.setPort(port);
            linkDomain.setUserName(userName);
            linkDomain.setPassword(password);
            nebulaPoolDataSource =  new NebulaPoolDataSource(linkDomain, timeout, connectionRetry, poolSize);
        } else {
            if (!NULL.equals(links)) {
                //split address
                List<LinkDomain> address = splitLinks();
                nebulaPoolDataSource = new NebulaPoolDataSource(address, timeout, connectionRetry, poolSize);
            }else{
                LinkDomain linkDomain = new LinkDomain();
                linkDomain.setHost("127.0.0.1");
                linkDomain.setPort(3699);
                linkDomain.setUserName(userName);
                linkDomain.setPassword(password);
                nebulaPoolDataSource = new NebulaPoolDataSource(linkDomain, timeout, connectionRetry, poolSize);
            }
        }
        //try connect once
        NebulaConnection connection = nebulaPoolDataSource.getConnection();
        nebulaPoolDataSource.release(connection);
        return nebulaPoolDataSource;
    }

    private List<LinkDomain> splitLinks() throws LinkConfigException{
        List<LinkDomain> address = new ArrayList<>();
        String[] linkArray = links.split(";;");
        for(String link : linkArray){
            LinkDomain linkDomain = new LinkDomain();
            String[] aLink = link.split("@@");
            if(aLink.length != 2){
                //configuration error
                throw new LinkConfigException(ErrorEnum.LINK_ERROR);
            }
            String[] hostPort = aLink[0].split("::");
            if(hostPort.length != 2){
                //configuration error
                throw new LinkConfigException(ErrorEnum.LINK_ERROR);
            }
            linkDomain.setHost(hostPort[0]);
            try {
                int port = Integer.parseInt(hostPort[1]);
                linkDomain.setPort(port);
            }catch (Exception e){
                //configuration port error
                throw new LinkConfigException(ErrorEnum.LINK_ERROR);
            }
            String[] userInfo = aLink[1].split("::");
            if(userInfo.length != 2){
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

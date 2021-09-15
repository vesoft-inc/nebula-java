/* Copyright (c) 2021 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */
package com.vesoft.nebula.session;

import com.vesoft.nebula.client.graph.exception.AuthFailedException;
import com.vesoft.nebula.client.graph.exception.IOErrorException;
import com.vesoft.nebula.client.graph.exception.NotValidConnectionException;
import com.vesoft.nebula.client.graph.net.NebulaPool;
import com.vesoft.nebula.exception.NebulaException;
import org.apache.commons.lang3.StringUtils;

/**
 * Description  NebulaPoolSessionManager is used for
 *
 * @author huangzhaolai-jk
 * Date  2021/7/15 - 18:17
 * nebula-session连接池包装类，让session获取更快捷
 * @version 1.0.0
 */
public class NebulaPoolSessionManager {

    private NebulaPool nebulaPool;

    private String userName;

    private String password;

    private boolean reconnect;

    public NebulaPoolSessionManager(NebulaPool nebulaPool, String userName, String password, boolean reconnect) {
        this.nebulaPool = nebulaPool;
        this.userName = userName;
        this.password = password;
        this.reconnect = reconnect;
    }

    public NebulaSessionWrapper getSession(String space) throws NotValidConnectionException, IOErrorException, AuthFailedException, NebulaException {
        NebulaSessionWrapper nebulaSessionWrapper = new NebulaSessionWrapper(this.nebulaPool.getSession(this.userName, this.password, this.reconnect));
        if (StringUtils.isNotBlank(space)) {
            nebulaSessionWrapper.execute("use " + space);
        }
        return nebulaSessionWrapper;
    }

    public NebulaSessionWrapper getSession(String userName, String password, boolean reconnect) throws NotValidConnectionException,
            IOErrorException, AuthFailedException, NebulaException {
        return new NebulaSessionWrapper(this.nebulaPool.getSession(userName, password, reconnect));
    }

    public NebulaSessionWrapper getSession(boolean reconnect) throws NotValidConnectionException,
            IOErrorException, AuthFailedException, NebulaException {
        return new NebulaSessionWrapper(this.nebulaPool.getSession(this.userName, this.password, reconnect));
    }

}

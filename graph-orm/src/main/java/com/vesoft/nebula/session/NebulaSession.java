/* Copyright (c) 2021 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */
package com.vesoft.nebula.session;

import com.vesoft.nebula.client.graph.data.ResultSet;
import com.vesoft.nebula.domain.Session;
import com.vesoft.nebula.exception.NebulaExecuteException;

/**
 * Description  NebulaSession is used for
 *
 * @author huangzhaolai-jk
 * Date  2021/7/15 - 18:26
 * @version 1.0.0
 */
public interface NebulaSession extends Session {


    /**
     * 执行查询
     *
     * @param statement
     * @return
     * @throws NebulaExecuteException
     */
    public ResultSet executeQuery(String statement) throws NebulaExecuteException;


}

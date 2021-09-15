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

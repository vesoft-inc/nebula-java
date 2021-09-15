package com.vesoft.nebula.domain;


import com.vesoft.nebula.domain.impl.QueryResult;
import com.vesoft.nebula.exception.NebulaExecuteException;

/**
 * Description  Session is used for
 *
 * @author huangzhaolai-jk
 * Date  2021/7/16 - 17:39
 * @version 1.0.0
 */
public interface Session {

    /**
     * 执行更新操作
     *
     * @param statement
     * @return
     * @throws NebulaExecuteException
     */
    public int execute(String statement) throws NebulaExecuteException;

    /**
     * 执行查询
     *
     * @param statement
     * @return
     * @throws NebulaExecuteException
     */
    public QueryResult executeQueryDefined(String statement) throws NebulaExecuteException;

    /**
     * 释放session
     */
    public void release();

    /**
     * Need server supported, v1.0 nebula-graph doesn't supported
     *
     * @return ping服务器
     */
    public boolean ping();

}

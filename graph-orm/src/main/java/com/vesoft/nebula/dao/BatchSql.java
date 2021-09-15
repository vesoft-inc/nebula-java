package com.vesoft.nebula.dao;


import com.vesoft.nebula.exception.NebulaException;

import java.util.List;

public interface BatchSql {
    /**
     * 获取批量执行的 sql 列表
     * @return
     * @throws NebulaException
     */
    public List<String> getSqlList() throws NebulaException;
}

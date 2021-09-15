package com.vesoft.nebula.dao;

import com.vesoft.nebula.domain.impl.GraphVertexEntity;
import com.vesoft.nebula.exception.NebulaException;

/**
 * @Author: ZhaoLai Huang
 * @Description:
 * @Date: Created in 19:59 2021/7/18
 */
public interface GraphVertexEntityFactory {

    /**
     * 构建GraphVertexEntity
     *
     * @param input
     * @return
     * @throws NebulaException
     */
    public <T> GraphVertexEntity<T> buildGraphVertexEntity(T input) throws NebulaException;

}

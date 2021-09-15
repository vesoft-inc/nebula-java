package com.vesoft.nebula.dao;

import com.vesoft.nebula.domain.impl.GraphEdgeEntity;
import com.vesoft.nebula.exception.NebulaException;

/**
 * @Author: ZhaoLai Huang
 * @Description:
 * @Date: Created in 20:00 2021/7/18
 */
public interface GraphEdgeEntityFactory {

    /**
     * 构建GraphEdgeEntity
     *
     * @param input
     * @param <S>
     * @param <T>
     * @param <E>
     * @return
     * @throws NebulaException
     */
    public <S, T, E> GraphEdgeEntity<S, T, E> buildGraphEdgeEntity(E input) throws NebulaException;

}

/* Copyright (c) 2021 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */
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

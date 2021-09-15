/* Copyright (c) 2021 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */
package com.vesoft.nebula.engine;

import com.vesoft.nebula.dao.EdgeUpdateEngine;
import com.vesoft.nebula.dao.GraphUpdateEdgeEngineFactory;
import com.vesoft.nebula.domain.impl.GraphEdgeEntity;
import com.vesoft.nebula.domain.impl.GraphVertexEntity;
import com.vesoft.nebula.exception.NebulaException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Description  NebulaUpdateEdgeEngineFactory is used for
 *
 * @author huangzhaolai-jk
 * Date  2021/7/19 - 10:52
 * @version 1.0.0
 */
@Slf4j
public class NebulaUpdateEdgeEngineFactory implements GraphUpdateEdgeEngineFactory {

    @Override
    public <S, T, E> EdgeUpdateEngine<S, T, E> build(List<GraphEdgeEntity<S, T, E>> graphEdgeEntities) throws NebulaException {
        return new NebulaBatchEdgesUpdate<>(graphEdgeEntities);
    }

    @Override
    public <S, T, E> EdgeUpdateEngine<S, T, E> build(List<GraphEdgeEntity<S, T, E>> graphEdgeEntities,
                                                     List<GraphVertexEntity<S>> srcGraphVertexEntities,
                                                     List<GraphVertexEntity<T>> dstGraphVertexEntities) throws NebulaException {
        return new NebulaBatchEdgesUpdate<>(graphEdgeEntities, srcGraphVertexEntities, dstGraphVertexEntities);
    }

}

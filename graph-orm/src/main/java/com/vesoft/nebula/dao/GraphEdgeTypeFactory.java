/* Copyright (c) 2021 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */
package com.vesoft.nebula.dao;

import com.vesoft.nebula.domain.impl.GraphEdgeType;
import com.vesoft.nebula.domain.impl.GraphVertexType;
import com.vesoft.nebula.exception.NebulaException;

/**
 * Description  GraphEdgeTypeFactory is used for
 *
 * @author huangzhaolai-jk
 * Date  2021/7/16 - 15:10
 * @version 1.0.0
 */
public interface GraphEdgeTypeFactory {

    /**
     * 根据类创建边类型
     *
     * @param clazz
     * @return
     * @throws NebulaException
     */
    public <S, T, E> GraphEdgeType<S, T, E> buildGraphEdgeType(Class<E> clazz) throws NebulaException;


    /**
     * 根据类 和 顶点类型创建边类型
     *
     * @param clazz
     * @param srcGraphVertexType
     * @param dstGraphVertexType
     * @return
     * @throws NebulaException
     */
    public <S, T, E> GraphEdgeType<S, T, E> buildGraphEdgeType(Class<E> clazz, GraphVertexType<S> srcGraphVertexType,
                                                               GraphVertexType<T> dstGraphVertexType) throws NebulaException;

}

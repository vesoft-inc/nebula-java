/* Copyright (c) 2021 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */
package com.vesoft.nebula.dao;

import com.vesoft.nebula.domain.impl.GraphVertexEntity;
import com.vesoft.nebula.domain.impl.GraphVertexType;

import java.util.List;

/**
 * Description  VertexUpdateEngine is used for
 * 目前顶点的更新，只适合单类型的vertex type，即单个tag，类似mysql的单表操作
 *
 * @author huangzhaolai-jk
 * Date  2021/7/16 - 17:10
 * @version 1.0.0
 */
public interface VertexUpdateEngine extends GraphUpdateEngine {

    /**
     * 获取顶点实体
     *
     * @return
     */
    public <T> List<GraphVertexEntity<T>> getGraphVertexEntityList();

    /**
     * 获取顶点类型
     *
     * @return
     */
    public <T> GraphVertexType<T> getGraphVertexType();

}

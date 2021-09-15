/* Copyright (c) 2021 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */
package com.vesoft.nebula.cache;


import com.vesoft.nebula.domain.impl.GraphEdgeType;
import com.vesoft.nebula.domain.impl.GraphVertexType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description  DefaultGraphTypeCache is used for
 * 默认的图类型缓存，一个图类型，只需要构建一次，永久缓存（除非重启）
 *
 * @author huangzhaolai-jk
 * Date  2021/7/16 - 19:42
 * @version 1.0.0
 */
public class DefaultGraphTypeCache implements GraphTypeCache {

    private Map<Class, GraphVertexType> graphVertexTypeMap = new ConcurrentHashMap<>();

    private Map<Class, GraphEdgeType> graphEdgeTypeMap = new ConcurrentHashMap<>();

    @Override
    public <T> GraphVertexType<T> getGraphVertexType(Class<T> clazz) {
        return this.graphVertexTypeMap.get(clazz);
    }

    @Override
    public <T> void putGraphVertexType(Class<T> clazz, GraphVertexType<T> graphVertexType) {
        if (clazz != null && graphVertexType != null) {
            this.graphVertexTypeMap.put(clazz, graphVertexType);
        }
    }

    @Override
    public GraphEdgeType getGraphEdgeType(Class clazz) {
        return this.graphEdgeTypeMap.get(clazz);
    }

    @Override
    public void putGraphEdgeType(Class clazz, GraphEdgeType graphEdgeType) {
        if (clazz != null && graphEdgeType != null) {
            this.graphEdgeTypeMap.put(clazz, graphEdgeType);
        }
    }
}

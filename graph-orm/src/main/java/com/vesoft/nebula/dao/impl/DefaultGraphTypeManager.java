package com.vesoft.nebula.dao.impl;

import com.vesoft.nebula.annotation.GraphEdge;
import com.vesoft.nebula.cache.DefaultGraphTypeCache;
import com.vesoft.nebula.cache.GraphTypeCache;
import com.vesoft.nebula.dao.GraphEdgeTypeFactory;
import com.vesoft.nebula.dao.GraphTypeManager;
import com.vesoft.nebula.dao.GraphVertexTypeFactory;
import com.vesoft.nebula.domain.GraphLabel;
import com.vesoft.nebula.domain.impl.GraphEdgeType;
import com.vesoft.nebula.domain.impl.GraphVertexType;
import com.vesoft.nebula.exception.NebulaException;

/**
 * Description  DefaultGraphTypeManager is used for
 *
 * @author huangzhaolai-jk
 * Date  2021/7/16 - 19:38
 * @version 1.0.0
 */
public class DefaultGraphTypeManager implements GraphTypeManager {

    public DefaultGraphTypeManager() {
        this.graphTypeCache = new DefaultGraphTypeCache();
        this.graphVertexTypeFactory = new DefaultGraphVertexTypeFactory();
        this.graphEdgeTypeFactory = new DefaultGraphEdgeTypeFactory();
    }

    private GraphTypeCache graphTypeCache;

    private GraphVertexTypeFactory graphVertexTypeFactory;

    private GraphEdgeTypeFactory graphEdgeTypeFactory;

    @Override
    public <T> GraphVertexType<T> getGraphVertexType(Class<T> clazz) throws NebulaException {
        if (clazz == null) {
            return null;
        }
        GraphVertexType<T> graphVertexType = graphTypeCache.getGraphVertexType(clazz);
        if (graphVertexType != null) {
            return graphVertexType;
        }
        graphVertexType = graphVertexTypeFactory.buildGraphVertexType(clazz);
        graphTypeCache.putGraphVertexType(clazz, graphVertexType);
        return graphVertexType;
    }

    @Override
    public <S, T, E> GraphEdgeType<S, T, E> getGraphEdgeType(Class<E> clazz) throws NebulaException {
        if (clazz == null) {
            return null;
        }
        GraphEdgeType graphEdgeType = graphTypeCache.getGraphEdgeType(clazz);
        if (graphEdgeType != null) {
            return graphEdgeType;
        }
        GraphEdge graphEdge = (GraphEdge) clazz.getAnnotation(GraphEdge.class);
        if (graphEdge == null) {
            return null;
        }
        Class srcVertexClass = graphEdge.srcVertex();
        Class dstVertexClass = graphEdge.dstVertex();
        GraphVertexType srcGraphVertexType = graphTypeCache.getGraphVertexType(srcVertexClass);
        GraphVertexType dstGraphVertexType = graphTypeCache.getGraphVertexType(dstVertexClass);
        graphEdgeType = graphEdgeTypeFactory.buildGraphEdgeType(clazz, srcGraphVertexType,
                dstGraphVertexType);
        graphTypeCache.putGraphEdgeType(clazz, graphEdgeType);
        if (srcGraphVertexType == null) {
            graphTypeCache.putGraphVertexType(graphEdgeType.getSrcVertexType().getTypeClass(), graphEdgeType.getSrcVertexType());
        }
        if (dstGraphVertexType == null) {
            graphTypeCache.putGraphVertexType(graphEdgeType.getDstVertexType().getTypeClass(), graphEdgeType.getDstVertexType());
        }
        return graphEdgeType;
    }

    @Override
    public GraphLabel getGraphLabel(Class clazz) throws NebulaException {
        try {
            GraphEdgeType graphEdgeType = this.getGraphEdgeType(clazz);
            if (graphEdgeType == null) {
                return this.getGraphVertexType(clazz);
            } else {
                return graphEdgeType;
            }
        } catch (NebulaException e) {
            return this.getGraphVertexType(clazz);
        }
    }
}

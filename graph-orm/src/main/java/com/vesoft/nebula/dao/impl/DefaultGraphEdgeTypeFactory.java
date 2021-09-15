package com.vesoft.nebula.dao.impl;

import com.google.common.collect.Maps;
import com.vesoft.nebula.annotation.GraphEdge;
import com.vesoft.nebula.common.GraphHelper;
import com.vesoft.nebula.dao.GraphEdgeTypeFactory;
import com.vesoft.nebula.dao.GraphVertexTypeFactory;
import com.vesoft.nebula.domain.impl.GraphEdgeType;
import com.vesoft.nebula.domain.impl.GraphEdgeTypeBuilder;
import com.vesoft.nebula.domain.impl.GraphVertexType;
import com.vesoft.nebula.enums.ErrorEnum;
import com.vesoft.nebula.enums.GraphDataTypeEnum;
import com.vesoft.nebula.exception.CheckThrower;
import com.vesoft.nebula.exception.NebulaException;

import java.util.Map;

/**
 * Description  DefaultGraphEdgeTypeFactory is used for
 * 默认的图边类型工厂类
 *
 * @author huangzhaolai-jk
 * Date  2021/7/16 - 16:58
 * @version 1.0.0
 */
public class DefaultGraphEdgeTypeFactory implements GraphEdgeTypeFactory {

    private GraphVertexTypeFactory graphVertexTypeFactory;

    public DefaultGraphEdgeTypeFactory() {
        this.graphVertexTypeFactory = new DefaultGraphVertexTypeFactory();
    }

    @Override
    public <S, T, E> GraphEdgeType<S, T, E> buildGraphEdgeType(Class<E> clazz) throws NebulaException {
        return buildGraphEdgeType(clazz, null, null);
    }

    @Override
    public <S, T, E> GraphEdgeType<S, T, E> buildGraphEdgeType(Class<E> clazz, GraphVertexType<S> srcGraphVertexType,
                                                               GraphVertexType<T> dstGraphVertexType) throws NebulaException {
        GraphEdge graphEdge = (GraphEdge) clazz.getAnnotation(GraphEdge.class);
        CheckThrower.ifTrueThrow(graphEdge == null, ErrorEnum.PARAMETER_NOT_NULL);
        String edgeName = graphEdge.value();
        boolean srcIdAsField = graphEdge.srcIdAsField();
        boolean dstIdAsField = graphEdge.dstIdAsField();
        //字段类型
        Map<String, GraphDataTypeEnum> dataTypeMap = Maps.newHashMap();
        if (srcGraphVertexType == null) {
            Class<S> srcVertex = graphEdge.srcVertex();
            srcGraphVertexType = graphVertexTypeFactory.buildGraphVertexType(srcVertex);
        }
        if (dstGraphVertexType == null) {
            Class<T> dstVertex = graphEdge.dstVertex();
            dstGraphVertexType = graphVertexTypeFactory.buildGraphVertexType(dstVertex);
        }
        CheckThrower.ifTrueThrow(srcGraphVertexType == null || dstGraphVertexType == null, ErrorEnum.INVALID_VERTEX_TAG);
        GraphEdgeTypeBuilder builder = GraphEdgeTypeBuilder.builder();
        GraphHelper.collectGraphProperties(builder, clazz, srcIdAsField, dstIdAsField);
        return builder.srcIdAsField(srcIdAsField).dstIdAsField(dstIdAsField).graphLabelName(edgeName)
                .labelClass(clazz).srcGraphVertexType(srcGraphVertexType).dstGraphVertexType(dstGraphVertexType).build();
    }
}

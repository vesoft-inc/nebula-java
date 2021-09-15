package com.vesoft.nebula.dao.impl;

import com.google.common.collect.Maps;
import com.vesoft.nebula.annotation.GraphProperty;
import com.vesoft.nebula.common.GraphHelper;
import com.vesoft.nebula.dao.GraphEdgeEntityFactory;
import com.vesoft.nebula.dao.GraphTypeManager;
import com.vesoft.nebula.domain.impl.GraphEdgeEntity;
import com.vesoft.nebula.domain.impl.GraphEdgeType;
import com.vesoft.nebula.domain.impl.GraphVertexType;
import com.vesoft.nebula.enums.ErrorEnum;
import com.vesoft.nebula.enums.GraphPropertyTypeEnum;
import com.vesoft.nebula.exception.CheckThrower;
import com.vesoft.nebula.exception.NebulaException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * @Author ZhaoLai Huang
 * created by ZhaoLai Huang on 2021/7/18
 */
@Slf4j
public class DefaultGraphEdgeEntityFactory implements GraphEdgeEntityFactory {

    private GraphTypeManager graphTypeManager;

    public DefaultGraphEdgeEntityFactory() {
        this.graphTypeManager = new DefaultGraphTypeManager();
    }

    public DefaultGraphEdgeEntityFactory(GraphTypeManager graphTypeManager) {
        this.graphTypeManager = graphTypeManager;
    }

    @Override
    public <S, T, E> GraphEdgeEntity<S, T, E> buildGraphEdgeEntity(E input) throws NebulaException {
        if (input == null) {
            return null;
        }
        Class<E> inputClass = (Class<E>) input.getClass();
        GraphEdgeType<S, T, E> graphEdgeType = graphTypeManager.getGraphEdgeType(inputClass);
        if (graphEdgeType == null) {
            return null;
        }
        //起点类型
        GraphVertexType<?> srcVertexType = graphEdgeType.getSrcVertexType();
        //终点类型
        GraphVertexType<?> dstVertexType = graphEdgeType.getDstVertexType();
        Field[] declaredFields = inputClass.getDeclaredFields();
        String srcId = null;
        String dstId = null;
        //所有属性与值
        Map<String, Object> propertyMap = Maps.newHashMapWithExpectedSize(declaredFields.length);
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);
            GraphProperty graphProperty = declaredField.getAnnotation(GraphProperty.class);
            if (graphProperty == null) {
                continue;
            }
            Object value = GraphHelper.formatFieldValue(declaredField, graphProperty, input, graphEdgeType);
            if (graphProperty.propertyTypeEnum().equals(GraphPropertyTypeEnum.GRAPH_EDGE_SRC_ID)) {
                srcId = (String) value;
                if (!graphEdgeType.isSrcIdAsField()) {
                    continue;
                }
            }
            if (graphProperty.propertyTypeEnum().equals(GraphPropertyTypeEnum.GRAPH_EDGE_DST_ID)) {
                dstId = (String) value;
                if (!graphEdgeType.isDstIdAsField()) {
                    continue;
                }
            }
            if (value != null) {
                propertyMap.put(graphProperty.value(), value);
            }
        }
        CheckThrower.ifTrueThrow(StringUtils.isBlank(srcId) || StringUtils.isBlank(dstId),
                ErrorEnum.INVALID_ID);
        return new GraphEdgeEntity(graphEdgeType, srcId, dstId, srcVertexType, dstVertexType, propertyMap);
    }
}

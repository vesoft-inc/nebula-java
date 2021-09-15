package com.vesoft.nebula.dao.impl;

import com.vesoft.nebula.annotation.GraphProperty;
import com.vesoft.nebula.common.GraphHelper;
import com.vesoft.nebula.dao.GraphTypeManager;
import com.vesoft.nebula.dao.GraphVertexEntityFactory;
import com.vesoft.nebula.domain.impl.GraphVertexEntity;
import com.vesoft.nebula.domain.impl.GraphVertexType;
import com.vesoft.nebula.enums.ErrorEnum;
import com.vesoft.nebula.enums.GraphPropertyTypeEnum;
import com.vesoft.nebula.exception.CheckThrower;
import com.vesoft.nebula.exception.NebulaException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author ZhaoLai Huang
 * created by ZhaoLai Huang on 2021/7/18
 */
@Slf4j
public class DefaultGraphVertexEntityFactory implements GraphVertexEntityFactory {

    private GraphTypeManager graphTypeManager;

    public DefaultGraphVertexEntityFactory(GraphTypeManager graphTypeManager) {
        this.graphTypeManager = graphTypeManager;
    }

    public DefaultGraphVertexEntityFactory() {
        this.graphTypeManager = new DefaultGraphTypeManager();
    }

    @Override
    public <T> GraphVertexEntity<T> buildGraphVertexEntity(T input) throws NebulaException {
        if (input == null) {
            return null;
        }
        Class<T> inputClass = (Class<T>) input.getClass();
        GraphVertexType<T> graphVertexType = graphTypeManager.getGraphVertexType(inputClass);
        if (graphVertexType == null) {
            return null;
        }
        Field[] declaredFields = inputClass.getDeclaredFields();
        String id = null;
        Map<String, Object> propertyMap = new HashMap<>();
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);
            GraphProperty graphProperty = declaredField.getAnnotation(GraphProperty.class);
            if (graphProperty == null) {
                continue;
            }
            Object value = GraphHelper.formatFieldValue(declaredField, graphProperty, input, graphVertexType);
            if (graphProperty.propertyTypeEnum().equals(GraphPropertyTypeEnum.GRAPH_VERTEX_ID)) {
                id = (String) value;
                if (!graphVertexType.isIdAsField()) {
                    continue;
                }
            }
            if (value != null) {
                propertyMap.put(graphProperty.value(), value);
            }
        }
        CheckThrower.ifTrueThrow(StringUtils.isBlank(id), ErrorEnum.INVALID_ID);
        return new GraphVertexEntity<>(graphVertexType, id, propertyMap);
    }
}

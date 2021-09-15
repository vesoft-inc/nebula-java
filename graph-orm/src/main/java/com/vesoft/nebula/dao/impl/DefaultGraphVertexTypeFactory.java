/* Copyright (c) 2021 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */
package com.vesoft.nebula.dao.impl;

import com.vesoft.nebula.annotation.GraphVertex;
import com.vesoft.nebula.common.GraphHelper;
import com.vesoft.nebula.dao.GraphVertexTypeFactory;
import com.vesoft.nebula.domain.impl.GraphVertexType;
import com.vesoft.nebula.domain.impl.GraphVertexTypeBuilder;
import com.vesoft.nebula.enums.GraphKeyPolicy;
import com.vesoft.nebula.exception.NebulaException;

/**
 * Description  NebulaGraphVertexTypeFactory is used for
 *
 * @author huangzhaolai-jk
 * Date  2021/7/16 - 16:41
 * @version 1.0.0
 */
public class DefaultGraphVertexTypeFactory implements GraphVertexTypeFactory {

    @Override
    public <T> GraphVertexType<T> buildGraphVertexType(Class<T> clazz) throws NebulaException {
        GraphVertex graphVertex = clazz.getAnnotation(GraphVertex.class);
        if (graphVertex == null) {
            return null;
        }
        String vertexName = graphVertex.value();
        //主键策略：hash uuid string
        GraphKeyPolicy graphKeyPolicy = graphVertex.keyPolicy();
        boolean idAsField = graphVertex.idAsField();
        GraphVertexTypeBuilder builder = GraphVertexTypeBuilder.builder();
        GraphHelper.collectGraphProperties(builder, clazz, idAsField, idAsField);
        return builder.graphKeyPolicy(graphKeyPolicy).idAsField(idAsField).graphLabelName(vertexName).labelClass(clazz).build();
    }

}

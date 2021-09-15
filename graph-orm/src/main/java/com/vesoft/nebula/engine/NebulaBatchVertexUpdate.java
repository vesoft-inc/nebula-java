/* Copyright (c) 2021 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */
package com.vesoft.nebula.engine;

import com.google.common.collect.Lists;
import com.vesoft.nebula.common.GraphHelper;
import com.vesoft.nebula.common.utils.StringUtil;
import com.vesoft.nebula.dao.VertexUpdateEngine;
import com.vesoft.nebula.domain.GraphLabel;
import com.vesoft.nebula.domain.impl.GraphVertexEntity;
import com.vesoft.nebula.domain.impl.GraphVertexType;
import com.vesoft.nebula.enums.ErrorEnum;
import com.vesoft.nebula.enums.GraphDataTypeEnum;
import com.vesoft.nebula.exception.CheckThrower;
import com.vesoft.nebula.exception.NebulaException;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 批量顶点更新引擎
 *
 * @author zhoupeng
 * @date 2020/4/13
 */
public class NebulaBatchVertexUpdate<T> implements VertexUpdateEngine {

    private static final String VERTEX_UPSET_SQL = "UPSERT VERTEX %s SET %s";

    private List<GraphVertexEntity<T>> graphVertexEntities;

    private GraphVertexType<T> graphVertexType;

    private int batchSize;


    /**
     * 构建顶点批量插入
     *
     * @param graphVertexEntities
     */
    public NebulaBatchVertexUpdate(List<GraphVertexEntity<T>> graphVertexEntities) throws NebulaException {
        CheckThrower.ifTrueThrow(CollectionUtils.isEmpty(graphVertexEntities), ErrorEnum.UPDATE_FIELD_DATA_NOT_EMPTY);
        this.graphVertexEntities = graphVertexEntities;
        this.graphVertexType = graphVertexEntities.get(0).getGraphVertexType();
        this.batchSize = graphVertexEntities.size();
    }

    private String getOneVertexSql() throws NebulaException {
        return generateUpsetSql(this.graphVertexEntities.get(0));
    }

    private List<String> getMultiVertexSql() throws NebulaException {
        // nebula> UPSERT VERTEX 111 SET player.name = "Dwight Howard", player.age = $^.player.age + 11;
        List<String> sqlList = Lists.newArrayListWithExpectedSize(batchSize);
        for (GraphVertexEntity graphVertexEntity : this.graphVertexEntities) {
            String sql = generateUpsetSql(graphVertexEntity);
            sqlList.add(sql);
        }
        return StringUtil.aggregate(sqlList, batchSize, ";");
    }

    private String generateUpsetSql(GraphVertexEntity graphVertexEntity) throws NebulaException {
        Set<Map.Entry<String, Object>> entries = graphVertexEntity.getProps().entrySet();
        String queryId = GraphHelper.getQueryId(this.graphVertexType, graphVertexEntity.getId());
        StringBuilder builder = new StringBuilder();
        Map<String, GraphDataTypeEnum> dataTypeMap = graphVertexEntity.getGraphVertexType().getDataTypeMap();
        for (Map.Entry<String, Object> entry : entries) {
            GraphDataTypeEnum graphDataTypeEnum = dataTypeMap.get(entry.getKey());
            if (GraphDataTypeEnum.STRING.equals(graphDataTypeEnum)) {
                builder.append(',').append(this.graphVertexType.getVertexName()).append('.')
                        .append(entry.getKey()).append("=\"").append(entry.getValue()).append("\"");
            } else {
                builder.append(',').append(this.graphVertexType.getVertexName()).append('.')
                        .append(entry.getKey()).append("=").append(entry.getValue());
            }
        }
        String sqlSet = builder.delete(0, 1).toString();
        return String.format(VERTEX_UPSET_SQL, queryId, sqlSet);
    }


    @Override
    public List<GraphVertexEntity<T>> getGraphVertexEntityList() {
        return this.graphVertexEntities;
    }

    @Override
    public GraphVertexType<T> getGraphVertexType() {
        return this.graphVertexType;
    }

    @Override
    public List<String> getSqlList() throws NebulaException {
        if (this.batchSize == 1) {
            return Lists.newArrayList(getOneVertexSql());
        }
        return getMultiVertexSql();
    }

    @Override
    public List<GraphLabel> getLabels() {
        return Lists.newArrayList(this.getGraphVertexType());
    }
}

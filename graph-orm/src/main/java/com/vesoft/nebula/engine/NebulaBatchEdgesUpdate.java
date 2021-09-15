/* Copyright (c) 2021 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */
package com.vesoft.nebula.engine;

import com.google.common.collect.Lists;
import com.vesoft.nebula.common.GraphHelper;
import com.vesoft.nebula.common.utils.StringUtil;
import com.vesoft.nebula.dao.EdgeUpdateEngine;
import com.vesoft.nebula.domain.GraphLabel;
import com.vesoft.nebula.domain.impl.GraphEdgeEntity;
import com.vesoft.nebula.domain.impl.GraphEdgeType;
import com.vesoft.nebula.domain.impl.GraphVertexEntity;
import com.vesoft.nebula.domain.impl.GraphVertexType;
import com.vesoft.nebula.enums.ErrorEnum;
import com.vesoft.nebula.enums.GraphDataTypeEnum;
import com.vesoft.nebula.exception.CheckThrower;
import com.vesoft.nebula.exception.NebulaException;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 批量边更新引擎
 *
 * @author zhoupeng
 * @date 2020/3/30
 */
public class NebulaBatchEdgesUpdate<S, T, E> implements EdgeUpdateEngine<S, T, E> {

    private static final String UPSET_SQL_FORMAT = "UPSERT EDGE %s->%s of %s SET %s";

    /**
     * 仅生成边的更新sql
     */
    private boolean isOnlyGenerateEdgeSql = true;

    private List<GraphEdgeEntity<S, T, E>> graphEdgeEntities;

    private GraphVertexType<S> srcGraphVertexType;

    private GraphVertexType<T> dstGraphVertexType;

    private List<GraphVertexEntity<S>> srcGraphVertexEntities;

    private List<GraphVertexEntity<T>> dstGraphVertexEntities;

    public NebulaBatchEdgesUpdate(List<GraphEdgeEntity<S, T, E>> graphEdgeEntities) throws NebulaException {
        this.graphEdgeEntities = graphEdgeEntities;
        CheckThrower.ifTrueThrow(CollectionUtils.isEmpty(graphEdgeEntities), ErrorEnum.UPDATE_FIELD_DATA_NOT_EMPTY);
        this.srcGraphVertexType = graphEdgeEntities.get(0).getSrcVertexType();
        this.dstGraphVertexType = graphEdgeEntities.get(0).getDstVertexType();
        this.isOnlyGenerateEdgeSql = true;
    }

    public NebulaBatchEdgesUpdate(List<GraphEdgeEntity<S, T, E>> graphEdgeEntities, List<GraphVertexEntity<S>> srcGraphVertexEntities,
                                  List<GraphVertexEntity<T>> dstGraphVertexEntities) throws NebulaException {
        this.graphEdgeEntities = graphEdgeEntities;
        CheckThrower.ifTrueThrow(CollectionUtils.isEmpty(graphEdgeEntities), ErrorEnum.UPDATE_FIELD_DATA_NOT_EMPTY);
        this.srcGraphVertexEntities = srcGraphVertexEntities;
        this.dstGraphVertexEntities = dstGraphVertexEntities;
        this.srcGraphVertexType = graphEdgeEntities.get(0).getSrcVertexType();
        this.dstGraphVertexType = graphEdgeEntities.get(0).getDstVertexType();
        this.isOnlyGenerateEdgeSql = false;
    }


    private List<String> getDstVertexSql() throws NebulaException {
        if (CollectionUtils.isNotEmpty(dstGraphVertexEntities)) {
            NebulaBatchVertexUpdate nebulaUpdateBatchVertex = new NebulaBatchVertexUpdate(dstGraphVertexEntities);
            return nebulaUpdateBatchVertex.getSqlList();
        }
        return Collections.emptyList();
    }

    private List<String> getSrcVertexSql() throws NebulaException {
        if (CollectionUtils.isNotEmpty(this.srcGraphVertexEntities)) {
            NebulaBatchVertexUpdate nebulaUpdateBatchVertex = new NebulaBatchVertexUpdate(this.srcGraphVertexEntities);
            return nebulaUpdateBatchVertex.getSqlList();
        }
        return Collections.emptyList();
    }


    @Override
    public List<String> getSqlList() throws NebulaException {
        List<String> sqlList = getEdgeSql();
        if (isOnlyGenerateEdgeSql) {
            return sqlList;
        }
        sqlList.addAll(this.getSrcVertexSql());
        sqlList.addAll(this.getDstVertexSql());
        return sqlList;
    }

    private List<String> getEdgeSql() throws NebulaException {
        if (this.graphEdgeEntities.size() == 1) {
            String sql = getOneSql();
            return Lists.newArrayList(sql);
        }
        return getMultiSql();
    }

    /**
     * 获取单边sql
     *
     * @return
     */
    private String getOneSql() throws NebulaException {
        return generateSql(this.graphEdgeEntities.get(0));
    }

    /**
     * 获取多边sql
     *
     * @return
     */
    private List<String> getMultiSql() throws NebulaException {
        List<String> sqlList = Lists.newArrayListWithExpectedSize(this.graphEdgeEntities.size());
        for (GraphEdgeEntity<S, T, E> graphEdgeEntity : this.graphEdgeEntities) {
            String sql = generateSql(graphEdgeEntity);
            sqlList.add(sql);
        }
        return StringUtil.aggregate(sqlList, sqlList.size(), ";");
    }

    private String generateSql(GraphEdgeEntity<S, T, E> graphEdgeEntity) throws NebulaException {
        String src = GraphHelper.getQuerySrcId(graphEdgeEntity.getGraphEdgeType(), graphEdgeEntity.getSrcId());
        String end = GraphHelper.getQueryDstId(graphEdgeEntity.getGraphEdgeType(), graphEdgeEntity.getDstId());
        Set<Map.Entry<String, Object>> entries = graphEdgeEntity.getProps().entrySet();
        StringBuilder sqlBuilder = new StringBuilder();
        Map<String, GraphDataTypeEnum> dataTypeMap = graphEdgeEntity.getGraphEdgeType().getDataTypeMap();
        for (Map.Entry<String, Object> entry : entries) {
            GraphDataTypeEnum graphDataTypeEnum = dataTypeMap.get(entry.getKey());
            if (GraphDataTypeEnum.STRING.equals(graphDataTypeEnum)) {
                sqlBuilder.append(",").append(entry.getKey()).append("=\"").append(entry.getValue()).append("\"");
            } else {
                sqlBuilder.append(",").append(entry.getKey()).append("=").append(entry.getValue());
            }
        }
        String sqlFieldSet = sqlBuilder.delete(0, 1).toString();
        return String.format(UPSET_SQL_FORMAT, src, end, graphEdgeEntity.getGraphEdgeType().getEdgeName(), sqlFieldSet);
    }


    @Override
    public List<GraphEdgeEntity<S, T, E>> getGraphEdgeEntityList() {
        return this.graphEdgeEntities;
    }

    @Override
    public GraphEdgeType<S, T, E> getGraphEdgeType() {
        return this.graphEdgeEntities.get(0).getGraphEdgeType();
    }

    @Override
    public List<GraphLabel> getLabels() {
        List<GraphLabel> list = Lists.newArrayList();
        GraphEdgeType<S, T, E> graphEdgeType = this.getGraphEdgeType();
        list.add(graphEdgeType);
        list.add(graphEdgeType.getSrcVertexType());
        list.add(graphEdgeType.getDstVertexType());
        return list;
    }

}

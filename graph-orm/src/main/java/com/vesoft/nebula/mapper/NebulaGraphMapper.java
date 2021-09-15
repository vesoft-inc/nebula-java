/* Copyright (c) 2021 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */
package com.vesoft.nebula.mapper;

import com.google.common.collect.Lists;
import com.vesoft.nebula.client.graph.exception.AuthFailedException;
import com.vesoft.nebula.client.graph.exception.IOErrorException;
import com.vesoft.nebula.client.graph.exception.NotValidConnectionException;
import com.vesoft.nebula.common.utils.CollectionUtils;
import com.vesoft.nebula.dao.*;
import com.vesoft.nebula.dao.impl.DefaultGraphEdgeEntityFactory;
import com.vesoft.nebula.dao.impl.DefaultGraphTypeManager;
import com.vesoft.nebula.dao.impl.DefaultGraphVertexEntityFactory;
import com.vesoft.nebula.domain.EdgeQuery;
import com.vesoft.nebula.domain.GraphLabel;
import com.vesoft.nebula.domain.GraphQuery;
import com.vesoft.nebula.domain.VertexQuery;
import com.vesoft.nebula.domain.impl.*;
import com.vesoft.nebula.engine.*;
import com.vesoft.nebula.enums.EdgeDirectionEnum;
import com.vesoft.nebula.enums.ErrorEnum;
import com.vesoft.nebula.exception.CheckThrower;
import com.vesoft.nebula.exception.NebulaException;
import com.vesoft.nebula.session.NebulaPoolSessionManager;
import com.vesoft.nebula.session.NebulaSessionWrapper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Description  NebulaGraphMapper is used for
 *
 * @author huangzhaolai-jk
 * Date  2021/7/16 - 17:37
 * @version 1.0.0
 */
@Slf4j
public class NebulaGraphMapper implements GraphMapper {

    @Setter
    @Getter
    private NebulaPoolSessionManager nebulaPoolSessionManager;

    private GraphUpdateEdgeEngineFactory graphUpdateEdgeEngineFactory;

    private GraphUpdateVertexEngineFactory graphUpdateVertexEngineFactory;

    @Setter
    @Getter
    private String space;

    private GraphVertexEntityFactory graphVertexEntityFactory;

    private GraphEdgeEntityFactory graphEdgeEntityFactory;

    private GraphTypeManager graphTypeManager;

    private void init() {
        this.graphVertexEntityFactory = new DefaultGraphVertexEntityFactory(graphTypeManager);
        this.graphEdgeEntityFactory = new DefaultGraphEdgeEntityFactory(graphTypeManager);
        NebulaCondition.setGraphTypeManager(graphTypeManager);
        NebulaVertexQuery.setGraphTypeManager(graphTypeManager);
        NebulaEdgeQuery.setGraphTypeManager(graphTypeManager);
    }

    public NebulaGraphMapper(NebulaPoolSessionManager nebulaPoolSessionManager,
                             String space) {
        this.graphTypeManager = new DefaultGraphTypeManager();
        this.graphUpdateVertexEngineFactory = new NebulaUpdateVertexEngineFactory();
        this.graphUpdateEdgeEngineFactory = new NebulaUpdateEdgeEngineFactory();
        this.nebulaPoolSessionManager = nebulaPoolSessionManager;
        this.space = space;
        init();
    }

    public NebulaGraphMapper(NebulaPoolSessionManager nebulaPoolSessionManager,
                             String space,
                             GraphUpdateVertexEngineFactory graphUpdateVertexEngineFactory,
                             GraphUpdateEdgeEngineFactory graphUpdateEdgeEngineFactory) {
        this.graphTypeManager = new DefaultGraphTypeManager();
        this.graphUpdateVertexEngineFactory = graphUpdateVertexEngineFactory;
        this.graphUpdateEdgeEngineFactory = graphUpdateEdgeEngineFactory;
        this.nebulaPoolSessionManager = nebulaPoolSessionManager;
        this.space = space;
        init();
    }

    private <T> int batchUpdateVertex(List<GraphVertexEntity<T>> graphVertexEntityList) throws NebulaException {
        VertexUpdateEngine build = this.graphUpdateVertexEngineFactory.build(graphVertexEntityList);
        List<String> sqlList = build.getSqlList();
        return executeBatchUpdateSql(space, sqlList);
    }

    @Override
    public <T> int saveVertexEntities(List<T> entities) throws NebulaException {
        if (CollectionUtils.isEmpty(entities)) {
            return 0;
        }
        List<GraphVertexEntity<T>> vertexEntities = Lists.newArrayListWithExpectedSize(entities.size());
        for (T entity : entities) {
            GraphVertexEntity<T> graphVertexEntity = graphVertexEntityFactory.buildGraphVertexEntity(entity);
            vertexEntities.add(graphVertexEntity);
            log.debug("构造对象entity={},graphVertexEntity={}", entity, graphVertexEntity);
        }
        log.debug("保存顶点信息到nebula,size={}", CollectionUtils.size(vertexEntities));
        return batchUpdateVertex(vertexEntities);
    }

    private <S, T, E> int batchUpdateEdge(List<GraphEdgeEntity<S, T, E>> graphEdgeEntities) throws NebulaException {
        EdgeUpdateEngine<S, T, E> build = this.graphUpdateEdgeEngineFactory.build(graphEdgeEntities);
        List<String> sqlList = build.getSqlList();
        return executeBatchUpdateSql(space, sqlList);
    }

    @Override
    public <S, T, E> int saveEdgeEntitiesWithVertex(List<E> entities, Function<String, S> srcVertexEntityFunction,
                                                    Function<String, T> dstVertexEntityFunction) throws NebulaException {
        if (CollectionUtils.isEmpty(entities)) {
            return 0;
        }
        List<GraphEdgeEntity<S, T, E>> graphEdgeEntities = Lists.newArrayListWithExpectedSize(entities.size());
        List<GraphVertexEntity<S>> srcGraphVertexEntities = Lists.newArrayListWithExpectedSize(entities.size());
        List<GraphVertexEntity<T>> dstGraphVertexEntities = Lists.newArrayListWithExpectedSize(entities.size());
        for (E entity : entities) {
            GraphEdgeEntity<S, T, E> graphEdgeEntity = graphEdgeEntityFactory.buildGraphEdgeEntity(entity);
            log.debug("构造对象entity={},graphEdgeEntity={}", entity, graphEdgeEntity);
            S srcEntity = srcVertexEntityFunction.apply(graphEdgeEntity.getSrcId());
            T dstEntity = dstVertexEntityFunction.apply(graphEdgeEntity.getDstId());
            GraphVertexEntity<S> srcVertexEntity = (GraphVertexEntity<S>) graphVertexEntityFactory.buildGraphVertexEntity(srcEntity);
            GraphVertexEntity<T> dstVertexEntity = (GraphVertexEntity<T>) graphVertexEntityFactory.buildGraphVertexEntity(dstEntity);
            srcGraphVertexEntities.add(srcVertexEntity);
            dstGraphVertexEntities.add(dstVertexEntity);
            graphEdgeEntities.add(graphEdgeEntity);
        }
        return batchUpdateEdgeWithVertex(graphEdgeEntities, srcGraphVertexEntities, dstGraphVertexEntities);
    }

    @Override
    public <S, T, E> int saveEdgeEntities(List<E> entities) throws NebulaException {
        if (CollectionUtils.isEmpty(entities)) {
            return 0;
        }
        List<GraphEdgeEntity<S, T, E>> graphEdgeEntities = Lists.newArrayListWithExpectedSize(entities.size());
        for (E entity : entities) {
            GraphEdgeEntity<S, T, E> graphEdgeEntity = graphEdgeEntityFactory.buildGraphEdgeEntity(entity);
            log.debug("构造对象entity={},graphEdgeEntity={}", entity, graphEdgeEntity);
            graphEdgeEntities.add(graphEdgeEntity);
        }
        return batchUpdateEdge(graphEdgeEntities);
    }

    private <S, T, E> int batchUpdateEdgeWithVertex(List<GraphEdgeEntity<S, T, E>> graphEdgeEntities,
                                                    List<GraphVertexEntity<S>> srcGraphVertexEntities,
                                                    List<GraphVertexEntity<T>> graphVertexEntities) throws NebulaException {
        EdgeUpdateEngine<S, T, E> build = this.graphUpdateEdgeEngineFactory.build(graphEdgeEntities,
                srcGraphVertexEntities, graphVertexEntities);
        List<String> sqlList = build.getSqlList();
        return executeBatchUpdateSql(space, sqlList);
    }

    @Override
    public int executeBatchUpdateSql(String space, List<String> sqlList) throws NebulaException {
        NebulaSessionWrapper session = null;
        try {
            session = nebulaPoolSessionManager.getSession(space);
            for (String sql : sqlList) {
                int execute = session.execute(sql);
                CheckThrower.ifTrueThrow(execute != 0, ErrorEnum.UPDATE_NEBULA_EROR);
            }
            return 0;
        } catch (IOErrorException | NotValidConnectionException | AuthFailedException e) {
            log.error("批量执行sql异常,space={},sqlList={}", space, sqlList, e);
            throw new NebulaException(ErrorEnum.SYSTEM_ERROR);
        } finally {
            if (session != null) {
                session.release();
            }
        }
    }

    @Override
    public int executeUpdateSql(String space, String sql) throws NebulaException, NotValidConnectionException {
        try {
            NebulaSessionWrapper session = nebulaPoolSessionManager.getSession(space);
            return session.execute(sql);
        } catch (IOErrorException | AuthFailedException e) {
            log.error("执行sql异常,space={},sql={}", space, sql, e);
            throw new NebulaException(ErrorEnum.SYSTEM_ERROR);
        }
    }

    @Override
    public int executeUpdateSql(String sql) throws NebulaException, NotValidConnectionException {
        return executeUpdateSql(this.space, sql);
    }

    @Override
    public QueryResult executeQuerySql(String sql) throws NebulaException {
        return executeQuerySql(this.space, sql);
    }

    @Override
    public QueryResult executeQuerySql(String space, String sql) throws NebulaException {
        NebulaSessionWrapper session = null;
        try {
            session = nebulaPoolSessionManager.getSession(space);
            return session.executeQueryDefined(sql);
        } catch (IOErrorException | NotValidConnectionException | AuthFailedException e) {
            log.error("执行sql异常,space={},sql={}", space, sql, e);
            throw new NebulaException(ErrorEnum.SYSTEM_ERROR);
        } finally {
            if (session != null) {
                session.release();
            }
        }
    }

    @Override
    public <T> List<T> executeQuerySql(String sql, Class<T> clazz) throws NebulaException {
        QueryResult rows = executeQuerySql(sql);
        GraphLabel graphLabel = graphTypeManager.getGraphLabel(clazz);
        for (QueryResult.Row row : rows) {
            for (Map.Entry<String, Object> entry : row) {
                String key = entry.getKey();
                String fieldName = graphLabel.getFieldName(key);
                Object o = graphLabel.reformatValue(fieldName, entry.getValue());
                row.setProp(key, o);
            }
        }
        return rows.getEntities(clazz);
    }

    @Override
    public QueryResult executeQuery(GraphQuery query) throws NebulaException {
        return executeQuerySql(query.buildSql());
    }

    @Override
    public QueryResult executeQuery(String space, GraphQuery query) throws NebulaException {
        return executeQuerySql(space, query.buildSql());
    }

    @Override
    public <T> List<T> executeQuery(GraphQuery query, Class<T> clazz) throws NebulaException {
        return executeQuerySql(query.buildSql(), clazz);
    }

    @Override
    public <T> List<T> goOutEdge(Class<T> edgeClazz, String... vertexIds) {
        GraphEdgeType<Object, Object, T> graphEdgeType = graphTypeManager.getGraphEdgeType(edgeClazz);
        String[] fieldsName = CollectionUtils.toStringArray(graphEdgeType.getAllFields());
        EdgeQuery query = NebulaEdgeQuery.build().goFrom(edgeClazz, vertexIds).yield(edgeClazz, fieldsName);
        return executeQuery(query, edgeClazz);
    }

    @Override
    public <T> List<T> goReverseEdge(Class<T> edgeClazz, String... vertexIds) {
        GraphEdgeType<Object, Object, T> graphEdgeType = graphTypeManager.getGraphEdgeType(edgeClazz);
        String[] fieldsName = CollectionUtils.toStringArray(graphEdgeType.getAllFields());
        EdgeQuery query = NebulaEdgeQuery.build().goFrom(edgeClazz, EdgeDirectionEnum.REVERSELY, vertexIds).yield(edgeClazz, fieldsName);
        return executeQuery(query, edgeClazz);
    }

    @Override
    public <T> List<T> fetchVertexTag(Class<T> vertexClazz, String... vertexIds) {
        GraphVertexType<T> graphVertexType = graphTypeManager.getGraphVertexType(vertexClazz);
        String[] fieldsName = CollectionUtils.toStringArray(graphVertexType.getAllFields());
        VertexQuery query = NebulaVertexQuery.build().fetchPropOn(vertexClazz, vertexIds).yield(vertexClazz, fieldsName);
        return executeQuery(query, vertexClazz);
    }

}

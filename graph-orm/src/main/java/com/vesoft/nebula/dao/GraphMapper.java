package com.vesoft.nebula.dao;


import com.vesoft.nebula.client.graph.exception.NotValidConnectionException;
import com.vesoft.nebula.domain.GraphQuery;
import com.vesoft.nebula.domain.impl.QueryResult;
import com.vesoft.nebula.exception.NebulaException;

import java.util.List;
import java.util.function.Function;

/**
 * Description  GraphMapper is used for
 *
 * @author huangzhaolai-jk
 * Date  2021/7/19 - 14:16
 * @version 1.0.0
 */
public interface GraphMapper {

    /**
     * 批量保存顶点信息
     *
     * @param entities
     * @param <T>
     * @return
     * @throws NebulaException
     */
    public <T> int saveVertexEntities(List<T> entities) throws NebulaException;


    /**
     * 批量保存边信息和顶点信息
     *
     * @param entities
     * @param srcVertexEntityFunction
     * @param dstVertexEntityFunction
     * @param <S>
     * @param <T>
     * @param <E>
     * @return
     * @throws NebulaException
     */
    public <S, T, E> int saveEdgeEntitiesWithVertex(List<E> entities, Function<String, S> srcVertexEntityFunction,
                                                    Function<String, T> dstVertexEntityFunction) throws NebulaException;


    /**
     * 批量保存边信息
     *
     * @param entities
     * @return
     * @throws NebulaException
     */
    public <S, T, E> int saveEdgeEntities(List<E> entities) throws NebulaException;


    /**
     * 批量执行更新语句
     *
     * @param space
     * @param sqlList
     * @return
     * @throws NebulaException
     * @throws NotValidConnectionException
     */
    public int executeBatchUpdateSql(String space, List<String> sqlList) throws NebulaException, NotValidConnectionException;


    /**
     * 执行更新sql
     *
     * @param space
     * @param sql
     * @return
     * @throws NebulaException
     * @throws NotValidConnectionException
     */
    public int executeUpdateSql(String space, String sql) throws NebulaException, NotValidConnectionException;


    /**
     * 执行更新sql
     *
     * @param sql
     * @return
     * @throws NebulaException
     * @throws NotValidConnectionException
     */
    public int executeUpdateSql(String sql) throws NebulaException, NotValidConnectionException;

    /**
     * 执行查询
     *
     * @param sql
     * @return
     * @throws NebulaException
     */
    public QueryResult executeQuerySql(String sql) throws NebulaException;


    /**
     * 执行查询
     *
     * @param space
     * @param sql
     * @return
     * @throws NebulaException
     */
    public QueryResult executeQuerySql(String space, String sql) throws NebulaException;


    /**
     * 执行查询
     *
     * @param sql
     * @param clazz
     * @param <T>
     * @return
     * @throws NebulaException
     */
    public <T> List<T> executeQuerySql(String sql, Class<T> clazz) throws NebulaException;


    /**
     * 执行查询
     *
     * @param query
     * @return
     * @throws NebulaException
     */
    public QueryResult executeQuery(GraphQuery query) throws NebulaException;


    /**
     * 指定空间执行查询
     *
     * @param space
     * @param query
     * @return
     * @throws NebulaException
     */
    public QueryResult executeQuery(String space, GraphQuery query) throws NebulaException;


    /**
     * 执行查询
     *
     * @param query
     * @param clazz
     * @param <T>
     * @return
     * @throws NebulaException
     */
    public <T> List<T> executeQuery(GraphQuery query, Class<T> clazz) throws NebulaException;


    /**
     * 查询边
     *
     * @param edgeClazz
     * @param vertexIds
     * @return
     */
    public <T> List<T> goOutEdge(Class<T> edgeClazz, String... vertexIds);

    /**
     * 查询反向边
     *
     * @param edgeClazz
     * @param vertexIds
     * @param <T>
     * @return
     */
    public <T> List<T> goReverseEdge(Class<T> edgeClazz, String... vertexIds);

    /**
     * 查询tag
     *
     * @param vertexClazz
     * @param vertexIds
     * @return
     */
    public <T> List<T> fetchVertexTag(Class<T> vertexClazz, String... vertexIds);

}

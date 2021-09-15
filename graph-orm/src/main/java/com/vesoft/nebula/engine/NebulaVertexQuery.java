package com.vesoft.nebula.engine;

import com.vesoft.nebula.dao.GraphTypeManager;
import com.vesoft.nebula.domain.GraphCondition;
import com.vesoft.nebula.domain.GraphExpression;
import com.vesoft.nebula.domain.GraphQuery;
import com.vesoft.nebula.domain.VertexQuery;
import com.vesoft.nebula.domain.impl.GraphVertexType;
import lombok.Getter;
import lombok.Setter;
import java.util.Map;

/**
 * Description  NebulaVertexQuery is used for
 *
 * @author huangzhaolai-jk
 * Date  2021/8/10 - 17:45
 * @version 1.0.0
 */
public class NebulaVertexQuery implements VertexQuery {

    @Getter
    @Setter
    private static GraphTypeManager graphTypeManager;

    private StringBuilder sqlBuilder = new StringBuilder();

    private NebulaVertexQuery() {
    }

    public static NebulaVertexQuery build() {
        return new NebulaVertexQuery();
    }

    @Override
    public VertexQuery fetchPropOn(Class clazz, String... vertexIds) {
        GraphVertexType graphVertexType = graphTypeManager.getGraphVertexType(clazz);
        String vertexName = graphVertexType.getVertexName();
        sqlBuilder.append("fetch prop on ").append(vertexName);
        NebulaQueryUtils.appendVertexId(graphVertexType, sqlBuilder, vertexIds);
        sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);
        return this;
    }

    @Override
    public VertexQuery connectAdd(GraphQuery graphQuery) {
        sqlBuilder.append(graphQuery.buildSql());
        return this;
    }

    @Override
    public String buildSql() {
        return this.sqlBuilder.toString();
    }

    @Override
    public VertexQuery limit(int size) {
        NebulaQueryUtils.limit(sqlBuilder, size);
        return this;
    }

    @Override
    public VertexQuery limit(int offset, int size) {
        NebulaQueryUtils.limit(sqlBuilder, offset, size);
        return this;
    }

    @Override
    public VertexQuery distinct() {
        NebulaQueryUtils.distinct(sqlBuilder);
        return this;
    }

    @Override
    public VertexQuery yield() {
        sqlBuilder.append(" yield ");
        return this;
    }

    @Override
    public VertexQuery yield(String symbol, Class clazz, String... fields) {
        NebulaQueryUtils.yield(graphTypeManager, sqlBuilder, symbol, clazz, fields);
        return this;
    }

    @Override
    public VertexQuery yield(Class clazz, String... fields) {
        NebulaQueryUtils.yield(graphTypeManager, sqlBuilder, clazz, fields);
        return this;
    }

    @Override
    public VertexQuery yield(String... fields) {
        NebulaQueryUtils.yield(sqlBuilder, fields);
        return this;
    }

    @Override
    public VertexQuery yield(Map<String, String> fieldAlias) {
        NebulaQueryUtils.yield(sqlBuilder, fieldAlias);
        return this;
    }

    @Override
    public VertexQuery yieldDistinct(Class clazz, String... fields) {
        NebulaQueryUtils.yieldDistinct(graphTypeManager, sqlBuilder, clazz, fields);
        return this;
    }

    @Override
    public VertexQuery yieldDistinct(String symbol, Class clazz, String... fields) {
        NebulaQueryUtils.yieldDistinct(graphTypeManager, sqlBuilder, symbol, clazz, fields);
        return this;
    }

    @Override
    public VertexQuery yieldDistinct(String... fields) {
        NebulaQueryUtils.yieldDistinct(sqlBuilder, fields);
        return this;
    }

    @Override
    public VertexQuery yieldDistinct(Map<String, String> fieldAlias) {
        NebulaQueryUtils.yieldDistinct(sqlBuilder, fieldAlias);
        return this;
    }

    @Override
    public VertexQuery pipe() {
        NebulaQueryUtils.pipe(sqlBuilder);
        return this;
    }

    @Override
    public GraphQuery unionAll(GraphQuery graphQuery) {
        NebulaQueryUtils.unionAll(sqlBuilder, graphQuery);
        return this;
    }

    @Override
    public GraphQuery union(GraphQuery graphQuery) {
        NebulaQueryUtils.union(sqlBuilder, graphQuery);
        return this;
    }

    @Override
    public VertexQuery groupBy(Class clazz, String... fields) {
        NebulaQueryUtils.groupBy(graphTypeManager, sqlBuilder, clazz, fields);
        return this;
    }

    @Override
    public VertexQuery groupBy(String... fields) {
        NebulaQueryUtils.groupBy(sqlBuilder, fields);
        return this;
    }

    @Override
    public VertexQuery countComma(String alias) {
        NebulaQueryUtils.countComma(sqlBuilder, alias);
        return this;
    }

    @Override
    public VertexQuery countComma(String field, String alias) {
        NebulaQueryUtils.countComma(sqlBuilder, field, alias);
        return this;
    }

    @Override
    public VertexQuery countComma(Class clazz, String field, String alias) {
        NebulaQueryUtils.countComma(graphTypeManager, sqlBuilder, clazz, field, alias);
        return this;
    }

    @Override
    public VertexQuery countComma(GraphExpression graphExpression, String alias) {
        NebulaQueryUtils.countComma(sqlBuilder, graphExpression, alias);
        return this;
    }

    @Override
    public VertexQuery count(String field, String alias) {
        NebulaQueryUtils.count(sqlBuilder, field, alias);
        return this;
    }

    @Override
    public VertexQuery count(Map<String, String> fieldAlias) {
        NebulaQueryUtils.count(sqlBuilder, fieldAlias);
        return this;
    }

    @Override
    public VertexQuery count(String alias) {
        NebulaQueryUtils.count(sqlBuilder, alias);
        return this;
    }

    @Override
    public VertexQuery count(Class clazz, String field, String alias) {
        NebulaQueryUtils.count(graphTypeManager, sqlBuilder, clazz, field, alias);
        return this;
    }

    @Override
    public VertexQuery count(GraphExpression graphExpression, String alias) {
        NebulaQueryUtils.count(sqlBuilder, graphExpression, alias);
        return this;
    }

    @Override
    public VertexQuery avg(GraphExpression graphExpression, String alias) {
        NebulaQueryUtils.avg(sqlBuilder, graphExpression, alias);
        return this;
    }

    @Override
    public VertexQuery avg(String field, String alias) {
        NebulaQueryUtils.avg(sqlBuilder, field, alias);
        return this;
    }

    @Override
    public VertexQuery avg(Class clazz, String field, String alias) {
        NebulaQueryUtils.avg(graphTypeManager, sqlBuilder, clazz, field, alias);
        return this;
    }

    @Override
    public VertexQuery avgComma(GraphExpression graphExpression, String alias) {
        NebulaQueryUtils.avgComma(sqlBuilder, graphExpression, alias);
        return this;
    }

    @Override
    public VertexQuery avgComma(String field, String alias) {
        NebulaQueryUtils.avgComma(sqlBuilder, field, alias);
        return this;
    }

    @Override
    public VertexQuery avgComma(Class clazz, String field, String alias) {
        NebulaQueryUtils.avgComma(graphTypeManager, sqlBuilder, clazz, field, alias);
        return this;
    }

    @Override
    public VertexQuery sum(GraphExpression graphExpression, String alias) {
        NebulaQueryUtils.sum(sqlBuilder, graphExpression, alias);
        return this;
    }

    @Override
    public VertexQuery sum(String field, String alias) {
        NebulaQueryUtils.sum(sqlBuilder, field, alias);
        return this;
    }

    @Override
    public VertexQuery sum(Class clazz, String field, String alias) {
        NebulaQueryUtils.sum(graphTypeManager, sqlBuilder, clazz, field, alias);
        return this;
    }

    @Override
    public VertexQuery sumComma(GraphExpression graphExpression, String alias) {
        NebulaQueryUtils.sumComma(sqlBuilder, graphExpression, alias);
        return this;
    }

    @Override
    public VertexQuery sumComma(String field, String alias) {
        NebulaQueryUtils.sumComma(sqlBuilder, field, alias);
        return this;
    }

    @Override
    public VertexQuery sumComma(Class clazz, String field, String alias) {
        NebulaQueryUtils.sumComma(graphTypeManager, sqlBuilder, clazz, field, alias);
        return this;
    }

    @Override
    public VertexQuery comma() {
        NebulaQueryUtils.comma(sqlBuilder);
        return this;
    }

    @Override
    public VertexQuery where(GraphCondition graphCondition) {
        NebulaQueryUtils.where(sqlBuilder, graphCondition);
        return this;
    }

}

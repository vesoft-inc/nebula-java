/* Copyright (c) 2021 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */
package com.vesoft.nebula.engine;

import com.vesoft.nebula.common.GraphHelper;
import com.vesoft.nebula.dao.GraphTypeManager;
import com.vesoft.nebula.domain.GraphCondition;
import com.vesoft.nebula.domain.GraphExpression;
import com.vesoft.nebula.domain.GraphLabel;
import com.vesoft.nebula.domain.GraphQuery;
import com.vesoft.nebula.domain.impl.GraphEdgeType;
import com.vesoft.nebula.domain.impl.GraphVertexType;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * Description  NebulaQueryUtils is used for
 *
 * @author huangzhaolai-jk
 * Date  2021/8/11 - 16:04
 * @version 1.0.0
 */
public class NebulaQueryUtils {

    public static void appendVertexSrcId(GraphEdgeType graphEdgeType, StringBuilder sqlBuilder, String... vertexIds) {
        for (String vertexId : vertexIds) {
            String vertexIdKey = GraphHelper.getQuerySrcId(graphEdgeType, vertexId);
            sqlBuilder.append(" ").append(vertexIdKey).append(",");
        }
    }

    public static void appendVertexDstId(GraphEdgeType graphEdgeType, StringBuilder sqlBuilder, String... vertexIds) {
        for (String vertexId : vertexIds) {
            String vertexIdKey = GraphHelper.getQueryDstId(graphEdgeType, vertexId);
            sqlBuilder.append(" ").append(vertexIdKey).append(",");
        }
    }

    public static void appendVertexId(GraphVertexType graphVertexType, StringBuilder sqlBuilder, String... vertexIds) {
        for (String vertexId : vertexIds) {
            String vertexIdKey = GraphHelper.getQueryId(graphVertexType, vertexId);
            sqlBuilder.append(" ").append(vertexIdKey).append(",");
        }
    }

    public static void yield(GraphTypeManager graphTypeManager, StringBuilder sqlBuilder, Class clazz, String... fields) {
        yield(graphTypeManager, sqlBuilder, null, clazz, fields);
    }

    public static void yield(GraphTypeManager graphTypeManager, StringBuilder sqlBuilder, String symbol, Class clazz, String... fields) {
        yieldWithDistinct(false, graphTypeManager, sqlBuilder, symbol, clazz, fields);
    }

    public static void limit(StringBuilder sqlBuilder, int size) {
        sqlBuilder.append(" | limit ").append(size);
    }

    public static void limit(StringBuilder sqlBuilder, int offset, int size) {
        sqlBuilder.append(" limit ").append(offset).append(",").append(size);
    }

    public static void distinct(StringBuilder sqlBuilder) {
        sqlBuilder.append(" distinct ");
    }

    public static void yield(StringBuilder sqlBuilder, String... fields) {
        sqlBuilder.append(" yield ");
        appendFields(sqlBuilder, fields);
    }

    public static void yield(StringBuilder sqlBuilder, Map<String, String> fieldAlias) {
        yieldWithDistinct(sqlBuilder, fieldAlias, false);
    }

    private static void yieldWithDistinct(boolean distinct, GraphTypeManager graphTypeManager, StringBuilder sqlBuilder, String prefix, Class clazz, String... fields) {
        GraphLabel graphLabel = graphTypeManager.getGraphLabel(clazz);
        String name = graphLabel.getName();
        sqlBuilder.append(" yield ");
        if (distinct) {
            sqlBuilder.append("distinct ");
        }
        for (String field : fields) {
            String fieldName = graphLabel.getFieldName(field);
            String propertyName = graphLabel.getPropertyName(fieldName);
            StringBuilder temp = new StringBuilder();
            if (StringUtils.isNotBlank(prefix)) {
                temp.append(prefix);
            }
            temp.append(name).append(".").append(fieldName).append(" as ").append(propertyName);
            sqlBuilder.append(temp).append(",");
        }
        sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);
    }

    public static void yieldDistinct(GraphTypeManager graphTypeManager, StringBuilder sqlBuilder, Class clazz, String... fields) {
        yieldDistinct(graphTypeManager, sqlBuilder, null, clazz, fields);
    }

    public static void yieldDistinct(GraphTypeManager graphTypeManager, StringBuilder sqlBuilder, String prefix, Class clazz, String... fields) {
        yieldWithDistinct(true, graphTypeManager, sqlBuilder, prefix, clazz, fields);
    }

    public static void yieldDistinct(StringBuilder sqlBuilder, String... fields) {
        sqlBuilder.append(" yield distinct ");
        appendFields(sqlBuilder, fields);
    }

    public static void yieldDistinct(StringBuilder sqlBuilder, Map<String, String> fieldAlias) {
        yieldWithDistinct(sqlBuilder, fieldAlias, true);
    }

    private static void yieldWithDistinct(StringBuilder sqlBuilder, Map<String, String> fieldAlias, boolean distinct) {
        sqlBuilder.append(" yield ");
        if (distinct) {
            sqlBuilder.append(" distinct ");
        }
        for (Map.Entry<String, String> entry : fieldAlias.entrySet()) {
            sqlBuilder.append(entry.getKey()).append(" as ").append(entry.getValue()).append(",");
        }
        sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);
    }

    public static void pipe(StringBuilder sqlBuilder) {
        sqlBuilder.append(" | ");
    }

    public static void unionAll(StringBuilder sqlBuilder, GraphQuery graphQuery) {
        sqlBuilder.append(" union all ").append(graphQuery.buildSql());
    }

    public static void union(StringBuilder sqlBuilder, GraphQuery graphQuery) {
        sqlBuilder.append(" union ").append(graphQuery.buildSql());
    }

    public static void groupBy(GraphTypeManager graphTypeManager, StringBuilder sqlBuilder, Class clazz, String... fields) {
        GraphLabel graphLabel = graphTypeManager.getGraphLabel(clazz);
        String name = graphLabel.getName();
        sqlBuilder.append(" group by ");
        for (String field : fields) {
            String fieldName = graphLabel.getFieldName(field);
            StringBuilder temp = new StringBuilder(name);
            temp.append(".").append(fieldName);
            sqlBuilder.append(temp).append(",");
        }
        sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);
    }

    public static void groupBy(StringBuilder sqlBuilder, String... fields) {
        sqlBuilder.append(" group by ");
        appendFields(sqlBuilder, fields);
    }

    public static void countComma(StringBuilder sqlBuilder, String alias) {
        count(sqlBuilder, alias);
        comma(sqlBuilder);
    }

    public static void count(StringBuilder sqlBuilder, String field, String alias) {
        sqlBuilder.append(" count(").append(field).append(")").append(" as ").append(alias);
    }

    public static void count(StringBuilder sqlBuilder, Map<String, String> fieldAlias) {
        for (Map.Entry<String, String> entry : fieldAlias.entrySet()) {
            sqlBuilder.append(" count(").append(entry.getKey()).append(")").append(" as ").append(entry.getValue()).append(",");
        }
        sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);
    }

    public static void countComma(StringBuilder sqlBuilder, String field, String alias) {
        count(sqlBuilder, field, alias);
        comma(sqlBuilder);
    }

    public static void countComma(GraphTypeManager graphTypeManager, StringBuilder sqlBuilder, Class clazz, String field, String alias) {
        count(graphTypeManager, sqlBuilder, clazz, field, alias);
        comma(sqlBuilder);
    }

    public static void countComma(StringBuilder sqlBuilder, GraphExpression graphExpression, String alias) {
        count(sqlBuilder, graphExpression, alias);
        comma(sqlBuilder);
    }


    public static void count(StringBuilder sqlBuilder, String alias) {
        sqlBuilder.append("count(*) as ").append(alias);
    }

    public static void count(GraphTypeManager graphTypeManager, StringBuilder sqlBuilder, Class clazz,
                             String field, String alias) {
        addFunctionOnFieldWithAlias(graphTypeManager, sqlBuilder, clazz, "count", field, alias);
    }

    private static void addFunctionOnFieldWithAlias(GraphTypeManager graphTypeManager, StringBuilder sqlBuilder, Class clazz,
                                                    String function, String field, String alias) {
        GraphLabel graphLabel = graphTypeManager.getGraphLabel(clazz);
        String name = graphLabel.getName();
        String fieldName = graphLabel.getFieldName(field);
        sqlBuilder.append(" ").append(function).append("(").append(name)
                .append(".").append(fieldName).append(")")
                .append(" as ").append(alias);
    }

    public static void count(StringBuilder sqlBuilder, GraphExpression graphExpression, String alias) {
        sqlBuilder.append(" count(").append(graphExpression.buildSql()).append(")").append(" as ").append(alias);
    }

    public static void avg(StringBuilder sqlBuilder, GraphExpression graphExpression, String alias) {
        avg(sqlBuilder, graphExpression.buildSql(), alias);
    }

    public static void avg(StringBuilder sqlBuilder, String field, String alias) {
        sqlBuilder.append(" avg(").append(field).append(")").append(" as ").append(alias);
    }

    public static void avg(GraphTypeManager graphTypeManager, StringBuilder sqlBuilder, Class clazz,
                           String field, String alias) {
        addFunctionOnFieldWithAlias(graphTypeManager, sqlBuilder, clazz, "avg", field, alias);
    }

    public static void avgComma(GraphTypeManager graphTypeManager, StringBuilder sqlBuilder, Class clazz,
                                String field, String alias) {
        avg(graphTypeManager, sqlBuilder, clazz, field, alias);
        comma(sqlBuilder);
    }

    public static void avgComma(StringBuilder sqlBuilder, GraphExpression graphExpression, String alias) {
        avg(sqlBuilder, graphExpression.buildSql(), alias);
        sqlBuilder.append(",");
    }

    public static void avgComma(StringBuilder sqlBuilder, String field, String alias) {
        avg(sqlBuilder, field, alias);
        sqlBuilder.append(",");
    }

    public static void sum(StringBuilder sqlBuilder, GraphExpression graphExpression, String alias) {
        sqlBuilder.append(" sum(").append(graphExpression.buildSql()).append(")").append(" as ").append(alias);
    }

    public static void sum(StringBuilder sqlBuilder, String field, String alias) {
        sqlBuilder.append(" sum(").append(field).append(")").append(" as ").append(alias);
    }

    public static void sum(GraphTypeManager graphTypeManager, StringBuilder sqlBuilder, Class clazz,
                           String field, String alias) {
        addFunctionOnFieldWithAlias(graphTypeManager, sqlBuilder, clazz, "sum", field, alias);
    }

    public static void sumComma(StringBuilder sqlBuilder, GraphExpression graphExpression, String alias) {
        sum(sqlBuilder, graphExpression.buildSql(), alias);
        comma(sqlBuilder);
    }

    public static void sumComma(StringBuilder sqlBuilder, String field, String alias) {
        sum(sqlBuilder, field, alias);
        comma(sqlBuilder);
    }

    public static void sumComma(GraphTypeManager graphTypeManager, StringBuilder sqlBuilder, Class clazz,
                                String field, String alias) {
        sum(graphTypeManager, sqlBuilder, clazz, field, alias);
        comma(sqlBuilder);
    }

    public static void comma(StringBuilder sqlBuilder) {
        sqlBuilder.append(",");
    }

    public static void where(StringBuilder sqlBuilder, GraphCondition graphCondition) {
        String graphConditionSql = graphCondition.buildSql();
        sqlBuilder.append(" where ").append(graphConditionSql);
    }

    private static void appendFields(StringBuilder sqlBuilder, String... fields) {
        for (String field : fields) {
            sqlBuilder.append(field).append(",");
        }
        sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);
    }

}

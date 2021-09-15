package com.vesoft.nebula.engine;

import com.google.common.collect.Lists;
import com.vesoft.nebula.dao.GraphTypeManager;
import com.vesoft.nebula.domain.GraphCondition;
import com.vesoft.nebula.domain.GraphExpression;
import com.vesoft.nebula.domain.GraphLabel;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

/**
 * Description  NebulaCondition is used for
 *
 * @author huangzhaolai-jk
 * Date  2021/8/10 - 17:47
 * @version 1.0.0
 */
public class NebulaCondition implements GraphCondition {

    @Getter
    @Setter
    private static GraphTypeManager graphTypeManager;

    private NebulaCondition() {
    }

    private StringBuilder conditionBuilder = new StringBuilder();

    public static NebulaCondition build() {
        return new NebulaCondition();
    }

    @Override
    public String buildSql() {
        return conditionBuilder.toString();
    }

    @Override
    public GraphCondition bracket() {
        conditionBuilder.insert(0, "(").append(")");
        return this;
    }

    @Override
    public GraphCondition and(GraphCondition graphCondition) {
        if (conditionBuilder.length() > 1) {
            conditionBuilder.append(" and ");
        }
        conditionBuilder.append(graphCondition.buildSql());
        return this;
    }

    @Override
    public <T> GraphCondition andIn(String field, Collection<T> collection) {
        andSymbol(field, " in ", collection);
        return this;
    }

    @Override
    public <T> GraphCondition andIn(Class clazz, String field, Collection<T> collection) {
        addShortClassCondition(clazz, field, " in ");
        buildValue(collection);
        return this;
    }

    @Override
    public <T> GraphCondition andNotIn(String field, Collection<T> collection) {
        andSymbol(field, " not in ", collection);
        return this;
    }

    @Override
    public <T> GraphCondition andNotIn(Class clazz, String field, Collection<T> collection) {
        addShortClassCondition(clazz, field, " not in ");
        buildValue(collection);
        return this;
    }

    private void buildValue(Object value) {
        if (value instanceof GraphExpression) {
            GraphExpression expression = (GraphExpression) value;
            conditionBuilder.append(expression.buildSql());
        } else if (value instanceof String) {
            conditionBuilder.append("'").append(value).append("'");
        } else if (value instanceof Collection) {
            Collection collection = (Collection) value;
            Collection tempCollection = Lists.newArrayListWithExpectedSize(collection.size());
            for (Object temp : collection) {
                if (temp instanceof String) {
                    tempCollection.add("'" + (String) temp + "'");
                } else {
                    tempCollection = collection;
                    break;
                }
            }
            conditionBuilder.append(tempCollection);
        } else {
            conditionBuilder.append(value);
        }
    }

    private void addCommonCondition(String field, String symbol, Object value) {
        if (conditionBuilder.length() > 0) {
            conditionBuilder.append(" and ");
        }
        conditionBuilder.append(" ").append(field).append(symbol);
        buildValue(value);
    }

    private void addShortClassCondition(Class clazz, String field, String symbol) {
        GraphLabel graphLabel = graphTypeManager.getGraphLabel(clazz);
        String fieldName = graphLabel.getFieldName(field);
        if (conditionBuilder.length() > 0) {
            conditionBuilder.append(" and ");
        }
        conditionBuilder.append(" ").append(graphLabel.getName()).append(".").append(fieldName).append(symbol);
    }

    private void addClassFormatCondition(Class clazz, String field, String symbol, Object value) {
        GraphLabel graphLabel = graphTypeManager.getGraphLabel(clazz);
        addShortClassCondition(clazz, field, symbol);
        String fieldName = graphLabel.getFieldName(field);
        buildValue(graphLabel.formatValue(fieldName, value));
    }

    private void addClassCondition(Class clazz, String field, String symbol, Object value) {
        addShortClassCondition(clazz, field, symbol);
        buildValue(value);
    }

    @Override
    public GraphCondition andEquals(String field, Object value) {
        addCommonCondition(field, "==", value);
        return this;
    }

    @Override
    public GraphCondition andSymbol(String field, String symbol, Object value) {
        addCommonCondition(field, symbol, value);
        return this;
    }

    @Override
    public GraphCondition andEqualsWithFinallyValue(Class clazz, String field, Object value) {
        this.addClassCondition(clazz, field, "==", value);
        return this;
    }

    @Override
    public GraphCondition andEqualsWithOriginalValue(Class clazz, String field, Object value) {
        this.addClassFormatCondition(clazz, field, "==", value);
        return this;
    }

    @Override
    public GraphCondition andBiggerEquals(String field, Object value) {
        addCommonCondition(field, " >= ", value);
        return this;
    }

    @Override
    public GraphCondition andBiggerEqualsWithFinallyValue(Class clazz, String field, Object value) {
        this.addClassCondition(clazz, field, ">=", value);
        return this;
    }

    @Override
    public GraphCondition andBiggerEqualsWithOriginalValue(Class clazz, String field, Object value) {
        this.addClassFormatCondition(clazz, field, ">=", value);
        return this;
    }

    @Override
    public GraphCondition andBigger(String field, Object value) {
        addCommonCondition(field, " > ", value);
        return this;
    }

    @Override
    public GraphCondition andBiggerWithOriginalValue(Class clazz, String field, Object value) {
        this.addClassFormatCondition(clazz, field, ">", value);
        return this;
    }

    @Override
    public GraphCondition andBiggerWithFinallyValue(Class clazz, String field, Object value) {
        this.addClassCondition(clazz, field, ">", value);
        return this;
    }

    @Override
    public GraphCondition andLessEquals(String field, Object value) {
        addCommonCondition(field, " <= ", value);
        return this;
    }

    @Override
    public GraphCondition andLessEqualsWithFinallyValue(Class clazz, String field, Object value) {
        this.addClassCondition(clazz, field, "<=", value);
        return this;
    }

    @Override
    public GraphCondition andLessEqualsWithOriginalValue(Class clazz, String field, Object value) {
        this.addClassFormatCondition(clazz, field, "<=", value);
        return this;
    }

    @Override
    public GraphCondition andLess(String field, Object value) {
        addCommonCondition(field, " < ", value);
        return this;
    }

    @Override
    public GraphCondition andLessWithOriginalValue(Class clazz, String field, Object value) {
        this.addClassFormatCondition(clazz, field, "<", value);
        return this;
    }

    @Override
    public GraphCondition andLessWithFinallyValue(Class clazz, String field, Object value) {
        this.addClassCondition(clazz, field, "<", value);
        return this;
    }

}

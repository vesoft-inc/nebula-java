/* Copyright (c) 2021 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */
package com.vesoft.nebula.engine;


import com.vesoft.nebula.domain.GraphExpression;

/**
 * Description  NebulaExpression is used for
 *
 * @author huangzhaolai-jk
 * Date  2021/8/19 - 10:59
 * @version 1.0.0
 */
public class NebulaExpression implements GraphExpression {

    private StringBuilder expressionBuilder = new StringBuilder();

    private NebulaExpression() {
    }

    public static NebulaExpression build() {
        return new NebulaExpression();
    }

    public static NebulaExpression build(String expression) {
        return new NebulaExpression().setExpression(expression);
    }

    public NebulaExpression setExpression(String expression) {
        this.expressionBuilder.append(expression);
        return this;
    }

    public NebulaExpression appendExpression(GraphExpression expression) {
        this.expressionBuilder.append(expression.buildSql());
        return this;
    }

    public NebulaExpression appendExpression(String expression) {
        this.expressionBuilder.append(expression);
        return this;
    }

    public NebulaExpression caseExpression(GraphExpression expression) {
        expressionBuilder.append(" case ").append(expression.buildSql());
        return this;
    }

    public NebulaExpression caseExpression(String expression) {
        expressionBuilder.append(" case ").append(expression);
        return this;
    }

    public NebulaExpression caseWhen(GraphExpression expression) {
        expressionBuilder.append(" case when ").append(expression.buildSql());
        return this;
    }

    public NebulaExpression caseWhen(String expression) {
        expressionBuilder.append(" case when ").append(expression);
        return this;
    }

    private String buildValue(Object value) {
        if (value instanceof GraphExpression) {
            GraphExpression expression = (GraphExpression) value;
            return expression.buildSql();
        } else if (value instanceof String) {
            return (String) value;
        } else {
            return value.toString();
        }
    }

    public NebulaExpression whenValue(Object value) {
        expressionBuilder.append(" when ").append(buildValue(value));
        return this;
    }

    public NebulaExpression thenValue(Object value) {
        expressionBuilder.append(" then ").append(buildValue(value));
        return this;
    }

    public NebulaExpression elseValue(Object value) {
        expressionBuilder.append(" else ").append(buildValue(value));
        return this;
    }

    public NebulaExpression endCase() {
        expressionBuilder.append(" end ");
        return this;
    }

    public NebulaExpression caseBooleanThenElseEnd(GraphExpression expression, Object thenValue, Object elseValue) {
        caseExpression(expression).whenValue(true).thenValue(thenValue).elseValue(elseValue).endCase();
        return this;
    }

    public NebulaExpression caseWhenThenEnd(GraphExpression expression, Object thenValue) {
        caseWhen(expression).thenValue(thenValue).endCase();
        return this;
    }

    public NebulaExpression caseWhenThenTrueEnd(GraphExpression expression) {
        caseWhen(expression).thenValue(true).endCase();
        return this;
    }

    public NebulaExpression caseBooleanThenElseEnd(String expression, Object thenValue, Object elseValue) {
        caseExpression(expression).whenValue(true).thenValue(thenValue).elseValue(elseValue).endCase();
        return this;
    }

    public NebulaExpression caseWhenThenTrueEnd(String expression) {
        caseWhen(expression).thenValue(true).endCase();
        return this;
    }

    @Override
    public String buildSql() {
        return expressionBuilder.toString();
    }
}

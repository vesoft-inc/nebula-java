/* Copyright (c) 2021 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */
package com.vesoft.nebula.domain;

import com.vesoft.nebula.enums.EdgeDirectionEnum;

import java.util.Map;

/**
 * Description  EdgeQuery is used for
 *
 * @author huangzhaolai-jk
 * Date  2021/8/10 - 14:18
 * @version 1.0.0
 */
public interface EdgeQuery extends GraphQuery {

    /**
     * 检索哪些顶点id的出边
     *
     * @param clazz
     * @param vertexIds
     * @return
     */
    public EdgeQuery goFrom(Class clazz, String... vertexIds);

    /**
     * 根据方向,检索哪些顶点id的边l
     *
     * @param clazz
     * @param directionEnum
     * @param vertexIds
     * @return
     */
    public EdgeQuery goFrom(Class clazz, EdgeDirectionEnum directionEnum, String... vertexIds);

    /**
     * 正向查询边steps步
     *
     * @param clazz
     * @param vertexIds
     * @param steps
     * @return
     */
    public EdgeQuery goFromSteps(Class clazz, int steps, String... vertexIds);

    /**
     * 正向查询边：fromSteps-toSteps步之内
     *
     * @param clazz
     * @param vertexIds
     * @param fromSteps
     * @param toSteps
     * @return
     */
    public EdgeQuery goFromSteps(Class clazz, int fromSteps, int toSteps, String... vertexIds);


    /**
     * 根据方向,检索顶点id的stpes步内的边
     *
     * @param clazz
     * @param directionEnum
     * @param vertexIds
     * @param steps
     * @return
     */
    public EdgeQuery goFromSteps(Class clazz, EdgeDirectionEnum directionEnum, int steps, String... vertexIds);

    /**
     * 根据方向,检索顶点id的fromSteps - toSteps跳步内的边
     *
     * @param clazz
     * @param directionEnum
     * @param vertexIds
     * @param fromSteps
     * @param toSteps
     * @return
     */
    public EdgeQuery goFromSteps(Class clazz, EdgeDirectionEnum directionEnum, int fromSteps, int toSteps, String... vertexIds);


    /**
     * 连接两个查询片段
     *
     * @param graphQuery
     * @return
     */
    @Override
    public EdgeQuery connectAdd(GraphQuery graphQuery);

    /**
     * limit
     *
     * @param size
     * @return
     */
    @Override
    public EdgeQuery limit(int size);

    /**
     * limit
     *
     * @param offset
     * @param size
     * @return
     */
    @Override
    public EdgeQuery limit(int offset, int size);

    /**
     * 去重
     *
     * @return
     */
    @Override
    public EdgeQuery distinct();


    /**
     * 添加yield关键字
     *
     * @return
     */
    @Override
    public EdgeQuery yield();


    /**
     * 查询哪个标签的哪些属性
     *
     * @param clazz
     * @param fields
     * @return
     */
    @Override
    public EdgeQuery yield(Class clazz, String... fields);

    /**
     * 查询哪个标签的哪些属性
     *
     * @param symbol
     * @param clazz
     * @param fields
     * @return
     */
    @Override
    public EdgeQuery yield(String symbol, Class clazz, String... fields);

    /**
     * 查询哪些属性
     *
     * @param fields
     * @return
     */
    @Override
    public EdgeQuery yield(String... fields);

    /**
     * 查询哪些属性
     *
     * @param fieldAlias 字段与别名映射
     * @return
     */
    @Override
    public EdgeQuery yield(Map<String, String> fieldAlias);


    /**
     * 查询哪个标签的哪些属性
     *
     * @param clazz
     * @param fields
     * @return
     */
    @Override
    public EdgeQuery yieldDistinct(Class clazz, String... fields);

    /**
     * 查询哪个标签的哪些属性
     *
     * @param prefix
     * @param clazz
     * @param fields
     * @return
     */
    @Override
    public EdgeQuery yieldDistinct(String prefix, Class clazz, String... fields);


    /**
     * 查询哪些属性
     *
     * @param fields
     * @return
     */
    @Override
    public EdgeQuery yieldDistinct(String... fields);


    /**
     * 查询哪些属性
     *
     * @param fieldAlias 字段与别名映射
     * @return
     */
    @Override
    public EdgeQuery yieldDistinct(Map<String, String> fieldAlias);


    /**
     * 管道分隔符
     *
     * @return
     */
    @Override
    public EdgeQuery pipe();

    /**
     * 根据某些属性分组查询
     *
     * @param clazz
     * @param fields
     * @return
     */
    @Override
    public EdgeQuery groupBy(Class clazz, String... fields);


    /**
     * 根据某些属性分组查询
     *
     * @param fields
     * @return
     */
    @Override
    public EdgeQuery groupBy(String... fields);

    /**
     * count(*)
     *
     * @param alias
     * @return
     */
    @Override
    public EdgeQuery countComma(String alias);

    /**
     * 对某字段计数并取别名
     *
     * @param field
     * @param alias
     * @return
     */
    @Override
    public EdgeQuery countComma(String field, String alias);

    /**
     * 计算属性
     *
     * @param clazz
     * @param field
     * @param alias
     * @return
     */
    @Override
    public EdgeQuery countComma(Class clazz, String field, String alias);

    /**
     * count条件表达式
     *
     * @param graphExpression
     * @param alias
     * @return
     */
    @Override
    public EdgeQuery countComma(GraphExpression graphExpression, String alias);


    /**
     * 对某字段计数并取别名
     *
     * @param field
     * @param alias
     * @return
     */
    @Override
    public EdgeQuery count(String field, String alias);

    /**
     * 对某些字段计数并取别名
     *
     * @param fieldAlias
     * @return
     */
    @Override
    public EdgeQuery count(Map<String, String> fieldAlias);

    /**
     * count(*)
     *
     * @param alias
     * @return
     */
    @Override
    public EdgeQuery count(String alias);

    /**
     * 计算属性
     *
     * @param clazz
     * @param field
     * @param alias
     * @return
     */
    @Override
    public EdgeQuery count(Class clazz, String field, String alias);


    /**
     * count条件表达式
     *
     * @param graphExpression
     * @param alias
     * @return
     */
    @Override
    public EdgeQuery count(GraphExpression graphExpression, String alias);

    /**
     * avg条件表达式
     *
     * @param graphExpression
     * @param alias
     * @return
     */
    @Override
    public EdgeQuery avg(GraphExpression graphExpression, String alias);

    /**
     * 某个字段的平均值
     *
     * @param field
     * @param alias
     * @return
     */
    @Override
    public EdgeQuery avg(String field, String alias);

    /**
     * 某个字段的平均值
     *
     * @param clazz
     * @param field
     * @param alias
     * @return
     */
    @Override
    public EdgeQuery avg(Class clazz, String field, String alias);

    /**
     * avgComma条件表达式
     *
     * @param graphExpression
     * @param alias
     * @return
     */
    @Override
    public EdgeQuery avgComma(GraphExpression graphExpression, String alias);

    /**
     * 某个字段的平均值，并且逗号分割
     *
     * @param field
     * @param alias
     * @return
     */
    @Override
    public EdgeQuery avgComma(String field, String alias);

    /**
     * 某个字段的平均值，并且逗号分割
     *
     * @param clazz
     * @param field
     * @param alias
     * @return
     */
    @Override
    public EdgeQuery avgComma(Class clazz, String field, String alias);

    /**
     * sum条件表达式
     *
     * @param graphExpression
     * @param alias
     * @return
     */
    @Override
    public EdgeQuery sum(GraphExpression graphExpression, String alias);

    /**
     * 某个字段的求和
     *
     * @param field
     * @param alias
     * @return
     */
    @Override
    public EdgeQuery sum(String field, String alias);

    /**
     * 某个字段的求和
     *
     * @param clazz
     * @param field
     * @param alias
     * @return
     */
    @Override
    public EdgeQuery sum(Class clazz, String field, String alias);

    /**
     * sumComma条件表达式
     *
     * @param graphExpression
     * @param alias
     * @return
     */
    @Override
    public EdgeQuery sumComma(GraphExpression graphExpression, String alias);

    /**
     * 某个字段的求和，并且逗号分割
     *
     * @param field
     * @param alias
     * @return
     */
    @Override
    public EdgeQuery sumComma(String field, String alias);

    /**
     * 某个字段的求和，并且逗号分割
     *
     * @param clazz
     * @param field
     * @param alias
     * @return
     */
    @Override
    public EdgeQuery sumComma(Class clazz, String field, String alias);


    /**
     * 逗号分隔
     *
     * @return
     */
    @Override
    public EdgeQuery comma();

    /**
     * 条件过滤
     *
     * @param graphCondition
     * @return
     */
    @Override
    public EdgeQuery where(GraphCondition graphCondition);

}

package com.vesoft.nebula.domain;

import java.util.Map;

/**
 * Description  GraphQuery is used for
 *
 * @author huangzhaolai-jk
 * Date  2021/8/10 - 14:18
 * @version 1.0.0
 */
public interface GraphQuery {

    /**
     * 构造sql
     *
     * @return
     */
    public String buildSql();

    /**
     * 连接两个查询片段
     *
     * @param graphQuery
     * @return
     */
    public GraphQuery connectAdd(GraphQuery graphQuery);

    /**
     * limit
     *
     * @param size
     * @return
     */
    public GraphQuery limit(int size);

    /**
     * limit
     *
     * @param offset
     * @param size
     * @return
     */
    public GraphQuery limit(int offset, int size);


    /**
     * 去重
     *
     * @return
     */
    public GraphQuery distinct();

    /**
     * 添加yield关键字
     *
     * @return
     */
    public GraphQuery yield();


    /**
     * 查询哪个标签的哪些属性
     *
     * @param clazz
     * @param fields
     * @return
     */
    public GraphQuery yield(Class clazz, String... fields);


    /**
     * 查询哪个标签的哪些属性
     *
     * @param symbol
     * @param clazz
     * @param fields
     * @return
     */
    public GraphQuery yield(String symbol, Class clazz, String... fields);


    /**
     * 查询哪些属性
     *
     * @param fields
     * @return
     */
    public GraphQuery yield(String... fields);


    /**
     * 查询哪些属性
     *
     * @param fieldAlias 字段与别名映射
     * @return
     */
    public GraphQuery yield(Map<String, String> fieldAlias);


    /**
     * 查询哪个标签的哪些属性
     *
     * @param clazz
     * @param fields
     * @return
     */
    public GraphQuery yieldDistinct(Class clazz, String... fields);


    /**
     * 查询哪个标签的哪些属性
     *
     * @param symbol
     * @param clazz
     * @param fields
     * @return
     */
    public GraphQuery yieldDistinct(String symbol, Class clazz, String... fields);


    /**
     * 查询哪些属性
     *
     * @param fields
     * @return
     */
    public GraphQuery yieldDistinct(String... fields);


    /**
     * 查询哪些属性
     *
     * @param fieldAlias 字段与别名映射
     * @return
     */
    public GraphQuery yieldDistinct(Map<String, String> fieldAlias);


    /**
     * 管道分隔符
     *
     * @return
     */
    public GraphQuery pipe();

    /**
     * 连接两个查询结果，查询列和列名需要一一对应
     *
     * @param graphQuery
     * @return
     */
    public GraphQuery unionAll(GraphQuery graphQuery);

    /**
     * 连接两个查询结果，查询列和列名需要一一对应，会去除重复结果
     *
     * @param graphQuery
     * @return
     */
    public GraphQuery union(GraphQuery graphQuery);

    /**
     * 根据某些属性分组查询
     *
     * @param clazz
     * @param fields
     * @return
     */
    public GraphQuery groupBy(Class clazz, String... fields);


    /**
     * 根据某些属性分组查询
     *
     * @param fields
     * @return
     */
    public GraphQuery groupBy(String... fields);

    /**
     * count(*)
     *
     * @param alias
     * @return
     */
    public GraphQuery countComma(String alias);


    /**
     * 对某字段计数并取别名
     *
     * @param field
     * @param alias
     * @return
     */
    public GraphQuery countComma(String field, String alias);

    /**
     * 计算属性
     *
     * @param clazz
     * @param field
     * @param alias
     * @return
     */
    public GraphQuery countComma(Class clazz, String field, String alias);

    /**
     * count条件表达式
     *
     * @param graphExpression
     * @param alias
     * @return
     */
    public GraphQuery countComma(GraphExpression graphExpression, String alias);


    /**
     * 对某字段计数并取别名
     *
     * @param field
     * @param alias
     * @return
     */
    public GraphQuery count(String field, String alias);


    /**
     * 对某些字段计数并取别名
     *
     * @param fieldAlias
     * @return
     */
    public GraphQuery count(Map<String, String> fieldAlias);

    /**
     * count(*)
     *
     * @param alias
     * @return
     */
    public GraphQuery count(String alias);

    /**
     * 计算属性
     *
     * @param clazz
     * @param field
     * @param alias
     * @return
     */
    public GraphQuery count(Class clazz, String field, String alias);


    /**
     * count条件表达式
     *
     * @param graphExpression
     * @param alias
     * @return
     */
    public GraphQuery count(GraphExpression graphExpression, String alias);

    /**
     * avg条件表达式
     *
     * @param graphExpression
     * @param alias
     * @return
     */
    public GraphQuery avg(GraphExpression graphExpression, String alias);

    /**
     * 某个字段的平均值
     *
     * @param field
     * @param alias
     * @return
     */
    public GraphQuery avg(String field, String alias);

    /**
     * 某个字段的平均值
     *
     * @param clazz
     * @param field
     * @param alias
     * @return
     */
    public GraphQuery avg(Class clazz, String field, String alias);

    /**
     * avgComma条件表达式
     *
     * @param graphExpression
     * @param alias
     * @return
     */
    public GraphQuery avgComma(GraphExpression graphExpression, String alias);

    /**
     * 某个字段的平均值，并且逗号分割
     *
     * @param field
     * @param alias
     * @return
     */
    public GraphQuery avgComma(String field, String alias);

    /**
     * 某个字段的平均值，并且逗号分割
     *
     * @param clazz
     * @param field
     * @param alias
     * @return
     */
    public GraphQuery avgComma(Class clazz, String field, String alias);

    /**
     * sum条件表达式
     *
     * @param graphExpression
     * @param alias
     * @return
     */
    public GraphQuery sum(GraphExpression graphExpression, String alias);

    /**
     * 某个字段的求和
     *
     * @param field
     * @param alias
     * @return
     */
    public GraphQuery sum(String field, String alias);

    /**
     * 某个字段的求和
     *
     * @param clazz
     * @param field
     * @param alias
     * @return
     */
    public GraphQuery sum(Class clazz, String field, String alias);

    /**
     * sumComma条件表达式
     *
     * @param graphExpression
     * @param alias
     * @return
     */
    public GraphQuery sumComma(GraphExpression graphExpression, String alias);

    /**
     * 某个字段的求和，并且逗号分割
     *
     * @param field
     * @param alias
     * @return
     */
    public GraphQuery sumComma(String field, String alias);

    /**
     * 某个字段的求和，并且逗号分割
     *
     * @param clazz
     * @param field
     * @param alias
     * @return
     */
    public GraphQuery sumComma(Class clazz, String field, String alias);

    /**
     * 逗号分隔
     *
     * @return
     */
    public GraphQuery comma();

    /**
     * 条件过滤
     *
     * @param graphCondition
     * @return
     */
    public GraphQuery where(GraphCondition graphCondition);


}

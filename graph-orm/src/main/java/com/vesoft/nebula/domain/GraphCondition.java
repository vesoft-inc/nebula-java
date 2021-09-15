package com.vesoft.nebula.domain;

import java.util.Collection;

/**
 * Description  GraphCondition is used for
 *
 * @author huangzhaolai-jk
 * Date  2021/8/10 - 16:02
 * @version 1.0.0
 */
public interface GraphCondition extends GraphExpression {

    /**
     * 将已有的条件过滤括号起来
     *
     * @return
     */
    public GraphCondition bracket();

    /**
     * 并关系连接
     *
     * @return
     */
    public GraphCondition and(GraphCondition graphCondition);

    /**
     * in过滤
     *
     * @param field
     * @param collection
     * @return
     */
    public <T> GraphCondition andIn(String field, Collection<T> collection);

    /**
     * in过滤
     *
     * @param clazz
     * @param field
     * @param collection
     * @return
     */
    public <T> GraphCondition andIn(Class clazz, String field, Collection<T> collection);

    /**
     * not in过滤
     *
     * @param field
     * @param collection
     * @return
     */
    public <T> GraphCondition andNotIn(String field, Collection<T> collection);

    /**
     * not in过滤
     *
     * @param clazz
     * @param field
     * @param collection
     * @return
     */
    public <T> GraphCondition andNotIn(Class clazz, String field, Collection<T> collection);

    /**
     * 最真实的等于条件过滤
     *
     * @param field
     * @param value
     * @return
     */
    public GraphCondition andEquals(String field, Object value);

    /**
     * 自定义且条件l
     *
     * @param field
     * @param symbol
     * @param value
     * @return
     */
    public GraphCondition andSymbol(String field, String symbol, Object value);

    /**
     * 查询过滤，用真实的过滤值
     *
     * @param clazz
     * @param field
     * @param value
     * @return
     */
    public GraphCondition andEqualsWithFinallyValue(Class clazz, String field, Object value);


    /**
     * 查询过滤，用代加工的值
     * 当加工器不存在时效果等同于 andEqualsWithFinallyValue
     *
     * @param clazz
     * @param field
     * @param value
     * @return
     */
    public GraphCondition andEqualsWithOriginalValue(Class clazz, String field, Object value);

    /**
     * 最真实的大于等于
     *
     * @param field
     * @param value
     * @return
     */
    public GraphCondition andBiggerEquals(String field, Object value);

    /**
     * 大于等于最终值
     *
     * @param clazz
     * @param field
     * @param value
     * @return
     */
    public GraphCondition andBiggerEqualsWithFinallyValue(Class clazz, String field, Object value);


    /**
     * 大于等于源值
     *
     * @param clazz
     * @param field
     * @param value
     * @return
     */
    public GraphCondition andBiggerEqualsWithOriginalValue(Class clazz, String field, Object value);

    /**
     * 最真实的大于
     *
     * @param field
     * @param value
     * @return
     */
    public GraphCondition andBigger(String field, Object value);

    /**
     * 大于源值
     *
     * @param clazz
     * @param field
     * @param value
     * @return
     */
    public GraphCondition andBiggerWithOriginalValue(Class clazz, String field, Object value);


    /**
     * 大于最终值
     *
     * @param clazz
     * @param field
     * @param value
     * @return
     */
    public GraphCondition andBiggerWithFinallyValue(Class clazz, String field, Object value);

    /**
     * 最真实的小于等于
     *
     * @param field
     * @param value
     * @return
     */
    public GraphCondition andLessEquals(String field, Object value);

    /**
     * 小于等于最终值
     *
     * @param clazz
     * @param field
     * @param value
     * @return
     */
    public GraphCondition andLessEqualsWithFinallyValue(Class clazz, String field, Object value);

    /**
     * 小于等于源值
     *
     * @param clazz
     * @param field
     * @param value
     * @return
     */
    public GraphCondition andLessEqualsWithOriginalValue(Class clazz, String field, Object value);

    /**
     * 最真实的小于
     *
     * @param field
     * @param value
     * @return
     */
    public GraphCondition andLess(String field, Object value);

    /**
     * 小于源值
     *
     * @param clazz
     * @param field
     * @param value
     * @return
     */
    public GraphCondition andLessWithOriginalValue(Class clazz, String field, Object value);


    /**
     * 小于最终值
     *
     * @param clazz
     * @param field
     * @param value
     * @return
     */
    public GraphCondition andLessWithFinallyValue(Class clazz, String field, Object value);


}

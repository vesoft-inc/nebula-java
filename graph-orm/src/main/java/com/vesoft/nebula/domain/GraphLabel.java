/* Copyright (c) 2021 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */
package com.vesoft.nebula.domain;

import com.vesoft.nebula.enums.GraphDataTypeEnum;

import java.util.Collection;
import java.util.List;

/**
 * Description  GraphLabel is used for
 *
 * @author huangzhaolai-jk
 * Date  2021/8/10 - 11:18
 * @version 1.0.0
 */
public interface GraphLabel {

    /**
     * 是否Tag
     *
     * @return
     */
    public boolean isTag();

    /**
     * 是否关系(边)
     *
     * @return
     */
    public boolean isEdge();

    /**
     * 获取标签名称
     *
     * @return
     */
    public String getName();

    /**
     * 获取必要字段
     *
     * @return
     */
    public List<String> getMustFields();

    /**
     * 获取所有字段
     *
     * @return
     */
    public Collection<String> getAllFields();

    /**
     * 格式化属性值
     *
     * @param field
     * @param originalValue
     * @return
     */
    public Object formatValue(String field, Object originalValue);

    /**
     * 反转格式化属性值
     *
     * @param field
     * @param databaseValue
     * @return
     */
    public Object reformatValue(String field, Object databaseValue);

    /**
     * 获取字段名
     *
     * @param property
     * @return
     */
    public String getFieldName(String property);

    /**
     * 获取属性名
     *
     * @param field
     * @return
     */
    public String getPropertyName(String field);

    /**
     * 获取字段的数据类型
     *
     * @param field
     * @return
     */
    public GraphDataTypeEnum getFieldDataType(String field);

}

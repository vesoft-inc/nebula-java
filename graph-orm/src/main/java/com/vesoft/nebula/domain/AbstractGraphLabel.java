/* Copyright (c) 2021 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */
package com.vesoft.nebula.domain;

import com.vesoft.nebula.dao.GraphValueFormatter;
import com.vesoft.nebula.enums.GraphDataTypeEnum;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Description  AbstractGraphLabel is used for
 *
 * @author huangzhaolai-jk
 * Date  2021/8/10 - 18:32
 * @version 1.0.0
 */
@Getter
@Setter
public abstract class AbstractGraphLabel implements GraphLabel {

    /**
     * 必要字段
     */
    protected List<String> mustFields;

    /**
     * 所有属性和字段映射
     */
    protected Map<String, String> propertyFieldMap;

    protected Map<String, GraphValueFormatter> propertyFormatMap;

    /**
     * 字段的数据类型
     */
    protected Map<String, GraphDataTypeEnum> dataTypeMap;

    @Override
    public Object formatValue(String field, Object originalValue) {
        GraphValueFormatter graphValueFormatter = this.propertyFormatMap.get(field);
        if (graphValueFormatter != null) {
            return graphValueFormatter.format(originalValue);
        }
        return originalValue;
    }

    @Override
    public Object reformatValue(String field, Object databaseValue) {
        GraphValueFormatter graphValueFormatter = this.propertyFormatMap.get(field);
        if (graphValueFormatter != null) {
            return graphValueFormatter.reformat(databaseValue);
        }
        return databaseValue;
    }

    @Override
    public String getFieldName(String property) {
        String fieldName = this.propertyFieldMap.get(property);
        if (StringUtils.isBlank(fieldName)) {
            fieldName = property;
        }
        return fieldName;
    }

    @Override
    public String getPropertyName(String field) {
        for (Map.Entry<String, String> next : this.propertyFieldMap.entrySet()) {
            if (Objects.equals(next.getValue(), field)) {
                return next.getKey();
            }
        }
        return field;
    }

    @Override
    public GraphDataTypeEnum getFieldDataType(String field) {
        GraphDataTypeEnum graphDataTypeEnum = this.dataTypeMap.get(field);
        if (graphDataTypeEnum == null) {
            graphDataTypeEnum = GraphDataTypeEnum.STRING;
        }
        return graphDataTypeEnum;
    }

    @Override
    public Collection<String> getAllFields() {
        return propertyFieldMap.values();
    }

}

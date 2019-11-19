/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.graph.client;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.vesoft.nebula.graph.ColumnValue;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

/**
 *
 */
public class NebulaRow {
    private List<ColumnValue> columns;
    private Map<String, Integer> fieldIndex = Maps.newHashMap();

    /**
     *
     */
    public NebulaRow() {

    }

    /**
     * @param field
     * @return
     */
    public Integer getFieldIndex(String field) {
        checkArgument(!Strings.isNullOrEmpty(field));
        checkArgument(fieldIndex.containsKey(field));
        return fieldIndex.get(field);
    }

    /**
     * @param field
     * @return
     */
    public boolean exist(String field) {
        return fieldIndex.containsKey(field);
    }

    /**
     * @param index
     * @return
     */
    public String getString(int index) {
        byte[] value = columns.get(index).getStr();
        return new String(value, Charset.defaultCharset());
    }

    /**
     * @param field
     * @return
     */
    public String getString(String field) {
        Integer index = getFieldIndex(field);
        return getString(index);
    }

    /**
     * @param index
     * @return
     */
    public Long getLong(int index) {
        return columns.get(index).getInteger();
    }

    /**
     * @param field
     * @return
     */
    public Long getLong(String field) {
        Integer index = getFieldIndex(field);
        return getLong(index);
    }

    /**
     * @param index
     * @return
     */
    public Double getDouble(int index) {
        double value = columns.get(index).getDouble_precision();
        return value;
    }

    /**
     * @param field
     * @return
     */
    public Double getDouble(String field) {
        Integer index = getFieldIndex(field);
        return getDouble(index);
    }


    /**
     * @param index
     * @return
     */
    public Float getFloat(int index) {
        float value = columns.get(index).getSingle_precision();
        return value;
    }

    /**
     * @param field
     * @return
     */
    public Float getFloat(String field) {
        Integer index = getFieldIndex(field);
        return getFloat(index);
    }
}

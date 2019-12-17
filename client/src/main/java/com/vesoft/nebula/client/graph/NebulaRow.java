/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.graph;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.vesoft.nebula.graph.ColumnValue;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class NebulaRow {
    private List<ColumnValue> columns;
    private Map<String, Integer> fieldIndex = Maps.newHashMap();

    /**
     * Constructor
     */
    public NebulaRow() {

    }

    /**
     * Get field index by field name.
     *
     * @param field field name
     * @return
     */
    public Integer getFieldIndex(String field) {
        checkArgument(!Strings.isNullOrEmpty(field));
        checkArgument(fieldIndex.containsKey(field));
        return fieldIndex.get(field);
    }

    /**
     * Check whether the field is exist.
     *
     * @param field field name
     * @return
     */
    public boolean exist(String field) {
        return fieldIndex.containsKey(field);
    }

    /**
     * Get string value by field index.
     *
     * @param index field index
     * @return
     */
    public String getString(int index) {
        byte[] value = columns.get(index).getStr();
        return new String(value, Charset.defaultCharset());
    }

    /**
     * Get string value by field name.
     *
     * @param field field name
     * @return
     */
    public String getString(String field) {
        Integer index = getFieldIndex(field);
        return getString(index);
    }

    /**
     * Get long value by field index.
     *
     * @param index field index
     * @return
     */
    public Long getLong(int index) {
        return columns.get(index).getInteger();
    }

    /**
     * Get long value by field name.
     *
     * @param field field name
     * @return
     */
    public Long getLong(String field) {
        Integer index = getFieldIndex(field);
        return getLong(index);
    }

    /**
     * Get double value by field index.
     *
     * @param index field index
     * @return
     */
    public Double getDouble(int index) {
        double value = columns.get(index).getDouble_precision();
        return value;
    }

    /**
     * Get double value by field name.
     *
     * @param field field name
     * @return
     */
    public Double getDouble(String field) {
        Integer index = getFieldIndex(field);
        return getDouble(index);
    }


    /**
     * Get float value by field index.
     *
     * @param index field index
     * @return
     */
    public Float getFloat(int index) {
        float value = columns.get(index).getSingle_precision();
        return value;
    }

    /**
     * Get float value by field name.
     *
     * @param field field name
     * @return
     */
    public Float getFloat(String field) {
        Integer index = getFieldIndex(field);
        return getFloat(index);
    }
}

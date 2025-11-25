/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.data;

import com.vesoft.nebula.driver.graph.decode.ColumnType;
import com.vesoft.nebula.proto.common.Record;
import com.vesoft.nebula.proto.common.Value;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class NRecord {
    private       ColumnType                type = ColumnType.COLUMN_TYPE_RECORD;
    private final Map<String, ValueWrapper> map  = new HashMap<>();

    public NRecord(Map<String, ValueWrapper> map) {
        this.map.putAll(map);
    }

    /**
     * returns true if this record contains a mapping for the specified key. The key cannot be null.
     *
     * @param key key whose presence in this record is to be checked
     * @return true if this record contains a mapping for the specified key
     * @throws NullPointerException if the specified key is null
     */
    public boolean containsKey(String key) {
        if (key == null) {
            throw new NullPointerException("null map key");
        }
        return map.containsKey(key);
    }


    /**
     * get the Value of specified key in this record
     *
     * @param key key whose corresponding value in this record will be returned
     * @return The ValueWrapper of the specified key
     */
    public ValueWrapper getValue(String key) {
        if (!containsKey(key)) {
            return null;
        }
        return map.get(key);
    }

    /**
     * Returns true if this record has no values
     *
     * @return true if this record contains no key-value mappings
     */
    public boolean isEmpty() {
        return map == null || map.isEmpty();
    }

    /**
     * get the number of key-value mappings in this record.
     *
     * @return the number of key-value mappings in this record
     */
    public int size() {
        return map.size();
    }

    /**
     * get the Map object for this record
     *
     * @return Map for this record
     */
    public Map<String, ValueWrapper> getValuesMap() {
        return map;
    }

    @Override
    public String toString() {
        return map.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        NRecord that = (NRecord) o;
        return map.equals(that.getValuesMap());
    }

    @Override
    public int hashCode() {
        return Objects.hash(map);
    }
}

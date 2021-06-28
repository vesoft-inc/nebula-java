/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.graph.data;

import com.vesoft.nebula.Edge;
import com.vesoft.nebula.Value;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Relationship extends BaseDataObject {
    private final Edge edge;

    public Relationship(Edge edge) {
        if (edge == null) {
            throw new RuntimeException("Input an null edge object");
        }
        this.edge = edge;
    }

    /**
     * get the src id from the relationship
     * @return ValueWrapper, if int id, you can call srcId().asLong(),
     *     if string id, you can call srcId().asString()
     */
    public ValueWrapper srcId() {
        return edge.type > 0 ? new ValueWrapper(edge.src, getDecodeType(), getTimezoneOffset())
            : new ValueWrapper(edge.dst, getDecodeType(), getTimezoneOffset());
    }

    /**
     * get the dst id from the relationship
     * @return ValueWrapper, if int id, you can call srcId().asLong(),
     *     if string id, you can call srcId().asString()
     */
    public ValueWrapper dstId() {
        return edge.type > 0 ? new ValueWrapper(edge.dst, getDecodeType(), getTimezoneOffset())
            : new ValueWrapper(edge.src, getDecodeType(), getTimezoneOffset());
    }

    /**
     * get edge name from the relationship
     * @return String
     */
    public String edgeName() {
        return new String(edge.name);
    }

    /**
     * get ranking from the relationship
     * @return long
     */
    public long ranking() {
        return edge.ranking;
    }

    public List<String> keys() throws UnsupportedEncodingException {
        List<String> propNames = new ArrayList<>();
        for (byte[] name : edge.props.keySet()) {
            propNames.add(new String(name, getDecodeType()));
        }
        return propNames;
    }

    /**
     * get property values from the relationship
     * @return the List of ValueWrapper
     */
    public List<ValueWrapper> values() {
        List<ValueWrapper> propVals = new ArrayList<>();
        for (Value val : edge.props.values()) {
            propVals.add(new ValueWrapper(val, getDecodeType(), getTimezoneOffset()));
        }
        return propVals;
    }

    /**
     * get property names and values from the relationship
     * @return the HashMap, key is String, value is ValueWrapper>
     */
    public HashMap<String, ValueWrapper> properties() throws UnsupportedEncodingException {
        HashMap<String, ValueWrapper> properties = new HashMap<>();
        for (byte[] key : edge.props.keySet()) {
            properties.put(new String(key, getDecodeType()),
                new ValueWrapper(edge.props.get(key), getDecodeType(), getTimezoneOffset()));
        }
        return properties;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Relationship that = (Relationship) o;
        return ranking() == that.ranking()
            && Objects.equals(srcId(), that.srcId())
            && Objects.equals(dstId(), that.dstId())
            && Objects.equals(edgeName(), that.edgeName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(edge, getDecodeType(), getTimezoneOffset());
    }

    @Override
    public String toString() {
        try {
            List<String> propStrs = new ArrayList<>();
            Map<String, ValueWrapper> props = properties();
            for (String key : props.keySet()) {
                propStrs.add(key + ": " + props.get(key).toString());
            }
            return String.format("(%s)-[:%s@%d{%s}]->(%s)",
                srcId(), edgeName(), ranking(), String.join(", ", propStrs), dstId());
        } catch (UnsupportedEncodingException e) {
            return e.getMessage();
        }
    }
}

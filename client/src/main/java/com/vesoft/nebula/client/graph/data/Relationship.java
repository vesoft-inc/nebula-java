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

public class Relationship {
    private final Edge edge;
    private final String decodeType = "utf-8";

    public Relationship(Edge edge) {
        if (edge == null) {
            throw new RuntimeException("Input an null edge object");
        }
        this.edge = edge;
    }

    public ValueWrapper srcId() throws UnsupportedEncodingException {
        return new ValueWrapper(edge.src, decodeType);
    }

    public ValueWrapper dstId() throws UnsupportedEncodingException {
        return new ValueWrapper(edge.dst, decodeType);
    }

    public String edgeName() {
        return new String(edge.name);
    }

    public long ranking() {
        return edge.ranking;
    }

    public List<String> keys() throws UnsupportedEncodingException {
        List<String> propNames = new ArrayList<>();
        for (byte[] name : edge.props.keySet()) {
            propNames.add(new String(name, decodeType));
        }
        return propNames;
    }

    public List<ValueWrapper> values() {
        List<ValueWrapper> propVals = new ArrayList<>();
        for (Value val : edge.props.values()) {
            propVals.add(new ValueWrapper(val, decodeType));
        }
        return propVals;
    }

    public HashMap<String, ValueWrapper> properties() throws UnsupportedEncodingException {
        HashMap<String, ValueWrapper> properties = new HashMap<>();
        for (byte[] key : edge.props.keySet()) {
            properties.put(new String(key, decodeType),
                new ValueWrapper(edge.props.get(key), decodeType));
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
        try {
            return ranking() == that.ranking()
                && Objects.equals(srcId(), that.srcId())
                && Objects.equals(dstId(), that.dstId())
                && Objects.equals(edgeName(), that.edgeName());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(edge, decodeType);
    }

    @Override
    public String toString() {
        try {
            List<String> propStrs = new ArrayList<>();
            Map<String, ValueWrapper> props = properties();
            for (String key : props.keySet()) {
                propStrs.add(key + ": " + props.get(key).toString());
            }
            return String.format("(\"%s\")-[:%s@%d{%s}]->(\"%s\")",
                srcId(), edgeName(), ranking(), String.join(", ", propStrs), dstId());
        } catch (UnsupportedEncodingException e) {
            return e.getMessage();
        }
    }
}

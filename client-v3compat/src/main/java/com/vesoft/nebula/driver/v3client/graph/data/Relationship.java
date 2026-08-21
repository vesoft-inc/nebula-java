/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.v3client.graph.data;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Wrapper around a graph relationship (edge), matching the v3 client's {@code Relationship} API.
 */
public class Relationship extends BaseDataObject {

    private final com.vesoft.nebula.driver.graph.data.Edge edge;

    public Relationship(com.vesoft.nebula.driver.graph.data.Edge edge) {
        if (edge == null) {
            throw new RuntimeException("Input an null edge object");
        }
        this.edge = edge;
    }

    /**
     * @return the source id as a {@link ValueWrapper}.
     */
    public ValueWrapper srcId() {
        return ValueWrapper.ofLong(edge.getSrcId());
    }

    /**
     * @return the destination id as a {@link ValueWrapper}.
     */
    public ValueWrapper dstId() {
        return ValueWrapper.ofLong(edge.getDstId());
    }

    /**
     * @return the edge name. The v3 client used the edge label as the name; the v5 driver stores
     *     the label(s) separately from the edge type name, so prefer the first label.
     */
    public String edgeName() {
        if (edge.getLabels() != null && !edge.getLabels().isEmpty()) {
            return edge.getLabels().get(0);
        }
        return edge.getType();
    }

    /**
     * @return the rank of the edge.
     */
    public long ranking() {
        return edge.getRank();
    }

    public List<String> keys() throws UnsupportedEncodingException {
        return new ArrayList<>(edge.getColumnNames());
    }

    public List<ValueWrapper> values() {
        List<ValueWrapper> propVals = new ArrayList<>();
        for (com.vesoft.nebula.driver.graph.data.ValueWrapper val : edge.getPropertyValues()) {
            propVals.add(new ValueWrapper(val, getTimezoneOffset()));
        }
        return propVals;
    }

    public HashMap<String, ValueWrapper> properties() throws UnsupportedEncodingException {
        HashMap<String, ValueWrapper> properties = new HashMap<>();
        for (Map.Entry<String, com.vesoft.nebula.driver.graph.data.ValueWrapper> entry
            : edge.getProperties().entrySet()) {
            properties.put(entry.getKey(), new ValueWrapper(entry.getValue(), getTimezoneOffset()));
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
        return edge.getRank() == that.edge.getRank()
            && edge.getSrcId() == that.edge.getSrcId()
            && edge.getDstId() == that.edge.getDstId()
            && Objects.equals(edge.getType(), that.edge.getType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(edge.getType(), edge.getRank(), edge.getSrcId(), edge.getDstId(),
                            getDecodeType(), getTimezoneOffset());
    }

    @Override
    public String toString() {
        List<String> propStrs = new ArrayList<>();
        for (Map.Entry<String, com.vesoft.nebula.driver.graph.data.ValueWrapper> entry
            : edge.getProperties().entrySet()) {
            propStrs.add(entry.getKey() + ": " + entry.getValue().toString());
        }
        return String.format("(%d)-[:%s@%d{%s}]->(%d)",
                             edge.getSrcId(), edge.getType(), edge.getRank(),
                             String.join(", ", propStrs), edge.getDstId());
    }
}

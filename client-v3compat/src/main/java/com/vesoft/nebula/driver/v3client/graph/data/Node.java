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
 * Wrapper around a graph node (vertex), matching the v3 client's {@code Node} API.
 *
 * <p>The v5 driver models a node as a single node type with a flat property map plus a list of
 * labels, so the v3 per-tag accessors ({@link #values(String)}, {@link #keys(String)},
 * {@link #properties(String)}) validate the given label and return the flat property set.
 */
public class Node extends BaseDataObject {

    private final com.vesoft.nebula.driver.graph.data.Node node;

    public Node(com.vesoft.nebula.driver.graph.data.Node node) {
        if (node == null) {
            throw new RuntimeException("Input an null node object");
        }
        this.node = node;
    }

    /**
     * @return the node id as a {@link ValueWrapper}; call {@code getId().asLong()}.
     */
    public ValueWrapper getId() {
        return ValueWrapper.ofLong(node.getId());
    }

    /**
     * @return the node type name(s) exposed as labels.
     */
    public List<String> tagNames() {
        return new ArrayList<>(node.getLabels());
    }

    /**
     * @return the labels of the node (alias of {@link #tagNames()}).
     */
    public List<String> labels() {
        return node.getLabels();
    }

    public boolean hasTagName(String tagName) {
        return node.getLabels().contains(tagName);
    }

    public boolean hasLabel(String tagName) {
        return node.getLabels().contains(tagName);
    }

    public List<ValueWrapper> values(String tagName) {
        checkTagName(tagName);
        return new ArrayList<>(propertiesFor(tagName).values());
    }

    public List<String> keys(String tagName) throws UnsupportedEncodingException {
        checkTagName(tagName);
        return new ArrayList<>(propertiesFor(tagName).keySet());
    }

    public HashMap<String, ValueWrapper> properties(String tagName)
        throws UnsupportedEncodingException {
        checkTagName(tagName);
        return propertiesFor(tagName);
    }

    private void checkTagName(String tagName) {
        if (!node.getLabels().contains(tagName)) {
            throw new IllegalArgumentException(tagName + " is not found");
        }
    }

    private HashMap<String, ValueWrapper> propertiesFor(String tagName) {
        HashMap<String, ValueWrapper> properties = new HashMap<>();
        for (Map.Entry<String, com.vesoft.nebula.driver.graph.data.ValueWrapper> entry
            : node.getProperties().entrySet()) {
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
        Node node = (Node) o;
        return Objects.equals(this.node.getId(), node.node.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(node.getId(), getDecodeType(), getTimezoneOffset());
    }

    @Override
    public String toString() {
        List<String> tagsStr = new ArrayList<>();
        Map<String, com.vesoft.nebula.driver.graph.data.ValueWrapper> props = node.getProperties();
        List<String> propStrs = new ArrayList<>();
        for (Map.Entry<String, com.vesoft.nebula.driver.graph.data.ValueWrapper> entry
            : props.entrySet()) {
            propStrs.add(entry.getKey() + ": " + entry.getValue().toString());
        }
        for (String name : node.getLabels()) {
            tagsStr.add(String.format(":%s {%s}", name, String.join(", ", propStrs)));
        }
        return String.format("(%d %s)", node.getId(), String.join(" ", tagsStr));
    }
}

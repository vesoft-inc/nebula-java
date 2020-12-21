/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.graph.data;

import com.vesoft.nebula.Tag;
import com.vesoft.nebula.Value;
import com.vesoft.nebula.Vertex;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Node {
    private Vertex vertex;
    private ValueWrapper vid;
    private String decodeType = "utf-8";
    List<String> tagNames = new ArrayList<>();

    public Node(Vertex vertex) throws UnsupportedEncodingException {
        if (vertex == null) {
            throw new RuntimeException("Input an null vertex object");
        }
        vid = new ValueWrapper(vertex.vid, this.decodeType);
        this.vertex = vertex;
        for (Tag tag : vertex.tags) {
            this.tagNames.add(new String(tag.name, decodeType));
        }
    }

    public ValueWrapper getId() {
        return vid;
    }

    public List<String> labels() {
        return tagNames;
    }

    public boolean hasLabel(String tagName) {
        return tagNames.contains(tagName);
    }

    public List<ValueWrapper> values(String tagName) {
        int index = tagNames.indexOf(tagName);
        if (index < 0) {
            throw new IllegalArgumentException(tagName + " is not found");
        }
        List<ValueWrapper> values = new ArrayList<>();
        for (Value val : vertex.tags.get(index).props.values()) {
            values.add(new ValueWrapper(val, decodeType));
        }
        return values;
    }

    public List<String> keys(String tagName) throws UnsupportedEncodingException {
        int index = tagNames.indexOf(tagName);
        if (index < 0) {
            throw new IllegalArgumentException(tagName + " is not found");
        }

        List<String> keys = new ArrayList<>();
        for (byte[] name : vertex.tags.get(index).props.keySet()) {
            keys.add(new String(name, decodeType));
        }
        return keys;
    }

    public HashMap<String, ValueWrapper> properties(String tagName)
        throws UnsupportedEncodingException {
        int index = tagNames.indexOf(tagName);
        if (index < 0) {
            throw new IllegalArgumentException(tagName + " is not found");
        }

        HashMap<String, ValueWrapper> properties = new HashMap();
        for (byte[] name : vertex.tags.get(index).props.keySet()) {
            properties.put(new String(name, decodeType),
                new ValueWrapper(vertex.tags.get(index).props.get(name), decodeType));
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
        return Objects.equals(vid, node.vid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vertex, vid, decodeType, tagNames);
    }

    @Override
    public String toString() {
        try {
            List<String> tagsStr = new ArrayList<>();
            List<String> propStrs = new ArrayList<>();
            for (String name : labels()) {
                Map<String, ValueWrapper> props = properties(name);
                for (String key : props.keySet()) {
                    propStrs.add(key + ": " + props.get(key).toString());
                }
                tagsStr.add(String.format(":%s {%s}", name, String.join(", ", propStrs)));
            }
            return String.format("(\"%s\" %s)", getId(), String.join(" ", tagsStr));
        } catch (UnsupportedEncodingException e) {
            return e.getMessage();
        }
    }
}

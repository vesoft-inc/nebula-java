/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
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

public class Node extends BaseDataObject {
    private final Vertex vertex;
    private final ValueWrapper vid;

    List<String> tagNames = new ArrayList<>();

    /**
     * Node is a wrapper around the Vertex type returned by nebula-graph
     * @param vertex the vertex returned by nebula-graph
     * @throws UnsupportedEncodingException if decoded binary failed
     */
    public Node(Vertex vertex) throws UnsupportedEncodingException {
        if (vertex == null) {
            throw new RuntimeException("Input an null vertex object");
        }
        vid = new ValueWrapper(vertex.vid, getDecodeType(), getTimezoneOffset());
        this.vertex = vertex;
        for (Tag tag : vertex.tags) {
            this.tagNames.add(new String(tag.name, getDecodeType()));
        }
    }

    /**
     * get vid from the node
     * @return ValueWrapper, if int id, you can call getId().asLong(),
     *     if string id, you can call getId().asString()
     */
    public ValueWrapper getId() {
        return vid;
    }

    /**
     * get all tag name from the node
     * @return the list of tag name
     */
    public List<String> tagNames() {
        return tagNames;
    }

    /**
     * Used to be compatible with older versions of interfaces
     * @return the list of tag name
     */
    public List<String> labels() {
        return tagNames;
    }

    /**
     * determine if node contains the given tag
     * @param tagName the tag name
     * @return boolean
     */
    public boolean hasTagName(String tagName) {
        return tagNames.contains(tagName);
    }

    /**
     * Used to be compatible with older versions of interfaces
     * @return tboolean
     */
    public boolean hasLabel(String tagName) {
        return tagNames.contains(tagName);
    }

    /**
     * get property values from the node
     * @param tagName the tag name
     * @return the list of ValueWrapper
     */
    public List<ValueWrapper> values(String tagName) {
        int index = tagNames.indexOf(tagName);
        if (index < 0) {
            throw new IllegalArgumentException(tagName + " is not found");
        }
        List<ValueWrapper> values = new ArrayList<>();
        for (Value val : vertex.tags.get(index).props.values()) {
            values.add(new ValueWrapper(val, getDecodeType(), getTimezoneOffset()));
        }
        return values;
    }

    /**
     * get property names from the node
     * @param tagName the given tag name
     * @return the list of property names
     * @throws UnsupportedEncodingException decode error exception
     */
    public List<String> keys(String tagName) throws UnsupportedEncodingException {
        int index = tagNames.indexOf(tagName);
        if (index < 0) {
            throw new IllegalArgumentException(tagName + " is not found");
        }

        List<String> keys = new ArrayList<>();
        for (byte[] name : vertex.tags.get(index).props.keySet()) {
            keys.add(new String(name, getDecodeType()));
        }
        return keys;
    }

    /**
     * get property names and values from the node
     * @param tagName the given tag name
     * @return the HashMap, key is property name, value is ValueWrapper
     * @throws UnsupportedEncodingException decode error exception
     */
    public HashMap<String, ValueWrapper> properties(String tagName)
        throws UnsupportedEncodingException {
        int index = tagNames.indexOf(tagName);
        if (index < 0) {
            throw new IllegalArgumentException(tagName + " is not found");
        }

        HashMap<String, ValueWrapper> properties = new HashMap();
        for (byte[] name : vertex.tags.get(index).props.keySet()) {
            properties.put(new String(name, getDecodeType()),
                new ValueWrapper(vertex.tags.get(index).props.get(name),
                                 getDecodeType(),
                                 getTimezoneOffset()));
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
        return Objects.hash(vertex, vid, getDecodeType(), getTimezoneOffset(), tagNames);
    }

    @Override
    public String toString() {
        try {
            List<String> tagsStr = new ArrayList<>();
            for (String name : tagNames()) {
                List<String> propStrs = new ArrayList<>();
                Map<String, ValueWrapper> props = properties(name);
                for (String key : props.keySet()) {
                    propStrs.add(key + ": " + props.get(key).toString());
                }
                tagsStr.add(String.format(":%s {%s}", name, String.join(", ", propStrs)));
            }
            return String.format("(%s %s)", getId(), String.join(" ", tagsStr));
        } catch (UnsupportedEncodingException e) {
            return e.getMessage();
        }
    }
}

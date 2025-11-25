/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Path {
    private String decodeType = "utf-8";

    private List<ValueWrapper> values;

    private List<Node> nodes = new ArrayList<>();
    private List<Edge> edges = new ArrayList<>();

    public Path(List<ValueWrapper> values) {
        this.values = values;
        for (ValueWrapper value : values) {
            if (value.isNode()) {
                nodes.add(value.asNode());
            } else {
                edges.add(value.asEdge());
            }
        }
    }

    /**
     * Create a list over the nodes in this path, nodes will appear in the same order as they appear
     * in the path.
     *
     * @return a List of all nodes in this path
     */
    public List<Node> nodes() {
        return nodes;
    }


    /**
     * Create a list over the edge in this path. The edges will appear
     * in the same order as they appear in the path.
     *
     * @return a List of all edges in this path
     */
    public List<Edge> edges() {
        return edges;
    }

    /**
     * Create a list over the nodes and edges in this path. The value will appear
     * in the same order as they appear in the path. The first value will be Node type, then the
     * next one will be edge type, and next one will be Node type.
     *
     * @return a List of all values in this path
     */
    public List<ValueWrapper> values() {
        return values;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Path that = (Path) obj;
        return values.equals(that.values);
    }

    @Override
    public int hashCode() {
        return values.hashCode();
    }


    public String toString() {
        if (values.size() == 0) {
            return null;
        }

        Node         prefixNode         = nodes.get(0);
        List<String> prefixNodePropStrs = new ArrayList<>();
        Map<String, ValueWrapper> prefixNodeProps    = prefixNode.getProperties();
        for (String key : prefixNodeProps.keySet()) {
            prefixNodePropStrs.add(key + ":" + prefixNodeProps.get(key).toString());
        }

        // just one node in the path
        if (edges.size() == 0) {
            String template = "(%d@%s:%s{%s})";
            return String.format(template, prefixNode.getId(),
                                 prefixNode.getType(),
                                 String.join("&", prefixNode.getLabels()),
                                 String.join(",", prefixNodePropStrs));
        }

        List<String> edgeStrs = new ArrayList<>();
        for (int i = 0; i < edges.size(); i++) {
            Edge edge = edges.get(i);

            List<String>              edgePropStrs = new ArrayList<>();
            Map<String, ValueWrapper> props        = edge.getProperties();
            for (String key : props.keySet()) {
                edgePropStrs.add(key + ":" + props.get(key).toString());
            }


            Node         suffixNode         = nodes.get(i + 1);
            List<String> suffixNodePropStrs = new ArrayList<>();
            Map<String, ValueWrapper> suffixNodeProps    = suffixNode.getProperties();
            for (String key : suffixNodeProps.keySet()) {
                suffixNodePropStrs.add(key + ":" + suffixNodeProps.get(key).toString());
            }

            String template;
            if (i == 0) {
                template = "(%d@%s:%s{%s})~[%d@%s:%s{%s}]~(%d@%s:%s{%s})";
                if (edge.isDirected() && edge.getSrcId() == prefixNode.getId()) {
                    template = "(%d@%s:%s{%s})-[%d@%s:%s{%s}]->(%d@%s:%s{%s})";
                }
                if (edge.isDirected() && edge.getSrcId() != prefixNode.getId()) {
                    template = "(%d@%s:%s{%s})<-[%d@%s:%s{%s}]-(%d@%s:%s{%s})";
                }

                edgeStrs.add(String.format(template,
                                           prefixNode.getId(),
                                           prefixNode.getType(),
                                           String.join("&", prefixNode.getLabels()),
                                           String.join(",", prefixNodePropStrs),
                                           edge.getRank(),
                                           edge.getType(),
                                           String.join("&", edge.getLabels()),
                                           String.join(",", edgePropStrs),
                                           suffixNode.getId(),
                                           suffixNode.getType(),
                                           String.join("&", suffixNode.getLabels()),
                                           String.join(",", suffixNodePropStrs)));
            } else {
                template = "~[%d@%s:%s{%s}]~(%d@%s:%s{%s})";
                if (edge.isDirected() && edge.getDstId() == suffixNode.getId()) {
                    template = "-[%d@%s:%s{%s}]->(%d@%s:%s{%s})";
                }
                if (edge.isDirected() && edge.getDstId() != suffixNode.getId()) {
                    template = "<-[%d@%s:%s{%s}]-(%d@%s:%s{%s})";
                }
                edgeStrs.add(String.format(template,
                                           edge.getRank(),
                                           edge.getType(),
                                           String.join("&", edge.getLabels()),
                                           String.join(",", edgePropStrs),
                                           suffixNode.getId(),
                                           suffixNode.getType(),
                                           String.join("&", suffixNode.getLabels()),
                                           String.join(",", suffixNodePropStrs)));
            }
        }
        return String.join("", edgeStrs);
    }

}

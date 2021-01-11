/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.graph.data;

import com.vesoft.nebula.Edge;
import com.vesoft.nebula.Path;
import com.vesoft.nebula.Step;
import com.vesoft.nebula.Value;
import com.vesoft.nebula.client.graph.exception.InvalidValueException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PathWrapper {
    private List<Segment> segments = new ArrayList<>();
    private List<Node> nodes = new ArrayList<>();
    private List<Relationship> relationships = new ArrayList<>();

    public static class Segment {
        Node startNode;
        Relationship relationShip;
        Node endNode;

        public Segment(Node startNode, Relationship relationShip, Node endNode) {
            this.startNode = startNode;
            this.relationShip = relationShip;
            this.endNode = endNode;
        }

        public Node getStartNode() {
            return startNode;
        }

        public Relationship getRelationShip() {
            return relationShip;
        }

        public Node getEndNode() {
            return endNode;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Segment segment = (Segment) o;
            return Objects.equals(startNode, segment.startNode)
                    && Objects.equals(relationShip, segment.relationShip)
                    && Objects.equals(endNode, segment.endNode);
        }

        @Override
        public int hashCode() {
            return Objects.hash(startNode, relationShip, endNode);
        }
    }

    /**
     * get the start node from the path
     * @return Node
     */
    public Node getStartNode() {
        if (nodes == null || nodes.isEmpty()) {
            return null;
        }
        return nodes.get(0);
    }

    /**
     * get the end node from the path
     * @return Node
     */
    public Node getEndNode() {
        if (nodes == null || nodes.isEmpty()) {
            return null;
        }
        return nodes.get(nodes.size() - 1);
    }

    /**
     * determine if path contains the given node
     * @param node the given node
     * @return boolean
     */
    public boolean containNode(Node node) {
        return nodes.contains(node);
    }

    /**
     * determine if path contains the given relationShip
     * @param relationship the given relationship
     * @return boolean
     */
    public boolean containRelationship(Relationship relationship) {
        return relationships.contains(relationship);
    }

    /**
     * get all nodes from the path
     * @return the list of Node
     */
    public List<Node> getNodes() {
        return nodes;
    }

    /**
     * get all relationship from the path
     * @return the List of Relationship
     */
    public List<Relationship> getRelationships() {
        return relationships;
    }

    /**
     * get all segments from the path
     * @return the List of Segment
     */
    public List<Segment> getSegments() {
        return segments;
    }

    /**
     * get the length of the path
     * @return int
     */
    public int length() {
        return segments.size();
    }

    public PathWrapper(Path path) throws InvalidValueException, UnsupportedEncodingException {
        if (path == null) {
            this.nodes = new ArrayList<>();
            this.relationships = new ArrayList<>();
            this.segments = new ArrayList<>();
            return;
        }
        nodes.add(new Node(path.src));
        List<Value> vids = new ArrayList<>();
        vids.add(path.src.vid);
        Value srcId;
        Value dstId;
        for (Step step : path.steps) {
            Node startNode;
            Node endNode;
            int type = step.type;
            if (step.type > 0) {
                startNode = nodes.get(nodes.size() - 1);
                endNode = new Node(step.dst);
                nodes.add(endNode);
                srcId = vids.get(vids.size() - 1);
                dstId = step.dst.vid;
            } else {
                type = -type;
                startNode = new Node(step.dst);
                endNode = nodes.get(nodes.size() - 1);
                nodes.add(startNode);
                dstId = vids.get(vids.size() - 1);
                srcId = step.dst.vid;
            }
            vids.add(step.dst.vid);
            Edge edge = new Edge(srcId,
                                 dstId,
                                 type,
                                 step.name,
                                 step.ranking,
                                 step.props);
            Relationship relationShip = new Relationship(edge);
            relationships.add(new Relationship(edge));
            Segment segment = new Segment(startNode, relationShip, endNode);
            if (segment.getStartNode() != nodes.get(nodes.size() - 1)
                    && segment.getEndNode() != nodes.get(nodes.size() - 1)) {
                throw new InvalidValueException(
                        String.format("Relationship [%s] does not connect to the last node",
                                       relationShip.toString()));
            }
            segments.add(segment);
        }
    }

    @Override
    public String toString() {
        try {
            Node startNode = getStartNode();
            List<String> edgeStrs = new ArrayList<>();
            if (segments.size() >= 1) {
                List<String> propStrs = new ArrayList<>();
                Map<String, ValueWrapper> props = segments.get(0).getRelationShip().properties();
                for (String key : props.keySet()) {
                    propStrs.add(key + ":" + props.get(key).toString());
                }
                if (segments.get(0).getStartNode() == startNode) {
                    edgeStrs.add(String.format("-[:%s@%d{%s}]->%s",
                        segments.get(0).getRelationShip().edgeName(),
                        segments.get(0).getRelationShip().ranking(),
                        String.join(", ", propStrs),
                        segments.get(0).getEndNode().toString()));
                } else {
                    edgeStrs.add(String.format("<-[:%s@%d{%s}]-%s",
                        segments.get(0).getRelationShip().edgeName(),
                        segments.get(0).getRelationShip().ranking(),
                        String.join(", ", propStrs),
                        segments.get(0).getStartNode().toString()));
                }

            }

            for (int i = 1; i < segments.size(); i++) {
                List<String> propStrs = new ArrayList<>();
                Map<String, ValueWrapper> props = segments.get(0).getRelationShip().properties();
                for (String key : props.keySet()) {
                    propStrs.add(key + ":" + props.get(key).toString());
                }
                if (segments.get(i).getStartNode() == segments.get(i - 1).getStartNode()) {
                    edgeStrs.add(String.format("-[:%s@%d{%s}]->%s",
                        segments.get(i).getRelationShip().edgeName(),
                        segments.get(i).getRelationShip().ranking(),
                        String.join(", ", propStrs),
                        segments.get(i).getEndNode().toString()));
                } else {
                    edgeStrs.add(String.format("<-[:%s@%d{%s}]-%s",
                        segments.get(i).getRelationShip().edgeName(),
                        segments.get(i).getRelationShip().ranking(),
                        String.join(", ", propStrs),
                        segments.get(i).getStartNode().toString()));
                }
            }
            return String.format("%s%s",
                getStartNode().toString(), String.join("", edgeStrs));
        } catch (UnsupportedEncodingException e) {
            return e.getMessage();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PathWrapper segments1 = (PathWrapper) o;
        return Objects.equals(segments, segments1.segments)
                && Objects.equals(nodes, segments1.nodes)
                && Objects.equals(relationships, segments1.relationships);
    }

    @Override
    public int hashCode() {
        return Objects.hash(segments, nodes, relationships);
    }
}

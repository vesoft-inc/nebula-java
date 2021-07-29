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

public class PathWrapper extends BaseDataObject {
    private List<Segment> segments = new ArrayList<>();
    private List<Node> nodes = new ArrayList<>();
    private List<Relationship> relationships = new ArrayList<>();
    private Path path = null;

    public static class Segment {
        Node startNode;
        Relationship relationShip;
        Node endNode;

        /**
         * The segment is used to represent an edge in a path.
         * It contains information about the starting point and the ending point,
         * as well as information about the edge,
         * and its direction is used to indicate that the starting point points to the ending point
         * @param startNode the start node
         * @param relationShip the edge
         * @param endNode the end node
         */
        public Segment(Node startNode, Relationship relationShip, Node endNode) {
            this.startNode = startNode;
            this.relationShip = relationShip;
            this.endNode = endNode;
        }

        /**
         * @return the start node of the segment
         */
        public Node getStartNode() {
            return startNode;
        }

        /**
         * @return the relationship of the segment
         */
        public Relationship getRelationShip() {
            return relationShip;
        }

        /**
         * @return the en node
         */
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

        @Override
        public String toString() {
            return "Segment{"
                    + "startNode=" + startNode
                    + ", relationShip=" + relationShip
                    + ", endNode=" + endNode
                    + '}';
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

    /**
     * PathWrapper is a wrapper around the Path type returned by nebula-graph
     * @param path the Path type returned by nebula-graph
     * @throws InvalidValueException
     * @throws UnsupportedEncodingException
     */
    public PathWrapper(Path path) throws InvalidValueException, UnsupportedEncodingException {
        if (path == null) {
            this.nodes = new ArrayList<>();
            this.relationships = new ArrayList<>();
            this.segments = new ArrayList<>();
            return;
        }
        this.path = path;
        nodes.add((Node) new Node(path.src)
            .setDecodeType(getDecodeType())
            .setTimezoneOffset(getTimezoneOffset()));
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
                endNode = (Node) new Node(step.dst)
                    .setDecodeType(getDecodeType())
                    .setTimezoneOffset(getTimezoneOffset());
                nodes.add(endNode);
                srcId = vids.get(vids.size() - 1);
                dstId = step.dst.vid;
            } else {
                type = -type;
                startNode = (Node) new Node(step.dst)
                    .setDecodeType(getDecodeType())
                    .setTimezoneOffset(getTimezoneOffset());
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
            Relationship relationShip = (Relationship) new Relationship(edge)
                .setDecodeType(getDecodeType())
                .setTimezoneOffset(getTimezoneOffset());
            relationships.add(relationShip);
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
            List<String> edgeStrs = new ArrayList<>();
            for (int i = 0; i < relationships.size(); i++) {
                Relationship relationship = relationships.get(i);
                List<String> propStrs = new ArrayList<>();
                Map<String, ValueWrapper> props = relationship.properties();
                for (String key : props.keySet()) {
                    propStrs.add(key + ": " + props.get(key).toString());
                }
                Step step = path.steps.get(i);
                Node node = (Node) new Node(step.dst)
                    .setDecodeType(getDecodeType())
                    .setTimezoneOffset(getTimezoneOffset());
                if (step.type > 0) {
                    edgeStrs.add(String.format("-[:%s@%d{%s}]->%s",
                        relationship.edgeName(),
                        relationship.ranking(),
                        String.join(", ", propStrs),
                        node.toString()));
                } else {
                    edgeStrs.add(String.format("<-[:%s@%d{%s}]-%s",
                        relationship.edgeName(),
                        relationship.ranking(),
                        String.join(", ", propStrs),
                        node.toString()));
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

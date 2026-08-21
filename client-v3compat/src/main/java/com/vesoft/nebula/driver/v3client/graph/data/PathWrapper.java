/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.v3client.graph.data;

import com.vesoft.nebula.driver.v3client.graph.exception.InvalidValueException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Wrapper around a graph path, matching the v3 client's {@code PathWrapper} API.
 */
public class PathWrapper extends BaseDataObject {

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

        @Override
        public String toString() {
            return "Segment{" + "startNode=" + startNode + ", relationShip=" + relationShip
                   + ", endNode=" + endNode + '}';
        }
    }

    private final List<Segment> segments = new ArrayList<>();
    private final List<Node> nodes = new ArrayList<>();
    private final List<Relationship> relationships = new ArrayList<>();

    public PathWrapper(com.vesoft.nebula.driver.graph.data.Path path, int timezoneOffset)
        throws InvalidValueException {
        setTimezoneOffset(timezoneOffset);
        if (path == null) {
            return;
        }
        for (com.vesoft.nebula.driver.graph.data.Node node : path.nodes()) {
            nodes.add((Node) new Node(node).setTimezoneOffset(timezoneOffset));
        }
        for (com.vesoft.nebula.driver.graph.data.Edge edge : path.edges()) {
            relationships.add((Relationship) new Relationship(edge).setTimezoneOffset(
                timezoneOffset));
        }
        for (int i = 0; i < relationships.size(); i++) {
            if (i + 1 >= nodes.size()) {
                throw new InvalidValueException("Malformed path: not enough nodes for edges");
            }
            segments.add(new Segment(nodes.get(i), relationships.get(i), nodes.get(i + 1)));
        }
    }

    public Node getStartNode() {
        if (nodes == null || nodes.isEmpty()) {
            return null;
        }
        return nodes.get(0);
    }

    public Node getEndNode() {
        if (nodes == null || nodes.isEmpty()) {
            return null;
        }
        return nodes.get(nodes.size() - 1);
    }

    public boolean containNode(Node node) {
        return nodes.contains(node);
    }

    public boolean containRelationship(Relationship relationship) {
        return relationships.contains(relationship);
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public List<Relationship> getRelationships() {
        return relationships;
    }

    public List<Segment> getSegments() {
        return segments;
    }

    public int length() {
        return segments.size();
    }

    @Override
    public String toString() {
        if (nodes.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(nodes.get(0).toString());
        for (int i = 0; i < relationships.size(); i++) {
            Relationship relationship = relationships.get(i);
            sb.append("-[:").append(relationship.edgeName()).append('@')
              .append(relationship.ranking()).append("{}]->");
            if (i + 1 < nodes.size()) {
                sb.append(nodes.get(i + 1).toString());
            }
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PathWrapper that = (PathWrapper) o;
        return Objects.equals(segments, that.segments)
            && Objects.equals(nodes, that.nodes)
            && Objects.equals(relationships, that.relationships);
    }

    @Override
    public int hashCode() {
        return Objects.hash(segments, nodes, relationships);
    }
}

/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.data;

import static com.vesoft.nebula.driver.graph.decode.struct.SizeConstant.EDGE_TYPE_ID_SIZE;

import com.vesoft.nebula.driver.graph.decode.struct.ResultGraphSchemas;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Edge extends BaseDataObject {

    private final int                       graphId;
    private final String                    graphName;
    private final int                       edgeTypeId;
    private final String                    edgeTypeName;
    private final List<String>              labels;
    private final long                      rank;
    private final long                      srcId;
    private final long                      dstId;
    private final Direction                 direction;
    private final Map<String, ValueWrapper> properties;


    /**
     * Edge is a wrapper around the Edge type returned by nebula-graph
     */
    public Edge(int graphId,
                int edgeTypeId,
                long rank,
                long srcId,
                long dstId,
                Map<String, ValueWrapper> properties,
                ResultGraphSchemas graphSchemas) {
        this.graphId = graphId;
        this.graphName = graphSchemas.getGraphSchema(graphId).getGraphName();
        this.edgeTypeId = edgeTypeId;
        int noDirectedTypeId = edgeTypeId & 0x3FFFFFFF;
        this.edgeTypeName = graphSchemas
                .getGraphSchema(graphId)
                .getEdgeSchema(noDirectedTypeId)
                .getEdgeTypeName();
        this.labels = graphSchemas
                .getGraphSchema(graphId)
                .getEdgeSchema(noDirectedTypeId)
                .getEdgeLabels();
        this.rank = rank;
        this.properties = properties;

        int edgeTypeMoveBits = EDGE_TYPE_ID_SIZE * 8 - 2;
        int directionBits    = (edgeTypeId >> edgeTypeMoveBits) & 0x3;
        switch (directionBits) {
            case 0x0:
                this.direction = Direction.OUTGOING;
                break;
            case 0x1:
                this.direction = Direction.INCOMING;
                break;
            case 0x2:
            case 0x3:
                this.direction = Direction.UNDIRECTED;
                break;
            default:
                this.direction = Direction.KNOWN;
        }
        if (this.direction == Direction.INCOMING) {
            this.srcId = dstId;
            this.dstId = srcId;
        } else {
            this.srcId = srcId;
            this.dstId = dstId;
        }
    }

    /**
     * get graph
     *
     * @return String
     */
    public String getGraph() {
        return graphName;
    }

    /**
     * get edge type name
     *
     * @return String
     */
    public String getType() {
        return edgeTypeName;
    }

    /**
     * get edge type id
     */
    public int getEdgeTypeId() {
        return edgeTypeId;
    }

    /**
     * if the edge is directed
     *
     * @return true if edge is directed
     */
    public boolean isDirected() {
        return direction == Direction.OUTGOING || direction == Direction.INCOMING;
    }

    /**
     * get edge labels
     *
     * @return list of edge labels
     */
    public List<String> getLabels() {
        return labels;
    }

    /**
     * get the src id
     *
     * @return long
     */
    public long getSrcId() {
        return srcId;
    }

    /**
     * get the dst id from the edge
     *
     * @return long
     */
    public long getDstId() {
        return dstId;
    }

    /**
     * get rank from the edge
     *
     * @return long
     */
    public long getRank() {
        return rank;
    }

    /**
     * get all property name
     *
     * @return the List of property names
     */
    public List<String> getColumnNames() {
        return new ArrayList<>(properties.keySet());
    }

    /**
     * get all property values
     *
     * @return the List of property values
     */
    public List<ValueWrapper> getPropertyValues() {
        List<ValueWrapper> values = new ArrayList<>();
        for (Map.Entry<String, ValueWrapper> kv : properties.entrySet()) {
            values.add(kv.getValue());
        }
        return values;
    }

    /**
     * get property names and values from the edge
     *
     * @return HashMap, property name -> property value
     */
    public Map<String, ValueWrapper> getProperties() {
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
        Edge that = (Edge) o;

        return getRank() == that.getRank()
                && ((getSrcId() == that.getSrcId() && getDstId() == that.getDstId())
                || (getSrcId() == that.getDstId() && getDstId() == that.getSrcId()))
                && Objects.equals(getType(), that.getType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(graphId, edgeTypeId, rank, srcId, dstId, getDecodeType());
    }

    @Override
    public String toString() {
        List<String>              propStrs = new ArrayList<>();
        Map<String, ValueWrapper> props    = getProperties();
        for (String key : props.keySet()) {
            propStrs.add(key + ":" + props.get(key).toString());
        }
        if (direction != Direction.UNDIRECTED) {
            return String.format("(%d)-[%d@%s:%s{%s}]->(%d)",
                                 getSrcId(),
                                 getRank(),
                                 getType(),
                                 String.join("&", getLabels()),
                                 String.join(",", propStrs),
                                 getDstId());
        } else {
            return String.format("(%d)~[%d@%s:%s{%s}]~(%d)",
                                 getSrcId(),
                                 getRank(),
                                 getType(),
                                 String.join("&", getLabels()),
                                 String.join(",", propStrs),
                                 getDstId());
        }

    }

    enum Direction {
        OUTGOING, INCOMING, UNDIRECTED, KNOWN;
    }
}

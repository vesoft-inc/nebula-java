/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.data;

import com.vesoft.nebula.driver.graph.decode.struct.ResultGraphSchemas;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Node extends BaseDataObject {
    private final int                       graphId;
    private final String                    graphName;
    private final int                       nodeTypeId;
    private final String                    nodeTypeName;
    private final List<String>              labels;
    private final long                      nodeId;
    private final Map<String, ValueWrapper> properties;

    public Node(int graphId,
                int nodeTypeId,
                long nodeId,
                Map<String, ValueWrapper> properties,
                ResultGraphSchemas graphSchemas) {
        this.graphId = graphId;
        this.graphName = graphSchemas.getGraphSchema(graphId).getGraphName();
        this.nodeTypeId = nodeTypeId;
        this.nodeTypeName = graphSchemas
                .getGraphSchema(graphId)
                .getNodeSchema(nodeTypeId)
                .getNodeTypeName();
        this.labels = graphSchemas
                .getGraphSchema(graphId)
                .getNodeSchema(nodeTypeId)
                .getNodeLabels();
        this.nodeId = nodeId;
        this.properties = properties;
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
     * get node type name
     *
     * @return String
     */
    public String getType() {
        return nodeTypeName;
    }

    /**
     * get node type id
     */
    public int getNodeTypeId() {
        return nodeTypeId;
    }


    /**
     * get node label list
     *
     * @return list of label
     */
    public List<String> getLabels() {
        return labels;
    }

    /**
     * get vid
     *
     * @return long id
     */
    public long getId() {
        return nodeId;
    }


    /**
     * get property names from the node
     *
     * @return the list of property names
     * @throws UnsupportedEncodingException decode error exception
     */
    public List<String> getColumnNames() throws UnsupportedEncodingException {
        List<String> keys = new ArrayList<>(properties.keySet());
        return keys;
    }

    /**
     * get all property values
     *
     * @return the List of property values
     */
    public List<ValueWrapper> getValues() {
        List<ValueWrapper> values = new ArrayList<>();
        for (Map.Entry<String, ValueWrapper> kv : properties.entrySet()) {
            values.add(kv.getValue());
        }
        return values;
    }

    /**
     * get all properties for node
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
        Node node = (Node) o;
        return getId() == node.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(graphId, nodeTypeId, nodeId, getDecodeType());
    }

    @Override
    public String toString() {
        Map<String, ValueWrapper> props    = getProperties();
        List<String>              propStrs = new ArrayList<>();
        for (String propName : props.keySet()) {
            propStrs.add(propName + ":" + props.get(propName).toString());
        }
        return String.format("(%d@%s:%s{%s})",
                             getId(),
                             getType(),
                             String.join("&", getLabels()),
                             String.join(",", propStrs));
    }
}

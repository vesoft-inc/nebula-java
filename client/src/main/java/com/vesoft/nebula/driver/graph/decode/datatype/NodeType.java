/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.decode.datatype;

import static com.vesoft.nebula.driver.graph.decode.ColumnType.COLUMN_TYPE_NODE;

import java.util.Map;

public class NodeType extends DataType {
    // graphId -> (nodeTypeId -> (prop name -> prop data type))
    private final Map<Integer, Map<Integer, Map<String, DataType>>> graphNodeTypes;

    public NodeType(Map<Integer, Map<Integer, Map<String, DataType>>> graphNodeTypes) {
        super(COLUMN_TYPE_NODE);
        this.graphNodeTypes = graphNodeTypes;
    }

    public Map<Integer, Map<Integer, Map<String, DataType>>> getNodeTypes() {
        return graphNodeTypes;
    }
}

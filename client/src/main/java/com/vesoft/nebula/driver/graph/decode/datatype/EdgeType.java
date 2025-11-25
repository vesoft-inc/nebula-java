/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.decode.datatype;

import com.vesoft.nebula.driver.graph.decode.ColumnType;
import java.util.Map;

public class EdgeType extends DataType {
    // graphId -> (edgeTypeId -> (prop name -> prop data type))
    private final Map<Integer, Map<Integer, Map<String, DataType>>> graphEdgeTypes;

    public EdgeType(Map<Integer,Map<Integer, Map<String, DataType>>> graphEdgeTypes) {
        super(ColumnType.COLUMN_TYPE_EDGE);
        this.graphEdgeTypes = graphEdgeTypes;
    }

    public Map<Integer, Map<Integer, Map<String, DataType>>> getEdgeTypes() {
        return graphEdgeTypes;
    }
}

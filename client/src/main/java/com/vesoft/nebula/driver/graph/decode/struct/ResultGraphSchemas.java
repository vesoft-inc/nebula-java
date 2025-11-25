/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.decode.struct;

import com.vesoft.nebula.proto.graph.PropertyGraphSchema;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultGraphSchemas {

    Map<Integer, GraphSchema> graphSchemaMap = new HashMap<>();

    public ResultGraphSchemas(List<PropertyGraphSchema> graphSchemas) {
        for (PropertyGraphSchema schema : graphSchemas) {
            graphSchemaMap.put(schema.getGraphId(), new GraphSchema(schema));
        }
    }

    public Map<Integer, GraphSchema> getGraphSchemaMap() {
        return graphSchemaMap;
    }

    public GraphSchema getGraphSchema(int graphId) {
        return graphSchemaMap.get(graphId);
    }
}

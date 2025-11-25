/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.decode.struct;

import static com.vesoft.nebula.driver.graph.decode.DecodeUtils.charset;

import com.google.common.base.Charsets;
import com.google.protobuf.ByteString;
import com.vesoft.nebula.proto.graph.EdgeType;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class EdgeSchema {
    private        int          edgeTypeId;
    private        String       edgeTypeName;
    private        List<String> edgeLabels = new ArrayList<>();

    public EdgeSchema(EdgeType edgeType) {
        this.edgeTypeId = edgeType.getEdgeTypeId();
        this.edgeTypeName = edgeType.getEdgeTypeName().toString(charset);
        for (ByteString label : edgeType.getLabelList()) {
            edgeLabels.add(label.toString(charset));
        }
    }

    public int getEdgeTypeId() {
        return edgeTypeId;
    }

    public String getEdgeTypeName() {
        return edgeTypeName;
    }

    public List<String> getEdgeLabels() {
        return edgeLabels;
    }
}

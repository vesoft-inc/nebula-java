/* Copyright (c) 2025 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.driver.graph.decode.struct;

import static com.vesoft.nebula.driver.graph.decode.DecodeUtils.charset;

import com.google.common.base.Charsets;
import com.google.protobuf.ByteString;
import com.vesoft.nebula.proto.graph.NodeType;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class NodeSchema {

    private int          nodeTypeId;
    private String       nodeTypeName;
    private List<String> nodeLabels = new ArrayList<>();

    public NodeSchema(NodeType nodeType) {
        this.nodeTypeId = nodeType.getNodeTypeId();
        this.nodeTypeName = nodeType.getNodeTypeName().toString(charset);
        for (ByteString label : nodeType.getLabelList()) {
            nodeLabels.add(label.toString(charset));
        }
    }

    public int getNodeTypeId() {
        return nodeTypeId;
    }

    public String getNodeTypeName() {
        return nodeTypeName;
    }

    public List<String> getNodeLabels() {
        return nodeLabels;
    }
}

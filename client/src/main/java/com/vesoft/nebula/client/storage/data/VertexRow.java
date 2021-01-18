/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.storage.data;

import com.vesoft.nebula.client.graph.data.ValueWrapper;
import java.io.UnsupportedEncodingException;
import java.util.Map;

public class VertexRow {
    private final ValueWrapper vid;
    private final Map<String, ValueWrapper> props;

    public VertexRow(ValueWrapper vid, Map<String, ValueWrapper> props) {
        this.vid = vid;
        this.props = props;
    }

    public ValueWrapper getVid() throws UnsupportedEncodingException {
        return vid;
    }


    public Map<String, ValueWrapper> getProps() {
        return props;
    }


    @Override
    public String toString() {
        return "Vertex{"
                + "vid=" + vid.getValue()
                + ", props=" + props
                + '}';
    }
}


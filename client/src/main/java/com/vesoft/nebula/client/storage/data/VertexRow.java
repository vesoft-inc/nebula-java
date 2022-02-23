/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License.
 */

package com.vesoft.nebula.client.storage.data;

import com.vesoft.nebula.client.graph.data.ValueWrapper;
import java.util.Map;

public class VertexRow {
    private final ValueWrapper vid;
    private final Map<String, ValueWrapper> props;

    public VertexRow(ValueWrapper vid, Map<String, ValueWrapper> props) {
        this.vid = vid;
        this.props = props;
    }

    public ValueWrapper getVid() {
        return vid;
    }

    public Map<String, ValueWrapper> getProps() {
        return props;
    }

    @Override
    public String toString() {
        return "Vertex{"
                + "vid=" + vid.toString()
                + ", props=" + props
                + '}';
    }
}


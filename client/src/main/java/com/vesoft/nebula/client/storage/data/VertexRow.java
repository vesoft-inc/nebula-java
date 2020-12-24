/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.storage.data;

import com.vesoft.nebula.Value;
import java.io.UnsupportedEncodingException;
import java.util.Map;

public class VertexRow {
    private final Value vid;
    private final Map<String, Object> props;
    private final String decodeType;

    public VertexRow(Value vid, Map<String, Object> props) {
        this(vid, props, "utf-8");
    }

    public VertexRow(Value vid, Map<String, Object> props, String decodeType) {
        this.vid = vid;
        this.props = props;
        this.decodeType = decodeType;
    }


    // todo int vid
    public String getVid() throws UnsupportedEncodingException {
        return new String(vid.getSVal(), decodeType);
    }


    public Map<String, Object> getProps() {
        return props;
    }


    @Override
    public String toString() {
        try {
            return "Vertex{"
                    + "vid=" + new String(vid.getSVal(), decodeType)
                    + ", props=" + props
                    + '}';
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}


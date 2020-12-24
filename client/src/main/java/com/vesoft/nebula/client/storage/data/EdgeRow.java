/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.storage.data;

import com.vesoft.nebula.Value;
import java.io.UnsupportedEncodingException;
import java.util.Map;


public class EdgeRow {
    private final Value srcId;
    private final Value dstId;
    private final Value rank;
    private final Map<String, Object> props;
    private final String decodeType;

    public EdgeRow(Value srcId, Value dstId, Value rank, Map<String, Object> props) {
        this(srcId, dstId, rank, props, "utf-8");
    }

    public EdgeRow(Value srcId, Value dstId, Value rank, Map<String, Object> props,
                   String decodeType) {
        this.srcId = srcId;
        this.dstId = dstId;
        this.rank = rank;
        this.props = props;
        this.decodeType = decodeType;
    }

    // todo int srcId
    public String getSrcId() throws UnsupportedEncodingException {
        return new String(srcId.getSVal(), decodeType);
    }

    // todo int dstId
    public String getDstId() throws UnsupportedEncodingException {
        return new String(dstId.getSVal(), decodeType);
    }

    public long getRank() {
        return rank.getIVal();
    }

    public Map<String, Object> getProps() {
        return props;
    }

    @Override
    public String toString() {
        try {
            return "Edge{"
                    + "srcId='" + getSrcId() + '\''
                    + ", dstId='" + getDstId() + '\''
                    + ", rank=" + getRank()
                    + ", props=" + props
                    + '}';
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
}

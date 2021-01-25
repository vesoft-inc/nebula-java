/* Copyright (c) 2020 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.storage.data;

import com.vesoft.nebula.client.graph.data.ValueWrapper;
import java.util.Map;

public class EdgeRow {
    private final ValueWrapper srcId;
    private final ValueWrapper dstId;
    private final long rank;
    private final Map<String, ValueWrapper> props;

    public EdgeRow(ValueWrapper srcId, ValueWrapper dstId, long rank,
                   Map<String, ValueWrapper> props) {
        this.srcId = srcId;
        this.dstId = dstId;
        this.rank = rank;
        this.props = props;
    }

    public ValueWrapper getSrcId() {
        return srcId;
    }

    public ValueWrapper getDstId() {
        return dstId;
    }

    public long getRank() {
        return rank;
    }

    public Map<String, ValueWrapper> getProps() {
        return props;
    }

    @Override
    public String toString() {
        return "Edge{"
                + "srcId='" + srcId.toString() + '\''
                + ", dstId='" + dstId.toString() + '\''
                + ", rank=" + rank
                + ", props=" + props
                + '}';
    }
}

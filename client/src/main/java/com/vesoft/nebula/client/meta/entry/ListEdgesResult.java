/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.meta.entry;

import com.google.common.collect.Maps;
import com.vesoft.nebula.meta.EdgeItem;
import java.util.Map;

public class ListEdgesResult {

    private Map<String, EdgeItem> result;

    public ListEdgesResult() {
        this.result = Maps.newHashMap();
    }

    public void add(String name, EdgeItem edgeItem) {
        result.put(name, edgeItem);
    }

    public Map<String, EdgeItem> getResult() {
        return result;
    }
}

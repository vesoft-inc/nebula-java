/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.meta.entry;

import com.google.common.collect.Maps;
import com.vesoft.nebula.meta.ID;
import java.util.Map;

public class ListSpaceResult {

    private Map<Integer, String> result;

    public ListSpaceResult() {
        this.result = Maps.newHashMap();
    }

    public Map<Integer, String> getResult() {
        return result;
    }

    public void add(ID id, String name) {
        result.put(id.getSpace_id(), name);
    }
}

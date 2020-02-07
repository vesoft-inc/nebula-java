/* Copyright (c) 2019 vesoft inc. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found in the LICENSES directory.
 */

package com.vesoft.nebula.client.meta.entry;

import com.google.common.collect.Maps;
import com.vesoft.nebula.meta.TagItem;
import java.util.Map;

public class ListTagsResult {

    private Map<String, TagItem> result;

    public ListTagsResult() {
        this.result = Maps.newHashMap();
    }

    public void add(String name, TagItem tagItem) {
        result.put(name, tagItem);
    }

    public Map<String, TagItem> getResult() {
        return result;
    }
}
